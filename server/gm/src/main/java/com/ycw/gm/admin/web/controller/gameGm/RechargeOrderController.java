package com.ycw.gm.admin.web.controller.gameGm;

import com.ycw.gm.admin.domain.RechargeOrder;
import com.ycw.gm.admin.service.IRechargeOrderService;
import com.ycw.gm.common.annotation.Log;
import com.ycw.gm.common.core.controller.BaseController;
import com.ycw.gm.common.core.page.TableDataInfo;
import com.ycw.gm.common.enums.BusinessType;
import com.ycw.gm.common.utils.poi.ExcelUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * 充值订单记录Controller
 * 
 * @author gamer
 * @date 2022-07-27
 */
@RestController
@RequestMapping("/rechargelog/rechargelog")
public class RechargeOrderController extends BaseController
{
    @Autowired
    private IRechargeOrderService rechargeOrderService;

    /**
     * 查询充值订单记录列表
     */
    @PreAuthorize("@ss.hasPermi('gm:rechargelog:list')")
    @GetMapping("/list")
    public TableDataInfo list(RechargeOrder rechargeOrder)
    {
        List<RechargeOrder> list = rechargeOrderService.selectRechargeOrderList(rechargeOrder);
        TableDataInfo dataTable = getDataTable(list);
        dataTable.setTotal(rechargeOrderService.countRechargeLogNum(rechargeOrder));
        return dataTable;
    }

    /**
     * 导出充值订单记录列表
     */
    @PreAuthorize("@ss.hasPermi('gm:rechargelog:export')")
    @Log(title = "充值订单记录", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, RechargeOrder rechargeOrder)
    {
        List<RechargeOrder> list = rechargeOrderService.selectAllRechargeOrderList(rechargeOrder);
        ExcelUtil<RechargeOrder> util = new ExcelUtil<RechargeOrder>(RechargeOrder.class);
        util.exportExcel(response, list, "充值订单记录数据");
    }

}
