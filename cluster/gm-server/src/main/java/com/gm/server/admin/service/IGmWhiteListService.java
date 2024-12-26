package com.gm.server.admin.service;

import com.gm.server.admin.domain.GmWhiteList;

import java.util.List;

/**
 * 白名单Service接口
 * 
 * @author gamer
 * @date 2022-07-29
 */
public interface IGmWhiteListService 
{
    /**
     * 查询白名单
     * 
     * @param id 白名单主键
     * @return 白名单
     */
    public GmWhiteList selectGmWhiteListById(Long id);

    /**
     * 查询白名单列表
     * 
     * @param gmWhiteList 白名单
     * @return 白名单集合
     */
    public List<GmWhiteList> selectGmWhiteListList(GmWhiteList gmWhiteList);

    /**
     * 新增白名单
     * 
     * @param gmWhiteList 白名单
     * @return 结果
     */
    public int insertGmWhiteList(GmWhiteList gmWhiteList);

    /**
     * 修改白名单
     * 
     * @param gmWhiteList 白名单
     * @return 结果
     */
    public int updateGmWhiteList(GmWhiteList gmWhiteList);

    /**
     * 批量删除白名单
     * 
     * @param ids 需要删除的白名单主键集合
     * @return 结果
     */
    public int deleteGmWhiteListByIds(Long[] ids);

    /**
     * 删除白名单信息
     * 
     * @param id 白名单主键
     * @return 结果
     */
    public int deleteGmWhiteListById(Long id);

    /**
     * 设置IP白名单
     * @param platformId
     * @param ipWhite
     */
    void setBackstageIpWhite(int platformId, String ipWhite);
    void addBackstageIpWhite(int platformId, String ipWhite);
    void removeBackstageIpWhite(int platformId, String ipWhite);

    /**
     * 设置白名单状态
     * @param platformId
     * @param flag
     */
    void setBackstageIpWhiteFlag(int platformId, boolean flag);

    boolean checkIsInIpWhiteList(int platformId, String ip, String account);

    void addBackstageAccountWhite(int platformId, String accountWhite);
    void removeBackstageAccountWhite(int platformId, String accountWhite);

    boolean checkIsInIpBlackList(int platformId, String ip);
    void removeBlackIp(int platformId, String ipWhite);
    void addIpBlack(int platformId, String ipStr);
}
