package com.gx.etl.network.handler;

import com.gx.etl.network.model.NetworkLog;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

import java.util.concurrent.*;

@Slf4j
@Component
public class NetworkLogHandler {

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    private final ExecutorService executor = Executors.newFixedThreadPool(
        Runtime.getRuntime().availableProcessors()
    );

    private final ConcurrentLinkedDeque<NetworkLog> recentLogs = new ConcurrentLinkedDeque<>();
    private static final int MAX_RECENT_LOGS = 200;

    /**
     * 处理接收到的日志
     */
    public void handleLog(String protocol, String direction, String message, String clientIp) {
        executor.submit(() -> {
            try {
                NetworkLog logEntry = new NetworkLog();
                logEntry.setProtocol(protocol);
                logEntry.setDirection(direction);
                logEntry.setMessage(message);
                logEntry.setClientIp(clientIp);
                logEntry.setStatus("SUCCESS");

                // 添加到最近日志
                recentLogs.addFirst(logEntry);
                while (recentLogs.size() > MAX_RECENT_LOGS) {
                    recentLogs.removeLast();
                }

                // 通过WebSocket推送到前端
                messagingTemplate.convertAndSend("/topic/network-logs", logEntry);

                log.info("[{}] {} from {}: {}", protocol, direction, clientIp, message);
            } catch (Exception e) {
                log.error("Failed to handle log: {}", e.getMessage(), e);
            }
        });
    }

    /**
     * 发送日志（从前端调用）
     */
    public NetworkLog sendLog(String protocol, String message) {
        // 1. 先记录一条"接收"日志（服务器收到并处理了消息）
        NetworkLog receiveLog = new NetworkLog();
        receiveLog.setProtocol(protocol);
        receiveLog.setDirection("RECEIVE");
        receiveLog.setMessage(message);
        receiveLog.setClientIp("127.0.0.1");
        receiveLog.setStatus("SUCCESS");
        if ("TCP".equalsIgnoreCase(protocol)) {
            receiveLog.setResponse("服务器已接收并处理");
        } else {
            receiveLog.setResponse("UDP包已接收");
        }
        recentLogs.addFirst(receiveLog);
        while (recentLogs.size() > MAX_RECENT_LOGS) {
            recentLogs.removeLast();
        }
        messagingTemplate.convertAndSend("/topic/network-logs", receiveLog);

        // 2. 再记录一条"发送"日志（发送响应）
        NetworkLog sendLog = new NetworkLog();
        sendLog.setProtocol(protocol);
        sendLog.setDirection("SEND");
        sendLog.setMessage(message);
        sendLog.setStatus("SUCCESS");

        try {
            if ("TCP".equalsIgnoreCase(protocol)) {
                sendLog.setResponse("ACK: " + message.length() + " bytes");
            } else if ("UDP".equalsIgnoreCase(protocol)) {
                sendLog.setResponse("ACK");
            }

            recentLogs.addFirst(sendLog);
            while (recentLogs.size() > MAX_RECENT_LOGS) {
                recentLogs.removeLast();
            }

            messagingTemplate.convertAndSend("/topic/network-logs", sendLog);

            log.info("[{}] SEND: {}", protocol, message);
        } catch (Exception e) {
            sendLog.setStatus("FAIL");
            sendLog.setErrorMsg(e.getMessage());
            log.error("Failed to send log: {}", e.getMessage(), e);
        }

        return sendLog;
    }

    /**
     * 获取最近的日志
     */
    public java.util.List<NetworkLog> getRecentLogs() {
        return new java.util.ArrayList<>(recentLogs);
    }

    /**
     * 清空日志
     */
    public void clearLogs() {
        recentLogs.clear();
        log.info("Network logs cleared");
    }
}
