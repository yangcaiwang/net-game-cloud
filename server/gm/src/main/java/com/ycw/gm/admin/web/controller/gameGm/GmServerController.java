package com.ycw.gm.admin.web.controller.gameGm;

import com.ycw.core.cluster.ClusterService;
import com.ycw.core.cluster.ClusterServiceImpl;
import com.ycw.core.cluster.entity.ServerEntity;
import com.ycw.core.cluster.property.PropertyConfig;
import com.ycw.core.cluster.template.*;
import com.ycw.core.internal.loader.service.ServiceContext;
import com.ycw.gm.admin.domain.GmServer;
import com.ycw.gm.admin.service.IServerService;
import com.ycw.gm.common.annotation.Log;
import com.ycw.gm.common.core.controller.BaseController;
import com.ycw.gm.common.core.domain.AjaxResult;
import com.ycw.gm.common.core.page.TableDataInfo;
import com.ycw.gm.common.enums.BusinessType;
import com.ycw.gm.common.utils.ParamParseUtils;
import com.ycw.gm.common.utils.StringUtils;
import com.ycw.gm.framework.datasource.DynamicDataSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 平台信息
 *
 * @author gamer
 */
@RestController
@RequestMapping("/gameGm/server")
public class GmServerController extends BaseController {
    @Autowired
    private IServerService serverService;

    @Autowired
    private DynamicDataSource dataSource;

    /**
     * 查询服务器列表
     */
    @PreAuthorize("@ss.hasPermi('gm:server:list')")
    @GetMapping("/list")
    public TableDataInfo list() {
        startPage();
        List<ServerEntity> list = serverService.selectServerAll();
        return getDataTable(list);
    }

    @GetMapping("/all")
    public TableDataInfo getAll() {
        List<ServerEntity> list = serverService.selectServerAll();
        return getDataTable(list);
    }

    /**
     * 修改保存服务器
     */
    @PreAuthorize("@ss.hasPermi('gm:server:edit')")
    @Log(title = "服务器管理", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@Validated @RequestBody ServerYmlTemplate serverYmlTemplate) {
        ClusterService clusterService = ServiceContext.getInstance().get(ClusterServiceImpl.class);
        ServerEntity serverEntity = clusterService.getServerEntity(serverYmlTemplate.getNode().getServerId());
        if (serverEntity == null) {
            return AjaxResult.error("修改服务器'" + serverEntity.getServerId() + "'失败，请联系管理员");
        }
        if (serverService.updateServer(serverYmlTemplate) > 0) {
            return AjaxResult.success();
        }
        return AjaxResult.error("修改服务器'" + serverEntity.getServerId() + "'失败，请联系管理员");
    }

    /**
     * 状态修改
     */
    @PreAuthorize("@ss.hasPermi('gm:server:edit')")
    @Log(title = "服务器管理", businessType = BusinessType.UPDATE)
    @PutMapping("/changeServerStatus")
    public AjaxResult changeStatus(@RequestBody GmServer server) {
        server.setUpdateBy(getUsername());
        return toAjax(serverService.updateServerStatus(server));
    }

    @PreAuthorize("@ss.hasPermi('gm:server:edit')")
    @Log(title = "服务器管理", businessType = BusinessType.UPDATE)
    @PutMapping("/changeServerTimeOpen")
    public AjaxResult changeServerTimeOpen(@RequestBody GmServer gs) {
        GmServer server = serverService.selectServerById(gs.getServerKeyId());
        if (server == null) {
            return AjaxResult.error("定时开启服务器失败");
        }
        if (server.getOpenTime().getTime() <= System.currentTimeMillis()) {
            return AjaxResult.error("定时开启服务器失败,开服时间不能小于当前时间");
        }
        return toAjax(serverService.serverOpenByTime(server, gs.getTimeOpen(), true));
    }

    @PreAuthorize("@ss.hasPermi('gm:server:kitout')")
    @Log(title = "服务器管理", businessType = BusinessType.UPDATE)
    @PostMapping("/kitout/{serverIds}")
    public AjaxResult kitOutAll(@PathVariable String[] serverIds, @RequestBody GmServer gs) {
        Long platformId = gs.getPlatformId();
        if (serverIds == null || Arrays.asList(serverIds).contains("-1")) {
            List<ServerEntity> list = serverService.selectServerAll();
            if (list.isEmpty()) {
                return AjaxResult.error("没有可操作的服务器");
            }
//            AsyncManager.me().execute(new TimerTask() {
//                @Override
//                public void run() {
//                    for (GmServer server : list) {
//                        if (platformId != null && !platformId.equals(server.getPlatformId())) {
//                            continue;
//                        }
//                        String url = ParamParseUtils.makeURL(server.getInHost(), server.getInPort(), "script");
//                        try {
//                            Map<String, Object> map = new HashMap<>();
//                            map.put("cmd", "OffLine");
//                            map.put("onlyOnce", "false");
//                            map.put("rid", "-1");
//                            ParamParseUtils.sendSyncTokenPost(url, map);
//                        } catch (Exception e) {
//                            logger.error(e.getMessage(), e);
//                        }
//                    }
//                }
//            });

        } else {
            for (String serverId : serverIds) {
                GmServer server = serverService.selectServerById(Long.parseLong(serverId));
                if (server == null) {
                    logger.info("服务器：{} 不存在", serverId);
                    continue;
                }
                if (platformId != null && !platformId.equals(server.getPlatformId())) {
                    continue;
                }
                String url = ParamParseUtils.makeURL(server.getInHost(), server.getInPort(), "script");
                try {
                    Map<String, Object> map = new HashMap<>();
                    map.put("cmd", "OffLine");
                    map.put("onlyOnce", "false");
                    map.put("rid", "-1");
                    ParamParseUtils.sendSyncTokenPost(url, map);
                } catch (Exception e) {
                    return AjaxResult.error("踢下线失败");
                }
            }
        }

        return AjaxResult.success();
    }

    @PreAuthorize("@ss.hasPermi('gm:server:edit')")
    @Log(title = "服务器管理", businessType = BusinessType.UPDATE)
    @PostMapping("/stop/{serverIds}")
    public AjaxResult stopServer(@PathVariable String[] serverIds, @RequestBody GmServer gs) {
        Long platformId = gs.getPlatformId();
        if (serverIds == null || Arrays.asList(serverIds).contains("-1")) {
            List<ServerEntity> list = serverService.selectServerAll();
            if (list.isEmpty()) {
                return AjaxResult.error("没有可操作的服务器");
            }
//            AsyncManager.me().execute(new TimerTask() {
//                @Override
//                public void run() {
//                    for (GmServer gmServer : list) {
//                        if (platformId != null && !platformId.equals(gmServer.getPlatformId())) {
//                            continue;
//                        }
//                        serverService.stopServer(gmServer);
//                    }
//                }
//            });
        } else {
            for (String serverId : serverIds) {
                GmServer server = serverService.selectServerById(Long.parseLong(serverId));
                if (server == null) {
                    logger.info("服务器：{} 不存在", serverId);
                    continue;
                }
                if (platformId != null && !platformId.equals(server.getPlatformId())) {
                    continue;
                }
                serverService.stopServer(server);
            }
        }
        return AjaxResult.success();
    }

    @PreAuthorize("@ss.hasPermi('gm:server:edit')")
    @Log(title = "服务器管理", businessType = BusinessType.UPDATE)
    @PostMapping("/start/{serverIds}")
    public AjaxResult startServer(@PathVariable String[] serverIds, @RequestBody GmServer gs) {
        Long platformId = gs.getPlatformId();
        if (serverIds == null || Arrays.asList(serverIds).contains("-1")) {
            List<ServerEntity> list = serverService.selectServerAll();
//            AsyncManager.me().execute(new TimerTask() {
//                @Override
//                public void run() {
//                    for (GmServer gmServer : list) {
//                        if (platformId != null && !platformId.equals(gmServer.getPlatformId())) {
//                            continue;
//                        }
//                        serverService.startServer(gmServer);
//                    }
//                }
//            });
        } else {
            for (String serverId : serverIds) {
                GmServer server = serverService.selectServerById(Long.parseLong(serverId));
                if (server == null) {
                    logger.info("服务器：{} 不存在", serverId);
                    continue;
                }
                if (platformId != null && !platformId.equals(server.getPlatformId())) {
                    continue;
                }
                serverService.startServer(server);
            }
        }

        return AjaxResult.success();
    }

    /**
     * 状态修改
     */
    @PreAuthorize("@ss.hasPermi('gm:server:edit')")
    @Log(title = "服务器管理", businessType = BusinessType.UPDATE)
    @PutMapping("/changeServerRegister")
    public AjaxResult changeServerRegister(@RequestBody GmServer server) {
        GmServer server1 = serverService.selectServerById(server.getServerKeyId());
        if (server1 == null) {
            return AjaxResult.error("server not exist");
        }
        String url = ParamParseUtils.makeURL(server1.getInHost(), server1.getInPort(), "script");
        try {
            Map<String, Object> map = new HashMap<>();
            map.put("cmd", "ModifySysConfig");
            map.put("onlyOnce", "false");
            map.put("registerSwitch", server.getRegisterSwitch() == 0);
            ParamParseUtils.sendSyncTokenPost(url, map);
        } catch (Exception e) {
            return AjaxResult.error("修改服务器状态失败");
        }
        server.setUpdateBy(getUsername());
        return toAjax(serverService.updateServerStatus(server));
    }

    /**
     * 更新服务器配置
     */
    @PreAuthorize("@ss.hasPermi('gm:server:edit')")
    @Log(title = "服务器管理", businessType = BusinessType.UPDATE)
    @PostMapping("/changeServerConfig/{serverIds}")
    public AjaxResult changeServerConfig(@PathVariable String[] serverIds) {
        int count = 0;
        for (String serverId : serverIds) {
            GmServer server1 = serverService.selectServerById(Long.parseLong(serverId));
            if (server1 == null) {
                logger.error("error server:{} not found", serverId);
                continue;
            }
            // 停服状态下才能操作
            if ("1".equals(server1.getRunStatus())) {
                continue;
            }
            // 开服了不能操作
            String serverStatus = server1.getServerStatus();
            if ("1".equals(serverStatus) || "2".equals(serverStatus) || "6".equals(serverStatus)) {
                continue;
            }

            if (StringUtils.isEmpty(server1.getHomePath())) {
                continue;
            }

            boolean b = syncServerConfig(server1);
            if (b) {
                count++;
            }
        }
        return toAjax(count);
    }

    private boolean syncServerConfig(GmServer server1) {
        StringBuilder sb = new StringBuilder();
        sb.append("sed -i 's/db_game_.*?/db_game_").append(server1.getServerId()).append("?/g' ").append(server1.getHomePath()).append("/resources/db_game.properties;");
        sb.append("sed -i 's/db_log_.*?/db_log_").append(server1.getServerId()).append("?/g' ").append(server1.getHomePath()).append("/resources/db_log.properties;");
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-ddHH:mm:ss");
        String format1 = dateFormat.format(server1.getOpenTime());
        sb.append("sed -i 's/server.open.time=.*/server.open.time=").append(format1).append("/g' ").append(server1.getHomePath()).append("/resources/server.cnf;");
        sb.append("sed -i 's/platform.id=[0-9]*/platform.id=").append(server1.getPlatformId()).append("/g' ").append(server1.getHomePath()).append("/resources/server.cnf;");
        sb.append("sed -i 's/server.id=[0-9]*/server.id=").append(server1.getServerId()).append("/g' ").append(server1.getHomePath()).append("/resources/server.cnf;");
        sb.append("sed -i 's/http.port=[0-9]*/http.port=").append(server1.getInPort()).append("/g' ").append(server1.getHomePath()).append("/resources/server.cnf;");
        sb.append("sed -i 's/tcp.ports=[0-9]*/tcp.ports=").append(server1.getClientPort()).append("/g' ").append(server1.getHomePath()).append("/resources/server.cnf;");
        sb.append("sed -i 's/^rpc.port=[0-9]*/rpc.port=").append(7000 + server1.getInPort() % 100).append("/g' ").append(server1.getHomePath()).append("/resources/server.cnf;");
        sb.append("sed -i 's/^rpc.host=.*/rpc.host=").append(server1.getInHost()).append("/g' ").append(server1.getHomePath()).append("/resources/server.cnf;");
        sb.append("sed -i 's/allow.reset.open.time=.*/allow.reset.open.time=true").append("/g' ").append(server1.getHomePath()).append("/resources/server.cnf;");

        logger.info("cmd:{}", sb);
        try {
            serverService.executeShell(server1.getInHost(), sb.toString());
            return true;
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
        return false;
    }

    /**
     * 部署服务器
     */
    @PreAuthorize("@ss.hasPermi('gm:server:edit')")
    @Log(title = "服务器管理", businessType = BusinessType.UPDATE)
    @PostMapping("/deployServer/{serverIds}")
    public AjaxResult deployServer(@PathVariable String[] serverIds) {
        int count = 0;
        for (String serverId : serverIds) {
            GmServer server1 = serverService.selectServerById(Long.parseLong(serverId));
            if (server1 == null) {
                logger.error("error server:{} not found", serverId);
                continue;
            }
            // 停服状态下才能操作
            if ("1".equals(server1.getRunStatus())) {
                continue;
            }
            // 开服了不能操作
            String serverStatus = server1.getServerStatus();
            if ("1".equals(serverStatus) || "2".equals(serverStatus) || "6".equals(serverStatus)) {
                continue;
            }

            if (StringUtils.isEmpty(server1.getHomePath())) {
                return AjaxResult.error("请先设置服务器路径");
            }

            StringBuilder sb = new StringBuilder();
            String baseServerPath = PropertyConfig.getString("server.game.base.path", "/home/game-server");
            sb.append("cp -r ").append(baseServerPath).append(" ").append(server1.getHomePath()).append(";");
            sb.append("rm -rf ").append(server1.getHomePath()).append("/libs;");
            sb.append("rm -rf ").append(server1.getHomePath()).append("/resources/template;");

            sb.append("ln -s ").append(baseServerPath).append("/libs").append(" ").append(server1.getHomePath()).append("/libs;");
            sb.append("ln -s ").append(baseServerPath).append("/resources/template").append(" ").append(server1.getHomePath()).append("/resources/template;");

            logger.info("cmd:{}", sb);
            try {
                serverService.executeShell(server1.getInHost(), sb.toString());
                count++;
                syncServerConfig(server1);
            } catch (Exception e) {
                return AjaxResult.error("修改服务器状态失败");
            }
        }
        return count > 0 ? toAjax(count) : AjaxResult.error("服务器操作失败");
    }

    @PreAuthorize("@ss.hasPermi('gm:server:merge')")
    @GetMapping("/mergelist")
    public TableDataInfo listMerge() {
        GmServer server = new GmServer();
        server.setServerStatus("6");
        List<ServerEntity> list = serverService.selectServerAll();
        return getDataTable(list);
    }

    /**
     * 合服处理
     */
    @PreAuthorize("@ss.hasPermi('gm:server:merge')")
    @PostMapping(value = "merge/{mainServerId}/{subServers}")
    public AjaxResult getInfo(@PathVariable("mainServerId") Long mainServerId, @PathVariable("subServers") String[] subServers) {
        System.err.println("合服:" + mainServerId);
        return AjaxResult.success();
    }
}
