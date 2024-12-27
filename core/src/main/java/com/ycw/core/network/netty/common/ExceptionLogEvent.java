package com.ycw.core.network.netty.common;

import com.ycw.core.internal.event.AbstractEvent;

public class ExceptionLogEvent extends AbstractEvent {
    private long roleId;
    private int serverId;
    private String session;
    private String exception;
    private int req;
    private int resp;

    public static ExceptionLogEvent valueOf(long roleId, int serverId, String sessionId,
                                            String exception, int req, int resp) {
        ExceptionLogEvent event = new ExceptionLogEvent();
        event.setRoleId(roleId);
        event.setException(exception);
        event.setReq(req);
        event.setResp(resp);
        event.setServerId(serverId);
        event.setSession(sessionId);
        return event;
    }

    public long getRoleId() {
        return roleId;
    }

    public void setRoleId(long roleId) {
        this.roleId = roleId;
    }

    public int getServerId() {
        return serverId;
    }

    public void setServerId(int serverId) {
        this.serverId = serverId;
    }

    public String getSession() {
        return session;
    }

    public void setSession(String session) {
        this.session = session;
    }

    public String getException() {
        return exception;
    }

    public void setException(String exception) {
        this.exception = exception;
    }

    public int getReq() {
        return req;
    }

    public void setReq(int req) {
        this.req = req;
    }

    public int getResp() {
        return resp;
    }

    public void setResp(int resp) {
        this.resp = resp;
    }
}
