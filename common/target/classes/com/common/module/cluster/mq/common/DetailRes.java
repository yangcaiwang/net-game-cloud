package com.common.module.cluster.mq.common;

//统一返回值,可描述失败细节
public class DetailRes {
    boolean isSuccess;
    String errMsg;

    public boolean isSuccess() {
        return isSuccess;
    }

    public void setSuccess(boolean success) {
        isSuccess = success;
    }

    public String getErrMsg() {
        return errMsg;
    }

    public void setErrMsg(String errMsg) {
        this.errMsg = errMsg;
    }

    public DetailRes(boolean isSuccess, String errMsg) {
        this.isSuccess = isSuccess;
        this.errMsg = errMsg;
    }
}
