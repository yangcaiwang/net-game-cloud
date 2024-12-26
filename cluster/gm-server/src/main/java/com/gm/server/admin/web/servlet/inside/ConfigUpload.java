package com.gm.server.admin.web.servlet.inside;

import com.alibaba.fastjson.JSONObject;
import com.gm.server.admin.domain.GmServer;
import com.gm.server.admin.web.servlet.inside.anno.FuncName;
import com.gm.server.common.utils.ParamParseUtils;

import java.util.List;
import java.util.Map;

@FuncName(name = "config_upload")
public class ConfigUpload extends InsideServlet {

	@Override
	public String process(Map<String, Object> map) throws Exception {
		log.error("context data: {}", map);
		String pid = map.containsKey("pid") ? castToString(map.get("pid")) : "-1";
		String sid = map.containsKey("sid") ? castToString(map.get("sid")) : "-1";
		List<GmServer> gsrvs = gsrvs(pid, sid);
		if (gsrvs.isEmpty()) {
			return fail(-1, String.format("server %s not found", sid)).toJSONString();
		}

		map.put("cmd", "ConfigUpload");
		map.put("onlyOnce", "false");
        JSONObject jObject = succ();
        jObject.put("Code", 1);
        StringBuilder sb = new StringBuilder();
		for (GmServer gsrv : gsrvs) {
			String url = ParamParseUtils.makeURL(gsrv.getInHost(), gsrv.getInPort(), "script");
			try {
				ParamParseUtils.sendSyncTokenPost(url, map);
			} catch (Exception e) {
                sb.append("pid:").append(pid).append(";sid:").append(sid).append(".").append(e.getLocalizedMessage()).append("\n");
			}
		}
        jObject.put("Msg", sb.toString());
		return jObject.toJSONString();
	}
}
