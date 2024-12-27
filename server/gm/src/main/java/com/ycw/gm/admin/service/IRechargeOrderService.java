package com.ycw.gm.admin.service;

import com.ycw.gm.admin.domain.RechargeOrder;

import java.util.List;

/**
 * 充值订单记录Service接口
 * 
 * @author gamer
 * @date 2022-07-27
 */
public interface IRechargeOrderService 
{
    /**
     * 查询充值订单记录列表
     * 
     * @param rechargeOrder 充值订单记录
     * @return 充值订单记录集合
     */
    public List<RechargeOrder> selectRechargeOrderList(RechargeOrder rechargeOrder);

    List<RechargeOrder> selectAllRechargeOrderList(RechargeOrder rechargeOrder);

    /**
     * 新增充值订单记录
     * 
     * @param rechargeOrder 充值订单记录
     * @return 结果
     */
    public int insertRechargeOrder(String tabName, RechargeOrder rechargeOrder);

    int countRechargeLogNum(RechargeOrder rechargeOrder);

}
