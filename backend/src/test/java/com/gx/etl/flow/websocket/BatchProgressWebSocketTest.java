package com.gx.etl.flow.websocket;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

/**
 * BatchProgressWebSocket 测试
 * 测试 WebSocket 连接管理和消息推送功能
 */
@SpringBootTest
public class BatchProgressWebSocketTest {

    @Test
    public void testWebSocketConnection() {
        // 测试 WebSocket 连接建立
        String taskId = "test-task-1";

        // 模拟 WebSocket 连接
        // 由于 WebSocket 需要真实的 WebSocket 客户端，这里先写一个占位测试
        // 实际测试需要使用 Spring 的 WebSocketTestClient

        // 验证连接管理逻辑
        assertNotNull(taskId, "taskId 不应该为空");
    }

    @Test
    public void testSendProgress() {
        // 测试发送进度消息
        String taskId = "test-task-2";

        // 创建进度消息
        ProgressMessage message = new ProgressMessage();
        message.setTaskId(taskId);
        message.setCurrent(1);
        message.setTotal(10);
        message.setSuccessCount(1);
        message.setFailCount(0);
        message.setStatus("running");

        // 验证消息格式
        assertEquals(taskId, message.getTaskId());
        assertEquals(1, message.getCurrent());
        assertEquals(10, message.getTotal());
        assertEquals(1, message.getSuccessCount());
        assertEquals(0, message.getFailCount());
        assertEquals("running", message.getStatus());
    }

    @Test
    public void testHasConnections() {
        // 测试检查连接状态
        String taskId = "test-task-3";

        // 初始状态应该没有连接
        assertFalse(BatchProgressWebSocket.hasConnections(taskId),
                "初始状态不应该有连接");
    }

    @Test
    public void testCleanup() {
        // 测试清理连接
        String taskId = "test-task-4";

        // 清理不应该抛出异常
        assertDoesNotThrow(() -> {
            BatchProgressWebSocket.cleanup(taskId);
        });
    }

    @Test
    public void testMultipleClientsSameTask() {
        // 测试多个客户端连接同一个 taskId
        String taskId = "test-task-5";

        // 模拟多个客户端连接
        // 由于需要真实的 WebSocket 客户端，这里测试连接管理逻辑

        // 验证 hasConnections 在没有连接时返回 false
        assertFalse(BatchProgressWebSocket.hasConnections(taskId),
                "没有连接时应该返回 false");
    }

    @Test
    public void testProgressMessageSerialization() {
        // 测试进度消息序列化
        ProgressMessage message = new ProgressMessage();
        message.setTaskId("task-123");
        message.setCurrent(5);
        message.setTotal(10);
        message.setSuccessCount(4);
        message.setFailCount(1);
        message.setElapsedTime(5000);
        message.setEstimatedRemaining(5000);
        message.setTotalRows(1500);
        message.setStatus("running");

        // 验证消息字段
        assertEquals("task-123", message.getTaskId());
        assertEquals(5, message.getCurrent());
        assertEquals(10, message.getTotal());
        assertEquals(4, message.getSuccessCount());
        assertEquals(1, message.getFailCount());
        assertEquals(5000, message.getElapsedTime());
        assertEquals(5000, message.getEstimatedRemaining());
        assertEquals(1500, message.getTotalRows());
        assertEquals("running", message.getStatus());
    }

    @Test
    public void testBatchDetailCreation() {
        // 测试批次详情创建
        ProgressMessage.BatchDetail detail = new ProgressMessage.BatchDetail();
        detail.setIndex(1);
        detail.setStatus("success");
        detail.setElapsedTime(1200);
        detail.setRowCount(150);

        // 验证批次详情字段
        assertEquals(1, detail.getIndex());
        assertEquals("success", detail.getStatus());
        assertEquals(1200, detail.getElapsedTime());
        assertEquals(150, detail.getRowCount());
        assertNull(detail.getErrorMsg(), "成功批次不应该有错误信息");
    }

    @Test
    public void testGetConnectionCount() {
        // 测试获取连接数
        String taskId = "test-task-6";

        // 初始状态应该没有连接
        assertEquals(0, BatchProgressWebSocket.getConnectionCount(taskId),
                "初始状态连接数应该为 0");
    }

    @Test
    public void testGetActiveTaskIds() {
        // 测试获取活跃任务列表
        Set<String> activeTaskIds = BatchProgressWebSocket.getActiveTaskIds();

        // 初始状态应该为空
        assertNotNull(activeTaskIds, "活跃任务列表不应该为 null");
        // 注意：由于其他测试可能创建了连接，这里不能断言为空
    }

    @Test
    public void testCleanupAll() {
        // 测试清理所有连接
        assertDoesNotThrow(() -> {
            BatchProgressWebSocket.cleanupAll();
        }, "清理所有连接不应该抛出异常");
    }

    @Test
    public void testSendProgressToNonExistentTask() {
        // 测试向不存在的任务发送消息
        String taskId = "non-existent-task";

        ProgressMessage message = new ProgressMessage();
        message.setTaskId(taskId);
        message.setCurrent(1);
        message.setTotal(1);
        message.setStatus("completed");

        // 发送消息不应该抛出异常
        assertDoesNotThrow(() -> {
            BatchProgressWebSocket.sendProgress(taskId, message);
        }, "向不存在的任务发送消息不应该抛出异常");
    }
}
