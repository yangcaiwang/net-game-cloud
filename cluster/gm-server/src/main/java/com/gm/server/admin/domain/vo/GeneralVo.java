package com.gm.server.admin.domain.vo;

import com.alibaba.fastjson.JSONObject;
import com.gm.server.common.annotation.Excel;


/**
 * @author wishcher tree
 * @date 2022/8/17 18:38
 */
public class GeneralVo {


    @Excel(name = "将领唯一ID")
    private Integer generalId;
    @Excel(name = "将领配置ID")
    private Integer resId;
    @Excel(name = "将领等级")
    private Integer level;
    //将领共鸣前等级
    @Excel(name = "将领共鸣前等级")
    private Integer oldLevel;

    @Excel(name = "将领星级")
    private Integer starLv;

    @Excel(name = "将领星级节点")
    private Integer starNodeId;

    @Excel(name = "将领战力")
    private String combatPower;
    // 是否无双
    @Excel(name = "是否无双")
    private String onlySurmount;

    @Excel(name = "将领属性")
    private String attributeMap;

    @Excel(name = "将领装备")
    private String equipArr;
     // 将领坐骑
    private String generalMount;
    // 统兵等级
    @Excel(name = "将领统兵等级")
    private Integer armsLevel;
    // 战魂装备
    @Excel(name = "将领战魂装备")
    private String soulEquipArr;
//    private ConcurrentHashMap<Integer, Integer> fightSoulEquip = new ConcurrentHashMap<>();

    // 将领天赋
    @Excel(name = "将领天赋")
    private String talents;

    // 槽位,将印
    @Excel(name = "将领将印")
    private String generalSealArr;

    // 创建时间
    @Excel(name = "创建时间")
    private String createTime;

    public Integer getGeneralId() {
        return generalId;
    }

    public void setGeneralId(Integer generalId) {
        this.generalId = generalId;
    }

    public Integer getResId() {
        return resId;
    }

    public void setResId(Integer resId) {
        this.resId = resId;
    }

    public Integer getLevel() {
        return level;
    }

    public void setLevel(Integer level) {
        this.level = level;
    }

    public Integer getOldLevel() {
        return oldLevel;
    }

    public void setOldLevel(Integer oldLevel) {
        this.oldLevel = oldLevel;
    }

    public Integer getStarLv() {
        return starLv;
    }

    public void setStarLv(Integer starLv) {
        this.starLv = starLv;
    }

    public Integer getStarNodeId() {
        return starNodeId;
    }

    public void setStarNodeId(Integer starNodeId) {
        this.starNodeId = starNodeId;
    }

    public String getCombatPower() {
        return combatPower;
    }

    public void setCombatPower(String combatPower) {
        this.combatPower = combatPower;
    }

    public String getOnlySurmount() {
        return onlySurmount;
    }

    public void setOnlySurmount(String onlySurmount) {
        this.onlySurmount = onlySurmount;
    }

    public String getAttributeMap() {
        return attributeMap;
    }

    public void setAttributeMap(String attributeMap) {
        this.attributeMap = attributeMap;
    }

    public String getEquipArr() {
        return equipArr;
    }

    public void setEquipArr(String equipArr) {
        this.equipArr = equipArr;
    }

    public String getGeneralMount() {
        return generalMount;
    }

    public void setGeneralMount(String generalMount) {
        this.generalMount = generalMount;
    }

    public Integer getArmsLevel() {
        return armsLevel;
    }

    public void setArmsLevel(Integer armsLevel) {
        this.armsLevel = armsLevel;
    }

    public String getSoulEquipArr() {
        return soulEquipArr;
    }

    public void setSoulEquipArr(String soulEquipArr) {
        this.soulEquipArr = soulEquipArr;
    }

    public String getTalents() {
        return talents;
    }

    public void setTalents(String talents) {
        this.talents = talents;
    }

    public String getGeneralSealArr() {
        return generalSealArr;
    }

    public void setGeneralSealArr(String generalSealArr) {
        this.generalSealArr = generalSealArr;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    public void toGeneralVo(JSONObject object) {
        Integer generalId1 = object.getInteger("generalId");
        setGeneralId(generalId1);
        Integer resId1 = object.getInteger("resId");
        setResId(resId1);
        int level = object.getIntValue("level");
        setLevel(level);
        int oldLevel = object.getIntValue("oldLevel");
        setOldLevel(oldLevel);
        int starLv = object.getIntValue("starLv");
        setStarLv(starLv);
        int starNodeId = object.getIntValue("starNodeId");
        setStarNodeId(starNodeId);
        String combatPower = object.getString("combatPower");
        setCombatPower(combatPower);
        String onlySurmount = object.getString("onlySurmount");
        setOnlySurmount(onlySurmount);
        String attributeMap = object.getString("attributeMap");
        setAttributeMap(attributeMap);
        String equipArr = object.getString("equipArr");
        setEquipArr(equipArr);
        String generalMount = object.getString("generalMount");
        setGeneralMount(generalMount);
        int armsLevel = object.getIntValue("armsLevel");
        setArmsLevel(armsLevel);
        String soulEquipArr = object.getString("soulEquipArr");
        setSoulEquipArr(soulEquipArr);
        String talents = object.getString("talents");
        setTalents(talents);
        String generalSealArr = object.getString("generalSealArr");
        setGeneralSealArr(generalSealArr);
        String createTime = object.getString("createTime");
        setCreateTime(createTime);
    }
}
