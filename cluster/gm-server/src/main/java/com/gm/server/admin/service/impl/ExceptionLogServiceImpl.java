package com.gm.server.admin.service.impl;

import com.gm.server.common.utils.DateUtils;
import com.gm.server.admin.domain.ExceptionLog;
import com.gm.server.admin.mapper.ExceptionLogMapper;
import com.gm.server.admin.service.IExceptionLogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 服务器异常记录Service业务层处理
 * 
 * @author gamer
 * @date 2022-08-01
 */
@Service
public class ExceptionLogServiceImpl implements IExceptionLogService 
{
    @Autowired
    private ExceptionLogMapper exceptionLogMapper;

    /**
     * 查询服务器异常记录
     * 
     * @param id 服务器异常记录主键
     * @return 服务器异常记录
     */
    @Override
    public ExceptionLog selectExceptionLogById(Long id)
    {
        return exceptionLogMapper.selectExceptionLogById(id);
    }

    /**
     * 查询服务器异常记录列表
     * 
     * @param exceptionLog 服务器异常记录
     * @return 服务器异常记录
     */
    @Override
    public List<ExceptionLog> selectExceptionLogList(ExceptionLog exceptionLog)
    {
        return exceptionLogMapper.selectExceptionLogList(exceptionLog);
    }

    /**
     * 新增服务器异常记录
     * 
     * @param exceptionLog 服务器异常记录
     * @return 结果
     */
    @Override
    public int insertExceptionLog(ExceptionLog exceptionLog)
    {
        exceptionLog.setCreateTime(DateUtils.getNowDate());
        return exceptionLogMapper.insertExceptionLog(exceptionLog);
    }

    /**
     * 修改服务器异常记录
     * 
     * @param exceptionLog 服务器异常记录
     * @return 结果
     */
    @Override
    public int updateExceptionLog(ExceptionLog exceptionLog)
    {
        exceptionLog.setUpdateTime(DateUtils.getNowDate());
        return exceptionLogMapper.updateExceptionLog(exceptionLog);
    }

    /**
     * 批量删除服务器异常记录
     * 
     * @param ids 需要删除的服务器异常记录主键
     * @return 结果
     */
    @Override
    public int deleteExceptionLogByIds(Long[] ids)
    {
        return exceptionLogMapper.deleteExceptionLogByIds(ids);
    }

    /**
     * 删除服务器异常记录信息
     * 
     * @param id 服务器异常记录主键
     * @return 结果
     */
    @Override
    public int deleteExceptionLogById(Long id)
    {
        return exceptionLogMapper.deleteExceptionLogById(id);
    }

    @Override
    public void cleanExceptionLog() {
        exceptionLogMapper.cleanExceptionLog();
    }
}
