package com.gm.server.admin.web.controller.gameGm;

import com.alibaba.fastjson.JSONObject;
import com.gm.server.admin.domain.GmRecharge;
import com.gm.server.admin.domain.GmServer;
import com.gm.server.admin.service.IGmRechargeService;
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
import java.util.List;

/**
 * GM充值Controller
 * 
 * @author gamer
 * @date 2022-08-02
 */
@RestController
@RequestMapping("/gameGm/recharge")
public class GmRechargeController extends BaseController
{
    @Autowired
    private IGmRechargeService gmRechargeService;

    @Autowired
    private IServerService serverService;

    /**
     * 查询GM充值列表
     */
    @PreAuthorize("@ss.hasPermi('gm:recharge:list')")
    @GetMapping("/list")
    public TableDataInfo list(GmRecharge gmRecharge)
    {
        startPage();
        List<GmRecharge> list = gmRechargeService.selectGmRechargeList(gmRecharge);
        return getDataTable(list);
    }

    /**
     * 导出GM充值列表
     */
    @PreAuthorize("@ss.hasPermi('gm:recharge:export')")
    @Log(title = "GM充值", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, GmRecharge gmRecharge)
    {
        List<GmRecharge> list = gmRechargeService.selectGmRechargeList(gmRecharge);
        ExcelUtil<GmRecharge> util = new ExcelUtil<GmRecharge>(GmRecharge.class);
        util.exportExcel(response, list, "GM充值数据");
    }

    /**
     * 获取GM充值详细信息
     */
    @PreAuthorize("@ss.hasPermi('gm:recharge:query')")
    @GetMapping(value = "/{id}")
    public AjaxResult getInfo(@PathVariable("id") Long id)
    {
        return AjaxResult.success(gmRechargeService.selectGmRechargeById(id));
    }

    /**
     * 新增GM充值
     */
    @PreAuthorize("@ss.hasPermi('gm:recharge:add')")
    @Log(title = "GM充值", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody GmRecharge gmRecharge)
    {
        return toAjax(gmRechargeService.insertGmRecharge(gmRecharge));
    }

    /**
     * 修改GM充值
     */
    @PreAuthorize("@ss.hasPermi('gm:recharge:edit')")
    @Log(title = "GM充值", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody GmRecharge gmRecharge)
    {
        return toAjax(gmRechargeService.updateGmRecharge(gmRecharge));
    }

    /**
     * 删除GM充值
     */
    @PreAuthorize("@ss.hasPermi('gm:recharge:remove')")
    @Log(title = "GM充值", businessType = BusinessType.DELETE)
	@DeleteMapping("/{ids}")
    public AjaxResult remove(@PathVariable Long[] ids)
    {
        return toAjax(gmRechargeService.deleteGmRechargeByIds(ids));
    }

    @PreAuthorize("@ss.hasPermi('gm:recharge:pass')")
    @PostMapping("/pass/{ids}")
    public AjaxResult passMail(@PathVariable Long[] ids, @Validated @RequestBody GmRecharge gmRecharge)
    {
        if (ids == null) {
            return AjaxResult.error("item not exists");
        }

        gmRecharge.setUpdateBy(getUsername());

        if (gmRecharge.getStatus() == 1) {
            List<GmRecharge> gmRecharges = gmRechargeService.selectGmRechargeByIds(ids);
            for (GmRecharge  recharge: gmRecharges) {
                if (recharge.getStatus() != null && recharge.getStatus() > 0) {
                    continue;
                }

                JSONObject jsonObject = new JSONObject();
                jsonObject.put("rid", recharge.getRoleIds());
                jsonObject.put("cmd", "GmRecharge");
                jsonObject.put("onlyOnce", "false");

                String rechargeIds = recharge.getRechargeIds();
                String[] split = rechargeIds.split("\\|");
                for (String s : split) {
                    String[] split1 = s.split(",");
                    if (split1.length < 2) {
                        continue;
                    }

                    int shopId = Integer.parseInt(split1[0]);
                    int count = Integer.parseInt(split1[1]);
                    jsonObject.put("goodsId", shopId);
                    for (int i = 0; i < count ; i++) {
                        GmServer gmServer = serverService.selectServerById((long)recharge.getServerId());
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


            }
        }

        return toAjax(gmRechargeService.updateGmRechargeStatus(gmRecharge, ids));
    }
}
