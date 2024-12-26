package com.gm.server.admin.web.servlet.inside;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.gm.server.admin.domain.Gangs;
import com.gm.server.admin.domain.GmServer;
import com.gm.server.admin.service.IGangsService;
import com.gm.server.admin.web.servlet.inside.anno.FuncName;
import com.gm.server.framework.datasource.DatabaseSourceKeyConst;
import com.gm.server.framework.datasource.DynamicDataSource;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.Map;

/**
 * @author tree
 * @date 2022/2/18 16:40
 */
@FuncName(name = "show_gang_info")
public class ShowLegionInfo extends InsideServlet{

    @Autowired
    private IGangsService gangsService;

    @Autowired
    private DynamicDataSource dataSource;
    @Override
    public String process(Map<String, Object> map) throws Exception {
//		Validate.isTrue(map.containsKey("act"), "act not exists");
        if (!map.containsKey("sid")) {
            return fail(1, "sid is empty").toString();
        }

        JSONObject succ = succ();
        try {
            List<GmServer> gsrvs = gsrvs("-1", (String) map.get("sid"));
            if (gsrvs.isEmpty()) {
                return fail(0, "server is empty").toString();
            }
            GmServer server = gsrvs.get(0);
            List<Gangs> list;
            try {
                dataSource.initOtherDataSource(DatabaseSourceKeyConst.getGameKey(server.getServerKeyId()), server.getDbUrl(), server.getDbUser(), server.getDbPass());
                list = gangsService.selectGangsList(new Gangs());
            } catch (Exception e) {
                log.error(e.getMessage(), e);
                return fail(0, "server fail").toString();
            } finally {
                dataSource.cleanDataSource();
            }

            JSONArray array = new JSONArray();
            for (Gangs entity : list) {
                JSONObject object = new JSONObject();
                object.put("gangsId", entity.getGangsId());
                object.put("gangsName", entity.getGangsName());
                object.put("gangsAdminId", entity.getGangsAdminId());
                object.put("level", entity.getLevel());
                object.put("notice", entity.getNotice());
                array.add(object);
            }
            succ.put("Data", array.toString());
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return fail(0, "error find general info").toString();
        }
        return succ.toJSONString();
    }

}
