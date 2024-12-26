package com.gm.server.admin.web.servlet.inside;

import com.gm.server.admin.domain.GmServer;
import com.gm.server.admin.web.servlet.constant.ConstForbidType;
import com.gm.server.admin.web.servlet.inside.anno.FuncName;
import com.gm.server.common.utils.ParamParseUtils;
import org.apache.commons.lang3.Validate;

import java.util.List;
import java.util.Map;


/**
 * 禁言
 * 
 * @author ljs
 *
 */
@FuncName(name = "forbid")
public class ForbidChat extends InsideServlet {

	@Override
	public String process(Map<String, Object> map) throws Exception {

		// act:帐号,针对账号封禁多个账号用|分开
		// rid:针对角色id封禁，多个玩家用|分开
		// tags:0解禁;1封禁
		log.error("context data: {}", map);
		Validate.isTrue(map.containsKey("rid"), "rid not exists");
		Validate.isTrue(map.containsKey("tags"), "flag not exists");
		Validate.isTrue(map.containsKey("time"), "time not exists");


		try {
			String pid = map.containsKey("pid") ? castToString(map.get("pid")) : "-1";
			String sid = castToString(map.get("sid"));
			List<GmServer> gsrvs = gsrvs(pid, sid);
			map.put("cmd", "ForbidPlayer");
			map.put("onlyOnce", "false");
			map.put("type", ConstForbidType.FORBID_TYPE_CHAT);
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
			return fail(1, e.getMessage()).toString();
		}

		return succ().toJSONString();
	}

}
