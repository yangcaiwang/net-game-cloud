package com.gm.server.admin.mapper;

import com.gm.server.admin.domain.ActivityOpen;

import java.util.List;

/**
 * 运营活动开启时间Mapper接口
 * 
 * @author gamer
 * @date 2022-07-26
 */
public interface ActivityOpenMapper 
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
     * 删除运营活动开启时间
     * 
     * @param id 运营活动开启时间主键
     * @return 结果
     */
    public int deleteActivityOpenById(Long id);

    /**
     * 批量删除运营活动开启时间
     * 
     * @param ids 需要删除的数据主键集合
     * @return 结果
     */
    public int deleteActivityOpenByIds(Long[] ids);
}
