package com.ycw.gm.admin.service;

import com.ycw.gm.admin.domain.*;
import com.ycw.gm.admin.domain.vo.GeneralVo;
import com.ycw.gm.admin.domain.vo.ItemVo;

import java.util.List;

/**
 * 角色业务层
 * 
 * @author gamer
 */
public interface IPlayerService
{
    /**
     * 根据条件分页查询角色数据
     * 
     * @param player 玩家信息
     * @return 玩家数据集合信息
     */
    public List<Player> selectPlayerList(Player player);

    /**
     * 查询所有角色
     * 
     * @return 玩家列表
     */
    public List<Player> selectPlayerAll();

    /**
     * 通过角色ID查询角色
     * 
     * @param playerId 玩家ID
     * @return 玩家对象信息
     */
    public Player selectPlayerById(Long playerId);

    /**
     * 通过玩家ID查询玩家基本信息
     * @param playerId
     * @return
     */
    public PlayerBase selectPlayerBaseById(Long playerId);

    /**
     * 通过角色ID查询角色
     *
     * @param roleId 玩家ID
     * @return 玩家对象信息
     */
    public List<GeneralVo> selectGeneralById(Long roleId, boolean all);
    int countGeneralVo(Long roleId);

    /**
     * 通过角色ID查询角色
     *
     * @param roleId 玩家ID
     * @return 玩家对象信息
     */
    public List<ItemVo> selectItemById(Long roleId);

    public List<ItemVo> selectItemByIdAll(Long roleId);

    public int countItem(Long roleId);

    int countItemLogNum(String dbName, Long roleId, ItemLog itemLog);
    int countPowerLogNum(String dbName, Long roleId, PowerLog powerLog);

    int countLoginLogNum(String dbName, LoginLog log);
    int countGeneralLogNum(String dbName, GeneralLog log);
    public List<ItemLog> selectItemLogById(String dbName, Long roleId, ItemLog itemLog, boolean all);
    List<PowerLog> selectPowerLogById(String dbName, Long roleId, PowerLog powerLog, boolean all);
    List<LoginLog> selectLoginLogById(String dbName, LoginLog log, boolean all);
    List<GeneralLog> selectGeneralLogById(String dbName, GeneralLog log, boolean all);
    public List<PlayerMail> selectPlayerMailById(Long playerId, PlayerMail playerMail);
}
