package com.gm.server.admin.mapper;

import com.gm.server.admin.domain.GmWhiteList;

import java.util.List;

/**
 * 白名单Mapper接口
 * 
 * @author gamer
 * @date 2022-07-29
 */
public interface GmWhiteListMapper 
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
     * 删除白名单
     * 
     * @param id 白名单主键
     * @return 结果
     */
    public int deleteGmWhiteListById(Long id);

    /**
     * 批量删除白名单
     * 
     * @param ids 需要删除的数据主键集合
     * @return 结果
     */
    public int deleteGmWhiteListByIds(Long[] ids);
}
