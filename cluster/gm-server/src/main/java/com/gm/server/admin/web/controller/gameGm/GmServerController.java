package com.gm.server.admin.web.controller.gameGm;

import com.common.module.cluster.enums.ServerState;
import com.common.module.cluster.property.PropertyConfig;
import com.gm.server.admin.domain.GmServer;
import com.gm.server.admin.service.IServerService;
import com.gm.server.common.annotation.Log;
import com.gm.server.common.constant.UserConstants;
import com.gm.server.common.core.controller.BaseController;
import com.gm.server.common.core.domain.AjaxResult;
import com.gm.server.common.core.page.TableDataInfo;
import com.gm.server.common.enums.BusinessType;
import com.gm.server.common.utils.ParamParseUtils;
import com.gm.server.common.utils.StringUtils;
import com.gm.server.common.utils.poi.ExcelUtil;
import com.gm.server.framework.datasource.DatabaseSourceKeyConst;
import com.gm.server.framework.datasource.DynamicDataSource;
import com.gm.server.framework.manager.AsyncManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * 平台信息
 *
 * @author gamer
 */
@RestController
@RequestMapping("/gameGm/server")
public class GmServerController extends BaseController
{
    @Autowired
    private IServerService serverService;

    @Autowired
    private DynamicDataSource dataSource;

    @PreAuthorize("@ss.hasPermi('gm:server:list')")
    @GetMapping("/list")
    public TableDataInfo list(GmServer server)
    {
        startPage();
        List<GmServer> list = serverService.selectServerList(server);
        return getDataTable(list);
    }

    @GetMapping("/all")
    public TableDataInfo getAll()
    {
        List<GmServer> list = serverService.selectServerAll();
        return getDataTable(list);
    }

    /**
     * 根据服务器编号获取详细信息
     */
    @PreAuthorize("@ss.hasPermi('gm:server:query')")
    @GetMapping(value = "/{serverKeyId}")
    public AjaxResult getInfo(@PathVariable Long serverKeyId)
    {
        return AjaxResult.success(serverService.selectServerById(serverKeyId));
    }

    @Log(title = "服务器管理", businessType = BusinessType.EXPORT)
    @PreAuthorize("@ss.hasPermi('gm:server:export')")
    @PostMapping("/export")
    public void export(HttpServletResponse response, GmServer server)
    {
        List<GmServer> list = serverService.selectServerList(server);
        ExcelUtil<GmServer> util = new ExcelUtil<GmServer>(GmServer.class);
        util.exportExcel(response, list, "服务器数据");
    }

    /**
     * 新增服务器
     */
    @PreAuthorize("@ss.hasPermi('gm:server:add')")
    @Log(title = "服务器管理", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@Validated @RequestBody GmServer server)
    {
        if (UserConstants.NOT_UNIQUE.equals(serverService.checkServerUnique(server)))
        {
            return AjaxResult.error("新增平台'" + server.getServerId() + "'失败，平台编号已存在");
        }
//        String serverKeyId = String.format("%d%d", server.getPlatformId(), server.getServerId());
        server.setCreateBy(getUsername());
//        server.setServerKeyId(Long.parseLong(serverKeyId));
        return toAjax(serverService.insertServer(server));

    }

    /**
     * 修改保存服务器
     */
    @PreAuthorize("@ss.hasPermi('gm:server:edit')")
    @Log(title = "服务器管理", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@Validated @RequestBody GmServer server)
    {
        GmServer server1 = serverService.selectServerById(server.getServerKeyId());
        if (server1 == null) {
            return AjaxResult.error("修改服务器'" + server.getServerName() + "'失败，请联系管理员");
        }
        server.setUpdateBy(getUsername());

        if (server.getDbUrl() != null && !server.getDbUrl().equals(server1.getDbUrl())) {
            dataSource.removeDataSourceByKey(DatabaseSourceKeyConst.getGameKey(server.getServerKeyId()));
        }
        if (server.getDbLogUrl() != null && !server.getDbLogUrl().equals(server1.getDbLogUrl())) {
            dataSource.removeDataSourceByKey(DatabaseSourceKeyConst.getLogKey(server.getServerKeyId()));
        }
        if (serverService.updateServer(server) > 0)
        {
            return AjaxResult.success();
        }
        return AjaxResult.error("修改服务器'" + server.getServerName() + "'失败，请联系管理员");
    }

    /**
     * 状态修改
     */
    @PreAuthorize("@ss.hasPermi('gm:server:edit')")
    @Log(title = "服务器管理", businessType = BusinessType.UPDATE)
    @PutMapping("/changeServerStatus")
    public AjaxResult changeStatus(@RequestBody GmServer server)
    {
        server.setUpdateBy(getUsername());
        return toAjax(serverService.updateServerStatus(server));
    }

    /**
     * 状态修改
     */
    @PreAuthorize("@ss.hasPermi('gm:server:edit')")
    @Log(title = "服务器管理", businessType = BusinessType.UPDATE)
    @PutMapping("/changeAllServerStatus")
    public AjaxResult changeAllServerStatus(@RequestBody GmServer server)
    {
        if (server.getServerStatus() == null) {
            return AjaxResult.error("status not found value");
        }
        int state = Integer.parseInt(server.getServerStatus());
        Long platformId = server.getPlatformId();
        List<GmServer> list = serverService.selectServerAll();
        boolean find = false;
        for (GmServer gs : list) {
            if (platformId != null && !platformId.equals(gs.getPlatformId())) {
                continue;
            }
            if (gs.getServerStatus() != null) {
                GmServer tmp = new GmServer();
                tmp.setServerKeyId(gs.getServerKeyId());
                tmp.setServerStatus(server.getServerStatus());
                if (state == ServerState.MAINTAIN.state) {
                    if (!ServerState.isMaintain(Integer.parseInt(gs.getServerStatus()))) {
                        serverService.updateServerStatus(tmp);
                    }
                } else if (!ServerState.isMaintain(state)) {
                    if (Integer.parseInt(gs.getServerStatus()) == ServerState.MAINTAIN.state) {
                        serverService.updateServerStatus(tmp);
                    }
                }
                find = true;
            }
        }
        if (!find) {
            return AjaxResult.error("没有可操作的服务器");
        }
        return AjaxResult.success();
    }

    /**
     * 删除服务器
     */
    @PreAuthorize("@ss.hasPermi('gm:server:remove')")
    @Log(title = "服务器管理", businessType = BusinessType.DELETE)
    @DeleteMapping("/{sids}")
    public AjaxResult remove(@PathVariable Long[] sids)
    {
        for (Long keyId : sids) {
            dataSource.removeDataSourceByKey(DatabaseSourceKeyConst.getLogKey(keyId));
            dataSource.removeDataSourceByKey(DatabaseSourceKeyConst.getGameKey(keyId));
        }
        return toAjax(serverService.deleteServerByIds(sids));
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
    public AjaxResult kitOutAll(@PathVariable String[] serverIds, @RequestBody GmServer gs)
    {
        Long platformId = gs.getPlatformId();
        if (serverIds == null || Arrays.asList(serverIds).contains("-1")) {
            List<GmServer> list = serverService.selectServerAll();
            if (list.isEmpty()) {
                return AjaxResult.error("没有可操作的服务器");
            }
            AsyncManager.me().execute(new TimerTask() {
                @Override
                public void run() {
                    for (GmServer server : list) {
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
                            logger.error(e.getMessage(), e);
                        }
                    }
                }
            });

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
            List<GmServer> list = serverService.selectServerAll();
            if (list.isEmpty()) {
                return AjaxResult.error("没有可操作的服务器");
            }
            AsyncManager.me().execute(new TimerTask() {
                @Override
                public void run() {
                    for (GmServer gmServer : list) {
                        if (platformId != null && !platformId.equals(gmServer.getPlatformId())) {
                            continue;
                        }
                        serverService.stopServer(gmServer);
                    }
                }
            });
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
            List<GmServer> list = serverService.selectServerAll();
            AsyncManager.me().execute(new TimerTask() {
                @Override
                public void run() {
                    for (GmServer gmServer : list) {
                        if (platformId != null && !platformId.equals(gmServer.getPlatformId())) {
                            continue;
                        }
                        serverService.startServer(gmServer);
                    }
                }
            });
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

    @PostMapping("/importTemplate")
    public void importTemplate(HttpServletResponse response)
    {
        ExcelUtil<GmServer> util = new ExcelUtil<GmServer>(GmServer.class);
        util.importTemplateExcel(response, "服务器数据");
    }

    @Log(title = "用户管理", businessType = BusinessType.IMPORT)
    @PreAuthorize("@ss.hasPermi('gm:server:import')")
    @PostMapping("/importData")
    public AjaxResult importData(MultipartFile file, boolean updateSupport) throws Exception
    {
        ExcelUtil<GmServer> util = new ExcelUtil<GmServer>(GmServer.class);
        List<GmServer> serverList = util.importExcel(file.getInputStream());
        String operName = getUsername();
        String message = serverService.importServer(serverList, updateSupport, operName);
        return AjaxResult.success(message);
    }

    /**
     * 状态修改
     */
    @PreAuthorize("@ss.hasPermi('gm:server:edit')")
    @Log(title = "服务器管理", businessType = BusinessType.UPDATE)
    @PutMapping("/changeServerRegister")
    public AjaxResult changeServerRegister(@RequestBody GmServer server)
    {
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
    public AjaxResult changeServerConfig(@PathVariable String[] serverIds)
    {
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
    public AjaxResult deployServer(@PathVariable String[] serverIds)
    {
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
    public TableDataInfo listMerge()
    {
        GmServer server = new GmServer();
        server.setServerStatus("6");
        List<GmServer> list = serverService.selectServerList(server);
        return getDataTable(list);
    }

    /**
     * 合服处理
     */
    @PreAuthorize("@ss.hasPermi('gm:server:merge')")
    @PostMapping(value = "merge/{mainServerId}/{subServers}")
    public AjaxResult getInfo(@PathVariable("mainServerId") Long mainServerId, @PathVariable("subServers") String[] subServers)
    {
        System.err.println("合服:" + mainServerId);
        return AjaxResult.success();
    }
}
