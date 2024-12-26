package com.gm.server.admin.web.servlet.inside;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.gm.server.admin.domain.ExceptionLog;
import com.gm.server.admin.service.IExceptionLogService;
import com.gm.server.admin.web.servlet.inside.anno.FuncName;
import org.apache.commons.lang3.Validate;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.Map;

/**
 * @author tree
 * @date 2022/2/18 16:40
 */
@FuncName(name = "show_err_msg")
public class ShowErrorMsg extends InsideServlet{

    @Autowired
    private IExceptionLogService exceptionLogService;
    @Override
    public String process(Map<String, Object> map) throws Exception {
//		Validate.isTrue(map.containsKey("act"), "act not exists");
        Validate.isTrue(map.containsKey("sid"), "rid not exists");
        Validate.isTrue(map.containsKey("page"), "srcRid not exists");

        JSONObject succ = succ();
        try {
//            String pid = map.containsKey("pid") ? TypeUtils.castToString(map.get("pid")) : "-1";
            String sid = castToString(map.get("sid"));
            int page = castToInt(map.get("page"));

            List<ExceptionLog> exceptionLogs = exceptionLogService.selectExceptionLogList(new ExceptionLog());


            JSONArray array = new JSONArray();
            for (ExceptionLog exceptionLogEntity : exceptionLogs) {
                JSONObject object = new JSONObject();
                object.put("sid", sid);
                object.put("time", exceptionLogEntity.getCreateTime());
                object.put("exception", exceptionLogEntity.getException());
                array.add(object.toJSONString());
            }
//            succ.put("Data", array.toJSONString());
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return fail(1, e.getMessage()).toString();
        }
        return succ.toJSONString();
    }


}
