package com.ycw.gm.admin.service;

import com.ycw.gm.admin.domain.GmMail;

import java.util.List;

/**
 * 角色业务层
 * 
 * @author gamer
 */
public interface IGmMailService
{
    /**
     * 根据条件分页查询角色数据
     * 
     * @param mail 服务器信息
     * @return 服务器数据集合信息
     */
    public List<GmMail> selectGmMailList(GmMail mail);

    /**
     * 查询所有角色
     * 
     * @return 服务器列表
     */
    public List<GmMail> selectMailAll();

    /**
     * 通过角色ID查询角色
     * 
     * @param mailId 服务器ID
     * @return 服务器对象信息
     */
    public GmMail selectGmMailById(Long mailId);

    /**
     * 根据条件分页查询角色数据
     *
     * @return 服务器数据集合信息
     */
    public List<GmMail> selectGmMailByIds(Long[] sids);

    /**
     * 新增保存服务器信息
     * 
     * @param mail 服务器信息
     * @return 结果
     */
    public int insertGmMail(GmMail mail);

    /**
     * 修改保存服务器信息
     * 
     * @param mail 服务器信息
     * @return 结果
     */
    public int updateGmMail(GmMail mail);

    /**
     * 修改保存服务器信息
     *
     * @param mail 服务器信息
     * @return 结果
     */
    public int updateGmMailStatus(GmMail mail, Long[] ids);

    /**
     * 通过服务器ID删除服务器
     * 
     * @param mailId 服务器ID
     * @return 结果
     */
    public int deleteMailById(Long mailId);

    /**
     * 批量删除服务器信息
     * 
     * @param sids 需要删除的服务器ID
     * @return 结果
     */
    public int deleteMailByIds(Long[] sids);

}
