package com.ycw.core.cluster.template.event;

import com.ycw.core.cluster.property.PropertyConfig;
import com.ycw.core.internal.event.AbstractEventObserver;
import com.ycw.core.internal.event.annotation.EventSubscriber;

/**
 * <修改服务器yml文件观察者实现类>
 * <p>
 *
 * @author <yangcaiwang>
 * @version <1.0>
 */
public class ModifyYmlEventObserver extends AbstractEventObserver {
    @EventSubscriber
    private void rec(ModifyYmlEvent modifyYmlEvent) {
        PropertyConfig.modifyServerYml(modifyYmlEvent.getServerYmlTemplate(), modifyYmlEvent.getServerType());
    }
}
