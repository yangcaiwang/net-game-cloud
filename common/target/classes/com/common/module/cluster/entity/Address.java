package com.common.module.cluster.entity;

import java.io.Serializable;

/**
 * <服务器解析地址类>
 * <p>
 *
 * @author <yangcaiwang>
 * @version <1.0>
 */
public class Address implements Serializable {
    /**
     * ip
     */
    private String host;

    /**
     * 端口号
     */
    private int port;

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }
}
