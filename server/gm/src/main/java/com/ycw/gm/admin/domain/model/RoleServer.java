package com.ycw.gm.admin.domain.model;

import com.alibaba.fastjson.JSONObject;

import java.io.Serializable;

/**
 * @author wishcher tree
 * @date 2022/7/20 11:49
 */
public class RoleServer implements Serializable {
    private int serverId;
    private String roleId;
    private int template;
    private int level;
    private String name;
    private long time;// 创建时间-秒时间戳
    private long lastLoginTime;// 最后登录时间-秒时间戳

    public int getServerId() {
        return serverId;
    }

    public void setServerId(int serverId) {
        this.serverId = serverId;
    }

    public String getRoleId() {
        return roleId;
    }

    public void setRoleId(String roleId) {
        this.roleId = roleId;
    }

    public int getTemplate() {
        return template;
    }

    public void setTemplate(int template) {
        this.template = template;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public long getLastLoginTime() {
        return lastLoginTime;
    }

    public void setLastLoginTime(long lastLoginTime) {
        this.lastLoginTime = lastLoginTime;
    }

    public JSONObject toJSONObject() {
        JSONObject object = new JSONObject();
        object.put("serverId", getServerId());
        object.put("roleId", getRoleId());
        object.put("template", getTemplate());
        object.put("level",getLevel());
        object.put("name", getName());
        return object;
    }
}
