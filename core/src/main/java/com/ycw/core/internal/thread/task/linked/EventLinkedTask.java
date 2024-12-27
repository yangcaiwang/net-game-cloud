package com.ycw.core.internal.thread.task.linked;

import com.ycw.core.internal.event.AbstractEvent;
import com.ycw.core.internal.event.EventObjectPool;

import java.util.function.Consumer;

public class EventLinkedTask extends AbstractLinkedTask {

    private AbstractEvent event;
    private Object object;
    private Consumer<AbstractEvent> consumer;

    @Override
    protected void exec() throws Exception {
        consumer.accept(event);
        EventObjectPool.collectEventObj(this);
    }

    public EventLinkedTask build(Consumer<AbstractEvent> consumer) {
        this.consumer = consumer;
        return this;
    }

    @Override
    public Object getIdentity() {
        if (object != null) {
            return object;
        }
        return event.getClass();
    }

    public AbstractEvent getEvent() {
        return event;
    }

    public void setEvent(AbstractEvent event) {
        this.event = event;
    }

    public Object getObject() {
        return object;
    }

    public void setObject(Object object) {
        this.object = object;
    }
}
