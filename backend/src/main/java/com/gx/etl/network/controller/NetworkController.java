package com.gx.etl.network.controller;

import com.gx.etl.network.handler.NetworkLogHandler;
import com.gx.etl.network.model.NetworkLog;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/v1/network")
public class NetworkController {

    @Autowired
    private NetworkLogHandler logHandler;

    /**
     * 发送日志
     */
    @PostMapping("/send")
    public Map<String, Object> sendLog(@RequestBody Map<String, String> request) {
        Map<String, Object> result = new HashMap<>();
        try {
            String protocol = request.get("protocol");
            String message = request.get("message");

            if (protocol == null || message == null) {
                result.put("success", false);
                result.put("error", "protocol and message are required");
                return result;
            }

            NetworkLog log = logHandler.sendLog(protocol.toUpperCase(), message);
            result.put("success", true);
            result.put("data", log);
        } catch (Exception e) {
            result.put("success", false);
            result.put("error", e.getMessage());
        }
        return result;
    }

    /**
     * 获取最近日志
     */
    @GetMapping("/logs")
    public Map<String, Object> getLogs() {
        Map<String, Object> result = new HashMap<>();
        List<NetworkLog> logs = logHandler.getRecentLogs();
        result.put("success", true);
        result.put("data", logs);
        return result;
    }

    /**
     * 清空日志
     */
    @DeleteMapping("/logs")
    public Map<String, Object> clearLogs() {
        Map<String, Object> result = new HashMap<>();
        logHandler.clearLogs();
        result.put("success", true);
        return result;
    }
}
