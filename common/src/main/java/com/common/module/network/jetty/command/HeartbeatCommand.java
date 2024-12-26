package com.common.module.network.jetty.command;

import com.common.module.network.jetty.JettyHttpServer;
import com.common.module.network.jetty.http.HttpSession;
import org.apache.commons.lang3.StringUtils;

/**
 * <http心跳实现类>
 * <p>
 * ps: gm服做注册中心
 *
 * @author <yangcaiwang>
 * @version <1.0>
 */
public class HeartbeatCommand extends AbsHttpCommand {
    @Override
    protected boolean execute(HttpSession httpSession) {
        String serverId = httpSession.getParameters().get("serverId");
        if (StringUtils.isNotEmpty(serverId)) {
            JettyHttpServer.getInstance().getJettyHeartbeatProcess().getHeartbeatMap().put(serverId, System.currentTimeMillis());
            return true;
        }

        return false;
    }
}
