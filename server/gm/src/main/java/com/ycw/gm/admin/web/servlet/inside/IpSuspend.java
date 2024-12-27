package com.ycw.gm.admin.web.servlet.inside;

import com.ycw.gm.admin.service.IGmWhiteListService;
import com.ycw.gm.admin.web.servlet.constant.ConstForbidType;
import com.ycw.gm.admin.web.servlet.inside.anno.FuncName;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Map;

/**
 * @author wangshucheng
 * @date 2020/9/28 17:13
 */
@FuncName(name = "ip_suspend")
public class IpSuspend extends InsideServlet {

    @Autowired
    private IGmWhiteListService whiteListService;

    @Override
    public String process(Map<String, Object> map) throws Exception {
//        String context = HttpUtils.getUTF8Body(req);
        log.error("context data: {}", map);
//        Validate.isTrue(map.containsKey("sid"), "sid not exists");
//        String sid = castToString(map.get("sid"));
//        String pid = map.containsKey("pid") ? castToString(map.get("pid")) : "-1";
        String tags = castToString(map.get("tags"));
        String ips = castToString(map.get("ip"));

        try {
            if ("1".equals(tags)) {
                whiteListService.addIpBlack(ConstForbidType.NORMAL_BLACK_IP_PLATFORM, ips);
            } else if ("0".equals(tags)) {
                whiteListService.removeBlackIp(ConstForbidType.NORMAL_BLACK_IP_PLATFORM, ips);
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return fail(1, e.getMessage()).toString();
        }
        return succ().toJSONString();
    }
}
