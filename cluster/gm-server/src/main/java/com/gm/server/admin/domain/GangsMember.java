package com.gm.server.admin.domain;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.gm.server.common.annotation.Excel;
import com.gm.server.common.core.domain.BaseEntity;

import java.util.Date;

/**
 * @author wishcher tree
 * @date 2022/5/9 16:21
 */
public class GangsMember extends BaseEntity {
    private static final long serialVersionUID = 1L;

    @Excel(name = "玩家ID", cellType = Excel.ColumnType.STRING)
    private Long roleId;
    @Excel(name = "帮贡")
    private String donate;

    @Excel(name = "军团技能")
    private String skills;

    @Excel(name = "活跃数据")
    private String legionActiveData;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date joinLegionTime;

    public Long getRoleId() {
        return roleId;
    }

    public void setRoleId(Long roleId) {
        this.roleId = roleId;
    }

    public String getDonate() {
        return donate;
    }

    public void setDonate(String donate) {
        this.donate = donate;
    }

    public String getSkills() {
        return skills;
    }

    public void setSkills(String skills) {
        this.skills = skills;
    }

    public Date getJoinLegionTime() {
        return joinLegionTime;
    }

    public void setJoinLegionTime(Date joinLegionTime) {
        this.joinLegionTime = joinLegionTime;
    }

    public String getLegionActiveData() {
        return legionActiveData;
    }

    public void setLegionActiveData(String legionActiveData) {
        this.legionActiveData = legionActiveData;
    }
}
