package com.common.module.internal.script;

import com.common.module.internal.event.AbstractEvent;

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
