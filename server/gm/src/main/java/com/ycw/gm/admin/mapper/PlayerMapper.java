package com.ycw.gm.admin.mapper;

import com.ycw.gm.admin.domain.Player;
import com.ycw.gm.admin.domain.PlayerBase;

import java.util.List;

/**
 * 玩家表 数据层
 * 
 * @author gamer
 */
public interface PlayerMapper
{
    /**
     * 根据条件分页查询玩家数据
     * 
     * @param player 玩家信息
     * @return 玩家数据集合信息
     */
    public List<Player> selectPlayerList(Player player);

    /**
     * 查询所有玩家
     * 
     * @return 玩家列表
     */
    public List<Player> selectPlayerAll();

    /**
     * 通过角色ID查询玩家
     * 
     * @param playerId 玩家ID
     * @return 玩家对象信息
     */
    public Player selectPlayerById(Long playerId);

    /**
     * 通过角色ID查询玩家基本信息
     *
     * @param playerId 玩家ID
     * @return 玩家对象信息
     */
    public PlayerBase selectPlayerBaseById(Long playerId);

}
