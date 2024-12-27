package com.ycw.gm.admin.web.servlet.inside;

import com.ycw.gm.admin.domain.GmServer;
import com.ycw.gm.admin.service.IServerService;
import com.ycw.gm.admin.web.servlet.inside.anno.FuncName;
import com.ycw.gm.common.utils.ParamParseUtils;
import org.apache.commons.lang3.Validate;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.Map;

/**
 * @author tree
 * @date 2022/2/18 16:40
 */
@FuncName(name = "modify_register_switch")
public class ModifyGameRegisterSwitch extends InsideServlet{

    @Autowired
    private IServerService serverService;
    @Override
    public String process(Map<String, Object> map) throws Exception {
        log.error("context data: {}", map);
//		Validate.isTrue(map.containsKey("act"), "act not exists");
        Validate.isTrue(map.containsKey("sid"), "rid not exists");
        try {
            String pid = map.containsKey("pid") ? castToString(map.get("pid")) : "-1";
            String sid = castToString(map.get("sid"));
            List<GmServer> gsrvs = gsrvs(pid, sid);
            if (gsrvs.isEmpty()) {
                return fail(-1, String.format("server %s not found", sid)).toJSONString();
            }
            String registerSwitch = map.containsKey("registerSwitch") ? castToString(map.get("registerSwitch")) : "true";
            map.put("cmd", "ModifySysConfig");
            map.put("onlyOnce", "false");

            for (GmServer gsrv : gsrvs) {
                String url = ParamParseUtils.makeURL(gsrv.getInHost(), gsrv.getInPort(), "script");
                try {
                    ParamParseUtils.sendSyncTokenPost(url, map);

                    GmServer gmServer = new GmServer();
                    gmServer.setServerKeyId(gsrv.getServerKeyId());
                    gmServer.setRegisterSwitch("false".equals(registerSwitch) ? 1 : 0);
                    serverService.updateServerStatus(gmServer);
                } catch (Exception e) {
                    log.error(e.getMessage(), e);
                }
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return fail(1, e.getMessage()).toString();
        }

        return succ().toJSONString();
    }
}
