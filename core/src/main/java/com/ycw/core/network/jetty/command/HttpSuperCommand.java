package com.ycw.core.network.jetty.command;

import com.ycw.core.internal.loader.service.AbstractService;
import com.ycw.core.network.jetty.http.HttpSession;

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
