package com.gm.server.admin.web.servlet.inside;

import com.gm.server.admin.domain.GmServer;
import com.gm.server.admin.web.servlet.inside.anno.FuncName;
import com.gm.server.common.utils.ParamParseUtils;
import org.apache.commons.lang3.Validate;

import java.util.List;
import java.util.Map;

/**
 * @author tree
 * @date 2022/2/18 16:40
 */
@FuncName(name = "role_exchange")
public class RoleExchange extends InsideServlet{
    @Override
    public String process(Map<String, Object> map) throws Exception {
        log.error("context data: {}", map);
//		Validate.isTrue(map.containsKey("act"), "act not exists");
        Validate.isTrue(map.containsKey("sid"), "rid not exists");
        Validate.isTrue(map.containsKey("srcRid"), "srcRid not exists");
        Validate.isTrue(map.containsKey("dstRid"), "dstRid not exists");

        try {
            String pid = map.containsKey("pid") ? castToString(map.get("pid")) : "-1";
            String sid = castToString(map.get("sid"));
            List<GmServer> gsrvs = gsrvs(pid, sid);
            if (gsrvs.isEmpty()) {
                return fail(-1, String.format("server %s not found", sid)).toJSONString();
            }
            map.put("cmd", "RoleExchange");
            map.put("onlyOnce", "false");
            for (GmServer gsrv : gsrvs) {
                String url = ParamParseUtils.makeURL(gsrv.getInHost(), gsrv.getInPort(), "script");
                try {
                    ParamParseUtils.sendSyncTokenPost(url, map);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return fail(1, e.getMessage()).toString();
        }

        return succ().toJSONString();
    }
}
