package com.gm.server.admin.web.controller.gameGm;

import com.gm.server.admin.domain.GmPlatform;
import com.gm.server.admin.service.IGmWhiteListService;
import com.gm.server.admin.service.IPlatformService;
import com.gm.server.common.annotation.Log;
import com.gm.server.common.constant.UserConstants;
import com.gm.server.common.core.controller.BaseController;
import com.gm.server.common.core.domain.AjaxResult;
import com.gm.server.common.core.page.TableDataInfo;
import com.gm.server.common.enums.BusinessType;
import com.gm.server.common.utils.poi.ExcelUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * 平台信息
 *
 * @author gamer
 */
@RestController
@RequestMapping("/gameGm/platform")
public class GmPlatformController extends BaseController
{
    @Autowired
    private IPlatformService platformService;

    @Autowired
    private IGmWhiteListService whiteListService;

    @PreAuthorize("@ss.hasPermi('gm:platform:list')")
    @GetMapping("/list")
    public TableDataInfo list(GmPlatform platform)
    {
        startPage();
        List<GmPlatform> list = platformService.selectPlatformList(platform);
        return getDataTable(list);
    }

    @GetMapping("/all")
    public TableDataInfo listAll()
    {
        List<GmPlatform> list = platformService.selectPlatformAll();
        return getDataTable(list);
    }

    /**
     * 根据平台编号获取详细信息
     */
    @PreAuthorize("@ss.hasPermi('gm:platform:query')")
    @GetMapping(value = "/{pid}")
    public AjaxResult getInfo(@PathVariable Long pid)
    {
        return AjaxResult.success(platformService.selectPlatformById(pid));
    }
    @Log(title = "平台管理", businessType = BusinessType.EXPORT)
    @PreAuthorize("@ss.hasPermi('gm:platform:export')")
    @PostMapping("/export")
    public void export(HttpServletResponse response, GmPlatform platform)
    {
        List<GmPlatform> list = platformService.selectPlatformList(platform);
        ExcelUtil<GmPlatform> util = new ExcelUtil<GmPlatform>(GmPlatform.class);
        util.exportExcel(response, list, "平台数据");
    }

    /**
     * 新增平台
     */
    @PreAuthorize("@ss.hasPermi('gm:platform:add')")
    @Log(title = "平台管理", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@Validated @RequestBody GmPlatform platform)
    {
        if (UserConstants.NOT_UNIQUE.equals(platformService.checkPlatformUnique(platform)))
        {
            return AjaxResult.error("新增平台'" + platform.getPlatformId() + "'失败，平台编号已存在");
        }
        platform.setCreateBy(getUsername());
        int count = platformService.insertPlatform(platform);
        if (count >0 && platform.getWhiteListStatus() != null) {
            whiteListService.setBackstageIpWhiteFlag((int)platform.getPlatformId().longValue(), "1".equals(platform.getWhiteListStatus()));
        }
        return toAjax(count);

    }

    /**
     * 修改保存角色
     */
    @PreAuthorize("@ss.hasPermi('gm:platform:edit')")
    @Log(title = "平台管理", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@Validated @RequestBody GmPlatform platform)
    {
        platform.setUpdateBy(getUsername());

        if (platformService.updatePlatform(platform) > 0)
        {
            if (platform.getWhiteListStatus() != null) {
                whiteListService.setBackstageIpWhiteFlag((int)platform.getPlatformId().longValue(), "1".equals(platform.getWhiteListStatus()));
            }
            return AjaxResult.success();
        }
        return AjaxResult.error("修改平台'" + platform.getPlatformName() + "'失败，请联系管理员");
    }

    /**
     * 状态修改
     */
    @PreAuthorize("@ss.hasPermi('gm:platform:edit')")
    @Log(title = "平台管理", businessType = BusinessType.UPDATE)
    @PutMapping("/changeWhiteListStatus")
    public AjaxResult changeStatus(@RequestBody GmPlatform platform)
    {
        platform.setUpdateBy(getUsername());
        whiteListService.setBackstageIpWhiteFlag((int)platform.getPlatformId().longValue(), "1".equals(platform.getWhiteListStatus()));
        return toAjax(platformService.updateWhiteStatus(platform));
    }

    /**
     * 删除平台
     */
    @PreAuthorize("@ss.hasPermi('gm:platform:remove')")
    @Log(title = "平台管理", businessType = BusinessType.DELETE)
    @DeleteMapping("/{pids}")
    public AjaxResult remove(@PathVariable Long[] pids)
    {
        return toAjax(platformService.deletePlatformByIds(pids));
    }

    /**
     * 状态修改
     */
    @PreAuthorize("@ss.hasPermi('gm:platform:edit')")
    @Log(title = "平台管理", businessType = BusinessType.UPDATE)
    @PutMapping("/changeAutoRegisterSwitch")
    public AjaxResult changeAutoRegisterSwitch(@RequestBody GmPlatform platform)
    {
        platform.setUpdateBy(getUsername());
        return toAjax(platformService.updateWhiteStatus(platform));
    }

}
