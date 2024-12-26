package com.gm.server.admin.mapper;

import com.gm.server.admin.domain.GmServer;

import java.util.List;

/**
 * 服务器表 数据层
 * 
 * @author gamer
 */
public interface GmServerMapper
{
    /**
     * 根据条件分页查询服务器数据
     * 
     * @param server 服务器信息
     * @return 服务器数据集合信息
     */
    public List<GmServer> selectServerList(GmServer server);

    /**
     * 查询所有服务器
     * 
     * @return 服务器列表
     */
    public List<GmServer> selectServerAll();

    /**
     * 通过角色ID查询服务器
     * 
     * @param pid 服务器ID
     * @return 服务器对象信息
     */
    public GmServer selectServerById(Long sid);

    /**
     * 修改服务器信息
     * 
     * @param server 服务器信息
     * @return 结果
     */
    public int updateServer(GmServer server);

    /**
     * 新增服务器信息
     * 
     * @param server 服务器信息
     * @return 结果
     */
    public int insertServer(GmServer server);

    /**
     * 通过服务器ID删除服务器
     * 
     * @param sid 服务器ID
     * @return 结果
     */
    public int deleteServerById(Long sid);

    /**
     * 批量删除服务器信息
     * 
     * @param sids 需要删除的服务器ID
     * @return 结果
     */
    public int deleteServerByIds(Long[] sids);

    /**
     * 选择多个服务器
     * @param sids
     * @return
     */
    public List<GmServer> selectServerByIds(Long[] sids);

    /**
     * 统计服务器数量
     * @return
     */
    public long countServer();
}
