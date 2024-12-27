package com.ycw.gm.admin.web.servlet.inside;

import com.alibaba.fastjson.JSONObject;
import com.ycw.gm.admin.domain.GmServer;
import com.ycw.gm.admin.web.servlet.inside.anno.FuncName;
import com.ycw.gm.common.utils.ParamParseUtils;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.Validate;

import java.util.List;
import java.util.Map;

@FuncName(name = "send_item")
public class AddItem extends InsideServlet {

	@Override
	public String process(Map<String, Object> map) throws Exception {
		log.error("context data: {}", map);
		Validate.isTrue(map.containsKey("sid"), "sid not exists");
		String pid = map.containsKey("pid") ? castToString(map.get("pid")) : "-1";
		String sid = castToString(map.get("sid"));
		List<GmServer> gsrvs = gsrvs(pid, sid);
		Validate.isTrue(!CollectionUtils.isEmpty(gsrvs), "gsrvs not found , pid=%d and sid=%d", pid, sid);


		Object rid = map.get("rid");
		if (rid == null) {
			JSONObject obj = fail(-1, "not role select");
			return obj.toJSONString();
		}
		Object item = map.get("items");
		if (item == null) {
			JSONObject obj = fail(-1, "not Item select");
			return obj.toJSONString();
		}

		map.put("cmd", "AddItem");
		map.put("onlyOnce", "false");
        JSONObject jObject = new JSONObject();
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
