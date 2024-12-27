package com.ycw.gm.admin.domain.model;

import com.alibaba.fastjson.JSONObject;
import com.ycw.gm.common.utils.SerializationUtils;

import java.io.Serializable;

public class SdkRechargeOrder implements Serializable {
    // 玩家id
    private long roleId;
    // 商品配置id
    private String productId;
    // 后台订单日志唯一id
    private long rechargeOrderLogId;
    // 订单号
    private String orderId;
    // 账户
    private String uid;
    // 金额(分)
    private int prices;
    // 创建时间(毫秒)
    private long createTime;
    // 游戏内部的订单号(如果有)
    private String billNo;

    // 渠道ID
    private String channelId;

    // 返利类型 0:无返利 1:狮子/官网充值
    private int rebateType;

    // 礼包ID
    private int giftUid;

    public long getRoleId() {
        return roleId;
    }

    public String getProductId() {
        return productId;
    }

    public long getRechargeOrderLogId() {
        return rechargeOrderLogId;
    }

    public String getOrderId() {
        return orderId;
    }

    public String getUid() {
        return uid;
    }

    public int getPrices() {
        return prices;
    }

    public long getCreateTime() {
        return createTime;
    }

    public String getBillNo() {
        return billNo;
    }

    public void setRoleId(long roleId) {
        this.roleId = roleId;
    }

    public void setProductId(String productId) {
        this.productId = productId;
    }

    public void setRechargeOrderLogId(long rechargeOrderLogId) {
        this.rechargeOrderLogId = rechargeOrderLogId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public void setPrices(int prices) {
        this.prices = prices;
    }

    public void setCreateTime(long createTime) {
        this.createTime = createTime;
    }

    public void setBillNo(String billNo) {
        this.billNo = billNo;
    }

    @Override
    public String toString() {
        return SerializationUtils.beanToJson(this);
    }

    public int getRebateType() {
        return rebateType;
    }

    public void setRebateType(int rebateType) {
        this.rebateType = rebateType;
    }

    public JSONObject toJSONObject() {
        JSONObject object = new JSONObject();
        object.put("orderId", getOrderId());
        object.put("roleId", getRoleId());
        object.put("productId", getProductId());
        object.put("rechargeOrderLogId", getRechargeOrderLogId());
        object.put("uid", getUid());
        object.put("prices", getPrices());
        object.put("createTime", getCreateTime());
        object.put("billNo", getBillNo());
        object.put("channelId", getChannelId());
        object.put("giftId", getGiftUid());
        object.put("rebateType", getRebateType());
        return object;
    }

    public String getChannelId() {
        return channelId;
    }

    public void setChannelId(String channelId) {
        this.channelId = channelId;
    }

    public int getGiftUid() {
        return giftUid;
    }

    public void setGiftUid(int giftUid) {
        this.giftUid = giftUid;
    }
}
