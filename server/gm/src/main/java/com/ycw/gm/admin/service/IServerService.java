package com.ycw.gm.admin.service;

import com.ycw.core.cluster.enums.ServerState;
import com.ycw.gm.admin.domain.GmServer;

import java.util.List;

/**
 * 角色业务层
 * 
 * @author gamer
 */
public interface IServerService
{
    /**
     * 根据条件分页查询角色数据
     * 
     * @param server 服务器信息
     * @return 服务器数据集合信息
     */
    public List<GmServer> selectServerList(GmServer server);

    /**
     * 查询所有角色
     * 
     * @return 服务器列表
     */
    public List<GmServer> selectServerAll();

    /**
     * 通过角色ID查询角色
     * 
     * @return 服务器对象信息
     */
    public GmServer selectServerById(Long sid);

    /**
     * 通过角色ID查询角色
     *
     * @return 服务器对象信息
     */
    public GmServer selectServerByServerIdAndPid(int platformId, int sid);

    /**
     * 新增保存服务器信息
     * 
     * @param server 服务器信息
     * @return 结果
     */
    public int insertServer(GmServer server);

    /**
     * 修改保存服务器信息
     * 
     * @param server 服务器信息
     * @return 结果
     */
    public int updateServer(GmServer server);

    /**
     * 修改白名单状态
     * 
     * @param server 角色信息
     * @return 结果
     */
    public int updateServerStatus(GmServer server);

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

    public List<GmServer> selectServerByIds(Long[] sids);

    /**
     * 校验服务器名称是否唯一
     *
     * @param server 服务器信息
     * @return 结果
     */
    public String checkServerUnique(GmServer server);

    List<GmServer> getServerByIdField(int pid, int minSid, int maxSid);
    GmServer getServerByIdFromRedis(int platformId, int serverId);

    int getVersionType(String version, int def);

    void addVersion(String version, int versionType);

    void delVersion(String version);

    /**
     * 设置定时开服
     * @param server
     * @param openType
     */
    boolean serverOpenByTime(GmServer server, int openType, boolean create);

    String stopServer(GmServer gmServer);
    String startServer(GmServer gmServer);

    long countServer();

    List<GmServer> gsrvs(String pid, String sid);

    void updateServersStatus(String pid, ServerState state, List<GmServer> servers, boolean newToHot);

    void updateServersOpenTime(String pid, String sid, long openTime, int activeType);

    void updateGameServerOpenTime(GmServer server, long openTime, boolean closeSwitch);

    String importServer(List<GmServer> serverList, Boolean isUpdateSupport, String operName);
    String executeShell(String host, String... cmd);
}
