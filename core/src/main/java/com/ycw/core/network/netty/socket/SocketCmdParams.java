package com.ycw.core.network.netty.socket;

import com.google.protobuf.Message;

import java.lang.reflect.Method;

/**
 * <业务处理器实现类>
 * <p>
 *
 * @author <yangcaiwang>
 * @version <1.0>
 */
public class SocketCmdParams {
    private int reqCmd;
    private int resCmd;
    private String comment;
    private Method method;

    private Class<? extends Message> reqMsgType;

    public int getReqCmd() {
        return reqCmd;
    }

    public void setReqCmd(int reqCmd) {
        this.reqCmd = reqCmd;
    }

    public int getResCmd() {
        return resCmd;
    }

    public void setResCmd(int resCmd) {
        this.resCmd = resCmd;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public Method getMethod() {
        return method;
    }

    public void setMethod(Method method) {
        this.method = method;
    }

    public Class<? extends Message> getReqMsgType() {
        return reqMsgType;
    }

    public void setReqMsgType(Class<? extends Message> reqMsgType) {
        this.reqMsgType = reqMsgType;
    }
}
