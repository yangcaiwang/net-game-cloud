package com.ycw.gm.admin.service.impl;

import com.ycw.gm.admin.domain.Gangs;
import com.ycw.gm.admin.domain.GangsMember;
import com.ycw.gm.admin.mapper.GangsMapper;
import com.ycw.gm.admin.mapper.GangsMemberMapper;
import com.ycw.gm.admin.service.IGangsService;
import com.ycw.gm.common.utils.spring.SpringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 玩家 业务层处理
 *
 * @author gamer
 */
@Service
public class GangsServiceImpl implements IGangsService {

    @Autowired
    private GangsMapper gangsMapper;

    @Autowired
    private GangsMemberMapper gangsMemberMapper;

    /**
     * 根据条件分页查询玩家数据
     *
     * @param gangs 玩家信息
     * @return 玩家数据集合信息
     */
    @Override
    public List<Gangs> selectGangsList(Gangs gangs) {
        return gangsMapper.selectGangsList(gangs);
    }

    /**
     * 查询所有玩家
     *
     * @return 玩家列表
     */
    @Override
    public List<Gangs> selectGangsAll() {
        return SpringUtils.getAopProxy(this).selectGangsList(new Gangs());
    }

    /**
     * 通过玩家ID查询玩家
     *
     * @param gangsId 玩家ID
     * @return 玩家对象信息
     */
    @Override
    public Gangs selectGangsById(Long gangsId) {
        return gangsMapper.selectGangsById(gangsId);
    }

    /**
     * 通过玩家ID查询玩家帮派个人信息
     * @param playerId
     * @return
     */
    @Override
    public GangsMember selectGangsMemberById(Long playerId) {
        return gangsMemberMapper.selectGangsMembersById(playerId);
    }

    @Override
    public List<GangsMember> selectGangsMemberByIds(List<Long> playerIds) {
        return gangsMemberMapper.selectGangsMembersByIds(playerIds);
    }
}
