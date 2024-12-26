package com.gm.server.admin.web.controller.gameGm;

import com.alibaba.fastjson.JSONObject;
import com.gm.server.admin.domain.GmBroadcast;
import com.gm.server.admin.domain.GmServer;
import com.gm.server.admin.service.IGmBroadcastService;
import com.gm.server.admin.service.IServerService;
import com.gm.server.common.annotation.Log;
import com.gm.server.common.core.controller.BaseController;
import com.gm.server.common.core.domain.AjaxResult;
import com.gm.server.common.core.page.TableDataInfo;
import com.gm.server.common.enums.BusinessType;
import com.gm.server.common.utils.ParamParseUtils;
import com.gm.server.common.utils.StringUtils;
import com.gm.server.common.utils.poi.ExcelUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 游戏管理  跑马灯Controller
 * 
 * @author gamer
 * @date 2022-08-01
 */
@RestController
@RequestMapping("/gameGm/broadcast")
public class GmBroadcastController extends BaseController
{
    @Autowired
    private IGmBroadcastService gmBroadcastService;

    @Autowired
    private IServerService serverService;

    /**
     * 查询游戏管理  跑马灯列表
     */
    @PreAuthorize("@ss.hasPermi('gm:broadcast:list')")
    @GetMapping("/list")
    public TableDataInfo list(GmBroadcast gmBroadcast)
    {
        startPage();
        List<GmBroadcast> list = gmBroadcastService.selectGmBroadcastList(gmBroadcast);
        return getDataTable(list);
    }

    /**
     * 导出游戏管理  跑马灯列表
     */
    @PreAuthorize("@ss.hasPermi('gm:broadcast:export')")
    @Log(title = "游戏管理  跑马灯", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, GmBroadcast gmBroadcast)
    {
        List<GmBroadcast> list = gmBroadcastService.selectGmBroadcastList(gmBroadcast);
        ExcelUtil<GmBroadcast> util = new ExcelUtil<GmBroadcast>(GmBroadcast.class);
        util.exportExcel(response, list, "游戏管理  跑马灯数据");
    }

    /**
     * 获取游戏管理  跑马灯详细信息
     */
    @PreAuthorize("@ss.hasPermi('gm:broadcast:query')")
    @GetMapping(value = "/{id}")
    public AjaxResult getInfo(@PathVariable("id") Long id)
    {
        GmBroadcast broadcast = gmBroadcastService.selectGmBroadcastById(id);
        if (StringUtils.isNotEmpty(broadcast.getServerId())) {
            if ("-1".equals(broadcast.getServerId())) {
                broadcast.setAllServer("ALL");
                List<String> list = new ArrayList<>();
                list.add("-1");
                broadcast.setServers(list);
            } else {
                String[] split = broadcast.getServerId().split(",");
                List<String> collect = Arrays.stream(split).collect(Collectors.toList());
                broadcast.setServers(collect);
            }
        } else {
            broadcast.setAllServer("");
            List<String> list = new ArrayList<>();
            broadcast.setServers(list);
        }
        return AjaxResult.success(broadcast);
    }

    /**
     * 新增游戏管理  跑马灯
     */
    @PreAuthorize("@ss.hasPermi('gm:broadcast:add')")
    @Log(title = "游戏管理  跑马灯", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody GmBroadcast gmBroadcast)
    {
        if (StringUtils.isEmpty(gmBroadcast.getServers())) {
            return AjaxResult.error("no server select");
        }
        if ("ALL".equals(gmBroadcast.getAllServer())) {
            gmBroadcast.setServerId("-1");
        } else {
            String collect = String.join(",", gmBroadcast.getServers());
            gmBroadcast.setServerId(collect);
        }
        gmBroadcast.setCreateBy(getUsername());
        return toAjax(gmBroadcastService.insertGmBroadcast(gmBroadcast));
    }

    /**
     * 修改游戏管理  跑马灯
     */
    @PreAuthorize("@ss.hasPermi('gm:broadcast:edit')")
    @Log(title = "游戏管理  跑马灯", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody GmBroadcast gmBroadcast)
    {
        if ("ALL".equals(gmBroadcast.getAllServer())) {
            gmBroadcast.setServerId("-1");
        } else {
            if (StringUtils.isNotEmpty(gmBroadcast.getServers())) {
                String collect = String.join(",", gmBroadcast.getServers());
                gmBroadcast.setServerId(collect);
            }
        }
        return toAjax(gmBroadcastService.updateGmBroadcast(gmBroadcast));
    }

    /**
     * 删除游戏管理  跑马灯
     */
    @PreAuthorize("@ss.hasPermi('gm:broadcast:remove')")
    @Log(title = "游戏管理  跑马灯", businessType = BusinessType.DELETE)
	@DeleteMapping("/{ids}")
    public AjaxResult remove(@PathVariable Long[] ids)
    {
        List<GmBroadcast> gmBroadcasts = gmBroadcastService.selectGmBroadcastByIds(ids);
        if (gmBroadcasts.stream().anyMatch(v -> v.getStatus() == 1)) {
            return AjaxResult.error("请先取消已发送的跑马灯方可删除");
        }
        return toAjax(gmBroadcastService.deleteGmBroadcastByIds(ids));
    }

    @PreAuthorize("@ss.hasPermi('gm:broadcast:pass')")
    @PostMapping("/pass/{ids}")
    public AjaxResult passBroadCast(@PathVariable Long[] ids, @Validated @RequestBody GmBroadcast gmBroadcast)
    {
        if (ids == null) {
            return AjaxResult.error("item not exists");
        }

        gmBroadcast.setUpdateBy(getUsername());

        if (gmBroadcast.getStatus() == 1 || gmBroadcast.getStatus() == 3) {
            List<GmBroadcast> gmBroadcasts = gmBroadcastService.selectGmBroadcastByIds(ids);
            for (GmBroadcast  gmBroadcast1: gmBroadcasts) {
                if (gmBroadcast.getStatus() == 1) {
                    if (gmBroadcast1.getStatus() != null && gmBroadcast1.getStatus() > 0) {
                        continue;
                    }
                } else {
                    if (gmBroadcast1.getStatus() == null || gmBroadcast1.getStatus() !=  1) {
                        continue;
                    }
                }

                JSONObject jsonObject = new JSONObject();
                jsonObject.put("content", gmBroadcast1.getBcContent());
                jsonObject.put("cmd", "BroadcastMessage");
                jsonObject.put("onlyOnce", "false");
                jsonObject.put("id", gmBroadcast1.getId());
                jsonObject.put("interval", gmBroadcast1.getInterval());
                jsonObject.put("times", gmBroadcast1.getTimes());
                if (gmBroadcast.getStatus() == 3) {
                    jsonObject.put("type", 2);
                } else {
                    jsonObject.put("type", 1);
                }

                List<GmServer> list1 = null;
                if (gmBroadcast1.getServerId() == null || "-1".equals(gmBroadcast1.getServerId())) {
                    list1 = serverService.selectServerAll();
                } else {
                    String[] split = gmBroadcast1.getServerId().split(",");

                    Long[] list = Arrays.stream(split).map(Long::parseLong).toArray(Long[]::new);
                    list1 = serverService.selectServerByIds(list);
                }
                for (GmServer gmServer : list1) {
                    if (gmServer.getInPort() <= 0) {
                        continue;
                    }
                    String url = ParamParseUtils.makeURL(gmServer.getInHost(), gmServer.getInPort(), "script");
                    try {
                        String result = ParamParseUtils.sendSyncTokenPost(url, jsonObject);
                        if (!StringUtils.isEmpty(result)) {
                            logger.error(result);
                        }
                    } catch (Exception e) {
                        logger.error(e.toString());
                    }
                }
            }
        }

        return toAjax(gmBroadcastService.updateGmBroadcastStatus(gmBroadcast, ids));
    }
}
