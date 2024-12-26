package com.gm.server.admin.service;

import com.gm.server.admin.domain.GmBroadcast;

import java.util.List;

/**
 * 游戏管理  跑马灯Service接口
 * 
 * @author gamer
 * @date 2022-08-01
 */
public interface IGmBroadcastService 
{
    /**
     * 查询游戏管理  跑马灯
     * 
     * @param id 游戏管理  跑马灯主键
     * @return 游戏管理  跑马灯
     */
    public GmBroadcast selectGmBroadcastById(Long id);

    /**
     * 查询游戏管理  跑马灯列表
     * 
     * @param gmBroadcast 游戏管理  跑马灯
     * @return 游戏管理  跑马灯集合
     */
    public List<GmBroadcast> selectGmBroadcastList(GmBroadcast gmBroadcast);

    public List<GmBroadcast> selectGmBroadcastByIds(Long[] sids);

    /**
     * 新增游戏管理  跑马灯
     * 
     * @param gmBroadcast 游戏管理  跑马灯
     * @return 结果
     */
    public int insertGmBroadcast(GmBroadcast gmBroadcast);

    /**
     * 修改游戏管理  跑马灯
     * 
     * @param gmBroadcast 游戏管理  跑马灯
     * @return 结果
     */
    public int updateGmBroadcast(GmBroadcast gmBroadcast);

    public int updateGmBroadcastStatus(GmBroadcast gmBroadcast, Long[] ids);

    /**
     * 批量删除游戏管理  跑马灯
     * 
     * @param ids 需要删除的游戏管理  跑马灯主键集合
     * @return 结果
     */
    public int deleteGmBroadcastByIds(Long[] ids);

    /**
     * 删除游戏管理  跑马灯信息
     * 
     * @param id 游戏管理  跑马灯主键
     * @return 结果
     */
    public int deleteGmBroadcastById(Long id);
}
