package com.ycw.gm.admin.mapper;

import com.ycw.gm.admin.domain.RechargeDetail;

import java.util.List;

/**
 * 玩家表 数据层
 * 
 * @author gamer
 */
public interface RechargeDetailMapper
{
    /**
     * 通过角色ID查询玩家
     * 
     * @return 玩家对象信息
     */
    public Long sumRecharge();

    public List<RechargeDetail> selectAllRecharge(RechargeDetail detail);


//    public Long countRecharge(@Param("tabName") String tabName);
//
//    public List<String> countTable(String schemaName);
}
