package com.gm.server.admin.service;

import com.gm.server.admin.domain.Gangs;
import com.gm.server.admin.domain.GangsMember;

import java.util.List;

/**
 * 角色业务层
 * 
 * @author gamer
 */
public interface IGangsService
{
    /**
     * 根据条件分页查询角色数据
     * 
     * @param gangs 帮派信息
     * @return 玩家数据集合信息
     */
    public List<Gangs> selectGangsList(Gangs gangs);

    /**
     * 查询所有角色
     * 
     * @return 玩家列表
     */
    public List<Gangs> selectGangsAll();

    /**
     *
     * @param gangsId 帮派ID
     * @return
     */
    public Gangs selectGangsById(Long gangsId);

    /**
     * 根据玩家查询玩家帮派信息
     * @param playerId
     * @return
     */
    public GangsMember selectGangsMemberById(Long playerId);

    List<GangsMember> selectGangsMemberByIds(List<Long>  playerIds);

}
