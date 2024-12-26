package com.gm.server.admin.mapper;

import com.gm.server.admin.domain.GeneralLog;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 玩家表 数据层
 * 
 * @author gamer
 */
public interface GeneralLogMapper
{
    /**
     * 通过角色ID查询玩家
     * 
     * @return 玩家对象信息
     */
    public List<GeneralLog> selectGeneralLogById(@Param("tabName") String tabName, @Param("log") GeneralLog log, @Param("limitStart") Long limitStart, @Param("limitEnd") Long limitEnd);

    public List<GeneralLog> selectGeneralLogByIdAll(@Param("tabName") String tabName, @Param("log") GeneralLog log);


    public long countLog(@Param("tabName") String tabName, @Param("log") GeneralLog log);

    public List<String> countTable(String schemaName);
}
