package com.ycw.gm.admin.web.servlet.inside;

import com.ycw.gm.admin.domain.GmServer;
import com.ycw.gm.admin.service.IServerService;
import com.ycw.gm.admin.web.servlet.inside.anno.FuncName;
import org.apache.commons.lang3.Validate;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.Map;

/**
 * @author wangshucheng
 * @date 2020/9/28 17:13
 */
@FuncName(name = "client_log_flag")
public class ClientLogFlag extends InsideServlet {

    @Autowired
    private IServerService serverService;

    @Override
    public String process(Map<String, Object> map) throws Exception {
//        String context = HttpUtils.getUTF8Body(req);
        log.error("context data: {}", map);
        Validate.isTrue(map.containsKey("sid"), "sid not exists");
        String sid = castToString(map.get("sid"));
        String pid = map.containsKey("pid") ? castToString(map.get("pid")) : "-1";
        String clientLog = castToString(map.get("clientLog"));

        List<GmServer> gsrvs = gsrvs(pid, sid);
        if (gsrvs.isEmpty()) {
            return fail(-1, String.format("server %s not found", sid)).toJSONString();
        }
        try {
            GmServer gmServer = gsrvs.get(0);
            GmServer newServer = new GmServer();
            newServer.setSort(gmServer.getSort());
            newServer.setServerKeyId(gmServer.getServerKeyId());
            newServer.setClientLog("1".equals(clientLog) ? "1" : "0");
            serverService.updateServer(newServer);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return fail(1, e.getMessage()).toString();
        }
        return succ().toJSONString();
    }
}
