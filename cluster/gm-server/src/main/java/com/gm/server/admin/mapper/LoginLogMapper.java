package com.gm.server.admin.mapper;

import com.gm.server.admin.domain.LoginLog;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 玩家表 数据层
 * 
 * @author gamer
 */
public interface LoginLogMapper
{
    /**
     * 通过角色ID查询玩家
     * 
     * @return 玩家对象信息
     */
    public List<LoginLog> selectLoginLogById(@Param("tabName") String tabName, @Param("log") LoginLog log, @Param("limitStart") Long limitStart, @Param("limitEnd") Long limitEnd);

    public List<LoginLog> selectLoginLogByIdAll(@Param("tabName") String tabName, @Param("log") LoginLog log);


    public long countLog(@Param("tabName") String tabName, @Param("log") LoginLog log);

    public List<String> countTable(String schemaName);
}
