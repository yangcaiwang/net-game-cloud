<template>
  <div class="app-container">
    <el-form :model="queryParams" ref="queryForm" size="small" :inline="true" v-show="showSearch">

      <el-form-item label="平台" prop="platformId">
        <el-select
          v-model="queryParams.platformId"
          placeholder="选择平台"
          style="width: 240px"
        >
        <el-option
          v-for="item in platformOptions"
          :key="item.platformId"
          :label="item.platformId + '_' +item.platformName"
          :value="item.platformId"
        />
        </el-select>
      </el-form-item>
      <el-form-item label="服务器编号" prop="serverId">
        <el-input
          v-model="queryParams.serverId"
          placeholder="请输入服务器编号"
          clearable
          style="width: 240px"
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item>
        <el-button type="primary" icon="el-icon-search" size="mini" @click="handleQuery">搜索</el-button>
        <el-button icon="el-icon-refresh" size="mini" @click="resetQuery">重置</el-button>
      </el-form-item>
    </el-form>

    <el-row :gutter="10" class="mb8">
      <el-col :span="1.5">
        <el-button
          type="primary"
          plain
          icon="el-icon-plus"
          size="mini"
          @click="handleAdd"
          v-hasPermi="['gm:server:add']"
        >新增</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
          type="success"
          plain
          icon="el-icon-edit"
          size="mini"
          :disabled="single"
          @click="handleUpdate"
          v-hasPermi="['gm:server:edit']"
        >修改</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
          type="danger"
          plain
          icon="el-icon-delete"
          size="mini"
          :disabled="multiple"
          @click="handleDelete"
          v-hasPermi="['gm:server:remove']"
        >删除</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
          type="warning"
          plain
          icon="el-icon-download"
          size="mini"
          @click="handleExport"
          v-hasPermi="['gm:server:export']"
        >导出</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
          type="danger"
          plain
          icon="el-icon-edit"
          size="mini"
          @click="handleStopServer"
          v-hasPermi="['gm:server:edit']"
        >停服</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
          type="success"
          plain
          icon="el-icon-edit"
          size="mini"
          @click="handleStartServer"
          v-hasPermi="['gm:server:edit']"
        >启服</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
          type="danger"
          plain
          icon="el-icon-edit"
          size="mini"
          @click="handleBatchMaintain"
          v-hasPermi="['gm:server:edit']"
        >批量维护</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
          type="success"
          plain
          icon="el-icon-edit"
          size="mini"
          @click="handleBatchOpen"
          v-hasPermi="['gm:server:edit']"
        >批量开启</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
          type="danger"
          plain
          icon="el-icon-edit"
          size="mini"
          @click="handleKitoutAll"
          v-hasPermi="['gm:server:kitout']"
        >全部下线</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
          type="danger"
          plain
          icon="el-icon-edit"
          size="mini"
          @click="handleSyncConfig"
          v-hasPermi="['gm:server:edit']"
        >批量同步配置</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
          type="danger"
          plain
          icon="el-icon-edit"
          size="mini"
          @click="deployServer"
          v-hasPermi="['gm:server:edit']"
        >批量部署</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
          type="info"
          plain
          icon="el-icon-upload2"
          size="mini"
          @click="handleImport"
          v-hasPermi="['gm:server:import']"
        >导入</el-button>
      </el-col>
      <right-toolbar :showSearch.sync="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <el-table v-loading="loading" :data="roleList" @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="55" align="center" />
      <el-table-column label="服务器标识" prop="serverKeyId" :show-overflow-tooltip="true" width="90" />
      <el-table-column label="服务器编号" prop="serverId" align="center" width="90" />
      <el-table-column label="服务器名" key="serverName" prop="serverName" align="center" />
      <el-table-column label="平台" key="platformName" prop="platform.platformName" :show-overflow-tooltip="true" width="90" />
      <el-table-column label="外网地址" prop="outHost" center />
      <el-table-column label="内外地址" prop="inHost" center />
      <el-table-column label="客户端端口" prop="clientPort" width="90" />
      <el-table-column label="游戏服端口" prop="inPort" width="90" />
      <el-table-column label="开服时间" align="center" prop="openTime" width="150">
        <template slot-scope="scope">
          <span>{{ parseTime(scope.row.openTime) }}</span>
        </template>
      </el-table-column>
      <el-table-column label="服务器状态" align="center" width="100">
        <template slot-scope="scope">
          <dict-tag :options="dict.type.game_server_type" :value="scope.row.serverStatus"/>
        </template>
      </el-table-column>
      <el-table-column label="运行状态" align="center" width="100">
        <template slot-scope="scope">
          <el-switch
            v-model="scope.row.runStatus"
            active-value="1"
            inactive-value="0"
            disabled
          ></el-switch>
        </template>
      </el-table-column>
      <el-table-column label="对外显示" align="center" width="100">
        <template slot-scope="scope">
          <el-switch
            v-model="scope.row.showOut"
            active-value="1"
            inactive-value="0"
            @change="handleShowOutStatusChange(scope.row)"
          ></el-switch>
        </template>
      </el-table-column>
      <el-table-column label="定时开服" align="center" width="100">
        <template slot-scope="scope">
          <el-switch
            v-model="scope.row.timeOpen"
            active-value="1"
            inactive-value="0"
            @change="changeServerTimeOpen(scope.row)"
          ></el-switch>
        </template>
      </el-table-column>
      <el-table-column label="关闭注册" align="center" width="100">
        <template slot-scope="scope">
          <el-switch
            v-model="scope.row.registerSwitch"
            :active-value=1
            :inactive-value=0
            @change="changeServerRegister(scope.row)"
          ></el-switch>
        </template>
      </el-table-column>
      <el-table-column label="创建时间" align="center" prop="createTime" width="90">
        <template slot-scope="scope">
          <span>{{ parseTime(scope.row.createTime) }}</span>
        </template>
      </el-table-column>
      <el-table-column label="操作" align="center" class-name="small-padding fixed-width" width="240">
        <template slot-scope="scope" v-if="scope.row.roleId !== 1">
          <el-button
            size="mini"
            type="text"
            icon="el-icon-edit"
            @click="handleUpdate(scope.row)"
            v-hasPermi="['gm:server:edit']"
          >修改</el-button>
          <el-button
            size="mini"
            type="text"
            icon="el-icon-delete"
            @click="handleDelete(scope.row)"
            v-hasPermi="['gm:server:remove']"
          >删除</el-button>
          <el-button
            size="mini"
            type="text"
            icon="el-icon-refresh"
            @click="handleKitoutAll(scope.row)"
            v-hasPermi="['gm:server:kitout']"
          >踢下线</el-button>
          <el-button
            size="mini"
            type="text"
            icon="el-icon-edit"
            @click="handleStopServer(scope.row)"
            v-hasPermi="['gm:server:edit']"
          >停服</el-button>
          <el-button
            size="mini"
            type="text"
            icon="el-icon-edit"
            @click="handleStartServer(scope.row)"
            v-hasPermi="['gm:server:edit']"
          >启服</el-button>
          <el-button
            size="mini"
            type="text"
            icon="el-icon-edit"
            @click="handleSyncConfig(scope.row)"
            v-hasPermi="['gm:server:edit']"
          >同步配置</el-button>
          <el-button
            size="mini"
            type="text"
            icon="el-icon-edit"
            @click="deployServer(scope.row)"
            v-hasPermi="['gm:server:edit']"
          >部署</el-button>
        </template>
      </el-table-column>
    </el-table>

    <pagination
      v-show="total>0"
      :total="total"
      :page.sync="queryParams.pageNum"
      :limit.sync="queryParams.pageSize"
      @pagination="getList"
    />

    <!-- 添加或修改服务器配置对话框 -->
    <el-dialog :title="title" :visible.sync="open" width="500px" append-to-body>
      <el-form ref="form" :model="form" :rules="rules" label-width="100px">
        <el-form-item label="服务器ID" prop="serverId">
          <el-input v-model="form.serverId" placeholder="请输入服务器ID" />
        </el-form-item>
        <el-form-item label="服务器名" prop="serverName">
          <el-input v-model="form.serverName" placeholder="请输入服务器名" />
        </el-form-item>
        <el-form-item label="平台">
          <el-select v-model="form.platformId" clearable placeholder="请选择平台">
            <el-option
              v-for="item in platformOptions"
              :key="item.id"
              :label="item.platformId + '_' +item.platformName"
              :value="item.platformId"
            ></el-option>
          </el-select>
        </el-form-item>

        <el-form-item label="外网地址" prop="outHost">
          <el-input v-model="form.outHost" placeholder="请输入外网地址" />
        </el-form-item>
        <el-form-item label="内网地址" prop="inHost">
          <el-input v-model="form.inHost" placeholder="请输入内网地址" />
        </el-form-item>
        <el-form-item label="客户端端口" prop="clientPort">
          <el-input v-model="form.clientPort" placeholder="请输入客户端端口" />
        </el-form-item>
        <el-form-item label="内网端口" prop="inPort">
          <el-input v-model="form.inPort" placeholder="请输入内网端口" />
        </el-form-item>
        <el-form-item label="游戏数据连接" prop="dbUrl">
          <el-input v-model="form.dbUrl" type="textarea" placeholder="请输入游戏服url" :autosize="{ minRows: 4, maxRows: 4}" />
        </el-form-item>
        <el-form-item label="游戏日志连接" prop="dbLogUrl">
          <el-input v-model="form.dbLogUrl" type="textarea" placeholder="请输入游戏日志url" :autosize="{ minRows: 4, maxRows: 4}" />
        </el-form-item>
        <el-form-item label="数据库用户" prop="dbUser">
          <el-input v-model="form.dbUser" placeholder="请输入数据库用户" />
        </el-form-item>
        <el-form-item label="数据库密码" prop="dbPass">
          <el-input v-model="form.dbPass" placeholder="请输入数据库密码" />
        </el-form-item>
        <el-form-item label="服务器顺序" prop="sort">
          <el-input-number v-model="form.sort" controls-position="right" :min="0" />
        </el-form-item>
        <el-form-item label="服务器状态">
          <el-select v-model="form.serverStatus" clearable placeholder="请选择服务器状态">
            <el-option
              v-for="item in dict.type.game_server_type"
                  :key="item.value"
                  :label="item.label"
                  :value="item.value"
              :disabled="item.status == 1"
            ></el-option>
          </el-select>
        </el-form-item>

        <el-form-item label="路径" prop="homePath">
          <el-input v-model="form.homePath" placeholder="请输入服务路径" />
        </el-form-item>

        <el-form-item label="开服时间">
            <el-date-picker
              v-model="form.openTime"
              style="width: 240px"
              value-format="yyyy-MM-dd HH:mm:ss"
              type="datetime"
            ></el-date-picker>
          </el-form-item>

      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button type="primary" @click="submitForm">确 定</el-button>
        <el-button @click="cancel">取 消</el-button>
      </div>
    </el-dialog>

    <!-- 服务器导入对话框 -->
    <el-dialog :title="upload.title" :visible.sync="upload.open" width="400px" append-to-body>
      <el-upload
        ref="upload"
        :limit="1"
        accept=".xlsx, .xls"
        :headers="upload.headers"
        :action="upload.url + '?updateSupport=' + upload.updateSupport"
        :disabled="upload.isUploading"
        :on-progress="handleFileUploadProgress"
        :on-success="handleFileSuccess"
        :auto-upload="false"
        drag
      >
        <i class="el-icon-upload"></i>
        <div class="el-upload__text">将文件拖到此处，或<em>点击上传</em></div>
        <div class="el-upload__tip text-center" slot="tip">
          <div class="el-upload__tip" slot="tip">
            <el-checkbox v-model="upload.updateSupport" /> 是否更新已经存在的服务器数据
          </div>
          <span>仅允许导入xls、xlsx格式文件。</span>
          <el-link type="primary" :underline="false" style="font-size:12px;vertical-align: baseline;" @click="importTemplate">下载模板</el-link>
        </div>
      </el-upload>
      <div slot="footer" class="dialog-footer">
        <el-button type="primary" @click="submitFileForm">确 定</el-button>
        <el-button @click="upload.open = false">取 消</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import {
  addServer,
  changeServerConfig,
  changeServerRegister,
  changeServerShowOutStatus,
  changeServerStatus,
  changeServerTimeOpen,
  dataScope,
  delServer,
  deployServer,
  getServer,
  kitoutAll,
  listServer,
  startServer,
  stopServer,
  updateServer
} from "@/api/gameGm/server";
// import { treeselect as menuTreeselect, roleMenuTreeselect } from "@/api/system/menu";
// import { treeselect as deptTreeselect, roleDeptTreeselect } from "@/api/system/dept";
import {listPlatformAll} from "@/api/gameGm/platform";
import {getToken} from "@/utils/auth";


export default {
  name: "Player",
  dicts: ['sys_normal_disable','game_server_type'],
  data() {
    return {
      // 遮罩层
      loading: true,
      // 选中数组
      ids: [],
      // 非单个禁用
      single: true,
      // 非多个禁用
      multiple: true,
      // 显示搜索条件
      showSearch: true,
      // 总条数
      total: 0,
      // 角色表格数据
      roleList: [],
      // 弹出层标题
      title: "",
      // 是否显示弹出层
      open: false,
      // 是否显示弹出层（数据权限）
      openDataScope: false,
      // menuExpand: false,
      // menuNodeAll: false,
      // deptExpand: true,
      // deptNodeAll: false,
      // 日期范围
      dateRange: [],
      // 菜单列表
      // menuOptions: [],
      // // 部门列表
      // deptOptions: [],
      // 查询参数
      queryParams: {
        pageNum: 1,
        pageSize: 20,
        serverId: undefined,
      },
      // 表单参数
      form: {},
      defaultProps: {
        children: "children",
        label: "label"
      },
      platformOptions: [],
      serverStatusOptions: [],
      platformName: undefined,
      changePlatformId: undefined,
      // 列信息
      columns: [
        { key: 0, label: `服务器标识`, visible: true },
        { key: 1, label: `服务器编号`, visible: true },
        { key: 2, label: `服务器名`, visible: true },
        { key: 3, label: `平台`, visible: true },
      ],
      // 表单校验
      rules: {
        serverId: [
          { required: true, message: "服务器ID不能为空", trigger: "blur" },
          {pattern: /^[1-9][0-9]{3}$/, message: '服务器ID需长度为4的数字', trigger: 'blur'}
        ],
        serverName: [
          { required: true, message: "服务器名不能为空", trigger: "blur" }
        ],
        platformId: [
          { required: true, message: "平台不能为空", trigger: "blur" }
        ],
        outHost: [
          { required: true, message: "外网地址不能为空", trigger: "blur" }
        ],
        inHost: [
          { required: true, message: "内网地址不能为空", trigger: "blur" }
        ],
        clientPort: [
          { required: true, message: "客户端端口不能为空", trigger: "blur" },
          {pattern: /^[0-9]*$/, message: '客户端端口需为数字', trigger: 'blur'}
        ],
        inPort: [
          { required: true, message: "服务端口不能为空", trigger: "blur" },
          {pattern: /^[0-9]*$/, message: '服务端口需为数字', trigger: 'blur'}
        ],
        sort: [
          { required: true, message: "排序不能为空", trigger: "blur" },
          {pattern: /^[0-9]*$/, message: '排序需为数字', trigger: 'blur'}
        ]
      },

      // 服务器导入参数
      upload: {
        // 是否显示弹出层（服务器导入）
        open: false,
        // 弹出层标题（服务器导入）
        title: "",
        // 是否禁用上传
        isUploading: false,
        // 是否更新已经存在的服务器数据
        updateSupport: 0,
        // 设置上传的请求头部
        headers: { Authorization: "Bearer " + getToken() },
        // 上传的地址
        url: process.env.VUE_APP_BASE_API + "/gameGm/server/importData"
      },
    };
  },
  watch: {
    // 根据名称筛选树
    platformName(val) {
      this.$refs.list.filter(val);
    },
    queryParams: {
      handler:function(newVal, oldVal) {
        let pid = this.changePlatformId;
        this.changePlatformId = newVal.platformId;
        if (pid !== this.changePlatformId) {
          this.getList();
        }
      },
      deep: true,
      immediate: true
    }
  },
  created() {
    this.getPlatformData();
    this.getList();
  },
  methods: {
    /** 查询列表 */
    getList() {
      this.loading = true;
      listServer(this.addDateRange(this.queryParams, this.dateRange)).then(response => {
          this.roleList = response.rows;
          this.total = response.total;
          this.loading = false;
        }
      );
    },

    getPlatformData() {
      listPlatformAll().then(response => {
        this.platformOptions = response.rows;
      });
    },

        // 取消按钮
    cancel() {
      this.open = false;
      this.reset();
    },

    // 表单重置
    reset() {
      if (this.$refs.menu != undefined) {
        this.$refs.menu.setCheckedKeys([]);
      }
      // this.menuExpand = false,
      // this.menuNodeAll = false,
      // this.deptExpand = true,
      // this.deptNodeAll = false,
      this.form = {
        serverKeyId: undefined,
        serverId: undefined,
        serverName: undefined,
        platformId: undefined,
        sort: 0,
        serverStatus: "0",
        remark: undefined,
        serverStatus: undefined
      };
      this.resetForm("form");
    },
    /** 搜索按钮操作 */
    handleQuery() {
      this.queryParams.pageNum = 1;
      this.getList();
    },
    /** 重置按钮操作 */
    resetQuery() {
      this.dateRange = [];
      this.resetForm("queryForm");
      this.handleQuery();
    },
    // 多选框选中数据
    handleSelectionChange(selection) {
      this.ids = selection.map(item => item.serverKeyId)
      this.single = selection.length!=1
      this.multiple = !selection.length
    },

     /** 新增按钮操作 */
    handleAdd() {
      this.reset();
      this.open = true;
      this.title = "添加服务器";
    },

    // 服务器开启状态修改
    handleShowOutStatusChange(row) {
      let text = row.showOut === "1" ? "开启" : "关闭";
      this.$modal.confirm('确认要"' + text + '""' + row.serverName + '"吗？').then(function() {
        return changeServerShowOutStatus(row.serverKeyId, row.showOut);
      }).then(() => {
        this.$modal.msgSuccess(text + "成功");
      }).catch(function() {
        row.showOut = row.showOut === "1" ? "1" : "0";
      });
    },

    // 服务器批量维护状态修改
    handleBatchMaintain() {
      const serverStatus = 6;
      const platformId = this.queryParams.platformId;
      const data = {
        serverStatus,
        platformId
      };
      this.$modal.confirm('确认批量"'+ '维护' +'"服务器吗？').then(function() {
        return changeServerStatus(data);
      }).then(() => {
        this.$modal.msgSuccess("维护成功");
        this.getList();
      }).catch(function() {
      });
    },

    // 服务器开启状态修改
    handleBatchOpen() {
      const serverStatus = 2;
      const platformId = this.queryParams.platformId;
      const data = {
        serverStatus,
        platformId
      };
      this.$modal.confirm('确认批量"'+ '开启' +'"服务器吗？').then(function() {
        return changeServerStatus(data);
      }).then(() => {
        this.$modal.msgSuccess("开启成功");
        this.getList();
      }).catch(function() {
      });
    },


    // 服务器定时开服状态修改
    changeServerTimeOpen(row) {
      let text = row.timeOpen === "1" ? "开启" : "关闭";
      this.$modal.confirm('确认要"' + text + '""' + row.serverName + '"定时开服吗？').then(function() {
        return changeServerTimeOpen(row.serverKeyId, row.timeOpen);
      }).then(() => {
        this.$modal.msgSuccess(text + "成功");
      }).catch(function() {
        row.timeOpen = row.timeOpen === "1" ? "1" : "0";
      });
    },

    // 服务器注册状态修改
    changeServerRegister(row) {
      let text = row.timeOpen === 1 ? "关闭" : "开启";
      this.$modal.confirm('确认要"' + text + '""' + row.serverName + '"服务器注册吗？').then(function() {
        return changeServerRegister(row.serverKeyId, row.registerSwitch);
      }).then(() => {
        this.$modal.msgSuccess(text + "成功");
      }).catch(function() {
        row.registerSwitch = row.registerSwitch === 1 ? 0 : 1;
      });
    },

    /** 修改按钮操作 */
    handleUpdate(row) {
      this.reset();
      const roleId = row.serverKeyId || this.ids
      getServer(roleId).then(response => {
        this.form = response.data;
        this.open = true;
        // this.$nextTick(() => {
        //   roleMenu.then(res => {
        //     let checkedKeys = res.checkedKeys
        //     checkedKeys.forEach((v) => {
        //         this.$nextTick(()=>{
        //             this.$refs.menu.setChecked(v, true ,false);
        //         })
        //     })
        //   });
        // });
        this.title = "修改服务器";
      });
    },

    handleStartServer(row) {
      const serverIds = row.serverKeyId || this.ids;
      const platformId = this.queryParams.platformId;
      const data = {
        platformId
      };
      if (serverIds === undefined || serverIds == "") {
        this.$modal.confirm('确认批量启动服务器吗？').then(function() {
          return startServer("-1", data);
        }).then(() => {
          this.$modal.msgSuccess("启服成功");
          this.getList();
        }).catch(function() {
        });
      } else {
        this.$modal.confirm('确认启动' + serverIds +'"服务器吗？').then(function() {
            startServer(serverIds, data).then(response => {
              this.$modal.msgSuccess("启服成功");
            });
        }).then(() => {
          this.getList();
        }).catch(function() {
        });
      }
    },

    handleStopServer(row) {
      const serverIds = row.serverKeyId || this.ids
      const platformId = this.queryParams.platformId;
      const data = {
        platformId
      };
      if (serverIds === undefined || serverIds == "") {
        this.$modal.confirm('确认批量停止服务器吗？').then(function() {
          return stopServer("-1", data);
        }).then(() => {
          this.$modal.msgSuccess("停止成功");
          this.getList();
        }).catch(function() {
        });
      } else {
        this.$modal.confirm('确认停止' + serverIds +'"服务器吗？').then(function() {
          stopServer(serverIds, data).then(response => {
              this.$modal.msgSuccess("停服成功");
          });
        }).then(() => {
          this.getList();
        }).catch(function() {
        });
      }

    },

    handleSyncConfig(row) {
      const serverIds = row.serverKeyId || this.ids;
      if (serverIds === undefined || serverIds == "") {
        this.$modal.msgError("未选择服务器");
        return;
      }
      this.$modal.confirm('是否确认同步ID为"' + serverIds + '"的数据项？').then(function() {
        return changeServerConfig(serverIds);
      }).then(() => {
        this.getList();
        this.$modal.msgSuccess("操作成功");
      }).catch(() => {});
    },

    deployServer(row) {
      const serverIds = row.serverKeyId || this.ids;
      if (serverIds === undefined || serverIds == "") {
        this.$modal.msgError("未选择服务器");
        return;
      }
      this.$modal.confirm('是否确认部署服务器："' + serverIds + '"的数据项？').then(function() {
        return deployServer(serverIds);
      }).then(() => {
        this.getList();
        this.$modal.msgSuccess("操作成功");
      }).catch(() => {});
    },

     /** 提交按钮 */
    submitForm: function() {
      this.$refs["form"].validate(valid => {
        if (valid) {
          if (this.form.serverKeyId != undefined) {
            // this.form.menuIds = this.getMenuAllCheckedKeys();
            updateServer(this.form).then(response => {
              this.$modal.msgSuccess("修改成功");
              this.open = false;
              this.getList();
            });
          } else {
            // this.form.menuIds = this.getMenuAllCheckedKeys();
            addServer(this.form).then(response => {
              this.$modal.msgSuccess("新增成功");
              this.open = false;
              this.getList();
            });
          }
        }
      });
    },
    /** 提交按钮（数据权限） */
    submitDataScope: function() {
      if (this.form.roleId != undefined) {
        this.form.deptIds = this.getDeptAllCheckedKeys();
        dataScope(this.form).then(response => {
          this.$modal.msgSuccess("修改成功");
          this.openDataScope = false;
          this.getList();
        });
      }
    },

    /** 删除按钮操作 */
    handleDelete(row) {
      const roleIds = row.serverKeyId || this.ids;
      this.$modal.confirm('是否确认删除玩家ID为"' + roleIds + '"的数据项？').then(function() {
        return delServer(roleIds);
      }).then(() => {
        this.getList();
        this.$modal.msgSuccess("删除成功");
      }).catch(() => {});
    },

    /** 踢出按钮操作 */
    handleKitoutAll(row) {
      const serverIds = row.serverKeyId || this.ids;
      const platformId = this.queryParams.platformId;
      const data = {
        platformId
      };
      if (serverIds === undefined || serverIds == "") {
        this.$modal.confirm('是否确认踢出服务器的所有在线玩家？').then(function() {
          return kitoutAll("-1", data);
        }).then(() => {
          this.getList();
          this.$modal.msgSuccess("操作成功");
        }).catch(() => {});
      } else {
        this.$modal.confirm('是否确认踢出服务器ID为"' + serverIds + '"的所有玩家？').then(function() {
          return kitoutAll(serverIds, data);
        }).then(() => {
          this.getList();
          this.$modal.msgSuccess("操作成功");
        }).catch(() => {});
      }


    },

    itemList() {
      this.loading = true;
      // listRole(this.addDateRange(this.queryParams, this.dateRange)).then(response => {
      //     this.roleList = response.rows;
      //     this.total = response.total;
      //     this.loading = false;
      //   }
      // );
    },

    /** 导出按钮操作 */
    handleExport() {
      this.download('gameGm/server/export', {
        ...this.queryParams
      }, `server_${new Date().getTime()}.xlsx`)
    },

    /** 导入按钮操作 */
    handleImport() {
      this.upload.title = "服务器导入";
      this.upload.open = true;
    },
    /** 下载模板操作 */
    importTemplate() {
      this.download('gameGm/server/importTemplate', {
      }, `server_template_${new Date().getTime()}.xlsx`)
    },
    // 文件上传中处理
    handleFileUploadProgress(event, file, fileList) {
      this.upload.isUploading = true;
    },
    // 文件上传成功处理
    handleFileSuccess(response, file, fileList) {
      this.upload.open = false;
      this.upload.isUploading = false;
      this.$refs.upload.clearFiles();
      this.$alert("<div style='overflow: auto;overflow-x: hidden;max-height: 70vh;padding: 10px 20px 0;'>" + response.msg + "</div>", "导入结果", { dangerouslyUseHTMLString: true });
      this.getList();
    },
    // 提交上传文件
    submitFileForm() {
      this.$refs.upload.submit();
    }
  }
};
</script>
