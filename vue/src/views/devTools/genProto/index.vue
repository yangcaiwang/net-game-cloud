<template>
  <div class="app-container">

    <p style="color:crimson">非开发者不可操作</p>

    <el-row>
      <el-button size="small" type="primary" @click="submitTree">生 成</el-button>
      <el-button size="small" type="primary" @click="addProto">添加proto</el-button>
            <el-select
        v-model="fileVersion"
        placeholder="选择proto版本"
        style="width: 215px;margin-left:10px"
        @visible-change="visibleChange"
      >
        <el-option
          v-for="item in allVersionArr "
          :key = "item.version"
          :label="item.versionName"
          :value="item.version"
        ></el-option>
      </el-select>
    </el-row>

  <el-row :gutter="20">
    <el-input style="width: 380px;margin-left:10px"
          placeholder="输入关键字进行过滤"
          v-model="filterText">
    </el-input>
  </el-row>
      <!-- <el-header>Header</el-header> -->
      <el-container>
        <el-aside width="380px">

          <el-tree
            :props="defaultProps"
            :load="loadNode"
            lazy
            ref="tree"
            accordion
            :default-expanded-keys="expandedKeys"
            node-key="id"
            style="height: calc(100vh - 290px)"
            @node-click="handleNodeClick"
            :expand-on-click-node="false"
            :filter-node-method="filterNode"
          >
            <span class="custom-tree-node" slot-scope="{ node, data }">
              <span :title="data.fileName" v-if="node.level == 1 && data.cmdPre == 0">{{ data.fileName }}</span>
              <span :title="data.fileName" v-if="node.level == 1  && data.cmdPre != 0">{{ data.fileName }}--{{data.cmdPre}}</span>
              <span :title="data.structName" v-if="node.level == 2 && data.cmd > 0" >{{data.cmd}}--{{ data.structName | ellipsis(16) }}</span>
              <span :title="data.structName" v-if="node.level == 2 && data.cmd <= 0"> {{ data.structName | ellipsis(16) }}</span>
              <span>
                <el-button
                  style="margin-left: 5px"
                  v-if="node.level != 2"
                  type="text"
                  size="mini"
                  @click="() => open(node, data, 1)"
                >
                  添加
                </el-button>
                <el-button
                  style="margin-left: 5px"
                  type="text"
                  size="mini"
                  @click="() => open(node, data, 2)"
                >
                  编辑
                </el-button>
                <el-button
                  style="margin-left: 5px"
                  type="text"
                  size="mini"
                  @click="() => remove(node, data)"
                >
                  删除
                </el-button>
              </span>
            </span>
          </el-tree>
        </el-aside>


        <el-container>
          <el-main v-if="(dialogVisible)">
            <el-form ref="form" :model="temp" label-width="120px" :rules="rules">
              <el-row>
                <el-col :span="6">
                  <p>消息描述</p>
                  <!-- <el-form-item label="消息描述：">
                  </el-form-item> -->
                </el-col>
              </el-row>
              <el-row v-if="addProtoFile">
                <el-col :span="6">
                  <el-form-item label="文件名" prop="fileName">
                    <el-input v-model="temp.fileName"></el-input>
                  </el-form-item>
                </el-col>
                <el-col :span="6">
                  <el-form-item label="文件描述" prop="fileDesc">
                    <el-input v-model="temp.fileDesc"></el-input>
                  </el-form-item>
                </el-col>
                <el-col :span="6">
                  <el-form-item label="命令前缀" prop="cmdPre">
                    <el-input v-model="temp.cmdPre"></el-input>
                  </el-form-item>
                </el-col>
              </el-row>
              <template v-if="addProtoFile && temp.dynamicImport != undefined">
                <el-row v-for="(itemImport, index) in temp.dynamicImport" :key="index + '.fileName'">
                <el-col :span="4">
                  <el-form-item label="import" :prop="'dynamicImport.' + index + '.fileName'">
                    <el-select v-model="temp.dynamicImport[index].fileName" style="width: 180px" placeholder="请选择" filterable>
                      <el-option
                        v-for="item in importOptions"
                        :key="item.id"
                        :label="item.fileName"
                        :value="item.fileName"
                      ></el-option>
                    </el-select>
                  </el-form-item>
                </el-col>
                <el-col :span="8">
                  <el-form-item>
                    <el-button v-if="index+1 == temp.dynamicImport.length" @click="handleAddImport" type="primary">增加</el-button>
                    <el-button v-if="index !== 0" @click="handleDelImport(itemImport, index)" type="danger">删除</el-button>
                  </el-form-item>
                </el-col>
              </el-row>
            </template>

            <template v-if="!addProtoFile">
              <el-row>
                <el-col :span="6">
                  <el-form-item label="协议类型" prop="messageType">
                    <el-select
                      v-model="temp.messageType"
                      placeholder="选择协议类型"
                      style="width: 240px"
                    >
                      <el-option key=1 label="枚举enum" value="1" />
                      <el-option key=2 label="消息message" value="2" />
                      <el-option key=3 label="结构体" value="3" />
                      <el-option key=4 label="广播消息" value="4" />
                    </el-select>
                  </el-form-item>
                </el-col>
              </el-row>
              <el-row >
                <el-col :span="6" >
                  <el-form-item label="消息名" prop="structName">
                    <el-input v-model="temp.structName"></el-input>
                  </el-form-item>
                </el-col>
                <el-col :span="6" v-if="(temp.messageType == 4 || temp.messageType == 2)">
                  <el-form-item label="消息ID" prop="cmd">
                    <el-input v-model="temp.cmd"></el-input>
                  </el-form-item>
                </el-col>
                <el-col :span="6">
                  <el-form-item label="消息描述" prop="structDesc">
                    <el-input v-model="temp.structDesc"></el-input>
                  </el-form-item>
                </el-col>
              </el-row>
              <el-row v-if="(temp.messageType == 2)">
                <el-col :span="6">
                  <el-form-item label="应答消息名" prop="respStructName">
                    <el-input v-model="temp.resp.structName"></el-input>
                  </el-form-item>
                </el-col>
                <el-col :span="6">
                  <el-form-item label="应答消息ID" prop="respCmd">
                    <el-input v-model="temp.resp.cmd"></el-input>
                  </el-form-item>
                </el-col>
                <el-col :span="6">
                  <el-form-item label="应答消息描述" prop="respStructDesc">
                    <el-input v-model="temp.resp.structDesc"></el-input>
                  </el-form-item>
                </el-col>
              </el-row>

              <el-row>
                <el-col :span="6">
                  <p v-if="temp.messageType != '2'">字段定义：</p>
                  <p v-if="temp.messageType == '2'">请求字段定义：</p>
                </el-col>
              </el-row>

              <template v-if="(temp.messageType != 1 && temp.structFieldList != undefined)">
                <el-row v-for="(item, index) in temp.structFieldList" :key="index">
                  <el-col :span="3">
                    <el-form-item label="操作" :prop="'structFieldList.'+ index  + '.fieldTypeDesc'">
                      <el-select
                        v-model="temp.structFieldList[index].fieldTypeDesc"
                        placeholder="选择操作类型"
                        style="width: 90px"
                        filterable
                      >
                      <el-option
                          v-for="item in fieldOptions"
                          :key="item.optName"
                          :label="item.optName"
                          :value="item.optName"
                        ></el-option>
                      </el-select>
                    </el-form-item>
                  </el-col>
                  <el-col :span="3">

                    <el-form-item label="定义" :prop="'structFieldList.' + index + '.fieldType'">
                      <el-select
                        v-model="temp.structFieldList[index].fieldType"
                        placeholder="选择操作类型"
                        style="width: 90px"
                        filterable
                      >
                      <el-option
                          v-for="item in selfDefineType"
                          :key="item.valueType"
                          :label="item.valueType"
                          :value="item.valueType"
                        ></el-option>
                      </el-select>
                    </el-form-item>

                    <!-- <el-form-item label="定义" :prop="'structFieldList.' + index + '.fieldType'">
                      <el-input v-model="temp.structFieldList[index].fieldType" style="width: 90px"></el-input>
                    </el-form-item> -->
                  </el-col>
                  <el-col :span="4">
                    <el-form-item label="字段名" :prop="'structFieldList.' + index + '.fieldName'">
                      <el-input v-model="temp.structFieldList[index].fieldName"  style="width: 160px"></el-input>
                    </el-form-item>
                  </el-col>
                  <el-col :span="2">
                    <el-form-item label="序号" :prop="'structFieldList.' + index + '.fieldValue'">
                      <el-input v-model="temp.structFieldList[index].fieldValue"  style="width: 60px"></el-input>
                    </el-form-item>
                  </el-col>
                  <el-col :span="4">
                    <el-form-item label="描述" :prop="'structFieldList.' + index + '.fieldDesc'">
                      <el-input v-model="temp.structFieldList[index].fieldDesc" style="width: 160px"></el-input>
                    </el-form-item>
                  </el-col>
                  <el-col :span="6">
                    <el-form-item>
                      <el-button v-if="index+1 == temp.structFieldList.length" @click="handleAddField" type="primary" >增加</el-button>
                      <el-button v-if="index !== 0" @click="handleDelField(item, index)" type="danger">删除</el-button>
                    </el-form-item>
                  </el-col>
                </el-row>
              </template>

              <el-row v-if="temp.messageType == 2 && temp.resp != undefined">
                <el-col :span="6">
                  <p >返回字段定义：</p>
                </el-col>
              </el-row>

              <template v-if="(temp.messageType == 2 && temp.resp != undefined && temp.resp.structFieldList != undefined)">
                <el-row v-for="(item, index) in temp.resp.structFieldList" :key="index + '.resp'">
                  <el-col :span="3">
                    <el-form-item label="操作" :prop="'resp.structFieldList.'+ index+ '.fieldTypeDesc'">
                      <el-select
                        v-model="temp.resp.structFieldList[index].fieldTypeDesc"
                        placeholder="选择操作类型"
                        style="width: 90px"
                        filterable
                      >
                      <el-option
                          v-for="item in fieldOptions"
                          :key="item.optName"
                          :label="item.optName"
                          :value="item.optName"
                        ></el-option>
                      </el-select>
                    </el-form-item>
                  </el-col>
                  <el-col :span="3">

                    <el-form-item label="定义" :prop="'resp.structFieldList.' + index+ '.fieldType'">
                      <el-select
                        v-model="temp.resp.structFieldList[index].fieldType"
                        placeholder="选择操作类型"
                        style="width: 90px"
                        filterable
                      >
                      <el-option
                          v-for="item in selfDefineType"
                          :key="item.valueType"
                          :label="item.valueType"
                          :value="item.valueType"
                        ></el-option>
                      </el-select>
                    </el-form-item>

                    <!-- <el-form-item label="定义" :prop="'resp.structFieldList.' + index+ '.fieldType'">
                      <el-input v-model="temp.resp.structFieldList[index].fieldType" style="width: 90px"></el-input>
                    </el-form-item> -->
                  </el-col>
                  <el-col :span="4">
                    <el-form-item label="字段名" :prop="'resp.structFieldList.' + index+ '.fieldName'">
                      <el-input v-model="temp.resp.structFieldList[index].fieldName"  style="width: 160px"></el-input>
                    </el-form-item>
                  </el-col>
                  <el-col :span="2">
                    <el-form-item label="序号" :prop="'resp.structFieldList.' + index + '.fieldValue'">
                      <el-input v-model="temp.resp.structFieldList[index].fieldValue"  style="width: 60px"></el-input>
                    </el-form-item>
                  </el-col>
                  <el-col :span="4">
                    <el-form-item label="描述" :prop="'resp.structFieldList.' + index+ '.fieldDesc'">
                      <el-input v-model="temp.resp.structFieldList[index].fieldDesc" style="width: 160px"></el-input>
                    </el-form-item>
                  </el-col>
                  <el-col :span="6">
                    <el-form-item>
                      <el-button v-if="index+1 == temp.resp.structFieldList.length" @click="handleAddRespField" type="primary" >增加</el-button>
                      <el-button v-if="index !== 0" @click="handleDelRespField(item, index)" type="danger">删除</el-button>
                    </el-form-item>
                  </el-col>
                </el-row>
              </template>

              <template v-if="(temp.messageType == 1 && temp.structFieldList != undefined && temp.structFieldList.length > 0)">
                <el-row v-for="(item, index) in temp.structFieldList" :key="index + '.enum'">
                  <el-col :span="6">
                    <el-form-item label="枚举名" :prop="'structFieldList.' + index + '.fieldName'">
                      <el-input v-model="temp.structFieldList[index].fieldName"  style="width: 160px"></el-input>
                    </el-form-item>
                  </el-col>
                  <el-col :span="6">
                    <el-form-item label="枚举值" :prop="'structFieldList.' + index + '.fieldValue'">
                      <el-input v-model="temp.structFieldList[index].fieldValue" style="width: 160px"></el-input>
                    </el-form-item>
                  </el-col>
                  <el-col :span="4">
                    <el-form-item label="描述" :prop="'structFieldList.' + index+ '.fieldDesc'">
                      <el-input v-model="temp.structFieldList[index].fieldDesc" style="width: 160px"></el-input>
                    </el-form-item>
                  </el-col>
                  <el-col :span="8">
                    <el-form-item>
                      <el-button v-if="index+1 == temp.structFieldList.length" @click="handleAddField" type="primary" >增加</el-button>
                      <el-button v-if="index !== 0" @click="handleDelField(item, index)" type="danger">删除</el-button>
                    </el-form-item>
                  </el-col>
                </el-row>
              </template>
            </template>

            </el-form>
            <el-button @click="cancel">取 消</el-button>
            <el-button size="small" type="primary" @click="submit">确 定</el-button>
          </el-main>

          <el-main v-if="showView && clickNodeData != undefined">
            <el-row v-if="clickNodeData.fileName != undefined">
              <el-col :span="8">
                <label >文件名：{{ clickNodeData.fileName }}</label>
                <!-- <el-input v-model="clickNodeData.fileName" readonly="readonly" style="width: 240px"></el-input> -->
              </el-col>
              <el-col :span="8">
                <label >文件描述：{{ clickNodeData.fileDesc }}</label>
                <!-- <el-input v-model="clickNodeData.fileDesc" readonly="readonly" style="width: 240px"></el-input> -->
              </el-col>
              <el-col :span="8">
                <label >命令前缀：{{ clickNodeData.cmdPre }}</label>
                <!-- <el-input v-model="clickNodeData.cmdPre" readonly="readonly" style="width: 240px"></el-input> -->
              </el-col>
            </el-row>
            <br/>
            <template v-if="clickNodeData.fileName != undefined">
              <el-row v-for="(item, index) in clickNodeData.dynamicImport" :key="index + '.dynamicImport'">
                <el-col :span="12">
                  <label>imports: {{ item.fileName }}</label>
                  <!-- <el-input v-model="item.fileName" readonly="readonly" style="width: 240px"></el-input> -->
                </el-col>
              </el-row>
            </template>


            <template v-if="clickNodeData.cmd != undefined">
              <el-row v-if="clickNodeData.cmd == 0">
                <el-col :span="8" >
                  <label>枚举名: {{ clickNodeData.structName }}</label>
                  <!-- <el-input v-model="clickNodeData.structName" readonly="readonly" style="width: 240px"></el-input> -->
                </el-col>
                <el-col :span="16">
                  <label>枚举名描述: {{ clickNodeData.structDesc }}</label>
                  <!-- <el-input v-model="clickNodeData.structDesc" readonly="readonly" style="width: 240px"></el-input> -->
                </el-col>
              </el-row>
              <el-row v-if="clickNodeData.cmd == -1 || clickNodeData.cmd > 0">
                <el-col :span="8" >
                  <label >消息名：{{ clickNodeData.structName }}</label>
                  <!-- <el-input v-model="clickNodeData.structName" readonly="readonly" style="width: 240px"></el-input> -->
                </el-col>
                <el-col :span="16" v-if="clickNodeData.cmd > 0 && clickNodeData.protocolCmd != undefined" >
                  <label >消息ID：{{clickNodeData.cmd}}-{{clickNodeData.protocolCmd.cmdName}}</label>
                </el-col>
                <el-col :span="12" >
                  <label>消息名描述：{{clickNodeData.structDesc}}</label>
                </el-col>
              </el-row>
              <br/>
              <el-row v-if="clickNodeData.cmd > 0 && clickNodeData.resp != undefined">
                <el-col :span="8" >
                  <label>返回消息名：{{clickNodeData.resp.structName}}</label>
                  <!-- <el-input v-model="clickNodeData.resp.structName" readonly="readonly" style="width: 240px"></el-input> -->
                </el-col>
                <el-col :span="16" v-if="clickNodeData.resp.cmd > 0 && clickNodeData.resp.protocolCmd != undefined" >
                  <label>返回消息ID：{{ clickNodeData.resp.cmd }}-{{ clickNodeData.resp.protocolCmd.cmdName }}</label>
                  <!-- <el-input v-model="clickNodeData.resp.cmd" readonly="readonly" style="width: 240px"></el-input> -->
                </el-col>
                <el-col :span="12" >
                  <label>返回消息描述：{{ clickNodeData.resp.structDesc }}</label>
                  <!-- <el-input v-model="clickNodeData.resp.structDesc" readonly="readonly" style="width: 240px"></el-input> -->
                </el-col>
              </el-row>
              <br/>
              <template v-if="clickNodeData.structFieldList != undefined">
                <label v-if="clickNodeData.cmd <= 0 || clickNodeData.resp == undefined">字段描述</label>
                <label v-if="clickNodeData.cmd > 0 && clickNodeData.resp != undefined">请求字段描述</label>
                <el-table :data="clickNodeData.structFieldList" >
                  <el-table-column label="字段操作类型" prop="fieldTypeDesc" align="center" width="180" v-if="clickNodeData.cmd != 0" />
                  <el-table-column label="字段类型" prop="fieldType" align="center" width="180" v-if="clickNodeData.cmd != 0" show-overflow-tooltip >
                    <template slot-scope="scope">
                      <el-popover trigger="hover" placement="top">
                        <p v-html="getFieldItemByName(scope.row.fieldType)" style="white-space:pre-wrap"></p>
                        <div slot="reference" class="name-wrapper">
                          <el-tag size="medium">{{ scope.row.fieldType }}</el-tag>
                        </div>
                      </el-popover>
                    </template>
                  </el-table-column>
                  <el-table-column label="字段名" prop="fieldName" align="center" />
                  <el-table-column label="字段序号" prop="fieldValue" align="center" width="180" />
                  <el-table-column label="字段描述" prop="fieldDesc" align="center" />
                </el-table>
                <br/>
                <template v-if="clickNodeData.resp != undefined">
                  <label>返回字段描述</label>
                  <el-table :data="clickNodeData.resp.structFieldList" >
                  <el-table-column label="字段操作类型" prop="fieldTypeDesc" align="center" width="180" />
                  <el-table-column label="字段类型" prop="fieldType" align="center" width="180" show-overflow-tooltip >
                    <template slot-scope="scope">
                      <el-popover trigger="hover" placement="top">
                         <div v-html="getFieldItemByName(scope.row.fieldType)" style="white-space:pre-wrap"></div>
                        <div slot="reference" class="name-wrapper">
                          <el-tag size="medium">{{ scope.row.fieldType }}</el-tag>
                        </div>
                      </el-popover>
                    </template>
                  </el-table-column>
                  <el-table-column label="字段名" prop="fieldName" align="center" />
                  <el-table-column label="字段序号" prop="fieldValue" align="center" width="180" />
                  <el-table-column label="字段描述" prop="fieldDesc" align="center" />
                </el-table>
                </template>

              </template>

            </template>
          </el-main>

          <el-main v-if="showLogView">
            <p style="text-align:left;color: blue;">{{ logTipsText }}</p>
            <div v-html="getGenLog()"></div>
          </el-main>
        </el-container>
      </el-container>

  </div>
</template>

<script>
import {
  // add,
  // remove,
  listAllProto,
  listProtoFileStruct,
  genProtoFile,
  saveProtoFileDesc,
  updateProtoFileDesc,
  saveProtoFileStruct,
  updateProtoFileStruct,
  listProtoStruct,
  deleteProtocolFileStruct,
  deleteProtocolFile,
  listProtoVersionInfo,
  getProtoFileAllStruct,
} from "@/api/devTools/devTools";
export default {
  watch: {
    filterText(val) {
      this.$refs.tree.filter(val);
      if (val == "") {
        if (this.node_had != undefined) {
          this.node_had.loaded = false;
        }
      }
    },
  },
  created() {
    // this.getAllProto();
    this.getAllVersions();
  },
  data() {
    return {
      filterText: '',
      expandedKeys: [],
      rules: {
        fileName: [{ required: true, message: "请输入文件名", trigger: "blur" },
        {
          pattern: /^[A-Za-z0-9_]*.proto$/,
          message: "请求消息名只能.proto结尾"
        }
        ],
        structName: [{ required: true, message: "请输入消息名", trigger: "blur" },
        {
          pattern: /^[A-Z][A-Za-z0-9_]*$/,
          message: "请求消息名只能以Req结尾"
        }
        ],
        cmd: [{ required: true, message: "请输入消息ID", trigger: "blur" },
        {
          pattern: /^\d{6,7}$/,
          message: "请求消息ID只能是数字并且长度位6或者7"
        }
        ],
        // name: [{ required: true, message: "请输入名称", trigger: "blur" }],
      },
      dialogVisible: false,
      data: [],
      defaultProps: {
        children: 'children',
        label: 'name',
        isLeaf: 'leaf'
      },
      parentId: 0,
      temp: {
        cmd: undefined,
        structName: undefined,
        structDesc: undefined,
        messageType: "1",
        resp: {
          cmd: undefined,
          structName: undefined,
          structDesc: undefined,
        },
        structFieldList: [
          {
            fieldTypeDesc: "",
            fieldType: "",
            fieldName: "",
            fieldDesc: "",
            fieldValue: "",
          }
        ],
        resp: {
          structFieldList: [
          {
            fieldTypeDesc: "",
            fieldType: "",
            fieldName: "",
            fieldDesc: "",
            fieldValue: "",
          }
        ],
        },
        dynamicImport: [
          {
            id: "",
            fileName: "",
          }
        ],
      },

      importOptions:[
        {
          id: "",
          fileName: ""
        },
      ],
      fieldOptions:[
        {
          optName: "optional"
        },
        {
          optName: "repeated"
        },
      ],
      id: undefined,
      node_had: undefined,
      resolve_had: undefined,
      addProtoFile: false,
      clickNodeData: undefined,
      showView: false,
      editProto: false,
      selfDefineType: [],
      openNode: undefined,
      showLogView: false,
      genLogValue: "",
      logTipsText: "正在生成提交中...",
      fileVersion: "main",
      nodeMaxId: 0,
      allVersionArr: [{"version": "main"}],
      protoAllFileField: undefined,
    };
  },
  components: {},
  mounted() {},
  methods: {
    filterNode(value, data) {
        if (!value) return true;

        if (data.fileName != undefined && data.fileName.indexOf(value) !== -1) {
          return true;
        }
        if (data.structName != undefined && data.structName.indexOf(value) !== -1) {
          return true;
        }
        if (data.cmd != undefined && data.cmd.toString().indexOf(value) !== -1) {
          return true;
        }
        if (data.cmdPre != undefined && data.cmdPre.indexOf(value) !== -1) {
          return true;
        }
        return false;
    },
    handleNodeClick(data) {
        if (this.editProto) {
          return;
        }

        this.clickNodeData = data;

        if (data.imports != undefined) {
          var dyImports = data.imports.split(";");
          var dyImportList = [];
          for (let index = 0; index < dyImports.length; index++) {
            const element = dyImports[index];
            dyImportList.push({fileName: element});
          }
          this.clickNodeData.dynamicImport = dyImportList;
        } else if (data.structName != undefined) {
          if (data.structFieldList != undefined && data.structFieldList.length > 0) {
            var arr = data.structFieldList.filter(item => item.fieldName != undefined && item.fieldName != "");
            data.structFieldList = arr;
          }
          if (data.resp != undefined && data.resp.structFieldList != undefined && data.resp.structFieldList.length > 0) {
            var arr = data.resp.structFieldList.filter(item => item.fieldName != undefined && item.fieldName != "");
            data.resp.structFieldList = arr;
          }

          var dataFind = {
            fileIndex : data.fileIndex,
            fileVersion: this.fileVersion,
            id: data.id,
            structName: data.structName,
            cmd: data.cmd,
          };
          getProtoFileAllStruct(dataFind).then(resp => {
            this.protoAllFileField = resp.rows;
          });

        }

        this.showView = true;
        this.dialogVisible = false;
        this.showLogView = false;
    },
    /**
     * 懒加载事件
     */
    loadNode(node, resolve) {
      if (this.filterText != '' && this.node_had != undefined) {
          this.node_had.loaded = false;
        }
      this.node_had = node;
      if (this.resolve_had == undefined) {
        this.resolve_had = resolve;
      }

      if (node.level == 0) {
        listAllProto(this.fileVersion).then(resp => {
          this.importOptions = resp.rows;
          this.nodeMaxId = Math.max.apply(Math, resp.rows.map(item => item.id));
          return resolve(resp.rows);
        });
      } else if (node.level == 1) {
        var data = {
          fileIndex : node.data.id,
          fileVersion: this.fileVersion,
        };

        listProtoFileStruct(data).then(resp => {
          var treeData = resp.rows;
          for (let index = 0; index < treeData.length; index++) {
            const element = treeData[index];
            element.leaf = true;
          }
          return resolve(treeData);
        });
      } else {
        return resolve();
      }

    },

    getAllVersions() {
      listProtoVersionInfo().then(resp => {
        this.allVersionArr = resp.rows;
        console.log(this.allVersionArr)
      });
    },

    // 添加传当前节点，编辑传父节点
    refresh() {
      if (this.openNode != undefined) {
        // this.openNode.loadData()
        var node = this.openNode;
        var resolve = this.resolve_had;
        while (node != undefined && node.level != 0) {
          node = node.parent;
        }
        if (node != undefined) {
          node.childNodes = []
          this.loadNode(node, resolve)
          // listAllProto(this.fileVersion).then(resp => {
          //   this.importOptions = resp.rows;
          //   this.loadNode(node, resolve)
          // });

        }
      }
      if (this.node_had != undefined) {
        var node = this.node_had;
        var resolve = this.resolve_had;
        while (node != undefined && node.level != 0) {
          node = node.parent;
        }
        if (node != undefined) {
          node.childNodes = []
          this.loadNode(node, resolve)
          // listAllProto(this.fileVersion).then(resp => {
          //   this.importOptions = resp.rows;

          // });

        }
      }

      // let node = this.$refs.tree.getNode(this.id);
      // node.loaded = false;
      // node.loadData()
      //node.expand();
    },
    open(node, data, type) {
      this.selfDefineType = [];
      this.id = node.id;
      this.openNode = node;
      // 添加
      if (type == 1) {
        this.restemp();
        this.addProtoFile = false;
        var data = {
          id : data.id,
        };
        this.temp.fileIndex = data.id;
        listProtoStruct(data).then(resp => {
          var respData = resp.rows;
          var len = respData.length;
          for (let index = 0; index < len; index++) {
            const element = respData[index];
            this.selfDefineType.push({valueType: element});
          }
        });
      } else {

        // 编辑

        if (node.level == 1) {
          this.restemp();
          const dynamicImportTemp = this.temp.dynamicImport;
          // this.temp = data;
          this.temp.cmdPre = data.cmdPre;
          this.temp.fileDesc = data.fileDesc;
          this.temp.dynamicImport = dynamicImportTemp;
          this.temp.fileName = data.fileName;
          this.temp.fileVersion = data.fileVersion;
          this.temp.id = data.id;
          // this.temp.dynamicImport = dyImportList;
          if (this.temp.dynamicImport == undefined || this.temp.dynamicImport.length == 0) {
            this.temp.dynamicImport.push({id: "", fileName: ""});
          }
          if (data.imports.length > 0) {
            var importArr = data.imports.split(";");
            this.temp.dynamicImport.splice(0, this.temp.dynamicImport.length);
            for (let index = 0; index < importArr.length; index++) {
              var str = importArr[index];
              var ss = str.split("/");
              const item = this.importOptions.find(item => item.fileName == ss[1]);
              if (item != undefined) {
                this.temp.dynamicImport.push(item);
              }
            }
          }
          this.addProtoFile = true;
        } else {
          this.temp = data;
          var data = {
            id : data.fileIndex,
          };
          listProtoStruct(data).then(resp => {
            var respData = resp.rows;
            var len = respData.length;
            for (let index = 0; index < len; index++) {
              const element = respData[index];
              this.selfDefineType.push({valueType: element})
            }
          });

          this.addProtoFile = false;

          if (this.temp.cmd == -1) {
            this.temp.messageType = "3";
          } else if (this.temp.cmd == 0) {
            this.temp.messageType = "1";
          } else if (this.temp.resp != undefined) {
            this.temp.messageType = "2";
          } else {
            this.temp.messageType = "4";
            this.temp.resp = undefined;
          }

          if (this.temp.structFieldList == undefined || this.temp.structFieldList.length == 0) {
            if (this.temp.structFieldList == undefined) {
              this.temp.structFieldList = [];
            }
            const structData =
                {
                  fieldTypeDesc: "optional",
                  fieldType: "",
                  fieldName: "",
                  fieldDesc: "",
                  fieldValue: "",
                  id: "",
                };
              this.temp.structFieldList.push(structData);
          }
          if (this.temp.resp != undefined && (this.temp.resp.structFieldList == undefined || this.temp.resp.structFieldList.length == 0) && this.temp.messageType == "2") {
            if (this.temp.resp.structFieldList == undefined) {
              this.temp.resp.structFieldList = [];
            }
            const structData =
                {
                  fieldTypeDesc: "optional",
                  fieldType: "",
                  fieldName: "",
                  fieldDesc: "",
                  fieldValue: "",
                };
              this.temp.resp.structFieldList.push(structData);
          }
        }
      }


      this.editProto = true;
      this.showView = false;
      this.dialogVisible = true;
      this.showLogView = false;
    },
    // 提交数据
    submit() {
      this.$refs["form"].validate((valid) => {
        if (valid) {
          if (this.addProtoFile) {
            if (this.temp.dynamicImport != undefined) {
              const len = this.temp.dynamicImport.length;
              var imports = "";

              for (let index = 0; index < len; index++) {
                const element = this.temp.dynamicImport[index];
                if (imports.includes(element)) {
                  this.$message.error("重复import文件");
                  return;
                }
                if (imports != "") {
                  imports += ";"
                }
                imports += "proto/" + element.fileName;
              }
              this.temp.imports = imports;
            }
            if (this.temp.id == undefined) {
              const item = this.importOptions.find(item => item.fileName == this.temp.fileName);
              if (item != undefined) {
                this.$message.error("文件名已经存在");
                return;
              }
              if (this.nodeMaxId % 10000 >= 3000) {
                this.$message.error("文件个数大于阈值，找管理员处理");
                return;
              }

              this.nodeMaxId++;
              this.temp.id = this.nodeMaxId;
              this.temp.fileVersion = this.fileVersion;
              saveProtoFileDesc(this.temp).then(resp => {
                if (resp.code == 200) {
                  this.$message.success("保存成功");
                  this.clickNodeData = this.temp;
                  this.showView = true;
                  this.dialogVisible = false;
                  this.addProtoFile = false;
                  this.editProto = false;
                  // this.getAllProto();

                  let node = this.node_had;
                  while (node != undefined && node.level != 0) {
                    node = node.parent;
                  }
                  if (node != undefined) {
                    node.loadData();
                  }
                }
              });
            } else {
              updateProtoFileDesc(this.temp).then(resp => {
                if (resp.code == 200) {
                  this.$message.success("更新成功");
                  this.clickNodeData = this.temp;
                  this.showView = true;
                  this.dialogVisible = false;
                  this.addProtoFile = false;
                  this.editProto = false;
                  // this.getAllProto();
                  let node = this.openNode;
                  if (node != undefined) {
                    node.loadData();
                    if (node.parent != undefined) {
                      node.parent.loadData();
                    }
                  }
                }
              });
            }
          } else {

            if (this.temp.messageType == 1) {
              let repArr = [];
              for (let index = 0; index < this.temp.structFieldList.length; index++) {
                const element = this.temp.structFieldList[index];
                if (element != undefined) {
                  if (repArr.find(v => v == element.fieldValue)) {
                    this.$message.error("值：" + element.fieldValue + " 重复了");
                    return;
                  }
                  repArr.push(element.fieldValue);
                }

              }
              this.temp.protoType = "enum";
              this.temp.cmd = "0";

            } else {
              this.temp.protoType = "message";
              if (this.temp.messageType == 3) {
                this.temp.cmd = "-1";
              } else if (this.temp.messageType == 2) {
                this.temp.resp.protoType = "message";
                this.temp.resp.fileIndex = this.temp.fileIndex;
              }
            }
            if (this.temp.id == undefined) {
              this.temp.fileVersion = this.fileVersion;
              saveProtoFileStruct(this.temp).then(resp => {
                if (resp.code == 200) {
                  this.$message.success("新消息保存成功");
                  this.clickNodeData = this.temp;
                  this.showView = true;
                  this.dialogVisible = false;
                  this.addProtoFile = false;
                  this.editProto = false;
                  let node = this.openNode;
                  if (node != undefined) {
                    node.loaded = false;
                    node.expanded = true;
                    node.loadData();
                  }
                }
              }).catch(() => {});
            } else {
              this.temp.fileVersion = this.fileVersion
              updateProtoFileStruct(this.temp).then(resp => {
                if (resp.code == 200) {
                  // this.refresh();
                  this.$message.success("字段更新成功");
                  this.clickNodeData = this.temp;
                  this.showView = true;
                  this.dialogVisible = false;
                  this.addProtoFile = false;
                  this.editProto = false;
                  let node = this.openNode;
                  if (node != undefined) {
                    node.loaded = false;
                    node.expanded = true;
                    node.loadData();
                  }
                }
              });
            }
          }

        } else {
          console.log("error submit!!");
          return false;
        }
      });
    },

    cancel() {
      this.restemp();
      this.editProto = false;
      this.showView = false;
      this.dialogVisible = false;
      this.addProtoFile = false;
      this.showLogView = false;
      this.clickNodeData = undefined;
    },

    // 提交数据
    submitTree() {
      this.editProto = false;
      this.showView = false;
      this.dialogVisible = false;
      this.addProtoFile = false;
      this.showLogView = true;
      this.logTipsText = "正在生成提交中...";
      this.genLogValue = "";
      genProtoFile(this.fileVersion).then(res => {
        this.$message.success("操作成功");
        this.logTipsText = "执行完成..."
        this.genLogValue = res.msg;

      }).catch(() => {
        this.logTipsText = "";
      });
    },

    addProto() {
      // 添加
      this.restemp();
      this.dialogVisible = true;
      this.addProtoFile = true;
      this.showView = false;
      this.showLogView = false;
    },

    remove(node, data) {

      if (node.childNodes.length > 0) {
        this.$message.error("请从最后一级删除");
        return false;
      } else {
        if (data.fileName != undefined) {
          data.fileVersion = this.fileVersion
          this.$modal.confirm('是否确认删除文件["' + data.fileName + '"]的节点？').then(function() {
            return deleteProtocolFile(data);
          }).then(() => {
            this.$modal.msgSuccess("删除成功");
            this.cancel();
            // this.refresh();
            node.loaded = false;
            node.expanded = false;
            if (node.parent != undefined) {
              const index = node.parent.childNodes.findIndex(item => item.id == node.id);
              node.parent.childNodes.splice(index, 1);
            }
            this.refresh();
          }).catch(() => {});

        } else {
          data.fileVersion = this.fileVersion
          this.$modal.confirm('是否确认删除["' + data.structName + '"]的节点？').then(function() {
            return deleteProtocolFileStruct(data);
          }).then(() => {
            this.$modal.msgSuccess("删除成功");
            this.cancel();
            // this.refresh();
            if (node.parent != undefined) {
              node.parent.loaded = false;
              node.parent.expanded = false;
              const index = node.parent.childNodes.findIndex(item => item.id == node.id);
              node.parent.childNodes.splice(index, 1);
              node.parent.loadData();
              node.parent.expanded = true;
            }
            node.loadData();
          }).catch(() => {});
        }
      }
    },
    restemp() {
      this.temp = {
        fileName: undefined,
        fileDesc: undefined,
        cmdPre: undefined,
        cmd: undefined,
        structName: undefined,
        structDesc: undefined,
        // id: undefined,
        // parentId: undefined, // 父id
        // mainProjectId: undefined, //项目id
        // name: undefined, // 名称
        // code: undefined, //编码
        // project: undefined,
        resp: {
          cmd: undefined,
          structName: undefined,
          structDesc: undefined,
          structFieldList: [
            {
              fieldTypeDesc: "",
              fieldType: "",
              fieldName: "",
              fieldDesc: "",
              fieldValue: "",
            }
          ],
        },
        // id: undefined,
        // parentId: undefined, // 父id
        // mainProjectId: undefined, //项目id
        // name: undefined, // 名称
        messageType: "1",
        // code: undefined, //编码

        structFieldList: [
          {
            fieldTypeDesc: "",
            fieldType: "",
            fieldName: "",
            fieldDesc: "",
            fieldValue: "",
          }
        ],
        dynamicImport : [
          {
            id: "",
            fileName: "",
          }
        ],
      };
    },

    handleAddImport() {
      this.temp.dynamicImport.push({
        id: "",
        fileName: "",
      });
    },

    handleDelImport(item, index) {
      if (this.temp.dynamicImport.length <= 1) {
        return false;
      }
      this.temp.dynamicImport.splice(index, 1);
    },

    handleAddField() {
      this.temp.structFieldList.push({
        fieldTypeDesc: "",
        fieldType: "",
        fieldName: "",
        fieldDesc: "",
        fieldValue: "",
      });
    },

    handleDelField(item, index) {
      this.temp.structFieldList.splice(index, 1);
    },

    handleAddRespField() {
      this.temp.resp.structFieldList.push({
        fieldTypeDesc: "",
        fieldType: "",
        fieldName: "",
        fieldDesc: "",
        fieldValue: "",
      });
    },

    handleDelRespField(item, index) {
      this.temp.resp.structFieldList.splice(index, 1);
    },

    handleAddEnum() {
      this.temp.structFieldList.push({
        enumOptName: "",
        enumOptValue: "",
      });
    },

    handleDelEnum(item, index) {
      this.temp.structFieldList.splice(index, 1);
    },

    getGenLog() {
      return this.genLogValue;
    },

    visibleChange(e) {
      if (!e) {
        this.restemp();
        this.refresh();
      }
    },

    getFieldItemByName(fieldName) {
      if (this.protoAllFileField == undefined) {
        return fieldName;
      }
      if (fieldName == undefined) {
        return fieldName;
      }

      var fieldList = [];
      for (let index = 0; index < this.protoAllFileField.length; index++) {
          const element = this.protoAllFileField[index];
          if (element.structName == fieldName) {
            fieldList.push(element);
          }
      }

      var content = "<span style=\"color:red\">字段类型: </span><span style=\"color:blue\">" + fieldName + "</span>\r\n";
      if (fieldList.length > 0) {
        for (let index = 0; index < this.protoAllFileField.length; index++) {
          const element = this.protoAllFileField[index];
          if (element.structName == fieldName) {
            content += "<span style=\"color:red\">类型描述：</span><span style=\"color:blue\">" +
            element.fieldTypeDesc + "</span>       <span style=\"color:red\">字段类型：</span></span><span style=\"color:blue\">" +
            element.fieldType + "</span>         <span style=\"color:red\">字段名：</span></span><span style=\"color:blue\">" +
            element.fieldName + "</span>   <span style=\"color:red\">字段释义：</span></span><span style=\"color:blue\">" +
            element.fieldDesc + "</span>\r\n";
          }
        }
      }

      return content;
    },
  },
};
</script>

<style lang="scss">
.custom-tree-node {
  flex: 1;
  display: flex;
  align-items: center;
  justify-content: space-between;
  font-size: 14px;
  padding-right: 8px;

  // width: 100%;
  // overflow: hidden;
  // white-space: nowrap;
  // text-overflow: ellipsis;
}

/* 改变节点高度 */
.el-tree-node__content {
  height: 36px !important;
}
.el-tree>.el-tree-node{
  min-width: 100%;
  display: inline-block;
}
.el-main {
  padding: 1%;
  height: calc(100vh - 290px);
  margin-left: 20px;
  margin-right: 20px;
  line-height: 20px;
}

/* 设置树形最外层的背景颜色和字体颜色 */
.el-tree{
  color: #000;// 树内字体颜色
  background-color: transparent; // 内外背景一致
}
/* 设置三角形图标的颜色 */
.el-tree-node__expand-icon {
  color: #c78676;
  &.is-leaf{
    color: transparent;
  }
}
/* 设置节点鼠标悬浮三角图标鼠标悬浮的颜色 */
.el-tree-node__content:hover .el-tree-node__expand-icon {
  color: #000;
}
/* 树节点鼠标悬浮式改变背景色，字体颜色 */
.el-tree-node__content:hover {
  background-color: #3274e6;
  color: #fff;
}
/** 鼠标悬浮颜色 */
.el-tree .el-tree-node__content{
  &:hover{
    background-color: rgb(181, 176, 228) ;
  }
}
/** 鼠标选中节点颜色 */
.el-tree .el-tree-node.is-current > .el-tree-node__content {
    background-color: rgb(181, 176, 228) !important;
}

/* 改变被点击节点背景颜色，字体颜色 */
.el-tree-node:focus > .el-tree-node__content {
  background-color: rgb(181, 176, 228) ;
  color: #fff;
}

.el-row {
    margin-bottom: 5px;
    &:last-child {
      margin-bottom: 0;
    }
}


</style>
