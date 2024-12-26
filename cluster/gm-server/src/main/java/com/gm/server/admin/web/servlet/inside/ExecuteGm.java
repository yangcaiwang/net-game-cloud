package com.gm.server.admin.web.servlet.inside;

import com.common.module.cluster.property.PropertyConfig;
import com.gm.server.admin.domain.GmServer;
import com.gm.server.admin.web.servlet.inside.anno.FuncName;
import org.apache.commons.lang3.Validate;

import java.util.List;
import java.util.Map;

@FuncName(name = "exe_gm")
public class ExecuteGm extends InsideServlet {

	@Override
	public String process(Map<String, Object> map) throws Exception {

//		String context = HttpUtils.getUTF8Body(req);
		log.error("context data: {}", map);
		Validate.isTrue(map.containsKey("sid"), "sid not exists");
		String sid = castToString(map.get("sid"));
		String pid = map.containsKey("pid") ? castToString(map.get("pid")) : "-1";
		List<GmServer> gsrvs = gsrvs(pid, sid);
		if (gsrvs.isEmpty()) {
			return fail(-1, String.format("server %s not found", sid)).toJSONString();
		}

		if (!PropertyConfig.getBooleanValue("back.gm.open", true)) {
			return fail(-1, "GM close").toJSONString();
		}

		// 不再支持
//		map.put("cmd", "ExecuteGm");
//		map.put("onlyOnce", "false");
//		for (GmServer gsrv : gsrvs) {
//			String url = ParamParseUtils.makeURL(gsrv.getInHost(), gsrv.getInPort(), "script");
//			try {
//				String result = ParamParseUtils.sendSyncPost(url, map);
//			} catch (Exception e) {
//			}
//		}
		return succ().toJSONString();
	}
}
