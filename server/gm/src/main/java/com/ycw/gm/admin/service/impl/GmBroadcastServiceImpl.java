package com.ycw.gm.admin.service.impl;

import com.ycw.gm.admin.domain.GmBroadcast;
import com.ycw.gm.admin.mapper.GmBroadcastMapper;
import com.ycw.gm.admin.service.IGmBroadcastService;
import com.ycw.gm.common.utils.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 游戏管理  跑马灯Service业务层处理
 * 
 * @author gamer
 * @date 2022-08-01
 */
@Service
public class GmBroadcastServiceImpl implements IGmBroadcastService
{
    @Autowired
    private GmBroadcastMapper gmBroadcastMapper;

    /**
     * 查询游戏管理  跑马灯
     * 
     * @param id 游戏管理  跑马灯主键
     * @return 游戏管理  跑马灯
     */
    @Override
    public GmBroadcast selectGmBroadcastById(Long id)
    {
        return gmBroadcastMapper.selectGmBroadcastById(id);
    }

    /**
     * 查询游戏管理  跑马灯列表
     * 
     * @param gmBroadcast 游戏管理  跑马灯
     * @return 游戏管理  跑马灯
     */
    @Override
    public List<GmBroadcast> selectGmBroadcastList(GmBroadcast gmBroadcast)
    {
        return gmBroadcastMapper.selectGmBroadcastList(gmBroadcast);
    }

    @Override
    public List<GmBroadcast> selectGmBroadcastByIds(Long[] sids) {
        return gmBroadcastMapper.selectGmBroadcastByIds(sids);
    }

    /**
     * 新增游戏管理  跑马灯
     * 
     * @param gmBroadcast 游戏管理  跑马灯
     * @return 结果
     */
    @Override
    public int insertGmBroadcast(GmBroadcast gmBroadcast)
    {
        gmBroadcast.setCreateTime(DateUtils.getNowDate());
        return gmBroadcastMapper.insertGmBroadcast(gmBroadcast);
    }

    /**
     * 修改游戏管理  跑马灯
     * 
     * @param gmBroadcast 游戏管理  跑马灯
     * @return 结果
     */
    @Override
    public int updateGmBroadcast(GmBroadcast gmBroadcast)
    {
        gmBroadcast.setUpdateTime(DateUtils.getNowDate());
        return gmBroadcastMapper.updateGmBroadcast(gmBroadcast);
    }

    @Override
    public int updateGmBroadcastStatus(GmBroadcast gmBroadcast, Long[] ids) {
        return gmBroadcastMapper.updateGmBroadcastStatus(gmBroadcast, ids);
    }

    /**
     * 批量删除游戏管理  跑马灯
     * 
     * @param ids 需要删除的游戏管理  跑马灯主键
     * @return 结果
     */
    @Override
    public int deleteGmBroadcastByIds(Long[] ids)
    {
        return gmBroadcastMapper.deleteGmBroadcastByIds(ids);
    }

    /**
     * 删除游戏管理  跑马灯信息
     * 
     * @param id 游戏管理  跑马灯主键
     * @return 结果
     */
    @Override
    public int deleteGmBroadcastById(Long id)
    {
        return gmBroadcastMapper.deleteGmBroadcastById(id);
    }
}
