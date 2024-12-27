package com.ycw.gm.admin.domain;

import com.ycw.gm.common.annotation.Excel;
import com.ycw.gm.common.core.domain.BaseEntity;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import java.util.List;

/**
 * 运营活动开启时间对象 activity_open
 * 
 * @author gamer
 * @date 2022-07-26
 */
public class ActivityOpen extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 唯一id */
    private Long id;

    /** 活动id */
    @Excel(name = "活动id")
    private Integer actId;

    /** 活动类型 */
    @Excel(name = "活动类型")
    private String actType;

    /** 活动时间 */
    @Excel(name = "活动时间")
    private String actTime;

    /** 开服x天不开 */
    @Excel(name = "开服x天不开")
    private Integer notOpenDay;

    /** &gt;0强制结束活动 */
    @Excel(name = "&gt;0强制结束活动")
    private Integer forceEnd;

    /** 开启渠道（|分隔） */
    @Excel(name = "开启渠道", readConverterExp = "|=分隔")
    private String openChannel;

    @Excel(name = "开启时间")
    private Integer openHour;

    /** 状态 */
    @Excel(name = "状态")
    private Integer status;

    /** 服务器id */
    @Excel(name = "服务器id")
    private String serverId;

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
    public void setActId(Integer actId) 
    {
        this.actId = actId;
    }

    public Integer getActId() 
    {
        return actId;
    }
    public void setActType(String actType)
    {
        this.actType = actType;
    }

    public String getActType()
    {
        return actType;
    }
    public void setActTime(String actTime) 
    {
        this.actTime = actTime;
    }

    public String getActTime() 
    {
        return actTime;
    }
    public void setNotOpenDay(Integer notOpenDay) 
    {
        this.notOpenDay = notOpenDay;
    }

    public Integer getNotOpenDay() 
    {
        return notOpenDay;
    }
    public void setForceEnd(Integer forceEnd) 
    {
        this.forceEnd = forceEnd;
    }

    public Integer getForceEnd() 
    {
        return forceEnd;
    }
    public void setOpenChannel(String openChannel) 
    {
        this.openChannel = openChannel;
    }

    public String getOpenChannel() 
    {
        return openChannel;
    }
    public void setStatus(Integer status) 
    {
        this.status = status;
    }

    public Integer getStatus() 
    {
        return status;
    }
    public void setServerId(String serverId) 
    {
        this.serverId = serverId;
    }

    public String getServerId() 
    {
        return serverId;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
            .append("id", getId())
            .append("actId", getActId())
            .append("actType", getActType())
            .append("actTime", getActTime())
            .append("notOpenDay", getNotOpenDay())
            .append("forceEnd", getForceEnd())
            .append("openChannel", getOpenChannel())
            .append("status", getStatus())
            .append("serverId", getServerId())
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

    public Integer getOpenHour() {
        return openHour;
    }

    public void setOpenHour(Integer openHour) {
        this.openHour = openHour;
    }
}
