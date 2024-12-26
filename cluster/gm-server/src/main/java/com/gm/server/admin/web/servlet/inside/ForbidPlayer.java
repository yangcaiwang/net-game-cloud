package com.gm.server.admin.web.servlet.inside;

import com.gm.server.admin.domain.GmServer;
import com.gm.server.admin.web.servlet.constant.ConstForbidType;
import com.gm.server.admin.web.servlet.inside.anno.FuncName;
import com.gm.server.common.utils.ParamParseUtils;
import org.apache.commons.lang3.Validate;

import java.util.List;
import java.util.Map;

/**
 * 封号
 * 
 * @author ljs
 *
 */
@FuncName(name = "ban")
public class ForbidPlayer extends InsideServlet {

	@Override
	public String process(Map<String, Object> map) throws Exception {

		// rid:针对角色id封禁，多个玩家用|分开
		// tags:0解禁;1封禁
//		String context = HttpUtils.getUTF8Body(req);
		log.error("context data: {}", map);
		Validate.isTrue(map.containsKey("rid"), "rid not exists");
		Validate.isTrue(map.containsKey("tags"), "tags not exists");
		Validate.isTrue(map.containsKey("time"), "time not exists");

//		String rid = TypeUtils.castToString(map.get("rid"));
//		Integer tags = TypeUtils.castToInt(map.get("tags"));
//		String t = TypeUtils.castToString(map.get("time"));
//		long timeout = StringUtils.isEmpty(t) ? TimeUnit.DAYS.toSeconds(3) : t.length() < 13 ? Long.parseLong(t) * 1000L : Long.parseLong(t);

//		IBackstageService backstageService = getService(IBackstageService.class);
		String pid = map.containsKey("pid") ? castToString(map.get("pid")) : "-1";
		String sid = castToString(map.get("sid"));
		List<GmServer> gsrvs = gsrvs(pid, sid);
		if (gsrvs.isEmpty()) {
			return fail(-1, String.format("server %s not found", sid)).toJSONString();
		}
		try {
//			if (rid != null) {
//				String[] rids = rid.split("\\|");
//				for (String _rid : rids) {
//					backstageService.forbidChat(Long.parseLong(_rid), timeout, tags, IBackstageService.FORBID_TYPE_LOGIN);
//				}
//			}
			map.put("cmd", "ForbidPlayer");
			map.put("onlyOnce", "false");
			map.put("type", ConstForbidType.FORBID_TYPE_LOGIN);
			for (GmServer gsrv : gsrvs) {
				String url = ParamParseUtils.makeURL(gsrv.getInHost(), gsrv.getInPort(), "script");
				try {
					ParamParseUtils.sendSyncTokenPost(url, map);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			return fail(0, e.getMessage()).toString();
		}
		return succ().toJSONString();
	}

}
