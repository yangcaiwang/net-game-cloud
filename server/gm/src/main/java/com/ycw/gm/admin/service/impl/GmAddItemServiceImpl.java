package com.ycw.gm.admin.service.impl;

import com.ycw.gm.admin.domain.GmAddItem;
import com.ycw.gm.admin.mapper.GmAddItemMapper;
import com.ycw.gm.admin.service.IGmAddItemService;
import com.ycw.gm.common.utils.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 游戏管理  添加道具Service业务层处理
 * 
 * @author gamer
 * @date 2022-07-26
 */
@Service
public class GmAddItemServiceImpl implements IGmAddItemService
{
    @Autowired
    private GmAddItemMapper gmAddItemMapper;

    /**
     * 查询游戏管理  添加道具
     * 
     * @param id 游戏管理  添加道具主键
     * @return 游戏管理  添加道具
     */
    @Override
    public GmAddItem selectGmAddItemById(Long id)
    {
        return gmAddItemMapper.selectGmAddItemById(id);
    }

    /**
     * 查询游戏管理  添加道具列表
     * 
     * @param gmAddItem 游戏管理  添加道具
     * @return 游戏管理  添加道具
     */
    @Override
    public List<GmAddItem> selectGmAddItemList(GmAddItem gmAddItem)
    {
        return gmAddItemMapper.selectGmAddItemList(gmAddItem);
    }

    @Override
    public List<GmAddItem> selectItemByIds(Long[] sids) {
        return gmAddItemMapper.selectItemByIds(sids);
    }

    /**
     * 新增游戏管理  添加道具
     * 
     * @param gmAddItem 游戏管理  添加道具
     * @return 结果
     */
    @Override
    public int insertGmAddItem(GmAddItem gmAddItem)
    {
        gmAddItem.setCreateTime(DateUtils.getNowDate());
        return gmAddItemMapper.insertGmAddItem(gmAddItem);
    }

    /**
     * 修改游戏管理  添加道具
     * 
     * @param gmAddItem 游戏管理  添加道具
     * @return 结果
     */
    @Override
    public int updateGmAddItem(GmAddItem gmAddItem)
    {
        gmAddItem.setUpdateTime(DateUtils.getNowDate());
        return gmAddItemMapper.updateGmAddItem(gmAddItem);
    }

    @Override
    public int updateGmAddItemStatus(GmAddItem gmAddItem, Long[] ids) {
        return gmAddItemMapper.updateGmAddItemStatus(gmAddItem, ids);
    }

    /**
     * 批量删除游戏管理  添加道具
     * 
     * @param ids 需要删除的游戏管理  添加道具主键
     * @return 结果
     */
    @Override
    public int deleteGmAddItemByIds(Long[] ids)
    {
        return gmAddItemMapper.deleteGmAddItemByIds(ids);
    }

    /**
     * 删除游戏管理  添加道具信息
     * 
     * @param id 游戏管理  添加道具主键
     * @return 结果
     */
    @Override
    public int deleteGmAddItemById(Long id)
    {
        return gmAddItemMapper.deleteGmAddItemById(id);
    }
}
