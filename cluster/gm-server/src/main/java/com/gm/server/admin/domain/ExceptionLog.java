package com.gm.server.admin.domain;

import com.gm.server.common.annotation.Excel;
import com.gm.server.common.core.domain.BaseEntity;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

/**
 * 服务器异常记录对象 exception_log
 * 
 * @author gamer
 * @date 2022-08-01
 */
public class ExceptionLog extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 唯一id */
    private Long id;

    /** 玩家ID */
    @Excel(name = "玩家ID")
    private String playerId;

    /** 服务器id */
    @Excel(name = "服务器id")
    private String serverId;

    /** 网络连接句柄 */
    @Excel(name = "网络连接句柄")
    private String ioSession;

    /** 异常信息 */
    @Excel(name = "异常信息")
    private String exception;

    /** 上行协议ID */
    @Excel(name = "上行协议ID")
    private Integer req;

    /** 下行协议ID */
    @Excel(name = "下行协议ID")
    private Integer resp;

    public void setId(Long id) 
    {
        this.id = id;
    }

    public Long getId() 
    {
        return id;
    }
    public void setPlayerId(String playerId) 
    {
        this.playerId = playerId;
    }

    public String getPlayerId() 
    {
        return playerId;
    }
    public void setServerId(String serverId) 
    {
        this.serverId = serverId;
    }

    public String getServerId() 
    {
        return serverId;
    }
    public void setIoSession(String ioSession) 
    {
        this.ioSession = ioSession;
    }

    public String getIoSession() 
    {
        return ioSession;
    }
    public void setException(String exception) 
    {
        this.exception = exception;
    }

    public String getException() 
    {
        return exception;
    }
    public void setReq(Integer req) 
    {
        this.req = req;
    }

    public Integer getReq() 
    {
        return req;
    }
    public void setResp(Integer resp) 
    {
        this.resp = resp;
    }

    public Integer getResp() 
    {
        return resp;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
            .append("id", getId())
            .append("playerId", getPlayerId())
            .append("serverId", getServerId())
            .append("ioSession", getIoSession())
            .append("exception", getException())
            .append("req", getReq())
            .append("resp", getResp())
            .append("createBy", getCreateBy())
            .append("createTime", getCreateTime())
            .append("updateBy", getUpdateBy())
            .append("updateTime", getUpdateTime())
            .toString();
    }
}
