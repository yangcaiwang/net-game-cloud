package com.ycw.core.cluster.node;

import com.ycw.core.cluster.ClusterService;
import com.ycw.core.cluster.ClusterServiceImpl;
import com.ycw.core.cluster.entity.ServerEntity;
import com.ycw.core.cluster.enums.ServerState;
import com.ycw.core.cluster.enums.ServerType;
import com.ycw.core.cluster.property.PropertyConfig;
import com.ycw.core.cluster.template.ServerYmlTemplate;
import com.ycw.core.internal.base.BaseConfigUtil;
import com.ycw.core.internal.db.entity.Repositories;
import com.ycw.core.internal.loader.Scanner;
import com.ycw.core.internal.loader.service.ServiceContext;
import com.ycw.core.network.jetty.HttpClient;
import com.ycw.core.network.jetty.http.HttpCode;
import com.ycw.core.network.netty.message.SocketChannelManage;
import com.ycw.core.util.PrintManager;
import com.ycw.core.util.SystemUtils;
import org.apache.maven.model.io.xpp3.MavenXpp3Reader;
import org.apache.maven.project.MavenProject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileReader;
import java.io.Reader;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * <服务器组件抽象类>
 * <p>
 *
 * @author <yangcaiwang>
 * @version <1.0>
 */
public abstract class AbstractServerNode implements SuperServerNode {

    public static final Logger log = LoggerFactory.getLogger(AbstractServerNode.class);
    public ServerYmlTemplate serverYmlTemplate;

    protected ServerType serverType;

    protected abstract void startRedission();

    protected abstract void registerRedission();

    protected abstract void startDatabase();

    protected abstract void startJettyServer();

    protected abstract void startNettyServer();

    protected abstract void startGrpcServer();

    public abstract void connectGrpcServer();

    @Override
    public ServerType serverType() {
        return serverType;
    }

    @Override
    public void init() {
        try {
            // (1)初始化服务器配置
            this.serverYmlTemplate = PropertyConfig.loadYml(PropertyConfig.getAbsolutePath("server.yml", serverType), ServerYmlTemplate.class);
            log.info("======================= [{}] 初始化服务器配置完毕 =======================", getServerId());

            PropertyConfig.modifyServerYml(ServerNodeComponent.getInstance().serverYmlTemplate, this.serverType);
            // (2)初始化游戏配置
            PropertyConfig.load(PropertyConfig.getAbsolutePath("server.properties", serverType));
            log.info("======================= [{}] 初始化游戏配置完毕 =======================", getServerId());

            // (3)初始化日志
            PropertyConfig.initLog4J2(PropertyConfig.getAbsolutePath("log4j2.xml", serverType));
            log.info("======================= [{}] 初始化日志完毕 =======================", getServerId());

            // (4)初始化基础配置
            BaseConfigUtil.getInstance().load(PropertyConfig.getAbsolutePath("json/", serverType), getGroupId() + "." + serverType.getName());
            log.info("======================= [{}] 初始化基础配置完毕 =======================", getServerId());

            // (5)扫描单例
            Scanner.getInstance().scan(getGroupId() + ".core", getGroupId() + "." + serverType.getName());
            log.info("======================= [{}] 扫描单例完毕 =======================", getServerId());
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    @Override
    public void start() {
        try {
            long begin = System.currentTimeMillis();
            startRedission();
            registerRedission();
            startDatabase();
            startJettyServer();
            startNettyServer();
            startGrpcServer();
            connectGrpcServer();
            long timeUsed = System.currentTimeMillis() - begin;
            PrintManager.showPrint(PropertyConfig.isDebug());
            log.info("======================= [{}] 启动完毕,耗时[{}]秒, pid=[{}] =======================", getServerId(), (timeUsed / 1000f), SystemUtils.getPid());
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    @Override
    public void stop() {
        try {
            // 更新服务器状态
            ClusterService clusterService = ServiceContext.getInstance().get(ClusterServiceImpl.class);
            ServerEntity serverEntity = clusterService.getServerEntity(getServerId());
            serverEntity.setServerState(ServerState.ERROR.state);
            clusterService.saveServerEntity(serverEntity);

            switch (this.serverType) {
                case GATE_SERVER:
                    // 踢掉本网关所有玩家连接
                    int players = SocketChannelManage.getInstance().kitOutAll();
                    if (players != 0) {
                        log.info("======================= [{}] kit out [{}] player =======================", getServerId(), players);
                    }

                    // 通过Jetty发布保存本网关所有玩家数据事件
                    List<ServerEntity> serverEntityList = clusterService.getServerEntity(serverEntity.getGroupId(), ServerType.GAME_SERVER);
                    for (ServerEntity entity : serverEntityList) {
                        if (ServerState.isError(entity.getServerState())) {
                            continue;
                        }

                        Map<String, String> params = new HashMap<>();
                        params.put("value", "allPlayer");
                        HttpClient.HttpResponse httpResponse = HttpClient.getInstance().sendGet(entity.getJettyServerAddr().getAddress(), params, null);
                        if (httpResponse.getCode() == HttpCode.SUCCESS.getIndex()) {
                            log.info("======================= [{}] all player data saved =======================", entity.getServerId());
                        }
                    }
                    break;
                case GAME_SERVER:
                    Repositories.flushAll();
                    log.info("======================= [{}] all db data saved =======================", getServerId());
                    break;

                case GM_SERVER:
                case LOGIN_SERVER:
                case BATTLE_SERVER:
                default:
            }

        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }

    /**
     * 获取pom依赖的组id
     */
    protected String getGroupId() {
        try {
            Reader reader = new FileReader("pom.xml");
            MavenXpp3Reader xpp3Reader = new MavenXpp3Reader();
            MavenProject project = new MavenProject(xpp3Reader.read(reader));
            return project.getGroupId();
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        return null;
    }

    /**
     * 获取服务器id
     */
    public String getServerId() {
        return serverYmlTemplate.getNode() == null ? "" : serverYmlTemplate.getNode().getServerId();
    }

    protected void setServerType(ServerType serverType) {
        this.serverType = serverType;
    }
}
