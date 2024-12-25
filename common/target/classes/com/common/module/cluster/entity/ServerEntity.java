package com.common.module.cluster.entity;

import com.common.module.cluster.constant.ClusterConstant;
import com.common.module.cluster.enums.ServerType;

import java.io.Serializable;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * <服务器实体类>
 * <p>
 *
 * @author <yangcaiwang>
 * @version <1.0>
 */
public class ServerEntity implements Serializable {
    /**
     * 服务器id
     */
    private String serverId;

    /**
     * 组id
     */
    private int groupId;

    /**
     * 权重
     */
    private int weight;

    /**
     * 服务器类型 {@link ServerType}
     */
    private ServerType serverType;

    /**
     * 服务器ip
     */
    private String host;

    /**
     * 服务器端口号
     */
    private int port;

    /**
     * grpc服务端ip
     */
    private String grpcServerHost;

    /**
     * grpc服务端 端口号
     */
    private int grpcServerPort;

    private CopyOnWriteArrayList<String> grpcServerId = new CopyOnWriteArrayList<>();

    /**
     * grpc客户端ip
     */
    private CopyOnWriteArrayList<String> grpcClientHost = new CopyOnWriteArrayList<>();

    /**
     * grpc客户端 端口号
     */
    private CopyOnWriteArrayList<Integer> grpcClientPort = new CopyOnWriteArrayList<>();

    /**
     * Jetty服务器暴露ip
     */
    private String jettyHost;

    /**
     * Jetty服务器暴露端口号
     */
    private int jettyPort;


    public int getGroupId() {
        return groupId;
    }

    public void setGroupId(int groupId) {
        this.groupId = groupId;
    }

    public int getWeight() {
        return weight;
    }

    public void setWeight(int weight) {
        this.weight = weight;
    }

    public ServerType getServerType() {
        return serverType;
    }

    public void setServerType(ServerType serverType) {
        this.serverType = serverType;
    }

    public String getServerId() {
        return serverId;
    }

    public void setServerId(String serverId) {
        this.serverId = serverId;
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

    public String getGrpcServerHost() {
        return grpcServerHost;
    }

    public void setGrpcServerHost(String grpcServerHost) {
        this.grpcServerHost = grpcServerHost;
    }

    public int getGrpcServerPort() {
        return grpcServerPort;
    }

    public void setGrpcServerPort(int grpcServerPort) {
        this.grpcServerPort = grpcServerPort;
    }

    public CopyOnWriteArrayList<String> getGrpcServerId() {
        return grpcServerId;
    }

    public void setGrpcServerId(CopyOnWriteArrayList<String> grpcServerId) {
        this.grpcServerId = grpcServerId;
    }

    public CopyOnWriteArrayList<String> getGrpcClientHost() {
        return grpcClientHost;
    }

    public void setGrpcClientHost(CopyOnWriteArrayList<String> grpcClientHost) {
        this.grpcClientHost = grpcClientHost;
    }

    public CopyOnWriteArrayList<Integer> getGrpcClientPort() {
        return grpcClientPort;
    }

    public void setGrpcClientPort(CopyOnWriteArrayList<Integer> grpcClientPort) {
        this.grpcClientPort = grpcClientPort;
    }

    public String getClusterGroup() {
        return ClusterConstant.CLUSTER_PREFIX + "_" + this.groupId;
    }

    public String getJettyHost() {
        return jettyHost;
    }

    public void setJettyHost(String jettyHost) {
        this.jettyHost = jettyHost;
    }

    public int getJettyPort() {
        return jettyPort;
    }

    public void setJettyPort(int jettyPort) {
        this.jettyPort = jettyPort;
    }
}
