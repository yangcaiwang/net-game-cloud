package com.ycw.core.network.jetty.command;

import com.ycw.core.network.jetty.http.HttpSession;

/**
 * <http命令接口>
 * <p>
 *
 * @author <yangcaiwang>
 * @version <1.0>
 */
public interface SuperHttpCommand {
    void running(HttpSession httpSession);

    boolean execute(HttpSession httpSession);
}
