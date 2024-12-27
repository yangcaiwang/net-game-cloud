package com.ycw.gm.admin.domain;

import com.ycw.gm.common.annotation.Excel;
import com.ycw.gm.common.core.domain.BaseEntity;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

/**
 * gm命令行对象 gm_execute_command
 * 
 * @author gamer
 * @date 2022-08-01
 */
public class GmExecuteCommand extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 唯一ID */
    private Long id;

    /** 玩家ID */
    @Excel(name = "玩家ID")
    private Long roleId;

    /** 执行命令 */
    @Excel(name = "执行命令")
    private String command;

    /** 状态 */
    @Excel(name = "状态")
    private Integer status;

    private Integer serverId;

    public void setId(Long id) 
    {
        this.id = id;
    }

    public Long getId() 
    {
        return id;
    }
    public void setRoleId(Long roleId) 
    {
        this.roleId = roleId;
    }

    public Long getRoleId() 
    {
        return roleId;
    }
    public void setCommand(String command) 
    {
        this.command = command;
    }

    public String getCommand() 
    {
        return command;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
            .append("id", getId())
            .append("roleId", getRoleId())
            .append("command", getCommand())
                .append("status", getStatus())
                .append("serverId", getServerId())
            .append("createBy", getCreateBy())
            .append("createTime", getCreateTime())
            .append("updateBy", getUpdateBy())
            .append("updateTime", getUpdateTime())
            .toString();
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Integer getStatus()
    {
        return status;
    }

    public Integer getServerId() {
        return serverId;
    }

    public void setServerId(Integer serverId) {
        this.serverId = serverId;
    }
}
