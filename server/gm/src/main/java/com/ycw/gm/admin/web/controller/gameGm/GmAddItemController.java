//package com.ycw.gm.admin.web.controller.gameGm;
//
//import com.alibaba.fastjson.JSONObject;
//import com.ycw.gm.admin.domain.GmAddItem;
//import com.ycw.gm.admin.domain.GmServer;
//import com.ycw.gm.admin.domain.model.ItemPair;
//import com.ycw.gm.admin.service.IGmAddItemService;
//import com.ycw.gm.admin.service.IServerService;
//import com.ycw.gm.common.annotation.Log;
//import com.ycw.gm.common.core.controller.BaseController;
//import com.ycw.gm.common.core.domain.AjaxResult;
//import com.ycw.gm.common.core.page.TableDataInfo;
//import com.ycw.gm.common.enums.BusinessType;
//import com.ycw.gm.common.utils.ParamParseUtils;
//import com.ycw.gm.common.utils.StringUtils;
//import com.ycw.gm.common.utils.poi.ExcelUtil;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.security.access.prepost.PreAuthorize;
//import org.springframework.validation.annotation.Validated;
//import org.springframework.web.bind.annotation.*;
//
//import javax.servlet.http.HttpServletResponse;
//import java.util.ArrayList;
//import java.util.Arrays;
//import java.util.List;
//import java.util.stream.Collectors;
//
///**
// * 游戏管理  添加道具Controller
// *
// * @author gamer
// * @date 2022-07-26
// */
//@RestController
//@RequestMapping("/gameGm/item")
//public class GmAddItemController extends BaseController
//{
//    @Autowired
//    private IGmAddItemService gmAddItemService;
//
//    @Autowired
//    private IServerService serverService;
//
//    /**
//     * 查询游戏管理  添加道具列表
//     */
//    @PreAuthorize("@ss.hasPermi('gm:item:list')")
//    @GetMapping("/list")
//    public TableDataInfo list(GmAddItem gmAddItem)
//    {
//        startPage();
//        List<GmAddItem> list = gmAddItemService.selectGmAddItemList(gmAddItem);
//        return getDataTable(list);
//    }
//
//    /**
//     * 导出游戏管理  添加道具列表
//     */
//    @PreAuthorize("@ss.hasPermi('gm:item:export')")
//    @Log(title = "游戏管理  添加道具", businessType = BusinessType.EXPORT)
//    @PostMapping("/export")
//    public void export(HttpServletResponse response, GmAddItem gmAddItem)
//    {
//        List<GmAddItem> list = gmAddItemService.selectGmAddItemList(gmAddItem);
//        ExcelUtil<GmAddItem> util = new ExcelUtil<GmAddItem>(GmAddItem.class);
//        util.exportExcel(response, list, "游戏管理  添加道具数据");
//    }
//
//    /**
//     * 获取游戏管理  添加道具详细信息
//     */
//    @PreAuthorize("@ss.hasPermi('gm:item:query')")
//    @GetMapping(value = "/{id}")
//    public AjaxResult getInfo(@PathVariable("id") Long id)
//    {
//        GmAddItem addItem = gmAddItemService.selectGmAddItemById(id);
//        if (StringUtils.isNotEmpty(addItem.getServerId())) {
//            if ("-1".equals(addItem.getServerId())) {
//                addItem.setAllServer("ALL");
//                List<String> list = new ArrayList<>();
//                list.add("-1");
//                addItem.setServers(list);
//            } else {
//                String[] split = addItem.getServerId().split(",");
//                List<String> collect = Arrays.stream(split).collect(Collectors.toList());
//                addItem.setServers(collect);
//            }
//        } else {
//            addItem.setAllServer("");
//            List<String> list = new ArrayList<>();
//            addItem.setServers(list);
//        }
//        if (StringUtils.isNotEmpty(addItem.getItem())) {
//            String[] split = addItem.getItem().split("\\|");
//            if (split.length > 0 && split.length % 2 == 0) {
//                List<ItemPair> list = new ArrayList<>();
//                for (int i = 0; i < split.length; i += 2) {
//                    ItemPair itemPair = new ItemPair();
//                    itemPair.setRefId(Integer.parseInt(split[i]));
//                    itemPair.setNum(Integer.parseInt(split[i + 1]));
//                    list.add(itemPair);
//                }
//                addItem.setDynamicItem(list);
//            }
//        }
//        return AjaxResult.success(addItem);
//    }
//
//    /**
//     * 新增游戏管理  添加道具
//     */
//    @PreAuthorize("@ss.hasPermi('gm:item:add')")
//    @Log(title = "游戏管理  添加道具", businessType = BusinessType.INSERT)
//    @PostMapping
//    public AjaxResult add(@RequestBody GmAddItem gmAddItem)
//    {
//        if (StringUtils.isEmpty(gmAddItem.getServers())) {
//            return AjaxResult.error("no server select");
//        }
//        if ("ALL".equals(gmAddItem.getAllServer())) {
//            gmAddItem.setServerId("-1");
//        } else {
//            String collect = String.join(",", gmAddItem.getServers());
//            gmAddItem.setServerId(collect);
//        }
//        if (gmAddItem.getDynamicItem() != null) {
//            StringBuilder builder = new StringBuilder();
//            gmAddItem.getDynamicItem().forEach(v -> {
//                builder.append(v.getRefId()).append("|");
//                builder.append(v.getNum()).append("|");
//            });
//            gmAddItem.setItem(builder.toString());
//        }
//        gmAddItem.setCreateBy(getUsername());
//        return toAjax(gmAddItemService.insertGmAddItem(gmAddItem));
//    }
//
//    /**
//     * 修改游戏管理  添加道具
//     */
//    @PreAuthorize("@ss.hasPermi('gm:item:edit')")
//    @Log(title = "游戏管理  添加道具", businessType = BusinessType.UPDATE)
//    @PutMapping
//    public AjaxResult edit(@RequestBody GmAddItem gmAddItem)
//    {
//        if ("ALL".equals(gmAddItem.getAllServer())) {
//            gmAddItem.setServerId("-1");
//        } else {
//            if (StringUtils.isNotEmpty(gmAddItem.getServers())) {
//                String collect = String.join(",", gmAddItem.getServers());
//                gmAddItem.setServerId(collect);
//            }
//        }
//        if (gmAddItem.getDynamicItem() != null) {
//            StringBuilder builder = new StringBuilder();
//            gmAddItem.getDynamicItem().forEach(v -> {
//                builder.append(v.getRefId()).append("|");
//                builder.append(v.getNum()).append("|");
//            });
//            gmAddItem.setItem(builder.toString());
//        }
//        gmAddItem.setUpdateBy(getUsername());
//        return toAjax(gmAddItemService.updateGmAddItem(gmAddItem));
//    }
//
//    /**
//     * 删除游戏管理  添加道具
//     */
//    @PreAuthorize("@ss.hasPermi('gm:item:remove')")
//    @Log(title = "游戏管理  添加道具", businessType = BusinessType.DELETE)
//	@DeleteMapping("/{ids}")
//    public AjaxResult remove(@PathVariable Long[] ids)
//    {
//        return toAjax(gmAddItemService.deleteGmAddItemByIds(ids));
//    }
//
//    @PreAuthorize("@ss.hasPermi('gm:item:pass')")
//    @PostMapping("/pass/{ids}")
//    public AjaxResult passItem(@PathVariable Long[] ids, @Validated @RequestBody GmAddItem gmAddItem)
//    {
//        if (ids == null) {
//            return AjaxResult.error("item not exists");
//        }
//
//        gmAddItem.setUpdateBy(getUsername());
//
//        if (gmAddItem.getStatus() == 1) {
//            List<GmAddItem> gmAddItems = gmAddItemService.selectItemByIds(ids);
//            for (GmAddItem  addItem: gmAddItems) {
//                if (addItem.getStatus() != null && addItem.getStatus() > 0) {
//                    continue;
//                }
//
//                JSONObject jsonObject = new JSONObject();
//                jsonObject.put("cid", "-1");
//                jsonObject.put("rid", addItem.getRoleIds());
//                jsonObject.put("cmd", "AddItem");
//                jsonObject.put("onlyOnce", "false");
//                jsonObject.put("items", addItem.getItem() != null ? addItem.getItem() : "");
//
//                List<GmServer> list1 = null;
//                if (addItem.getServerId() == null || "-1".equals(addItem.getServerId())) {
//                    list1 = serverService.selectServerAll();
//                } else {
//                    String[] split = addItem.getServerId().split(",");
//
//                    Long[] list = Arrays.stream(split).map(Long::parseLong).toArray(Long[]::new);
//                    list1 = serverService.selectServerByIds(list);
//                }
//                for (GmServer gmServer : list1) {
//                    if (gmServer.getInPort() <= 0) {
//                        continue;
//                    }
//                    String url = ParamParseUtils.makeURL(gmServer.getInHost(), gmServer.getInPort(), "script");
//                    try {
//                        String result = ParamParseUtils.sendSyncTokenPost(url, jsonObject);
//                        if (!StringUtils.isEmpty(result)) {
//                            logger.error(result);
//                        }
//                    } catch (Exception e) {
//                        logger.error(e.toString());
//                    }
//                }
//            }
//        }
//
//        return toAjax(gmAddItemService.updateGmAddItemStatus(gmAddItem, ids));
//    }
//}
