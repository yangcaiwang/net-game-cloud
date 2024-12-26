package com.gm.server.admin.web.controller.protoApi;

import java.io.Serializable;

public class ProtoVersionData implements Serializable {
    private String version;
    private String protoPath;
    private String commitPath;

    private String versionName;

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getProtoPath() {
        return protoPath;
    }

    public void setProtoPath(String protoPath) {
        this.protoPath = protoPath;
    }

    public String getCommitPath() {
        return commitPath;
    }

    public void setCommitPath(String commitPath) {
        this.commitPath = commitPath;
    }

    public String getVersionName() {
        return versionName;
    }

    public void setVersionName(String versionName) {
        this.versionName = versionName;
    }
}
