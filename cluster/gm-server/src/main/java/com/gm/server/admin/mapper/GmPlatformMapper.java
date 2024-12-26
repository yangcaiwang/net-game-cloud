package com.gm.server.admin.mapper;

import com.gm.server.admin.domain.GmPlatform;

import java.util.List;

/**
 * 平台表 数据层
 * 
 * @author gamer
 */
public interface GmPlatformMapper
{
    /**
     * 根据条件分页查询平台数据
     * 
     * @param platform 平台信息
     * @return 平台数据集合信息
     */
    public List<GmPlatform> selectPlatformList(GmPlatform platform);

    /**
     * 查询所有平台
     * 
     * @return 平台列表
     */
    public List<GmPlatform> selectPlatformAll();

    /**
     * 通过角色ID查询平台
     * 
     * @param pid 平台ID
     * @return 平台对象信息
     */
    public GmPlatform selectPlatformById(Long pid);

    /**
     * 修改平台信息
     * 
     * @param platform 平台信息
     * @return 结果
     */
    public int updatePlatform(GmPlatform platform);

    /**
     * 新增平台信息
     * 
     * @param platform 平台信息
     * @return 结果
     */
    public int insertPlatform(GmPlatform platform);

    /**
     * 通过平台ID删除平台
     * 
     * @param pid 平台ID
     * @return 结果
     */
    public int deletePlatformById(Long pid);

    /**
     * 批量删除角色信息
     * 
     * @param pids 需要删除的平台ID
     * @return 结果
     */
    public int deletePlatformByIds(Long[] pids);
}
