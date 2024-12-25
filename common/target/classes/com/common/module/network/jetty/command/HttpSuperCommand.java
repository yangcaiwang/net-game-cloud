package com.common.module.network.jetty.command;

import com.common.module.internal.loader.service.AbstractService;
import com.common.module.network.jetty.http.HttpSession;

/**
 * <http命令抽象类>
 * <p>
 *
 * @author <yangcaiwang>
 * @version <1.0>
 */
public abstract class HttpSuperCommand extends AbstractService {

	public abstract void running(HttpSession httpSession);
}
