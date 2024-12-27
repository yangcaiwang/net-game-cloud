package com.ycw.gm.admin.service;

import com.ycw.gm.admin.domain.RechargeDetail;

import java.util.List;

/**
 * @author wishcher tree
 * @date 2022/8/3 16:19
 */
public interface IRechargeDetailService {

//    int countRechargeNum(String dbName);
    Long sumRecharge();
    List<RechargeDetail> selectAllRecharge(RechargeDetail detail);
}
