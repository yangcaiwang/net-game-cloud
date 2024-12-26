package com.gm.server.admin.web.servlet.inside;

import com.alibaba.fastjson.JSONObject;
import com.gm.server.admin.domain.GmServer;
import com.gm.server.admin.web.servlet.inside.anno.FuncName;
import com.gm.server.common.utils.ParamParseUtils;
import com.gm.server.common.utils.SerializationUtils;
import org.apache.logging.log4j.util.Strings;

import java.util.List;
import java.util.Map;

/**
 * @author tree
 * @date 2022/2/24 18:04
 */
@FuncName(name = "activity_open")
public class ActivityOpen extends InsideServlet{
    @Override
    public String process(Map<String, Object> map) throws Exception {
//        Validate.isTrue(map.containsKey("sid"), "sid not exists");
        Integer forceEnd = castToInt(map.get("forceEnd"));
        boolean isOpen = (forceEnd == null || forceEnd == 0);
        String actId = castToString(map.get("actId"));
        if (Strings.isEmpty(actId)) {
            return fail(-1, "no act Id").toJSONString();
        }
        String pid = map.containsKey("pid") ? castToString(map.get("pid")) : "-1";
        String sid = map.containsKey("sid") ? castToString(map.get("sid") ): "-1";
        List<GmServer> gsrvs = gsrvs(pid, sid);

        map.put("cmd", "ActivityOpen");
        map.put("onlyOnce", "false");
        if (isOpen) {
            String actTime = castToString(map.get("actTime"));
            if (Strings.isEmpty(actTime)) {
                return fail(-1, "no act time").toJSONString();
            }
            String[] s = actTime.split("_");
            int[] arr = new int[s.length];
            for (int i = 0; i < s.length; i++) {
                arr[i] = Integer.parseInt(s[i]);
            }

            map.put("actTime", SerializationUtils.beanToJson(arr));
        }
        StringBuilder err = new StringBuilder();
        for (GmServer gsrv : gsrvs) {
            String url = ParamParseUtils.makeURL(gsrv.getInHost(), gsrv.getInPort(), "script");
            try {
                String s = ParamParseUtils.sendSyncTokenPost(url, map);
                if ("-2".equals(s)) {
                    err.append(String.format("%s error, need type 5;", gsrv.getServerId()));
                }
                if ("-1".equals(s)) {
                    err.append(String.format("%s error, not config;", gsrv.getServerId()));
                }
            } catch (Exception e) {
                log.error(e.getMessage(), e);
            }
        }
        if (!Strings.isEmpty(err.toString())) {
            log.error("开启活动出错，{} ", err);
            JSONObject fail = fail();
            fail.put("Msg", err.toString());
            return fail.toJSONString();
        }
        return succ().toJSONString();
    }
}
