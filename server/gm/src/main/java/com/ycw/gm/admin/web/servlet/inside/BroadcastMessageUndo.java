package com.ycw.gm.admin.web.servlet.inside;

import com.alibaba.fastjson.JSONObject;
import com.ycw.gm.admin.domain.GmServer;
import com.ycw.gm.admin.web.servlet.inside.anno.FuncName;
import com.ycw.gm.common.utils.ParamParseUtils;
import org.apache.commons.lang3.Validate;

import java.util.List;
import java.util.Map;

@FuncName(name = "broadcast_undo")
public class BroadcastMessageUndo extends InsideServlet {

	@Override
	public String process(Map<String, Object> map) throws Exception {
		log.error("context data: {}", map);
		Validate.isTrue(map.containsKey("sid"), "sid not exists");
		String pid = map.containsKey("pid") ? castToString(map.get("pid")) : "-1";
		String sid = castToString(map.get("sid"));
		List<GmServer> gsrvs = gsrvs(pid, sid);
//		Validate.isTrue(!CollectionUtils.isEmpty(gsrvs), "gsrvs not found , pid=%s and sid=%s", pid, sid);

		map.put("cmd", "BroadcastMessage");
		map.put("onlyOnce", "false");
		map.put("type", 2);
		JSONObject jsonObject = new JSONObject();
		for (GmServer gsrv : gsrvs) {
			String url = ParamParseUtils.makeURL(gsrv.getInHost(), gsrv.getInPort(), "script");
			try {
				ParamParseUtils.sendSyncTokenPost(url, map);
				jsonObject.put("Code", 1);
			} catch (Exception e) {
				jsonObject.put("Msg", e.getLocalizedMessage());
				jsonObject.put("Code", 0);
			}
		}
		return jsonObject.toJSONString();

	}
}
