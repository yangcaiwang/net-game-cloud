package com.ycw.core.network.jetty.httpCmd;

import com.ycw.core.network.jetty.http.HttpSession;

/**
 * <http命令接口>
 * <p>
 *
 * @author <yangcaiwang>
 * @version <1.0>
 */
public interface SuperHttpCmd {
    void running(HttpSession httpSession);

    boolean execute(HttpSession httpSession);
}
