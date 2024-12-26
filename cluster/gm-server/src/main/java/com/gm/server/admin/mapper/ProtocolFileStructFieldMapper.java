package com.gm.server.admin.mapper;

import com.gm.server.admin.domain.ProtocolFileStructField;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author wishcher tree
 * @date 2022/12/12 17:07
 */
public interface ProtocolFileStructFieldMapper {
    public List<ProtocolFileStructField> selectProtocolFileStructFieldList(ProtocolFileStructField protocolFileStructField);
    public ProtocolFileStructField selectProtocolFileStructFieldById(Integer id);
    public void insertProtocolFileStructField(ProtocolFileStructField protocolFileStructField);

    public void insertProtocolFileStructFieldBatch(@Param("fields") List<ProtocolFileStructField> fields);
    public int updateProtocolFileStructField(ProtocolFileStructField protocolFileStructField);

    public int updateProtocolFileStructFieldBatch(@Param("fields") List<ProtocolFileStructField> fields);
    public int deleteProtocolFileStructFieldById(Integer id);

    public int deleteProtocolFileStructFieldByIds(Integer[] pids);

    public List<ProtocolFileStructField> selectProtocolFileStructFieldByName(String[] fieldNames);

    public List<ProtocolFileStructField> selectStructFieldByMoreCondition(@Param("fieldIds") Integer[] fieldIds, @Param("structNames") String[] structNames);
}
