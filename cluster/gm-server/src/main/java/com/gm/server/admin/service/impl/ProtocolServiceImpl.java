package com.gm.server.admin.service.impl;

import com.gm.server.admin.domain.ProtocolCmd;
import com.gm.server.admin.domain.ProtocolFileDesc;
import com.gm.server.admin.domain.ProtocolFileStruct;
import com.gm.server.admin.domain.ProtocolFileStructField;
import com.gm.server.admin.mapper.ProtocolCmdMapper;
import com.gm.server.admin.mapper.ProtocolFileDescMapper;
import com.gm.server.admin.mapper.ProtocolFileStructFieldMapper;
import com.gm.server.admin.mapper.ProtocolFileStructMapper;
import com.gm.server.admin.service.IProtocolService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author wishcher tree
 * @date 2022/12/12 18:06
 */
@Service
public class ProtocolServiceImpl implements IProtocolService {
    @Autowired
    private ProtocolCmdMapper protocolCmdMapper;
    @Autowired
    private ProtocolFileDescMapper protocolFileDescMapper;
    @Autowired
    private ProtocolFileStructFieldMapper protocolFileStructFieldMapper;
    @Autowired
    private ProtocolFileStructMapper protocolFileStructMapper;


    @Override
    public List<ProtocolCmd> selectProtocolCmdList(ProtocolCmd protocolCmd) {
        return protocolCmdMapper.selectProtocolCmdList(protocolCmd);
    }

    @Override
    public ProtocolCmd selectProtocolCmdById(Integer id, String fileVersion) {
        return protocolCmdMapper.selectProtocolCmdById(id, fileVersion);
    }

    @Override
    public List<ProtocolCmd> selectProtocolCmdByMoreId(Integer[] ids, String fileVersion) {
        return protocolCmdMapper.selectProtocolCmdByMoreId(ids, fileVersion);
    }

    @Override
    public void insertProtocolCmd(ProtocolCmd protocolCmd) {
        protocolCmdMapper.insertProtocolCmd(protocolCmd);
    }

    @Override
    public int updateProtocolCmd(ProtocolCmd protocolCmd) {
        return protocolCmdMapper.updateProtocolCmd(protocolCmd);
    }

    @Override
    public int deleteProtocolCmdById(Integer id, String cmdVersion) {
        return protocolCmdMapper.deleteProtocolCmdById(id, cmdVersion);
    }

    @Override
    public int deleteProtocolCmdByVersion(String version) {
        return protocolCmdMapper.deleteProtocolCmdByVersion(version);
    }

    @Override
    public List<ProtocolFileDesc> selectProtocolFileDescList(ProtocolFileDesc protocolFileDesc) {
        return protocolFileDescMapper.selectProtocolFileDescList(protocolFileDesc);
    }

    @Override
    public ProtocolFileDesc selectProtocolFileDescById(Integer id) {
        return protocolFileDescMapper.selectProtocolFileDescById(id);
    }

    @Override
    public void insertProtocolFileDesc(ProtocolFileDesc protocolFileDesc) {
        protocolFileDescMapper.insertProtocolFileDesc(protocolFileDesc);
    }

    @Override
    public int updateProtocolFileDesc(ProtocolFileDesc protocolFileDesc) {
        return protocolFileDescMapper.updateProtocolFileDesc(protocolFileDesc);
    }

    @Override
    public int deleteProtocolFileDescById(Integer id) {
        return protocolFileDescMapper.deleteProtocolFileDescById(id);
    }

    @Override
    public int deleteProtocolFileDescByVersion(String version) {
        return protocolFileDescMapper.deleteProtocolFileDescByVersion(version);
    }

    @Override
    public List<ProtocolFileStructField> selectProtocolFileStructFieldList(ProtocolFileStructField protocolFileStructField) {
        return protocolFileStructFieldMapper.selectProtocolFileStructFieldList(protocolFileStructField);
    }

    @Override
    public ProtocolFileStructField selectProtocolFileStructFieldById(Integer id) {
        return protocolFileStructFieldMapper.selectProtocolFileStructFieldById(id);
    }

    @Override
    public void insertProtocolFileStructField(ProtocolFileStructField protocolFileStructField) {
        protocolFileStructFieldMapper.insertProtocolFileStructField(protocolFileStructField);
    }

    @Override
    public int updateProtocolFileStructField(ProtocolFileStructField protocolFileStructField) {
        return protocolFileStructFieldMapper.updateProtocolFileStructField(protocolFileStructField);
    }

    @Override
    public int deleteProtocolFileStructFieldById(Integer id) {
        return protocolFileStructFieldMapper.deleteProtocolFileStructFieldById(id);
    }

    @Override
    public List<ProtocolFileStruct> selectProtocolFileStructList(ProtocolFileStruct protocolFileStruct) {
        return protocolFileStructMapper.selectProtocolFileStructList(protocolFileStruct);
    }

    @Override
    public ProtocolFileStruct selectProtocolFileStructById(Integer id) {
        return protocolFileStructMapper.selectProtocolFileStructById(id);
    }

    @Override
    public void insertProtocolFileStruct(ProtocolFileStruct protocolFileStruct) {
        protocolFileStructMapper.insertProtocolFileStruct(protocolFileStruct);
    }

    @Override
    public int updateProtocolFileStruct(ProtocolFileStruct protocolFileStruct) {
        return protocolFileStructMapper.updateProtocolFileStruct(protocolFileStruct);
    }

    @Override
    public int deleteProtocolFileStructById(Integer id) {
        return protocolFileStructMapper.deleteProtocolFileStructById(id);
    }

    @Override
    public List<ProtocolFileStructField> selectStructFieldByMoreCondition(List<Integer> fileIds, List<String> structNames) {
        return protocolFileStructFieldMapper.selectStructFieldByMoreCondition(fileIds.toArray(new Integer[0]), structNames.toArray(new String[0]));
    }

    @Override
    public void insertProtocolFileStructFieldBatch(List<ProtocolFileStructField> fields) {
        protocolFileStructFieldMapper.insertProtocolFileStructFieldBatch(fields);
    }

    @Override
    public void updateProtocolFileStructFieldBatch(List<ProtocolFileStructField> fields) {
        protocolFileStructFieldMapper.updateProtocolFileStructFieldBatch(fields);
    }

    @Override
    public int deleteProtocolFileStructFieldByIds(Integer[] pids) {
        return protocolFileStructFieldMapper.deleteProtocolFileStructFieldByIds(pids);
    }

    @Override
    public List<ProtocolFileStructField> selectProtocolFileStructFieldByName(List<String> fieldNames) {
        return protocolFileStructFieldMapper.selectProtocolFileStructFieldByName(fieldNames.toArray(new String[0]));
    }
}
