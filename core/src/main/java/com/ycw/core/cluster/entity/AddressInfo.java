package com.ycw.core.cluster.entity;

import java.io.Serializable;

/**
 * <地址信息内部类>
 * <p>
 *
 * @author <yangcaiwang>
 * @version <1.0>
 */
public class AddressInfo implements Serializable {

    /**
     * ip
     */
    private String host;

    /**
     * 端口
     */
    private int port;

    /**
     * 地址
     */
    private String address;

    /**
     * 心跳时间
     */
    private long heartbeatTime;

    /**
     * 心跳超时时间
     */
    private long heartbeatTimeout;

    public static AddressInfo valueOf() {
        return new AddressInfo();
    }

    public static AddressInfo valueOf(String host, int port) {
        AddressInfo addressInfo = new AddressInfo();
        addressInfo.setHost(host);
        addressInfo.setPort(port);
        addressInfo.updateAddr();
        return addressInfo;
    }
    public static AddressInfo valueOf(String host, int port, long heartbeatTime, long heartbeatTimeout) {
        AddressInfo addressInfo = new AddressInfo();
        addressInfo.setHost(host);
        addressInfo.setPort(port);
        addressInfo.setHeartbeatTime(heartbeatTime);
        addressInfo.setHeartbeatTimeout(heartbeatTimeout);
        addressInfo.updateAddr();
        return addressInfo;
    }

    public void updateAddr() {
        address = host + ":" + port;
    }

    public void updateAddr(String host, int port) {
        setHost(host);
        setPort(port);
        updateAddr();
    }

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

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public long getHeartbeatTime() {
        return heartbeatTime;
    }

    public void setHeartbeatTime(long heartbeatTime) {
        this.heartbeatTime = heartbeatTime;
    }

    public long getHeartbeatTimeout() {
        return heartbeatTimeout;
    }

    public void setHeartbeatTimeout(long heartbeatTimeout) {
        this.heartbeatTimeout = heartbeatTimeout;
    }

    @Override
    public String toString() {
        return "AddressInfo{" +
                "host='" + host + '\'' +
                ", port=" + port +
                ", address='" + address + '\'' +
                ", heartbeatTime=" + heartbeatTime +
                ", heartbeatTimeout=" + heartbeatTimeout +
                '}';
    }
}