package com.gx.etl.flow.websocket;

import org.springframework.stereotype.Component;

import javax.websocket.*;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArraySet;

/**
 * 批量执行进度 WebSocket 端点
 */
@ServerEndpoint("/ws/batchProgress/{taskId}")
@Component
public class BatchProgressWebSocket {

    // taskId -> Set<Session> 映射
    private static final ConcurrentHashMap<String, Set<Session>> taskSessions = new ConcurrentHashMap<>();

    @OnOpen
    public void onOpen(Session session, @PathParam("taskId") String taskId) {
        taskSessions.computeIfAbsent(taskId, k -> new CopyOnWriteArraySet<>()).add(session);
        System.out.println("[WebSocket] 连接建立: taskId=" + taskId + ", sessionId=" + session.getId());
    }

    @OnClose
    public void onClose(Session session, @PathParam("taskId") String taskId) {
        Set<Session> sessions = taskSessions.get(taskId);
        if (sessions != null) {
            sessions.remove(session);
            if (sessions.isEmpty()) {
                taskSessions.remove(taskId);
            }
        }
        System.out.println("[WebSocket] 连接关闭: taskId=" + taskId + ", sessionId=" + session.getId());
    }

    @OnError
    public void onError(Session session, @PathParam("taskId") String taskId, Throwable error) {
        System.out.println("[WebSocket] 连接错误: taskId=" + taskId + ", error=" + error.getMessage());
    }

    @OnMessage
    public void onMessage(String message, @PathParam("taskId") String taskId) {
        System.out.println("[WebSocket] 收到消息: taskId=" + taskId + ", message=" + message);
        // 处理客户端发送的消息（如取消命令或开始执行命令）
        if ("start".equals(message)) {
            // 通知 BatchExecutionService 开始执行
            System.out.println("[WebSocket] 收到开始执行命令: taskId=" + taskId);
        }
    }

    /**
     * 发送进度消息到指定 taskId 的所有客户端
     */
    public static void sendProgress(String taskId, ProgressMessage message) {
        Set<Session> sessions = taskSessions.get(taskId);
        if (sessions == null || sessions.isEmpty()) {
            return;
        }

        String jsonMessage = com.alibaba.fastjson.JSON.toJSONString(message);
        for (Session session : sessions) {
            if (session.isOpen()) {
                try {
                    session.getBasicRemote().sendText(jsonMessage);
                } catch (IOException e) {
                    System.err.println("[WebSocket] 发送消息失败: " + e.getMessage());
                }
            }
        }
    }

    /**
     * 检查指定 taskId 是否有客户端连接
     */
    public static boolean hasConnections(String taskId) {
        Set<Session> sessions = taskSessions.get(taskId);
        return sessions != null && !sessions.isEmpty();
    }

    /**
     * 清理指定 taskId 的所有连接
     */
    public static void cleanup(String taskId) {
        Set<Session> sessions = taskSessions.remove(taskId);
        if (sessions != null) {
            for (Session session : sessions) {
                if (session.isOpen()) {
                    try {
                        session.close();
                    } catch (IOException e) {
                        System.err.println("[WebSocket] 关闭连接失败: " + e.getMessage());
                    }
                }
            }
        }
    }

    /**
     * 获取指定 taskId 的连接数
     */
    public static int getConnectionCount(String taskId) {
        Set<Session> sessions = taskSessions.get(taskId);
        return sessions != null ? sessions.size() : 0;
    }

    /**
     * 获取所有任务的 taskId 列表
     */
    public static Set<String> getActiveTaskIds() {
        return taskSessions.keySet();
    }

    /**
     * 清理所有连接
     */
    public static void cleanupAll() {
        for (String taskId : taskSessions.keySet()) {
            cleanup(taskId);
        }
    }
}
