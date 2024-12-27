package com.ycw.core.internal.script;

import com.ycw.core.internal.event.AbstractEvent;

/**
 * <热更脚本事件类>
 * <p>
 *
 * @author <yangcaiwang>
 * @version <1.0>
 */
public class HotScriptEvent extends AbstractEvent {
    private Object obj;

    public static HotScriptEvent valueOf(Object obj) {
        HotScriptEvent event = new HotScriptEvent();
        event.obj = obj;
        return event;
    }

    public Object getObj() {
        return obj;
    }
}
