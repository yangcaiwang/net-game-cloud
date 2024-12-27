package com.ycw.gm.admin.mapper;

import com.ycw.gm.admin.domain.ProtocolFileDesc;

import java.util.List;

/**
 * @author wishcher tree
 * @date 2022/12/12 17:07
 */
public interface ProtocolFileDescMapper {
    public List<ProtocolFileDesc> selectProtocolFileDescList(ProtocolFileDesc protocolFileDesc);
    public ProtocolFileDesc selectProtocolFileDescById(Integer id);
    public void insertProtocolFileDesc(ProtocolFileDesc protocolFileDesc);
    public int updateProtocolFileDesc(ProtocolFileDesc protocolFileDesc);
    public int deleteProtocolFileDescById(Integer id);

    public int deleteProtocolFileDescByVersion(String version);
}
