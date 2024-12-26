package com.gm.server.admin.mapper;

import com.gm.server.admin.domain.GmRecharge;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * GM充值Mapper接口
 * 
 * @author gamer
 * @date 2022-08-02
 */
public interface GmRechargeMapper 
{
    /**
     * 查询GM充值
     * 
     * @param id GM充值主键
     * @return GM充值
     */
    public GmRecharge selectGmRechargeById(Long id);

    /**
     * 查询GM充值列表
     * 
     * @param gmRecharge GM充值
     * @return GM充值集合
     */
    public List<GmRecharge> selectGmRechargeList(GmRecharge gmRecharge);

    public List<GmRecharge> selectGmRechargeByIds(Long[] sids);

    /**
     * 新增GM充值
     * 
     * @param gmRecharge GM充值
     * @return 结果
     */
    public int insertGmRecharge(GmRecharge gmRecharge);

    /**
     * 修改GM充值
     * 
     * @param gmRecharge GM充值
     * @return 结果
     */
    public int updateGmRecharge(GmRecharge gmRecharge);

    public int updateGmRechargeStatus(@Param("recharge") GmRecharge gmRecharge, @Param("sid") Long[] ids);


    /**
     * 删除GM充值
     * 
     * @param id GM充值主键
     * @return 结果
     */
    public int deleteGmRechargeById(Long id);

    /**
     * 批量删除GM充值
     * 
     * @param ids 需要删除的数据主键集合
     * @return 结果
     */
    public int deleteGmRechargeByIds(Long[] ids);
}
