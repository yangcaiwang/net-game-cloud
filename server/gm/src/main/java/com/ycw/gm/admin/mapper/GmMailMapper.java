package com.ycw.gm.admin.mapper;

import com.ycw.gm.admin.domain.GmMail;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 玩家表 数据层
 * 
 * @author gamer
 */
public interface GmMailMapper
{
    /**
     * 通过角色ID查询玩家
     * 
     * @param gmMail 邮件
     * @return 玩家对象信息
     */
    public List<GmMail> selectGmMailList(GmMail gmMail);
    public GmMail selectMailById(Long mailId);

    public List<GmMail> selectMailByIds(Long[] sids);

    /**
     * 新增邮件信息
     *
     * @param gmMail 邮件信息
     * @return 结果
     */
    public int insertGmMail(GmMail gmMail);

    /**
     * 修改mail信息
     *
     * @param gmMail 邮件信息
     * @return 结果
     */
    public int updateGmMail(GmMail gmMail);

    /**
     * 修改mail信息
     *
     * @param gmMail 邮件信息
     * @return 结果
     */
    public int updateGmMailStatus(@Param("mail") GmMail gmMail, @Param("sid") Long[] sid);

    /**
     * 通过ID删除邮件
     *
     * @param sid 邮件id
     * @return 结果
     */
    public int deleteGmMailById(Long sid);

    /**
     * 批量删除邮件信息
     *
     * @param sids 需要删除的邮件ID
     * @return 结果
     */
    public int deleteGmMailByIds(Long[] sids);
}
