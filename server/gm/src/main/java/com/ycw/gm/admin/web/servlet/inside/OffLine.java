package com.ycw.gm.admin.web.servlet.inside;

import com.alibaba.fastjson.JSONObject;
import com.ycw.gm.admin.domain.GmServer;
import com.ycw.gm.admin.web.servlet.inside.anno.FuncName;
import com.ycw.gm.common.utils.ParamParseUtils;
import org.apache.commons.lang3.Validate;

import java.util.List;
import java.util.Map;

/**
 * @author wangshucheng
 * @date 2020/9/28 15:31
 */
@FuncName(name = "off_line")
public class OffLine extends InsideServlet {

    @Override
    public String process(Map<String, Object> map) throws Exception {
        log.error("context data: {}", map);
        Validate.isTrue(map.containsKey("sid"), "sid not exists");
        String pid = map.containsKey("pid") ? castToString(map.get("pid")) : "-1";
        String sid = castToString(map.get("sid"));
        List<GmServer> gsrvs = gsrvs(pid, sid);
        if (gsrvs.isEmpty()) {
            return fail(-1, String.format("server %s not found", sid)).toJSONString();
        }
//        JSONArray jsonArray = new JSONArray();
        JSONObject jsonObject = new JSONObject();
        map.put("cmd", "OffLine");
        map.put("onlyOnce", "false");
        StringBuilder sb = new StringBuilder();
        for (GmServer gsrv : gsrvs) {
            String url = ParamParseUtils.makeURL(gsrv.getInHost(), gsrv.getInPort(), "script");
            try {
                ParamParseUtils.sendSyncTokenPost(url, map);
            } catch (Exception e) {
                sb.append("pid:").append(pid).append(";sid:").append(sid).append(".").append(e.getLocalizedMessage());
            }
        }
        jsonObject.put("Code", 1);
        if (sb.length() > 0) {
            log.error("send fail msg:{}", sb.toString());
            jsonObject.put("Msg", sb.toString());
            jsonObject.put("Code", 0);
        }
        return jsonObject.toJSONString();
    }
}
