package com.gm.server.admin.mapper;

import com.gm.server.admin.domain.GangsMember;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 玩家表 数据层
 * 
 * @author gamer
 */
public interface GangsMemberMapper
{
    /**
     * 通过角色ID查询玩家
     * 
     * @param playerId 帮派ID
     * @return 成员对象信息
     */
    public GangsMember selectGangsMembersById(Long playerId);

    public List<GangsMember> selectGangsMembersByIds(@Param("playerIds") List<Long> playerIds);

}
