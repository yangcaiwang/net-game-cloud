
package com.common.module.internal.script;

import com.alibaba.fastjson.JSONObject;
import com.common.module.internal.loader.service.IService;

/**
 * 脚本处理器
 */
public interface ScriptProcessor extends IService {

	/**
	 * 执行脚本逻辑
	 */
	String process(JSONObject request) throws Exception;
}
