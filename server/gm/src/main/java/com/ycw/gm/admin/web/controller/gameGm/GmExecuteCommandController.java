package com.ycw.gm.admin.web.controller.gameGm;

import com.alibaba.fastjson.JSONObject;
import com.ycw.core.cluster.property.PropertyConfig;
import com.ycw.gm.admin.domain.GmExecuteCommand;
import com.ycw.gm.admin.domain.GmServer;
import com.ycw.gm.admin.service.IGmExecuteCommandService;
import com.ycw.gm.admin.service.IServerService;
import com.ycw.gm.common.annotation.Log;
import com.ycw.gm.common.core.controller.BaseController;
import com.ycw.gm.common.core.domain.AjaxResult;
import com.ycw.gm.common.core.page.TableDataInfo;
import com.ycw.gm.common.enums.BusinessType;
import com.ycw.gm.common.utils.ParamParseUtils;
import com.ycw.gm.common.utils.StringUtils;
import com.ycw.gm.common.utils.poi.ExcelUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * gm命令行Controller
 * 
 * @author gamer
 * @date 2022-08-01
 */
@RestController
@RequestMapping("/gameGm/command")
public class GmExecuteCommandController extends BaseController
{
    @Autowired
    private IGmExecuteCommandService gmExecuteCommandService;

    @Autowired
    private IServerService serverService;

    /**
     * 查询gm命令行列表
     */
    @PreAuthorize("@ss.hasPermi('gm:command:list')")
    @GetMapping("/list")
    public TableDataInfo list(GmExecuteCommand gmExecuteCommand)
    {
        startPage();
        List<GmExecuteCommand> list = gmExecuteCommandService.selectGmExecuteCommandList(gmExecuteCommand);
        return getDataTable(list);
    }

    /**
     * 导出gm命令行列表
     */
    @PreAuthorize("@ss.hasPermi('gm:command:export')")
    @Log(title = "gm命令行", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, GmExecuteCommand gmExecuteCommand)
    {
        List<GmExecuteCommand> list = gmExecuteCommandService.selectGmExecuteCommandList(gmExecuteCommand);
        ExcelUtil<GmExecuteCommand> util = new ExcelUtil<GmExecuteCommand>(GmExecuteCommand.class);
        util.exportExcel(response, list, "gm命令行数据");
    }

    /**
     * 获取gm命令行详细信息
     */
    @PreAuthorize("@ss.hasPermi('gm:command:query')")
    @GetMapping(value = "/{id}")
    public AjaxResult getInfo(@PathVariable("id") Long id)
    {
        return AjaxResult.success(gmExecuteCommandService.selectGmExecuteCommandById(id));
    }

    /**
     * 新增gm命令行
     */
    @PreAuthorize("@ss.hasPermi('gm:command:add')")
    @Log(title = "gm命令行", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody GmExecuteCommand gmExecuteCommand)
    {
        gmExecuteCommand.setCreateBy(getUsername());
        return toAjax(gmExecuteCommandService.insertGmExecuteCommand(gmExecuteCommand));
    }

    /**
     * 修改gm命令行
     */
    @PreAuthorize("@ss.hasPermi('gm:command:edit')")
    @Log(title = "gm命令行", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody GmExecuteCommand gmExecuteCommand)
    {
        return toAjax(gmExecuteCommandService.updateGmExecuteCommand(gmExecuteCommand));
    }

    /**
     * 删除gm命令行
     */
    @PreAuthorize("@ss.hasPermi('gm:command:remove')")
    @Log(title = "gm命令行", businessType = BusinessType.DELETE)
	@DeleteMapping("/{ids}")
    public AjaxResult remove(@PathVariable Long[] ids)
    {
        return toAjax(gmExecuteCommandService.deleteGmExecuteCommandByIds(ids));
    }

    @PreAuthorize("@ss.hasPermi('gm:item:pass')")
    @PostMapping("/pass/{ids}")
    public AjaxResult passMail(@PathVariable Long[] ids, @Validated @RequestBody GmExecuteCommand gmExecuteCommand)
    {
        if (ids == null) {
            return AjaxResult.error("item not exists");
        }

        gmExecuteCommand.setUpdateBy(getUsername());

        if (gmExecuteCommand.getStatus() == 1) {
            if (!PropertyConfig.getBooleanValue("back.gm.open", true)) {
                return AjaxResult.error("GM close");
            }
            List<GmExecuteCommand> gmExecuteCommands = gmExecuteCommandService.selectGmExecuteCommandByIds(ids);
            for (GmExecuteCommand  executeCommand: gmExecuteCommands) {
                if (executeCommand.getStatus() != null && executeCommand.getStatus() > 0) {
                    continue;
                }

                JSONObject jsonObject = new JSONObject();
                jsonObject.put("cid", "-1");
                jsonObject.put("rid", executeCommand.getRoleId());
                jsonObject.put("cmd", "ExecuteGm");
                jsonObject.put("onlyOnce", "false");
                jsonObject.put("pid", "-1");
                jsonObject.put("gmCommand", executeCommand.getCommand());

                GmServer gmServer = serverService.selectServerById((long)executeCommand.getServerId());
                if (gmServer.getInPort() > 0) {
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

        return toAjax(gmExecuteCommandService.updateGmExecuteCommandStatus(gmExecuteCommand, ids));
    }
}
