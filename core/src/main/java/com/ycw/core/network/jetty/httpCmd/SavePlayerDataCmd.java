package com.ycw.core.network.jetty.httpCmd;

import com.ycw.core.cluster.entity.ServerEntity;
import com.ycw.core.internal.event.EventBusesImpl;
import com.ycw.core.network.grpc.event.ConnectGrpcServerEvent;
import com.ycw.core.network.jetty.http.HttpSession;
import com.ycw.core.util.SerializationUtils;

/**
 * <保存玩家数据实现类>
 * <p>
 *
 * @author <yangcaiwang>
 * @version <1.0>
 */
public class SavePlayerDataCmd extends BaseHttpCmd {
    @Override
    public boolean execute(HttpSession httpSession) {
        String type = httpSession.getParameters().get("value");
        if (type.equals("allPlayer")) {
            EventBusesImpl.getInstance().syncPublish(new ConnectGrpcServerEvent(SerializationUtils.jsonToBean(httpSession.getParameters().get("serverEntity"), ServerEntity.class)));
            httpSession.sendHttpResponseSuccess();
        } else {

        }
        return true;
    }
}
