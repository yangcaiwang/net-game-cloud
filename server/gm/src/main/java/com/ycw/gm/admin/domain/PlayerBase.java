package com.ycw.gm.admin.domain;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.ycw.gm.common.core.domain.BaseEntity;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import java.util.Date;

public class PlayerBase extends BaseEntity {
    private Long gangsId;
    private String gangsName;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date loginTime;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date logoutTime;

    private String registerIp;
    private String lastLoginIp;

    private String registerAddress;
    private String lastLoginAddress;
    private String gold;
    private String copper;

    private String sumGold;
    private String costGold;

    private String vipLevel;
    private String vipExp;

    private Integer onlineTime;

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.MULTI_LINE_STYLE)
                .append("gangsId", getGangsId())
                .append("gangsName", getGangsName())
                .append("loginTime", getLoginTime())
                .append("logoutTime", getLogoutTime())
                .append("registerIp", getRegisterIp())
                .append("lastLoginIp", getLastLoginIp())
                .append("gold", getGold())
                .append("copper", getCopper())
                .append("sumGold", getSumGold())
                .append("costGold", getCostGold())
                .append("vipLevel", getVipLevel())
                .append("vipExp", getVipExp())
                .append("onlineTime", getOnlineTime())
                .toString();
    }

    public Long getGangsId() {
        return gangsId;
    }

    public void setGangsId(Long gangsId) {
        this.gangsId = gangsId;
    }

    public String getGangsName() {
        return gangsName;
    }

    public void setGangsName(String gangsName) {
        this.gangsName = gangsName;
    }

    public Date getLoginTime() {
        return loginTime;
    }

    public void setLoginTime(Date loginTime) {
        this.loginTime = loginTime;
    }

    public Date getLogoutTime() {
        return logoutTime;
    }

    public void setLogoutTime(Date logoutTime) {
        this.logoutTime = logoutTime;
    }

    public String getRegisterIp() {
        return registerIp;
    }

    public void setRegisterIp(String registerIp) {
        this.registerIp = registerIp;
    }

    public String getLastLoginIp() {
        return lastLoginIp;
    }

    public void setLastLoginIp(String lastLoginIp) {
        this.lastLoginIp = lastLoginIp;
    }

    public String getGold() {
        return gold;
    }

    public void setGold(String gold) {
        this.gold = gold;
    }

    public String getCopper() {
        return copper;
    }

    public void setCopper(String copper) {
        this.copper = copper;
    }

    public String getSumGold() {
        return sumGold;
    }

    public void setSumGold(String sumGold) {
        this.sumGold = sumGold;
    }

    public String getCostGold() {
        return costGold;
    }

    public void setCostGold(String costGold) {
        this.costGold = costGold;
    }

    public String getVipLevel() {
        return vipLevel;
    }

    public void setVipLevel(String vipLevel) {
        this.vipLevel = vipLevel;
    }

    public String getVipExp() {
        return vipExp;
    }

    public void setVipExp(String vipExp) {
        this.vipExp = vipExp;
    }

    public Integer getOnlineTime() {
        return onlineTime;
    }

    public void setOnlineTime(Integer onlineTime) {
        this.onlineTime = onlineTime;
    }

    public String getRegisterAddress() {
        return registerAddress;
    }

    public void setRegisterAddress(String registerAddress) {
        this.registerAddress = registerAddress;
    }

    public String getLastLoginAddress() {
        return lastLoginAddress;
    }

    public void setLastLoginAddress(String lastLoginAddress) {
        this.lastLoginAddress = lastLoginAddress;
    }
}
