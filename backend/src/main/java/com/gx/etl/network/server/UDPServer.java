package com.gx.etl.network.server;

import com.gx.etl.network.handler.NetworkLogHandler;
import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.DatagramPacket;
import io.netty.channel.socket.nio.NioDatagramChannel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

@Slf4j
@Component
public class UDPServer {

    @Autowired
    private NetworkLogHandler logHandler;

    private EventLoopGroup group;
    private Channel serverChannel;
    private volatile boolean running = false;

    private int port = 9091;

    @PostConstruct
    public void start() {
        new Thread(this::startServer).start();
    }

    public void startServer() {
        group = new NioEventLoopGroup();

        try {
            Bootstrap bootstrap = new Bootstrap();
            bootstrap.group(group)
                     .channel(NioDatagramChannel.class)
                     .option(ChannelOption.SO_BROADCAST, true)
                     .handler(new UDPHandler(logHandler));

            ChannelFuture future = bootstrap.bind(port).sync();
            serverChannel = future.channel();
            running = true;
            log.info("UDP Server started on port {}", port);

            serverChannel.closeFuture().sync();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            log.error("UDP Server interrupted", e);
        } finally {
            shutdown();
        }
    }

    public void shutdown() {
        running = false;
        if (serverChannel != null) serverChannel.close();
        if (group != null) group.shutdownGracefully();
        log.info("UDP Server stopped");
    }

    public boolean isRunning() {
        return running;
    }

    public void setPort(int port) {
        this.port = port;
    }

    // UDP消息处理Handler
    @Slf4j
    static class UDPHandler extends SimpleChannelInboundHandler<DatagramPacket> {
        private final NetworkLogHandler logHandler;

        public UDPHandler(NetworkLogHandler logHandler) {
            this.logHandler = logHandler;
        }

        @Override
        protected void channelRead0(ChannelHandlerContext ctx, DatagramPacket packet) {
            String msg = packet.content().toString(io.netty.util.CharsetUtil.UTF_8);
            String clientIp = packet.sender().toString();
            logHandler.handleLog("UDP", "RECEIVE", msg, clientIp);

            // 发送响应
            byte[] response = "ACK".getBytes();
            DatagramPacket responsePacket = new DatagramPacket(
                Unpooled.copiedBuffer(response),
                packet.sender()
            );
            ctx.writeAndFlush(responsePacket);
        }

        @Override
        public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
            log.error("UDP handler error: {}", cause.getMessage());
        }
    }
}
