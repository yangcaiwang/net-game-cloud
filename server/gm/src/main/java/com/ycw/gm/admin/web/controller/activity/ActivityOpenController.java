package com.ycw.gm.admin.web.controller.activity;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.ycw.gm.admin.domain.ActivityOpen;
import com.ycw.gm.admin.domain.GmServer;
import com.ycw.gm.admin.service.IActivityOpenService;
import com.ycw.gm.admin.service.IServerService;
import com.ycw.gm.common.annotation.Log;
import com.ycw.gm.common.core.controller.BaseController;
import com.ycw.gm.common.core.domain.AjaxResult;
import com.ycw.gm.common.core.page.TableDataInfo;
import com.ycw.gm.common.enums.BusinessType;
import com.ycw.gm.common.utils.ParamParseUtils;
import com.ycw.gm.common.utils.StringUtils;
import com.ycw.gm.common.utils.poi.ExcelUtil;
import com.ycw.gm.framework.manager.AsyncManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.TimerTask;
import java.util.stream.Collectors;

/**
 * 运营活动开启时间Controller
 * 
 * @author gamer
 * @date 2022-07-26
 */
@RestController
@RequestMapping("/activity/open")
public class ActivityOpenController extends BaseController
{
    @Autowired
    private IActivityOpenService activityOpenService;

    @Autowired
    private IServerService serverService;

    /**
     * 查询运营活动开启时间列表
     */
    @PreAuthorize("@ss.hasPermi('gm:activity:list')")
    @GetMapping("/list")
    public TableDataInfo list(ActivityOpen activityOpen)
    {
        startPage();
        List<ActivityOpen> list = activityOpenService.selectActivityOpenList(activityOpen);
        return getDataTable(list);
    }

    /**
     * 导出运营活动开启时间列表
     */
    @PreAuthorize("@ss.hasPermi('gm:activity:export')")
    @Log(title = "运营活动开启时间", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, ActivityOpen activityOpen)
    {
        List<ActivityOpen> list = activityOpenService.selectActivityOpenList(activityOpen);
        ExcelUtil<ActivityOpen> util = new ExcelUtil<ActivityOpen>(ActivityOpen.class);
        util.exportExcel(response, list, "运营活动开启时间数据");
    }

    /**
     * 获取运营活动开启时间详细信息
     */
    @PreAuthorize("@ss.hasPermi('gm:activity:query')")
    @GetMapping(value = "/{id}")
    public AjaxResult getInfo(@PathVariable("id") Long id)
    {
        ActivityOpen activityOpen = activityOpenService.selectActivityOpenById(id);
        if (StringUtils.isNotEmpty(activityOpen.getServerId())) {
            if ("-1".equals(activityOpen.getServerId())) {
                activityOpen.setAllServer("ALL");
                List<String> list = new ArrayList<>();
                list.add("-1");
                activityOpen.setServers(list);
            } else {
                String[] split = activityOpen.getServerId().split(",");
                List<String> collect = Arrays.stream(split).collect(Collectors.toList());
                activityOpen.setServers(collect);
            }
        } else {
            activityOpen.setAllServer("");
            List<String> list = new ArrayList<>();
            activityOpen.setServers(list);
        }
        return AjaxResult.success(activityOpen);
    }

    /**
     * 新增运营活动开启时间
     */
    @PreAuthorize("@ss.hasPermi('gm:activity:add')")
    @Log(title = "运营活动开启时间", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody ActivityOpen activityOpen)
    {
        if (StringUtils.isEmpty(activityOpen.getServers())) {
            return AjaxResult.error("no server select");
        }
        if ("ALL".equals(activityOpen.getAllServer())) {
            activityOpen.setServerId("-1");
        } else {
            String collect = String.join(",", activityOpen.getServers());
            activityOpen.setServerId(collect);
        }

        String[] split = activityOpen.getActTime().split("\\|");
        for (String str : split) {
            if (!StringUtils.isNumeric(str)) {
                return AjaxResult.error("需要数据类型的活动时间");
            }
        }

        activityOpen.setCreateBy(getUsername());
        return toAjax(activityOpenService.insertActivityOpen(activityOpen));
    }

    /**
     * 修改运营活动开启时间
     */
    @PreAuthorize("@ss.hasPermi('gm:activity:edit')")
    @Log(title = "运营活动开启时间", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody ActivityOpen activityOpen)
    {
        if ("ALL".equals(activityOpen.getAllServer())) {
            activityOpen.setServerId("-1");
        } else {
            if (StringUtils.isNotEmpty(activityOpen.getServers())) {
                String collect = String.join(",", activityOpen.getServers());
                activityOpen.setServerId(collect);
            }
        }
        activityOpen.setUpdateBy(getUsername());
        return toAjax(activityOpenService.updateActivityOpen(activityOpen));
    }

    /**
     * 删除运营活动开启时间
     */
    @PreAuthorize("@ss.hasPermi('gm:activity:remove')")
    @Log(title = "运营活动开启时间", businessType = BusinessType.DELETE)
	@DeleteMapping("/{ids}")
    public AjaxResult remove(@PathVariable Long[] ids)
    {
        return toAjax(activityOpenService.deleteActivityOpenByIds(ids));
    }

    /**
     * 状态修改
     */
    @PreAuthorize("@ss.hasPermi('gm:activity:edit')")
    @Log(title = "运营活动开启时间", businessType = BusinessType.UPDATE)
    @PutMapping("/changeStatus")
    public AjaxResult changeStatus(@RequestBody ActivityOpen activityOpen)
    {
        ActivityOpen activityOpen1 = activityOpenService.selectActivityOpenById(activityOpen.getId());
        if (activityOpen1 == null) {
            return AjaxResult.error("活动不存在");
        }
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("cmd", "ActivityOpen");
        jsonObject.put("onlyOnce", "false");
        jsonObject.put("actId", activityOpen1.getActId());
        if (activityOpen.getStatus() == 1) {
            jsonObject.put("actType", activityOpen1.getActType());
            jsonObject.put("notOpenDay", activityOpen1.getNotOpenDay());
            if (!StringUtils.isEmpty(activityOpen1.getOpenChannel())) {
                jsonObject.put("openChannel", activityOpen1.getOpenChannel());
            }
            String[] split = activityOpen1.getActTime().split("\\|");
            int[] ints = Arrays.stream(split).mapToInt(Integer::parseInt).toArray();
            jsonObject.put("actTime", JSON.toJSONString(ints));
        } else {
            jsonObject.put("forceEnd", 1);
        }

        List<GmServer> list1 = null;
        if (activityOpen1.getServerId() == null || "-1".equals(activityOpen1.getServerId())) {
            list1 = serverService.selectServerAll();
        } else {
            String[] split = activityOpen1.getServerId().split(",");

            Long[] list = Arrays.stream(split).map(Long::parseLong).toArray(Long[]::new);
            list1 = serverService.selectServerByIds(list);
        }

        for (GmServer gmServer : list1) {
            if (gmServer.getInPort() <= 0) {
                continue;
            }
            String url = ParamParseUtils.makeURL(gmServer.getInHost(), gmServer.getInPort(), "script");
            AsyncManager.me().execute(new TimerTask() {
                @Override
                public void run() {
                    try {
                        String result = ParamParseUtils.sendSyncTokenPost(url, jsonObject);
                        if (!StringUtils.isEmpty(result)) {
                            logger.error(result);
                        }
                    } catch (Exception e) {
                        logger.error(e.toString());
                    }
                }
            });
        }

        activityOpen.setUpdateBy(getUsername());
        return toAjax(activityOpenService.updateActivityOpen(activityOpen));
    }
}
