package com.gm.server.admin.domain;

import com.gm.server.common.core.domain.BaseEntity;

/**
 * @author wishcher tree
 * @date 2022/12/12 16:55
 */
public class ProtocolCmd extends BaseEntity {
    private Integer cmdId;
    private String cmdName;
    private String cmdDesc;

    private String cmdVersion;
    
    public String getCmdName() {
        return cmdName;
    }

    public void setCmdName(String cmdName) {
        this.cmdName = cmdName;
    }

    public String getCmdDesc() {
        return cmdDesc;
    }

    public void setCmdDesc(String cmdDesc) {
        this.cmdDesc = cmdDesc;
    }

    public Integer getCmdId() {
        return cmdId;
    }

    public void setCmdId(Integer cmdId) {
        this.cmdId = cmdId;
    }

    public String getCmdVersion() {
        return cmdVersion;
    }

    public void setCmdVersion(String cmdVersion) {
        this.cmdVersion = cmdVersion;
    }
}
