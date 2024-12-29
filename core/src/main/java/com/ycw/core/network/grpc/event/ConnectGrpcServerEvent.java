package com.ycw.core.network.grpc.event;

import com.ycw.core.cluster.entity.ServerEntity;
import com.ycw.core.internal.event.AbstractEvent;

public class ConnectGrpcServerEvent extends AbstractEvent {
    private ServerEntity serverEntity;

    public ConnectGrpcServerEvent(ServerEntity serverEntity) {
        this.serverEntity = serverEntity;
    }

    public ServerEntity getServerEntity() {
        return serverEntity;
    }

    public void setServerEntity(ServerEntity serverEntity) {
        this.serverEntity = serverEntity;
    }
}
