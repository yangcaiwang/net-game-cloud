package com.common.module.cluster.mq.common;

public class MessageWithTime {
    private long id;
    private long time;
    private Object message;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public Object getMessage() {
        return message;
    }

    public void setMessage(Object message) {
        this.message = message;
    }

    public MessageWithTime(long id, long time, Object message) {
        this.id = id;
        this.time = time;
        this.message = message;
    }
}
