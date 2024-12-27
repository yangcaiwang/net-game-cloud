package com.ycw.gm.admin.mapper;

import com.ycw.gm.admin.domain.PlayerMail;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 玩家表 数据层
 * 
 * @author gamer
 */
public interface PlayerMailMapper
{
    /**
     * 通过角色ID查询玩家
     * 
     * @param playerId 玩家ID
     * @return 玩家对象信息
     */
    public List<PlayerMail> selectPlayerMailById(@Param("playerId") Long playerId, @Param("playerMail") PlayerMail playerMail);

}
