package com.ycw.gm.admin.domain;

import com.ycw.gm.common.core.domain.BaseEntity;

/**
 * @author wishcher tree
 * @date 2022/12/12 17:03
 */
public class ProtocolFileStructField extends BaseEntity {
    private Integer id;
    private String fieldDesc;
    private String fieldTypeDesc; // 类型描述 optional repeated
    private String fieldType; // 类型 int string bool ...
    private String fieldName; // 字段名
    private String structName; // 对应的cmd

    private String fieldValue; // 定义属性顺序值
    private Integer fileIndex; // 文件索引

    public boolean checkEq(ProtocolFileStructField fileStructField) {
        if (fileStructField.getFieldDesc() != null && fieldDesc != null) {
            if (!fileStructField.getFieldDesc().equals(fieldDesc)) {
                return false;
            }
        }
        if (fileStructField.getFieldTypeDesc() != null && fieldTypeDesc != null) {
            if (!fileStructField.getFieldTypeDesc().equals(fieldTypeDesc)) {
                return false;
            }
        }
        if (fileStructField.getFieldType() != null && fieldType != null) {
            if (!fileStructField.getFieldType().equals(fieldType)) {
                return false;
            }
        }
        if (fileStructField.getFieldName() != null && fieldName != null) {
            if (!fileStructField.getFieldName().equals(fieldName)) {
                return false;
            }
        }
        if (fileStructField.getStructName() != null && structName != null) {
            if (!fileStructField.getStructName().equals(structName)) {
                return false;
            }
        }
        if (fileStructField.getFieldValue() != null && fieldValue != null) {
            if (!fileStructField.getFieldValue().equals(fieldValue)) {
                return false;
            }
        }
        if (fileStructField.getFileIndex() != null && fileIndex != null) {
            if (!fileStructField.getFileIndex().equals(fileIndex)) {
                return false;
            }
        }
        return true;
    }
    public String getFieldDesc() {
        return fieldDesc;
    }

    public void setFieldDesc(String fieldDesc) {
        this.fieldDesc = fieldDesc;
    }

    public String getFieldTypeDesc() {
        return fieldTypeDesc;
    }

    public void setFieldTypeDesc(String fieldTypeDesc) {
        this.fieldTypeDesc = fieldTypeDesc;
    }

    public String getFieldType() {
        return fieldType;
    }

    public void setFieldType(String fieldType) {
        this.fieldType = fieldType;
    }

    public String getFieldName() {
        return fieldName;
    }

    public void setFieldName(String fieldName) {
        this.fieldName = fieldName;
    }

    public String getStructName() {
        return structName;
    }

    public void setStructName(String structName) {
        this.structName = structName;
    }

    public String getFieldValue() {
        return fieldValue;
    }

    public void setFieldValue(String fieldValue) {
        this.fieldValue = fieldValue;
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
}
