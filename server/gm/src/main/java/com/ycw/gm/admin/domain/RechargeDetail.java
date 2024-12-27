package com.ycw.gm.admin.domain;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.ycw.gm.common.annotation.Excel;
import com.ycw.gm.common.core.domain.BaseEntity;

import java.util.Date;

/**
 * @author wishcher tree
 * @date 2022/8/3 15:54
 */
public class RechargeDetail extends BaseEntity {
    @Excel(name = "玩家ID", cellType = Excel.ColumnType.STRING)
    private Long roleId;

    @Excel(name = "充值配置ID")
    private Integer payId;
    @Excel(name = "充值类型")
    private Integer payType;

    @Excel(name = "充值金额")
    private Integer payMoney;
    @Excel(name = "充值后台ID")
    private String orderLogId;

    @Excel(name = "SDK订单ID")
    private String sdkOrder;

    @Excel(name = "创建时间", dateFormat = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date firstCreateTime;

    public Long getRoleId() {
        return roleId;
    }

    public void setRoleId(Long roleId) {
        this.roleId = roleId;
    }

    public Integer getPayId() {
        return payId;
    }

    public void setPayId(Integer payId) {
        this.payId = payId;
    }

    public Integer getPayType() {
        return payType;
    }

    public void setPayType(Integer payType) {
        this.payType = payType;
    }

    public Integer getPayMoney() {
        return payMoney;
    }

    public void setPayMoney(Integer payMoney) {
        this.payMoney = payMoney;
    }

    public String getOrderLogId() {
        return orderLogId;
    }

    public void setOrderLogId(String orderLogId) {
        this.orderLogId = orderLogId;
    }

    public String getSdkOrder() {
        return sdkOrder;
    }

    public void setSdkOrder(String sdkOrder) {
        this.sdkOrder = sdkOrder;
    }

    public Date getFirstCreateTime() {
        return firstCreateTime;
    }

    public void setFirstCreateTime(Date firstCreateTime) {
        this.firstCreateTime = firstCreateTime;
    }
}
