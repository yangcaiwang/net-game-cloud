package com.gm.server.admin.service.impl;

import cn.hutool.core.util.NumberUtil;
import com.common.module.cluster.enums.ServerState;
import com.common.module.cluster.property.PropertyConfig;
import com.gm.server.admin.domain.GmPlatform;
import com.gm.server.admin.domain.GmServer;
import com.gm.server.admin.mapper.GmPlatformMapper;
import com.gm.server.admin.mapper.GmServerMapper;
import com.gm.server.admin.service.IServerService;
import com.gm.server.common.constant.Constants;
import com.gm.server.common.constant.UserConstants;
import com.gm.server.common.core.redis.RedisCache;
import com.gm.server.common.exception.ServiceException;
import com.gm.server.common.utils.ExecuteShellUtil;
import com.gm.server.common.utils.ParamParseUtils;
import com.gm.server.common.utils.StringUtils;
import com.gm.server.common.utils.bean.BeanValidators;
import com.gm.server.common.utils.spring.SpringUtils;
import org.apache.logging.log4j.util.Strings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.validation.Validator;
import java.util.*;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * 服务器 业务层处理
 *
 * @author gamer
 */
@Service
public class ServerServiceImpl implements IServerService {
    protected final Logger logger = LoggerFactory.getLogger(this.getClass());
    @Autowired
    private GmServerMapper serverMapper;

    @Autowired
    private GmPlatformMapper gmPlatformMapper;


    @Autowired
    private RedisCache redisCache;

    @Autowired
    private ScheduledExecutorService scheduledExecutorService;

    @Autowired
    protected Validator validator;

    private ScheduledFuture<?> scheduledFuture;

    /**
     * 根据条件分页查询服务器数据
     *
     * @param server 服务器信息
     * @return 服务器数据集合信息
     */
    @Override
    public List<GmServer> selectServerList(GmServer server) {
        return serverMapper.selectServerList(server);
    }

    /**
     * 查询所有服务器
     *
     * @return 服务器列表
     */
    @Override
    public List<GmServer> selectServerAll() {
        return SpringUtils.getAopProxy(this).selectServerList(new GmServer());
    }

    /**
     * 通过服务器ID查询服务器
     *
     * @param sid 服务器ID
     * @return 服务器对象信息
     */
    @Override
    public GmServer selectServerById(Long sid) {
        return serverMapper.selectServerById(sid);
    }

    @Override
    public GmServer selectServerByServerIdAndPid(int platformId, int sid) {
        String s = genServerKey(String.valueOf(platformId), String.valueOf(sid));
        return selectServerById(Long.parseLong(s));
    }

    private String getServerRedisKey(String pid, String serverId) {
        return getServerRedisKey(genServerKey(pid, serverId));
    }

    private String genServerKey(String pid, String serverId) {
        return String.format("%s%s", pid, serverId);
    }

    private String getServerRedisKey(String sid) {
        return Constants.SERVER_INFO + sid;
    }

    /**
     * 新增保存服务器信息
     *
     * @param server 服务器信息
     * @return 结果
     */
    @Override
    @Transactional
    public int insertServer(GmServer server) {
        String s = genServerKey(String.valueOf(server.getPlatformId()), String.valueOf(server.getServerId()));
        server.setServerKeyId(Long.parseLong(s));
        updateRedisServer(server);
        // 新增服务器信息
        return serverMapper.insertServer(server);
    }

    /**
     * 修改保存服务器信息
     *
     * @param server 服务器信息
     * @return 结果
     */
    @Override
    @Transactional
    public int updateServer(GmServer server) {
        updateRedisServer(server);
        // 服务器服务器信息
        return serverMapper.updateServer(server);
    }

    private void updateRedisServer(GmServer server) {
        String serverRedisKey = getServerRedisKey(String.valueOf(server.getServerKeyId()));
        GmServer gmServer = redisCache.getCacheObject(serverRedisKey);
        boolean change = false;
        if (gmServer == null) {
            gmServer = selectServerById(server.getServerKeyId());
            if (gmServer == null) {
                gmServer = server;
            }
            change = true;
        }
        if (server.getServerStatus() != null) {
            gmServer.setServerStatus(server.getServerStatus());
            change = true;
        }
        if (server.getServerId() != null) {
            gmServer.setServerId(server.getServerId());
            change = true;
        }
        if (server.getServerType() != null) {
            gmServer.setServerType(server.getServerType());
            change = true;
        }
        if (server.getInPort() != null) {
            gmServer.setInPort(server.getInPort());
            change = true;
        }
        if (server.getPlatformId() != null) {
            gmServer.setPlatformId(server.getPlatformId());
            change = true;
        }
        if (server.getInHost() != null) {
            gmServer.setInHost(server.getInHost());
            change = true;
        }
        if (server.getClientPort() != null) {
            gmServer.setClientPort(server.getClientPort());
            change = true;
        }
        if (server.getOutHost() != null) {
            gmServer.setOutHost(server.getOutHost());
            change = true;
        }
        if (server.getSort() != null) {
            gmServer.setSort(server.getSort());
            change = true;
        }
        if (server.getOpenTime() != null) {
            gmServer.setOpenTime(server.getOpenTime());
            change = true;
            serverOpenByTime(server, 1, false);
        }
        if (server.getServerName() != null) {
            gmServer.setServerName(server.getServerName());
            change = true;
        }
        if (server.getShowOut() != null) {
            gmServer.setShowOut(server.getShowOut());
            change = true;
        }
        if (server.getClientLog() != null) {
            gmServer.setClientLog(server.getClientLog());
            change = true;
        }

        if (change) {
            redisCache.setCacheObject(serverRedisKey, gmServer);
        }
    }

    /**
     * 修改服务器状态
     *
     * @param server 服务器信息
     * @return 结果
     */
    @Override
    public int updateServerStatus(GmServer server) {
        updateRedisServer(server);
        return serverMapper.updateServer(server);
    }

    /**
     * 通过服务器ID删除服务器
     *
     * @param sid 服务器ID
     * @return 结果
     */
    @Override
    @Transactional
    public int deleteServerById(Long sid) {
        GmServer server = serverMapper.selectServerById(sid);
        if (server != null) {
            redisCache.deleteObject(getServerRedisKey(String.valueOf(server.getServerKeyId())));
        }
        return serverMapper.deleteServerById(sid);
    }

    /**
     * 批量删除服务器信息
     *
     * @param sids 需要删除的服务器ID
     * @return 结果
     */
    @Override
    @Transactional
    public int deleteServerByIds(Long[] sids) {
        for (long sid : sids) {
            GmServer server = serverMapper.selectServerById(sid);
            if (server != null) {
                redisCache.deleteObject(getServerRedisKey(String.valueOf(sid)));
            }
        }
        return serverMapper.deleteServerByIds(sids);
    }

    @Override
    public List<GmServer> selectServerByIds(Long[] sids) {
        return serverMapper.selectServerByIds(sids);
    }

    @Override
    public String checkServerUnique(GmServer server) {
        Long roleId = StringUtils.isNull(server.getServerId()) ? -1L : server.getServerId();
        GmServer info = serverMapper.selectServerById(server.getServerId());
        if (StringUtils.isNotNull(info) && info.getServerId().longValue() != roleId.longValue()) {
            return UserConstants.NOT_UNIQUE;
        }
        return UserConstants.UNIQUE;
    }

    @Override
    public List<GmServer> getServerByIdField(int pid, int minSid, int maxSid) {
        List<GmServer> list = new ArrayList<>();
        Collection<String> keys = redisCache.keys(Constants.SERVER_INFO + "*");

        for (String key : keys) {
            GmServer cacheObject = redisCache.getCacheObject(key);
            if (cacheObject != null && cacheObject.getPlatformId() == pid && cacheObject.getServerId() != null && cacheObject.getServerId() >= minSid && cacheObject.getServerId() <= maxSid) {
                list.add(cacheObject);
            }
        }
        return list;
    }

    @Override
    public GmServer getServerByIdFromRedis(int platformId, int serverId) {
        String serverRedisKey = getServerRedisKey(String.valueOf(platformId), String.valueOf(serverId));
        return redisCache.getCacheObject(serverRedisKey);
    }

    @Override
    public int getVersionType(String version, int def) {
        Map<String, Integer> cacheMap = redisCache.getCacheMap(Constants.VERSION_INFO);
        return cacheMap.getOrDefault(version, def);
    }

    @Override
    public void addVersion(String version, int versionType) {
        Map<String, Integer> cacheMap = redisCache.getCacheMap(Constants.VERSION_INFO);
        cacheMap.put(version, versionType);
    }

    @Override
    public void delVersion(String version) {
        Map<String, Integer> cacheMap = redisCache.getCacheMap(Constants.VERSION_INFO);
        cacheMap.remove(version);
    }

    @Override
    public boolean serverOpenByTime(GmServer server, int openType, boolean create) {
        String key = Constants.SERVER_OPEN_BY_TIME + server.getServerKeyId();
        if (!create) {
            if (redisCache.getCacheObject(key) == null) {
                return false;
            }
        }
        if (openType == 1) {
            long openTime = server.getOpenTime().getTime() / 1000L;
            long curr = System.currentTimeMillis() / 1000L;
            if (openTime > curr) {
                redisCache.setCacheObject(key, openTime, (int) (openTime - curr + 10), TimeUnit.SECONDS);

                if (scheduledFuture == null) {
                    scheduledFuture = scheduledExecutorService.scheduleWithFixedDelay(this::checkServerOpenTime, 10, 10, TimeUnit.SECONDS);
                }
            }
        } else {
            redisCache.deleteObject(key);
        }
        return true;
    }

    public void checkServerOpenTime() {
        Collection<String> keys = redisCache.keys(Constants.SERVER_OPEN_BY_TIME + "*");
        Boolean aBoolean = redisCache.getCacheObject(Constants.SERVER_OPEN_TIME_LOCK);
        if (aBoolean != null) {
            return;
        }
        redisCache.setCacheObject(Constants.SERVER_OPEN_TIME_LOCK, true);
        boolean expire = redisCache.expire(Constants.SERVER_OPEN_TIME_LOCK, 30);
        if (expire) {
            try {
                long now = System.currentTimeMillis() / 1000L;
                for (String key : keys) {
                    Long cacheObject = redisCache.getCacheObject(key);
                    if (cacheObject != null && now >= cacheObject) {
                        changeStatus();
                        String substring = key.substring(Constants.SERVER_OPEN_BY_TIME.length());
                        long serverId = Long.parseLong(substring);
                        GmServer server = selectServerById(serverId);
                        if (server != null) {
                            GmServer tmpServer = new GmServer();
                            tmpServer.setServerKeyId(server.getServerKeyId());
                            tmpServer.setServerStatus("1");
                            tmpServer.setShowOut("1");
                            updateServer(tmpServer);

                            updateGameServerOpenTime(server, cacheObject, true);
                            logger.info("open server:{} {} success", server.getServerKeyId(), server.getServerName());
                        }
                        redisCache.deleteObject(key);
                    }
                }
            } catch (Exception e) {
                throw new RuntimeException(e);
            } finally {
                redisCache.deleteObject(Constants.SERVER_OPEN_TIME_LOCK);
            }
        }
    }

    @Override
    public void updateGameServerOpenTime(GmServer server, long openTime, boolean closeOpen) {
        Map<String, Object> map = new HashMap<>();
        map.put("serverOpenTime", openTime);
        map.put("cmd", "UpdateServerOpenTime");
        map.put("onlyOnce", "false");
        map.put("closeOpen", closeOpen);
        String url = ParamParseUtils.makeURL(server.getInHost(), server.getInPort(), "script");
        try {
            ParamParseUtils.sendSyncTokenPost(url, map);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
    }

    private void changeStatus() {
        List<GmServer> list = selectServerList(new GmServer());
        for (GmServer gsrv : list) {
            if (Integer.parseInt(gsrv.getServerStatus()) == 1) {
                GmServer tmpServer = new GmServer();
                tmpServer.setServerKeyId(gsrv.getServerKeyId());
                tmpServer.setServerStatus(String.valueOf(2));
                updateServerStatus(tmpServer);
            }
        }
    }

    @Override
    public String stopServer(GmServer gmServer) {
        String homePath = gmServer.getHomePath();
        if (Strings.isEmpty(homePath)) {
            return null;
        }
        if (!homePath.endsWith("/")) {
            homePath += "/";
        }
        String bootName = PropertyConfig.getString("boot.name", "boot.sh");
        String stopCmd = "nohup " + homePath + "/" + bootName + " stop > /dev/null 2>&1 &";
        logger.info("执行服务器【{}】停服操作，命令：{}", gmServer.getServerId(), stopCmd);
        // nohup ./stop.sh > /dev/null 2>&1 &
        return executeShell(gmServer.getInHost(), stopCmd);
    }

    @Override
    public String startServer(GmServer gmServer) {
        String homePath = gmServer.getHomePath();
        if (Strings.isEmpty(homePath)) {
            return null;
        }
        if (!homePath.endsWith("/")) {
            homePath += "/";
        }
        String bootName = PropertyConfig.getString("boot.name", "boot.sh");
        String startCmd = "sh " + homePath + "/" + bootName + " start";
        logger.info("执行服务器【{}】启动操作，命令：{}", gmServer.getServerId(), startCmd);

        return executeShell(gmServer.getInHost(), startCmd);
    }

    @Override
    public long countServer() {
        return serverMapper.countServer();
    }

    @Override
    public String executeShell(String host, String... cmd) {
        try {
            return ExecuteShellUtil.execCmd(host, PropertyConfig.getString("inner.password", "123456"), cmd);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
        return null;
    }

    @Override
    public List<GmServer> gsrvs(String pid, String sid) {
        List<GmServer> gsrvs = new ArrayList<>();
        if (sid.length() == 8 && NumberUtil.isNumber(sid)) {
            gsrvs.add(selectServerById(Long.parseLong(sid)));
            return gsrvs;
        }
        gsrvs.addAll(selectServerAll());
        if (!pid.equals("-1")) {
            String[] pids = pid.split("\\|");
            if (pids.length > 0) {
                List<Long> pList = new ArrayList<>();
                Map<String, Object> options = new HashMap<>();
                for (String platformId : pids) {
                    pList.add(Long.parseLong(platformId));
                }
                gsrvs = gsrvs.stream().filter(v -> pList.contains(v.getPlatformId())).collect(Collectors.toList());
            }
        }
        if (!sid.equals("-1")) {
            String[] sids = sid.split("\\|");
            if (sids.length > 0) {
                for (String s : sids) {
                    gsrvs.removeIf(v -> v.getServerId() != Integer.parseInt(s));
                }
            }
        }
        return gsrvs;
    }

    @Override
    public void updateServersStatus(String pid, ServerState state, List<GmServer> servers, boolean newToHot) {
        boolean isNew = state == ServerState.NEW;
        if (isNew && newToHot) {
            GmPlatform gmPlatform = null;
            if (!servers.isEmpty()) {
                GmServer server = servers.get(0);
                if (server != null) {
                    pid = String.valueOf(server.getPlatformId());
                    gmPlatform = gmPlatformMapper.selectPlatformById(server.getPlatformId());
                }
            }

            List<GmServer> gsrvs1 = gsrvs(pid, "-1");
            for (GmServer gsrv : gsrvs1) {
                if (Integer.parseInt(gsrv.getServerStatus()) == ServerState.NEW.state) {
                    GmServer tmpServer = new GmServer();
                    tmpServer.setServerKeyId(gsrv.getServerKeyId());
                    tmpServer.setServerStatus(String.valueOf(ServerState.HOT.state));

                    if (gmPlatform != null && Integer.valueOf(1).equals(gmPlatform.getAutoRegisterSwitch()) && !Strings.isEmpty(gsrv.getInHost())) {
                        tmpServer.setRegisterSwitch(1);
                        String url = ParamParseUtils.makeURL(gsrv.getInHost(), gsrv.getInPort(), "script");
                        try {
                            Map<String, Object> map = new HashMap<>();
                            map.put("cmd", "ModifySysConfig");
                            map.put("onlyOnce", "false");
                            map.put("registerSwitch", tmpServer.getRegisterSwitch() == 0);
                            ParamParseUtils.sendSyncTokenPost(url, map);
                        } catch (Exception e) {
                            logger.error(e.getMessage(), e);
                        }
                    }
                    updateServerStatus(tmpServer);
                }
            }
        }
        for (GmServer gsrv : servers) {
            GmServer tmpServer = new GmServer();
            tmpServer.setServerKeyId(gsrv.getServerKeyId());
            tmpServer.setServerStatus(String.valueOf(state.state));
            if (isNew) {
                tmpServer.setShowOut("1");
                updateGameServerOpenTime(gsrv, gsrv.getOpenTime().getTime(), true);
            }
            updateServerStatus(tmpServer);
        }
    }

    @Override
    public void updateServersOpenTime(String pid, String sid, long openTime, int activeType) {
        Date date = new Date();
        if (String.valueOf(openTime).length() == 10) {
            openTime = openTime * 1000;
        }
        date.setTime(openTime);
        List<GmServer> gsrvs = gsrvs(pid, sid);
        for (GmServer gsrv : gsrvs) {
            GmServer tmpServer = new GmServer();
            tmpServer.setServerKeyId(gsrv.getServerKeyId());
            tmpServer.setOpenTime(date);
            if (activeType <= 1) {
                tmpServer.setRunStatus(String.valueOf(activeType));
            }
            updateServer(tmpServer);
        }
    }

    @Override
    public String importServer(List<GmServer> serverList, Boolean isUpdateSupport, String operName) {
        if (StringUtils.isNull(serverList) || serverList.size() == 0) {
            throw new ServiceException("导入用户数据不能为空！");
        }
        int successNum = 0;
        int failureNum = 0;
        StringBuilder successMsg = new StringBuilder();
        StringBuilder failureMsg = new StringBuilder();
        for (GmServer server : serverList) {
            try {
                // 验证是否存在这个用户
                GmServer s = serverMapper.selectServerById(server.getServerKeyId());
                if (StringUtils.isNull(s)) {
                    BeanValidators.validateWithException(validator, server);
                    server.setCreateBy(operName);
                    this.insertServer(server);
                    successNum++;
                    successMsg.append("<br/>" + successNum + "、服务器： " + server.getServerName() + " 导入成功");
                } else if (isUpdateSupport) {
                    BeanValidators.validateWithException(validator, server);
                    server.setUpdateBy(operName);
                    this.updateServer(server);
                    String serverName = StringUtils.isEmpty(server.getServerName()) ? s.getServerName() : server.getServerName();
                    successNum++;
                    successMsg.append("<br/>" + successNum + "、服务器 " + serverName + " 更新成功");
                } else {
                    failureNum++;
                    failureMsg.append("<br/>" + failureNum + "、服务器 " + server.getServerName() + " 已存在");
                }
            } catch (Exception e) {
                failureNum++;
                String msg = "<br/>" + failureNum + "、服务器 " + server.getServerName() + " 导入失败：";
                failureMsg.append(msg + e.getMessage());
                logger.error(msg, e);
            }
        }
        if (failureNum > 0) {
            failureMsg.insert(0, "很抱歉，导入失败！共 " + failureNum + " 条数据格式不正确，错误如下：");
            throw new ServiceException(failureMsg.toString());
        } else {
            successMsg.insert(0, "恭喜您，数据已全部导入成功！共 " + successNum + " 条，数据如下：");
        }
        return successMsg.toString();
    }
}
