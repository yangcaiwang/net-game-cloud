package com.ycw.gm.admin.mapper;

import com.ycw.gm.admin.domain.RechargeOrder;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 充值订单记录Mapper接口
 * 
 * @author gamer
 * @date 2022-07-27
 */
public interface RechargeOrderMapper 
{
    /**
     * 查询充值订单记录列表
     * 
     * @param rechargeOrder 充值订单记录
     * @return 充值订单记录集合
     */
    public List<RechargeOrder> selectRechargeOrderList(@Param("tabName") String tabName, @Param("rechargeOrder") RechargeOrder rechargeOrder, @Param("limitStart") Long limitStart, @Param("limitEnd") Long limitEnd);

    /**
     * 新增充值订单记录
     * 
     * @param rechargeOrder 充值订单记录
     * @return 结果
     */
    public int insertRechargeOrder(@Param("tabName") String tabName, @Param("rechargeOrder") RechargeOrder rechargeOrder);

    public long countTableSize(@Param("tabName") String tabName, @Param("rechargeOrder") RechargeOrder rechargeOrder);
}
