package com.ycw.core.cluster.entity;

import com.ycw.core.cluster.enums.ServerState;
import com.ycw.core.cluster.enums.ServerType;

import java.io.Serializable;
import java.util.concurrent.ConcurrentHashMap;

/**
 * <服务器实体类>
 * <p>
 * ps: 注册到redission分布式配置中心
 * 优点
 * 1.增加代码可读性和拓展性：如果需要添加新的属性，只需要在Builder类中添加相应的方法，而不需要修改构造函数
 * 2.不可变对象和线程安全：一旦创建后其状态（属性）就不能被改变的对象。由于不可变对象的状态不能改变，它们天然就是线程安全的，因为不存在并发修改状态的问题。
 * 3.只提供 serverType,serverState,grpcClientAddr的set方法
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
     * 服务器类型 {@link ServerType}
     */
    private ServerType serverType;

    /**
     * 服务器状态 {@link ServerState}
     */
    private ServerState serverState;

    /**
     * 组id
     */
    private int groupId;

    /**
     * 权重
     */
    private int weight;

    /**
     * 服务器地址
     */
    private AddressInfo serverAddr;

    /**
     * 游戏库数据源信息
     */
    private DataSourceInfo dbGameSourceInfo;

    /**
     * 日志库数据源信息
     */
    private DataSourceInfo dbLogSourceInfo;

    /**
     * grpc服务端地址
     */
    private AddressInfo grpcServerAddr;

    /**
     * grpc客户端地址<目标serverId,目标grpcServerAddr></>
     */
    private ConcurrentHashMap<String, AddressInfo> grpcClientAddr = new ConcurrentHashMap<>();

    /**
     * Jetty服务端地址
     */
    private AddressInfo jettyServerAddr;

    /**
     * netty服务端地址
     */
    private AddressInfo nettyServerAddr;

    // 私有构造函数，防止外部直接构造
    public ServerEntity(Builder builder) {
        this.serverId = builder.serverId;
        this.serverType = builder.serverType;
        this.serverState = builder.serverState;
        this.groupId = builder.groupId;
        this.weight = builder.weight;
        this.serverAddr = builder.serverAddr;
        this.dbGameSourceInfo = builder.dbGameSourceInfo;
        this.dbLogSourceInfo = builder.dbLogSourceInfo;
        this.grpcServerAddr = builder.grpcServerAddr;
        this.grpcClientAddr = builder.grpcClientAddr;
        this.jettyServerAddr = builder.jettyServerAddr;
        this.nettyServerAddr = builder.nettyServerAddr;
    }

    // 建造者内部类
    public static class Builder {
        private String serverId;
        private ServerType serverType;
        private ServerState serverState;
        private int groupId;
        private int weight;
        private AddressInfo serverAddr;
        private DataSourceInfo dbGameSourceInfo;
        private DataSourceInfo dbLogSourceInfo;
        private AddressInfo grpcServerAddr;
        private ConcurrentHashMap<String, AddressInfo> grpcClientAddr = new ConcurrentHashMap<>();
        private AddressInfo jettyServerAddr;
        private AddressInfo nettyServerAddr;

        // 建造者类的构造函数
        public Builder() {
        }

        // 链式设置属性值
        public Builder serverId(String serverId) {
            this.serverId = serverId;
            return this;
        }

        public Builder serverType(ServerType serverType) {
            this.serverType = serverType;
            return this;
        }

        public Builder serverState(ServerState serverState) {
            this.serverState = serverState;
            return this;
        }

        public Builder groupId(int groupId) {
            this.groupId = groupId;
            return this;
        }

        public Builder weight(int weight) {
            this.weight = weight;
            return this;
        }

        public Builder serverAddr(AddressInfo serverAddr) {
            this.serverAddr = serverAddr;
            return this;
        }

        public Builder dbGameSourceInfo(DataSourceInfo dbGameSourceInfo) {
            this.dbGameSourceInfo = dbGameSourceInfo;
            return this;
        }

        public Builder dbLogSourceInfo(DataSourceInfo dbLogSourceInfo) {
            this.dbLogSourceInfo = dbLogSourceInfo;
            return this;
        }

        public Builder grpcServerAddr(AddressInfo grpcServerAddr) {
            this.grpcServerAddr = grpcServerAddr;
            return this;
        }

        public Builder grpcClientAddr(ConcurrentHashMap<String, AddressInfo> grpcClientAddr) {
            this.grpcClientAddr = grpcClientAddr;
            return this;
        }

        public Builder jettyServerAddr(AddressInfo jettyServerAddr) {
            this.jettyServerAddr = jettyServerAddr;
            return this;
        }

        public Builder nettyServerAddr(AddressInfo nettyServerAddr) {
            this.nettyServerAddr = nettyServerAddr;
            return this;
        }

        public ServerEntity build() {
            return new ServerEntity(this);
        }
    }

    public String getServerId() {
        return serverId;
    }

    public ServerType getServerType() {
        return serverType;
    }

    public ServerState getServerState() {
        return serverState;
    }

    public int getGroupId() {
        return groupId;
    }

    public int getWeight() {
        return weight;
    }

    public AddressInfo getServerAddr() {
        return serverAddr;
    }

    public DataSourceInfo getDbGameSourceInfo() {
        return dbGameSourceInfo;
    }

    public DataSourceInfo getDbLogSourceInfo() {
        return dbLogSourceInfo;
    }

    public AddressInfo getGrpcServerAddr() {
        return grpcServerAddr;
    }

    public ConcurrentHashMap<String, AddressInfo> getGrpcClientAddr() {
        return grpcClientAddr;
    }

    public AddressInfo getJettyServerAddr() {
        return jettyServerAddr;
    }

    public AddressInfo getNettyServerAddr() {
        return nettyServerAddr;
    }

    public void setServerType(ServerType serverType) {
        this.serverType = serverType;
    }

    public void setServerState(ServerState serverState) {
        this.serverState = serverState;
    }

    public void setGrpcClientAddr(ConcurrentHashMap<String, AddressInfo> grpcClientAddr) {
        this.grpcClientAddr = grpcClientAddr;
    }

    @Override
    public String toString() {
        return "ServerEntity{" +
                "serverId='" + serverId + '\'' +
                ", serverType=" + serverType +
                ", serverState=" + serverState +
                ", groupId=" + groupId +
                ", weight=" + weight +
                ", serverAddr=" + serverAddr +
                ", dbGameSourceInfo=" + dbGameSourceInfo +
                ", dbLogSourceInfo=" + dbLogSourceInfo +
                ", grpcServerAddr=" + grpcServerAddr +
                ", grpcClientAddr=" + grpcClientAddr +
                ", jettyServerAddr=" + jettyServerAddr +
                ", nettyServerAddr=" + nettyServerAddr +
                '}';
    }
}
