package com.ycw.gm.admin.mapper;

import com.ycw.gm.admin.domain.Item;

/**
 * 玩家表 数据层
 * 
 * @author gamer
 */
public interface ItemMapper
{
    /**
     * 通过角色ID查询玩家
     * 
     * @param roleId 玩家ID
     * @return 玩家对象信息
     */
    public Item selectItemById(Long roleId);

}
