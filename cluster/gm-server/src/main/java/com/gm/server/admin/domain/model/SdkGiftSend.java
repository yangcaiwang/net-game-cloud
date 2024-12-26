package com.gm.server.admin.domain.model;

import com.alibaba.fastjson.JSONObject;

import java.io.Serializable;

public class SdkGiftSend implements Serializable {
    private long roleId;
    private String userId;
    private int giftId;
    private long time;

    public long getRoleId() {
        return roleId;
    }

    public void setRoleId(long roleId) {
        this.roleId = roleId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public int getGiftId() {
        return giftId;
    }

    public void setGiftId(int giftId) {
        this.giftId = giftId;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public JSONObject toJSONObject() {
        JSONObject object = new JSONObject();
        object.put("roleId", roleId);
        object.put("userId", userId);
        object.put("giftId", giftId);
        object.put("time", time);
        return object;
    }
}
