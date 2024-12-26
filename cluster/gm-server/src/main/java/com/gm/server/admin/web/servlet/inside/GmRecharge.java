package com.gm.server.admin.web.servlet.inside;

import com.alibaba.fastjson.JSONObject;
import com.gm.server.admin.domain.GmServer;
import com.gm.server.admin.web.servlet.inside.anno.FuncName;
import com.gm.server.common.utils.ParamParseUtils;
import org.apache.commons.lang3.Validate;

import java.util.List;
import java.util.Map;

@FuncName(name = "gm_pay")
public class GmRecharge extends InsideServlet {

	@Override
	public String process(Map<String, Object> map) throws Exception {
		log.error("context data: {}", map);
		Validate.isTrue(map.containsKey("sid"), "sid not exists");
//		String pid = TypeUtils.castToString(map.get("pid"));
		String sid = castToString(map.get("sid"));
		String pid = map.containsKey("pid") ? castToString(map.get("pid")) : "-1";

		map.put("cmd", "GmRecharge");
		map.put("onlyOnce", "false");

		JSONObject jsonObject = new JSONObject();
		List<GmServer> gsrvs = gsrvs("-1", sid);
		if (!gsrvs.isEmpty()) {
			GmServer backstageGSRV =  gsrvs.get(0);
			String url = ParamParseUtils.makeURL(backstageGSRV.getInHost(), backstageGSRV.getInPort(), "script");
//		jsonObject.put("pid", cid);
//		jsonObject.put("sid", 1);
			jsonObject.put("Code", 1);
			try {
				ParamParseUtils.sendSyncTokenPost(url, map);
			} catch (Exception e) {
				jsonObject.put("Msg", e.getLocalizedMessage());
				jsonObject.put("Code", 0);
			}
		} else {
			jsonObject.put("Code", 0);
			jsonObject.put("Msg", "not find server:" + sid);
		}


		return jsonObject.toJSONString();

	}
}
