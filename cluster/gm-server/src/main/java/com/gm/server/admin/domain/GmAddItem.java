package com.gm.server.admin.domain;

import com.gm.server.admin.domain.model.ItemPair;
import com.gm.server.common.annotation.Excel;
import com.gm.server.common.core.domain.BaseEntity;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import java.util.List;

/**
 * 游戏管理  添加道具对象 gm_add_item
 * 
 * @author gamer
 * @date 2022-07-26
 */
public class GmAddItem extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 唯一id */
    private Long id;

    /** 服务器 */
    @Excel(name = "服务器")
    private String serverId;


    /** 玩家 */
    @Excel(name = "玩家")
    private String roleIds;

    /** 道具 */
    @Excel(name = "道具")
    private String item;


    /** 状态 */
    @Excel(name = "状态")
    private Integer status;

    private List<String> servers;
    private String allServer;
    private List<ItemPair> dynamicItem;

    public void setId(Long id) 
    {
        this.id = id;
    }

    public Long getId() 
    {
        return id;
    }
    public void setServerId(String serverId) 
    {
        this.serverId = serverId;
    }

    public String getServerId()
    {
        return serverId;
    }

    public void setRoleIds(String roleIds)
    {
        this.roleIds = roleIds;
    }

    public String getRoleIds() 
    {
        return roleIds;
    }
    public void setItem(String item) 
    {
        this.item = item;
    }

    public String getItem() 
    {
        return item;
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
            .append("serverId", getServerId())
            .append("roleIds", getRoleIds())
            .append("item", getItem())
            .append("status", getStatus())
            .append("createBy", getCreateBy())
            .append("createTime", getCreateTime())
            .append("updateBy", getUpdateBy())
            .append("updateTime", getUpdateTime())
            .toString();
    }

    public List<String> getServers() {
        return servers;
    }

    public void setServers(List<String> servers) {
        this.servers = servers;
    }

    public String getAllServer() {
        return allServer;
    }

    public void setAllServer(String allServer) {
        this.allServer = allServer;
    }

    public List<ItemPair> getDynamicItem() {
        return dynamicItem;
    }

    public void setDynamicItem(List<ItemPair> dynamicItem) {
        this.dynamicItem = dynamicItem;
    }
}
