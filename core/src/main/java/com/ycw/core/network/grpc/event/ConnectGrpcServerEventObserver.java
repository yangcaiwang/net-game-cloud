package com.ycw.core.network.grpc.event;

import com.ycw.core.cluster.entity.ServerEntity;
import com.ycw.core.internal.event.AbstractEventObserver;
import com.ycw.core.internal.event.annotation.EventSubscriber;
import com.ycw.core.network.grpc.GrpcManage;

public class ConnectGrpcServerEventObserver extends AbstractEventObserver {

    @EventSubscriber
    public void rec(ConnectGrpcServerEvent event) {
        ServerEntity serverEntity = event.getServerEntity();
        GrpcManage.getInstance().connectGrpcServer(serverEntity);
    }
}