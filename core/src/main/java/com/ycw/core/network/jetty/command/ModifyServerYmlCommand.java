package com.ycw.core.network.jetty.command;

import com.ycw.core.cluster.node.ServerNode;
import com.ycw.core.cluster.template.ServerYmlTemplate;
import com.ycw.core.cluster.template.event.ModifyYmlEvent;
import com.ycw.core.internal.event.EventBusesImpl;
import com.ycw.core.network.jetty.http.HttpSession;
import com.ycw.core.util.SerializationUtils;

/**
 * <更新目标服务器yml配置实现类>
 * <p>
 *
 * @author <yangcaiwang>
 * @version <1.0>
 */
public class ModifyServerYmlCommand extends BaseHttpCommand {
    @Override
    public boolean execute(HttpSession httpSession) {
        ServerYmlTemplate serverYmlTemplate = SerializationUtils.jsonToBean(httpSession.getParameters().get("serverYmlTemplate"), ServerYmlTemplate.class);
        EventBusesImpl.getInstance().syncPublish(new ModifyYmlEvent(serverYmlTemplate, ServerNode.getInstance().serverType()));
        httpSession.sendHttpResponseSuccess();
        return true;
    }
}
