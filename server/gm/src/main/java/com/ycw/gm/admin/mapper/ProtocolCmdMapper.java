package com.ycw.gm.admin.mapper;

import com.ycw.gm.admin.domain.ProtocolCmd;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author wishcher tree
 * @date 2022/12/12 17:07
 */
public interface ProtocolCmdMapper {
    public List<ProtocolCmd> selectProtocolCmdList(ProtocolCmd protocolCmd);
    public ProtocolCmd selectProtocolCmdById(@Param("id") Integer cmdId, @Param("fileVersion") String fileVersion);
    public void insertProtocolCmd(ProtocolCmd protocolCmd);
    public int updateProtocolCmd(ProtocolCmd protocolCmd);
    public int deleteProtocolCmdById(@Param("id") Integer id, @Param("cmdVersion") String cmdVersion);
    public int deleteProtocolCmdByVersion(String version);

    public List<ProtocolCmd> selectProtocolCmdByMoreId(@Param("ids") Integer[] ids, @Param("fileVersion") String fileVersion);

}
