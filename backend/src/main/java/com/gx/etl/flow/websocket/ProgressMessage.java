package com.gx.etl.flow.websocket;

import lombok.Data;

import java.util.List;
import java.util.Map;

/**
 * 批量执行进度消息
 */
@Data
public class ProgressMessage {
    private String taskId;
    private int current;
    private int total;
    private int successCount;
    private int failCount;
    private long elapsedTime;
    private long estimatedRemaining;
    private long totalRows;
    private String status; // running, completed, cancelled
    private List<BatchDetail> batchDetails;

    @Data
    public static class BatchDetail {
        private int index;
        private Map<String, String> params;
        private String status; // success, fail
        private long elapsedTime;
        private long rowCount;
        private String errorMsg;
    }
}
