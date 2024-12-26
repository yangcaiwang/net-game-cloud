package com.gm.server.admin.domain;

import com.gm.server.common.annotation.Excel;
import com.gm.server.common.core.domain.BaseEntity;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import java.util.List;

/**
 * 游戏管理  跑马灯对象 gm_broadcast
 * 
 * @author gamer
 * @date 2022-08-01
 */
public class GmBroadcast extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 唯一id */
    private Long id;

    /** 服务器 */
    @Excel(name = "服务器")
    private String serverId;

    /** 间隔时间 */
    @Excel(name = "间隔时间")
    private Integer interval;

    /** 广播次数 */
    @Excel(name = "广播次数")
    private Integer times;

    /** 广播内容 */
    @Excel(name = "广播内容")
    private String bcContent;

    /** 邮件状态 */
    @Excel(name = "状态")
    private Integer status;

    private List<String> servers;
    private String allServer;

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

    public void setInterval(Integer interval)
    {
        this.interval = interval;
    }

    public Integer getInterval() 
    {
        return interval;
    }
    public void setTimes(Integer times) 
    {
        this.times = times;
    }

    public Integer getTimes() 
    {
        return times;
    }
    public void setBcContent(String bcContent) 
    {
        this.bcContent = bcContent;
    }

    public String getBcContent() 
    {
        return bcContent;
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
            .append("interval", getInterval())
            .append("times", getTimes())
            .append("bcContent", getBcContent())
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
}
