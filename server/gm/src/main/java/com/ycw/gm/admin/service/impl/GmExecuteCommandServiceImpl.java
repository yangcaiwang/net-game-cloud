package com.ycw.gm.admin.service.impl;

import com.ycw.gm.admin.domain.GmExecuteCommand;
import com.ycw.gm.admin.mapper.GmExecuteCommandMapper;
import com.ycw.gm.admin.service.IGmExecuteCommandService;
import com.ycw.gm.common.utils.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * gm命令行Service业务层处理
 * 
 * @author gamer
 * @date 2022-08-01
 */
@Service
public class GmExecuteCommandServiceImpl implements IGmExecuteCommandService
{
    @Autowired
    private GmExecuteCommandMapper gmExecuteCommandMapper;

    /**
     * 查询gm命令行
     * 
     * @param id gm命令行主键
     * @return gm命令行
     */
    @Override
    public GmExecuteCommand selectGmExecuteCommandById(Long id)
    {
        return gmExecuteCommandMapper.selectGmExecuteCommandById(id);
    }

    /**
     * 查询gm命令行列表
     * 
     * @param gmExecuteCommand gm命令行
     * @return gm命令行
     */
    @Override
    public List<GmExecuteCommand> selectGmExecuteCommandList(GmExecuteCommand gmExecuteCommand)
    {
        return gmExecuteCommandMapper.selectGmExecuteCommandList(gmExecuteCommand);
    }

    @Override
    public List<GmExecuteCommand> selectGmExecuteCommandByIds(Long[] sids) {
        return gmExecuteCommandMapper.selectGmExecuteCommandByIds(sids);
    }

    /**
     * 新增gm命令行
     * 
     * @param gmExecuteCommand gm命令行
     * @return 结果
     */
    @Override
    public int insertGmExecuteCommand(GmExecuteCommand gmExecuteCommand)
    {
        gmExecuteCommand.setCreateTime(DateUtils.getNowDate());
        return gmExecuteCommandMapper.insertGmExecuteCommand(gmExecuteCommand);
    }

    /**
     * 修改gm命令行
     * 
     * @param gmExecuteCommand gm命令行
     * @return 结果
     */
    @Override
    public int updateGmExecuteCommand(GmExecuteCommand gmExecuteCommand)
    {
        gmExecuteCommand.setUpdateTime(DateUtils.getNowDate());
        return gmExecuteCommandMapper.updateGmExecuteCommand(gmExecuteCommand);
    }

    @Override
    public int updateGmExecuteCommandStatus(GmExecuteCommand gmExecuteCommand, Long[] ids) {
        return gmExecuteCommandMapper.updateGmExecuteCommandStatus(gmExecuteCommand, ids);
    }

    /**
     * 批量删除gm命令行
     * 
     * @param ids 需要删除的gm命令行主键
     * @return 结果
     */
    @Override
    public int deleteGmExecuteCommandByIds(Long[] ids)
    {
        return gmExecuteCommandMapper.deleteGmExecuteCommandByIds(ids);
    }

    /**
     * 删除gm命令行信息
     * 
     * @param id gm命令行主键
     * @return 结果
     */
    @Override
    public int deleteGmExecuteCommandById(Long id)
    {
        return gmExecuteCommandMapper.deleteGmExecuteCommandById(id);
    }
}
