package com.gx.etl.network.model;

import lombok.Data;

@Data
public class NetworkConfig {

    /**
     * TCP服务器端口
     */
    private int tcpPort = 9090;

    /**
     * UDP服务器端口
     */
    private int udpPort = 9091;

    /**
     * 是否启用TCP服务器
     */
    private boolean tcpEnabled = true;

    /**
     * 是否启用UDP服务器
     */
    private boolean udpEnabled = true;

    /**
     * 最大连接数
     */
    private int maxConnections = 1000;

    /**
     * 读取超时(秒)
     */
    private int readTimeout = 300;

    /**
     * 心跳间隔(秒)
     */
    private int heartbeatInterval = 60;
}
