package com.gm.server.admin.mapper;

import com.gm.server.admin.domain.GmAddItem;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 游戏管理  添加道具Mapper接口
 * 
 * @author gamer
 * @date 2022-07-26
 */
public interface GmAddItemMapper 
{
    /**
     * 查询游戏管理  添加道具
     * 
     * @param id 游戏管理  添加道具主键
     * @return 游戏管理  添加道具
     */
    public GmAddItem selectGmAddItemById(Long id);

    /**
     * 查询游戏管理  添加道具列表
     * 
     * @param gmAddItem 游戏管理  添加道具
     * @return 游戏管理  添加道具集合
     */
    public List<GmAddItem> selectGmAddItemList(GmAddItem gmAddItem);

    public List<GmAddItem> selectItemByIds(Long[] sids);

    /**
     * 新增游戏管理  添加道具
     * 
     * @param gmAddItem 游戏管理  添加道具
     * @return 结果
     */
    public int insertGmAddItem(GmAddItem gmAddItem);

    /**
     * 修改游戏管理  添加道具
     * 
     * @param gmAddItem 游戏管理  添加道具
     * @return 结果
     */
    public int updateGmAddItem(GmAddItem gmAddItem);

    public int updateGmAddItemStatus(@Param("item") GmAddItem gmAddItem, @Param("sid") Long[] ids);

    /**
     * 删除游戏管理  添加道具
     * 
     * @param id 游戏管理  添加道具主键
     * @return 结果
     */
    public int deleteGmAddItemById(Long id);

    /**
     * 批量删除游戏管理  添加道具
     * 
     * @param ids 需要删除的数据主键集合
     * @return 结果
     */
    public int deleteGmAddItemByIds(Long[] ids);
}
