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
     * 端口
     */
    private String address;

    public static AddressInfo valueOf() {
        return new AddressInfo();
    }

    public static AddressInfo valueOf(String host, int port) {
        AddressInfo addressInfo = new AddressInfo();
        addressInfo.setHost(host);
        addressInfo.setPort(port);
        addressInfo.setAddress(host + ":" + port);
        return addressInfo;
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

    @Override
    public String toString() {
        return "AddressInfo{" +
                "host='" + host + '\'' +
                ", port=" + port +
                ", address='" + address + '\'' +
                '}';
    }
}