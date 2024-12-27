package com.ycw.gm.admin.web.controller.gameGm;

import com.ycw.gm.admin.domain.GmWhiteList;
import com.ycw.gm.admin.service.IGmWhiteListService;
import com.ycw.gm.common.annotation.Log;
import com.ycw.gm.common.core.controller.BaseController;
import com.ycw.gm.common.core.domain.AjaxResult;
import com.ycw.gm.common.core.page.TableDataInfo;
import com.ycw.gm.common.enums.BusinessType;
import com.ycw.gm.common.utils.poi.ExcelUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * 白名单Controller
 * 
 * @author gamer
 * @date 2022-07-29
 */
@RestController
@RequestMapping("/gm/whitelist")
public class GmWhiteListController extends BaseController
{
    @Autowired
    private IGmWhiteListService gmWhiteListService;

    /**
     * 查询白名单列表
     */
    @PreAuthorize("@ss.hasPermi('gm:whitelist:list')")
    @GetMapping("/list")
    public TableDataInfo list(GmWhiteList gmWhiteList)
    {
        startPage();
        List<GmWhiteList> list = gmWhiteListService.selectGmWhiteListList(gmWhiteList);
        return getDataTable(list);
    }

    /**
     * 导出白名单列表
     */
    @PreAuthorize("@ss.hasPermi('gm:whitelist:export')")
    @Log(title = "白名单", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, GmWhiteList gmWhiteList)
    {
        List<GmWhiteList> list = gmWhiteListService.selectGmWhiteListList(gmWhiteList);
        ExcelUtil<GmWhiteList> util = new ExcelUtil<GmWhiteList>(GmWhiteList.class);
        util.exportExcel(response, list, "白名单数据");
    }

    /**
     * 获取白名单详细信息
     */
    @PreAuthorize("@ss.hasPermi('gm:whitelist:query')")
    @GetMapping(value = "/{id}")
    public AjaxResult getInfo(@PathVariable("id") Long id)
    {
        return AjaxResult.success(gmWhiteListService.selectGmWhiteListById(id));
    }

    /**
     * 新增白名单
     */
    @PreAuthorize("@ss.hasPermi('gm:whitelist:add')")
    @Log(title = "白名单", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody GmWhiteList gmWhiteList)
    {
        if (gmWhiteList.getWhiteType() == 1) {
            gmWhiteListService.addBackstageIpWhite(gmWhiteList.getPlatformId(), gmWhiteList.getArgs());
        } else if (gmWhiteList.getWhiteType() == 2) {
            gmWhiteListService.addBackstageAccountWhite(gmWhiteList.getPlatformId(), gmWhiteList.getArgs());
        }
        return toAjax(gmWhiteListService.insertGmWhiteList(gmWhiteList));
    }

    /**
     * 修改白名单
     */
    @PreAuthorize("@ss.hasPermi('gm:whitelist:edit')")
    @Log(title = "白名单", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody GmWhiteList gmWhiteList)
    {
        if (gmWhiteList.getWhiteType() == 1) {
            gmWhiteListService.addBackstageIpWhite(gmWhiteList.getPlatformId(), gmWhiteList.getArgs());
        } else if (gmWhiteList.getWhiteType() == 2) {
            gmWhiteListService.addBackstageAccountWhite(gmWhiteList.getPlatformId(), gmWhiteList.getArgs());
        }
        return toAjax(gmWhiteListService.updateGmWhiteList(gmWhiteList));
    }

    /**
     * 删除白名单
     */
    @PreAuthorize("@ss.hasPermi('gm:whitelist:remove')")
    @Log(title = "白名单", businessType = BusinessType.DELETE)
	@DeleteMapping("/{ids}")
    public AjaxResult remove(@PathVariable Long[] ids)
    {
        for (long id : ids) {
            GmWhiteList gmWhiteList = gmWhiteListService.selectGmWhiteListById(id);
            if (gmWhiteList != null) {
                if (gmWhiteList.getWhiteType() == 1) {
                    gmWhiteListService.removeBackstageIpWhite(gmWhiteList.getPlatformId(), gmWhiteList.getArgs());
                } else if (gmWhiteList.getWhiteType() == 2){
                    gmWhiteListService.removeBackstageAccountWhite(gmWhiteList.getPlatformId(), gmWhiteList.getArgs());
                }
            }
        }
        return toAjax(gmWhiteListService.deleteGmWhiteListByIds(ids));
    }
}
