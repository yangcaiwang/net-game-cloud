package com.ycw.gm.admin.mapper;

import com.ycw.gm.admin.domain.General;

/**
 * 玩家表 数据层
 * 
 * @author gamer
 */
public interface GeneralMapper
{
    /**
     * 通过角色ID查询玩家
     * 
     * @param roleId 玩家ID
     * @return 玩家对象信息
     */
    public General selectGeneralById(Long roleId);

}
