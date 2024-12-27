package com.ycw.gm.admin.service.impl;

import com.ycw.gm.admin.domain.GmRecharge;
import com.ycw.gm.admin.mapper.GmRechargeMapper;
import com.ycw.gm.admin.service.IGmRechargeService;
import com.ycw.gm.common.utils.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * GM充值Service业务层处理
 * 
 * @author gamer
 * @date 2022-08-02
 */
@Service
public class GmRechargeServiceImpl implements IGmRechargeService
{
    @Autowired
    private GmRechargeMapper gmRechargeMapper;

    /**
     * 查询GM充值
     * 
     * @param id GM充值主键
     * @return GM充值
     */
    @Override
    public GmRecharge selectGmRechargeById(Long id)
    {
        return gmRechargeMapper.selectGmRechargeById(id);
    }

    @Override
    public List<GmRecharge> selectGmRechargeByIds(Long[] sids) {
        return gmRechargeMapper.selectGmRechargeByIds(sids);
    }

    /**
     * 查询GM充值列表
     * 
     * @param gmRecharge GM充值
     * @return GM充值
     */
    @Override
    public List<GmRecharge> selectGmRechargeList(GmRecharge gmRecharge)
    {
        return gmRechargeMapper.selectGmRechargeList(gmRecharge);
    }

    /**
     * 新增GM充值
     * 
     * @param gmRecharge GM充值
     * @return 结果
     */
    @Override
    public int insertGmRecharge(GmRecharge gmRecharge)
    {
        gmRecharge.setCreateTime(DateUtils.getNowDate());
        return gmRechargeMapper.insertGmRecharge(gmRecharge);
    }

    /**
     * 修改GM充值
     * 
     * @param gmRecharge GM充值
     * @return 结果
     */
    @Override
    public int updateGmRecharge(GmRecharge gmRecharge)
    {
        gmRecharge.setUpdateTime(DateUtils.getNowDate());
        return gmRechargeMapper.updateGmRecharge(gmRecharge);
    }

    @Override
    public int updateGmRechargeStatus(GmRecharge gmRecharge, Long[] ids) {
        return gmRechargeMapper.updateGmRechargeStatus(gmRecharge, ids);
    }

    /**
     * 批量删除GM充值
     * 
     * @param ids 需要删除的GM充值主键
     * @return 结果
     */
    @Override
    public int deleteGmRechargeByIds(Long[] ids)
    {
        return gmRechargeMapper.deleteGmRechargeByIds(ids);
    }

    /**
     * 删除GM充值信息
     * 
     * @param id GM充值主键
     * @return 结果
     */
    @Override
    public int deleteGmRechargeById(Long id)
    {
        return gmRechargeMapper.deleteGmRechargeById(id);
    }
}
