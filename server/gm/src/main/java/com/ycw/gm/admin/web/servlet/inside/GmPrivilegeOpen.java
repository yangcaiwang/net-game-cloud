package com.ycw.gm.admin.web.servlet.inside;

import com.ycw.gm.admin.domain.GmServer;
import com.ycw.gm.admin.web.servlet.inside.anno.FuncName;
import com.ycw.gm.common.utils.ParamParseUtils;

import java.util.List;
import java.util.Map;

@FuncName(name = "gm_privilege_switch")
public class GmPrivilegeOpen extends InsideServlet {

	@Override
	public String process(Map<String, Object> map) throws Exception {

//		String context = HttpUtils.getUTF8Body(req);
		log.error("context data: {}", map);
		String pid = map.containsKey("pid") ? castToString(map.get("pid")) : "-1";
		List<GmServer> gsrvs = gsrvs(pid, "-1");

		map.put("cmd", "GmPrivilege");
		map.put("onlyOnce", "false");
		map.put("optType", 2);
		for (GmServer gsrv : gsrvs) {
			String url = ParamParseUtils.makeURL(gsrv.getInHost(), gsrv.getInPort(), "script");
			try {
				String result = ParamParseUtils.sendSyncTokenPost(url, map);
			} catch (Exception e) {
				log.error(e.getMessage(), e);
			}
		}
		return succ().toJSONString();
	}
}
