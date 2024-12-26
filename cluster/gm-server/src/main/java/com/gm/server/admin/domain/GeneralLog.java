package com.gm.server.admin.domain;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.gm.server.common.annotation.Excel;
import com.gm.server.common.core.domain.BaseEntity;

import java.util.Date;

/**
 * @author wishcher tree
 * @date 2022/5/9 9:43
 */
public class GeneralLog extends BaseEntity {
    @Excel(name = "记录ID", cellType = Excel.ColumnType.STRING)
    private Long id;
    @Excel(name = "玩家ID", cellType = Excel.ColumnType.STRING)
    private Long roleId;

    @Excel(name = "将领ID")
    private Integer genId;

    @Excel(name = "将领配置ID")
    private Integer genResId;

    @Excel(name = "将领名字")
    private String genName;

    @Excel(name = "操作原因")
    private String reason;

    @Excel(name = "操作原因ID")
    private String reasonId;

    @Excel(name = "操作类型 1=增加 2=删除")
    private Integer optType;

    @Excel(name = "将领参数")
    private String args;

    @Excel(name = "创建时间", dateFormat = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date firstCreateTime;

    public Long getRoleId() {
        return roleId;
    }

    public void setRoleId(Long roleId) {
        this.roleId = roleId;
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

    public Integer getGenId() {
        return genId;
    }

    public void setGenId(Integer genId) {
        this.genId = genId;
    }

    public String getGenName() {
        return genName;
    }

    public void setGenName(String genName) {
        this.genName = genName;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public String getReasonId() {
        return reasonId;
    }

    public void setReasonId(String reasonId) {
        this.reasonId = reasonId;
    }

    public Integer getOptType() {
        return optType;
    }

    public void setOptType(Integer optType) {
        this.optType = optType;
    }

    public String getArgs() {
        return args;
    }

    public void setArgs(String args) {
        this.args = args;
    }

    public Integer getGenResId() {
        return genResId;
    }

    public void setGenResId(Integer genResId) {
        this.genResId = genResId;
    }
}
