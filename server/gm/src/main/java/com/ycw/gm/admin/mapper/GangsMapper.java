package com.ycw.gm.admin.mapper;

import com.ycw.gm.admin.domain.Gangs;

import java.util.List;

/**
 * 玩家表 数据层
 * 
 * @author gamer
 */
public interface GangsMapper
{
    /**
     * 通过角色ID查询玩家
     * 
     * @param gangs 帮派ID
     * @return 玩家对象信息
     */
    public List<Gangs> selectGangsList(Gangs gangs);
    public Gangs selectGangsById(Long gangsId);

    public List<Gangs> selectAllGangs();
}
