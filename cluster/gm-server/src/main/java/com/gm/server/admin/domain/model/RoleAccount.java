package com.gm.server.admin.domain.model;

import org.apache.logging.log4j.util.Strings;

import java.io.Serializable;
import java.util.*;

/**
 * @author wishcher tree
 * @date 2022/7/20 11:48
 */
public class RoleAccount implements Serializable {

    private Map<Integer, Map<Integer, RoleServer>> bindServerMap = new HashMap<>();

    public Map<Integer, Map<Integer, RoleServer>> getBindServerMap() {
        return bindServerMap;
    }

    public void setBindServerMap(Map<Integer, Map<Integer, RoleServer>> bindServerMap) {
        this.bindServerMap = bindServerMap;
    }

    public List<RoleServer> getAllRoleServer(int pid) {
        Map<Integer, RoleServer> integerRoleServerMap = bindServerMap.get(pid);
        if (integerRoleServerMap != null) {
            return new ArrayList<>(integerRoleServerMap.values());
        }
        return Collections.emptyList();
    }

    public void bindAndUpdateInfo(int platformId, int serverId, long roleId, int template, String name, int level) {
        Map<Integer, RoleServer> integerRoleServerMap = bindServerMap.computeIfAbsent(platformId, v -> new HashMap<>());
        boolean first = !integerRoleServerMap.containsKey(serverId);
        RoleServer roleServer = integerRoleServerMap.computeIfAbsent(serverId, v -> new RoleServer());
        roleServer.setRoleId(String.valueOf(roleId));
        roleServer.setServerId(serverId);
        roleServer.setLevel(level);
        roleServer.setName(name);
        roleServer.setTemplate(template);
        roleServer.setLastLoginTime(System.currentTimeMillis());
        if (first) {
            roleServer.setTime(System.currentTimeMillis());
        }
    }

    public boolean updateInfo(int platformId, int serverId, String name, int level, long lastLoginTime, int template) {
        Map<Integer, RoleServer> integerRoleServerMap = bindServerMap.computeIfAbsent(platformId, v -> new HashMap<>());
        RoleServer roleServer = integerRoleServerMap.computeIfAbsent(serverId, v -> new RoleServer());
        boolean save = false;
        if (serverId != roleServer.getServerId()) {
            roleServer.setServerId(serverId);
            save = true;
        }
        if (level > 0 && level != roleServer.getLevel()) {
            roleServer.setLevel(level);
            save = true;
        }
        if (!Strings.isEmpty(name) && !name.equals(roleServer.getName())) {
            roleServer.setName(name);
            save = true;
        }
        if (lastLoginTime > 0L && lastLoginTime != roleServer.getLastLoginTime()) {
            roleServer.setLastLoginTime(lastLoginTime);
            save = true;
        }
        if (template > 0L && template != roleServer.getTemplate()) {
            roleServer.setTemplate(template);
            save = true;
        }
        return save;
    }

    public RoleServer getRoleServer(int platformId, int serverId) {
        Map<Integer, RoleServer> integerRoleServerMap = bindServerMap.computeIfAbsent(platformId, v -> new HashMap<>());
        return integerRoleServerMap.get(serverId);
    }

    public void removeBindServer(int platformId, int serverId) {
        Map<Integer, RoleServer> integerRoleServerMap = bindServerMap.get(platformId);
        if (integerRoleServerMap != null) {
            integerRoleServerMap.remove(serverId);
        }
    }

}
