package com.ycw.gm.admin.web.controller.system;

import com.ycw.gm.admin.web.servlet.ServletFuncMgr;
import com.ycw.gm.admin.web.servlet.inside.InsideServlet;
import com.ycw.gm.common.config.GamerConfig;
import com.ycw.gm.common.utils.ParamParseUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

/**
 * 首页
 *
 * @author gamer
 */
@RestController
public class SysIndexController
{
    protected final Logger logger = LoggerFactory.getLogger(this.getClass());
    /** 系统基础配置 */
    @Autowired
    private GamerConfig gamerConfig;

    @Autowired
    private ServletFuncMgr servletFuncMgr;

    /**
     * 访问首页，提示语
     */
    @RequestMapping("/")
    public String index(HttpServletRequest request)
    {
        try {
            if ("POST".equals(request.getMethod())) {
                String process = process(request);
                if (process != null && process.length() > 0) {
                    return process;
                }
            }
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
//            throw new RuntimeException(e);
        }

        return "roads not reach";
    }

    private String process(HttpServletRequest request) throws Exception {
        Map<String, Object> paramsMap = ParamParseUtils.getParamsMap(request);
        if (paramsMap == null) {
            return null;
        }
        String fun = (String) paramsMap.get("fun");
        if (fun == null) {
            return null;
        }
        InsideServlet servlet = servletFuncMgr.getServlet(fun);
        if (servlet == null) {
            return null;
        }
        logger.error("receive context data: {}", paramsMap);
        return servlet.process(paramsMap);
    }
}
