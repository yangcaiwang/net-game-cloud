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
    private BaseYmlTemplate grpc;
    private BaseYmlTemplate jetty;
    private BaseYmlTemplate netty;
    private DbYmlTemplate dbGame;
    private DbYmlTemplate dbLog;

    public NodeYmlTemplate getNode() {
        return node;
    }

    public void setNode(NodeYmlTemplate node) {
        this.node = node;
    }

    public BaseYmlTemplate getGrpc() {
        return grpc;
    }

    public void setGrpc(BaseYmlTemplate grpc) {
        this.grpc = grpc;
    }

    public BaseYmlTemplate getJetty() {
        return jetty;
    }

    public void setJetty(BaseYmlTemplate jetty) {
        this.jetty = jetty;
    }

    public BaseYmlTemplate getNetty() {
        return netty;
    }

    public void setNetty(BaseYmlTemplate netty) {
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

    @Override
    public String toString() {
        return "ServerYmlTemplate{" +
                "node=" + node +
                ", grpc=" + grpc +
                ", jetty=" + jetty +
                ", netty=" + netty +
                ", dbGame=" + dbGame +
                ", dbLog=" + dbLog +
                '}';
    }
}
