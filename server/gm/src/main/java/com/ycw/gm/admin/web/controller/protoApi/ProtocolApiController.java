package com.ycw.gm.admin.web.controller.protoApi;

import cn.hutool.core.collection.ConcurrentHashSet;
import com.ycw.core.cluster.property.PropertyConfig;
import com.ycw.gm.admin.domain.ProtocolCmd;
import com.ycw.gm.admin.domain.ProtocolFileDesc;
import com.ycw.gm.admin.domain.ProtocolFileStruct;
import com.ycw.gm.admin.domain.ProtocolFileStructField;
import com.ycw.gm.admin.service.IProtocolService;
import com.ycw.gm.common.core.controller.BaseController;
import com.ycw.gm.common.core.domain.AjaxResult;
import com.ycw.gm.common.core.page.TableDataInfo;
import com.ycw.gm.common.utils.ExecuteShellUtil;
import com.ycw.gm.common.utils.StringUtils;
import org.apache.logging.log4j.util.Strings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.async.WebAsyncTask;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.concurrent.Callable;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;

/**
 * @author wishcher tree
 * @date 2022/12/12 18:11
 */
@RestController
@RequestMapping("/devTools/proto")
public class ProtocolApiController extends BaseController {

    @Autowired
    private IProtocolService protocolService;

    private AtomicBoolean saveProtoFile = new AtomicBoolean(false);
    private AtomicBoolean commitFile = new AtomicBoolean(false);

    private Set<Integer> updateDbProto = new ConcurrentHashSet();

    @GetMapping("/listProtoCmd")
    public TableDataInfo listProtoCmd(ProtocolCmd protocolCmd) {
        if (!PropertyConfig.isDebug()) {
            return getDataTableError();
        }
        return getDataTable(protocolService.selectProtocolCmdList(protocolCmd));
    }

    @GetMapping("/listProtoVersionInfo")
    public TableDataInfo listProtoVersionInfo() {
        if (!PropertyConfig.isDebug()) {
            return getDataTableError();
        }
        List<ProtoVersionData> list = new ArrayList<>();
        for (ProtoVersionData value : protoVersionDataMap.values()) {
            ProtoVersionData versionData = new ProtoVersionData();
            versionData.setVersionName(value.getVersionName());
            versionData.setVersion(value.getVersion());
            list.add(versionData);
        }

        return getDataTable(list);
    }

    @GetMapping("/listProtoFile")
    public TableDataInfo listProtoFile(String fileVersion) {
        if (!PropertyConfig.isDebug()) {
            return getDataTableError();
        }
        ProtocolFileDesc protocolFileDesc = new ProtocolFileDesc();
        protocolFileDesc.setFileVersion(fileVersion);
        return getDataTable(protocolService.selectProtocolFileDescList(protocolFileDesc));
    }

    @GetMapping("/listProtoStruct")
    public TableDataInfo listProtoStruct(ProtocolFileDesc protocolFileDesc) {
        if (!PropertyConfig.isDebug()) {
            return getDataTableError();
        }
        Set<String> list = new HashSet<>();
//        list.add("int8");
//        list.add("int16");
        list.add("int32");
        list.add("int64");
        list.add("bool");
        list.add("string");
        ProtocolFileDesc protocolFileDesc1 = protocolService.selectProtocolFileDescById(protocolFileDesc.getId());
        if (protocolFileDesc1 != null) {
            ProtocolFileStruct struct = new ProtocolFileStruct();
            struct.setFileIndex(protocolFileDesc.getId());
            List<ProtocolFileStruct> protocolFileStructs = protocolService.selectProtocolFileStructList(struct);
            protocolFileStructs.stream().filter(v -> v.getCmd() <= 0).forEach(v -> list.add(v.getStructName()));

            String imports = protocolFileDesc1.getImports();
            if (StringUtils.isNotEmpty(imports)) {
                String[] split = imports.split(";");
                for (String s : split) {
                    String[] split1 = s.split("/");
                    String s1 = split1[split1.length - 1];

                    ProtocolFileDesc protocolFileDesc2 = new ProtocolFileDesc();
                    protocolFileDesc2.setFileName(s1);
                    protocolFileDesc2.setFileVersion(protocolFileDesc1.getFileVersion());
                    List<ProtocolFileDesc> protocolFileDescs = protocolService.selectProtocolFileDescList(protocolFileDesc2);
                    for (ProtocolFileDesc fileDesc : protocolFileDescs) {
                        struct = new ProtocolFileStruct();
                        struct.setFileIndex(fileDesc.getId());
                        protocolFileStructs = protocolService.selectProtocolFileStructList(struct);
                        protocolFileStructs.stream().filter(v -> v.getCmd() <= 0).forEach(v -> list.add(v.getStructName()));
                    }
                }
            }
        }
        return getDataTable(new ArrayList<>(list));
    }

    @PostMapping("/saveProtoFileDesc")
    public AjaxResult saveProtoFileDesc(@Validated @RequestBody ProtocolFileDesc protocolFileDesc) {
        if (!PropertyConfig.isDebug()) {
            return AjaxResult.error("仅开发模式下可用");
        }
        List<ProtocolFileDesc> protocolFileDescs = protocolService.selectProtocolFileDescList(protocolFileDesc);
        if (!protocolFileDescs.isEmpty()) {
            return AjaxResult.error("found exist proto file");
        }
        String version = Strings.isEmpty(protocolFileDesc.getFileVersion()) ? "main" : protocolFileDesc.getFileVersion();

        protocolFileDesc.setSyntax("proto2");
        String substring = protocolFileDesc.getFileName().substring(0, protocolFileDesc.getFileName().length() - 6);
        protocolFileDesc.setJavaPackage("com.pp.game.slg2.msg." + substring);
        protocolFileDesc.setFileVersion(version);
        String s = substring.substring(0, 1).toUpperCase() + substring.substring(1) + "Proto";
        protocolFileDesc.setJavaOuterClassname(s);
        protocolService.insertProtocolFileDesc(protocolFileDesc);

        ProtocolFileDesc protocolFileDesc1 = new ProtocolFileDesc();
        protocolFileDesc1.setFileName(protocolFileDesc.getFileName());
        protocolFileDesc1.setFileVersion(version);
        List<ProtocolFileDesc> protocolFileDescs1 = protocolService.selectProtocolFileDescList(protocolFileDesc1);
        if (!protocolFileDescs1.isEmpty()) {
            ProtocolFileDesc protocolFileDesc2 = protocolFileDescs1.get(0);
            return AjaxResult.success(protocolFileDesc2);
        }
        return AjaxResult.success();
    }

    @PutMapping("/updateProtoFileDesc")
    public AjaxResult updateProtoFileDesc(@Validated @RequestBody ProtocolFileDesc protocolFileDesc) {
        if (!PropertyConfig.isDebug()) {
            return AjaxResult.error("仅开发模式下可用");
        }
        List<ProtocolFileDesc> protocolFileDescs = protocolService.selectProtocolFileDescList(protocolFileDesc);
        if (protocolFileDescs.isEmpty()) {
            return AjaxResult.error("not found exist proto file");
        }
        protocolService.updateProtocolFileDesc(protocolFileDesc);
        return AjaxResult.success();
    }

    private AjaxResult checkProtocolFileStruct(ProtocolFileStruct protocolFileStruct) {
        Integer fileIndex = protocolFileStruct.getFileIndex();
        if (fileIndex == null) {
            return AjaxResult.error(" not found file Index");
        }
        if (Strings.isEmpty(protocolFileStruct.getProtoType())) {
            return AjaxResult.error(" ProtoType empty");
        }

        if (protocolFileStruct.getStructFieldList() != null && protocolFileStruct.getStructFieldList().size() > 0) {
            boolean isEnum = protocolFileStruct.getCmd() == 0 && "enum".equals(protocolFileStruct.getProtoType());
            if (isEnum) {
                List<ProtocolFileStructField> protocolFileStructFields = protocolService.selectProtocolFileStructFieldByName(protocolFileStruct.getStructFieldList().stream().map(v -> v.getFieldName()).collect(Collectors.toList()));
                protocolFileStructFields = protocolFileStructFields.stream().filter(v -> v.getFieldType() == null || Objects.equals(v.getFieldType(), "null")).collect(Collectors.toList());
                if (!protocolFileStructFields.isEmpty()) {
                    List<ProtocolFileStructField> collect1 = protocolFileStructFields.stream().filter(v -> protocolFileStruct.getStructFieldList().stream().anyMatch(vv -> vv.getFieldName().equals(v.getFieldName()) && !Objects.equals(vv.getStructName(), v.getStructName()))).collect(Collectors.toList());
                    if (!collect1.isEmpty()) {
                        ProtocolFileStructField protocolFileStructField = collect1.get(0);
                        return AjaxResult.error(String.format("other field name :%s.%s exist", protocolFileStructField.getStructName(), protocolFileStructField.getFieldName()));
                    }
                }
            }
            List<Integer> fieldValueList = new ArrayList<>();
            List<String> fieldNameList = new ArrayList<>();
            for (ProtocolFileStructField protocolFileStructField : protocolFileStruct.getStructFieldList()) {
                if (Strings.isEmpty(protocolFileStructField.getFieldName())) {
                    continue;
                }
                if (!StringUtils.isNumeric(protocolFileStructField.getFieldValue())) {
                    return AjaxResult.error(String.format("序号[%s]必须是数字", protocolFileStructField.getFieldValue()));
                }
                if (fieldNameList.contains(protocolFileStructField.getFieldName())) {
                    return AjaxResult.error(String.format("消息字段名重复了 重复序号%s", protocolFileStructField.getFieldName()));
                }
                int iFieldValue = Integer.parseInt(protocolFileStructField.getFieldValue());
                if (fieldValueList.contains(iFieldValue)) {
                    return AjaxResult.error(String.format("消息序号重复了 重复序号%d", iFieldValue));
                }
                fieldValueList.add(iFieldValue);
                fieldNameList.add(protocolFileStructField.getFieldName());
            }
        }

        if (protocolFileStruct.getCmd() > 0) {
            boolean needResp = false;
            int startValue = Integer.parseInt(String.valueOf(protocolFileStruct.getCmd()).substring(0, 1));
            if (startValue == 1) {
                if (!protocolFileStruct.getStructName().endsWith("Req")) {
                    return AjaxResult.error("请求消息必须以Req结尾");
                }
                if (protocolFileStruct.getResp() == null || protocolFileStruct.getResp().getCmd() == null) {
                    return AjaxResult.error("请求消息错误，需要对应的返回消息ID");
                }
                String respCmd = String.valueOf(protocolFileStruct.getResp().getCmd());
                if (!respCmd.startsWith("2")) {
                    return AjaxResult.error("响应消息号错误,需以2开头");
                }
                String strCmdReq = String.valueOf(protocolFileStruct.getCmd()).substring(1);
                String strCmdResp = respCmd.substring(1);
                if (!strCmdReq.equals(strCmdResp)) {
                    return AjaxResult.error("请求消息号或响应消息号错误");
                }
                needResp = true;
            } else if (startValue == 3) {
                if (!protocolFileStruct.getStructName().endsWith("Notify")) {
                    return AjaxResult.error("推送消息必须以Notify结尾");
                }
            }
            if (needResp && protocolFileStruct.getResp() != null && protocolFileStruct.getResp().getCmd() != null) {

                if (!protocolFileStruct.getResp().getStructName().endsWith("Resp")) {
                    return AjaxResult.error("应答消息必须以Resp结尾");
                }
                List<Integer> fieldValueList = new ArrayList<>();
                List<String> fieldNameList = new ArrayList<>();
                for (ProtocolFileStructField protocolFileStructField : protocolFileStruct.getResp().getStructFieldList()) {
                    if (Strings.isEmpty(protocolFileStructField.getFieldName())) {
                        continue;
                    }

                    if (!StringUtils.isNumeric(protocolFileStructField.getFieldValue())) {
                        return AjaxResult.error(String.format("序号[%s]必须是数字", protocolFileStructField.getFieldValue()));
                    }
                    if (fieldNameList.contains(protocolFileStructField.getFieldName())) {
                        return AjaxResult.error(String.format("返回消息字段名重复了 重复序号%s", protocolFileStructField.getFieldName()));
                    }
                    int iFieldValue = Integer.parseInt(protocolFileStructField.getFieldValue());
                    if (fieldValueList.contains(iFieldValue)) {
                        return AjaxResult.error(String.format("返回消息序号重复了 重复序号%d", iFieldValue));
                    }
                    fieldValueList.add(iFieldValue);
                    fieldNameList.add(protocolFileStructField.getFieldName());
                }
            }
        }
        return null;
    }

    @PostMapping("/saveProtoFileStruct")
    public AjaxResult saveProtoFileStruct(@Validated @RequestBody ProtocolFileStruct protocolFileStruct) {
        if (!PropertyConfig.isDebug()) {
            return AjaxResult.error("仅开发模式下可用");
        }
        Integer fileIndex = protocolFileStruct.getFileIndex();
        if (fileIndex == null) {
            return AjaxResult.error(" not found file Index");
        }
        if (protocolFileStruct.getId() != null) {
            return AjaxResult.error(" found ID exist");
        }

        ProtocolFileStruct struct = new ProtocolFileStruct();
        struct.setFileIndex(protocolFileStruct.getFileIndex());
        struct.setStructName(protocolFileStruct.getStructName());
        struct.setCmd(protocolFileStruct.getCmd());
        List<ProtocolFileStruct> protocolFileDescs = protocolService.selectProtocolFileStructList(struct);
        if (!protocolFileDescs.isEmpty()) {
            return AjaxResult.error("found exist proto file");
        }
        String version = Strings.isEmpty(protocolFileStruct.getFileVersion()) ? "main" : protocolFileStruct.getFileVersion();
        if (protocolFileStruct.getCmd() > 0) {
            ProtocolCmd protocolCmd = protocolService.selectProtocolCmdById(protocolFileStruct.getCmd(), version);
            if (protocolCmd != null) {
                return AjaxResult.error("定义的消息ID已经存在，不能重复定义");
            }
            int pre = Integer.parseInt(String.valueOf(protocolFileStruct.getCmd()).substring(0, 1));
            if (pre == 1) {
                if (protocolFileStruct.getResp() != null && protocolFileStruct.getResp().getCmd() != null) {
                    ProtocolFileStruct resp = protocolFileStruct.getResp();
                    ProtocolFileStruct structResp = new ProtocolFileStruct();
                    structResp.setFileIndex(resp.getFileIndex());
                    structResp.setStructName(resp.getStructName());
                    structResp.setCmd(resp.getCmd());
                    List<ProtocolFileStruct> respFile = protocolService.selectProtocolFileStructList(structResp);
                    if (!respFile.isEmpty()) {
                        return AjaxResult.error(String.format("found exist struct cmd:%d", structResp.getCmd()));
                    }
                }
            }
        }

        AjaxResult ajaxResult = checkProtocolFileStruct(protocolFileStruct);
        if (ajaxResult != null) {
            return ajaxResult;
        }
        if (updateDbProto.contains(fileIndex)) {
            return AjaxResult.error("正在保存，请勿重复提交");
        }

        updateDbProto.add(fileIndex);
        protocolService.insertProtocolFileStruct(protocolFileStruct);
        if (protocolFileStruct.getCmd() > 0) {
            ProtocolCmd protocolCmd = new ProtocolCmd();
            protocolCmd.setCmdId(protocolFileStruct.getCmd());
            String structName = protocolFileStruct.getStructName();
            String s = structName.substring(0, 1).toUpperCase() + structName.substring(1).replaceAll("([A-Z])", "_$1");
            String s1 = s.toUpperCase();
            protocolCmd.setCmdName(s1);

            protocolCmd.setCmdVersion(version);
            protocolCmd.setCmdDesc(protocolFileStruct.getStructDesc());
            protocolService.insertProtocolCmd(protocolCmd);
        }
        if (protocolFileStruct.getStructFieldList() != null) {
            List<ProtocolFileStructField> inserts = new ArrayList<>();
            for (ProtocolFileStructField protocolFileStructField : protocolFileStruct.getStructFieldList()) {
                if (StringUtils.isEmpty(protocolFileStructField.getFieldName())) {
                    continue;
                }
                protocolFileStructField.setFileIndex(fileIndex);
                protocolFileStructField.setStructName(protocolFileStruct.getStructName());
                inserts.add(protocolFileStructField);
            }
            if (!inserts.isEmpty()) {
                protocolService.insertProtocolFileStructFieldBatch(inserts);
            }
        }
        if (protocolFileStruct.getCmd() > 0 && protocolFileStruct.getResp() != null && protocolFileStruct.getResp().getCmd() != null) {
            protocolFileStruct.getResp().setProtoType(protocolFileStruct.getProtoType());
            protocolFileStruct.getResp().setFileIndex(fileIndex);
            protocolService.insertProtocolFileStruct(protocolFileStruct.getResp());

            ProtocolCmd protocolCmd = new ProtocolCmd();
            protocolCmd.setCmdId(protocolFileStruct.getResp().getCmd());
            String structName = protocolFileStruct.getResp().getStructName();
            String s = structName.substring(0, 1).toUpperCase() + structName.substring(1).replaceAll("([A-Z])", "_$1");
            String s1 = s.toUpperCase();
            protocolCmd.setCmdName(s1);

            protocolCmd.setCmdVersion(version);
            protocolCmd.setCmdDesc(protocolFileStruct.getResp().getStructDesc());
            protocolService.insertProtocolCmd(protocolCmd);

            if (protocolFileStruct.getResp().getStructFieldList() != null) {
                List<ProtocolFileStructField> inserts = new ArrayList<>();
                for (ProtocolFileStructField protocolFileStructField : protocolFileStruct.getResp().getStructFieldList()) {
                    if (StringUtils.isEmpty(protocolFileStructField.getFieldName())) {
                        continue;
                    }
                    protocolFileStructField.setFileIndex(fileIndex);
                    protocolFileStructField.setStructName(structName);
                    inserts.add(protocolFileStructField);
                }
                if (!inserts.isEmpty()) {
                    protocolService.insertProtocolFileStructFieldBatch(inserts);
                }
            }
        }
        updateDbProto.remove(fileIndex);

        return AjaxResult.success();
    }

    @PutMapping("/updateProtoFileStruct")
    public AjaxResult updateProtoFileStruct(@Validated @RequestBody ProtocolFileStruct protocolFileStruct) {
        Integer fileIndex = protocolFileStruct.getFileIndex();
        if (fileIndex == null) {
            return AjaxResult.error(" not found file Index");
        }
        if (protocolFileStruct.getId() == null) {
            return AjaxResult.error(" not found protocolFileStruct ID");
        }

        ProtocolFileStruct struct = new ProtocolFileStruct();
        struct.setId(protocolFileStruct.getId());
        List<ProtocolFileStruct> protocolFileDescs = protocolService.selectProtocolFileStructList(struct);
        if (protocolFileDescs.isEmpty()) {
            return AjaxResult.error("not found exist proto file");
        }
        String version = Strings.isEmpty(protocolFileStruct.getFileVersion()) ? "main" : protocolFileStruct.getFileVersion();

        if (protocolFileStruct.getCmd() > 0) {
            ProtocolCmd protocolCmd = protocolService.selectProtocolCmdById(protocolFileStruct.getCmd(), version);
            if (protocolCmd == null) {
                return AjaxResult.error("消息不存在");
            }
            int pre = Integer.parseInt(String.valueOf(protocolFileStruct.getCmd()).substring(0, 1));
            if (pre == 1) {
                if (protocolFileStruct.getResp() != null && protocolFileStruct.getResp().getCmd() != null) {
                    ProtocolFileStruct resp = protocolFileStruct.getResp();
                    ProtocolFileStruct structResp = new ProtocolFileStruct();
                    structResp.setFileIndex(resp.getFileIndex());
                    structResp.setStructName(resp.getStructName());
                    structResp.setCmd(resp.getCmd());
                    List<ProtocolFileStruct> respFile = protocolService.selectProtocolFileStructList(structResp);
                    if (respFile.isEmpty()) {
                        return AjaxResult.error("found not exist proto file");
                    }
                }
            }
        }

        AjaxResult ajaxResult = checkProtocolFileStruct(protocolFileStruct);
        if (ajaxResult != null) {
            return ajaxResult;
        }

        if (updateDbProto.contains(fileIndex)) {
            return AjaxResult.error("正在保存，请勿重复提交");
        }
        updateDbProto.add(fileIndex);
        if (protocolFileStruct.getId() != null) {
            protocolService.updateProtocolFileStruct(protocolFileStruct);
        } else {
            protocolService.insertProtocolFileStruct(protocolFileStruct);
        }

        if (protocolFileStruct.getCmd() > 0) {
            ProtocolCmd protocolCmd = new ProtocolCmd();
            protocolCmd.setCmdId(protocolFileStruct.getCmd());
            protocolCmd.setCmdVersion(version);
            List<ProtocolCmd> protocolCmds = protocolService.selectProtocolCmdList(protocolCmd);
            protocolCmd.setCmdDesc(protocolFileStruct.getStructDesc());
            String structName = protocolFileStruct.getStructName();
            String s = structName.substring(0, 1).toUpperCase() + structName.substring(1).replaceAll("([A-Z])", "_$1");
            String s1 = s.toUpperCase();
            protocolCmd.setCmdName(s1);
            if (protocolCmds.isEmpty()) {
                protocolService.insertProtocolCmd(protocolCmd);
            } else {
                protocolService.updateProtocolCmd(protocolCmd);
            }
        }
        ProtocolFileStructField selectFileStruct = new ProtocolFileStructField();
        selectFileStruct.setStructName(protocolFileStruct.getStructName());
        selectFileStruct.setFileIndex(fileIndex);
        List<ProtocolFileStructField> protocolFileStructFields = protocolService.selectProtocolFileStructFieldList(selectFileStruct);
        if (protocolFileStruct.getStructFieldList() != null) {
            List<ProtocolFileStructField> updateList = new ArrayList<>();
            List<ProtocolFileStructField> insertList = new ArrayList<>();
            for (ProtocolFileStructField protocolFileStructField : protocolFileStruct.getStructFieldList()) {
                if (StringUtils.isEmpty(protocolFileStructField.getFieldName())) {
                    continue;
                }
                protocolFileStructField.setFileIndex(fileIndex);
                protocolFileStructField.setStructName(protocolFileStruct.getStructName());
                if (protocolFileStructField.getId() != null) {
                    ProtocolFileStructField protocolFileStructField1 = protocolFileStructFields.stream().filter(v -> Objects.equals(v.getId(), protocolFileStructField.getId())).findFirst().orElse(null);
                    protocolFileStructFields.removeIf(v -> Objects.equals(v.getId(), protocolFileStructField.getId()));
                    if (protocolFileStructField1 != null && protocolFileStructField1.checkEq(protocolFileStructField)) {
                    } else {
                        updateList.add(protocolFileStructField);
                    }
                } else {
                    insertList.add(protocolFileStructField);
                }
            }
            if (!updateList.isEmpty()) {
                protocolService.updateProtocolFileStructFieldBatch(updateList);
            }
            if (!insertList.isEmpty()) {
                protocolService.insertProtocolFileStructFieldBatch(insertList);
            }
        }
        if (!protocolFileStructFields.isEmpty()) {
            Integer[] objects = protocolFileStructFields.stream().map(ProtocolFileStructField::getId).toArray(Integer[]::new);
            protocolService.deleteProtocolFileStructFieldByIds(objects);
        }
        if (protocolFileStruct.getCmd() > 0 && protocolFileStruct.getResp() != null &&  protocolFileStruct.getResp().getCmd() != null) {
            protocolFileStruct.getResp().setProtoType(protocolFileStruct.getProtoType());
            protocolFileStruct.getResp().setFileIndex(fileIndex);
            if (protocolFileStruct.getResp().getId() != null) {
                protocolService.updateProtocolFileStruct(protocolFileStruct.getResp());
            } else {
                protocolService.insertProtocolFileStruct(protocolFileStruct.getResp());
            }

            ProtocolCmd protocolCmd = new ProtocolCmd();
            protocolCmd.setCmdId(protocolFileStruct.getResp().getCmd());
            protocolCmd.setCmdVersion(version);
            List<ProtocolCmd> protocolCmds = protocolService.selectProtocolCmdList(protocolCmd);
            protocolCmd.setCmdDesc(protocolFileStruct.getResp().getStructDesc());
            String structName = protocolFileStruct.getResp().getStructName();
            String s = structName.substring(0, 1).toUpperCase() + structName.substring(1).replaceAll("([A-Z])", "_$1");
            String s1 = s.toUpperCase();
            protocolCmd.setCmdName(s1);
            if (protocolCmds.isEmpty()) {
                protocolService.insertProtocolCmd(protocolCmd);
            } else {
                protocolService.updateProtocolCmd(protocolCmd);
            }
            selectFileStruct = new ProtocolFileStructField();
            selectFileStruct.setStructName(protocolFileStruct.getResp().getStructName());
            selectFileStruct.setFileIndex(fileIndex);
            protocolFileStructFields = protocolService.selectProtocolFileStructFieldList(selectFileStruct);
            if (protocolFileStruct.getResp().getStructFieldList() != null) {
                List<ProtocolFileStructField> updateList = new ArrayList<>();
                List<ProtocolFileStructField> insertList = new ArrayList<>();
                for (ProtocolFileStructField protocolFileStructField : protocolFileStruct.getResp().getStructFieldList()) {
                    if (StringUtils.isEmpty(protocolFileStructField.getFieldName())) {
                        continue;
                    }
                    protocolFileStructField.setFileIndex(fileIndex);
                    protocolFileStructField.setStructName(protocolFileStruct.getResp().getStructName());
                    if (protocolFileStructField.getId() != null) {
                        ProtocolFileStructField protocolFileStructField1 = protocolFileStructFields.stream().filter(v -> Objects.equals(v.getId(), protocolFileStructField.getId())).findFirst().orElse(null);
                        protocolFileStructFields.removeIf(v -> Objects.equals(v.getId(), protocolFileStructField.getId()));
                        if (protocolFileStructField1 != null && protocolFileStructField1.checkEq(protocolFileStructField)) {
                        } else {
                            updateList.add(protocolFileStructField);
                        }
                    } else {
                        insertList.add(protocolFileStructField);
//                        protocolService.insertProtocolFileStructField(protocolFileStructField);
                    }
                }
                if (!updateList.isEmpty()) {
                    protocolService.updateProtocolFileStructFieldBatch(updateList);
                }
                if (!insertList.isEmpty()) {
                    protocolService.insertProtocolFileStructFieldBatch(insertList);
                }
            }
            if (!protocolFileStructFields.isEmpty()) {
                Integer[] objects = protocolFileStructFields.stream().map(ProtocolFileStructField::getId).toArray(Integer[]::new);
                protocolService.deleteProtocolFileStructFieldByIds(objects);
            }
        }
        updateDbProto.remove(fileIndex);

        return AjaxResult.success();
    }

    @GetMapping("/listProtocolFileStruct")
    public TableDataInfo listProtocolFileStruct(ProtocolFileStruct protocolFileStruct) {
        String version = Strings.isEmpty(protocolFileStruct.getFileVersion()) ? "main" : protocolFileStruct.getFileVersion();
        List<ProtocolFileStruct> protocolFileStruct1 = getProtocolFileStruct(protocolFileStruct);

        ProtocolFileStructField protocolFileStructField = new ProtocolFileStructField();
        protocolFileStructField.setFileIndex(protocolFileStruct.getFileIndex());
        List<ProtocolFileStructField> structFieldList = protocolService.selectProtocolFileStructFieldList(protocolFileStructField);

        List<Integer> cmdList = new ArrayList<>();
        for (ProtocolFileStruct fileStruct : protocolFileStruct1) {
            if (fileStruct.getCmd() > 0) {
                cmdList.add(fileStruct.getCmd());
            }
            if (fileStruct.getResp() != null && fileStruct.getResp().getCmd() > 0) {
                cmdList.add(fileStruct.getResp().getCmd());
            }
        }
        Map<Integer, ProtocolCmd> cmdMap = new HashMap<>();
        if (!cmdList.isEmpty()) {
            List<ProtocolCmd> protocolCmds = protocolService.selectProtocolCmdByMoreId(cmdList.toArray(new Integer[0]), version);
            cmdMap = protocolCmds.stream().collect(Collectors.toMap(k -> k.getCmdId(), v -> v));
        }

        for (ProtocolFileStruct fileStruct : protocolFileStruct1) {
            List<ProtocolFileStructField> protocolFileStructFields = structFieldList.stream().filter(v -> v.getStructName().equals(fileStruct.getStructName())).collect(Collectors.toList());
            List<ProtocolFileStructField> collect = protocolFileStructFields.stream().sorted(Comparator.comparingInt(a -> Integer.parseInt(a.getFieldValue()))).collect(Collectors.toList());
            fileStruct.setStructFieldList(collect);
            if (fileStruct.getCmd() > 0) {
                ProtocolCmd protocolCmd1 = cmdMap.get(fileStruct.getCmd());
                if (protocolCmd1 != null) {
                    fileStruct.setProtocolCmd(protocolCmd1);
                }
            }

            if (fileStruct.getResp() != null) {
                protocolFileStructFields = structFieldList.stream().filter(v -> v.getStructName().equals(fileStruct.getResp().getStructName())).collect(Collectors.toList());
                collect = protocolFileStructFields.stream().sorted(Comparator.comparingInt(a -> Integer.parseInt(a.getFieldValue()))).collect(Collectors.toList());
                fileStruct.getResp().setStructFieldList(collect);

                if (fileStruct.getResp().getCmd() > 0) {
                    ProtocolCmd protocolCmd1 = cmdMap.get(fileStruct.getResp().getCmd());
                    if (protocolCmd1 != null) {
                        fileStruct.getResp().setProtocolCmd(protocolCmd1);
                    }
                }
            }
        }

        return getDataTable(protocolFileStruct1);
    }

    @GetMapping("/getProtoFileAllStruct")
    public TableDataInfo getProtoFileAllStruct(ProtocolFileStruct protocolFileStruct) {

        ProtocolFileStruct protocolFileStruct2 = new ProtocolFileStruct();
        protocolFileStruct2.setFileIndex(protocolFileStruct.getFileIndex());
        List<ProtocolFileStruct> protocolFileStruct1 = getProtocolFileStruct(protocolFileStruct2);
        ProtocolFileStruct protocolFileStruct3 = protocolFileStruct1.stream().filter(v -> v.getStructName().equals(protocolFileStruct.getStructName())).findFirst().orElse(null);
        if (protocolFileStruct3 == null) {
            return getDataTableError();
        }

        ProtocolFileStructField protocolFileStructField = new ProtocolFileStructField();
        protocolFileStructField.setFileIndex(protocolFileStruct.getFileIndex());
        protocolFileStructField.setStructName(protocolFileStruct3.getStructName());
        List<ProtocolFileStructField> structFieldList = protocolService.selectProtocolFileStructFieldList(protocolFileStructField);
        List<ProtocolFileStructField> list = new ArrayList<>(structFieldList);

        if (protocolFileStruct3.getResp() != null) {
            protocolFileStructField = new ProtocolFileStructField();
            protocolFileStructField.setFileIndex(protocolFileStruct.getFileIndex());
            protocolFileStructField.setStructName(protocolFileStruct3.getResp().getStructName());
            structFieldList = protocolService.selectProtocolFileStructFieldList(protocolFileStructField);
            list.addAll(structFieldList);
        }

//        ProtocolFileStructField protocolFileStructField = new ProtocolFileStructField();
//        protocolFileStructField.setFileIndex(protocolFileStruct.getFileIndex());
//        List<ProtocolFileStructField> structFieldList = protocolService.selectProtocolFileStructFieldList(protocolFileStructField);
//
//        List<ProtocolFileStructField> protocolFileStructFields = structFieldList.stream().filter(v -> v.getStructName().equals(protocolFileStruct.getStructName())).collect(Collectors.toList());
//        List<ProtocolFileStructField> collect = protocolFileStructFields.stream().sorted(Comparator.comparingInt(a -> Integer.parseInt(a.getFieldValue()))).collect(Collectors.toList());
//
//
//        if (protocolFileStruct.getResp() != null) {
//            protocolFileStructFields = structFieldList.stream().filter(v -> v.getStructName().equals(protocolFileStruct.getResp().getStructName())).collect(Collectors.toList());
//            collect = protocolFileStructFields.stream().sorted(Comparator.comparingInt(a -> Integer.parseInt(a.getFieldValue()))).collect(Collectors.toList());
//            list.addAll(collect);
//        }

        Set<Integer> allFileIndex = new HashSet<>();
        allFileIndex.add(protocolFileStruct.getFileIndex());
        ProtocolFileDesc protocolFileDesc1 = protocolService.selectProtocolFileDescById(protocolFileStruct.getFileIndex());
        if (protocolFileDesc1 != null) {
            String imports = protocolFileDesc1.getImports();
            if (StringUtils.isNotEmpty(imports)) {
                String[] split = imports.split(";");
                for (String s : split) {
                    String[] split1 = s.split("/");
                    String s1 = split1[split1.length - 1];

                    ProtocolFileDesc protocolFileDesc2 = new ProtocolFileDesc();
                    protocolFileDesc2.setFileName(s1);
                    protocolFileDesc2.setFileVersion(protocolFileDesc1.getFileVersion());
                    List<ProtocolFileDesc> protocolFileDescs = protocolService.selectProtocolFileDescList(protocolFileDesc2);
                    for (ProtocolFileDesc fileDesc : protocolFileDescs) {
                        allFileIndex.add(fileDesc.getId());
                    }
                }
            }
        }

        List<String> listType = new ArrayList<>();
        listType.add("int32");
        listType.add("int64");
        listType.add("bool");
        listType.add("string");

        List<String> fileField = new ArrayList<>();
        for (ProtocolFileStructField fileStructField : list) {
            if (Strings.isEmpty(fileStructField.getFieldType()) || "null".equals(fileStructField.getFieldType())) {
                continue;
            }
            if (listType.contains(fileStructField.getFieldType())) {
                continue;
            }

            fileField.add(fileStructField.getFieldType());
        }

        List<ProtocolFileStructField> structFieldList1 = new ArrayList<>();

        if (!allFileIndex.isEmpty() && !fileField.isEmpty()) {
            structFieldList1.addAll(protocolService.selectStructFieldByMoreCondition(new ArrayList<>(allFileIndex), fileField));
            structFieldList1.removeIf(v -> StringUtils.isEmpty(v.getFieldType()) || "null".equals(v.getFieldType()));
        }
        return getDataTable(structFieldList1);

    }

    private List<ProtocolFileStruct> getProtocolFileStruct(ProtocolFileStruct protocolFileStruct) {
        // 协议层数据
        ProtocolFileStruct struct = new ProtocolFileStruct();
        struct.setFileIndex(protocolFileStruct.getFileIndex());
        struct.setStructName(protocolFileStruct.getStructName());
        struct.setCmd(protocolFileStruct.getCmd());
        struct.setId(protocolFileStruct.getId());
        List<ProtocolFileStruct> protocolFileStructs = protocolService.selectProtocolFileStructList(struct);
        List<ProtocolFileStruct> reqData = protocolFileStructs.stream().filter(v -> v.getCmd() > 0).collect(Collectors.toList());
        List<ProtocolFileStruct> structList = protocolFileStructs.stream().filter(v -> v.getCmd() <= 0).collect(Collectors.toList());
        List<ProtocolFileStruct> collect = reqData.stream().sorted(Comparator.comparingInt(ProtocolFileStruct::getCmd)).collect(Collectors.toList());
        for (ProtocolFileStruct reqDatum : collect) {
            if (reqDatum.getStructName().endsWith("Resp") && !reqDatum.getStructName().equals("ErrorResp")) {
                String substring = reqDatum.getStructName().substring(0, reqDatum.getStructName().length() - 4);
                ProtocolFileStruct protocolFileStruct1 = structList.stream().filter(v -> v.getStructName().endsWith("Req") && v.getStructName().substring(0, v.getStructName().length() - 3).equals(substring)).findFirst().orElse(null);
                if (protocolFileStruct1 != null) {
                    protocolFileStruct1.setResp(reqDatum);
                } else {
                    logger.error("not found : {} {}", reqDatum.getCmd(), reqDatum.getStructName());
                }
            } else {
                structList.add(reqDatum);
            }
        }
        return structList;
    }

    @GetMapping("/listProtocolFileStructField")
    public TableDataInfo listProtocolFileStructField(ProtocolFileStructField protocolFileStructField) {
        return getDataTable(protocolService.selectProtocolFileStructFieldList(protocolFileStructField));
    }

    @GetMapping(value = "/genProtoFile")
    @ResponseBody
    public WebAsyncTask<AjaxResult> genProtoFile(String fileVersion) {
        if (!PropertyConfig.isDebug()) {
            return new WebAsyncTask<>(1, () -> AjaxResult.error("仅开发模式下可用"));
        }
        if (saveProtoFile.get() || commitFile.get()) {
            return new WebAsyncTask<>(1, () -> AjaxResult.error("正在生成阶段，请稍后..."));
        }

        ProtoVersionData protoVersionData = getProtoVersionData(fileVersion);
        if (protoVersionData == null) {
            return new WebAsyncTask<>(1, () -> AjaxResult.error("版本提交目录配置不存在"));
        }
        saveProtoFile.compareAndSet(false, true);
//        String baseProtoPath = PropertyConfig.get("proto.path", "K:\\workspace_idea\\server-main-sanguo\\slg2-msg-proto");
        String localPath = protoVersionData.getProtoPath();

        StringBuilder sbPro = new StringBuilder();
        sbPro.append("syntax=\"proto2\";\n");
        sbPro.append("option java_package=\"com.pp.game.slg2.msg.protocol\";\n");
        sbPro.append("option java_outer_classname=\"ProtocolProto\";\n\n");
        sbPro.append("//协议定义\n");
        sbPro.append("//六位协议号 第一位1表示请求 2表示响应 3表示服务器通知 次二位表示模块id 后三位表示模块内协议号\n");

        ProtocolCmd protocolCmd1 = new ProtocolCmd();
        fileVersion = StringUtils.isEmpty(fileVersion) ? "main" : fileVersion;
        protocolCmd1.setCmdVersion(fileVersion);
        List<ProtocolCmd> protocolCmds = protocolService.selectProtocolCmdList(protocolCmd1);
        if (protocolCmds.isEmpty()) {
            saveProtoFile.compareAndSet(true, false);
            return new WebAsyncTask<>(1, () -> AjaxResult.error("没有协议可生成"));
        }
        List<ProtocolCmd> collect2 = protocolCmds.stream().sorted((a,b) -> {
            String aStr = String.valueOf(a.getCmdId());
            String bStr = String.valueOf(b.getCmdId());
            int compare = Integer.compare(aStr.length(), bStr.length());
            if (compare == 0) {
                compare = Integer.compare(Integer.parseInt(aStr.substring(1)), Integer.parseInt(bStr.substring(1)));
            }
            if (compare == 0) {
                compare = Integer.compare(Integer.parseInt(aStr.substring(0, 1)), Integer.parseInt(bStr.substring(0, 1)));
            }
            return compare;
        }).collect(Collectors.toList());

        boolean isUseNewProto = PropertyConfig.getBooleanValue("use.new.proto.file", false);
        if (isUseNewProto) {
            List<ProtocolCmd> collect3 = collect2.stream().filter(v -> {
                if (v.getCmdId().toString().startsWith("1")) {
                    return v.getCmdId() / 1000 > 10008;
                } else if (v.getCmdId().toString().startsWith("2")) {
                    return v.getCmdId() / 1000 > 20008;
                } else if (v.getCmdId().toString().startsWith("3")) {
                    return v.getCmdId() / 1000 > 30008;
                }
                return true;
            }).collect(Collectors.toList());
            collect2.removeAll(collect3);

            if (!collect3.isEmpty()) {
                StringBuilder proNewSb = new StringBuilder();
                proNewSb.append("syntax=\"proto2\";\n");
                proNewSb.append("option java_package=\"com.pp.game.slg2.msg.protocolNew\";\n");
                proNewSb.append("option java_outer_classname=\"ProtocolNewProto\";\n\n");
                proNewSb.append("//协议定义\n");
                proNewSb.append("\n//新结构体，上面那个已经爆了，所以超出10008（也就是10008001后）的协议都放在这里 第一位1表示请求 2表示响应 3表示服务器通知 次二位表示模块id " +
                        "后三位表示模块内协议号\n");
                proNewSb.append("enum ProtocolNew {\n");
                for (ProtocolCmd protocolCmd : collect3) {
                    proNewSb.append("    ").append(protocolCmd.getCmdName()).append(" = ").append(protocolCmd.getCmdId()).append(";").append("// ").append(protocolCmd.getCmdDesc()).append("\n");
                }
                proNewSb.append("}\n");
                File proFile1 = new File(localPath + "protocolNew.proto");
                try {
                    FileOutputStream fileProOutputStream = new FileOutputStream(proFile1);
                    fileProOutputStream.write(proNewSb.toString().getBytes(StandardCharsets.UTF_8));
                    fileProOutputStream.flush();
                    fileProOutputStream.close();
                } catch (IOException e) {
                    saveProtoFile.compareAndSet(true, false);
                    throw new RuntimeException(e);
                }
            }
        }

        sbPro.append("enum Protocol {\n");
        for (ProtocolCmd protocolCmd : collect2) {
            sbPro.append("    ").append(protocolCmd.getCmdName()).append(" = ").append(protocolCmd.getCmdId()).append(";").append("// ").append(protocolCmd.getCmdDesc()).append("\n");
        }
        sbPro.append("}\n");

        File proFile = new File(localPath + "protocol.proto");
        try {
            FileOutputStream fileProOutputStream = new FileOutputStream(proFile);
            fileProOutputStream.write(sbPro.toString().getBytes(StandardCharsets.UTF_8));
            fileProOutputStream.flush();
            fileProOutputStream.close();
        } catch (IOException e) {
            saveProtoFile.compareAndSet(true, false);
            throw new RuntimeException(e);
        }


        ProtocolFileDesc protocolFileDesc = new ProtocolFileDesc();
        protocolFileDesc.setFileVersion(fileVersion);
        List<ProtocolFileDesc> protocolFileDescs = protocolService.selectProtocolFileDescList(protocolFileDesc);
        for (ProtocolFileDesc fileDesc : protocolFileDescs) {
            try {
                String fileName = fileDesc.getFileName();
                StringBuilder sb = new StringBuilder();
                sb.append("syntax = \"").append(fileDesc.getSyntax()).append("\";\n");
                sb.append("option java_package = \"").append(fileDesc.getJavaPackage()).append("\";\n");
                sb.append("option java_outer_classname = \"").append(fileDesc.getJavaOuterClassname()).append("\";\n");
                String imports = fileDesc.getImports();
                if (StringUtils.isNotEmpty(imports)) {
                    String[] split = imports.split(";");
                    for (String s : split) {
                        sb.append("import \"").append(s).append("\";\n");
                    }
                }
                sb.append("\n");

                ProtocolFileStruct protocolFileStruct = new ProtocolFileStruct();
                protocolFileStruct.setFileIndex(fileDesc.getId());
                List<ProtocolFileStruct> protocolFileStructs = getProtocolFileStruct(protocolFileStruct);

                ProtocolFileStructField protocolFileStructField = new ProtocolFileStructField();
                protocolFileStructField.setFileIndex(fileDesc.getId());
                List<ProtocolFileStructField> protocolFileStructFields = protocolService.selectProtocolFileStructFieldList(protocolFileStructField);
                Map<String, List<ProtocolFileStructField>> collect = protocolFileStructFields.stream().collect(Collectors.groupingBy(v -> v.getStructName()));

                for (ProtocolFileStruct fileStruct : protocolFileStructs) {
                    if (StringUtils.isNotEmpty(fileStruct.getStructDesc())) {
                        sb.append("// ").append(fileStruct.getStructDesc()).append("\n");
                    }
                    sb.append(fileStruct.getProtoType()).append(" ").append(fileStruct.getStructName()).append(" {\n");
                    List<ProtocolFileStructField> protocolFileStructFields1 = collect.get(fileStruct.getStructName());
                    if (protocolFileStructFields1 != null) {
                        List<ProtocolFileStructField> collect1 = protocolFileStructFields1.stream().sorted(Comparator.comparingInt(a -> Integer.parseInt(a.getFieldValue()))).collect(Collectors.toList());
                        for (ProtocolFileStructField fileStructField : collect1) {
                            if (fileStruct.getCmd() != 0) {
                                sb.append("    ").append(fileStructField.getFieldTypeDesc()).append(" ").append(fileStructField.getFieldType()).append(" ").append(fileStructField.getFieldName()).append(" ").append("=").append(" ").append(fileStructField.getFieldValue()).append(";").append(" // ").append(fileStructField.getFieldDesc()).append("\n");
                            } else {
                                sb.append("    ").append(fileStructField.getFieldName()).append(" ").append("=").append(" ").append(fileStructField.getFieldValue()).append(";").append(" // ").append(fileStructField.getFieldDesc()).append("\n");
                            }
                        }
                    }
                    sb.append("}\n\n");

                    ProtocolFileStruct resp = fileStruct.getResp();
                    if (resp != null) {
                        if (StringUtils.isNotEmpty(resp.getStructDesc())) {
                            sb.append("// ").append(resp.getStructDesc()).append("\n");
                        }
                        sb.append(resp.getProtoType()).append(" ").append(resp.getStructName()).append(" {\n");
                        protocolFileStructFields1 = collect.get(resp.getStructName());
                        if (protocolFileStructFields1 != null) {
                            List<ProtocolFileStructField> collect1 = protocolFileStructFields1.stream().sorted(Comparator.comparingInt(a -> Integer.parseInt(a.getFieldValue()))).collect(Collectors.toList());
                            for (ProtocolFileStructField fileStructField : collect1) {
                                sb.append("    ").append(fileStructField.getFieldTypeDesc()).append(" ").append(fileStructField.getFieldType()).append(" ").append(fileStructField.getFieldName()).append(" ").append("=").append(" ").append(fileStructField.getFieldValue()).append(";").append(" // ").append(fileStructField.getFieldDesc()).append("\n");
                            }
                        }
                        sb.append("}\n\n");
                    }
                }

                File file = new File(localPath + fileName);
                FileOutputStream fileOutputStream = new FileOutputStream(file);
                fileOutputStream.write(sb.toString().getBytes(StandardCharsets.UTF_8));
                fileOutputStream.flush();
                fileOutputStream.close();
            } catch (Exception e) {
                saveProtoFile.compareAndSet(true, false);
                throw new RuntimeException(e);
            }
        }

        saveProtoFile.compareAndSet(true, false);
        if (PropertyConfig.getBooleanValue("proto.auto.commit", true)) {
            Callable<AjaxResult> callable = () -> {
                String s = "";
                try {
                    commitFile.compareAndSet(false, true);
                    logger.info("commit file:{}", protoVersionData.getCommitPath());
                    String commitPathFile = protoVersionData.getCommitPath();//PropertyConfig.get("commit.bat.path", "K:\\workspace_idea\\server-main-sanguo\\slg2-msg-proto\\batch_commit.bat");
                    File batFile = new File(commitPathFile);
                    boolean batFileExist = batFile.exists();
                    if (batFileExist) {
                        s = ExecuteShellUtil.callCmd(batFile.getPath());
                        logger.info(s);
                    } else {
                        logger.error("svn update fail");
                    }
                    commitFile.compareAndSet(true, false);
                } catch (Exception e) {
                    commitFile.compareAndSet(true, false);
                    throw new RuntimeException(e);
                }
                return AjaxResult.success(s);
            };

            WebAsyncTask<AjaxResult> webAsyncTask = new WebAsyncTask<>(60000, callable);
            webAsyncTask.onTimeout(() -> {
                commitFile.compareAndSet(true, false);
                return AjaxResult.error("接口超时，请稍等片刻");
            });
            return webAsyncTask;
        }

        return new WebAsyncTask<>(1, AjaxResult::success);
    }

    @DeleteMapping("/deleteProtocolFileStruct")
    public AjaxResult deleteProtocolFileStruct(@Validated @RequestBody ProtocolFileStruct protocolFileStruct)
    {
        if (!PropertyConfig.isDebug()) {
            return AjaxResult.error("仅开发模式下可用");
        }
        String version = Strings.isEmpty(protocolFileStruct.getFileVersion()) ? "main" : protocolFileStruct.getFileVersion();

        ProtocolFileStructField selectFileStruct = new ProtocolFileStructField();
        selectFileStruct.setStructName(protocolFileStruct.getStructName());
        selectFileStruct.setFileIndex(protocolFileStruct.getFileIndex());
        int count = 0;
        List<ProtocolFileStructField> protocolFileStructFields = protocolService.selectProtocolFileStructFieldList(selectFileStruct);
        List<Integer> collectDel = protocolFileStructFields.stream().map(ProtocolFileStructField::getId).collect(Collectors.toList());

        if (protocolFileStruct.getCmd() > 0) {
            protocolService.deleteProtocolCmdById(protocolFileStruct.getCmd(), version);
            count++;
        }

        if (protocolFileStruct.getCmd() > 0 && protocolFileStruct.getResp() != null) {
            selectFileStruct = new ProtocolFileStructField();
            selectFileStruct.setStructName(protocolFileStruct.getResp().getStructName());
            selectFileStruct.setFileIndex(protocolFileStruct.getFileIndex());
            protocolFileStructFields = protocolService.selectProtocolFileStructFieldList(selectFileStruct);

            List<Integer> collect = protocolFileStructFields.stream().map(ProtocolFileStructField::getId).collect(Collectors.toList());
            collectDel.addAll(collect);

            protocolService.deleteProtocolCmdById(protocolFileStruct.getResp().getCmd(), version);
            protocolService.deleteProtocolFileStructById(protocolFileStruct.getResp().getId());
            count++;
        }
        count++;
        protocolService.deleteProtocolFileStructById(protocolFileStruct.getId());
        count += collectDel.size();
        if (!collectDel.isEmpty()) {
            Integer[] objects = collectDel.toArray(new Integer[0]);
            protocolService.deleteProtocolFileStructFieldByIds(objects);
        }

        return toAjax(count);
    }

    @DeleteMapping("/deleteProtocolFile")
    public AjaxResult deleteProtocolFile(@Validated @RequestBody ProtocolFileDesc protocolFileDesc)
    {
        if (!PropertyConfig.isDebug()) {
            return AjaxResult.error("仅开发模式下可用");
        }
        if (protocolFileDesc.getId() == null) {
            return AjaxResult.error("不存在ID");
        }
        ProtocolFileStructField selectFileStructField = new ProtocolFileStructField();
        selectFileStructField.setFileIndex(protocolFileDesc.getId());
        List<ProtocolFileStructField> protocolFileStructFields = protocolService.selectProtocolFileStructFieldList(selectFileStructField);
        for (ProtocolFileStructField protocolFileStructField : protocolFileStructFields) {
            protocolService.deleteProtocolFileStructFieldById(protocolFileStructField.getId());
        }
        String version = Strings.isEmpty(protocolFileDesc.getFileVersion()) ? "main" : protocolFileDesc.getFileVersion();

        ProtocolFileStruct selectFileStruct = new ProtocolFileStruct();
        selectFileStruct.setFileIndex(protocolFileDesc.getId());
        List<ProtocolFileStruct> protocolFileStructs = protocolService.selectProtocolFileStructList(selectFileStruct);
        for (ProtocolFileStruct protocolFileStruct : protocolFileStructs) {
            protocolService.deleteProtocolFileStructById(protocolFileStruct.getId());
            if (protocolFileStruct.getCmd() > 0) {
                protocolService.deleteProtocolCmdById(protocolFileStruct.getCmd(), version);
            }
            if (protocolFileStruct.getResp() != null) {
                protocolService.deleteProtocolFileStructById(protocolFileStruct.getResp().getId());
                if (protocolFileStruct.getResp().getCmd() > 0) {
                    protocolService.deleteProtocolCmdById(protocolFileStruct.getResp().getCmd(), version);
                }
            }
        }

        return toAjax(protocolService.deleteProtocolFileDescById(protocolFileDesc.getId()));
    }

    private static Map<String, ProtoVersionData> protoVersionDataMap = new HashMap<>();
    static {
        if (PropertyConfig.isDebug()) {
            ProtoVersionData protoVersionData = new ProtoVersionData();
            protoVersionData.setVersion("main");
            protoVersionData.setProtoPath("K:\\workspace_idea\\server-main-sanguo\\slg2-msg-proto\\proto\\");
            protoVersionData.setCommitPath("K:\\workspace_idea\\server-main-sanguo\\slg2-msg-proto\\proto_java261.bat");
            protoVersionData.setVersionName("三国-main");
            protoVersionDataMap.put(protoVersionData.getVersion(), protoVersionData);


            ProtoVersionData protoVersionData1 = new ProtoVersionData();
            protoVersionData1.setVersion("xhzs-main");
            protoVersionData1.setProtoPath("K:\\workspace_idea\\xhzs\\slg2-server\\proto-module\\proto\\");
            protoVersionData1.setCommitPath("K:\\workspace_idea\\xhzs\\slg2-server\\proto-module\\proto_java261.bat");
            protoVersionData1.setVersionName("星魂之上-main");
            protoVersionDataMap.put(protoVersionData1.getVersion(), protoVersionData1);
        }
    }

    private ProtoVersionData getProtoVersionData(String version) {
        return protoVersionDataMap.get(version);
    }
}
