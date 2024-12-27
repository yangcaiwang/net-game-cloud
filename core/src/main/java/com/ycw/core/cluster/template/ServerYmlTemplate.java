package com.ycw.core.cluster.template;

import java.io.Serializable;

/**
 * <服务器解析yml模版类>
 * <p>
 *
 * @author <yangcaiwang>
 * @version <1.0>
 */
public class ServerYmlTemplate implements Serializable {
    private NodeYmlTemplate node;
    private GrpcYmlTemplate grpc;
    private JettyYmlTemplate jetty;
    private NettyYmlTemplate netty;
    private DbYmlTemplate dbGame;
    private DbYmlTemplate dbLog;

    public NodeYmlTemplate getNode() {
        return node;
    }

    public void setNode(NodeYmlTemplate node) {
        this.node = node;
    }

    public GrpcYmlTemplate getGrpc() {
        return grpc;
    }

    public void setGrpc(GrpcYmlTemplate grpc) {
        this.grpc = grpc;
    }

    public JettyYmlTemplate getJetty() {
        return jetty;
    }

    public void setJetty(JettyYmlTemplate jetty) {
        this.jetty = jetty;
    }

    public NettyYmlTemplate getNetty() {
        return netty;
    }

    public void setNetty(NettyYmlTemplate netty) {
        this.netty = netty;
    }

    public DbYmlTemplate getDbGame() {
        return dbGame;
    }

    public void setDbGame(DbYmlTemplate dbGame) {
        this.dbGame = dbGame;
    }

    public DbYmlTemplate getDbLog() {
        return dbLog;
    }

    public void setDbLog(DbYmlTemplate dbLog) {
        this.dbLog = dbLog;
    }
}
