package com.gm.server.admin.mapper;

import com.gm.server.admin.domain.GmBroadcast;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 游戏管理  跑马灯Mapper接口
 * 
 * @author gamer
 * @date 2022-08-01
 */
public interface GmBroadcastMapper 
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

    public int updateGmBroadcastStatus(@Param("broadcast") GmBroadcast gmBroadcast, @Param("sid") Long[] ids);

    /**
     * 删除游戏管理  跑马灯
     * 
     * @param id 游戏管理  跑马灯主键
     * @return 结果
     */
    public int deleteGmBroadcastById(Long id);

    /**
     * 批量删除游戏管理  跑马灯
     * 
     * @param ids 需要删除的数据主键集合
     * @return 结果
     */
    public int deleteGmBroadcastByIds(Long[] ids);
}
