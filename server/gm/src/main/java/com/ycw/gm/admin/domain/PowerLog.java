package com.ycw.gm.admin.domain;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.ycw.gm.common.annotation.Excel;
import com.ycw.gm.common.core.domain.BaseEntity;

import java.util.Date;

/**
 * @author wishcher tree
 * @date 2022/5/9 9:43
 */
public class PowerLog extends BaseEntity {
    @Excel(name = "记录ID", cellType = Excel.ColumnType.STRING)
    private Long id;
    @Excel(name = "玩家ID", cellType = Excel.ColumnType.STRING)
    private Long roleId;

    @Excel(name = "变化前战力")
    private String prePower;
    @Excel(name = "战力")
    private String power;
    @Excel(name = "战力变化原因")
    private String powerChangeReason;
    @Excel(name = "战力变化描述")
    private String powerDesc;

    @Excel(name = "创建时间", dateFormat = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date firstCreateTime;

    public Long getRoleId() {
        return roleId;
    }

    public void setRoleId(Long roleId) {
        this.roleId = roleId;
    }

    public String getPower() {
        return power;
    }

    public void setPower(String power) {
        this.power = power;
    }

    public String getPowerChangeReason() {
        return powerChangeReason;
    }

    public void setPowerChangeReason(String powerChangeReason) {
        this.powerChangeReason = powerChangeReason;
    }

    public String getPrePower() {
        return prePower;
    }

    public void setPrePower(String prePower) {
        this.prePower = prePower;
    }

    public String getPowerDesc() {
        return powerDesc;
    }

    public void setPowerDesc(String powerDesc) {
        this.powerDesc = powerDesc;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Date getFirstCreateTime() {
        return firstCreateTime;
    }

    public void setFirstCreateTime(Date firstCreateTime) {
        this.firstCreateTime = firstCreateTime;
    }
}
