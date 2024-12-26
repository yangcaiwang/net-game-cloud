package com.gm.server.admin.web.servlet.inside;

import com.gm.server.admin.domain.GmServer;
import com.gm.server.admin.service.IServerService;
import com.gm.server.admin.web.servlet.inside.anno.FuncName;
import org.apache.commons.lang3.Validate;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.Map;

/**
 * 更新游戏服务器开服时间
 *
 *         ${tags}UpdateGameServerOpentime2020上午11:02:03com.pp.game.login.
 *         servlet.inside$
 */
@FuncName(name = "upd_opentime")
public class UpdateGameServerOpentime extends InsideServlet {

	@Autowired
	private IServerService serverService;
	@Override
	public String process(Map<String, Object> map) throws Exception {
		Validate.isTrue(map.containsKey("sid"), "sid not exists");
		Validate.isTrue(map.containsKey("workTime"), "workTime not exists");
		String sid = castToString(map.get("sid"));
		String pid = map.containsKey("pid") ? castToString(map.get("pid")) : "-1";
		long workTime = castToLong(map.get("workTime"));
		int activeType = map.containsKey("active") ? (castToBoolean(map.get("active")) ? 1 : 0) : 2;

		serverService.updateServersOpenTime(pid, sid, workTime, activeType);

		if (!"-1".equals(sid) && sid != null) {
			List<GmServer> gsrvs = gsrvs(pid, sid);
			if (!gsrvs.isEmpty()) {
				GmServer server = gsrvs.get(0);
				serverService.updateGameServerOpenTime(server, workTime, false);
			}
		}

		return succ().toJSONString();
	}
}
