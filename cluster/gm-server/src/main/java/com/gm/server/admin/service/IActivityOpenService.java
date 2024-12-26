package com.gm.server.admin.service;

import com.gm.server.admin.domain.ActivityOpen;

import java.util.List;

/**
 * 运营活动开启时间Service接口
 * 
 * @author gamer
 * @date 2022-07-26
 */
public interface IActivityOpenService 
{
    /**
     * 查询运营活动开启时间
     * 
     * @param id 运营活动开启时间主键
     * @return 运营活动开启时间
     */
    public ActivityOpen selectActivityOpenById(Long id);

    /**
     * 查询运营活动开启时间列表
     * 
     * @param activityOpen 运营活动开启时间
     * @return 运营活动开启时间集合
     */
    public List<ActivityOpen> selectActivityOpenList(ActivityOpen activityOpen);

    /**
     * 新增运营活动开启时间
     * 
     * @param activityOpen 运营活动开启时间
     * @return 结果
     */
    public int insertActivityOpen(ActivityOpen activityOpen);

    /**
     * 修改运营活动开启时间
     * 
     * @param activityOpen 运营活动开启时间
     * @return 结果
     */
    public int updateActivityOpen(ActivityOpen activityOpen);

    /**
     * 批量删除运营活动开启时间
     * 
     * @param ids 需要删除的运营活动开启时间主键集合
     * @return 结果
     */
    public int deleteActivityOpenByIds(Long[] ids);

    /**
     * 删除运营活动开启时间信息
     * 
     * @param id 运营活动开启时间主键
     * @return 结果
     */
    public int deleteActivityOpenById(Long id);
}
