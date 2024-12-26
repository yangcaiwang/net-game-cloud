package com.gm.server.admin.web.servlet.inside;

import com.gm.server.admin.domain.GmServer;
import com.gm.server.admin.service.IServerService;
import com.gm.server.admin.web.servlet.inside.anno.FuncName;
import com.gm.server.common.utils.StringUtils;
import org.apache.commons.lang3.Validate;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.Map;

@FuncName(name = "modify_server_name")
public class ModifyServerName extends InsideServlet {

	@Autowired
	private IServerService serverService;
	@Override
	public String process(Map<String, Object> map) throws Exception {

		log.error("context data: {}", map);
		Validate.isTrue(map.containsKey("sid"), "sid not exists");
		String sid = castToString(map.get("sid"));
		String pid = map.containsKey("pid") ? castToString(map.get("pid")) : "-1";
		String sName = castToString(map.get("new_name"));
		List<GmServer> gsrvs = gsrvs(pid, sid);
		if (gsrvs.isEmpty()) {
			return fail(-1, String.format("server %s not found", sid)).toJSONString();
		}
		if (StringUtils.isEmpty(sName) || sName.length() > 30) {
			return fail(-1, String.format("server name %s not valid", sName)).toJSONString();
		}

		for (GmServer gsrv : gsrvs) {
			GmServer server = new GmServer();
			server.setServerKeyId(gsrv.getServerKeyId());
			server.setServerName(sName);
			serverService.updateServer(server);
		}

		return succ().toJSONString();
	}
}
