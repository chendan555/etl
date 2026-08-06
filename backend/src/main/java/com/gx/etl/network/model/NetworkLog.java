package com.gx.etl.network.model;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("network_log")
public class NetworkLog {

    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 协议类型: TCP/UDP
     */
    private String protocol;

    /**
     * 方向: SEND/RECEIVE
     */
    private String direction;

    /**
     * 消息内容
     */
    private String message;

    /**
     * 响应内容
     */
    private String response;

    /**
     * 客户端IP
     */
    private String clientIp;

    /**
     * 客户端端口
     */
    private Integer clientPort;

    /**
     * 状态: SUCCESS/FAIL
     */
    private String status;

    /**
     * 错误信息
     */
    private String errorMsg;

    /**
     * 创建时间
     */
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    public NetworkLog() {
        this.createTime = LocalDateTime.now();
    }
}
