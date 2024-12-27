package com.ycw.gm.admin.web.servlet.inside;

import com.ycw.gm.admin.domain.GmServer;
import com.ycw.gm.admin.web.servlet.inside.anno.FuncName;
import com.ycw.gm.common.utils.ParamParseUtils;
import com.ycw.gm.framework.manager.AsyncManager;

import java.util.List;
import java.util.Map;
import java.util.TimerTask;

@FuncName(name = "push_gift_config_upload")
public class PushGiftConfigUpload extends InsideServlet {

	@Override
	public String process(Map<String, Object> map) throws Exception {
		log.error("context data: {}", map);
		String pid = map.containsKey("pid") ? castToString(map.get("pid")) : "-1";
		String sid = map.containsKey("sid") ? castToString(map.get("sid")) : "-1";
		List<GmServer> gsrvs = gsrvs(pid, sid);
		if (gsrvs.isEmpty()) {
			return fail(-1, String.format("server %s not found", sid)).toJSONString();
		}

		map.put("cmd", "PushGiftConfigUpload");
		map.put("onlyOnce", "false");
		for (GmServer gsrv : gsrvs) {
			AsyncManager.me().execute(new TimerTask() {
				@Override
				public void run() {
					String url = ParamParseUtils.makeURL(gsrv.getInHost(), gsrv.getInPort(), "script");
					try {
						ParamParseUtils.sendSyncTokenPost(url, map);
					} catch (Exception e) {
						log.error(e.getMessage(), e);
					}
				}
			});
		}
		return succ().toJSONString();
	}
}
