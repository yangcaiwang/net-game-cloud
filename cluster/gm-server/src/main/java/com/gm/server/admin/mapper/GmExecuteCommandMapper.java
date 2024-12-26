package com.gm.server.admin.mapper;

import com.gm.server.admin.domain.GmExecuteCommand;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * gm命令行Mapper接口
 * 
 * @author gamer
 * @date 2022-08-01
 */
public interface GmExecuteCommandMapper 
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

    public int updateGmExecuteCommandStatus(@Param("command") GmExecuteCommand gmExecuteCommand, @Param("sid") Long[] ids);

    /**
     * 删除gm命令行
     * 
     * @param id gm命令行主键
     * @return 结果
     */
    public int deleteGmExecuteCommandById(Long id);

    /**
     * 批量删除gm命令行
     * 
     * @param ids 需要删除的数据主键集合
     * @return 结果
     */
    public int deleteGmExecuteCommandByIds(Long[] ids);
}
