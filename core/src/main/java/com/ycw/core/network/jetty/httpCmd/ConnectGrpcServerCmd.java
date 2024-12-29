package com.ycw.core.network.jetty.httpCmd;

import com.ycw.core.cluster.entity.ServerEntity;
import com.ycw.core.internal.event.EventBusesImpl;
import com.ycw.core.network.grpc.event.ConnectGrpcServerEvent;
import com.ycw.core.network.jetty.http.HttpSession;
import com.ycw.core.util.SerializationUtils;

/**
 * <开启grpc客户端实现类>
 * <p>
 *
 * @author <yangcaiwang>
 * @version <1.0>
 */
public class ConnectGrpcServerCmd extends BaseHttpCmd {
    @Override
    public boolean execute(HttpSession httpSession) {
        EventBusesImpl.getInstance().syncPublish(new ConnectGrpcServerEvent(SerializationUtils.jsonToBean(httpSession.getParameters().get("serverEntity"), ServerEntity.class)));
        httpSession.sendHttpResponseSuccess();
        return true;
    }
}
