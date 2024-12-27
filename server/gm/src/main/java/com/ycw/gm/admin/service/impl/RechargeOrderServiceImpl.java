package com.ycw.gm.admin.service.impl;

import com.ycw.gm.admin.domain.RechargeOrder;
import com.ycw.gm.admin.mapper.RechargeOrderMapper;
import com.ycw.gm.admin.service.IRechargeOrderService;
import com.ycw.gm.common.constant.Constants;
import com.ycw.gm.common.core.page.PageDomain;
import com.ycw.gm.common.core.page.TableSupport;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * 充值订单记录Service业务层处理
 * 
 * @author gamer
 * @date 2022-07-27
 */
@Service
public class RechargeOrderServiceImpl implements IRechargeOrderService
{
    @Autowired
    private RechargeOrderMapper rechargeOrderMapper;

    /**
     * 查询充值订单记录列表
     * 
     * @param rechargeOrder 充值订单记录
     * @return 充值订单记录
     */
    @Override
    public List<RechargeOrder> selectRechargeOrderList(RechargeOrder rechargeOrder)
    {
        PageDomain pageDomain = TableSupport.buildPageRequest();
        int pageNum = pageDomain.getPageNum() - 1;
        int pageSize = pageDomain.getPageSize();

        int startSize = pageSize * pageNum;
        int sumCount = 0;
        List<RechargeOrder> logList = new ArrayList<>();
        for (int i = 1; i <= Constants.RECHARGE_TABLE_NUM; i++) {
            String tabName = Constants.RECHARGE_NAME_PRE + i;
            long countNum = rechargeOrderMapper.countTableSize(tabName, rechargeOrder);
            if (sumCount + countNum > startSize) {
                long limitStart = 0;
                if (sumCount >= startSize) {
                } else {
                    limitStart = startSize - sumCount;
                }
                long limitEnd = Math.min(pageSize - logList.size(), countNum);
                List<RechargeOrder> logs = rechargeOrderMapper.selectRechargeOrderList(tabName, rechargeOrder, limitStart, limitEnd);
                logList.addAll(logs);
                if (logList.size() >= pageSize) {
                    break;
                }
            }
            sumCount += countNum;
        }

        return logList;
    }

    @Override
    public List<RechargeOrder> selectAllRechargeOrderList(RechargeOrder rechargeOrder)
    {

        List<RechargeOrder> logList = new ArrayList<>();
        for (int i = 1; i <= Constants.RECHARGE_TABLE_NUM; i++) {
            String tabName = Constants.RECHARGE_NAME_PRE + i;
            long countNum = rechargeOrderMapper.countTableSize(tabName, rechargeOrder);
            List<RechargeOrder> logs = rechargeOrderMapper.selectRechargeOrderList(tabName, rechargeOrder, 0L, countNum);
            logList.addAll(logs);
        }

        return logList;
    }

    /**
     * 新增充值订单记录
     * 
     * @param rechargeOrder 充值订单记录
     * @return 结果
     */
    @Override
    public int insertRechargeOrder(String tabName, RechargeOrder rechargeOrder)
    {
        return rechargeOrderMapper.insertRechargeOrder(tabName, rechargeOrder);
    }

    @Override
    public int countRechargeLogNum(RechargeOrder rechargeOrder) {
        int sumCount = 0;
        for (int i = 1; i <= Constants.RECHARGE_TABLE_NUM; i++) {
            String tabName = Constants.RECHARGE_NAME_PRE + i;
            long countNum = rechargeOrderMapper.countTableSize(tabName, rechargeOrder);
            sumCount += countNum;
        }
        return sumCount;
    }
}
