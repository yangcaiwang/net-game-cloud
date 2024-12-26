package com.gm.server.admin.web.controller.gameGm;

import com.gm.server.admin.domain.ExceptionLog;
import com.gm.server.admin.service.IExceptionLogService;
import com.gm.server.common.annotation.Log;
import com.gm.server.common.core.controller.BaseController;
import com.gm.server.common.core.domain.AjaxResult;
import com.gm.server.common.core.page.TableDataInfo;
import com.gm.server.common.enums.BusinessType;
import com.gm.server.common.utils.poi.ExcelUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * 服务器异常记录Controller
 * 
 * @author gamer
 * @date 2022-08-01
 */
@RestController
@RequestMapping("/gameGm/exception")
public class ExceptionLogController extends BaseController
{
    @Autowired
    private IExceptionLogService exceptionLogService;

    /**
     * 查询服务器异常记录列表
     */
    @PreAuthorize("@ss.hasPermi('gm:exception:list')")
    @GetMapping("/list")
    public TableDataInfo list(ExceptionLog exceptionLog)
    {
        startPage();
        List<ExceptionLog> list = exceptionLogService.selectExceptionLogList(exceptionLog);
        return getDataTable(list);
    }

    /**
     * 导出服务器异常记录列表
     */
    @PreAuthorize("@ss.hasPermi('gm:exception:export')")
    @Log(title = "服务器异常记录", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, ExceptionLog exceptionLog)
    {
        List<ExceptionLog> list = exceptionLogService.selectExceptionLogList(exceptionLog);
        ExcelUtil<ExceptionLog> util = new ExcelUtil<ExceptionLog>(ExceptionLog.class);
        util.exportExcel(response, list, "服务器异常记录数据");
    }

    /**
     * 获取服务器异常记录详细信息
     */
    @PreAuthorize("@ss.hasPermi('gm:exception:query')")
    @GetMapping(value = "/{id}")
    public AjaxResult getInfo(@PathVariable("id") Long id)
    {
        return AjaxResult.success(exceptionLogService.selectExceptionLogById(id));
    }

    /**
     * 新增服务器异常记录
     */
    @PreAuthorize("@ss.hasPermi('gm:exception:add')")
    @Log(title = "服务器异常记录", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody ExceptionLog exceptionLog)
    {
        return toAjax(exceptionLogService.insertExceptionLog(exceptionLog));
    }

    /**
     * 修改服务器异常记录
     */
    @PreAuthorize("@ss.hasPermi('gm:exception:edit')")
    @Log(title = "服务器异常记录", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody ExceptionLog exceptionLog)
    {
        return toAjax(exceptionLogService.updateExceptionLog(exceptionLog));
    }

    /**
     * 删除服务器异常记录
     */
    @PreAuthorize("@ss.hasPermi('gm:exception:remove')")
    @Log(title = "服务器异常记录", businessType = BusinessType.DELETE)
	@DeleteMapping("/{ids}")
    public AjaxResult remove(@PathVariable Long[] ids)
    {
        return toAjax(exceptionLogService.deleteExceptionLogByIds(ids));
    }

    @PreAuthorize("@ss.hasPermi('gm:exception:remove')")
    @Log(title = "服务器异常记录", businessType = BusinessType.CLEAN)
    @DeleteMapping("/clean")
    public AjaxResult clean()
    {
        exceptionLogService.cleanExceptionLog();
        return AjaxResult.success();
    }
}
