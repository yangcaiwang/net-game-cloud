//package com.gm.server.admin.web.controller.gameGm;
//
//import cn.hutool.json.XML;
//import com.alibaba.fastjson.JSON;
//import com.alibaba.fastjson.JSONObject;
//import com.gm.server.admin.base.config.GameArgConfig;
//import com.gm.server.admin.domain.GmServer;
//import com.gm.server.admin.domain.RechargeOrder;
//import com.gm.server.admin.domain.model.SdkGiftSend;
//import com.gm.server.admin.domain.model.SdkRechargeOrder;
//import com.gm.server.admin.domain.model.SdkShopGoodsSend;
//import com.gm.server.admin.domain.model.SdkVipCustomerServiceInfo;
//import com.gm.server.admin.service.IRechargeOrderService;
//import com.gm.server.admin.service.IServerService;
//import com.gm.server.admin.web.servlet.constant.ConstForbidType;
//import com.gm.server.admin.web.servlet.constant.ConstKeyId;
//import com.gm.server.admin.web.servlet.constant.H2Commands;
//import com.gm.server.common.constant.Constants;
//import com.gm.server.common.core.controller.BaseController;
//import com.gm.server.common.core.redis.RedisCache;
//import com.gm.server.common.utils.ParamParseUtils;
//import com.gm.server.common.utils.StringUtils;
//import com.gm.server.common.utils.crypto.CryptoUtils;
//import com.gm.server.common.utils.ssl.RSAUtils;
//import org.apache.commons.lang3.ArrayUtils;
//import org.apache.logging.log4j.util.Strings;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.web.bind.annotation.GetMapping;
//import org.springframework.web.bind.annotation.PostMapping;
//import org.springframework.web.bind.annotation.RequestMapping;
//import org.springframework.web.bind.annotation.RestController;
//
//import javax.servlet.http.HttpServletRequest;
//import javax.servlet.http.HttpServletResponse;
//import java.io.IOException;
//import java.io.UnsupportedEncodingException;
//import java.net.URLDecoder;
//import java.net.URLEncoder;
//import java.nio.charset.StandardCharsets;
//import java.util.*;
//import java.util.concurrent.TimeUnit;
//import java.util.regex.Matcher;
//import java.util.regex.Pattern;
//
//@RestController("WYGamerRecharge")
//public class WYGamerRechargeController extends BaseController {
//
//    @Autowired
//    private RedisCache redisCache;
//
//    @Autowired
//    private IServerService serverService;
//
//    @Autowired
//    private IRechargeOrderService rechargeOrderService;
//
//    @PostMapping(value = "/WYGamerRechargeServlet")
//    public void rechargeServlet(HttpServletRequest req, HttpServletResponse resp) throws Exception {
//        try {
//            Map<String, String> map = parseRequestParam(req);
//
//            if (HotConfigUtils.isDebug()) {
//                logger.info("param:{}", map);
//            }
//
//            List<String> list = new ArrayList<>(map.keySet());
//            list.remove("Sign");
//            Collections.sort(list);
//            StringBuilder stringBuilder = new StringBuilder();
//            for (String key : list) {
//                stringBuilder.append(key).append("=").append(map.get(key)).append("&");
//            }
//            String orderId = map.get("OrderId");
//            String billNo = map.get("BillNo");
//            String userCode = map.get("UserCode");
//            String gameId = map.get("GameId");
//            String amount = map.get("Amount");
//            String productId = map.get("ProductId");
//            long roleId = Long.parseLong(map.get("RoleId"));
//            int serverId = Integer.parseInt(map.get("ServerId"));
//            int status = Integer.parseInt(map.get("Status"));
//            String extraInfo = map.get("ExtraInfo");
//            String sign = map.get("Sign");
//
//            boolean success = false;
//            String errMsg = "";
//
//            String payKey = null;
//            GameArgConfig gameArgConfig = ConfigMgr.getGameArgConfigByAppId(Integer.parseInt(gameId));
//            if (gameArgConfig != null) {
//                payKey = gameArgConfig.getPayKey();
//            } else {
//                payKey = HotConfigUtils.get("pay.key");
//            }
//            stringBuilder.append("PayKey").append("=").append(payKey);
//            String md5 = CryptoUtils.encodeMD5(stringBuilder.toString()).toLowerCase();
//
//
//            String format = String.format("%s%s", orderId, billNo);
//            int tabIndex = getTableIndex(format);
//            int platformId = 0;
//            if (!md5.equals(sign)) {
//                errMsg = "sign check error";
//            } else {
//                if (status == 0) {
//                    int giftUid = 0;
//
//                    try {
//                        JSONObject attachObj = JSONObject.parseObject(extraInfo);
//                        platformId = attachObj.getIntValue(ConstKeyId.PLATFORM_ID);
//                        if (attachObj.containsKey(ConstKeyId.ID)) {// 优先使用客户端透传参数中的id
//                            productId = attachObj.getString(ConstKeyId.ID);
//                        }
//                        if (attachObj.containsKey("giftUid")) {
//                            giftUid = Integer.parseInt(attachObj.getString("giftUid"));
//                        }
//                    } catch (Exception e) {
//                        String encode = URLDecoder.decode(extraInfo, "UTF-8");
////                        String parse = (String) JSONObject.parse(encode);
//                        JSONObject attachObj = JSONObject.parseObject(encode);
//                        platformId = attachObj.getIntValue(ConstKeyId.PLATFORM_ID);
//                        if (attachObj.containsKey(ConstKeyId.ID)) {// 优先使用客户端透传参数中的id
//                            productId = attachObj.getString(ConstKeyId.ID);
//                        }
//                        if (attachObj.containsKey("giftUid")) {
//                            giftUid = Integer.parseInt(attachObj.getString("giftUid"));
//                        }
//                    }
//                    // 平台是否关闭充值
//                    if (checkSdkRechargeSwitchClose(platformId)) {
//                        resp.getWriter().write("sdk recharge close !");
//                        return;
//                    }
//
//                    GmServer server = serverService.selectServerByServerIdAndPid(platformId, serverId);
//                    if (server == null) {
//                        errMsg = "Recharge,订单校验通过,未找到服务器ID,pid={},sid={}";
//                        logger.error(errMsg, platformId, serverId);
//                        resp.getWriter().write(URLEncoder.encode(errMsg,"UTF-8"));
//                        return;
//                    }
//
//                    String redisKey = getRedisKey(orderId);
//                    String cacheMap = redisCache.getCacheObject(redisKey);
//                    if (cacheMap == null) {
//                        String url = H2RPCUtils.makeURL(server.getInHost(), server.getInPort());
//                        String object = H2RPCUtils.call(url, H2Commands.SDK_RECHARGE, rechargeOrder(roleId, productId, tabIndex, orderId, userCode, amount, billNo, "", giftUid));
//                        JSONObject object1 = JSON.parseObject(object);
//                        SdkResult rechargeResult = SdkResult.toSdkResult(object1);
//                        if (rechargeResult.isSuccess()) {
//                            success = true;
//                            redisCache.setCacheObject(redisKey, orderId, HotConfigUtils.getIntValue("order.success.time.min", 10), TimeUnit.MINUTES);
//                        } else {
//                            errMsg = rechargeResult.getErrorMsg();
//                        }
//                    } else {
//                        success = true;
//                        errMsg = "order repeated";
//                    }
//                } else {
//                    errMsg = "error recall game server";
//                }
//            }
//
//            RechargeOrder rechargeOrder = toRechargeOrder(platformId, serverId, success ? 1 : -1, orderId, userCode,
//                    productId, "", gameId, errMsg, amount, roleId, billNo, "0", "", JSONObject.toJSONString(map));
//
//
//            String tabName = Constants.RECHARGE_NAME_PRE + tabIndex;
//            rechargeOrderService.insertRechargeOrder(tabName, rechargeOrder);
//
//            String result = success ? "success" : URLEncoder.encode(errMsg,"UTF-8");
//
//            logger.info("roleId:{}, payId:{}, orderId:{}, result:{} errMsg:{} billNo:{}", roleId, productId, orderId, result, errMsg, billNo);
//            if (!success) {
//                logger.error("userCode:{}, gameId:{}, orderId:{}, productId:{}", userCode, gameId, orderId, productId);
//                logger.error("sign:{}, mds:{}", sign, md5);
//            }
//            resp.getWriter().write(result);
//        } catch (Exception e) {
//            logger.error(e.getMessage(), e);
//            String result = "Error Occur";
//            resp.getWriter().write(result);
//        }
//
//    }
//
//    private String getRedisShopGoodsSendKey(String cno) {
//        return Constants.SHOP_GOODS_SEND + ":" + cno;
//    }
//    private String getRedisKey(String orderId) {
//        return Constants.RECHARGE_ORDER_LIST + ":" + orderId;
//    }
//
//    private String getRedisPreKey(String orderId) {
//        return Constants.RECHARGE_PRE_ORDER_LIST + ":" + orderId;
//    }
//
//    private int getTableIndex(String str) {
//        int code = str.hashCode();
//        code = code < 0 ? -code : code;
//        return code % 5 + 1;
//    }
//
//    private String vipCunstomerServiceInfo(long roleId, int cd, String aid, long time) {
//        SdkVipCustomerServiceInfo sdkVipCustomerServiceInfo = new SdkVipCustomerServiceInfo();
//        sdkVipCustomerServiceInfo.setRoleId(roleId);
//        sdkVipCustomerServiceInfo.setCd(cd);
//        sdkVipCustomerServiceInfo.setAid(aid);
//        sdkVipCustomerServiceInfo.setTime(time);
//        return sdkVipCustomerServiceInfo.toJSONObject().toJSONString();
//    }
//
//    private String shopGoodsSend(long roleId, String userId, float price, int rebate, String cno, String product, long timestamp) {
//        SdkShopGoodsSend sdkShopGoodsSend = new SdkShopGoodsSend();
//        sdkShopGoodsSend.setRoleId(roleId);
//        sdkShopGoodsSend.setUserId(userId);
//        try {
//            int iPrice = Math.round(price * 100f);
//            sdkShopGoodsSend.setPrice(iPrice);
//        } catch (Exception e) {
//            logger.error(e.getMessage(), e);
//        }
//        sdkShopGoodsSend.setRebate(rebate);
//        sdkShopGoodsSend.setCno(cno);
//        sdkShopGoodsSend.setProduct(product);
//        sdkShopGoodsSend.setTimestamp(timestamp);
//        return sdkShopGoodsSend.toJSONObject().toJSONString();
//    }
//
//    private String giftSend(long roleId, String userId, int giftId, long time) {
//        SdkGiftSend sdkGiftSend = new SdkGiftSend();
//        sdkGiftSend.setRoleId(roleId);
//        sdkGiftSend.setUserId(userId);
//        sdkGiftSend.setGiftId(giftId);
//        sdkGiftSend.setTime(time);
//
//        return sdkGiftSend.toJSONObject().toJSONString();
//    }
//
//    private String rechargeOrder(long roleId, String productId, long rechargeOrderLogId,
//                                 String orderId, String uid, String prices, String billNo, String channelId, int giftUid) {
//        return rechargeOrder(roleId, productId, rechargeOrderLogId, orderId, uid, prices, billNo, channelId, giftUid, 0);
//    }
//    private String rechargeOrder(long roleId, String productId, long rechargeOrderLogId,
//                                           String orderId, String uid, String prices, String billNo, String channelId, int giftUid, int rebateType) {
//        SdkRechargeOrder sdkRechargeOrder = new SdkRechargeOrder();
//        sdkRechargeOrder.setRoleId(roleId);
//        sdkRechargeOrder.setProductId(productId);
//        sdkRechargeOrder.setRechargeOrderLogId(rechargeOrderLogId);
//        sdkRechargeOrder.setOrderId(orderId);
//        sdkRechargeOrder.setUid(uid);
//        sdkRechargeOrder.setCreateTime(System.currentTimeMillis() / 1000);
//        sdkRechargeOrder.setBillNo(billNo);
//        sdkRechargeOrder.setChannelId(channelId);
//        sdkRechargeOrder.setGiftUid(giftUid);
//        sdkRechargeOrder.setRebateType(rebateType);
//        try {
//            int iPrice = Math.round(Float.parseFloat(prices) * 100f);
//            sdkRechargeOrder.setPrices(iPrice);
//        } catch (Exception e) {
//            logger.error(e.getMessage(), e);
//        }
//        return sdkRechargeOrder.toJSONObject().toJSONString();
//    }
//
//    private Map<String, String> parseRequestParam(HttpServletRequest req) throws Exception {
//        String parameter = ParamParseUtils.getUTF8Body(req);
//        Map<String, String> map = new HashMap<>();
//        if (!StringUtils.isEmpty(parameter)) {
//            map.putAll(parseRequestParam(parameter));
//        } else {
//            Map<String, String[]> parameterMap = req.getParameterMap();
//            parameterMap.forEach((k, v) -> {
//                map.put(k, v[0]);
//            });
//        }
//        return map;
//    }
//
//    private Map<String, String> parseRequestParamByParams(HttpServletRequest req) throws Exception {
//        Map<String, String[]> parameterMap = req.getParameterMap();
//        Map<String, String> map = new HashMap<>();
//        if (!parameterMap.isEmpty()) {
//            parameterMap.forEach((k, v) -> {
//                map.put(k, v[0]);
//            });
//            return map;
//        }
//        String parameter = ParamParseUtils.getUTF8Body(req);
//        if (!StringUtils.isEmpty(parameter)) {
//            map.putAll(parseRequestParam(parameter));
//        }
//        return map;
//    }
//
//    private Map<String, String> parseRequestParam(String parameter) throws Exception {
//        Map<String, String> map = new HashMap<>();
//        if (!StringUtils.isEmpty(parameter)) {
//            parameter = parameter.replaceAll("&quot;", "\"");
//            int index = parameter.indexOf("&");
//            if (index >= 0) {
//                String[] split1 = parameter.split("&");
//                for (String str : split1) {
//                    int spIndex = str.indexOf("=");
//                    if (spIndex <= 0) {
//                        continue;
//                    }
//                    String key = str.substring(0, spIndex);
//                    String value = str.substring(spIndex + 1);
//                    map.put(key, value);
////                    String[] split2 = str.split("=");
////                    if (split2.length > 1) {
////                    }
//                }
//            }
//        }
//        return map;
//    }
//
//    @PostMapping(value = "/yingHeRecharge")
//    public void yingHeRecharge(HttpServletRequest req, HttpServletResponse resp) throws Exception {
//        String res = "FAILED";
//        Map<String, String> map = null;
//        try {
//            map = parseRequestParam(req);
//            if (HotConfigUtils.isDebug()) {
//                logger.info("Recharge,参数parameterMap={},", map);
//            }
//
//            String orderId = map.get("order_id");
//            String appIdStr = map.get("app_id");
//            int appId = Integer.parseInt(appIdStr);
//            String ch_order_id = map.get("ch_order_id");
//            String ch_id = map.get("ch_id");
//            int order_status = Integer.parseInt(map.get("order_status"));
//            if (order_status != 2) {
//                resp.getWriter().write(res);
//                logger.info("Recharge,orderId={},order_status={},", orderId, order_status);
//                return;
//            }
//            String product_price = map.get("product_price");
//            String role_id = map.get("role_id");
//            String extraInfo = map.get("ext");
//            String usrCode = map.get("mem_id");
//            String sign = map.get("sign");
//
//            if (Strings.isEmpty(product_price) /*|| !StringUtils.isNumeric1(product_price)*/) {
//                resp.getWriter().write(res);
//                logger.error("Recharge,OrderId={},role_id={} amount == 0!!!", orderId, role_id);
//                return;
//            }
//            int amountTmp = (int) Float.parseFloat(product_price) * 100;
//            if (amountTmp <= 0) {
//                resp.getWriter().write(res);
//                logger.error("Recharge,OrderId={},RoleData={} amount == 0!!! empty", orderId, role_id);
//                return;
//            }
//
//            long roleId = Long.parseLong(role_id);
//            int serverId = 0;
//            boolean success = false;
//            String errMsg = "";
//            String productId = "0";
//            String gameId = HotConfigUtils.get("51gamer.back.gameId", "6");
//
//            int platformId = 0;
//            int giftUid = 0;
//            try {
//                JSONObject attachObj = JSONObject.parseObject(extraInfo);
//                platformId = attachObj.getIntValue(ConstKeyId.PLATFORM_ID);
//                if (attachObj.containsKey(ConstKeyId.ID)) {// 优先使用客户端透传参数中的id
//                    productId = attachObj.getString(ConstKeyId.ID);
//                }
//                if (attachObj.containsKey(ConstKeyId.SERVER_ID)) {
//                    serverId = Integer.parseInt(attachObj.getString(ConstKeyId.SERVER_ID));
//                }
//                if (attachObj.containsKey(ConstKeyId.ROLE_ID)) {
//                    roleId = Long.parseLong(attachObj.getString(ConstKeyId.ROLE_ID));
//                }
//                if (attachObj.containsKey("giftUid")) {
//                    giftUid = Integer.parseInt(attachObj.getString("giftUid"));
//                }
//            } catch (Exception e) {
//                logger.error("Recharge,额外数据出错,OrderId={},error={}", orderId, e.getMessage());
//                String encode = URLDecoder.decode(extraInfo, "UTF-8");
//                try {
////                    String parse = JSONObject.parse(encode);
//                    JSONObject attachObj = JSONObject.parseObject(encode);
//                    platformId = attachObj.getIntValue(ConstKeyId.PLATFORM_ID);
//                    if (attachObj.containsKey(ConstKeyId.ID)) {// 优先使用客户端透传参数中的id
//                        productId = attachObj.getString(ConstKeyId.ID);
//                    }
//                    if (attachObj.containsKey(ConstKeyId.SERVER_ID)) {
//                        serverId = Integer.parseInt(attachObj.getString(ConstKeyId.SERVER_ID));
//                    }
//                    if (attachObj.containsKey(ConstKeyId.ROLE_ID)) {
//                        roleId = Long.parseLong(attachObj.getString(ConstKeyId.ROLE_ID));
//                    }
//                    if (attachObj.containsKey("giftUid")) {
//                        giftUid = Integer.parseInt(attachObj.getString("giftUid"));
//                    }
//                } catch (Exception e1) {
//                    logger.error("Recharge,额外数据乱码,OrderId={},RoleData={},error={}", orderId, encode, e1.getMessage());
//                    resp.getWriter().write(res);
//                    return;
//                }
//            }
//            // 平台是否关闭充值
//            if (checkSdkRechargeSwitchClose(platformId)) {
//                resp.getWriter().write(res);
//                return;
//            }
//            if (productId.isEmpty() || "0".equals(productId)) {
//                logger.error("Recharge,道具ID为空,OrderId={},RoleData={}", orderId, extraInfo);
//            }
//            String gameKey = HotConfigUtils.get("game.key");
//            int tmpPlatformId = platformId;
//            GameArgConfig gameArgConfig = ConfigMgr.getGameArgConfigMap().values().stream().filter(v -> v.getPlatformId() == tmpPlatformId && v.getAppId() == appId).findFirst().orElse(null);
//            if (gameArgConfig != null) {
//                gameId = String.valueOf(gameArgConfig.getGameId());
//                gameKey = gameArgConfig.getGameKey();
//            }
//
//            String signParam = paramMap(map, gameKey);
//            if (HotConfigUtils.isDebug()) {
//                logger.info("Recharge,signParam={},", signParam);
//            }
//            if (Strings.isEmpty(usrCode)) {
//                usrCode = role_id;
//            }
//
//
//            String format = String.format("%s%s", orderId, ch_order_id);
//            int tabIndex = getTableIndex(format);
//
//            String mySign = CryptoUtils.encodeMD5(signParam);
//            if (!mySign.equals(sign)) {
//                errMsg = "sign not valid";
////                resp.getWriter().write(res);
//                logger.error("error!Recharge,OrderId={},sign={} mySign == {}", orderId, sign, mySign);
//            } else {
//                GmServer server = serverService.selectServerByServerIdAndPid(platformId, serverId);
//                if (server == null) {
//                    errMsg = "Recharge,订单校验通过,未找到服务器ID,pid={},sid={}";
//                    logger.error(errMsg, platformId, serverId);
//                } else {
//                    String redisKey = getRedisKey(orderId);
//                    String cacheMap = redisCache.getCacheObject(redisKey);
//                    if (cacheMap == null) {
//                        String url = H2RPCUtils.makeURL(server.getInHost(), server.getInPort());
//                        String jsonObject = H2RPCUtils.call(url, H2Commands.SDK_RECHARGE, rechargeOrder(roleId, productId, tabIndex, orderId, usrCode, product_price, ch_order_id, ch_id, giftUid));
//                        JSONObject object1 = JSON.parseObject(jsonObject);
//                        SdkResult rechargeResult = SdkResult.toSdkResult(object1);
//                        if (rechargeResult.isSuccess()) {
//                            success = true;
//                            res = "SUCCESS";
//                            redisCache.setCacheObject(redisKey, orderId, HotConfigUtils.getIntValue("order.success.time.min", 10), TimeUnit.MINUTES);
//                        } else {
//                            errMsg = rechargeResult.getErrorMsg();
//                            logger.error("Recharge,通知游戏服发放道具失败,OrderId={},RoleData={},errorMsg={}", orderId, extraInfo, errMsg);
//                        }
//                    } else {
//                        logger.error("Recharge,订单已存在,OrderId={},RoleData={}", orderId, extraInfo);
//                        res = "SUCCESS";
//                        resp.getWriter().write(res);
//                        return;
//                    }
//                }
//            }
//
//
//            RechargeOrder rechargeOrder = toRechargeOrder(platformId, serverId, success ? 1 : -1, orderId, usrCode,
//                    productId, ch_id, gameId, errMsg, product_price, roleId, ch_order_id, "0", appIdStr, JSONObject.toJSONString(map));
//
//            String tabName = Constants.RECHARGE_NAME_PRE + tabIndex;
//            rechargeOrderService.insertRechargeOrder(tabName, rechargeOrder);
//            logger.info("Recharge,充值结果：{},OrderId={},RoleData={}", res, orderId, extraInfo);
//            resp.getWriter().write(res);
//        } catch (Exception e) {
//            logger.error(e.getMessage(), e);
//            logger.error("param Info:{}", map);
//            resp.getWriter().write(res);
//        }
//    }
//
//    public static String paramMap(Map<String, String> map, String gameKey) {
//        Map<String, String> objectMap = new LinkedHashMap<>();
//        objectMap.put("app_id", map.get("app_id"));
//        objectMap.put("ch_id", map.get("ch_id"));
//        objectMap.put("ch_order_id", map.get("ch_order_id"));
//        objectMap.put("cp_order_id", map.get("cp_order_id"));
//        objectMap.put("ext", map.get("ext"));
//        objectMap.put("finish_time", map.get("finish_time"));
//        objectMap.put("mem_id", map.get("mem_id"));
//        objectMap.put("order_id", map.get("order_id"));
//        objectMap.put("order_status", map.get("order_status"));
//        objectMap.put("pay_time", map.get("pay_time"));
//        objectMap.put("product_id", map.get("product_id"));
//        objectMap.put("product_name", map.get("product_name"));
//        objectMap.put("product_price", map.get("product_price"));
//        objectMap.put("role_id", map.get("role_id"));
//        objectMap.put("server_id", map.get("server_id"));
//        objectMap.put("app_key", gameKey);
//        return paramString(objectMap,"sign");
//    }
//
//    @PostMapping(value = "/quickSdkRecharge")
//    public void yingHeRechargeQuickSdk(HttpServletRequest req, HttpServletResponse resp) throws Exception {
//        String res = "FAILED";
//        Map<String, String> map = new HashMap<>();
//        try {
//            String parameter = ParamParseUtils.getUTF8Body(req);
//            if (!StringUtils.isEmpty(parameter)) {
//                parameter = parameter.replaceAll("&quot;", "\"");
//                String[] split1 = StringUtils.parsedByJoiner("&", parameter);
//                for (String str : split1) {
//                    String[] split2 = StringUtils.parsedByJoiner("=", str);
//                    map.put(split2[0], split2[1]);
//                }
//            } else {
//                Map<String, String[]> parameterMap = req.getParameterMap();
//                parameterMap.forEach((k, v) -> {
//                    map.put(k, v[0]);
//                });
//            }
//            if (HotConfigUtils.isDebug()) {
//                logger.info("Recharge,参数parameterMap={},", map);
//            }
//
//            String nt_data = map.get("nt_data");
//            String md5Sign = map.get("md5Sign");
//            String sign = map.get("sign");
//
//            String payKey = HotConfigUtils.get("quick.pay.key");
//            String xml = decode(nt_data, payKey);
//            if (HotConfigUtils.isDebug()) {
//                logger.info("Recharge,解析得到XML字符串:{}", xml);
//            }
//
//            //解析xml得到一个Map
//            cn.hutool.json.JSONObject dataMap = XML.toJSONObject(xml);
//            cn.hutool.json.JSONObject message = getMessageDataByNode("message", dataMap);
//            if (message == null) {
//                resp.getWriter().write(res);
//                logger.error("message read error");
//                return;
//            }
//            if (HotConfigUtils.isDebug()) {
//                logger.info("Recharge,Message数据:{}", message);
//            }
//
//            String gameOrderId = message.getStr("game_order");
//            String orderId = message.getStr("order_no");
//            String isTest = message.getStr("is_test");
//            String uid = message.getStr("channel_uid");
//            String channel = message.getStr("channel");
//            String extraInfo = message.getStr("extras_params");
//            String time = message.getStr("pay_time");
//            String amount = message.getStr("amount");
//            String status = message.getStr("status");
//
//            if (Strings.isEmpty(amount)) {
//                resp.getWriter().write(res);
//                logger.error("Recharge,OrderId={},role_id={} amount == {}!!!", orderId, uid, amount);
//                return;
//            }
//            if (!"0".equals(status)) {
//                resp.getWriter().write(res);
//                logger.error("Recharge,OrderId={},role_id={} status error:{}!!!", orderId, uid, status);
//                return;
//            }
//            int amountTmp = (int) Float.parseFloat(amount) * 100;
//            if (amountTmp <= 0) {
//                resp.getWriter().write(res);
//                logger.error("Recharge,OrderId={},RoleData={} amount == 0!!! empty", orderId, uid);
//                return;
//            }
//
//            long roleId = 0L;
//            int serverId = 0;
//            boolean success = false;
//            String errMsg = "";
//            String productId = "0";
//
//            int platformId = 0;
//            int giftUid = 0;
//            try {
//                JSONObject attachObj = JSONObject.parseObject(extraInfo);
//                platformId = attachObj.getIntValue(ConstKeyId.PLATFORM_ID);
//                if (attachObj.containsKey(ConstKeyId.ID)) {// 优先使用客户端透传参数中的id
//                    productId = attachObj.getString(ConstKeyId.ID);
//                }
//                if (attachObj.containsKey(ConstKeyId.SERVER_ID)) {
//                    serverId = Integer.parseInt(attachObj.getString(ConstKeyId.SERVER_ID));
//                }
//                if (attachObj.containsKey(ConstKeyId.ROLE_ID)) {
//                    roleId = Long.parseLong(attachObj.getString(ConstKeyId.ROLE_ID));
//                }
//                if (attachObj.containsKey("giftUid")) {
//                    giftUid = Integer.parseInt(attachObj.getString("giftUid"));
//                }
//            } catch (Exception e) {
//                logger.error("Recharge,额外数据出错,OrderId={},error={}", orderId, e.getMessage());
//                String encode = URLDecoder.decode(extraInfo, "UTF-8");
//                try {
////                    String parse = JSONObject.parse(encode);
//                    JSONObject attachObj = JSONObject.parseObject(encode);
//                    platformId = attachObj.getIntValue(ConstKeyId.PLATFORM_ID);
//                    if (attachObj.containsKey(ConstKeyId.ID)) {// 优先使用客户端透传参数中的id
//                        productId = attachObj.getString(ConstKeyId.ID);
//                    }
//                    if (attachObj.containsKey(ConstKeyId.SERVER_ID)) {
//                        serverId = Integer.parseInt(attachObj.getString(ConstKeyId.SERVER_ID));
//                    }
//                    if (attachObj.containsKey(ConstKeyId.ROLE_ID)) {
//                        roleId = Long.parseLong(attachObj.getString(ConstKeyId.ROLE_ID));
//                    }
//                    if (attachObj.containsKey("giftUid")) {
//                        giftUid = Integer.parseInt(attachObj.getString("giftUid"));
//                    }
//                } catch (Exception e1) {
//                    logger.error("Recharge,额外数据乱码,OrderId={},RoleData={},error={}", orderId, encode, e1.getMessage());
//                    resp.getWriter().write(res);
//                    return;
//                }
//            }
//            // 平台是否关闭充值
//            if (checkSdkRechargeSwitchClose(platformId)) {
//                resp.getWriter().write(res);
//                return;
//            }
//            if (productId.isEmpty() || "0".equals(productId)) {
//                logger.error("Recharge,道具ID为空,OrderId={},RoleData={}", orderId, extraInfo);
//            }
//
//            String format = String.format("%s%s", orderId, gameOrderId);
//            int tabIndex = getTableIndex(format);
//
//            int finalPid = platformId;
//            GameArgConfig gameArgConfig = ConfigMgr.getGameArgConfigMap().values().stream().filter(v -> finalPid == v.getPlatformId() ).findFirst().orElse(null);
//            if (gameArgConfig == null) {
//                logger.error("充值失败，没有对应的参数配置：platform:{} server:{} roleId:{}", platformId, serverId, roleId);
//                resp.getWriter().write(res);
//                return;
//            }
//            String md5Key = gameArgConfig.getPayKey();
//            String md5 = CryptoUtils.encodeMD5(nt_data + sign + md5Key);
//            if (!md5.equals(md5Sign)) {
//                errMsg = "sign not valid";
////                resp.getWriter().write(res);
//                logger.error("error!Recharge,OrderId={},sign={} mySign == {}", orderId, sign, md5);
//            } else {
//                GmServer server = serverService.selectServerByServerIdAndPid(platformId, serverId);
//                if (server == null) {
//                    errMsg = "Recharge,订单校验通过,未找到服务器ID,pid={},sid={}";
//                    logger.error(errMsg, platformId, serverId);
//                } else {
//                    String redisKey = getRedisKey(orderId);
//                    String cacheMap = redisCache.getCacheObject(redisKey);
//                    if (cacheMap == null) {
//                        String url = H2RPCUtils.makeURL(server.getInHost(), server.getInPort());
//                        String jsonObject = H2RPCUtils.call(url, H2Commands.SDK_RECHARGE, rechargeOrder(roleId, productId, tabIndex, orderId, uid, amount, gameOrderId, channel, giftUid));
//                        JSONObject object1 = JSON.parseObject(jsonObject);
//                        SdkResult rechargeResult = SdkResult.toSdkResult(object1);
//                        if (rechargeResult.isSuccess()) {
//                            success = true;
//                            res = "SUCCESS";
//                            redisCache.setCacheObject(redisKey, orderId, HotConfigUtils.getIntValue("order.success.time.min", 10), TimeUnit.MINUTES);
//                        } else {
//                            errMsg = rechargeResult.getErrorMsg();
//                            logger.error("Recharge,通知游戏服发放道具失败,OrderId={},RoleData={},errorMsg={}", orderId, extraInfo, errMsg);
//                        }
//                    } else {
//                        logger.error("Recharge,订单已存在,OrderId={},RoleData={}", orderId, extraInfo);
//                        res = "SUCCESS";
//                        resp.getWriter().write(res);
//                        return;
//                    }
//                }
//            }
//
//            RechargeOrder rechargeOrder = toRechargeOrder(platformId, serverId, success ? 1 : -1, orderId, uid,
//                    productId, channel, "", errMsg, amount, roleId, gameOrderId, "0", "", JSONObject.toJSONString(map));
//
//            String tabName = Constants.RECHARGE_NAME_PRE + tabIndex;
//            rechargeOrderService.insertRechargeOrder(tabName, rechargeOrder);
//            logger.info("Recharge,充值结果：{},OrderId={},RoleData={}", res, orderId, extraInfo);
//            resp.getWriter().write(res);
//        } catch (Exception e) {
//            logger.error(e.getMessage(), e);
//            logger.error("param Info:{}", map);
//            resp.getWriter().write(res);
//        }
//    }
//
//    public static String paramString(Map<String, String> map, String... ignore) {
//        StringJoiner sb = new StringJoiner("&");
//        map.forEach((k, v) -> {
//            if (ArrayUtils.contains(ignore, k)) {
//                return;
//            }
//            try {
//                sb.add(k + "=" + URLEncoder.encode(v,"utf-8"));
//            } catch (UnsupportedEncodingException e) {
//                e.printStackTrace();
//            }
//        });
//        return sb.toString();
//    }
//
//    // 检查平台是否已经关闭充值
//    public boolean checkSdkRechargeSwitchClose(int platformId) {
//        String s = HotConfigUtils.get("sdk.recharge.plat.close", "");
//        String[] split = s.split(",");
//        if (split.length == 0) {
//            return false;
//        }
//        return Arrays.stream(split).anyMatch(v -> String.valueOf(platformId).equals(v) || "-1".equals(v));
//    }
//
//    private final static Pattern pattern = Pattern.compile("\\d+");
//    public String decode(String src, String key) {
//        if (src == null || src.length() == 0) {
//            return src;
//        }
//        Matcher m = pattern.matcher(src);
//        List<Integer> list = new ArrayList<>();
//        while (m.find()) {
//            try {
//                String group = m.group();
//                list.add(Integer.valueOf(group));
//            } catch (Exception e) {
//                logger.error(e.getMessage(), e);
//                return src;
//            }
//        }
//        if (!list.isEmpty()) {
//            try {
//                byte[] data = new byte[list.size()];
//                byte[] keys = key.getBytes();
//                for (int i = 0; i < data.length; i++) {
//                    data[i] = (byte) (list.get(i) - (0xff & keys[i % keys.length]));
//                }
//                return new String(data, StandardCharsets.UTF_8);
//            } catch (Exception e){
//                logger.error(e.getMessage(), e);
//            }
//            return src;
//        } else {
//            return src;
//        }
//    }
//
//    private cn.hutool.json.JSONObject getMessageDataByNode(String message, cn.hutool.json.JSONObject jsonObject) {
//        for (Map.Entry<String, Object> stringObjectEntry : jsonObject.entrySet()) {
//            String key = stringObjectEntry.getKey();
//            Object value = stringObjectEntry.getValue();
//            if (key.equals(message)) {
//                if (value instanceof cn.hutool.json.JSONObject) {
//                    return (cn.hutool.json.JSONObject) value;
//                }
//                return null;
//            } else {
//                if (value instanceof cn.hutool.json.JSONObject) {
//                    return getMessageDataByNode(message, (cn.hutool.json.JSONObject) value);
//                }
//            }
//        }
//        return null;
//    }
//
//    @PostMapping(value = "/qiJieRecharge")
//    public void qiJieRecharge(HttpServletRequest req, HttpServletResponse resp) throws Exception {
//        String res = "FAILURE";
//        try {
//            Map<String, String> map = parseRequestParam(req);
//
//            if (HotConfigUtils.isDebug()) {
//                logger.info("param:{}", map);
//            }
//
//            String orderId = map.get("orderNum");
//            String billNo = orderId;
//            String userCode = map.get("userId");
//            String appId = map.get("appId");
//            String money = map.get("money");
//            int serverId = Integer.parseInt(map.get("serverId"));
//            long roleId = Long.parseLong(map.get("roleId"));
//            String roleName = map.get("roleName");
//            int status = Integer.parseInt(map.get("status"));
//
//            String extraInfo = map.get("extInfo");
//            String sign = map.get("sign");
//
//            boolean success = false;
//            String errMsg = "";
//            String productId = "";
//            String gameId = "";
//            String format = String.format("%s%s", orderId, billNo);
//            int tabIndex = getTableIndex(format);
//            int platformId = 0;
//            if (status == 1) {
//                int giftUid = 0;
//                try {
//                    JSONObject attachObj = JSONObject.parseObject(extraInfo);
//                    platformId = attachObj.getIntValue(ConstKeyId.PLATFORM_ID);
//                    if (attachObj.containsKey(ConstKeyId.ID)) {// 优先使用客户端透传参数中的id
//                        productId = attachObj.getString(ConstKeyId.ID);
//                    }
//                    if (attachObj.containsKey("giftUid")) {
//                        giftUid = Integer.parseInt(attachObj.getString("giftUid"));
//                    }
//                } catch (Exception e) {
//                    String encode = URLDecoder.decode(extraInfo, "UTF-8");
//                    JSONObject attachObj = JSONObject.parseObject(encode);
//                    platformId = attachObj.getIntValue(ConstKeyId.PLATFORM_ID);
//                    if (attachObj.containsKey(ConstKeyId.ID)) {// 优先使用客户端透传参数中的id
//                        productId = attachObj.getString(ConstKeyId.ID);
//                    }
//                    if (attachObj.containsKey("giftUid")) {
//                        giftUid = Integer.parseInt(attachObj.getString("giftUid"));
//                    }
//                }
//                // 平台是否关闭充值
//                if (checkSdkRechargeSwitchClose(platformId)) {
//                    resp.getWriter().write(res);
//                    return;
//                }
//
//                if (platformId == 0 || Strings.isEmpty(productId)) {
//                    logger.error("platform:{} produce:{} error. role:{} server:{}", platformId, productId, roleId, serverId);
//                    resp.getWriter().write(res);
//                    return;
//                }
//
//                int finalPid = platformId;
//                GameArgConfig gameArgConfig = ConfigMgr.getGameArgConfigMap().values().stream().filter(v -> finalPid == v.getPlatformId() && v.getPayKey().equals(appId)).findFirst().orElse(null);
//                if (gameArgConfig == null) {
//                    logger.error("充值失败，没有对应的参数配置：platform:{} appId:{} server:{} roleId:{}", platformId, appId, serverId, roleId);
//                    resp.getWriter().write(res);
//                    return;
//                }
//                gameId = String.valueOf(gameArgConfig.getGameId());
//                String md5 = CryptoUtils.encodeMD5(appId + userCode + orderId + money + serverId + roleId + roleName + extraInfo + status + gameArgConfig.getGameKey()).toLowerCase();
//                if (!md5.equals(sign)) {
//                    errMsg = "sign check error";
//                } else {
//                    GmServer server = serverService.selectServerByServerIdAndPid(platformId, serverId);
//                    if (server == null) {
//                        errMsg = "Recharge,订单校验通过,未找到服务器ID,pid={},sid={}";
//                        logger.error(errMsg, platformId, serverId);
//                        resp.getWriter().write(res);
//                        return;
//                    }
//                    String redisKey = getRedisKey(orderId);
//                    String cacheMap = redisCache.getCacheObject(redisKey);
//                    if (cacheMap == null) {
//                        String url = H2RPCUtils.makeURL(server.getInHost(), server.getInPort());
//                        String object = H2RPCUtils.call(url, H2Commands.SDK_RECHARGE, rechargeOrder(roleId, productId, tabIndex, orderId, userCode, money, billNo, "", giftUid));
//                        JSONObject object1 = JSON.parseObject(object);
//                        SdkResult rechargeResult = SdkResult.toSdkResult(object1);
//                        if (rechargeResult.isSuccess()) {
//                            success = true;
//                            res = "SUCCESS";
//                            redisCache.setCacheObject(redisKey, orderId, HotConfigUtils.getIntValue("order.success.time.min", 10), TimeUnit.MINUTES);
//                        } else {
//                            errMsg = rechargeResult.getErrorMsg();
//                        }
//                    } else {
//                        errMsg = "order repeated";
//                    }
//                }
//            } else {
//                errMsg = "error recall game server";
//            }
//
//            RechargeOrder rechargeOrder = toRechargeOrder(platformId, serverId, success ? 1 : -1, orderId, userCode,
//                    productId, "", gameId, errMsg, money, roleId, billNo, "0", "", JSONObject.toJSONString(map));
//
//            String tabName = Constants.RECHARGE_NAME_PRE + tabIndex;
//            rechargeOrderService.insertRechargeOrder(tabName, rechargeOrder);
//
//            String result = success ? "success" : URLEncoder.encode(errMsg,"UTF-8");
//
//            logger.info("roleId:{}, payId:{}, orderId:{}, result:{} errMsg:{} billNo:{}", roleId, productId, orderId, result, errMsg, billNo);
//            if (!success) {
//                logger.error("userCode:{}, gameId:{}, orderId:{}, productId:{}", userCode, gameId, orderId, productId);
//            }
//            resp.getWriter().write(res);
//        } catch (Exception e) {
//            logger.error(e.getMessage(), e);
//            resp.getWriter().write(res);
//        }
//    }
//
//    @GetMapping(value = "/leiNiaoRecharge")
//    public void leiNiaoRecharge(HttpServletRequest req, HttpServletResponse resp) throws Exception {
//        String res = "FAILURE";
//        try {
//            Map<String, String> map = parseRequestParam(req);
//            if (map.isEmpty()) {
//                resp.getWriter().write("no support");
//                return;
//            }
//            if (HotConfigUtils.isDebug()) {
//                logger.info("param:{}", map);
//            }
//
//            String orderId = map.get("GameOrderNo");
//            if (Strings.isEmpty(orderId)) {
//                resp.getWriter().write("no support");
//                return;
//            }
//            String billNo = map.get("TradeNo");
//            String userCode = "";
//            String appId = map.get("GameID");
//            String totalFee = map.get("TotalFee");
//            float money = !Strings.isEmpty(totalFee) ? Float.parseFloat(totalFee) : 0;
//            int serverId = 0;
//            int status = Integer.parseInt(map.get("OrderStatus"));
//
//            String extraInfo = map.get("CallBackData");
//
//            boolean success = false;
//            String errMsg = "";
//            String productId = "";
//            String gameId = appId;
//            long roleId = 0L;
//            String format = String.format("%s%s", orderId, billNo);
//            int tabIndex = getTableIndex(format);
//            int platformId = 0;
//            if (status == 1) {
//                int giftUid = 0;
//                try {
//                    JSONObject attachObj = JSONObject.parseObject(extraInfo);
//                    platformId = attachObj.getIntValue(ConstKeyId.PLATFORM_ID);
//                    if (attachObj.containsKey(ConstKeyId.ID)) {// 优先使用客户端透传参数中的id
//                        productId = attachObj.getString(ConstKeyId.ID);
//                    }
//                    roleId = attachObj.getLongValue("roleId");
//                    serverId = attachObj.getIntValue("serverId");
//                    if (attachObj.containsKey("giftUid")) {
//                        giftUid = Integer.parseInt(attachObj.getString("giftUid"));
//                    }
//                } catch (Exception e) {
//                    String encode = URLDecoder.decode(extraInfo, "UTF-8");
//                    JSONObject attachObj = JSONObject.parseObject(encode);
//                    platformId = attachObj.getIntValue(ConstKeyId.PLATFORM_ID);
//                    if (attachObj.containsKey(ConstKeyId.ID)) {// 优先使用客户端透传参数中的id
//                        productId = attachObj.getString(ConstKeyId.ID);
//                    }
//                    roleId = attachObj.getLongValue("roleId");
//                    serverId = attachObj.getIntValue("serverId");
//                    if (attachObj.containsKey("giftUid")) {
//                        giftUid = Integer.parseInt(attachObj.getString("giftUid"));
//                    }
//                }
//                // 平台是否关闭充值
//                if (checkSdkRechargeSwitchClose(platformId)) {
//                    errMsg = "平台错误";
//                    resp.getWriter().write(toLeiNiaoResult(-1, errMsg));
//                    return;
//                }
//
//                if (platformId == 0 || Strings.isEmpty(productId)) {
//                    logger.error("platform:{} produce:{} error. role:{} server:{}", platformId, productId, roleId, serverId);
//                    errMsg = "订单错误";
//                    resp.getWriter().write(toLeiNiaoResult(-1, errMsg));
//                    return;
//                }
//
//                int finalPid = platformId;
//                GameArgConfig gameArgConfig = ConfigMgr.getGameArgConfigMap().values().stream().filter(v -> finalPid == v.getPlatformId() && appId.equals(v.getAppId() + "")).findFirst().orElse(null);
//                if (gameArgConfig == null) {
//                    logger.error("充值失败，没有对应的参数配置：platform:{} appId:{} server:{} roleId:{}", platformId, appId, serverId, roleId);
//                    errMsg = "充值错误";
//                    resp.getWriter().write(toLeiNiaoResult(-1, errMsg));
//                    return;
//                }
//                String sign = map.get("SignatureMD5");
//                gameId = String.valueOf(gameArgConfig.getGameId());
//                List<String> list = new ArrayList<>(map.keySet());
//                list.remove("SignatureMD5");
//                Collections.sort(list);
//                StringBuilder stringBuilder = new StringBuilder();
//                for (String key : list) {
//                    stringBuilder.append(key).append("=").append(map.get(key)).append("&");
//                }
//
//                String substring = stringBuilder.substring(0, stringBuilder.length() - 1).toLowerCase();
//                String md5 = CryptoUtils.encodeMD5(substring + gameArgConfig.getGameKey()).toUpperCase();
//                if (!md5.equals(sign)) {
//                    errMsg = "sign check error";
//                } else {
//                    GmServer server = serverService.selectServerByServerIdAndPid(platformId, serverId);
//                    if (server == null) {
//                        errMsg = "Recharge,订单校验通过,未找到服务器ID,pid={},sid={}";
//                        logger.error(errMsg, platformId, serverId);
//                        resp.getWriter().write(toLeiNiaoResult(-1, errMsg));
//                        return;
//                    }
//                    String redisKey = getRedisKey(orderId);
//                    String cacheMap = redisCache.getCacheObject(redisKey);
//                    if (cacheMap == null) {
//                        String url = H2RPCUtils.makeURL(server.getInHost(), server.getInPort());
//                        String object = H2RPCUtils.call(url, H2Commands.SDK_RECHARGE, rechargeOrder(roleId, productId, tabIndex, orderId, userCode, money / 100 + "", billNo, "", giftUid));
//                        JSONObject object1 = JSON.parseObject(object);
//                        SdkResult rechargeResult = SdkResult.toSdkResult(object1);
//                        if (rechargeResult.isSuccess()) {
//                            success = true;
//                            res = "SUCCESS";
//                            redisCache.setCacheObject(redisKey, orderId, HotConfigUtils.getIntValue("order.success.time.min", 10), TimeUnit.MINUTES);
//                        } else {
//                            errMsg = rechargeResult.getErrorMsg();
//                        }
//                    } else {
//                        errMsg = "order repeated";
//                    }
//                }
//            } else {
//                errMsg = "error recall game server";
//            }
//
//            RechargeOrder rechargeOrder = toRechargeOrder(platformId, serverId, success ? 1 : -1, orderId, userCode,
//                    productId, "", gameId, errMsg, String.valueOf(money / 100), roleId, billNo, "0", appId, JSONObject.toJSONString(map));
//
//
//            String tabName = Constants.RECHARGE_NAME_PRE + tabIndex;
//            rechargeOrderService.insertRechargeOrder(tabName, rechargeOrder);
//
//            String result = success ? "success" : URLEncoder.encode(errMsg,"UTF-8");
//
//            logger.info("roleId:{}, payId:{}, orderId:{}, result:{} errMsg:{} billNo:{} gameId:{}", roleId, productId, orderId, result, errMsg, billNo, appId);
//            if (!success) {
//                logger.error("userCode:{}, gameId:{}, orderId:{}, productId:{} ", userCode, gameId, orderId, productId);
//            }
//
//            resp.getWriter().write(toLeiNiaoResult(success ? 0 : -1, errMsg));
//        } catch (Exception e) {
//            logger.error(e.getMessage(), e);
//            resp.getWriter().write(res);
//        }
//    }
//
//    private String toLeiNiaoResult(int resultCode, String error) {
//        JSONObject jsonObject = new JSONObject();
//        jsonObject.put("ResultCode", resultCode);
//        jsonObject.put("ResultMessage", error);
//        return jsonObject.toJSONString();
//    }
//
//    private String toQQResult(int resultCode, String error) {
//        JSONObject jsonObject = new JSONObject();
//        jsonObject.put("ret", resultCode);
//        jsonObject.put("msg", error);
//        return jsonObject.toJSONString();
//    }
//
//    @RequestMapping("/qqGameRecharge")
//    public void qqGameRecharge(HttpServletRequest req, HttpServletResponse resp) throws Exception {
//        Map<String, String> map = null;
//        try {
//            map = parseRequestParamByParams(req);
//            if (HotConfigUtils.isDebug()) {
//                logger.info("Recharge,参数parameterMap={},", map);
//            }
//            if (map.isEmpty()) {
//                resp.getWriter().write(toQQResult(-1, "item args error"));
//                return;
//            }
//
//            String orderId = map.get("token");
//            String appIdStr = map.get("appid");
//            int appId = Integer.parseInt(appIdStr);
//            String ch_order_id = map.get("billno");
////            String ch_id = map.get("ch_id");
////            int order_status = Integer.parseInt(map.get("order_status"));
////            if (order_status != 2) {
////                resp.getWriter().write(res);
////                logger.info("Recharge,orderId={},order_status={},", ch_order_id, order_status);
////                return;
////            }
//            String payItem = map.get("payitem");
////            String role_id = map.get("role_id");
////            String extraInfo = map.get("appmeta");
//            String usrCode = map.get("openid");
//            String sign = map.get("sig");
//            String[] payItemSplit = payItem.split("\\*");
//            if (payItemSplit.length < 3) {
//                resp.getWriter().write(toQQResult(-1, "item length error"));
//                logger.info("Recharge,orderId={}, token={}", ch_order_id, orderId);
//                return;
//            }
//
//            String product_price = payItemSplit[1];
//            if (Strings.isEmpty(product_price) /*|| !StringUtils.isNumeric1(product_price)*/) {
//                resp.getWriter().write(toQQResult(-1, "item price error"));
//                logger.error("Recharge,OrderId={},usrCode={} token={} amount == 0!!!", ch_order_id, usrCode, orderId);
//                return;
//            }
//            int amountTmp = (int) Float.parseFloat(product_price) * 10;
//            if (amountTmp <= 0) {
//                resp.getWriter().write(toQQResult(-1, "item price error"));
//                logger.error("Recharge,OrderId={},RoleData={} token={} amount == 0!!! empty", ch_order_id, usrCode, orderId);
//                return;
//            }
//
//            long roleId = 0;
//            int serverId = 0;
//            boolean success = false;
//            String errMsg = "";
//            String productId = payItemSplit[0];
//            String gameId = HotConfigUtils.get("51gamer.back.gameId", "6");
//
//            int platformId = 0;
//            int giftUid = 0;
//            String extraInfo = null;
//            String redisPreKey = getRedisPreKey(orderId);
//            try {
//
//                extraInfo = redisCache.getCacheObject(redisPreKey);
//                if (Strings.isEmpty(extraInfo)) {
//                    resp.getWriter().write(toQQResult(-1, "token error"));
//                    logger.error("Recharge,OrderId={},RoleData={} token={} amount == 0!!! empty", ch_order_id, usrCode, orderId);
//                    return;
//                }
//
//                JSONObject attachObj = JSONObject.parseObject(extraInfo);
//                platformId = attachObj.getIntValue(ConstKeyId.PLATFORM_ID);
//                if (attachObj.containsKey(ConstKeyId.ID)) {// 优先使用客户端透传参数中的id
//                    productId = attachObj.getString(ConstKeyId.ID);
//                }
//                if (attachObj.containsKey(ConstKeyId.SERVER_ID)) {
//                    serverId = Integer.parseInt(attachObj.getString(ConstKeyId.SERVER_ID));
//                }
//                if (attachObj.containsKey(ConstKeyId.ROLE_ID)) {
//                    roleId = Long.parseLong(attachObj.getString(ConstKeyId.ROLE_ID));
//                }
//                if (attachObj.containsKey("giftUid")) {
//                    giftUid = Integer.parseInt(attachObj.getString("giftUid"));
//                }
//            } catch (Exception e) {
//                logger.error("Recharge,额外数据乱码,extraInfo null");
//                resp.getWriter().write(toQQResult(-1, "ext args token error"));
//                return;
//            }
//            // 平台是否关闭充值
//            if (checkSdkRechargeSwitchClose(platformId)) {
//                resp.getWriter().write(toQQResult(-1, "close"));
//                return;
//            }
//            if (productId.isEmpty() || "0".equals(productId)) {
//                logger.error("Recharge,道具ID为空,OrderId={},RoleData={}", ch_order_id, extraInfo);
//                resp.getWriter().write(toQQResult(-1, "product empty"));
//                return;
//            }
//            String gameKey = HotConfigUtils.get("game.key");
//            int tmpPlatformId = platformId;
//            GameArgConfig gameArgConfig = ConfigMgr.getGameArgConfigMap().values().stream().filter(v -> v.getPlatformId() == tmpPlatformId).findFirst().orElse(null);
//            if (gameArgConfig != null) {
//                gameId = String.valueOf(gameArgConfig.getGameId());
//                gameKey = gameArgConfig.getPayKey();
//            }
//
//            String format = String.format("%s%s", ch_order_id, ch_order_id);
//            int tabIndex = getTableIndex(format);
//
//            Map<String, String> keyMap = new HashMap<>(map);
//            keyMap.remove("sig");
//
//            StringBuilder sb = new StringBuilder();
//            List<String> keyList = new ArrayList<>(keyMap.keySet());
//            Collections.sort(keyList);
//            for (String ss : keyList) {
//                String s = keyMap.get(ss);
//                sb.append(ss).append("=").append(encodeQQUrlArgs(s)).append("&");
//            }
//            String substring = sb.substring(0, sb.length() - 1);
//            String encode = URLEncoder.encode(substring, "utf-8");
//            encode = encode.replaceAll("\\*", "%2A");
//            String reqUrl = "/qqGameRecharge";
//            String srcStr = "GET&" + URLEncoder.encode(reqUrl, "utf-8") + "&" + encode;
//            if (HotConfigUtils.isDebug()) {
//                logger.info("Recharge,signParam={},", srcStr);
//            }
//            String mySign = CryptoUtils.hmacsha1(srcStr, gameKey + "&");
//            if (!mySign.equals(sign)) {
//                errMsg = "sign not valid";
////                resp.getWriter().write(res);
//                logger.error("error!Recharge,OrderId={},sign={}, mySign = {}", ch_order_id, sign, mySign);
//            } else {
//                GmServer server = serverService.selectServerByServerIdAndPid(platformId, serverId);
//                if (server == null) {
//                    errMsg = "Recharge,订单校验通过,未找到服务器ID,pid={},sid={}";
//                    logger.error(errMsg, platformId, serverId);
//                } else {
//                    String redisKey = getRedisKey(ch_order_id);
//                    String cacheMap = redisCache.getCacheObject(redisKey);
//                    if (cacheMap == null) {
//                        String url = H2RPCUtils.makeURL(server.getInHost(), server.getInPort());
//                        String jsonObject = H2RPCUtils.call(url, H2Commands.SDK_RECHARGE, rechargeOrder(roleId, productId, tabIndex, ch_order_id, usrCode, product_price, ch_order_id, "", giftUid));
//                        JSONObject object1 = JSON.parseObject(jsonObject);
//                        SdkResult rechargeResult = SdkResult.toSdkResult(object1);
//                        if (rechargeResult.isSuccess()) {
//                            success = true;
//                            redisCache.setCacheObject(redisKey, ch_order_id, HotConfigUtils.getIntValue("order.success.time.min", 10), TimeUnit.MINUTES);
//                            redisCache.deleteObject(redisPreKey);
//                        } else {
//                            errMsg = rechargeResult.getErrorMsg();
//                            logger.error("Recharge,通知游戏服发放道具失败,OrderId={},RoleData={},errorMsg={}", ch_order_id, extraInfo, errMsg);
//                        }
//                    } else {
//                        errMsg = "Recharge,订单已存在";
//                        logger.error("Recharge,订单已存在,OrderId={},RoleData={}", ch_order_id, extraInfo);
//                    }
//                }
//            }
//
//
////            rechargeOrder.setResultCode(success ? 1 : -1);
////            if (!StringUtils.isEmpty(errMsg)) {
////                rechargeOrder.setErrorMsg(errMsg);
////            }
//            RechargeOrder rechargeOrder = toRechargeOrder(platformId, serverId, success ? 1 : -1, orderId, usrCode,
//                    productId, map.getOrDefault("platformId", ""), gameId, errMsg, product_price, roleId, ch_order_id, "0", appIdStr, JSONObject.toJSONString(map));
//
//
//            String tabName = Constants.RECHARGE_NAME_PRE + tabIndex;
//            rechargeOrderService.insertRechargeOrder(tabName, rechargeOrder);
//            logger.info("Recharge,充值结果：{},OrderId={},RoleData={}", errMsg, ch_order_id, extraInfo);
//            resp.getWriter().write(toQQResult(success ? 0 : -1, success ? "ok":errMsg));
//        } catch (Exception e) {
//            logger.error(e.getMessage(), e);
//            logger.error("param Info:{}", map);
//            resp.getWriter().write(toQQResult(-1, "other error"));
//        }
//    }
//
//
//    private String encodeQQUrlArgs(String value) {
//        StringBuilder res = new StringBuilder();
//        for (char c : value.toCharArray()) {
//            if (!String.valueOf(c).matches("[0-9a-zA-Z!*()]")) {
//                String hexString = Integer.toHexString(c);
//                res.append("%").append(hexString.toUpperCase());
//            } else {
//                res.append(c);
//            }
//        }
//        return res.toString();
//    }
//
//    @PostMapping(value = "/yiYuanRecharge")
//    public void yiYuanRecharge(HttpServletRequest req, HttpServletResponse resp) throws Exception {
//        String res = "0";
//        try {
//            Map<String, String> map = parseRequestParam(req);
//
//            if (HotConfigUtils.isDebug()) {
//                logger.info("param:{}", map);
//            }
//
//            String billNo = map.get("orderId");
//            String cpOrderId = map.get("cpOrderId");
//            String userCode = map.get("uid");
//            int appId = HotConfigUtils.getIntValue("yiyuan.sdk.app.id", 6044);
//            String money = map.get("amount");
//            int serverId = Integer.parseInt(map.get("zoneId"));
//            long roleId = Long.parseLong(map.get("roleId"));
//            int status = Integer.parseInt(map.get("orderStatus"));
//            // 1=安卓 2=ioS
//            int clientType = Integer.parseInt(map.get("clientType"));
//            int pid = Integer.parseInt(map.get("platformId"));
//
//            String extraInfo = map.get("extInfo");
//            String sign = map.get("sign");
//
//            int moneyInt = Integer.parseInt(money) / 100;
//            money = String.valueOf(moneyInt);
//
//
//            boolean success = false;
//            String errMsg = "";
//            String productId = "";
//            String gameId = "";
//            String format = String.format("%s%s", billNo, billNo);
//            int tabIndex = getTableIndex(format);
//            int platformId = 0;
//            if (status == 1) {
//                int giftUid = 0;
//                try {
//                    JSONObject attachObj = JSONObject.parseObject(extraInfo);
//                    platformId = attachObj.getIntValue(ConstKeyId.PLATFORM_ID);
//                    if (attachObj.containsKey(ConstKeyId.ID)) {// 优先使用客户端透传参数中的id
//                        productId = attachObj.getString(ConstKeyId.ID);
//                    }
//                    if (attachObj.containsKey(ConstKeyId.SERVER_ID)) {// 优先使用客户端透传参数中的id
//                        serverId = attachObj.getIntValue(ConstKeyId.SERVER_ID);
//                    }
//                    if (attachObj.containsKey("giftUid")) {
//                        giftUid = Integer.parseInt(attachObj.getString("giftUid"));
//                    }
//                } catch (Exception e) {
//                    String encode = URLDecoder.decode(extraInfo, "UTF-8");
//                    JSONObject attachObj = JSONObject.parseObject(encode);
//                    platformId = attachObj.getIntValue(ConstKeyId.PLATFORM_ID);
//                    if (attachObj.containsKey(ConstKeyId.ID)) {// 优先使用客户端透传参数中的id
//                        productId = attachObj.getString(ConstKeyId.ID);
//                    }
//                    if (attachObj.containsKey(ConstKeyId.SERVER_ID)) {// 优先使用客户端透传参数中的id
//                        serverId = attachObj.getIntValue(ConstKeyId.SERVER_ID);
//                    }
//                    if (attachObj.containsKey("giftUid")) {
//                        giftUid = Integer.parseInt(attachObj.getString("giftUid"));
//                    }
//                }
//                // 平台是否关闭充值
//                if (checkSdkRechargeSwitchClose(platformId)) {
//                    resp.getWriter().write(res);
//                    return;
//                }
//
//                if (platformId == 0 || Strings.isEmpty(productId)) {
//                    logger.error("platform:{} produce:{} error. role:{} server:{}", platformId, productId, roleId, serverId);
//                    resp.getWriter().write(res);
//                    return;
//                }
//
//                int finalPid = platformId;
//                String recallUrl = "";
//                GameArgConfig gameArgConfig = ConfigMgr.getGameArgConfigMap().values().stream().filter(v -> finalPid == v.getPlatformId() && v.getAppId() == appId).findFirst().orElse(null);
//                if (gameArgConfig == null) {
//                    logger.error("充值失败，没有对应的参数配置：platform:{} appId:{} server:{} roleId:{}", platformId, appId, serverId, roleId);
//                    resp.getWriter().write(res);
//                    return;
//                }
//                gameId = String.valueOf(gameArgConfig.getGameId());
//                recallUrl = gameArgConfig.getRecallUrl();
//
//                List<String> list = new ArrayList<>(map.keySet());
//                list.remove("sign");
//                Collections.sort(list);
//                StringBuilder stringBuilder = new StringBuilder();
//                for (String key : list) {
//                    stringBuilder.append(key).append("=").append(map.get(key)).append("&");
//                }
//
//                String substring = stringBuilder.substring(0, stringBuilder.length() - 1);
//                String md5 = CryptoUtils.encodeMD5(substring + gameArgConfig.getPayKey());
//
//                if (!md5.equals(sign)) {
//                    errMsg = "sign check error";
//                    res = "1";
//                } else {
//                    GmServer server = serverService.selectServerByServerIdAndPid(platformId, serverId);
//                    if (server == null) {
//                        errMsg = "Recharge,订单校验通过,未找到服务器ID,pid={},sid={}";
//                        logger.error(errMsg, platformId, serverId);
//                        resp.getWriter().write(res);
//                        return;
//                    }
//                    String redisKey = getRedisKey(billNo);
//                    String cacheMap = redisCache.getCacheObject(redisKey);
//                    if (cacheMap == null) {
//
//                        JSONObject jsonObject = yiYuanCheckRechargeSucRecall(pid, Long.parseLong(gameArgConfig.getGameKey()), billNo, cpOrderId, gameArgConfig.getPayKey(), recallUrl);
//                        if ("0".equals(jsonObject.getString("code"))) {
//                            String url = H2RPCUtils.makeURL(server.getInHost(), server.getInPort());
//                            String object = H2RPCUtils.call(url, H2Commands.SDK_RECHARGE, rechargeOrder(roleId, productId, tabIndex, cpOrderId, userCode, money, billNo, "", giftUid));
//                            JSONObject object1 = JSON.parseObject(object);
//                            SdkResult rechargeResult = SdkResult.toSdkResult(object1);
//                            if (rechargeResult.isSuccess()) {
//                                success = true;
//                                res = "SUCCESS";
//                                redisCache.setCacheObject(redisKey, billNo, HotConfigUtils.getIntValue("order.success.time.min", 10), TimeUnit.MINUTES);
//                            } else {
//                                errMsg = rechargeResult.getErrorMsg();
//                            }
//                        } else {
//                            errMsg = jsonObject.getString("msg");
//                            logger.error("recheck error {}", jsonObject);
//                            res = "3";
//                        }
//                    } else {
//                        errMsg = "order repeated";
//                    }
//                }
//            } else {
//                errMsg = "error receive statue";
//                res = "2";
//            }
//
//            RechargeOrder rechargeOrder = toRechargeOrder(platformId, serverId, success ? 1 : -1, cpOrderId, userCode,
//                    productId, map.getOrDefault("platformId", ""), gameId, errMsg, money, roleId, billNo, "0", String.valueOf(appId), JSONObject.toJSONString(map));
//
//
//            String tabName = Constants.RECHARGE_NAME_PRE + tabIndex;
//            rechargeOrderService.insertRechargeOrder(tabName, rechargeOrder);
//
//            String result = success ? "success" : URLEncoder.encode(errMsg,"UTF-8");
//
//            logger.info("roleId:{}, payId:{}, orderId:{}, result:{} errMsg:{} billNo:{} res={}", roleId, productId, cpOrderId, result, errMsg, billNo, res);
//            if (!success) {
//                logger.error("userCode:{}, gameId:{}, orderId:{}, productId:{}", userCode, gameId, cpOrderId, productId);
//            }
//            resp.getWriter().write(res);
//        } catch (Exception e) {
//            logger.error(e.getMessage(), e);
//            resp.getWriter().write(res);
//        }
//    }
//
//    private JSONObject yiYuanCheckRechargeSucRecall(int platformId, long appId, String orderId, String cpOrderId, String gameKey, String url) throws IOException {
////        String url = HotConfigUtils.get("one.yuan.recall.url", "http://release.anjiu.cn/cp/getpaystatus");
//        JSONObject jsonObject = new JSONObject();
//        jsonObject.put("platformId", platformId);
//        jsonObject.put("appId", appId);
//        jsonObject.put("orderId", orderId);
//        jsonObject.put("cpOrderId", cpOrderId);
//
//        List<String> list = new ArrayList<>(jsonObject.keySet());
//        Collections.sort(list);
//        StringBuilder stringBuilder = new StringBuilder();
//        for (String key : list) {
//            stringBuilder.append(key).append("=").append(jsonObject.get(key)).append("&");
//        }
//
//        String substring = stringBuilder.substring(0, stringBuilder.length() - 1);
//        String md5 = CryptoUtils.encodeMD5(substring + gameKey);
//        jsonObject.put("sign", md5);
//
//        String s = ParamParseUtils.sendSyncPost(url, jsonObject);
//        return JSONObject.parseObject(s);
//    }
//
//    @PostMapping(value = "/guopanRecharge")
//    public void guopanRecharge(HttpServletRequest req, HttpServletResponse resp) throws Exception {
//        String res = "fail";
//        Map<String, String> map = null;
//        try {
//            map = parseRequestParam(req);
//            if (HotConfigUtils.isDebug()) {
//                logger.info("Recharge,参数parameterMap={},", map);
//            }
//
//            String orderId = map.get("serialNumber");
//            String appIdStr = map.get("appid");
//            int appId = Integer.parseInt(appIdStr);
//            String ch_order_id = map.get("trade_no");
//            int order_status = Integer.parseInt(map.get("status"));
//            if (order_status != 1) {
//                resp.getWriter().write(res);
//                logger.info("Recharge,orderId={},order_status={},", orderId, order_status);
//                return;
//            }
//            String product_price = map.get("money");
//            String extraInfo = map.get("reserved");
//            String usrCode = map.get("game_uin");
//            String sign = map.get("sign");
//            String time = map.get("t");
//
//            if (Strings.isEmpty(product_price) /*|| !StringUtils.isNumeric1(product_price)*/) {
//                resp.getWriter().write(res);
//                logger.error("Recharge,OrderId={},usrCode={} amount == 0!!!", orderId, usrCode);
//                return;
//            }
//            int amountTmp = (int) Float.parseFloat(product_price) * 100;
//            if (amountTmp <= 0) {
//                resp.getWriter().write(res);
//                logger.error("Recharge,OrderId={},usrCode={} amount == 0!!! empty", orderId, usrCode);
//                return;
//            }
//
//            long roleId = 0;
//            int serverId = 0;
//            boolean success = false;
//            String errMsg = "";
//            String productId = "0";
//            String gameId = HotConfigUtils.get("51gamer.back.gameId", "6");
//
//            int platformId = 0;
//            int giftUid = 0;
//            try {
//                JSONObject attachObj = JSONObject.parseObject(extraInfo);
//                platformId = attachObj.getIntValue(ConstKeyId.PLATFORM_ID);
//                if (attachObj.containsKey(ConstKeyId.ID)) {// 优先使用客户端透传参数中的id
//                    productId = attachObj.getString(ConstKeyId.ID);
//                }
//                if (attachObj.containsKey(ConstKeyId.SERVER_ID)) {
//                    serverId = Integer.parseInt(attachObj.getString(ConstKeyId.SERVER_ID));
//                }
//                if (attachObj.containsKey(ConstKeyId.ROLE_ID)) {
//                    roleId = Long.parseLong(attachObj.getString(ConstKeyId.ROLE_ID));
//                }
//                if (attachObj.containsKey("giftUid")) {
//                    giftUid = Integer.parseInt(attachObj.getString("giftUid"));
//                }
//            } catch (Exception e) {
//                logger.error("Recharge,额外数据出错,OrderId={},error={}", orderId, e.getMessage());
//                String encode = URLDecoder.decode(extraInfo, "UTF-8");
//                try {
//                    JSONObject attachObj = JSONObject.parseObject(encode);
//                    platformId = attachObj.getIntValue(ConstKeyId.PLATFORM_ID);
//                    if (attachObj.containsKey(ConstKeyId.ID)) {// 优先使用客户端透传参数中的id
//                        productId = attachObj.getString(ConstKeyId.ID);
//                    }
//                    if (attachObj.containsKey(ConstKeyId.SERVER_ID)) {
//                        serverId = Integer.parseInt(attachObj.getString(ConstKeyId.SERVER_ID));
//                    }
//                    if (attachObj.containsKey(ConstKeyId.ROLE_ID)) {
//                        roleId = Long.parseLong(attachObj.getString(ConstKeyId.ROLE_ID));
//                    }
//                    if (attachObj.containsKey("giftUid")) {
//                        giftUid = Integer.parseInt(attachObj.getString("giftUid"));
//                    }
//                } catch (Exception e1) {
//                    logger.error("Recharge,额外数据乱码,OrderId={},RoleData={},error={}", orderId, encode, e1.getMessage());
//                    resp.getWriter().write(res);
//                    return;
//                }
//            }
//            // 平台是否关闭充值
//            if (checkSdkRechargeSwitchClose(platformId)) {
//                resp.getWriter().write(res);
//                return;
//            }
//            if (productId.isEmpty() || "0".equals(productId)) {
//                logger.error("Recharge,道具ID为空,OrderId={},RoleData={}", orderId, extraInfo);
//            }
//            String gameKey = HotConfigUtils.get("game.key");
//            int tmpPlatformId = platformId;
//            String recallUrl = "";
//            GameArgConfig gameArgConfig = ConfigMgr.getGameArgConfigMap().values().stream().filter(v -> v.getPlatformId() == tmpPlatformId && v.getAppId() == appId).findFirst().orElse(null);
//            if (gameArgConfig != null) {
//                gameId = String.valueOf(gameArgConfig.getGameId());
//                gameKey = gameArgConfig.getPayKey();
//                recallUrl = gameArgConfig.getRecallUrl();
//            }
//
//            String signParam = orderId + product_price + order_status + time + gameKey; //paramMap(map, gameKey);
//            if (HotConfigUtils.isDebug()) {
//                logger.info("Recharge,signParam={},", signParam);
//            }
//            if (Strings.isEmpty(usrCode)) {
//                usrCode = String.valueOf(roleId);
//            }
//
//            String format = String.format("%s%s", orderId, ch_order_id);
//            int tabIndex = getTableIndex(format);
//
//            String mySign = CryptoUtils.encodeMD5(signParam);
//            if (!mySign.equals(sign)) {
//                errMsg = "sign not valid";
////                resp.getWriter().write(res);
//                logger.error("error!Recharge,OrderId={},sign={} mySign == {}", orderId, sign, mySign);
//            } else {
//                GmServer server = serverService.selectServerByServerIdAndPid(platformId, serverId);
//                if (server == null) {
//                    errMsg = "Recharge,订单校验通过,未找到服务器ID,pid={},sid={}";
//                    logger.error(errMsg, platformId, serverId);
//                } else {
//                    String redisKey = getRedisKey(orderId);
//                    String cacheMap = redisCache.getCacheObject(redisKey);
//                    if (cacheMap == null) {
//                        String s1 = guopanRechargeCheck(orderId, appIdStr, gameKey, recallUrl);
//                        String[] split = s1.split(",");
//                        if (split.length > 0 && "success".equals(split[0])) {
//                            String url = H2RPCUtils.makeURL(server.getInHost(), server.getInPort());
//                            String jsonObject = H2RPCUtils.call(url, H2Commands.SDK_RECHARGE, rechargeOrder(roleId, productId, tabIndex, orderId, usrCode, product_price, ch_order_id, "", giftUid));
//                            JSONObject object1 = JSON.parseObject(jsonObject);
//                            SdkResult rechargeResult = SdkResult.toSdkResult(object1);
//                            if (rechargeResult.isSuccess()) {
//                                success = true;
//                                res = "success";
//                                redisCache.setCacheObject(redisKey, orderId, HotConfigUtils.getIntValue("order.success.time.min", 10), TimeUnit.MINUTES);
//                            } else {
//                                errMsg = rechargeResult.getErrorMsg();
//                                logger.error("Recharge,通知游戏服发放道具失败,OrderId={},RoleData={},errorMsg={}", orderId, extraInfo, errMsg);
//                            }
//                        } else {
//                            errMsg = "Recharge,订单查询失败";
//                            logger.error("Recharge,订单查询失败,OrderId={},RoleData={} reason={}", orderId, extraInfo, s1);
//                        }
//                    } else {
//                        errMsg = "Recharge,订单已存在";
//                        logger.error("Recharge,订单已存在,OrderId={},RoleData={} ", orderId, extraInfo);
//                    }
//                }
//            }
//
//            RechargeOrder rechargeOrder = toRechargeOrder(platformId, serverId, success ? 1 : -1, orderId, usrCode,
//                    productId, "", gameId, errMsg, product_price, roleId, ch_order_id, "0", appIdStr, JSONObject.toJSONString(map));
//
//
//            String tabName = Constants.RECHARGE_NAME_PRE + tabIndex;
//            rechargeOrderService.insertRechargeOrder(tabName, rechargeOrder);
//            logger.info("Recharge,充值结果：{},OrderId={},RoleData={}", res, orderId, extraInfo);
//            resp.getWriter().write(res);
//        } catch (Exception e) {
//            logger.error(e.getMessage(), e);
//            logger.error("param Info:{}", map);
//            resp.getWriter().write(res);
//        }
//    }
//
//    private String guopanRechargeCheck(String orderId, String appId, String gameKey, String url) throws IOException {
////        String url = HotConfigUtils.get("guopan.recall.url", "http://open.guopan.cn/api2/gp_sdk_order_status.php");
//        Map<String, Object> jsonObject = new HashMap<>();
//        jsonObject.put("serialNumber", orderId);
//        jsonObject.put("appid", appId);
//        jsonObject.put("orderId", orderId);
//        long now = System.currentTimeMillis() / 1000L;
//        jsonObject.put("t", String.valueOf(now));
//        String signStr = appId + orderId + now + gameKey;
//        String sign = CryptoUtils.encodeMD5(signStr);
//        jsonObject.put("sign", sign);
//
//        return ParamParseUtils.sendSyncGet(url, jsonObject, "utf-8");
//    }
//
//    @PostMapping(value = "/xiaoQiRecharge")
//    public void xiaoQiRecharge(HttpServletRequest req, HttpServletResponse resp) throws Exception {
//        String res = "fail";
//        try {
//            Map<String, String> map = parseRequestParamByParams(req);
//
//            if (HotConfigUtils.isDebug()) {
//                logger.info("param:{}", map);
//            }
//
//            String orderId = map.get("game_orderid");
//            String billNo = map.get("xiao7_goid");
////            String userCode = map.get("userId");
////            String appId = map.get("appId");
////            String money = map.get("money");
//            int serverId = Integer.parseInt(map.get("game_area"));
//            long roleId = Long.parseLong(map.get("game_role_id"));
//            if (roleId == 0L) {
//                res = "failed:game_role_id error";
//                resp.getWriter().write(res);
//                return ;
//            }
//            String roleName = map.get("game_role_name");
////            int status = Integer.parseInt(map.get("status"));
//
//            String extraInfo = map.get("extends_info_data");
//            String signData = map.get("sign_data");
//
//            boolean success = false;
//            String errMsg = "";
//            String productId = "";
//            String gameId = "";
//            String format = String.format("%s%s", orderId, billNo);
//            int tabIndex = getTableIndex(format);
//            int platformId = 0;
//            int giftUid = 0;
//            Map<String, String> stringStringMap = parseRequestParam(extraInfo);
//            if (stringStringMap.containsKey("giftUid")) {
//                giftUid = Integer.parseInt(stringStringMap.get("giftUid"));
//            } else {
//                if (map.containsKey("giftUid")) {
//                    giftUid = Integer.parseInt(map.get("giftUid"));
//                }
//            }
//            if (stringStringMap.containsKey(ConstKeyId.ID)) {// 优先使用客户端透传参数中的id
//                productId = stringStringMap.get(ConstKeyId.ID);
//            } else {
//                if (map.containsKey(ConstKeyId.ID)) {
//                    productId = map.get(ConstKeyId.ID);
//                }
//            }
//            if (stringStringMap.containsKey(ConstKeyId.PLATFORM_ID)) {
//                platformId = Integer.parseInt(stringStringMap.get(ConstKeyId.PLATFORM_ID));
//            } else {
//                if (map.containsKey(ConstKeyId.PLATFORM_ID)) {
//                    platformId = Integer.parseInt(map.get(ConstKeyId.PLATFORM_ID));
//                }
//            }
//            // 平台是否关闭充值
//            if (checkSdkRechargeSwitchClose(platformId)) {
//                resp.getWriter().write(res);
//                return;
//            }
//
//            if (platformId == 0 || Strings.isEmpty(productId)) {
//                logger.error("platform:{} produce:{} error. role:{} server:{}", platformId, productId, roleId, serverId);
//                resp.getWriter().write(res);
//                return;
//            }
//
//            int tmpPid = platformId;
//            GameArgConfig gameArgConfig = ConfigMgr.getGameArgConfigMap().values().stream().filter(v -> v.getPlatformId() == tmpPid && v.getMethodType() == ConstForbidType.GAME_ARG_METHOD_TYPE_7).findFirst().orElse(null);
//            if (gameArgConfig == null) {
//                logger.error("充值失败，没有对应的参数配置：platform:{} server:{} roleId:{}", platformId, serverId, roleId);
//                resp.getWriter().write(res);
//                return;
//            }
//
//            String money = null;
//            String userCode = null;
//            try {
//                String encrypData = map.get("encryp_data");
//                String payInfo = RSAUtils.decryptShaWithRAS(encrypData, gameArgConfig.getPayKey());
//                Map<String, String> pInfoMap = parseRequestParam(payInfo);
//                money = pInfoMap.get("pay_price");
//                userCode = pInfoMap.get("guid");
//                if (money == null || userCode == null) {
//                    res = "encryp_data_decrypt_failed";
//                    resp.getWriter().write(res);
//                    logger.error("money or use code is null");
//                    return ;
//                }
//            } catch (Exception e) {
//                res = "encryp_data_decrypt_failed";
//                resp.getWriter().write(res);
//                logger.error(e.getMessage(), e);
//                return ;
//            }
//
//            List<String> list = new ArrayList<>(map.keySet());
//            list.remove("sign_data");
//            Collections.sort(list);
//            StringBuilder stringBuilder = new StringBuilder();
//            for (String key : list) {
//                stringBuilder.append(key).append("=").append(map.get(key)).append("&");
//            }
//
//            String substring = stringBuilder.substring(0, stringBuilder.length() - 1);
//            boolean b = RSAUtils.encryptShaWithRSA(substring, signData, gameArgConfig.getPayKey());
//
//            if (!b) {
//                errMsg = "sign_data_verify_failed";
//                res = errMsg;
//            } else {
//                GmServer server = serverService.selectServerByServerIdAndPid(platformId, serverId);
//                if (server == null) {
//                    errMsg = "Recharge,订单校验通过,未找到服务器ID,pid={},sid={}";
//                    logger.error(errMsg, platformId, serverId);
//                    resp.getWriter().write(res);
//                    return;
//                }
//                String redisKey = getRedisKey(orderId);
//                String cacheMap = redisCache.getCacheObject(redisKey);
//                if (cacheMap == null) {
//                    JSONObject jsonObject = xiaoQiRechargeCheck(orderId, gameArgConfig.getGameKey(), gameArgConfig.getRecallUrl(), gameArgConfig.getPayKey());
//                    if ("0".equals(jsonObject.getString("errorno"))) {
//                        String url = H2RPCUtils.makeURL(server.getInHost(), server.getInPort());
//                        String object = H2RPCUtils.call(url, H2Commands.SDK_RECHARGE, rechargeOrder(roleId, productId, tabIndex, orderId, userCode, money, billNo, "", giftUid));
//                        JSONObject object1 = JSON.parseObject(object);
//                        SdkResult rechargeResult = SdkResult.toSdkResult(object1);
//                        if (rechargeResult.isSuccess()) {
//                            success = true;
//                            res = "success";
//                            redisCache.setCacheObject(redisKey, orderId, HotConfigUtils.getIntValue("order.success.time.min", 10), TimeUnit.MINUTES);
//                        } else {
//                            errMsg = rechargeResult.getErrorMsg();
//                        }
//                    } else {
//                        errMsg = jsonObject.getString("errormsg");
//                        logger.error("recheck error {}", jsonObject);
//                        res = "failed:game_orderid error";
//                    }
//
//                } else {
//                    errMsg = "order repeated";
//                    res = "success";
//                }
//            }
//
//            RechargeOrder rechargeOrder = toRechargeOrder(platformId, serverId, success ? 1 : -1, orderId, userCode,
//                    productId, "", gameId, errMsg, money, roleId, billNo, "0", "", JSONObject.toJSONString(map));
//
//
//            String tabName = Constants.RECHARGE_NAME_PRE + tabIndex;
//            rechargeOrderService.insertRechargeOrder(tabName, rechargeOrder);
//
//            String result = success ? "success" : URLEncoder.encode(errMsg,"UTF-8");
//
//            logger.info("roleId:{}, payId:{}, orderId:{}, result:{} errMsg:{} billNo:{}", roleId, productId, orderId, result, errMsg, billNo);
//            if (!success) {
//                logger.error("userCode:{}, gameId:{}, orderId:{}, productId:{}", userCode, gameId, orderId, productId);
//            }
//            resp.getWriter().write(res);
//        } catch (Exception e) {
//            logger.error(e.getMessage(), e);
//            resp.getWriter().write(res);
//        }
//    }
//
//    private JSONObject xiaoQiRechargeCheck(String orderId, String gameKey, String url, String payKye) throws Exception {
//        JSONObject jsonObject = new JSONObject();
//        jsonObject.put("appkey", gameKey);
//        jsonObject.put("game_orderid", orderId);
//
//        List<String> list = new ArrayList<>(jsonObject.keySet());
//        Collections.sort(list);
//        StringBuilder stringBuilder = new StringBuilder();
//        for (String key : list) {
//            stringBuilder.append(key).append("=").append(jsonObject.get(key)).append("&");
//        }
//
//        String substring = stringBuilder.substring(0, stringBuilder.length() - 1);
//        String md5 = CryptoUtils.encodeMD5(substring + payKye);
//        jsonObject.put("sign", md5);
//
//        String s = ParamParseUtils.sendSyncPost(url, jsonObject);
//        return JSONObject.parseObject(s);
//    }
//
//    @PostMapping(value = "/jhWanRecharge")
//    public void jhWanRecharge(HttpServletRequest req, HttpServletResponse resp) throws Exception {
//        String res = "error";
//        try {
//            Map<String, String> map = parseRequestParam(req);
//
//            if (HotConfigUtils.isDebug()) {
//                logger.info("param:{}", map);
//            }
//
//            String orderId = map.get("orderid");
//            String billNo = map.get("cporderid");
//            String userCode = map.get("username");
//            String gameId = map.get("gameid");
//            String money = map.get("amount");
//            String paytime = map.get("paytime");
//            int serverId = Integer.parseInt(map.get("serverid"));
//            long roleId = Long.parseLong(map.get("roleid"));
//            if (roleId == 0L) {
//                res = "error";
//                resp.getWriter().write(res);
//                return ;
//            }
//
//            String extraInfo = map.get("extrasparams");
//            String sign = map.get("sign");
//
//            boolean success = false;
//            String errMsg = "";
//            String productId = "";
//            String format = String.format("%s%s", orderId, billNo);
//            int tabIndex = getTableIndex(format);
//            int platformId = 0;
//            int giftUid = 0;
//            try {
//                JSONObject attachObj = JSONObject.parseObject(extraInfo);
//                platformId = attachObj.getIntValue(ConstKeyId.PLATFORM_ID);
//                if (attachObj.containsKey(ConstKeyId.ID)) {// 优先使用客户端透传参数中的id
//                    productId = attachObj.getString(ConstKeyId.ID);
//                }
//                if (attachObj.containsKey("giftUid")) {
//                    giftUid = Integer.parseInt(attachObj.getString("giftUid"));
//                }
//            } catch (Exception e) {
//                String encode = URLDecoder.decode(extraInfo, "UTF-8");
//                encode = encode.replaceAll("&quot;", "\"");
//                JSONObject attachObj = JSONObject.parseObject(encode);
//                platformId = attachObj.getIntValue(ConstKeyId.PLATFORM_ID);
//                if (attachObj.containsKey(ConstKeyId.ID)) {// 优先使用客户端透传参数中的id
//                    productId = attachObj.getString(ConstKeyId.ID);
//                }
//                if (attachObj.containsKey("giftUid")) {
//                    giftUid = Integer.parseInt(attachObj.getString("giftUid"));
//                }
//            }
//            // 平台是否关闭充值
//            if (checkSdkRechargeSwitchClose(platformId)) {
//                resp.getWriter().write(res);
//                return;
//            }
//
//            if (platformId == 0 || Strings.isEmpty(productId)) {
//                logger.error("platform:{} produce:{} error. role:{} server:{}", platformId, productId, roleId, serverId);
//                resp.getWriter().write(res);
//                return;
//            }
//
//            GameArgConfig gameArgConfig = ConfigMgr.getGameArgConfigByAppId(Integer.parseInt(gameId));
//            if (gameArgConfig == null) {
//                logger.error("充值失败，没有对应的参数配置：platform:{} server:{} roleId:{}", platformId, serverId, roleId);
//                resp.getWriter().write(res);
//                return;
//            }
//
//            String otherArgs = extraInfo.replaceAll("\"", "&quot;");
//            otherArgs = URLEncoder.encode(otherArgs, "utf-8");
//
//            String stringBuilder = "orderid=" + orderId + "&" + "cporderid=" + billNo + "&" +
//                    "username=" + userCode + "&" + "gameid=" + gameId + "&" +
//                    "roleid=" + roleId + "&" + "serverid=" + serverId + "&" +
//                    "amount=" + money + "&" + "paytime=" + paytime + "&" +
//                    "extrasparams=" + otherArgs + "&" + "key=" + gameArgConfig.getPayKey();
//
//            String md5 = CryptoUtils.encodeMD5(stringBuilder);
//
//            if (!md5.equals(sign)) {
//                errMsg = "errorSign";
//                res = errMsg;
//                logger.error("充值失败，md5=[{}] sign=[{}] ex:{}", md5, sign, otherArgs);
//            } else {
//                GmServer server = serverService.selectServerByServerIdAndPid(platformId, serverId);
//                if (server == null) {
//                    errMsg = "Recharge,订单校验通过,未找到服务器ID,pid={},sid={}";
//                    logger.error(errMsg, platformId, serverId);
//                    resp.getWriter().write(res);
//                    return;
//                }
//                String redisKey = getRedisKey(orderId);
//                String cacheMap = redisCache.getCacheObject(redisKey);
//                if (cacheMap == null) {
//                    String url = H2RPCUtils.makeURL(server.getInHost(), server.getInPort());
//                    String object = H2RPCUtils.call(url, H2Commands.SDK_RECHARGE, rechargeOrder(roleId, productId, tabIndex, orderId, userCode, money, billNo, "", giftUid));
//                    JSONObject object1 = JSON.parseObject(object);
//                    SdkResult rechargeResult = SdkResult.toSdkResult(object1);
//                    if (rechargeResult.isSuccess()) {
//                        success = true;
//                        res = "success";
//                        redisCache.setCacheObject(redisKey, orderId, HotConfigUtils.getIntValue("order.success.time.min", 10), TimeUnit.MINUTES);
//                    } else {
//                        errMsg = rechargeResult.getErrorMsg();
//                    }
//
//                } else {
//                    errMsg = "order repeated";
//                    res = "success";
//                }
//            }
//
//            RechargeOrder rechargeOrder = toRechargeOrder(platformId, serverId, success ? 1 : -1, orderId, userCode,
//                    productId, "", gameId, errMsg, money, roleId, billNo, "0", "", JSONObject.toJSONString(map));
//
//            String tabName = Constants.RECHARGE_NAME_PRE + tabIndex;
//            rechargeOrderService.insertRechargeOrder(tabName, rechargeOrder);
//
//            String result = success ? "success" : URLEncoder.encode(errMsg,"UTF-8");
//
//            logger.info("roleId:{}, payId:{}, orderId:{}, result:{} errMsg:{} billNo:{}", roleId, productId, orderId, result, errMsg, billNo);
//            if (!success) {
//                logger.error("userCode:{}, gameId:{}, orderId:{}, productId:{}", userCode, gameId, orderId, productId);
//            }
//            resp.getWriter().write(res);
//        } catch (Exception e) {
//            logger.error(e.getMessage(), e);
//            resp.getWriter().write(res);
//        }
//    }
//
//    private RechargeOrder toRechargeOrder(int platformId, int serverId, int resultCode, String orderId, String userCode, String productId,
//                                          String channelId, String gameId, String errMsg, String money, long roleId, String billNo, String testType, String appIdStr, String reqArgs) {
//        RechargeOrder rechargeOrder = new RechargeOrder();
//        rechargeOrder.setAttachPlatformId(platformId);
//        rechargeOrder.setAttachServerId(serverId);
//        rechargeOrder.setResultCode(resultCode);
//        if (!StringUtils.isEmpty(errMsg)) {
//            rechargeOrder.setErrorMsg(errMsg);
//        }
//
//        rechargeOrder.setOrder(orderId);
//        rechargeOrder.setUid(userCode);
//        rechargeOrder.setProductId(productId);
//        rechargeOrder.setChannelId(channelId);
//        rechargeOrder.setGameId(gameId);
//        String s = reqArgs;
//        if (s.length() > 1024) {
//            s = s.substring(0, 1023);
//        }
//        rechargeOrder.setRequest(s);
//        rechargeOrder.setAmount(money);
//        rechargeOrder.setRoleId(roleId);
//        rechargeOrder.setBillNo(billNo);
//        rechargeOrder.setTestType(testType);
//        rechargeOrder.setAppId(appIdStr);
//        rechargeOrder.setCreateTime(new Date());
//        return rechargeOrder;
//    }
//
//    @GetMapping(value = "/shiziRecharge")
//    public void shiziRecharge(HttpServletRequest req, HttpServletResponse resp) throws Exception {
//        String res = "FAILURE";
//        try {
//            Map<String, String> map = parseRequestParam(req);
//            if (map.isEmpty()) {
//                resp.getWriter().write("FAILURE");
//                return;
//            }
//            if (HotConfigUtils.isDebug()) {
//                logger.info("param:{}", map);
//            }
//
//            String amount = map.get("amount");
//            String extraInfo = map.get("callback_info");
//            String orderId = map.get("order_id");
//            String roleIdStr = map.get("role_id");
//            String serverIdStr = map.get("server_id");
//            String statusStr = map.get("status");
//            String sign = map.get("sign");
//
//            long roleId = 0L;
//            if (!Strings.isEmpty(roleIdStr)) {
//                roleId = Long.parseLong(roleIdStr);
//            }
//
//            int serverId = 0;
//            if (!Strings.isEmpty(serverIdStr)) {
//                serverId = Integer.parseInt(serverIdStr);
//            }
//
//            if (Strings.isEmpty(statusStr)) {
//                resp.getWriter().write("ErrorUserVerify");
//                return;
//            }
//
//            if (Strings.isEmpty(orderId)) {
//                resp.getWriter().write("ErrorUserVerify");
//                return;
//            }
//
//            JSONObject attachObj = JSONObject.parseObject(extraInfo);
//
//            String billNo = attachObj.getString(ConstKeyId.CP_ORDER_ID);
//            String userCode = "";
//            float money = !Strings.isEmpty(amount) ? Float.parseFloat(amount) : 0;
//
//            boolean success = false;
//            String errMsg = "";
//            String productId = "";
//            String gameId = serverIdStr;
//            String format = String.format("%s%s", orderId, billNo);
//            int tabIndex = getTableIndex(format);
//            int platformId = 0;
//            int rebateType = 0;
//            if (Integer.parseInt(statusStr) == 1) {
//                int giftUid = 0;
//                try {
//                    platformId = attachObj.getIntValue(ConstKeyId.PLATFORM_ID);
//                    if (attachObj.containsKey(ConstKeyId.ID)) {// 优先使用客户端透传参数中的id
//                        productId = attachObj.getString(ConstKeyId.ID);
//                    }
//                    roleId = attachObj.getLongValue("roleId");
//                    serverId = attachObj.getIntValue("serverId");
//                    if (attachObj.containsKey("giftUid")) {
//                        giftUid = Integer.parseInt(attachObj.getString("giftUid"));
//                    }
//                    if (attachObj.containsKey(ConstKeyId.REBATE_TYPE)) {
//                        rebateType = Integer.parseInt(attachObj.getString(ConstKeyId.REBATE_TYPE));
//                    }
//                } catch (Exception e) {
//                    String encode = URLDecoder.decode(extraInfo, "UTF-8");
//                    platformId = attachObj.getIntValue(ConstKeyId.PLATFORM_ID);
//                    if (attachObj.containsKey(ConstKeyId.ID)) {// 优先使用客户端透传参数中的id
//                        productId = attachObj.getString(ConstKeyId.ID);
//                    }
//                    roleId = attachObj.getLongValue("roleId");
//                    serverId = attachObj.getIntValue("serverId");
//                    if (attachObj.containsKey("giftUid")) {
//                        giftUid = Integer.parseInt(attachObj.getString("giftUid"));
//                    }
//                    if (attachObj.containsKey(ConstKeyId.REBATE_TYPE)) {
//                        rebateType = Integer.parseInt(attachObj.getString(ConstKeyId.REBATE_TYPE));
//                    }
//                }
//                // 平台是否关闭充值
//                if (checkSdkRechargeSwitchClose(platformId)) {
//                    resp.getWriter().write("ErrorUserVerify");
//                    return;
//                }
//
//                if (platformId == 0 || Strings.isEmpty(productId)) {
//                    logger.error("platform:{} produce:{} error. role:{} server:{}", platformId, productId, roleId, serverId);
//                    resp.getWriter().write("ErrorUserVerify");
//                    return;
//                }
//
//                int finalPid = platformId;
//                GameArgConfig gameArgConfig = ConfigMgr.getGameArgConfigMap().values().stream().filter(v -> finalPid == v.getPlatformId() && v.getMethodType() == ConstForbidType.GAME_ARG_METHOD_TYPE_9).findFirst().orElse(null);
//                if (gameArgConfig == null) {
//                    logger.error("充值失败，没有对应的参数配置：platform:{} appId:{} server:{} roleId:{}", platformId, serverId, serverId, roleId);
//                    resp.getWriter().write("ErrorUserVerify");
//                    return;
//                }
//                gameId = String.valueOf(gameArgConfig.getGameId());
//
//                List<String> list = Arrays.asList("amount", "callback_info", "order_id", "role_id", "server_id", "status", "timestamp", "type", "user_id");
//                StringBuilder stringBuilder = new StringBuilder();
//                for (String key : list) {
//                    stringBuilder.append(map.get(key)).append("#");
//                }
//
//                String substring = stringBuilder.substring(0, stringBuilder.length());
//                String md5 = CryptoUtils.encodeMD5(substring + gameArgConfig.getPayKey());
//                if (!md5.equals(sign)) {
//                    errMsg = "ErrorSign";
//                } else {
//                    GmServer server = serverService.selectServerByServerIdAndPid(platformId, serverId);
//                    if (server == null) {
//                        errMsg = "Recharge,订单校验通过,未找到服务器ID,pid={},sid={}";
//                        logger.error(errMsg, platformId, serverId);
//                        resp.getWriter().write("ErrorUserVerify");
//                        return;
//                    }
//                    String redisKey = getRedisKey(orderId);
//                    String cacheMap = redisCache.getCacheObject(redisKey);
//                    if (cacheMap == null) {
//                        String url = H2RPCUtils.makeURL(server.getInHost(), server.getInPort());
//                        String object = H2RPCUtils.call(url, H2Commands.SDK_RECHARGE, rechargeOrder(roleId, productId, tabIndex, orderId, userCode, money + "", billNo, "", giftUid, rebateType));
//                        JSONObject object1 = JSON.parseObject(object);
//                        SdkResult rechargeResult = SdkResult.toSdkResult(object1);
//                        if (rechargeResult.isSuccess()) {
//                            success = true;
//                            res = "SUCCESS";
//                            redisCache.setCacheObject(redisKey, orderId, HotConfigUtils.getIntValue("order.success.time.min", 10), TimeUnit.MINUTES);
//                        } else {
//                            errMsg = rechargeResult.getErrorMsg();
//                        }
//                    } else {
//                        errMsg = "ErrorRepeat";
//                    }
//                }
//            } else {
//                errMsg = "FAILURE";
//            }
//
//            RechargeOrder rechargeOrder = toRechargeOrder(platformId, serverId, success ? 1 : -1, orderId, userCode,
//                    productId, "", gameId, errMsg, String.valueOf(money), roleId, billNo, "0", serverIdStr, JSONObject.toJSONString(map));
//
//
//            String tabName = Constants.RECHARGE_NAME_PRE + tabIndex;
//            rechargeOrderService.insertRechargeOrder(tabName, rechargeOrder);
//
//            String result = success ? "success" : URLEncoder.encode(errMsg,"UTF-8");
//
//            logger.info("roleId:{}, payId:{}, orderId:{}, result:{} errMsg:{} billNo:{} gameId:{}", roleId, productId, orderId, result, errMsg, billNo, serverId);
//            if (!success) {
//                logger.error("userCode:{}, gameId:{}, orderId:{}, productId:{} ", userCode, gameId, orderId, productId);
//            }
//
//            resp.getWriter().write(success ? "SUCCESS" : errMsg);
//        } catch (Exception e) {
//            logger.error(e.getMessage(), e);
//            resp.getWriter().write(res);
//        }
//    }
//
//    @PostMapping(value = "/shopGoodsSend")
//    public void shopGoodsSend(HttpServletRequest req, HttpServletResponse resp) throws Exception {
//        int code = 0;
//        String gamecno = "";
//        String msg = "发送失败!";
//        try {
//            Map<String, String> map = parseRequestParam(req);
//
//            if (HotConfigUtils.isDebug()) {
//                logger.info("param:{}", map);
//            }
//
//            String userid = map.get("userid");
//            int serverid = Integer.parseInt(map.get("serverid"));
//            String roleidStr = map.get("roleid");
//            long roleid = Long.parseLong(roleidStr);
//            float price = Float.parseFloat(map.get("price"));
//            int rebate = Integer.parseInt(map.get("rebate"));
//            String cno = map.get("cno");
//            int status = Integer.parseInt(map.get("status"));
//            String product = map.get("product");
//            long timestamp = Long.parseLong(map.get("timestamp"));
//
//            gamecno = cno;
//
//            if (status == 1) {
//                int platformId = Integer.parseInt(StringUtils.substring(roleidStr, 0, 4));
//                GameArgConfig gameArgConfig = ConfigMgr.getGameArgConfigMap().values().stream().filter(v -> platformId == v.getPlatformId() && v.getMethodType() == ConstForbidType.GAME_ARG_METHOD_TYPE_9).findFirst().orElse(null);
//
//                if (gameArgConfig == null) {
//                    logger.error("推送失败，没有对应的参数配置：platform:{} server:{} roleId:{}", platformId, serverid, roleid);
//                    msg = "参数配置错误!";
//                    resp.getWriter().write(toShopGoodsSendResult(code, gamecno, URLDecoder.decode(msg, "UTF-8")));
//                    return;
//                }
//
//                String sign = map.remove("sign");
//
//                StringBuilder stringBuilder = new StringBuilder();
//
//                map.entrySet().stream().sorted(Map.Entry.comparingByKey()).forEach(stringStringEntry -> {
//                    stringBuilder.append(stringStringEntry.getKey()).append("=").append(stringStringEntry.getValue()).append("&");
//                });
//                stringBuilder.append("key=").append(gameArgConfig.getPayKey());
//                String string = stringBuilder.toString();
//
//                String md5 = CryptoUtils.encodeMD5(string);
//                if (md5.equals(sign)) {
//                    GmServer server = serverService.selectServerByServerIdAndPid(platformId, serverid);
//                    if (server == null) {
//                        msg = "找不到对应游戏服务器!";
//                        resp.getWriter().write(toShopGoodsSendResult(code, gamecno, URLDecoder.decode(msg, "UTF-8")));
//                        return;
//                    }
//
//                    String redisKey = getRedisShopGoodsSendKey(cno);
//                    String cacheMap = redisCache.getCacheObject(redisKey);
//                    if (cacheMap == null) {
//                        String url = H2RPCUtils.makeURL(server.getInHost(), server.getInPort());
//                        String object = H2RPCUtils.call(url, H2Commands.SHOP_GOODS_SEND, shopGoodsSend(roleid, userid, price, rebate, cno, product, timestamp));
//                        JSONObject object1 = JSON.parseObject(object);
//                        SdkResult rechargeResult = SdkResult.toSdkResult(object1);
//                        if (rechargeResult.isSuccess()) {
//                            code = 1;
//                            msg = "发送成功!";
//                            redisCache.setCacheObject(redisKey, cno, HotConfigUtils.getIntValue("order.success.time.min", 10), TimeUnit.MINUTES);
//                        } else {
//                            msg = rechargeResult.getErrorMsg();
//                        }
//                    } else {
//                        msg = "重复发送!";
//                    }
//                }
//            }
//            resp.getWriter().write(toShopGoodsSendResult(code, gamecno, URLDecoder.decode(msg, "UTF-8")));
//        } catch (Exception e) {
//            logger.error(e.getMessage(), e);
//            resp.getWriter().write(toShopGoodsSendResult(code, gamecno, URLDecoder.decode(msg, "UTF-8")));
//        }
//    }
//
//    private String toShopGoodsSendResult(int resultCode, String cno, String msg) {
//        JSONObject jsonObject = new JSONObject();
//        jsonObject.put("code", resultCode);
//        jsonObject.put("gamecno", cno);
//        jsonObject.put("msg", msg);
//        return jsonObject.toJSONString();
//    }
//
//    @PostMapping(value = "/giftSend")
//    public void giftSend(HttpServletRequest req, HttpServletResponse resp) throws Exception {
//        int state = 0;
//        String msg = "发送失败!";
//        try {
//            Map<String, String> map = parseRequestParam(req);
//
//            if (HotConfigUtils.isDebug()) {
//                logger.info("param:{}", map);
//            }
//
//            int giftid = Integer.parseInt(map.get("giftid"));
//            String userid = map.get("userid");
//            int serverid = Integer.parseInt(map.get("serverid"));
//            String roleidStr = map.get("roleid");
//            long roleid = Long.parseLong(roleidStr);
//            long time = Long.parseLong(map.get("time"));
//
//            int platformId = Integer.parseInt(StringUtils.substring(roleidStr, 0, 4));
//            GameArgConfig gameArgConfig = ConfigMgr.getGameArgConfigMap().values().stream().filter(v -> platformId == v.getPlatformId() && v.getMethodType() == ConstForbidType.GAME_ARG_METHOD_TYPE_9).findFirst().orElse(null);
//
//            if (gameArgConfig == null) {
//                logger.error("推送失败，没有对应的参数配置：platform:{} server:{} roleId:{}", platformId, serverid, roleid);
//                msg = "参数配置错误!";
//                resp.getWriter().write(toGiftSendResult(state, URLDecoder.decode(msg, "UTF-8")));
//                return;
//            }
//
//            String sign = map.remove("sign");
//
//            StringBuilder stringBuilder = new StringBuilder();
//
//            map.entrySet().stream().sorted(Map.Entry.comparingByKey()).forEach(stringStringEntry -> {
//                stringBuilder.append(stringStringEntry.getKey()).append("=").append(stringStringEntry.getValue()).append("&");
//            });
//            stringBuilder.append(gameArgConfig.getPayKey());
//            String string = stringBuilder.toString();
//
//            String md5 = CryptoUtils.encodeMD5(string);
//            if (md5.equals(sign)) {
//                GmServer server = serverService.selectServerByServerIdAndPid(platformId, serverid);
//                if (server == null) {
//                    msg = "找不到对应游戏服务器!";
//                    resp.getWriter().write(toGiftSendResult(state, URLDecoder.decode(msg, "UTF-8")));
//                    return;
//                }
//
//                String url = H2RPCUtils.makeURL(server.getInHost(), server.getInPort());
//                String object = H2RPCUtils.call(url, H2Commands.GIFT_SEND, giftSend(roleid, userid, giftid, time));
//                JSONObject object1 = JSON.parseObject(object);
//                SdkResult rechargeResult = SdkResult.toSdkResult(object1);
//                if (rechargeResult.isSuccess()) {
//                    state = 1;
//                    msg = "发送成功!";
//                } else {
//                    msg = rechargeResult.getErrorMsg();
//                }
//            }
//            resp.getWriter().write(toGiftSendResult(state, URLDecoder.decode(msg, "UTF-8")));
//        } catch (Exception e) {
//            logger.error(e.getMessage(), e);
//            resp.getWriter().write(toGiftSendResult(state, URLDecoder.decode(msg, "UTF-8")));
//        }
//    }
//
//    private String toGiftSendResult(int state, String msg) {
//        JSONObject jsonObject = new JSONObject();
//        jsonObject.put("state", state);
//        jsonObject.put("msg", msg);
//        return jsonObject.toJSONString();
//    }
//
//    @GetMapping(value = "/vipCustomerService")
//    public void vipCustomerService(HttpServletRequest req, HttpServletResponse resp) throws Exception {
//        String msg = "FAIL";
//        try {
//            Map<String, String> map = parseRequestParam(req);
//
//            if (HotConfigUtils.isDebug()) {
//                logger.info("param:{}", map);
//            }
//
//            String uidStr = map.get("uid");
//            long roleId = Long.parseLong(uidStr);
//            int cd = Integer.parseInt(map.get("cd"));
//            String aid = map.get("aid");
//            int serverId = Integer.parseInt(map.get("server_id"));
//            long time = Long.parseLong(map.get("time"));
//            String sign = map.get("sign");
//
//            if (cd < 0) {
//                resp.getWriter().write(toVipCustomerServiceResult("CdError"));
//                return;
//            }
//
//            int platformId = Integer.parseInt(StringUtils.substring(uidStr, 0, 4));
//            GameArgConfig gameArgConfig = ConfigMgr.getGameArgConfigMap().values().stream().filter(v -> platformId == v.getPlatformId() && v.getMethodType() == ConstForbidType.GAME_ARG_METHOD_TYPE_9).findFirst().orElse(null);
//
//            if (gameArgConfig == null) {
//                logger.error("推送失败，没有对应的参数配置：platform:{} server:{} roleId:{}", platformId, serverId, roleId);
//                resp.getWriter().write(toVipCustomerServiceResult("ServerError"));
//                return;
//            }
//
//            String md5 = CryptoUtils.computeMD5Hash("r54hkj3lu4t0r0fj2z7m89ke7122step" + time);
//            if (md5.equals(sign)) {
//                GmServer server = serverService.selectServerByServerIdAndPid(platformId, serverId);
//                if (server == null) {
//                    resp.getWriter().write(toVipCustomerServiceResult("ServerError"));
//                    return;
//                }
//
//                String url = H2RPCUtils.makeURL(server.getInHost(), server.getInPort());
//                String object = H2RPCUtils.call(url, H2Commands.VIP_CUSTOMER_SERVICE_SEND, vipCunstomerServiceInfo(roleId, cd, aid, time));
//                JSONObject object1 = JSON.parseObject(object);
//                SdkResult rechargeResult = SdkResult.toSdkResult(object1);
//                if (rechargeResult.isSuccess()) {
//                    msg = "SUCCESS";
//                } else {
//                    msg = rechargeResult.getErrorMsg();
//                }
//                resp.getWriter().write(toVipCustomerServiceResult(msg));
//            } else {
//                resp.getWriter().write(toVipCustomerServiceResult("SignError"));
//            }
//        } catch (Exception e) {
//            logger.error(e.getMessage(), e);
//            resp.getWriter().write(toVipCustomerServiceResult("FAIL"));
//        }
//    }
//
//    private String toVipCustomerServiceResult(String msg) {
//        JSONObject jsonObject = new JSONObject();
//        jsonObject.put("message", msg);
//        return jsonObject.toJSONString();
//    }
//
//
//}
