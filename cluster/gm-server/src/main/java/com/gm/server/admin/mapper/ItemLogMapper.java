package com.gm.server.admin.mapper;

import com.gm.server.admin.domain.ItemLog;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 玩家表 数据层
 * 
 * @author gamer
 */
public interface ItemLogMapper
{
    /**
     * 通过角色ID查询玩家
     * 
     * @param playerId 玩家ID
     * @return 玩家对象信息
     */
    public List<ItemLog> selectItemLogById(@Param("tabName") String tabName, @Param("playerId") Long playerId, @Param("itemLog") ItemLog itemLog, @Param("limitStart") Long limitStart, @Param("limitEnd") Long limitEnd);

    public List<ItemLog> selectItemLogByIdAll(@Param("tabName") String tabName,@Param("playerId") Long playerId, @Param("itemLog") ItemLog itemLog);


    public long countItemLog(@Param("tabName") String tabName, @Param("playerId") Long playerId, @Param("itemLog") ItemLog itemLog);

    public List<String> countTable(String schemaName);
}
