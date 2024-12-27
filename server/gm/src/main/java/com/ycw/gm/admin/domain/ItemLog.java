package com.ycw.gm.admin.domain;

import com.ycw.gm.common.annotation.Excel;
import com.ycw.gm.common.core.domain.BaseEntity;

/**
 * @author wishcher tree
 * @date 2022/5/9 9:43
 */
public class ItemLog extends BaseEntity {
    private Long id;
    @Excel(name = "玩家ID", cellType = Excel.ColumnType.STRING)
    private Long identityId;
    @Excel(name = "玩家名")
    private String roleName;
    @Excel(name = "道具来源")
    private String source;
    @Excel(name = "道具分组类型")
    private String group;
    @Excel(name = "背包索引")
    private String itemId;
    @Excel(name = "道具ID")
    private String resId;
    @Excel(name = "操作数量")
    private String quantity;
    @Excel(name = "操作前数量")
    private String before;
    @Excel(name = "操作后数量")
    private String current;
    @Excel(name = "操作描述")
    private String description;

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getGroup() {
        return group;
    }

    public void setGroup(String group) {
        this.group = group;
    }

    public String getItemId() {
        return itemId;
    }

    public void setItemId(String itemId) {
        this.itemId = itemId;
    }

    public String getResId() {
        return resId;
    }

    public void setResId(String resId) {
        this.resId = resId;
    }

    public String getQuantity() {
        return quantity;
    }

    public void setQuantity(String quantity) {
        this.quantity = quantity;
    }

    public String getBefore() {
        return before;
    }

    public void setBefore(String before) {
        this.before = before;
    }

    public String getCurrent() {
        return current;
    }

    public void setCurrent(String current) {
        this.current = current;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Long getIdentityId() {
        return identityId;
    }

    public void setIdentityId(Long identityId) {
        this.identityId = identityId;
    }

    public String getRoleName() {
        return roleName;
    }

    public void setRoleName(String roleName) {
        this.roleName = roleName;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
}
