package com.gm.server.admin.service.impl;

import com.common.module.cluster.property.PropertyConfig;
import com.gm.server.common.constant.Constants;
import com.gm.server.common.core.redis.RedisCache;
import com.gm.server.admin.domain.model.RoleAccount;
import com.gm.server.admin.domain.model.RoleServer;
import com.gm.server.admin.service.IAccountBindService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

/**
 * @author wishcher tree
 * @date 2022/7/20 13:36
 */
@Service
public class AccountBindServiceImpl implements IAccountBindService {
    @Autowired
    private RedisCache redisCache;

    private String getAccountBindKey(String account) {
        return Constants.ACCOUNT_BIND + account;
    }


    public void registerAccount(String account) {
        String accountBindKey = getAccountBindKey(account);
        RoleAccount roleAccount = redisCache.getCacheObject(accountBindKey);
        if (roleAccount == null) {
            roleAccount = new RoleAccount();
            redisCache.setCacheObject(accountBindKey, roleAccount);
        }
    }

    @Override
    public boolean bindAccount(String account, int platformId, int serverId, long roleId, int template, String name, int level) {
        String accountBindKey = getAccountBindKey(account);
        RoleAccount roleAccount = redisCache.getCacheObject(accountBindKey);
        if (roleAccount == null) {
            roleAccount = new RoleAccount();
        }
        roleAccount.bindAndUpdateInfo(platformId, serverId, roleId, template, name, level);
        redisCache.setCacheObject(accountBindKey, roleAccount);
        return true;
    }

    public int recordIpCount(String ip) {
        String accountBindKey = "recordRegis:" + ip;
        Integer count = redisCache.getCacheObject(accountBindKey);
        if (count != null) {
            count += 1;
        } else {
            count = 1;
        }
        redisCache.setCacheObject(accountBindKey, count, PropertyConfig.getIntValue("record.ip.count.time", 5), TimeUnit.MINUTES);
        return count;
    }

    @Override
    public boolean updateAccountInfo(String account, int platformId, int serverId, String name, int level, long lastLoginTime, int template) {
        String accountBindKey = getAccountBindKey(account);
        RoleAccount roleAccount = redisCache.getCacheObject(accountBindKey);
        if (roleAccount != null) {
            boolean b = roleAccount.updateInfo(platformId, serverId, name, level, lastLoginTime, template);
            if (b) {
                redisCache.setCacheObject(accountBindKey, roleAccount);
            }
            return true;
        }
        return false;
    }

    @Override
    public boolean checkAccountExist(String account) {
        String accountBindKey = getAccountBindKey(account);
        return redisCache.getCacheObject(accountBindKey) != null;
    }

    @Override
    public RoleServer getRoleServer(String account, int platformId, int serverId) {
        String accountBindKey = getAccountBindKey(account);
        RoleAccount roleAccount = redisCache.getCacheObject(accountBindKey);
        if (roleAccount != null) {
            return roleAccount.getRoleServer(platformId, serverId);
        }
        return null;
    }

    @Override
    public void removeRoleServer(String account, int platformId, int serverId) {
        String accountBindKey = getAccountBindKey(account);
        RoleAccount roleAccount = redisCache.getCacheObject(accountBindKey);
        if (roleAccount != null) {
            roleAccount.removeBindServer(platformId, serverId);
            redisCache.setCacheObject(accountBindKey, roleAccount);
        }
    }

    @Override
    public RoleAccount getRoleAccount(String account) {
        String accountBindKey = getAccountBindKey(account);
        return redisCache.getCacheObject(accountBindKey);
    }
}
