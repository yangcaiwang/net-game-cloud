package com.ycw.gm.admin.domain;

import com.ycw.gm.common.annotation.Excel;
import com.ycw.gm.common.core.domain.BaseEntity;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

/**
 * 帮派表 gangs
 * 
 * @author gamer
 */
public class Gangs extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 帮派ID */
    @Excel(name = "帮派编号", cellType = Excel.ColumnType.STRING)
    private Long gangsId;

    /** 玩家名称 */
    @Excel(name = "帮派名称")
    private String gangsName;

    @Excel(name = "帮主编号", cellType = Excel.ColumnType.NUMERIC)
    private Long gangsAdminId;

    @Excel(name = "帮派等级")
    private String level;

    @Excel(name = "公告内容")
    private String notice;

    @Excel(name = "活跃")
    private String active;

    @Excel(name = "周活跃值")
    private String weekActive;

    @Excel(name = "日活跃值")
    private String dailyActive;

    @Excel(name = "官职信息")
    private String positionMap;

    @Excel(name = "职务列表")
    private String members;
    public Gangs()
    {

    }

    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
                .append("gangsId", getGangsId())
                .append("gangsName", getGangsName())
                .append("gangsAdminId", getGangsAdminId())
                .append("level", getLevel())
                .append("createBy", getCreateBy())
            .append("createTime", getCreateTime())
            .append("updateBy", getUpdateBy())
            .append("updateTime", getUpdateTime())
            .append("remark", getRemark())
            .toString();
    }

    public String getLevel() {
        return level;
    }

    public void setLevel(String level) {
        this.level = level;
    }

    public Long getGangsId() {
        return gangsId;
    }

    public void setGangsId(Long gangsId) {
        this.gangsId = gangsId;
    }

    public String getGangsName() {
        return gangsName;
    }

    public void setGangsName(String gangsName) {
        this.gangsName = gangsName;
    }

    public Long getGangsAdminId() {
        return gangsAdminId;
    }

    public void setGangsAdminId(Long gangsAdminId) {
        this.gangsAdminId = gangsAdminId;
    }

    public String getNotice() {
        return notice;
    }

    public void setNotice(String notice) {
        this.notice = notice;
    }

    public String getActive() {
        return active;
    }

    public void setActive(String active) {
        this.active = active;
    }

    public String getWeekActive() {
        return weekActive;
    }

    public void setWeekActive(String weekActive) {
        this.weekActive = weekActive;
    }

    public String getDailyActive() {
        return dailyActive;
    }

    public void setDailyActive(String dailyActive) {
        this.dailyActive = dailyActive;
    }

    public String getPositionMap() {
        return positionMap;
    }

    public void setPositionMap(String positionMap) {
        this.positionMap = positionMap;
    }

    public String getMembers() {
        return members;
    }

    public void setMembers(String members) {
        this.members = members;
    }
}
