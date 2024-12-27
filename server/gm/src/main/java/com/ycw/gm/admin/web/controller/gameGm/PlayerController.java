package com.ycw.gm.admin.web.controller.gameGm;

import com.alibaba.fastjson.JSONObject;
import com.ycw.gm.admin.domain.*;
import com.ycw.gm.admin.domain.vo.GeneralVo;
import com.ycw.gm.admin.domain.vo.ItemVo;
import com.ycw.gm.admin.service.IPlayerService;
import com.ycw.gm.admin.service.IServerService;
import com.ycw.gm.common.annotation.Log;
import com.ycw.gm.common.core.controller.BaseController;
import com.ycw.gm.common.core.domain.AjaxResult;
import com.ycw.gm.common.core.page.TableDataInfo;
import com.ycw.gm.common.enums.BusinessType;
import com.ycw.gm.common.utils.ParamParseUtils;
import com.ycw.gm.common.utils.ip.AddressUtils;
import com.ycw.gm.common.utils.poi.ExcelUtil;
import com.ycw.gm.framework.datasource.DatabaseSourceKeyConst;
import com.ycw.gm.framework.datasource.DynamicDataSource;
import org.apache.logging.log4j.util.Strings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 玩家信息
 * 
 * @author gamer
 */
@RestController
@RequestMapping("/gameGm/player")
public class PlayerController extends BaseController
{
    @Autowired
    private IPlayerService playerService;

    @Autowired
    private DynamicDataSource dataSource;

    @Autowired
    private IServerService serverService;

    @PreAuthorize("@ss.hasPermi('gm:player:list')")
    @GetMapping("/list")
    public TableDataInfo list(Player player, String serverId)
    {
        if (Strings.isEmpty(serverId)) {
            return new TableDataInfo();
        }

        GmServer server = serverService.selectServerById(Long.parseLong(serverId));
        if (server == null) {
            return getDataTableError();
        }

        List<Player> list;
        try {
            dataSource.initOtherDataSource(DatabaseSourceKeyConst.getGameKey(server.getServerKeyId()), server.getDbUrl(), server.getDbUser(), server.getDbPass());
            startPage();
            list = playerService.selectPlayerList(player);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            return getDataTableError();
        } finally {
            dataSource.cleanDataSource();
        }
        return getDataTable(list);
    }

    @PreAuthorize("@ss.hasPermi('gm:player:list')")
    @GetMapping("/genlist/{roleId}")
    public TableDataInfo listGeneral(@PathVariable("roleId") Long roleId, String serverId)
    {
        if (Strings.isEmpty(serverId)) {
            return new TableDataInfo();
        }

        GmServer server = serverService.selectServerById(Long.parseLong(serverId));
        if (server == null) {
            return getDataTableError();
        }

        List<GeneralVo> list;
        int allCount = 0;
        try {
            dataSource.initOtherDataSource(DatabaseSourceKeyConst.getGameKey(server.getServerKeyId()), server.getDbUrl(), server.getDbUser(), server.getDbPass());
            allCount = playerService.countGeneralVo(roleId);
            startPage();
            list = playerService.selectGeneralById(roleId, false);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            return getDataTableError();
        } finally {
            dataSource.cleanDataSource();
        }
        TableDataInfo dataTable = getDataTable(list);
        dataTable.setTotal(allCount);
        return dataTable;
    }

    @PreAuthorize("@ss.hasPermi('gm:player:list')")
    @GetMapping("/item/{roleId}")
    public TableDataInfo listItem(@PathVariable("roleId") Long roleId, String serverId)
    {
        if (Strings.isEmpty(serverId)) {
            return new TableDataInfo();
        }

        GmServer server = serverService.selectServerById(Long.parseLong(serverId));
        if (server == null) {
            return getDataTableError();
        }

        List<ItemVo> list;
        int countAll;
        try {
            dataSource.initOtherDataSource(DatabaseSourceKeyConst.getGameKey(server.getServerKeyId()), server.getDbUrl(), server.getDbUser(), server.getDbPass());
            countAll = playerService.countItem(roleId);
            startPage();
            list = playerService.selectItemById(roleId);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            return getDataTableError();
        } finally {
            dataSource.cleanDataSource();
        }
        TableDataInfo dataTable = getDataTable(list);
        dataTable.setTotal(countAll);
        return dataTable;
    }

    @PreAuthorize("@ss.hasPermi('gm:player:list')")
    @GetMapping("/itemlog/{roleId}")
    public TableDataInfo listItemLog(@PathVariable("roleId") Long roleId, String serverId, ItemLog itemLog)
    {
        if (Strings.isEmpty(serverId)) {
            return new TableDataInfo();
        }

        GmServer server = serverService.selectServerById(Long.parseLong(serverId));
        if (server == null) {
            return getDataTableError();
        }

        String url = server.getDbLogUrl();
        String[] ss = url.split("\\?")[0].split("\\/");
        String realName = ss[ss.length - 1];

        List<ItemLog> list;
        int countAll;
        try {
            dataSource.initOtherDataSource(DatabaseSourceKeyConst.getLogKey(server.getServerKeyId()), url, server.getDbUser(), server.getDbPass());
            startPage();
            countAll = playerService.countItemLogNum(realName, roleId, itemLog);
            list = playerService.selectItemLogById(realName, roleId, itemLog, false);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            return getDataTableError();
        } finally {
            dataSource.cleanDataSource();
        }
        TableDataInfo dataTable = getDataTable(list);
        dataTable.setTotal(countAll);
        return dataTable;
    }

    @PreAuthorize("@ss.hasPermi('gm:player:list')")
    @GetMapping("/powerlog/{roleId}")
    public TableDataInfo listPowerLog(@PathVariable("roleId") Long roleId, String serverId, PowerLog powerLog)
    {
        if (Strings.isEmpty(serverId)) {
            return new TableDataInfo();
        }

        GmServer server = serverService.selectServerById(Long.parseLong(serverId));
        if (server == null) {
            return getDataTableError();
        }

        String url = server.getDbLogUrl();
        String[] ss = url.split("\\?")[0].split("\\/");
        String realName = ss[ss.length - 1];

        List<PowerLog> list;
        int countAll;
        try {
            dataSource.initOtherDataSource(DatabaseSourceKeyConst.getLogKey(server.getServerKeyId()), url, server.getDbUser(), server.getDbPass());
            startPage();
            countAll = playerService.countPowerLogNum(realName, roleId, powerLog);
            list = playerService.selectPowerLogById(realName, roleId, powerLog, false);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            return getDataTableError();
        } finally {
            dataSource.cleanDataSource();
        }
        TableDataInfo dataTable = getDataTable(list);
        dataTable.setTotal(countAll);
        return dataTable;
    }

    @PreAuthorize("@ss.hasPermi('gm:player:list')")
    @GetMapping("/generallog/{roleId}")
    public TableDataInfo listGeneralLog(@PathVariable("roleId") Long roleId, String serverId, GeneralLog log)
    {
        if (Strings.isEmpty(serverId)) {
            return new TableDataInfo();
        }

        GmServer server = serverService.selectServerById(Long.parseLong(serverId));
        if (server == null) {
            return getDataTableError();
        }

        String url = server.getDbLogUrl();
        String[] ss = url.split("\\?")[0].split("\\/");
        String realName = ss[ss.length - 1];

        List<GeneralLog> list;
        int countAll;
        try {
            dataSource.initOtherDataSource(DatabaseSourceKeyConst.getLogKey(server.getServerKeyId()), url, server.getDbUser(), server.getDbPass());
            startPage();
            log.setRoleId(roleId);
            countAll = playerService.countGeneralLogNum(realName, log);
            list = playerService.selectGeneralLogById(realName, log, false);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            return getDataTableError();
        } finally {
            dataSource.cleanDataSource();
        }
        TableDataInfo dataTable = getDataTable(list);
        dataTable.setTotal(countAll);
        return dataTable;
    }

    @PreAuthorize("@ss.hasPermi('gm:player:list')")
    @GetMapping("/loginlog/{roleId}")
    public TableDataInfo listLoginLog(@PathVariable("roleId") Long roleId, String serverId, LoginLog log)
    {
        if (Strings.isEmpty(serverId)) {
            return new TableDataInfo();
        }

        GmServer server = serverService.selectServerById(Long.parseLong(serverId));
        if (server == null) {
            return getDataTableError();
        }

        String url = server.getDbLogUrl();
        String[] ss = url.split("\\?")[0].split("\\/");
        String realName = ss[ss.length - 1];

        List<LoginLog> list;
        int countAll;
        try {
            dataSource.initOtherDataSource(DatabaseSourceKeyConst.getLogKey(server.getServerKeyId()), url, server.getDbUser(), server.getDbPass());
            startPage();
            log.setRoleId(roleId);
            countAll = playerService.countLoginLogNum(realName, log);
            list = playerService.selectLoginLogById(realName, log, false);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            return getDataTableError();
        } finally {
            dataSource.cleanDataSource();
        }
        TableDataInfo dataTable = getDataTable(list);
        dataTable.setTotal(countAll);
        return dataTable;
    }

    /**
     * 根据玩家编号获取详细信息
     */
    @PreAuthorize("@ss.hasPermi('gm:player:query')")
    @GetMapping(value = "/{playerId}/{serverId}")
    public AjaxResult getInfo(@PathVariable("playerId") Long playerId, @PathVariable("serverId") String serverId)
    {
        GmServer server = serverService.selectServerById(Long.parseLong(serverId));
        if (server == null) {
            return AjaxResult.error("no server");
        }
        try {
            dataSource.initOtherDataSource(DatabaseSourceKeyConst.getGameKey(server.getServerKeyId()), server.getDbUrl(), server.getDbUser(), server.getDbPass());
            return AjaxResult.success(playerService.selectPlayerById(playerId));
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            return AjaxResult.error("Exception fail");
        } finally {
            dataSource.cleanDataSource();
        }
    }

    /**
     * 根据玩家编号获取基本详细信息
     */
    @PreAuthorize("@ss.hasPermi('gm:player:query')")
    @GetMapping(value = "/base/{playerId}/{serverId}")
    public AjaxResult getBaseInfo(@PathVariable("playerId") Long playerId, @PathVariable("serverId") String serverId)
    {
        GmServer server = serverService.selectServerById(Long.parseLong(serverId));
        if (server == null) {
            return AjaxResult.error("no server");
        }
        try {
            dataSource.initOtherDataSource(DatabaseSourceKeyConst.getGameKey(server.getServerKeyId()), server.getDbUrl(), server.getDbUser(), server.getDbPass());
            PlayerBase playerBase = playerService.selectPlayerBaseById(playerId);
            playerBase.setRegisterAddress(AddressUtils.getRealAddressByIP(playerBase.getRegisterIp()));
            playerBase.setLastLoginAddress(AddressUtils.getRealAddressByIP(playerBase.getLastLoginIp()));
            return AjaxResult.success(playerBase);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            return AjaxResult.error("exception fail");
        } finally {
            dataSource.cleanDataSource();
        }
    }
    @Log(title = "玩家管理", businessType = BusinessType.EXPORT)
    @PreAuthorize("@ss.hasPermi('gm:player:export')")
    @PostMapping("/export")
    public void export(HttpServletResponse response, Player player, String serverId)
    {
        GmServer server = serverService.selectServerById(Long.parseLong(serverId));
        if (server == null) {
            return ;
        }
        List<Player> list;
        try {
            dataSource.initOtherDataSource(DatabaseSourceKeyConst.getGameKey(server.getServerKeyId()), server.getDbUrl(), server.getDbUser(), server.getDbPass());
            list = playerService.selectPlayerList(player);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            return;
        } finally {
            dataSource.cleanDataSource();
        }
        ExcelUtil<Player> util = new ExcelUtil<Player>(Player.class);
        util.exportExcel(response, list, "玩家数据");
    }

    @Log(title = "玩家管理", businessType = BusinessType.EXPORT)
    @PreAuthorize("@ss.hasPermi('gm:player:general:export')")
    @PostMapping("/general/export/{roleId}")
    public void genExport(HttpServletResponse response,@PathVariable("roleId") Long roleId, String serverId)
    {
        GmServer server = serverService.selectServerById(Long.parseLong(serverId));
        if (server == null) {
            return ;
        }
        List<GeneralVo> list;
        try {
            dataSource.initOtherDataSource(DatabaseSourceKeyConst.getGameKey(server.getServerKeyId()), server.getDbUrl(), server.getDbUser(), server.getDbPass());
            list = playerService.selectGeneralById(roleId, true);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            return;
        } finally {
            dataSource.cleanDataSource();
        }
        ExcelUtil<GeneralVo> util = new ExcelUtil<GeneralVo>(GeneralVo.class);
        util.exportExcel(response, list, "玩家将领数据");
    }

    @Log(title = "玩家管理", businessType = BusinessType.EXPORT)
    @PreAuthorize("@ss.hasPermi('gm:player:export')")
    @PostMapping("/item/export/{roleId}")
    public void itemExport(HttpServletResponse response,@PathVariable("roleId") Long roleId, String serverId)
    {
        GmServer server = serverService.selectServerById(Long.parseLong(serverId));
        if (server == null) {
            return ;
        }
        List<ItemVo> list;
        try {
            dataSource.initOtherDataSource(DatabaseSourceKeyConst.getGameKey(server.getServerKeyId()), server.getDbUrl(), server.getDbUser(), server.getDbPass());
            list = playerService.selectItemByIdAll(roleId);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            return;
        } finally {
            dataSource.cleanDataSource();
        }
        ExcelUtil<ItemVo> util = new ExcelUtil<>(ItemVo.class);
        util.exportExcel(response, list, "玩家道具数据");
    }

    @Log(title = "玩家管理", businessType = BusinessType.EXPORT)
    @PreAuthorize("@ss.hasPermi('gm:player:export')")
    @PostMapping("/itemlog/export/{roleId}")
    public void itemLogExport(HttpServletResponse response,@PathVariable("roleId") Long roleId, String serverId, ItemLog itemLog)
    {
        GmServer server = serverService.selectServerById(Long.parseLong(serverId));
        if (server == null) {
            return ;
        }

        String url = server.getDbLogUrl();
        String[] ss = url.split("\\?")[0].split("\\/");
        String realName = ss[ss.length - 1];

        List<ItemLog> list;
        try {
            dataSource.initOtherDataSource(DatabaseSourceKeyConst.getLogKey(server.getServerKeyId()), url, server.getDbUser(), server.getDbPass());
            list = playerService.selectItemLogById(realName, roleId, itemLog, true);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            return;
        } finally {
            dataSource.cleanDataSource();
        }
        ExcelUtil<ItemLog> util = new ExcelUtil<>(ItemLog.class);
        util.exportExcel(response, list, "玩家道具日志数据");
    }

    @Log(title = "玩家管理", businessType = BusinessType.EXPORT)
    @PreAuthorize("@ss.hasPermi('gm:player:export')")
    @PostMapping("/powerlog/export/{roleId}")
    public void powerLogExport(HttpServletResponse response,@PathVariable("roleId") Long roleId, String serverId, PowerLog powerLog)
    {
        GmServer server = serverService.selectServerById(Long.parseLong(serverId));
        if (server == null) {
            return ;
        }

        String url = server.getDbLogUrl();
        String[] ss = url.split("\\?")[0].split("\\/");
        String realName = ss[ss.length - 1];

        List<PowerLog> list;
        try {
            dataSource.initOtherDataSource(DatabaseSourceKeyConst.getLogKey(server.getServerKeyId()), url, server.getDbUser(), server.getDbPass());
            list = playerService.selectPowerLogById(realName, roleId, powerLog, true);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            return;
        } finally {
            dataSource.cleanDataSource();
        }
        ExcelUtil<PowerLog> util = new ExcelUtil<>(PowerLog.class);
        util.exportExcel(response, list, "玩家战力日志数据");
    }

    @Log(title = "玩家管理", businessType = BusinessType.EXPORT)
    @PreAuthorize("@ss.hasPermi('gm:player:export')")
    @PostMapping("/loginlog/export/{roleId}")
    public void loginLogExport(HttpServletResponse response,@PathVariable("roleId") Long roleId, String serverId, LoginLog log)
    {
        GmServer server = serverService.selectServerById(Long.parseLong(serverId));
        if (server == null) {
            return ;
        }

        String url = server.getDbLogUrl();
        String[] ss = url.split("\\?")[0].split("\\/");
        String realName = ss[ss.length - 1];

        List<LoginLog> list;
        try {
            log.setRoleId(roleId);
            dataSource.initOtherDataSource(DatabaseSourceKeyConst.getLogKey(server.getServerKeyId()), url, server.getDbUser(), server.getDbPass());
            list = playerService.selectLoginLogById(realName, log, true);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            return;
        } finally {
            dataSource.cleanDataSource();
        }
        ExcelUtil<LoginLog> util = new ExcelUtil<>(LoginLog.class);
        util.exportExcel(response, list, "玩家登陆日志数据");
    }

    @Log(title = "玩家管理", businessType = BusinessType.EXPORT)
    @PreAuthorize("@ss.hasPermi('gm:player:export')")
    @PostMapping("/generallog/export/{roleId}")
    public void generalLogExport(HttpServletResponse response,@PathVariable("roleId") Long roleId, String serverId, GeneralLog log)
    {
        GmServer server = serverService.selectServerById(Long.parseLong(serverId));
        if (server == null) {
            return ;
        }

        String url = server.getDbLogUrl();
        String[] ss = url.split("\\?")[0].split("\\/");
        String realName = ss[ss.length - 1];

        List<GeneralLog> list;
        try {
            log.setRoleId(roleId);
            dataSource.initOtherDataSource(DatabaseSourceKeyConst.getLogKey(server.getServerKeyId()), url, server.getDbUser(), server.getDbPass());
            list = playerService.selectGeneralLogById(realName, log, true);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            return;
        } finally {
            dataSource.cleanDataSource();
        }
        ExcelUtil<GeneralLog> util = new ExcelUtil<>(GeneralLog.class);
        util.exportExcel(response, list, "玩家将领日志数据");
    }
    @PreAuthorize("@ss.hasPermi('gm:player:list')")
    @GetMapping("/mail/{playerId}")
    public TableDataInfo listMail(@PathVariable("playerId") Long playerId, String serverId, PlayerMail playerMail)
    {
        if (Strings.isEmpty(serverId)) {
            return new TableDataInfo();
        }

        GmServer server = serverService.selectServerById(Long.parseLong(serverId));
        if (server == null) {
            return getDataTableError();
        }

        List<PlayerMail> list;
        try {
            dataSource.initOtherDataSource(DatabaseSourceKeyConst.getGameKey(server.getServerKeyId()), server.getDbUrl(), server.getDbUser(), server.getDbPass());
            startPage();
            list = playerService.selectPlayerMailById(playerId, playerMail);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            return getDataTableError();
        } finally {
            dataSource.cleanDataSource();
        }
        return getDataTable(list);
    }

    @PreAuthorize("@ss.hasPermi('gm:player:remove')")
    @Log(title = "玩家管理", businessType = BusinessType.DELETE)
    @DeleteMapping("/del/{roleIds}")
    public AjaxResult remove(@PathVariable Long[] roleIds, String serverId)
    {
        GmServer server = serverService.selectServerById(Long.parseLong(serverId));
        if (server == null) {
            return AjaxResult.error("服务器不存在");
        }

        String url = ParamParseUtils.makeURL(server.getInHost(), server.getInPort(), "script");
        try {
            String s = "1";
            for (long roleId : roleIds) {
                Map<String, Object> map = new HashMap<>();
                map.put("cmd", "DeleteRole");
                map.put("onlyOnce", "false");
                map.put("rid", roleId);
                s = ParamParseUtils.sendSyncTokenPost(url, map);
            }
            if ("-1".equals(s)) {
                return AjaxResult.error("玩家已删除");
            }
        } catch (Exception e) {
            return AjaxResult.error("删除失败");
        }
        return AjaxResult.success();
    }

    @PreAuthorize("@ss.hasPermi('gm:player:remove')")
    @Log(title = "玩家管理", businessType = BusinessType.UPDATE)
    @PostMapping("/kitout/{roleIds}")
    public AjaxResult kitout(@PathVariable Long[] roleIds, String serverId)
    {
        GmServer server = serverService.selectServerById(Long.parseLong(serverId));
        if (server == null) {
            return AjaxResult.error("服务器不存在");
        }

        String url = ParamParseUtils.makeURL(server.getInHost(), server.getInPort(), "script");
        try {
            for (long roleId : roleIds) {
                Map<String, Object> map = new HashMap<>();
                map.put("cmd", "OffLine");
                map.put("onlyOnce", "false");
                map.put("rid", roleId);
                ParamParseUtils.sendSyncTokenPost(url, map);
            }
        } catch (Exception e) {
            return AjaxResult.error("踢下线失败");
        }
        return AjaxResult.success();
    }

    @PreAuthorize("@ss.hasPermi('gm:player:mailInvalid')")
    @PostMapping("/mailInvalid/{mails}")
    public AjaxResult mailInvalid(@PathVariable Long[] mails, String serverId)
    {
        GmServer server = serverService.selectServerById(Long.parseLong(serverId));
        if (server == null) {
            return AjaxResult.error("服务器不存在");
        }

        String url = ParamParseUtils.makeURL(server.getInHost(), server.getInPort(), "script");
        try {
            List<Long> collect = Arrays.stream(mails).collect(Collectors.toList());
            Map<String, Object> map = new HashMap<>();
            map.put("cmd", "MailInvalid");
            map.put("onlyOnce", "false");
            map.put("mailIds", JSONObject.toJSONString(collect));
            ParamParseUtils.sendSyncTokenPost(url, map);
        } catch (Exception e) {
            return AjaxResult.error("操作邮件失败");
        }
        return AjaxResult.success();
    }
}
