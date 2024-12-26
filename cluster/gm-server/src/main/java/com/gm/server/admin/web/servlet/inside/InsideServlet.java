package com.gm.server.admin.web.servlet.inside;

import com.alibaba.fastjson.JSONObject;
import com.gm.server.admin.domain.GmServer;
import com.gm.server.admin.service.IServerService;
import com.gm.server.common.utils.ParamParseUtils;
import com.gm.server.common.utils.StringUtils;
import com.gm.server.common.utils.spring.SpringUtils;
import org.apache.logging.log4j.util.Strings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;

/**
 * 后台内部通讯接口父类
 * 
 *         ${tags}InsideServlet2020下午5:03:45com.pp.game.login.servlet.
 *         inside$
 */
public abstract class InsideServlet {

	protected static final Logger log = LoggerFactory.getLogger(InsideServlet.class);

	public abstract String process(Map<String, Object> map) throws Exception;

	/**
	 * pid:平台(1000-9999)，-1全平台,多个平台用|分开 sid:区服id(1000-9999),-1全服,多个区服用|分开
	 * 
	 * @param pid
	 * @param sid
	 * @return
	 */
	protected List<GmServer> gsrvs(String pid, String sid) {
		IServerService bean = SpringUtils.getBean(IServerService.class);
		return bean.gsrvs(pid, sid);
	}

	/**
	 * 成功,返回0
	 * 
	 * @return
	 */
	protected JSONObject succ() {
		JSONObject succ = new JSONObject();
		succ.put("Code", 1);
		return succ;
	}

	/**
	 * 失败返回异常码
	 * 
	 * @param errorCode
	 * @return
	 */
	protected JSONObject fail(int errorCode) {
		JSONObject succ = new JSONObject();
		succ.put("Code", errorCode);
		return succ;
	}

	/**
	 * 返回异常码并带上异常信息
	 * 
	 * @param errorCode
	 * @param exception
	 * @return
	 */
	protected JSONObject fail(int errorCode, Exception exception) {
		JSONObject succ = new JSONObject();
		succ.put("Code", errorCode);
		try {
			succ.put("Msg", exception == null ? "nil" : exception.getMessage());
		} catch (Exception e) {
		}
		return succ;
	}

	/**
	 * 返回异常码并带上异常信息
	 * 
	 * @param errorCode
	 * @param exception
	 * @return
	 */
	protected JSONObject fail(int errorCode, String exception, Object... params) {
		JSONObject succ = new JSONObject();
		succ.put("Code", errorCode);
		try {
			succ.put("Msg", StringUtils.isEmpty(exception) ? "nil" : String.format(exception, params));
		} catch (Exception e) {
		}
		return succ;
	}

	/**
	 * 简单的失败返回,异常码为1
	 * 
	 * @return
	 */
	protected JSONObject fail() {
		return fail(1);
	}

	protected Map<String, Object> getParamsMap(HttpServletRequest req) throws Exception {
		String param = req.getParameter("params");
		Map<String, Object> map;
		if (Strings.isEmpty(param)) {
			String context = ParamParseUtils.getUTF8Body(req);
			if (!Strings.isEmpty(context)) {
				map = ParamParseUtils.parsedRSAParamsString(context);
			} else {
				log.error("参数错误，不存在参数params，解析出错！！！1");
				throw new RuntimeException("错误的http参数信息");
//				return Collections.emptyMap();
//				map = new HashMap<>();
//				for (String key : req.getParameterMap().keySet()) {
//					map.put(key, req.getParameter(key));
//				}
			}
		} else {
			map = ParamParseUtils.parsedRSAString(param);
		}
		return map;
	}

	protected String castToString(Object obj) {
		if (obj == null) {
			return "";
		}
		return String.valueOf(obj);
	}

	protected Integer castToInt(Object obj) {
		return Integer.parseInt(castToString(obj));
	}

	protected Long castToLong(Object obj) {
		return Long.parseLong(castToString(obj));
	}

	protected Boolean castToBoolean(Object obj) {
		return Boolean.parseBoolean(castToString(obj));
	}
}
