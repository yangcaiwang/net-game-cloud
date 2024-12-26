package com.gm.server.admin.domain;

import com.gm.server.common.annotation.Excel;
import com.gm.server.common.core.domain.BaseEntity;

/**
 * @author wishcher tree
 * @date 2022/5/7 15:41
 */
public class General extends BaseEntity {

    @Excel(name = "玩家ID", cellType = Excel.ColumnType.STRING)
    private long roleId;

    @Excel(name = "将领")
    private String generalMap;

    public long getRoleId() {
        return roleId;
    }

    public void setRoleId(long roleId) {
        this.roleId = roleId;
    }

    public String getGeneralMap() {
        return generalMap;
    }

    public void setGeneralMap(String generalMap) {
        this.generalMap = generalMap;
    }
}
