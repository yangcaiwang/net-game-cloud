package com.gm.server.admin.web.servlet.inside;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.gm.server.admin.domain.GmServer;
import com.gm.server.admin.domain.vo.GeneralVo;
import com.gm.server.admin.service.IPlayerService;
import com.gm.server.admin.web.servlet.inside.anno.FuncName;
import com.gm.server.framework.datasource.DatabaseSourceKeyConst;
import com.gm.server.framework.datasource.DynamicDataSource;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author tree
 * @date 2022/2/18 16:40
 */
@FuncName(name = "show_general")
public class ShowGeneralInfo extends InsideServlet{

    @Autowired
    private IPlayerService playerService;

    @Autowired
    private DynamicDataSource dataSource;
    @Override
    public String process(Map<String, Object> map) throws Exception {
//		Validate.isTrue(map.containsKey("act"), "act not exists");
        if (!map.containsKey("sid") || !map.containsKey("rid")) {
            return fail(1, "sid or rid is empty").toString();
        }
        long roleId = castToLong(map.get("rid"));

        JSONObject succ = succ();
        try {
            List<GmServer> gsrvs = gsrvs("-1", (String) map.get("sid"));
            if (gsrvs.isEmpty()) {
                return fail(1, "server is empty").toString();
            }
            GmServer server = gsrvs.get(0);
            List<GeneralVo> list;
            try {
                dataSource.initOtherDataSource(DatabaseSourceKeyConst.getGameKey(server.getServerKeyId()), server.getDbUrl(), server.getDbUser(), server.getDbPass());
                list = playerService.selectGeneralById(roleId, true);
            } catch (Exception e) {
                log.error(e.getMessage(), e);
                return fail(1, "server fail").toString();
            } finally {
                dataSource.cleanDataSource();
            }

            JSONArray array = new JSONArray();
            for (GeneralVo entity : list) {
                JSONObject object = new JSONObject();
                object.put("resId", entity.getResId());
                object.put("level", entity.getLevel());
                object.put("starLv", entity.getStarLv());
                object.put("combatPower", entity.getCombatPower());
                int[][] ints = JSON.parseObject(entity.getEquipArr(), int[][].class);
                Map<Integer, Integer> collect = Arrays.stream(ints).collect(Collectors.toMap(k -> k[0], v -> v[1]));
                object.put("equipmentMap", JSONObject.toJSONString(collect));
                object.put("armsLevel", entity.getArmsLevel());
                array.add(object);
            }
            succ.put("Data", array.toString());
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return fail(1, "error find general info").toString();
        }
        return succ.toJSONString();
    }

}
