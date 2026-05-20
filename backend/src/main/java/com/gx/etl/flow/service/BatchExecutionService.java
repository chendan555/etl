package com.gx.etl.flow.service;

import com.gx.etl.flow.entity.ExecutionLog;
import com.gx.etl.flow.entity.Transformation;
import com.gx.etl.flow.websocket.BatchProgressWebSocket;
import com.gx.etl.flow.websocket.ProgressMessage;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

/**
 * 批量执行服务（支持并行执行）
 */
@Service
public class BatchExecutionService {

    @Resource
    private TransformationService transformationService;

    @Resource
    private FlowExecutionEngine executionEngine;

    // 从配置文件读取并行执行的线程数
    @Value("${batch.execution.parallel-size:5}")
    private int parallelSize;

    // 存储取消标志
    private static final ConcurrentHashMap<String, AtomicBoolean> cancelFlags = new ConcurrentHashMap<>();

    /**
     * 异步执行批量任务
     */
    @Async
    public void executeBatchAsync(String taskId, Long transformationId, List<Map<String, String>> paramList) {
        System.out.println("[BatchExecution] 准备执行: taskId=" + taskId + ", 并行数=" + parallelSize);

        // 初始化取消标志
        cancelFlags.put(taskId, new AtomicBoolean(false));

        // 等待 WebSocket 连接建立
        waitForConnection(taskId);

        System.out.println("[BatchExecution] 开始异步执行: taskId=" + taskId);

        // 加载流程
        Transformation trans = transformationService.getById(transformationId);
        if (trans == null || trans.getFlowJson() == null || trans.getFlowJson().isEmpty()) {
            sendError(taskId, "流程不存在或未保存: " + transformationId);
            return;
        }
        String flowJson = trans.getFlowJson();
        String transName = trans.getName();

        // 线程安全的统计变量
        AtomicInteger successCount = new AtomicInteger(0);
        AtomicInteger failCount = new AtomicInteger(0);
        AtomicInteger completedCount = new AtomicInteger(0);
        AtomicLong totalRows = new AtomicLong(0);
        long startTime = System.currentTimeMillis();

        // 使用 CopyOnWriteArrayList 确保线程安全
        List<ProgressMessage.BatchDetail> batchDetails = new CopyOnWriteArrayList<>();

        // 创建线程池
        ExecutorService executor = Executors.newFixedThreadPool(parallelSize);
        List<Future<?>> futures = new ArrayList<>();

        // 提交所有批次任务
        for (int i = 0; i < paramList.size(); i++) {
            final int batchIndex = i;
            final Map<String, String> row = paramList.get(i);

            Future<?> future = executor.submit(() -> {
                // 检查取消标志
                if (cancelFlags.get(taskId).get()) {
                    return;
                }

                System.out.println("[BatchExecution] 第 " + (batchIndex + 1) + " 批开始执行: " + row);

                ExecutionLog log = null;
                long batchStartTime = System.currentTimeMillis();

                try {
                    log = transformationService.createExecutionLog(transformationId, transName + "（第" + (batchIndex + 1) + "批）");

                    // 转换参数类型
                    Map<String, Object> paramMap = new HashMap<>(row);

                    FlowExecutionEngine.ExecutionResult result = executionEngine.executeAndStore(flowJson, transformationId, paramMap);

                    if (log != null) {
                        transformationService.updateExecutionLog(log, 1, null, result.getTotalRows());
                    }

                    successCount.incrementAndGet();
                    totalRows.addAndGet(result.getTotalRows());

                    // 创建批次详情
                    ProgressMessage.BatchDetail detail = new ProgressMessage.BatchDetail();
                    detail.setIndex(batchIndex + 1);
                    detail.setParams(row);
                    detail.setStatus("success");
                    detail.setElapsedTime(System.currentTimeMillis() - batchStartTime);
                    detail.setRowCount(result.getTotalRows());
                    batchDetails.add(detail);

                    System.out.println("[BatchExecution] 第 " + (batchIndex + 1) + " 批执行成功");

                } catch (Exception e) {
                    failCount.incrementAndGet();
                    if (log != null) {
                        transformationService.updateExecutionLog(log, 2, e.getMessage(), 0);
                    }

                    // 创建失败批次详情
                    ProgressMessage.BatchDetail detail = new ProgressMessage.BatchDetail();
                    detail.setIndex(batchIndex + 1);
                    detail.setParams(row);
                    detail.setStatus("fail");
                    detail.setElapsedTime(System.currentTimeMillis() - batchStartTime);
                    detail.setRowCount(0);
                    detail.setErrorMsg(e.getMessage());
                    batchDetails.add(detail);

                    System.out.println("[BatchExecution] 第 " + (batchIndex + 1) + " 批执行失败: " + e.getMessage());
                }

                // 更新完成数并发送进度消息
                int completed = completedCount.incrementAndGet();
                sendProgressMessage(taskId, completed, paramList.size(), successCount.get(), failCount.get(),
                        totalRows.get(), startTime, new ArrayList<>(batchDetails));
            });

            futures.add(future);
        }

        // 等待所有任务完成
        for (Future<?> future : futures) {
            try {
                future.get();
            } catch (InterruptedException | ExecutionException e) {
                System.err.println("[BatchExecution] 任务执行异常: " + e.getMessage());
            }
        }

        // 关闭线程池
        executor.shutdown();

        // 检查是否被取消
        if (cancelFlags.get(taskId).get()) {
            System.out.println("[BatchExecution] 任务被取消: taskId=" + taskId);
            sendCancelledMessage(taskId, completedCount.get(), paramList.size(), successCount.get(), failCount.get(),
                    totalRows.get(), startTime, new ArrayList<>(batchDetails));
        } else {
            // 执行完成
            sendCompletedMessage(taskId, paramList.size(), successCount.get(), failCount.get(),
                    totalRows.get(), startTime, new ArrayList<>(batchDetails));
            System.out.println("[BatchExecution] 执行完成: taskId=" + taskId);
        }

        cleanup(taskId);
    }

    /**
     * 取消任务
     */
    public void cancelTask(String taskId) {
        AtomicBoolean cancelFlag = cancelFlags.get(taskId);
        if (cancelFlag != null) {
            cancelFlag.set(true);
            System.out.println("[BatchExecution] 设置取消标志: taskId=" + taskId);
        }
    }

    /**
     * 检查任务是否已取消
     */
    public boolean isCancelled(String taskId) {
        AtomicBoolean cancelFlag = cancelFlags.get(taskId);
        return cancelFlag != null && cancelFlag.get();
    }

    /**
     * 发送进度消息
     */
    private void sendProgressMessage(String taskId, int current, int total, int successCount, int failCount,
                                     long totalRows, long startTime, List<ProgressMessage.BatchDetail> batchDetails) {
        ProgressMessage message = new ProgressMessage();
        message.setTaskId(taskId);
        message.setCurrent(current);
        message.setTotal(total);
        message.setSuccessCount(successCount);
        message.setFailCount(failCount);
        message.setElapsedTime(System.currentTimeMillis() - startTime);
        message.setEstimatedRemaining(calculateEstimatedRemaining(current, total, startTime));
        message.setTotalRows(totalRows);
        message.setStatus("running");
        message.setBatchDetails(batchDetails);

        BatchProgressWebSocket.sendProgress(taskId, message);
    }

    /**
     * 发送完成消息
     */
    private void sendCompletedMessage(String taskId, int total, int successCount, int failCount,
                                      long totalRows, long startTime, List<ProgressMessage.BatchDetail> batchDetails) {
        ProgressMessage message = new ProgressMessage();
        message.setTaskId(taskId);
        message.setCurrent(total);
        message.setTotal(total);
        message.setSuccessCount(successCount);
        message.setFailCount(failCount);
        message.setElapsedTime(System.currentTimeMillis() - startTime);
        message.setEstimatedRemaining(0);
        message.setTotalRows(totalRows);
        message.setStatus("completed");
        message.setBatchDetails(batchDetails);

        BatchProgressWebSocket.sendProgress(taskId, message);
    }

    /**
     * 发送取消消息
     */
    private void sendCancelledMessage(String taskId, int current, int total, int successCount, int failCount,
                                      long totalRows, long startTime, List<ProgressMessage.BatchDetail> batchDetails) {
        ProgressMessage message = new ProgressMessage();
        message.setTaskId(taskId);
        message.setCurrent(current);
        message.setTotal(total);
        message.setSuccessCount(successCount);
        message.setFailCount(failCount);
        message.setElapsedTime(System.currentTimeMillis() - startTime);
        message.setEstimatedRemaining(0);
        message.setTotalRows(totalRows);
        message.setStatus("cancelled");
        message.setBatchDetails(batchDetails);

        BatchProgressWebSocket.sendProgress(taskId, message);
    }

    /**
     * 发送错误消息
     */
    private void sendError(String taskId, String errorMsg) {
        ProgressMessage message = new ProgressMessage();
        message.setTaskId(taskId);
        message.setStatus("error");

        ProgressMessage.BatchDetail errorDetail = new ProgressMessage.BatchDetail();
        errorDetail.setErrorMsg(errorMsg);
        message.setBatchDetails(Collections.singletonList(errorDetail));

        BatchProgressWebSocket.sendProgress(taskId, message);
        cleanup(taskId);
    }

    /**
     * 计算预计剩余时间
     */
    private long calculateEstimatedRemaining(int current, int total, long startTime) {
        if (current == 0) return 0;
        long elapsed = System.currentTimeMillis() - startTime;
        long avgTimePerBatch = elapsed / current;
        return avgTimePerBatch * (total - current);
    }

    /**
     * 等待 WebSocket 连接建立
     */
    private void waitForConnection(String taskId) {
        int maxWaitTime = 5000;
        int waitInterval = 100;
        int waited = 0;

        while (!BatchProgressWebSocket.hasConnections(taskId) && waited < maxWaitTime) {
            try {
                Thread.sleep(waitInterval);
                waited += waitInterval;
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            }
        }

        if (BatchProgressWebSocket.hasConnections(taskId)) {
            System.out.println("[BatchExecution] WebSocket 连接已建立: taskId=" + taskId);
        } else {
            System.out.println("[BatchExecution] 等待 WebSocket 连接超时: taskId=" + taskId);
        }
    }

    /**
     * 清理任务资源
     */
    private void cleanup(String taskId) {
        cancelFlags.remove(taskId);
    }
}
