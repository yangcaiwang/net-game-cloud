package com.gm.server.admin.mapper;

import com.gm.server.admin.domain.ProtocolFileStruct;

import java.util.List;

/**
 * @author wishcher tree
 * @date 2022/12/12 17:07
 */
public interface ProtocolFileStructMapper {
    public List<ProtocolFileStruct> selectProtocolFileStructList(ProtocolFileStruct protocolFileStruct);
    public ProtocolFileStruct selectProtocolFileStructById(Integer id);
    public void insertProtocolFileStruct(ProtocolFileStruct protocolFileStruct);
    public int updateProtocolFileStruct(ProtocolFileStruct protocolFileStruct);
    public int deleteProtocolFileStructById(Integer id);
}
