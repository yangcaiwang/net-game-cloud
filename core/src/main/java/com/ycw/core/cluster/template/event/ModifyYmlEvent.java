package com.ycw.core.cluster.template.event;


import com.ycw.core.cluster.enums.ServerType;
import com.ycw.core.cluster.template.ServerYmlTemplate;
import com.ycw.core.internal.event.AbstractEvent;

/**
 * <话题(topic)事件类>
 * <p>
 *
 * @author <yangcaiwang>
 * @version <1.0>
 */
public class ModifyYmlEvent extends AbstractEvent {

    private ServerYmlTemplate serverYmlTemplate;

    private ServerType serverType;

    public ModifyYmlEvent(ServerYmlTemplate serverYmlTemplate, ServerType serverType) {
        this.serverYmlTemplate = serverYmlTemplate;
        this.serverType = serverType;
    }

    public ServerYmlTemplate getServerYmlTemplate() {
        return serverYmlTemplate;
    }

    public void setServerYmlTemplate(ServerYmlTemplate serverYmlTemplate) {
        this.serverYmlTemplate = serverYmlTemplate;
    }

    public ServerType getServerType() {
        return serverType;
    }

    public void setServerType(ServerType serverType) {
        this.serverType = serverType;
    }
}
