package com.gm.server.admin.web.servlet.inside;

import com.gm.server.admin.service.IGmWhiteListService;
import com.gm.server.admin.web.servlet.inside.anno.FuncName;
import com.gm.server.common.utils.StringUtils;
import io.netty.util.internal.StringUtil;
import org.apache.commons.lang3.Validate;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Map;

/**
 * @author wangshucheng
 * @date 2020/9/28 17:13
 */
@FuncName(name = "ip_white")
public class IpWhite extends InsideServlet {

    @Autowired
    private IGmWhiteListService whiteListService;

    @Override
    public String process(Map<String, Object> map) throws Exception {
//        String context = HttpUtils.getUTF8Body(req);
        log.error("context data: {}", map);
        Validate.isTrue(map.containsKey("sid"), "sid not exists");
        String sid = castToString(map.get("sid"));
        String pid = map.containsKey("pid") ? castToString(map.get("pid")) : "-1";
        String tags = castToString(map.get("tags"));
        String ips = castToString(map.get("ip"));

        try {
            if (!StringUtil.isNullOrEmpty(ips)) {
                whiteListService.setBackstageIpWhite(Integer.parseInt(pid), ips);
            }

            if (!StringUtils.isEmpty(tags)) {
                whiteListService.setBackstageIpWhiteFlag(Integer.parseInt(pid), "1".equals(tags));
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return fail(1, e.getMessage()).toString();
        }
        return succ().toJSONString();
    }
}
