//package com.ycw.gm.admin.web.controller.gameGm;
//
//import com.alibaba.fastjson.JSONObject;
//import com.ycw.gm.admin.domain.GmMail;
//import com.ycw.gm.admin.domain.GmServer;
//import com.ycw.gm.admin.domain.model.ItemPair;
//import com.ycw.gm.admin.service.IGmMailService;
//import com.ycw.gm.admin.service.IServerService;
//import com.ycw.gm.common.annotation.Log;
//import com.ycw.gm.common.core.controller.BaseController;
//import com.ycw.gm.common.core.domain.AjaxResult;
//import com.ycw.gm.common.core.page.TableDataInfo;
//import com.ycw.gm.common.enums.BusinessType;
//import com.ycw.gm.common.utils.ParamParseUtils;
//import com.ycw.gm.common.utils.StringUtils;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.security.access.prepost.PreAuthorize;
//import org.springframework.validation.annotation.Validated;
//import org.springframework.web.bind.annotation.*;
//
//import java.util.Arrays;
//import java.util.Date;
//import java.util.List;
//import java.util.stream.Collectors;
//
///**
// * 邮件管理信息
// *
// * @author gamer
// */
//@RestController
//@RequestMapping("/gameGm/mail")
//public class GmMailController extends BaseController
//{
//    @Autowired
//    private IGmMailService mailService;
//
//    @Autowired
//    private IServerService serverService;
//
//    @PreAuthorize("@ss.hasPermi('gm:mail:list')")
//    @GetMapping("/list")
//    public TableDataInfo list(GmMail mail)
//    {
//        startPage();
//        return getDataTable(mailService.selectGmMailList(mail));
//    }
//
//    @GetMapping("/all")
//    public TableDataInfo getAll()
//    {
//        List<GmMail> list = mailService.selectMailAll();
//        return getDataTable(list);
//    }
//
//    @PreAuthorize("@ss.hasPermi('gm:mail:query')")
//    @GetMapping(value = "/{mailId}")
//    public AjaxResult getInfo(@PathVariable Long mailId)
//    {
//        return AjaxResult.success(mailService.selectGmMailById(mailId));
//    }
//
//    @PreAuthorize("@ss.hasPermi('gm:mail:add')")
//    @Log(title = "邮件管理", businessType = BusinessType.INSERT)
//    @PostMapping
//    public AjaxResult add(@Validated @RequestBody GmMail mail)
//    {
////        if (UserConstants.NOT_UNIQUE.equals(serverService.checkServerUnique(server)))
////        {
////            return AjaxResult.error("新增平台'" + server.getServerId() + "'失败，平台编号已存在");
////        }
//        if ("ALL".equals(mail.getAllServer())) {
//            mail.setServerList("-1");
//        } else {
//            String collect = mail.getServers().stream().collect(Collectors.joining(","));
//            mail.setServerList(collect);
//        }
//        if (mail.getDynamicItem() != null) {
//            StringBuilder builder = new StringBuilder();
//            for (ItemPair item : mail.getDynamicItem()) {
//                builder.append(item.getRefId()).append("|");
//                builder.append(item.getNum()).append("|");
//            }
//            mail.setItems(builder.substring(0, builder.length() - 1));
//        }
//        mail.setCreateBy(getUsername());
//        return toAjax(mailService.insertGmMail(mail));
//
//    }
//
//    @PreAuthorize("@ss.hasPermi('gm:mail:edit')")
//    @Log(title = "邮件管理", businessType = BusinessType.UPDATE)
//    @PutMapping
//    public AjaxResult edit(@Validated @RequestBody GmMail mail)
//    {
//        mail.setUpdateBy(getUsername());
//
//        if (mailService.updateGmMail(mail) > 0)
//        {
//            return AjaxResult.success();
//        }
//        return AjaxResult.error("修改邮件'" + mail.getId() + "'失败，请联系管理员");
//    }
//
//    @PreAuthorize("@ss.hasPermi('gm:mail:remove')")
//    @Log(title = "邮件管理", businessType = BusinessType.DELETE)
//    @DeleteMapping("/{sids}")
//    public AjaxResult remove(@PathVariable Long[] sids)
//    {
//        return toAjax(mailService.deleteMailByIds(sids));
//    }
//
//    @PreAuthorize("@ss.hasPermi('gm:mail:remove')")
//    @Log(title = "邮件管理", businessType = BusinessType.DELETE)
//    @DeleteMapping("/remove/{sid}")
//    public AjaxResult removeOne(@PathVariable Long sid)
//    {
//        return toAjax(mailService.deleteMailById(sid));
//    }
//
//    @PreAuthorize("@ss.hasPermi('gm:mail:pass')")
//    @PostMapping("/pass/{ids}")
//    public AjaxResult passMail(@PathVariable Long[] ids, @Validated @RequestBody GmMail mail)
//    {
//        if (ids == null) {
//            return AjaxResult.error("mail not exists");
//        }
////        if (StringUtils.isNotEmpty(gmMail.getMailStatus())) {
////            return AjaxResult.error("mail run pass");
////        }
//        mail.setUpdateBy(getUsername());
//        List<GmMail> gmMails = mailService.selectGmMailByIds(ids);
//        if ("1".equals(mail.getMailStatus())) {
//            for (GmMail gmMail : gmMails) {
//                if (StringUtils.isNotEmpty(gmMail.getMailStatus())) {
//                    continue;
//                }
//                StringBuilder roleSb = new StringBuilder();
//                if ("1".equals(gmMail.getSendType())) {// 指定玩家
//                    String s = gmMail.getTargetIds().replaceAll(",", "\\|");
//                    roleSb.append(s);
//                } else if ("2".equals(gmMail.getSendType())) {// 指定联盟
//                } else if ("3".equals(gmMail.getSendType())) {// 全服
//                    roleSb.append("-1");
//                }
//                long vTime = 0;
//                Date validTime = gmMail.getValidTime();
//                if (validTime == null || System.currentTimeMillis() > validTime.getTime()) {
//                } else {
//                    vTime = validTime.getTime();
//                }
//                JSONObject jsonObject = new JSONObject();
//                jsonObject.put("cid", "-1");
//                jsonObject.put("rid", roleSb.toString());
//                jsonObject.put("title", gmMail.getTitle());
//                jsonObject.put("content", gmMail.getContent());
//                jsonObject.put("validTime", vTime);
//                jsonObject.put("items", gmMail.getItems() != null ? gmMail.getItems() : "");
//                jsonObject.put("cmd", "SendMail");
//                jsonObject.put("onlyOnce", "false");
//
//                List<GmServer> list1 = null;
//                if (gmMail.getServerList() == null || "-1".equals(gmMail.getServerList())) {
//                    list1 = serverService.selectServerAll();
//                } else {
//                    String[] split = gmMail.getServerList().split(",");
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
//        return toAjax(mailService.updateGmMailStatus(mail, ids));
//    }
//}
