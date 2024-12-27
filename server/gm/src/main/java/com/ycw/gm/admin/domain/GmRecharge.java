package com.ycw.gm.admin.domain;

import com.ycw.gm.common.annotation.Excel;
import com.ycw.gm.common.core.domain.BaseEntity;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

/**
 * GM充值对象 gm_recharge
 * 
 * @author gamer
 * @date 2022-08-02
 */
public class GmRecharge extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 唯一ID */
    private Long id;

    /** 玩家 */
    @Excel(name = "玩家")
    private String roleIds;

    /** 充值档次 */
    @Excel(name = "充值档次")
    private String rechargeIds;

    /** 服务器ID */
    @Excel(name = "服务器ID")
    private Integer serverId;

    /** 状态 */
    @Excel(name = "状态")
    private Integer status;

    public void setId(Long id) 
    {
        this.id = id;
    }

    public Long getId() 
    {
        return id;
    }
    public void setRoleIds(String roleIds) 
    {
        this.roleIds = roleIds;
    }

    public String getRoleIds() 
    {
        return roleIds;
    }
    public void setRechargeIds(String rechargeIds) 
    {
        this.rechargeIds = rechargeIds;
    }

    public String getRechargeIds() 
    {
        return rechargeIds;
    }
    public void setServerId(Integer serverId) 
    {
        this.serverId = serverId;
    }

    public Integer getServerId() 
    {
        return serverId;
    }
    public void setStatus(Integer status) 
    {
        this.status = status;
    }

    public Integer getStatus() 
    {
        return status;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
            .append("id", getId())
            .append("roleIds", getRoleIds())
            .append("rechargeIds", getRechargeIds())
            .append("serverId", getServerId())
            .append("status", getStatus())
            .append("createBy", getCreateBy())
            .append("createTime", getCreateTime())
            .append("updateBy", getUpdateBy())
            .append("updateTime", getUpdateTime())
            .toString();
    }
}
