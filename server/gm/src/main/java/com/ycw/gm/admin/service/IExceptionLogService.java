package com.ycw.gm.admin.service;

import com.ycw.gm.admin.domain.ExceptionLog;

import java.util.List;

/**
 * 服务器异常记录Service接口
 * 
 * @author gamer
 * @date 2022-08-01
 */
public interface IExceptionLogService 
{
    /**
     * 查询服务器异常记录
     * 
     * @param id 服务器异常记录主键
     * @return 服务器异常记录
     */
    public ExceptionLog selectExceptionLogById(Long id);

    /**
     * 查询服务器异常记录列表
     * 
     * @param exceptionLog 服务器异常记录
     * @return 服务器异常记录集合
     */
    public List<ExceptionLog> selectExceptionLogList(ExceptionLog exceptionLog);

    /**
     * 新增服务器异常记录
     * 
     * @param exceptionLog 服务器异常记录
     * @return 结果
     */
    public int insertExceptionLog(ExceptionLog exceptionLog);

    /**
     * 修改服务器异常记录
     * 
     * @param exceptionLog 服务器异常记录
     * @return 结果
     */
    public int updateExceptionLog(ExceptionLog exceptionLog);

    /**
     * 批量删除服务器异常记录
     * 
     * @param ids 需要删除的服务器异常记录主键集合
     * @return 结果
     */
    public int deleteExceptionLogByIds(Long[] ids);

    /**
     * 删除服务器异常记录信息
     * 
     * @param id 服务器异常记录主键
     * @return 结果
     */
    public int deleteExceptionLogById(Long id);

    /**
     * 清空
     *
     */
    public void cleanExceptionLog();
}
