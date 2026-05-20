package com.gx.etl.flow.websocket;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

/**
 * 批量执行异步化测试
 */
@SpringBootTest
public class BatchExecutionAsyncTest {

    @Test
    public void testTaskIdGeneration() {
        // 测试 taskId 生成
        String taskId = UUID.randomUUID().toString();

        assertNotNull(taskId, "taskId 不应该为 null");
        assertFalse(taskId.isEmpty(), "taskId 不应该为空");
        assertEquals(36, taskId.length(), "UUID 格式的 taskId 长度应该是 36");
        assertTrue(taskId.contains("-"), "UUID 应该包含连字符");
    }

    @Test
    public void testProgressMessageForAsyncExecution() {
        // 测试异步执行的进度消息
        String taskId = UUID.randomUUID().toString();

        ProgressMessage message = new ProgressMessage();
        message.setTaskId(taskId);
        message.setCurrent(0);
        message.setTotal(10);
        message.setSuccessCount(0);
        message.setFailCount(0);
        message.setStatus("running");

        assertEquals(taskId, message.getTaskId());
        assertEquals(0, message.getCurrent());
        assertEquals(10, message.getTotal());
        assertEquals("running", message.getStatus());
    }

    @Test
    public void testProgressUpdate() {
        // 测试进度更新
        String taskId = UUID.randomUUID().toString();

        ProgressMessage message = new ProgressMessage();
        message.setTaskId(taskId);
        message.setTotal(10);

        // 模拟进度更新
        for (int i = 1; i <= 10; i++) {
            message.setCurrent(i);
            message.setSuccessCount(i);
            message.setStatus(i == 10 ? "completed" : "running");

            assertEquals(i, message.getCurrent());
            assertEquals(i, message.getSuccessCount());
            assertEquals(i == 10 ? "completed" : "running", message.getStatus());
        }
    }

    @Test
    public void testBatchDetailWithError() {
        // 测试失败批次的详情
        ProgressMessage.BatchDetail detail = new ProgressMessage.BatchDetail();
        detail.setIndex(3);
        detail.setStatus("fail");
        detail.setElapsedTime(500);
        detail.setRowCount(0);
        detail.setErrorMsg("数据库连接超时");

        assertEquals(3, detail.getIndex());
        assertEquals("fail", detail.getStatus());
        assertEquals(500, detail.getElapsedTime());
        assertEquals(0, detail.getRowCount());
        assertEquals("数据库连接超时", detail.getErrorMsg());
    }

    @Test
    public void testCancelMessage() {
        // 测试取消消息
        String taskId = UUID.randomUUID().toString();

        ProgressMessage message = new ProgressMessage();
        message.setTaskId(taskId);
        message.setCurrent(5);
        message.setTotal(10);
        message.setSuccessCount(4);
        message.setFailCount(1);
        message.setStatus("cancelled");

        assertEquals("cancelled", message.getStatus());
        assertEquals(5, message.getCurrent());
        assertEquals(10, message.getTotal());
    }
}
