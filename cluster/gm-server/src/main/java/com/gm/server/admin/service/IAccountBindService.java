package com.gm.server.admin.service;

import com.gm.server.admin.domain.model.RoleAccount;
import com.gm.server.admin.domain.model.RoleServer;

/**
 * @author wishcher tree
 * @date 2022/7/20 13:36
 */
public interface IAccountBindService {

    void registerAccount(String account);
    boolean bindAccount(String account, int platformId, int serverId, long roleId, int template, String name, int level);

    boolean updateAccountInfo(String account, int platformId, int serverId, String name, int level, long lastLoginTime, int template);

    boolean checkAccountExist(String account);

    RoleServer getRoleServer(String account, int platformId, int serverId);
    void removeRoleServer(String account, int platformId, int serverId);

    RoleAccount getRoleAccount(String account);

    int recordIpCount(String ip);
}
