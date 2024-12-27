package com.ycw.gm.admin.mapper;

import com.ycw.gm.admin.domain.PowerLog;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 玩家表 数据层
 * 
 * @author gamer
 */
public interface PowerLogMapper
{
    /**
     * 通过角色ID查询玩家
     * 
     * @param roleId 玩家ID
     * @return 玩家对象信息
     */
    public List<PowerLog> selectPowerLogById(@Param("tabName") String tabName, @Param("roleId") Long roleId, @Param("log") PowerLog log, @Param("limitStart") Long limitStart, @Param("limitEnd") Long limitEnd);

    public List<PowerLog> selectPowerLogByIdAll(@Param("tabName") String tabName,@Param("roleId") Long roleId, @Param("log") PowerLog log);


    public long countPowerLog(@Param("tabName") String tabName, @Param("roleId") Long roleId, @Param("log") PowerLog log);

    public List<String> countTable(String schemaName);
}
