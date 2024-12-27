package com.ycw.gm.admin.web.servlet.inside;

import com.ycw.gm.admin.web.servlet.inside.anno.FuncName;

import java.util.Map;

/**
 * @author wangshucheng
 * @date 2020/10/19 19:40
 */
@FuncName(name = "system_notice")
public class SystemNotice extends InsideServlet {
    @Override
    public String process(Map<String, Object> map) throws Exception {
        log.error("send mail context data: {}", map);
        return succ().toJSONString();
    }
}
