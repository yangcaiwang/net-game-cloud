package com.gm.server.admin.service.impl;

import com.gm.server.common.constant.Constants;
import com.gm.server.common.core.redis.RedisCache;
import com.gm.server.common.utils.DateUtils;
import com.gm.server.common.utils.StringUtils;
import com.gm.server.common.utils.ip.IpUtils;
import com.gm.server.admin.domain.GmWhiteList;
import com.gm.server.admin.mapper.GmWhiteListMapper;
import com.gm.server.admin.service.IGmWhiteListService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * 白名单Service业务层处理
 * 
 * @author gamer
 * @date 2022-07-29
 */
@Service
public class GmWhiteListServiceImpl implements IGmWhiteListService 
{
    @Autowired
    private GmWhiteListMapper gmWhiteListMapper;

    @Autowired
    private RedisCache redisCache;

    /**
     * 查询白名单
     * 
     * @param id 白名单主键
     * @return 白名单
     */
    @Override
    public GmWhiteList selectGmWhiteListById(Long id)
    {
        return gmWhiteListMapper.selectGmWhiteListById(id);
    }

    /**
     * 查询白名单列表
     * 
     * @param gmWhiteList 白名单
     * @return 白名单
     */
    @Override
    public List<GmWhiteList> selectGmWhiteListList(GmWhiteList gmWhiteList)
    {
        return gmWhiteListMapper.selectGmWhiteListList(gmWhiteList);
    }

    /**
     * 新增白名单
     * 
     * @param gmWhiteList 白名单
     * @return 结果
     */
    @Override
    public int insertGmWhiteList(GmWhiteList gmWhiteList)
    {
        gmWhiteList.setCreateTime(DateUtils.getNowDate());
        return gmWhiteListMapper.insertGmWhiteList(gmWhiteList);
    }

    /**
     * 修改白名单
     * 
     * @param gmWhiteList 白名单
     * @return 结果
     */
    @Override
    public int updateGmWhiteList(GmWhiteList gmWhiteList)
    {
        gmWhiteList.setUpdateTime(DateUtils.getNowDate());
        return gmWhiteListMapper.updateGmWhiteList(gmWhiteList);
    }

    /**
     * 批量删除白名单
     * 
     * @param ids 需要删除的白名单主键
     * @return 结果
     */
    @Override
    public int deleteGmWhiteListByIds(Long[] ids)
    {
        return gmWhiteListMapper.deleteGmWhiteListByIds(ids);
    }

    /**
     * 删除白名单信息
     * 
     * @param id 白名单主键
     * @return 结果
     */
    @Override
    public int deleteGmWhiteListById(Long id)
    {
        return gmWhiteListMapper.deleteGmWhiteListById(id);
    }

    /**
     * 设置白名单
     *
     * @param platformId
     *            平台/渠道id
     * @return
     */
    public void setBackstageIpWhite(int platformId, String ipWhite) {
        if (StringUtils.isNotEmpty(ipWhite)) {
            addBackstageIpWhite(platformId, ipWhite);
        }
    }

    public void addBackstageIpWhite(int platformId, String ipWhite) {
        if (StringUtils.isNotEmpty(ipWhite)) {
            String[] split = ipWhite.split("[\\|,]");
            String pk = getIpWhiteKey(platformId);
            List<Object> cacheList = redisCache.getCacheList(pk);
            boolean add = false;
            for (String ip : split) {
                if (!cacheList.contains(ip)) {
                    cacheList.add(ip);
                    add = true;
                }
            }
            if (add) {
                redisCache.setCacheList(pk, cacheList);
            }
        }
    }

    private String getIpWhiteKey(int platformId) {
        return Constants.backstage_ip_white_prefix + "-" + platformId + "-" + 99999;
    }

    @Override
    public void removeBackstageIpWhite(int platformId, String ipWhite) {
        if (StringUtils.isNotEmpty(ipWhite)) {
            String[] split = ipWhite.split("[\\|,]");
            String pk = getIpWhiteKey(platformId);
            List<Object> cacheList = redisCache.getCacheList(pk);
            for (String ip : split) {
                cacheList.remove(ip);
            }
            redisCache.setCacheList(pk, cacheList);
        }
    }

    /**
     * 获取ip白名单
     * @param platformId
     */
    public List<Object> getBackstageIpWhite(int platformId) {
        String pk = getIpWhiteKey(platformId);
        List<Object> cacheList = redisCache.getCacheList(pk);
        return new ArrayList<>(cacheList);
    }

    public void addBackstageAccountWhite(int platformId, String accountWhite) {
        if (StringUtils.isNotEmpty(accountWhite)) {
            String[] split = accountWhite.split("[\\|,]");
            String pk = getAccountWhiteFlagKey(platformId);
            List<Object> cacheList = redisCache.getCacheList(pk);
            boolean add = false;
            for (String ip : split) {
                if (!cacheList.contains(ip)) {
                    cacheList.add(ip);
                    add = true;
                }
            }
            if (add) {
                redisCache.setCacheList(pk, cacheList);
            }
        }
    }

    private String getAccountWhiteFlagKey(int platformId) {
        return Constants.backstage_account_white_prefix +"-" + platformId;
    }

    @Override
    public void removeBackstageAccountWhite(int platformId, String accountWhite) {
        if (accountWhite != null) {
            String[] split = accountWhite.split("[\\|,]");
            String pk = getAccountWhiteFlagKey(platformId);
            List<Object> cacheList = redisCache.getCacheList(pk);
            for (String ip : split) {
                cacheList.remove(ip);
            }
            redisCache.setCacheList(pk, cacheList);
        }
    }

    public List<Object> getBackstageAccountWhite(int platformId) {
        String pk = getAccountWhiteFlagKey(platformId);
        List<Object> cacheList = redisCache.getCacheList(pk);
        if (cacheList == null) {
            return null;
        }
        return new ArrayList<>(cacheList);
    }

    /**
     * 设置白名单开关
     *
     * @param platformId
     *            平台/渠道id
     * @return
     */
    public void setBackstageIpWhiteFlag(int platformId, boolean flag) {
        String pkFlag = getIpFlagKey(platformId);
        redisCache.setCacheObject(pkFlag, flag ? 1 : 0);
    }

    private String getIpFlagKey(int platformId) {
        return Constants.backstage_ip_white_prefix_flag + "-" + platformId;
    }

    @Override
    public boolean checkIsInIpWhiteList(int platformId, String ip, String account) {
        String pkFlag = getIpFlagKey(platformId);
        Integer atomicLong = redisCache.getCacheObject(pkFlag);
        if (atomicLong == null || atomicLong != 1) {
            return false;
        }
        List<Object> backstageAccountWhite = getBackstageAccountWhite(platformId);
        if (backstageAccountWhite != null && backstageAccountWhite.contains(account)) {
            return true;
        }

        List<Object> backstageIpWhite = getBackstageIpWhite(platformId);
        if (backstageIpWhite != null) {
            for (Object str : backstageIpWhite) {
                if (IpUtils.isInRange(ip, String.valueOf(str))) {
                    return true;
                }
            }
        }

        return false;
    }

    /////////////////// 黑名单ip /////////////////////
    @Override
    public void addIpBlack(int platformId, String ipStr) {
        if (StringUtils.isNotEmpty(ipStr)) {
            String[] split = ipStr.split("[\\|,]");
            String pk = getBlackIpKey(platformId);
            List<Object> cacheList = redisCache.getCacheList(pk);
            boolean add = false;
            for (String ip : split) {
                if (!cacheList.contains(ip)) {
                    cacheList.add(ip);
                    add = true;
                }
            }
            if (add) {
                redisCache.setCacheList(pk, cacheList);
            }
        }
    }

    private String getBlackIpKey(int platformId) {
        return Constants.IP_BLACK_PRE +"-" + platformId;
    }

    @Override
    public void removeBlackIp(int platformId, String ipWhite) {
        if (StringUtils.isNotEmpty(ipWhite)) {
            String[] split = ipWhite.split("[\\|,]");
            String pk = getBlackIpKey(platformId);
            List<Object> cacheList = redisCache.getCacheList(pk);
            for (String ip : split) {
                cacheList.remove(ip);
            }
            redisCache.setCacheList(pk, cacheList);
        }
    }

    @Override
    public boolean checkIsInIpBlackList(int platformId, String ip) {
        String pk = getBlackIpKey(platformId);
        List<Object> cacheList = redisCache.getCacheList(pk);
        if (cacheList != null && !cacheList.isEmpty()) {
            for (Object str : cacheList) {
                if (IpUtils.isInRange(ip, String.valueOf(str))) {
                    return true;
                }
            }
        }

        return false;
    }
}
