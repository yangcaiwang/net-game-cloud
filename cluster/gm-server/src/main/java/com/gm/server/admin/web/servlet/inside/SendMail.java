package com.gm.server.admin.web.servlet.inside;

import com.gm.server.admin.domain.GmServer;
import com.gm.server.admin.domain.model.BackMail;
import com.gm.server.admin.domain.model.ItemPair;
import com.gm.server.admin.web.servlet.inside.anno.FuncName;
import com.gm.server.common.utils.ParamParseUtils;
import com.gm.server.framework.manager.AsyncManager;
import org.apache.commons.lang3.Validate;
import org.apache.logging.log4j.util.Strings;

import java.util.List;
import java.util.Map;
import java.util.TimerTask;

@FuncName(name = "send_mail")
public class SendMail extends InsideServlet {

	@Override
	public String process(Map<String, Object> map) throws Exception {
		log.error("send mail context data: {}", map);
		Validate.isTrue(map.containsKey("sid"), "sid not exists");
		String pid = map.containsKey("pid") ? castToString(map.get("pid")) : "-1";
		String sid = castToString(map.get("sid"));
		String cid = map.containsKey("cid") ? castToString(map.get("cid")) : "-1";
		List<GmServer> gsrvs = gsrvs(pid, sid);
		if (gsrvs.isEmpty()) {
			return fail(-1, String.format("server %s not found", sid)).toJSONString();
		}

		map.put("cmd", "SendMail");
		map.put("onlyOnce", "false");
		if (Strings.isEmpty(cid)) {
			cid = "-1";
		}
		map.put("cid", cid);
		String rid = map.containsKey("rid") ? castToString(map.get("rid")) : "-1";
		if (Strings.isEmpty(rid)) {
			rid = "-1";
			map.put("rid", rid);
		}
//		JSONObject jObject = new JSONObject();
//		jObject.put("Code", 1);
//		StringBuilder sb = new StringBuilder();
		for (GmServer gsrv : gsrvs) {
			AsyncManager.me().execute(new TimerTask() {
				@Override
				public void run() {
					String url = ParamParseUtils.makeURL(gsrv.getInHost(), gsrv.getInPort(), "script");
					try {
						ParamParseUtils.sendSyncTokenPost(url, map);
					} catch (Exception e) {
						log.error(e.getMessage(), e);
//						sb.append("pid:").append(pid).append(";sid:").append(sid).append(".").append(e.getLocalizedMessage());
					}
				}
			});
		}
//		if (sb.length() > 0) {
//			log.error("send fail msg:{}", sb.toString());
//			jObject.put("Msg", sb.toString());
//			jObject.put("Code", 0);
//		}
		return succ().toJSONString();
	}

	private BackMail creatBackMail(long addressee, String channel, String title, String context, long validTime, List<ItemPair> list) {
		BackMail backMail = new BackMail();
		backMail.setAddresseeId(addressee);
		backMail.setChannel(channel);
		backMail.setGenre(1); // 默认为系统邮件
		backMail.setTitle(title);
		backMail.setContext(context);
		backMail.setAnnex(list);
		backMail.setValidTime(validTime);
		return backMail;
	}
}
