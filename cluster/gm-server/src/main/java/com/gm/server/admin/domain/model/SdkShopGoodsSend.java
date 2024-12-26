package com.gm.server.admin.domain.model;

import com.alibaba.fastjson.JSONObject;

import java.io.Serializable;

public class SdkShopGoodsSend implements Serializable {
    private long roleId;
    private String userId;
    private int price;
    private int rebate;
    private String cno;
    private String product;
    private long timestamp;

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

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public int getRebate() {
        return rebate;
    }

    public void setRebate(int rebate) {
        this.rebate = rebate;
    }

    public String getCno() {
        return cno;
    }

    public void setCno(String cno) {
        this.cno = cno;
    }

    public String getProduct() {
        return product;
    }

    public void setProduct(String product) {
        this.product = product;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public JSONObject toJSONObject() {
        JSONObject object = new JSONObject();
        object.put("roleId", roleId);
        object.put("userId", userId);
        object.put("price", price);
        object.put("cno", cno);
        object.put("product", product);
        object.put("timestamp", timestamp);
        return object;
    }
}
