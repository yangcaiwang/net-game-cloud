package com.gm.server.admin.domain.model;

import com.alibaba.fastjson.JSONObject;

import java.io.Serializable;

public class SdkVipCustomerServiceInfo implements Serializable {
    private long roleId;
    private int cd;
    private String aid;
    private long time;

    public long getRoleId() {
        return roleId;
    }

    public void setRoleId(long roleId) {
        this.roleId = roleId;
    }

    public int getCd() {
        return cd;
    }

    public void setCd(int cd) {
        this.cd = cd;
    }

    public String getAid() {
        return aid;
    }

    public void setAid(String aid) {
        this.aid = aid;
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
        object.put("cd", cd);
        object.put("aid", aid);
        object.put("time", time);
        return object;
    }
}
