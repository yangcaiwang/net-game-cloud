package com.gm.server.admin.domain;

import com.gm.server.common.annotation.Excel;
import com.gm.server.common.core.domain.BaseEntity;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

/**
 * 平台表 gm_platform
 * 
 * @author gamer
 */
public class GmPlatform extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    @Excel(name = "平台编号", cellType = Excel.ColumnType.NUMERIC)
    private Long platformId;

    /** 平台名称 */
    @Excel(name = "平台名称")
    private String platformName;

    /** 平台排序 */
    @Excel(name = "平台排序")
    private String sort;

    /** 白名单状态（1正常 0停用） */
    @Excel(name = "白名单状态", readConverterExp = "1=正常,0=停用")
    private String whiteListStatus;

    private Integer autoRegisterSwitch;
    public GmPlatform()
    {

    }
    public String getWhiteListStatus()
    {
        return whiteListStatus;
    }

    public void setWhiteListStatus(String whiteListStatus)
    {
        this.whiteListStatus = whiteListStatus;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
            .append("platformId", getPlatformId())
            .append("platformName", getPlatformName())
            .append("sort", getSort())
            .append("whiteListStatus", getWhiteListStatus())
            .append("createBy", getCreateBy())
            .append("createTime", getCreateTime())
            .append("updateBy", getUpdateBy())
            .append("updateTime", getUpdateTime())
            .append("remark", getRemark())
            .toString();
    }

    public Long getPlatformId() {
        return platformId;
    }

    public void setPlatformId(Long platformId) {
        this.platformId = platformId;
    }

    public String getPlatformName() {
        return platformName;
    }

    public void setPlatformName(String platformName) {
        this.platformName = platformName;
    }

    public String getSort() {
        return sort;
    }

    public void setSort(String sort) {
        this.sort = sort;
    }

    public Integer getAutoRegisterSwitch() {
        return autoRegisterSwitch;
    }

    public void setAutoRegisterSwitch(Integer autoRegisterSwitch) {
        this.autoRegisterSwitch = autoRegisterSwitch;
    }
}
