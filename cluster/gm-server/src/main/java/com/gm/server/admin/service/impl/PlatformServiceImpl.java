package com.gm.server.admin.service.impl;

import com.gm.server.common.constant.UserConstants;
import com.gm.server.common.utils.StringUtils;
import com.gm.server.common.utils.spring.SpringUtils;
import com.gm.server.admin.domain.GmPlatform;
import com.gm.server.admin.mapper.GmPlatformMapper;
import com.gm.server.admin.service.IPlatformService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 平台 业务层处理
 * 
 * @author gamer
 */
@Service
public class PlatformServiceImpl implements IPlatformService
{
    @Autowired
    private GmPlatformMapper platformMapper;

    /**
     * 根据条件分页查询平台数据
     * 
     * @param platform 平台信息
     * @return 平台数据集合信息
     */
    @Override
    public List<GmPlatform> selectPlatformList(GmPlatform platform)
    {
        return platformMapper.selectPlatformList(platform);
    }

    /**
     * 查询所有平台
     * 
     * @return 平台列表
     */
    @Override
    public List<GmPlatform> selectPlatformAll()
    {
        return SpringUtils.getAopProxy(this).selectPlatformList(new GmPlatform());
    }

    /**
     * 通过平台ID查询平台
     * 
     * @param pid 平台ID
     * @return 平台对象信息
     */
    @Override
    public GmPlatform selectPlatformById(Long pid)
    {
        return platformMapper.selectPlatformById(pid);
    }

    /**
     * 新增保存平台信息
     * 
     * @param platform 平台信息
     * @return 结果
     */
    @Override
    @Transactional
    public int insertPlatform(GmPlatform platform)
    {
        // 新增平台信息
        return platformMapper.insertPlatform(platform);
    }

    /**
     * 修改保存平台信息
     * 
     * @param platform 平台信息
     * @return 结果
     */
    @Override
    @Transactional
    public int updatePlatform(GmPlatform platform)
    {
        // 平台平台信息
        return platformMapper.updatePlatform(platform);
    }

    /**
     * 修改平台状态
     * 
     * @param platform 平台信息
     * @return 结果
     */
    @Override
    public int updateWhiteStatus(GmPlatform platform)
    {
        return platformMapper.updatePlatform(platform);
    }

    /**
     * 通过平台ID删除平台
     * 
     * @param pid 平台ID
     * @return 结果
     */
    @Override
    @Transactional
    public int deletePlatformById(Long pid)
    {
        return platformMapper.deletePlatformById(pid);
    }

    /**
     * 批量删除平台信息
     * 
     * @param pids 需要删除的平台ID
     * @return 结果
     */
    @Override
    @Transactional
    public int deletePlatformByIds(Long[] pids)
    {
        return platformMapper.deletePlatformByIds(pids);
    }

    @Override
    public String checkPlatformUnique(GmPlatform platform)
    {
        Long roleId = StringUtils.isNull(platform.getPlatformId()) ? -1L : platform.getPlatformId();
        GmPlatform info = platformMapper.selectPlatformById(platform.getPlatformId());
        if (StringUtils.isNotNull(info) && info.getPlatformId().longValue() != roleId.longValue())
        {
            return UserConstants.NOT_UNIQUE;
        }
        return UserConstants.UNIQUE;
    }

}
