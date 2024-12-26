package com.gm.server.admin.service;

import com.gm.server.admin.domain.GmExecuteCommand;

import java.util.List;

/**
 * gm命令行Service接口
 * 
 * @author gamer
 * @date 2022-08-01
 */
public interface IGmExecuteCommandService 
{
    /**
     * 查询gm命令行
     * 
     * @param id gm命令行主键
     * @return gm命令行
     */
    public GmExecuteCommand selectGmExecuteCommandById(Long id);

    /**
     * 查询gm命令行列表
     * 
     * @param gmExecuteCommand gm命令行
     * @return gm命令行集合
     */
    public List<GmExecuteCommand> selectGmExecuteCommandList(GmExecuteCommand gmExecuteCommand);

    public List<GmExecuteCommand> selectGmExecuteCommandByIds(Long[] sids);

    /**
     * 新增gm命令行
     * 
     * @param gmExecuteCommand gm命令行
     * @return 结果
     */
    public int insertGmExecuteCommand(GmExecuteCommand gmExecuteCommand);

    /**
     * 修改gm命令行
     * 
     * @param gmExecuteCommand gm命令行
     * @return 结果
     */
    public int updateGmExecuteCommand(GmExecuteCommand gmExecuteCommand);

    public int updateGmExecuteCommandStatus(GmExecuteCommand gmExecuteCommand, Long[] ids);


    /**
     * 批量删除gm命令行
     * 
     * @param ids 需要删除的gm命令行主键集合
     * @return 结果
     */
    public int deleteGmExecuteCommandByIds(Long[] ids);

    /**
     * 删除gm命令行信息
     * 
     * @param id gm命令行主键
     * @return 结果
     */
    public int deleteGmExecuteCommandById(Long id);
}
