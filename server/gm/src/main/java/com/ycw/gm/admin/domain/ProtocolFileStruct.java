package com.ycw.gm.admin.domain;

import com.ycw.gm.common.core.domain.BaseEntity;

import java.util.List;

/**
 * @author wishcher tree
 * @date 2022/12/12 17:00
 */
public class ProtocolFileStruct extends BaseEntity {
    private Integer id;
    private String structDesc; // 描述
    private String protoType; // 类型 message enum ...
    private String structName; // 协议名
    private Integer cmd; // 对应的cmd

    private Integer fileIndex; // 文件索引

    private ProtocolCmd protocolCmd; // 结构对应的命令描述
    private ProtocolFileStruct resp; // 返回的数据
    private List<ProtocolFileStructField> structFieldList;

    private String fileVersion;

    public String getStructDesc() {
        return structDesc;
    }

    public void setStructDesc(String structDesc) {
        this.structDesc = structDesc;
    }

    public String getProtoType() {
        return protoType;
    }

    public void setProtoType(String protoType) {
        this.protoType = protoType;
    }

    public String getStructName() {
        return structName;
    }

    public void setStructName(String structName) {
        this.structName = structName;
    }

    public Integer getFileIndex() {
        return fileIndex;
    }

    public void setFileIndex(Integer fileIndex) {
        this.fileIndex = fileIndex;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getCmd() {
        return cmd;
    }

    public void setCmd(Integer cmd) {
        this.cmd = cmd;
    }

    public ProtocolFileStruct getResp() {
        return resp;
    }

    public void setResp(ProtocolFileStruct resp) {
        this.resp = resp;
    }

    public List<ProtocolFileStructField> getStructFieldList() {
        return structFieldList;
    }

    public void setStructFieldList(List<ProtocolFileStructField> structFieldList) {
        this.structFieldList = structFieldList;
    }

    public ProtocolCmd getProtocolCmd() {
        return protocolCmd;
    }

    public void setProtocolCmd(ProtocolCmd protocolCmd) {
        this.protocolCmd = protocolCmd;
    }

    public String getFileVersion() {
        return fileVersion;
    }

    public void setFileVersion(String fileVersion) {
        this.fileVersion = fileVersion;
    }
}
