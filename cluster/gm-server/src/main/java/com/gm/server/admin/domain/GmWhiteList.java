package com.gm.server.admin.domain;

import com.gm.server.common.annotation.Excel;
import com.gm.server.common.core.domain.BaseEntity;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

/**
 * 白名单对象 gm_white_list
 * 
 * @author gamer
 * @date 2022-07-29
 */
public class GmWhiteList extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 唯一id */
    private Long id;

    /** 白名单类型 */
    @Excel(name = "白名单类型")
    private Integer whiteType;

    /** 平台id */
    @Excel(name = "平台id")
    private Integer platformId;

    /** 白名单值 */
    @Excel(name = "白名单值")
    private String args;

    public void setId(Long id) 
    {
        this.id = id;
    }

    public Long getId() 
    {
        return id;
    }
    public void setWhiteType(Integer whiteType)
    {
        this.whiteType = whiteType;
    }

    public Integer getWhiteType()
    {
        return whiteType;
    }
    public void setPlatformId(Integer platformId)
    {
        this.platformId = platformId;
    }

    public Integer getPlatformId()
    {
        return platformId;
    }
    public void setArgs(String args) 
    {
        this.args = args;
    }

    public String getArgs() 
    {
        return args;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
            .append("id", getId())
            .append("whiteType", getWhiteType())
            .append("platformId", getPlatformId())
            .append("args", getArgs())
            .append("createBy", getCreateBy())
            .append("createTime", getCreateTime())
            .append("updateBy", getUpdateBy())
            .append("updateTime", getUpdateTime())
            .toString();
    }
}
