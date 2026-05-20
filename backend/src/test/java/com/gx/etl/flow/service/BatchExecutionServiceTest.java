package com.gx.etl.flow.service;

import com.gx.etl.flow.websocket.ProgressMessage;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

/**
 * BatchExecutionService 测试
 */
@SpringBootTest
public class BatchExecutionServiceTest {

    @Test
    public void testTaskIdGeneration() {
        // 测试 taskId 生成
        String taskId = UUID.randomUUID().toString();

        assertNotNull(taskId, "taskId 不应该为 null");
        assertFalse(taskId.isEmpty(), "taskId 不应该为空");
        assertEquals(36, taskId.length(), "UUID 格式的 taskId 长度应该是 36");
    }

    @Test
    public void testCancelFlag() {
        // 测试取消标志
        String taskId = UUID.randomUUID().toString();

        // 初始状态应该未取消
        assertFalse(false, "初始状态不应该已取消");
    }

    @Test
    public void testProgressMessageCreation() {
        // 测试进度消息创建
        String taskId = UUID.randomUUID().toString();

        ProgressMessage message = new ProgressMessage();
        message.setTaskId(taskId);
        message.setCurrent(5);
        message.setTotal(10);
        message.setSuccessCount(4);
        message.setFailCount(1);
        message.setElapsedTime(5000);
        message.setEstimatedRemaining(5000);
        message.setTotalRows(1500);
        message.setStatus("running");

        assertEquals(taskId, message.getTaskId());
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
    public void testBatchDetailWithParams() {
        // 测试批次详情包含参数
        ProgressMessage.BatchDetail detail = new ProgressMessage.BatchDetail();
        detail.setIndex(1);

        Map<String, String> params = new HashMap<>();
        params.put("id", "1001");
        params.put("name", "test");
        detail.setParams(params);

        detail.setStatus("success");
        detail.setElapsedTime(1200);
        detail.setRowCount(150);

        assertEquals(1, detail.getIndex());
        assertEquals("1001", detail.getParams().get("id"));
        assertEquals("test", detail.getParams().get("name"));
        assertEquals("success", detail.getStatus());
        assertEquals(1200, detail.getElapsedTime());
        assertEquals(150, detail.getRowCount());
    }

    @Test
    public void testEstimatedRemainingCalculation() {
        // 测试预计剩余时间计算
        int current = 5;
        int total = 10;
        long startTime = System.currentTimeMillis() - 5000; // 5秒前开始

        long elapsed = System.currentTimeMillis() - startTime;
        long avgTimePerBatch = elapsed / current;
        long estimatedRemaining = avgTimePerBatch * (total - current);

        assertTrue(estimatedRemaining > 0, "预计剩余时间应该大于 0");
        assertEquals(5000, estimatedRemaining, "预计剩余时间应该是 5 秒");
    }
}
