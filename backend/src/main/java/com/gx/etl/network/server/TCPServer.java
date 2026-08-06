package com.gx.etl.network.server;

import com.gx.etl.network.handler.NetworkLogHandler;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.LineBasedFrameDecoder;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

@Slf4j
@Component
public class TCPServer {

    @Autowired
    private NetworkLogHandler logHandler;

    private EventLoopGroup bossGroup;
    private EventLoopGroup workerGroup;
    private Channel serverChannel;
    private volatile boolean running = false;

    private int port = 9090;

    @PostConstruct
    public void start() {
        new Thread(this::startServer).start();
    }

    public void startServer() {
        bossGroup = new NioEventLoopGroup(1);
        workerGroup = new NioEventLoopGroup();

        try {
            ServerBootstrap bootstrap = new ServerBootstrap();
            bootstrap.group(bossGroup, workerGroup)
                     .channel(NioServerSocketChannel.class)
                     .childHandler(new ChannelInitializer<SocketChannel>() {
                         @Override
                         protected void initChannel(SocketChannel ch) {
                             ChannelPipeline pipeline = ch.pipeline();
                             pipeline.addLast(new LineBasedFrameDecoder(65536));
                             pipeline.addLast(new StringDecoder());
                             pipeline.addLast(new StringEncoder());
                             pipeline.addLast(new TCPServerHandler(logHandler));
                         }
                     });

            ChannelFuture future = bootstrap.bind(port).sync();
            serverChannel = future.channel();
            running = true;
            log.info("TCP Server started on port {}", port);

            serverChannel.closeFuture().sync();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            log.error("TCP Server interrupted", e);
        } finally {
            shutdown();
        }
    }

    public void shutdown() {
        running = false;
        if (serverChannel != null) serverChannel.close();
        if (bossGroup != null) bossGroup.shutdownGracefully();
        if (workerGroup != null) workerGroup.shutdownGracefully();
        log.info("TCP Server stopped");
    }

    public boolean isRunning() {
        return running;
    }

    public void setPort(int port) {
        this.port = port;
    }

    // TCP消息处理Handler
    @Slf4j
    static class TCPServerHandler extends SimpleChannelInboundHandler<String> {
        private final NetworkLogHandler logHandler;

        public TCPServerHandler(NetworkLogHandler logHandler) {
            this.logHandler = logHandler;
        }

        @Override
        protected void channelRead0(ChannelHandlerContext ctx, String msg) {
            String clientIp = ctx.channel().remoteAddress().toString();
            logHandler.handleLog("TCP", "RECEIVE", msg, clientIp);
            // 发送确认响应
            ctx.writeAndFlush("ACK: " + msg.length() + " bytes\n");
        }

        @Override
        public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
            log.error("TCP handler error: {}", cause.getMessage());
            ctx.close();
        }

        @Override
        public void channelActive(ChannelHandlerContext ctx) {
            log.info("TCP client connected: {}", ctx.channel().remoteAddress());
        }

        @Override
        public void channelInactive(ChannelHandlerContext ctx) {
            log.info("TCP client disconnected: {}", ctx.channel().remoteAddress());
        }
    }
}
