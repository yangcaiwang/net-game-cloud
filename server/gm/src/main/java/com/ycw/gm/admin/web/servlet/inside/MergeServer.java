package com.ycw.gm.admin.web.servlet.inside;

import com.ycw.gm.admin.web.servlet.inside.anno.FuncName;

import java.util.Map;

@FuncName(name = "merge_server")
public class MergeServer extends InsideServlet {
    @Override
    public String process(Map<String, Object> map) throws Exception {
//        int mainSid = TypeUtils.castToInt(map.get("serverId"));
//        String mergedServerIds = TypeUtils.castToString(map.get("mergedServerIds"));
//
//        ConfigUtil.loadConf(PropertyConfig.getPathname("merge.config", "resources/merge/merger.cnf"));
//
//        List<Integer> subServerList = new ArrayList<>();
//        String[] split = mergedServerIds.split("\\|");
//        for (String sid : split) {
//            subServerList.add(Integer.parseInt(sid));
//        }
//
//        List<Integer> allServer = new ArrayList<>(subServerList);
//        if (!allServer.contains(mainSid)) {
//            allServer.add(mainSid);
//        }
//
//        BackstageGSRVRepository backstageGSRVRepository = Repositories.getRepository(BackstageGSRV.class);
//        Map<Integer, BackstageGSRV> collect = backstageGSRVRepository.getAllServerEntityByStatus().stream().collect(Collectors.toMap(BackstageGSRV::getSid, v -> v));
//        BackstageGSRV mainServer = collect.get(mainSid);
//        if (mainServer == null || mainServer.getDbUrl() == null || mainServer.getDbUrl().isEmpty()) {
//            log.error("main server empty:{}", mainSid);
//            return;
//        }
//        ISysServerService service = Services.getService(ISysServerService.class);
//        for (int serverId : allServer) {
//            BackstageGSRV backstageGSRV = collect.get(serverId);
//            if (backstageGSRV != null) {
//                try {
//                    String s = service.stopServer(backstageGSRV);
//                    log.error("opt server stop:{}", s);
//                } catch (Exception e) {
//                    log.error(e.getMessage() + ", merger error", e);
//                    return;
//                }
//            }
//        }
//
//        Map<Integer, DatabaseConfig> childConfigMap = new HashMap<>();
//        for (int serverId : subServerList) {
//            BackstageGSRV backstageGSRV = collect.get(serverId);
//            if (backstageGSRV != null) {
//                DatabaseConfig databaseConfig = DatabaseConfig.valueOf(serverId, backstageGSRV.getDbUrl(), backstageGSRV.getDbUser(), backstageGSRV.getDbPass());
//                childConfigMap.put(serverId, databaseConfig);
//            }
//        }
//
//        DatabaseConfig databaseConfig = DatabaseConfig.valueOf(mainSid, mainServer.getDbUrl(), mainServer.getDbUser(), mainServer.getDbPass());
//        String url = databaseConfig.getUrl();
//        String[] urlSplit = url.split("\\?");
//        String dbInfo = urlSplit[0];
//        String dbSourceName = dbInfo.substring(dbInfo.lastIndexOf("/") + 1);
//        String newDbName = "db_game_merge" + "_" + databaseConfig.getServerId() + "_" + DateUnit.timeFormat("yyyyMMdd", System.currentTimeMillis());
//        String newUrl = url.replaceAll(dbSourceName, newDbName);
//        databaseConfig.setUrl(newUrl);
//
//        MergeManager.getInstance().doMergeRole(databaseConfig, childConfigMap);
        log.error("merge all server success!!!");
        return succ().toJSONString();
    }
}
