<template>
  <div class="app-container">
    <el-form :model="queryParams" ref="queryForm" size="small" :inline="true" v-show="showSearch">
      <el-form-item label="服务器标识" prop="serverId">
        <el-input
          v-model="queryParams.serverId"
          placeholder="请输入服务器唯一id"
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
        >新增
        </el-button>
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
        >删除
        </el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
          type="danger"
          plain
          icon="el-icon-edit"
          size="mini"
          @click="handleStopServer"
          v-hasPermi="['gm:server:edit']"
        >停服
        </el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
          type="success"
          plain
          icon="el-icon-edit"
          size="mini"
          @click="handleStartServer"
          v-hasPermi="['gm:server:edit']"
        >启服
        </el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
          type="danger"
          plain
          icon="el-icon-edit"
          size="mini"
          @click="handleBatchMaintain"
          v-hasPermi="['gm:server:edit']"
        >批量维护
        </el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
          type="success"
          plain
          icon="el-icon-edit"
          size="mini"
          @click="handleBatchOpen"
          v-hasPermi="['gm:server:edit']"
        >批量开启
        </el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
          type="danger"
          plain
          icon="el-icon-edit"
          size="mini"
          @click="handleKitoutAll"
          v-hasPermi="['gm:server:kitout']"
        >全部下线
        </el-button>
      </el-col>
      <right-toolbar :showSearch.sync="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <el-table v-loading="loading" :data="serverTableList" @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="55" align="center"/>
      <el-table-column label="唯一标识" prop="serverId" align="center" width="100"/>
      <el-table-column label="名称" prop="serverName" :show-overflow-tooltip="true" width="50"/>
      <el-table-column label="类型" prop="serverType" align="center" width="100" :formatter="formatServerType"/>
      <el-table-column label="状态" prop="serverState" align="center" width="50" :formatter="formatServerState"/>
      <el-table-column label="地址" prop="serverAddr.address" align="center" width="150"/>
      <el-table-column label="组" prop="groupId" align="center" width="100"/>
      <el-table-column label="权重" prop="weight" align="center" width="100"/>
      <el-table-column label="Grpc客户端地址" align="center" width="150">
        <template slot-scope="scope">
          <el-popover
            placement="top-start"
            width="500"
            trigger="hover"
            :content="scope.row.grpcClientAddr">
            <el-button size="mini" slot="reference">详情</el-button>
          </el-popover>
        </template>
      </el-table-column>
      <el-table-column label="Grpc服务端地址" prop="grpcServerAddr.address" align="center" width="150"/>
      <el-table-column label="Jetty服务端地址" prop="jettyServerAddr.address" align="center" width="150"/>
      <el-table-column label="Netty服务端地址" prop="nettyServerAddr.address" align="center" width="150"/>
      <el-table-column label="开服时间" align="center" prop="openTime" width="160">
        <template slot-scope="scope">
          <span>{{ parseTime(scope.row.openTime) }}</span>
        </template>
      </el-table-column>
      <el-table-column label="注册时间" align="center" prop="registerTime" width="160">
        <template slot-scope="scope">
          <span>{{ parseTime(scope.row.registerTime) }}</span>
        </template>
      </el-table-column>
      <el-table-column label="操作" align="center" class-name="small-padding fixed-width" width="200">
        <template slot-scope="scope">
          <el-button
            size="mini"
            type="text"
            icon="el-icon-edit"
            @click="handleUpdate(scope.row)"
            v-hasPermi="['gm:server:edit']"
          >修改
          </el-button>
          <el-button
            size="mini"
            type="text"
            icon="el-icon-delete"
            @click="handleDelete(scope.row)"
            v-hasPermi="['gm:server:remove']"
          >删除
          </el-button>
          <el-button
            size="mini"
            type="text"
            icon="el-icon-refresh"
            @click="handleKitoutAll(scope.row)"
            v-hasPermi="['gm:server:kitout']"
          >踢下线
          </el-button>
          <el-button
            size="mini"
            type="text"
            icon="el-icon-edit"
            @click="handleStopServer(scope.row)"
            v-hasPermi="['gm:server:edit']"
          >停服
          </el-button>
          <el-button
            size="mini"
            type="text"
            icon="el-icon-edit"
            @click="handleStartServer(scope.row)"
            v-hasPermi="['gm:server:edit']"
          >启服
          </el-button>
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
          <!--          <el-input v-model="form.serverId" placeholder="请输入服务器ID"/>-->
        </el-form-item>
        <el-form-item label="服务器名" prop="serverName">
          <el-input v-model="form.serverName" placeholder="请输入服务器名"/>
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
          <el-input v-model="form.outHost" placeholder="请输入外网地址"/>
        </el-form-item>
        <el-form-item label="内网地址" prop="inHost">
          <el-input v-model="form.inHost" placeholder="请输入内网地址"/>
        </el-form-item>
        <el-form-item label="客户端端口" prop="clientPort">
          <el-input v-model="form.clientPort" placeholder="请输入客户端端口"/>
        </el-form-item>
        <el-form-item label="内网端口" prop="inPort">
          <el-input v-model="form.inPort" placeholder="请输入内网端口"/>
        </el-form-item>
        <el-form-item label="游戏数据连接" prop="dbUrl">
          <el-input v-model="form.dbUrl" type="textarea" placeholder="请输入游戏服url"
                    :autosize="{ minRows: 4, maxRows: 4}"/>
        </el-form-item>
        <el-form-item label="游戏日志连接" prop="dbLogUrl">
          <el-input v-model="form.dbLogUrl" type="textarea" placeholder="请输入游戏日志url"
                    :autosize="{ minRows: 4, maxRows: 4}"/>
        </el-form-item>
        <el-form-item label="数据库用户" prop="dbUser">
          <el-input v-model="form.dbUser" placeholder="请输入数据库用户"/>
        </el-form-item>
        <el-form-item label="数据库密码" prop="dbPass">
          <el-input v-model="form.dbPass" placeholder="请输入数据库密码"/>
        </el-form-item>
        <el-form-item label="服务器顺序" prop="sort">
          <el-input-number v-model="form.sort" controls-position="right" :min="0"/>
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
          <el-input v-model="form.homePath" placeholder="请输入服务路径"/>
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
  </div>

</template>

<script>
import {
  addServer,
  changeServerStatus,
  dataScope,
  delServer,
  getServer,
  kitoutAll,
  listServer,
  startServer,
  stopServer,
  updateServer
} from "@/api/gameGm/server";
import {listPlatformAll} from "@/api/gameGm/platform";
import {getToken} from "@/utils/auth";


export default {
  name: "Player",
  dicts: ['sys_normal_disable', 'game_server_type'],
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
      // 服务器表格数据
      serverTableList: [],
      // 弹出层标题
      title: "",
      // 是否显示弹出层
      open: false,
      // 是否显示弹出层（数据权限）
      openDataScope: false,
      // 日期范围
      dateRange: [],
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
        {key: 0, label: `服务器标识`, visible: true},
      ],
      // 表单校验
      rules: {
        serverId: [
          {required: true, message: "服务器ID不能为空", trigger: "blur"},
          {pattern: /^[1-9][0-9]{3}$/, message: '服务器ID需长度为4的数字', trigger: 'blur'}
        ],
        serverName: [
          {required: true, message: "服务器名不能为空", trigger: "blur"}
        ],
        platformId: [
          {required: true, message: "平台不能为空", trigger: "blur"}
        ],
        outHost: [
          {required: true, message: "外网地址不能为空", trigger: "blur"}
        ],
        inHost: [
          {required: true, message: "内网地址不能为空", trigger: "blur"}
        ],
        clientPort: [
          {required: true, message: "客户端端口不能为空", trigger: "blur"},
          {pattern: /^[0-9]*$/, message: '客户端端口需为数字', trigger: 'blur'}
        ],
        inPort: [
          {required: true, message: "服务端口不能为空", trigger: "blur"},
          {pattern: /^[0-9]*$/, message: '服务端口需为数字', trigger: 'blur'}
        ],
        sort: [
          {required: true, message: "排序不能为空", trigger: "blur"},
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
        headers: {Authorization: "Bearer " + getToken()},
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
      handler: function (newVal, oldVal) {
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
          this.serverTableList = response.rows;
          this.total = response.total;
          this.loading = false;
        }
      );
    },
    formatServerState(row, column, cellValue) {
      let status;
      switch (cellValue) {
        case 1:
          status = "白名单"
          break;
        case 2:
          status = "新服"
          break;
        case 3:
          status = "火爆"
          break;
        case 4:
          status = "拥挤"
          break;
        case 5:
          status = "空闲"
          break;
        case 6:
          status = "灰度"
          break;
        case 7:
          status = "维护中"
          break;
        case 8:
          status = "正常"
          break;
        case 9:
          status = "异常"
          break;
        default:
          status = "状态错误"
      }
      return status;
    },

    formatServerType(row, column, cellValue) {
      let type = 0;
      switch (cellValue) {
        case 1:
          type = "Gm服"
          break;
        case 2:
          type = "聊天服"
          break;
        case 3:
          type = "登录服"
          break;
        case 4:
          type = "网关服"
          break;
        case 5:
          type = "游戏服"
          break;
        case 6:
          type = "战斗服"
          break;
        default:
          type = "类型错误"
      }
      return type;
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
      this.form = {
        serverKeyId: undefined,
        serverId: undefined,
        serverName: undefined,
        platformId: undefined,
        sort: undefined,
        serverType: undefined,
        serverStatus: undefined,
        remark: undefined,
        // serverStatus: undefined
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
      this.ids = selection.map(item => item.serverId)
      this.single = selection.length != 1
      this.multiple = !selection.length
    },

    /** 新增按钮操作 */
    handleAdd() {
      this.reset();
      this.open = true;
      this.title = "添加服务器";
    },

    // 服务器批量维护状态修改
    handleBatchMaintain() {
      const serverStatus = 6;
      const platformId = this.queryParams.platformId;
      const data = {
        serverStatus,
        platformId
      };
      this.$modal.confirm('确认批量"' + '维护' + '"服务器吗？').then(function () {
        return changeServerStatus(data);
      }).then(() => {
        this.$modal.msgSuccess("维护成功");
        this.getList();
      }).catch(function () {
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
      this.$modal.confirm('确认批量"' + '开启' + '"服务器吗？').then(function () {
        return changeServerStatus(data);
      }).then(() => {
        this.$modal.msgSuccess("开启成功");
        this.getList();
      }).catch(function () {
      });
    },

    /** 修改按钮操作 */
    handleUpdate(row) {
      this.reset();
      const roleId = row.serverId || this.ids
      getServer(roleId).then(response => {
        this.form = response.data;
        this.open = true;
        this.$nextTick(() => {
          roleMenu.then(res => {
            let checkedKeys = res.checkedKeys
            checkedKeys.forEach((v) => {
              this.$nextTick(() => {
                this.$refs.menu.setChecked(v, true, false);
              })
            })
          });
        });
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
        this.$modal.confirm('确认批量启动服务器吗？').then(function () {
          return startServer("-1", data);
        }).then(() => {
          this.$modal.msgSuccess("启服成功");
          this.getList();
        }).catch(function () {
        });
      } else {
        this.$modal.confirm('确认启动' + serverIds + '"服务器吗？').then(function () {
          startServer(serverIds, data).then(response => {
            this.$modal.msgSuccess("启服成功");
          });
        }).then(() => {
          this.getList();
        }).catch(function () {
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
        this.$modal.confirm('确认批量停止服务器吗？').then(function () {
          return stopServer("-1", data);
        }).then(() => {
          this.$modal.msgSuccess("停止成功");
          this.getList();
        }).catch(function () {
        });
      } else {
        this.$modal.confirm('确认停止' + serverIds + '"服务器吗？').then(function () {
          stopServer(serverIds, data).then(response => {
            this.$modal.msgSuccess("停服成功");
          });
        }).then(() => {
          this.getList();
        }).catch(function () {
        });
      }

    },

    /** 提交按钮 */
    submitForm: function () {
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
    submitDataScope: function () {
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
      this.$modal.confirm('是否确认删除玩家ID为"' + roleIds + '"的数据项？').then(function () {
        return delServer(roleIds);
      }).then(() => {
        this.getList();
        this.$modal.msgSuccess("删除成功");
      }).catch(() => {
      });
    },

    /** 踢出按钮操作 */
    handleKitoutAll(row) {
      const serverIds = row.serverKeyId || this.ids;
      const platformId = this.queryParams.platformId;
      const data = {
        platformId
      };
      if (serverIds === undefined || serverIds == "") {
        this.$modal.confirm('是否确认踢出服务器的所有在线玩家？').then(function () {
          return kitoutAll("-1", data);
        }).then(() => {
          this.getList();
          this.$modal.msgSuccess("操作成功");
        }).catch(() => {
        });
      } else {
        this.$modal.confirm('是否确认踢出服务器ID为"' + serverIds + '"的所有玩家？').then(function () {
          return kitoutAll(serverIds, data);
        }).then(() => {
          this.getList();
          this.$modal.msgSuccess("操作成功");
        }).catch(() => {
        });
      }
    },

    itemList() {
      this.loading = true;
    },
  }
};
</script>
