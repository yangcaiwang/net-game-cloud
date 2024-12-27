package com.ycw.gm.admin.domain.vo;

import com.alibaba.fastjson.JSONObject;
import com.ycw.gm.common.annotation.Excel;

/**
 * @author wishcher tree
 * @date 2022/5/7 18:00
 */
public class ItemVo {
    @Excel(name = "道具ID")
    private String resId;

    @Excel(name = "道具数量")
    private String num;

    @Excel(name = "背包索引")
    private String index;
    @Excel(name = "道具等级")
    private String itemLevel;

    @Excel(name = "道具经验")
    private String itemExp;

    @Excel(name = "装备目标ID")
    private String targetId;

    @Excel(name = "装备目标类型")
    private String targetEntityType;

    @Excel(name = "完美度")
    private String perfection;

    public String getResId() {
        return resId;
    }

    public void setResId(String resId) {
        this.resId = resId;
    }

    public String getNum() {
        return num;
    }

    public void setNum(String num) {
        this.num = num;
    }

    public String getIndex() {
        return index;
    }

    public void setIndex(String index) {
        this.index = index;
    }

    public String getItemLevel() {
        return itemLevel;
    }

    public void setItemLevel(String itemLevel) {
        this.itemLevel = itemLevel;
    }

    public String getItemExp() {
        return itemExp;
    }

    public void setItemExp(String itemExp) {
        this.itemExp = itemExp;
    }

    public String getTargetId() {
        return targetId;
    }

    public void setTargetId(String targetId) {
        this.targetId = targetId;
    }

    public String getTargetEntityType() {
        return targetEntityType;
    }

    public void setTargetEntityType(String targetEntityType) {
        this.targetEntityType = targetEntityType;
    }

    public String getPerfection() {
        return perfection;
    }

    public void setPerfection(String perfection) {
        this.perfection = perfection;
    }

    public void toItemVo(JSONObject object) {
        String resId1 = object.getString("resId");
        setResId(resId1);
        String num = object.getString("num");
        setNum(num);
        String index = object.getString("index");
        setIndex(index);
        String itemLevel = object.getString("itemLevel");
        setItemLevel(itemLevel);
        String itemExp = object.getString("itemExp");
        setItemExp(itemExp);
        String targetEntityType = object.getString("targetEntityType");
        setTargetEntityType(targetEntityType);
        String targetId = object.getString("targetId");
        setTargetId(targetId);
        String perfection = object.getString("perfection");
        setPerfection(perfection);
    }
}
