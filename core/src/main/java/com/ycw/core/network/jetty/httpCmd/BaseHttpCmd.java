package com.ycw.core.network.jetty.httpCmd;

import com.ycw.core.internal.loader.service.AbstractService;
import com.ycw.core.network.jetty.http.HttpCode;
import com.ycw.core.network.jetty.http.HttpSession;

/**
 * <http命令抽象类>
 * <p>
 *
 * @author <yangcaiwang>
 * @version <1.0>
 */
public class BaseHttpCmd extends AbstractService implements SuperHttpCmd {
    @Override
    public void running(HttpSession httpSession) {
        if (execute(httpSession)) {
            return;
        }

        httpSession.sendHttpResponseError(HttpCode.SYSTEM_ERROR);
    }

    @Override
    public boolean execute(HttpSession httpSession) {
        return false;
    }
}
