package com.gm.server.admin.web.controller.gameGm;

import com.gm.server.admin.domain.GmServer;
import com.gm.server.admin.domain.RechargeDetail;
import com.gm.server.admin.domain.RechargeSum;
import com.gm.server.admin.service.IRechargeDetailService;
import com.gm.server.admin.service.IServerService;
import com.gm.server.common.annotation.Log;
import com.gm.server.common.core.controller.BaseController;
import com.gm.server.common.core.page.TableDataInfo;
import com.gm.server.common.enums.BusinessType;
import com.gm.server.common.utils.StringUtils;
import com.gm.server.common.utils.poi.ExcelUtil;
import com.gm.server.framework.datasource.DatabaseSourceKeyConst;
import com.gm.server.framework.datasource.DynamicDataSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.List;

/**
 * 充值订单记录Controller
 * 
 * @author gamer
 * @date 2022-07-27
 */
@RestController
@RequestMapping("/rechargelog/rechargesum")
public class RechargeSumController extends BaseController
{
    @Autowired
    private IRechargeDetailService rechargeSumService;

    @Autowired
    private IServerService serverService;

    @Autowired
    private DynamicDataSource dataSource;

    /**
     * 查询充值订单记录列表
     */
    @PreAuthorize("@ss.hasPermi('gm:rechargesum:list')")
    @GetMapping("/list")
    public TableDataInfo list()
    {
        int count = (int) serverService.countServer();
        startPage();
        List<GmServer> collect = serverService.selectServerList(new GmServer());
        List<RechargeSum> list = new ArrayList<>();
        for (GmServer server : collect) {
            String url = server.getDbLogUrl();
            if (StringUtils.isEmpty(url)) {
                list.add(toRechargeSum(server, "0"));
                continue;
            }

            try {
                dataSource.initOtherDataSource(DatabaseSourceKeyConst.getLogKey(server.getServerKeyId()), url, server.getDbUser(), server.getDbPass());

                Long sum = rechargeSumService.sumRecharge();
                list.add(toRechargeSum(server, String.valueOf(sum == null ? 0 : sum)));
            } catch (Exception e) {
                logger.error(e.getMessage(), e);
                list.add(toRechargeSum(server, "0"));
            } finally {
                dataSource.cleanDataSource();
            }
        }
        TableDataInfo dataTable = getDataTable(list);
        dataTable.setTotal(count);
        return dataTable;
    }

    private RechargeSum toRechargeSum(GmServer server, String sum) {
        RechargeSum rechargeSum = new RechargeSum();
        rechargeSum.setSumMoney(sum);
        rechargeSum.setServerKeyId(server.getServerKeyId());
        rechargeSum.setServerName(server.getServerName());
        return rechargeSum;
    }

    /**
     * 查询充值细节列表
     */
    @PreAuthorize("@ss.hasPermi('gm:rechargesum:detail')")
    @GetMapping("/detail")
    public TableDataInfo detail(RechargeDetail rechargeDetail, Long serverId)
    {
        if (serverId == null) {
            return new TableDataInfo();
        }

        GmServer server = serverService.selectServerById(serverId);
        if (server == null || server.getDbLogUrl() == null) {
            return getDataTableError();
        }

        List<RechargeDetail> list;
        try {
            dataSource.initOtherDataSource(DatabaseSourceKeyConst.getLogKey(server.getServerKeyId()), server.getDbLogUrl(), server.getDbUser(), server.getDbPass());
            startPage();
            list = rechargeSumService.selectAllRecharge(rechargeDetail);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            return getDataTableError();
        } finally {
            dataSource.cleanDataSource();
        }

        return getDataTable(list);
    }

    /**
     * 导出充值细节订单记录列表
     */
    @PreAuthorize("@ss.hasPermi('gm:rechargesum:export')")
    @Log(title = "充值订单记录", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response)
    {
        List<GmServer> list1 = serverService.selectServerAll();
        List<RechargeSum> list = new ArrayList<>();

        for (GmServer server : list1) {

            String url = server.getDbLogUrl();
            if (StringUtils.isEmpty(url)) {
                continue;
            }

            try {
                dataSource.initOtherDataSource(DatabaseSourceKeyConst.getLogKey(server.getServerKeyId()), url, server.getDbUser(), server.getDbPass());

//                String realName = ss[ss.length - 1];
                Long sum = rechargeSumService.sumRecharge();
                RechargeSum rechargeSum = new RechargeSum();
                rechargeSum.setSumMoney(String.valueOf(sum == null ? 0 : sum));
                rechargeSum.setServerKeyId(server.getServerKeyId());
                rechargeSum.setServerName(server.getServerName());
                list.add(rechargeSum);
            } catch (Exception e) {
                logger.error(e.getMessage(), e);
            } finally {
                dataSource.cleanDataSource();
            }
        }

        ExcelUtil<RechargeSum> util = new ExcelUtil<RechargeSum>(RechargeSum.class);
        util.exportExcel(response, list, "总充值记录数据");
    }

    /**
     * 导出充值细节订单记录列表
     */
    @PreAuthorize("@ss.hasPermi('gm:rechargesum:detail:export')")
    @Log(title = "充值订单详情记录", businessType = BusinessType.EXPORT)
    @PostMapping("/detail/export")
    public void exportDetail(HttpServletResponse response, Long serverId)
    {
        if (serverId == null) {
            return;
        }

        GmServer server = serverService.selectServerById(serverId);
        if (server == null || server.getDbLogUrl() == null) {
            return ;
        }

        List<RechargeDetail> list;
        try {
            dataSource.initOtherDataSource(DatabaseSourceKeyConst.getLogKey(server.getServerKeyId()), server.getDbLogUrl(), server.getDbUser(), server.getDbPass());
            startPage();
            list = rechargeSumService.selectAllRecharge(new RechargeDetail());
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            return;
        } finally {
            dataSource.cleanDataSource();
        }

        ExcelUtil<RechargeDetail> util = new ExcelUtil<RechargeDetail>(RechargeDetail.class);
        util.exportExcel(response, list, "充值详情记录数据");
    }

}
