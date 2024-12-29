package com.ycw.gm.admin.service;

import com.ycw.core.cluster.entity.ServerEntity;
import com.ycw.core.cluster.template.ServerYmlTemplate;
import com.ycw.gm.admin.domain.GmServer;

import java.util.List;

/**
 * 角色业务层
 *
 * @author gamer
 */
public interface IServerService {
    /**
     * 查询服务器列表
     *
     * @return 服务器列表
     */
    public List<ServerEntity> selectServerAll();

    /**
     * 通过角色ID查询服务器
     *
     * @return 服务器对象信息
     */
    public GmServer selectServerById(Long sid);

    public List<GmServer> selectServerByIds(Long[] sids);

    long countServer();

    /**
     * 修改保存服务器信息
     *
     * @param t 配置信息
     * @return 结果
     */
    public int updateServer(ServerYmlTemplate serverYmlTemplate);

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

    boolean serverOpenByTime(GmServer server, int openType, boolean create);

    String stopServer(GmServer gmServer);

    String startServer(GmServer gmServer);

    void updateGameServerOpenTime(GmServer server, long openTime, boolean closeSwitch);

    String executeShell(String host, String... cmd);
}
