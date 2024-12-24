package com.common.module.network.jetty.command;

import com.common.module.network.jetty.http.HttpCode;
import com.common.module.network.jetty.http.HttpSession;

public abstract class AbsHttpCommand extends HttpSuperCommand {

	@Override
	public void running(HttpSession httpSession) {
		if (execute(httpSession)) {
			return;
		}
		httpSession.sendHttpResponseError(HttpCode.SYSTEM_ERROR);
	}

	protected abstract boolean execute(HttpSession httpSession);
}
