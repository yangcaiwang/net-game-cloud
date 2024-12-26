package com.gm.server.admin.web.controller.gameGm;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.gm.server.admin.domain.Gangs;
import com.gm.server.admin.domain.GangsMember;
import com.gm.server.admin.domain.GmServer;
import com.gm.server.admin.service.IGangsService;
import com.gm.server.admin.service.IServerService;
import com.gm.server.common.annotation.Log;
import com.gm.server.common.core.controller.BaseController;
import com.gm.server.common.core.domain.AjaxResult;
import com.gm.server.common.core.page.TableDataInfo;
import com.gm.server.common.enums.BusinessType;
import com.gm.server.common.utils.ParamParseUtils;
import com.gm.server.common.utils.StringUtils;
import com.gm.server.common.utils.poi.ExcelUtil;
import com.gm.server.framework.datasource.DatabaseSourceKeyConst;
import com.gm.server.framework.datasource.DynamicDataSource;
import org.apache.logging.log4j.util.Strings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 帮派信息
 * 
 * @author gamer
 */
@RestController
@RequestMapping("/gameGm/gangs")
public class GangsController extends BaseController
{
    @Autowired
    private IGangsService gangsService;

    @Autowired
    private DynamicDataSource dataSource;

    @Autowired
    private IServerService serverService;

    @PreAuthorize("@ss.hasPermi('gm:gangs:list')")
    @GetMapping("/list")
    public TableDataInfo list(Gangs gangs, String serverId)
    {
        if (Strings.isEmpty(serverId)) {
            return new TableDataInfo();
        }

        GmServer server = serverService.selectServerById(Long.parseLong(serverId));
        if (server == null) {
            return getDataTableError();
        }

        List<Gangs> list;
        try {
            dataSource.initOtherDataSource(DatabaseSourceKeyConst.getGameKey(server.getServerKeyId()), server.getDbUrl(), server.getDbUser(), server.getDbPass());
            startPage();
            list = gangsService.selectGangsList(gangs);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            return getDataTableError();
        } finally {
            dataSource.cleanDataSource();
        }
        return getDataTable(list);
    }

    @PreAuthorize("@ss.hasPermi('gm:gangs:player')")
    @GetMapping("/playerlist/{gangsId}")
    public TableDataInfo listPlayer(@PathVariable("gangsId") Long gangsId, String serverId)
    {
        if (Strings.isEmpty(serverId)) {
            return new TableDataInfo();
        }

        GmServer server = serverService.selectServerById(Long.parseLong(serverId));
        if (server == null) {
            return getDataTableError();
        }

        List<GangsMember> list = new ArrayList<>();
        try {
            dataSource.initOtherDataSource(DatabaseSourceKeyConst.getGameKey(server.getServerKeyId()), server.getDbUrl(), server.getDbUser(), server.getDbPass());
            Gangs gangs = gangsService.selectGangsById(gangsId);
            if (gangs != null) {
                String members = gangs.getMembers();
                Map map = JSON.parseObject(members, Map.class);

                List<Long> roleIds = new ArrayList<>();
                map.keySet().forEach(v -> {
                    roleIds.add((long)v);
                });

                List<GangsMember> gangsMember = gangsService.selectGangsMemberByIds(roleIds);
                if (gangsMember != null) {
                    list.addAll(gangsMember);
                }
            }
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            return getDataTableError();
        } finally {
            dataSource.cleanDataSource();
        }
        return getDataTable(list);
    }

    /**
     * 根据玩家编号获取详细信息
     */
    @PreAuthorize("@ss.hasPermi('gm:gangs:query')")
    @GetMapping(value = "/{gangsId}/{serverId}")
    public AjaxResult getInfo(@PathVariable("gangsId") Long gangsId, @PathVariable("serverId") String serverId)
    {
        GmServer server = serverService.selectServerById(Long.parseLong(serverId));
        if (server == null) {
            return AjaxResult.error("no server");
        }
        try {
            dataSource.initOtherDataSource(DatabaseSourceKeyConst.getGameKey(server.getServerKeyId()), server.getDbUrl(), server.getDbUser(), server.getDbPass());
            return AjaxResult.success(gangsService.selectGangsById(gangsId));
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            return AjaxResult.error("Exception Fail");
        } finally {
            dataSource.cleanDataSource();
        }
    }


    @Log(title = "帮派管理", businessType = BusinessType.EXPORT)
    @PreAuthorize("@ss.hasPermi('gm:gangs:export')")
    @PostMapping("/export")
    public void export(HttpServletResponse response, Gangs gangs, String serverId)
    {
        GmServer server = serverService.selectServerById(Long.parseLong(serverId));
        if (server == null) {
            return ;
        }
        List<Gangs> list;
        try {
            dataSource.initOtherDataSource(DatabaseSourceKeyConst.getGameKey(server.getServerKeyId()), server.getDbUrl(), server.getDbUser(), server.getDbPass());
            list = gangsService.selectGangsList(gangs);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            return ;
        } finally {
            dataSource.cleanDataSource();
        }
        ExcelUtil<Gangs> util = new ExcelUtil<>(Gangs.class);
        util.exportExcel(response, list, "帮派数据");
    }

    @PreAuthorize("@ss.hasPermi('gm:gangs:noticeedit')")
    @Log(title = "帮派管理 ", businessType = BusinessType.UPDATE)
    @PostMapping("/notice/{gangsId}")
    public AjaxResult modifyNotice(@PathVariable Long gangsId, String serverId, String notice)
    {
        if (Strings.isEmpty(serverId)) {
            return AjaxResult.error("server not exist");
        }
        GmServer server = serverService.selectServerById(Long.parseLong(serverId));
        if (server == null) {
            return AjaxResult.error("server not exist");
        }
        if (gangsId == null) {
            return AjaxResult.error("联盟错误");
        }

        JSONObject jsonObject = new JSONObject();
        jsonObject.put("cmd", "ModifyLegionNotice");
        jsonObject.put("onlyOnce", "false");
        jsonObject.put("rid", gangsId);
        jsonObject.put("notice", notice);
        String url = ParamParseUtils.makeURL(server.getInHost(), server.getInPort(), "script");
        try {
            String result = ParamParseUtils.sendSyncTokenPost(url, jsonObject);
            if (!StringUtils.isEmpty(result)) {
                logger.error(result);
            }
        } catch (Exception e) {
            logger.error(e.toString());
        }
        return AjaxResult.success();
    }
}
