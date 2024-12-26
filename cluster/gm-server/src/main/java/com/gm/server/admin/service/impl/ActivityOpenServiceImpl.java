package com.gm.server.admin.service.impl;

import com.gm.server.common.utils.DateUtils;
import com.gm.server.admin.domain.ActivityOpen;
import com.gm.server.admin.mapper.ActivityOpenMapper;
import com.gm.server.admin.service.IActivityOpenService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 运营活动开启时间Service业务层处理
 * 
 * @author gamer
 * @date 2022-07-26
 */
@Service
public class ActivityOpenServiceImpl implements IActivityOpenService
{
    @Autowired
    private ActivityOpenMapper activityOpenMapper;

    /**
     * 查询运营活动开启时间
     * 
     * @param id 运营活动开启时间主键
     * @return 运营活动开启时间
     */
    @Override
    public ActivityOpen selectActivityOpenById(Long id)
    {
        return activityOpenMapper.selectActivityOpenById(id);
    }

    /**
     * 查询运营活动开启时间列表
     * 
     * @param activityOpen 运营活动开启时间
     * @return 运营活动开启时间
     */
    @Override
    public List<ActivityOpen> selectActivityOpenList(ActivityOpen activityOpen)
    {
        return activityOpenMapper.selectActivityOpenList(activityOpen);
    }

    /**
     * 新增运营活动开启时间
     * 
     * @param activityOpen 运营活动开启时间
     * @return 结果
     */
    @Override
    public int insertActivityOpen(ActivityOpen activityOpen)
    {
        activityOpen.setCreateTime(DateUtils.getNowDate());
        return activityOpenMapper.insertActivityOpen(activityOpen);
    }

    /**
     * 修改运营活动开启时间
     * 
     * @param activityOpen 运营活动开启时间
     * @return 结果
     */
    @Override
    public int updateActivityOpen(ActivityOpen activityOpen)
    {
        activityOpen.setUpdateTime(DateUtils.getNowDate());
        return activityOpenMapper.updateActivityOpen(activityOpen);
    }

    /**
     * 批量删除运营活动开启时间
     * 
     * @param ids 需要删除的运营活动开启时间主键
     * @return 结果
     */
    @Override
    public int deleteActivityOpenByIds(Long[] ids)
    {
        return activityOpenMapper.deleteActivityOpenByIds(ids);
    }

    /**
     * 删除运营活动开启时间信息
     * 
     * @param id 运营活动开启时间主键
     * @return 结果
     */
    @Override
    public int deleteActivityOpenById(Long id)
    {
        return activityOpenMapper.deleteActivityOpenById(id);
    }
}
