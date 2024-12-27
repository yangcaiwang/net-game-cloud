package com.ycw.gm.admin.domain;

import com.ycw.gm.common.core.domain.BaseEntity;

/**
 * @author wishcher tree
 * @date 2022/12/12 16:58
 */
public class ProtocolFileDesc extends BaseEntity {
    private Integer id;
    private String fileName;
    private String syntax;
    private String javaPackage;
    private String javaOuterClassname;
    private String imports;
    private String fileDesc;

    private String fileVersion;

    private String cmdPre;

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getSyntax() {
        return syntax;
    }

    public void setSyntax(String syntax) {
        this.syntax = syntax;
    }

    public String getJavaPackage() {
        return javaPackage;
    }

    public void setJavaPackage(String javaPackage) {
        this.javaPackage = javaPackage;
    }

    public String getJavaOuterClassname() {
        return javaOuterClassname;
    }

    public void setJavaOuterClassname(String javaOuterClassname) {
        this.javaOuterClassname = javaOuterClassname;
    }

    public String getImports() {
        return imports;
    }

    public void setImports(String imports) {
        this.imports = imports;
    }

    public String getFileDesc() {
        return fileDesc;
    }

    public void setFileDesc(String fileDesc) {
        this.fileDesc = fileDesc;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getFileVersion() {
        return fileVersion;
    }

    public void setFileVersion(String fileVersion) {
        this.fileVersion = fileVersion;
    }

    public String getCmdPre() {
        return cmdPre;
    }

    public void setCmdPre(String cmdPre) {
        this.cmdPre = cmdPre;
    }
}
