package com.gm.server.admin.web.servlet.inside;

import com.common.module.cluster.enums.ServerState;
import com.common.module.cluster.property.PropertyConfig;
import com.gm.server.admin.domain.GmServer;
import com.gm.server.admin.service.IServerService;
import com.gm.server.admin.web.servlet.inside.anno.FuncName;
import org.apache.commons.lang3.Validate;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.Map;

/**
 * 更新游戏服务器状态
 * 
 * @author ljs
 *
 *         ${tags}UpdateGameServerStatus2020上午11:02:20com.pp.game.login.
 *         servlet.inside$
 */
@FuncName(name = "upd_status")
public class UpdateGameServerStatus extends InsideServlet {

	@Autowired
	private IServerService serverService;

	@Override
	public String process(Map<String, Object> map) throws Exception {
		Validate.isTrue(map.containsKey("sid"), "sid not exists");
		Validate.isTrue(map.containsKey("isOpen"), "sid not exists");
		String sid = castToString(map.get("sid"));
		int isOpen = castToInt(map.get("isOpen"));
		String pid = map.containsKey("pid") ? castToString(map.get("pid")) : "-1";

		List<GmServer> gsrvs = gsrvs(pid, sid);
		ServerState state = isOpen == 1 ? ServerState.NEW : ServerState.MAINTAIN;
		serverService.updateServersStatus(pid, state, gsrvs, PropertyConfig.getBooleanValue("update.server.hot", true));
		return succ().toJSONString();
	}

}
