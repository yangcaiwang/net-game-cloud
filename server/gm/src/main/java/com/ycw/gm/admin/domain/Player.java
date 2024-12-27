package com.ycw.gm.admin.domain;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.ycw.gm.common.annotation.Excel;
import com.ycw.gm.common.core.domain.BaseEntity;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import java.util.Date;

/**
 * 玩家表 player
 * 
 * @author gamer
 */
public class Player extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 玩家ID */
    @Excel(name = "玩家编号", cellType = Excel.ColumnType.STRING)
    private Long roleId;

    /** 玩家名称 */
    @Excel(name = "玩家名称")
    private String roleName;

    @Excel(name = "玩家账号")
    private String account;

    @Excel(name = "渠道")
    private String channelId;

    @Excel(name = "玩家等级")
    private String level;

    @Excel(name = "玩家经验")
    private String exp;
    private String headResId;
    private String titleLvResId;

    @Excel(name = "玩家战力")
    private String power;

    @Excel(name = "是否删除", readConverterExp = "1=删除,0=正常")
    private String delFlag;

    /** 是否在线 */
    @Excel(name = "是否在线", readConverterExp = "1=在线,0=离线")
    private String online;

    @Excel(name = "创建时间", dateFormat = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date firstCreateTime;

    @Excel(name = "最近登录时间", dateFormat = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date loginTime;
    public Player()
    {

    }

    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
                .append("roleId", getRoleId())
                .append("roleName", getRoleName())
                .append("account", getAccount())
                .append("channelId", getChannelId())
                .append("level", getLevel())
                .append("exp", getExp())
                .append("headResId", getHeadResId())
                .append("online", getOnline())
                .append("power", getPower())
                .append("delFlag", getDelFlag())
                .append("firstCreateTime", getFirstCreateTime())
            .append("createTime", getCreateTime())
            .append("updateBy", getUpdateBy())
            .append("updateTime", getUpdateTime())
            .append("remark", getRemark())
            .toString();
    }

    public Long getRoleId() {
        return roleId;
    }

    public void setRoleId(Long roleId) {
        this.roleId = roleId;
    }

    public String getRoleName() {
        return roleName;
    }

    public void setRoleName(String roleName) {
        this.roleName = roleName;
    }

    public String getOnline() {
        return online;
    }

    public void setOnline(String online) {
        this.online = online;
    }

    public String getAccount() {
        return account;
    }

    public void setAccount(String account) {
        this.account = account;
    }

    public String getLevel() {
        return level;
    }

    public void setLevel(String level) {
        this.level = level;
    }

    public String getExp() {
        return exp;
    }

    public void setExp(String exp) {
        this.exp = exp;
    }

    public String getHeadResId() {
        return headResId;
    }

    public void setHeadResId(String headResId) {
        this.headResId = headResId;
    }

    public String getChannelId() {
        return channelId;
    }

    public void setChannelId(String channelId) {
        this.channelId = channelId;
    }

    public String getPower() {
        return power;
    }

    public void setPower(String power) {
        this.power = power;
    }

    public String getDelFlag() {
        return delFlag;
    }

    public void setDelFlag(String delFlag) {
        this.delFlag = delFlag;
    }

    public String getTitleLvResId() {
        return titleLvResId;
    }

    public void setTitleLvResId(String titleLvResId) {
        this.titleLvResId = titleLvResId;
    }

    public Date getFirstCreateTime() {
        return firstCreateTime;
    }

    public void setFirstCreateTime(Date firstCreateTime) {
        this.firstCreateTime = firstCreateTime;
    }

    public Date getLoginTime() {
        return loginTime;
    }

    public void setLoginTime(Date loginTime) {
        this.loginTime = loginTime;
    }
}
