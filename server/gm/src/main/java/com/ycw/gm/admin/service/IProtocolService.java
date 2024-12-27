package com.ycw.gm.admin.service;

import com.ycw.gm.admin.domain.ProtocolCmd;
import com.ycw.gm.admin.domain.ProtocolFileDesc;
import com.ycw.gm.admin.domain.ProtocolFileStruct;
import com.ycw.gm.admin.domain.ProtocolFileStructField;

import java.util.List;

/**
 * @author wishcher tree
 * @date 2022/12/12 18:05
 */
public interface IProtocolService {
    List<ProtocolCmd> selectProtocolCmdList(ProtocolCmd protocolCmd);
    ProtocolCmd selectProtocolCmdById(Integer id, String fileVersion);
    List<ProtocolCmd> selectProtocolCmdByMoreId(Integer[] ids, String fileVersion);
    void insertProtocolCmd(ProtocolCmd protocolCmd);
    int updateProtocolCmd(ProtocolCmd protocolCmd);
    int deleteProtocolCmdById(Integer id, String cmdVersion);
    int deleteProtocolCmdByVersion(String version);

    List<ProtocolFileDesc> selectProtocolFileDescList(ProtocolFileDesc protocolFileDesc);
    ProtocolFileDesc selectProtocolFileDescById(Integer id);
    void insertProtocolFileDesc(ProtocolFileDesc protocolFileDesc);
    int updateProtocolFileDesc(ProtocolFileDesc protocolFileDesc);
    int deleteProtocolFileDescById(Integer id);
    int deleteProtocolFileDescByVersion(String version);

    List<ProtocolFileStructField> selectProtocolFileStructFieldList(ProtocolFileStructField protocolFileStructField);
    ProtocolFileStructField selectProtocolFileStructFieldById(Integer id);
    void insertProtocolFileStructField(ProtocolFileStructField protocolFileStructField);
    int updateProtocolFileStructField(ProtocolFileStructField protocolFileStructField);
    int deleteProtocolFileStructFieldById(Integer id);

    List<ProtocolFileStruct> selectProtocolFileStructList(ProtocolFileStruct protocolFileStruct);
    ProtocolFileStruct selectProtocolFileStructById(Integer id);
    void insertProtocolFileStruct(ProtocolFileStruct protocolFileStruct);
    int updateProtocolFileStruct(ProtocolFileStruct protocolFileStruct);
    int deleteProtocolFileStructById(Integer id);

    List<ProtocolFileStructField> selectStructFieldByMoreCondition(List<Integer> fileIds, List<String> structNames);

    void insertProtocolFileStructFieldBatch(List<ProtocolFileStructField> fields);
    void updateProtocolFileStructFieldBatch(List<ProtocolFileStructField> fields);

    int deleteProtocolFileStructFieldByIds(Integer[] pids);

    List<ProtocolFileStructField> selectProtocolFileStructFieldByName(List<String> fieldNames);

}
