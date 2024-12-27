package com.ycw.gm.admin.service;

import com.ycw.gm.admin.domain.GmPlatform;

import java.util.List;

/**
 * 角色业务层
 * 
 * @author gamer
 */
public interface IPlatformService
{
    /**
     * 根据条件分页查询角色数据
     * 
     * @param platform 平台信息
     * @return 平台数据集合信息
     */
    public List<GmPlatform> selectPlatformList(GmPlatform platform);

    /**
     * 查询所有角色
     * 
     * @return 平台列表
     */
    public List<GmPlatform> selectPlatformAll();

    /**
     * 通过角色ID查询角色
     * 
     * @param pid 平台ID
     * @return 平台对象信息
     */
    public GmPlatform selectPlatformById(Long pid);

    /**
     * 新增保存平台信息
     * 
     * @param platform 平台信息
     * @return 结果
     */
    public int insertPlatform(GmPlatform platform);

    /**
     * 修改保存平台信息
     * 
     * @param platform 平台信息
     * @return 结果
     */
    public int updatePlatform(GmPlatform platform);

    /**
     * 修改白名单状态
     * 
     * @param platform 角色信息
     * @return 结果
     */
    public int updateWhiteStatus(GmPlatform platform);

    /**
     * 通过平台ID删除平台
     * 
     * @param pid 平台ID
     * @return 结果
     */
    public int deletePlatformById(Long pid);

    /**
     * 批量删除平台信息
     * 
     * @param pIds 需要删除的平台ID
     * @return 结果
     */
    public int deletePlatformByIds(Long[] pIds);

    /**
     * 校验平台名称是否唯一
     *
     * @param platform 平台信息
     * @return 结果
     */
    public String checkPlatformUnique(GmPlatform platform);
}
