<template>
  <div class="app-container">
    <!-- <el-form :model="queryParams" ref="queryForm" size="small" :inline="true" v-show="showSearch" label-width="68px">
      <el-form-item>
        <el-button type="primary" icon="el-icon-search" size="mini" @click="handleQuery">搜索</el-button>
        <el-button icon="el-icon-refresh" size="mini" @click="resetQuery">重置</el-button>
      </el-form-item>
    </el-form> -->

    <el-row :gutter="10" class="mb8">
      <el-col :span="1.5">
        <el-button
          type="primary"
          plain
          icon="el-icon-plus"
          size="mini"
          @click="handleAdd"
          v-hasPermi="['gm:broadcast:add']"
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
          v-hasPermi="['gm:broadcast:edit']"
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
          v-hasPermi="['gm:broadcast:remove']"
        >删除</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
          type="warning"
          plain
          icon="el-icon-download"
          size="mini"
          @click="handleExport"
          v-hasPermi="['gm:broadcast:export']"
        >导出</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
          type="danger"
          plain
          icon="el-icon-check"
          size="mini"
          :disabled="multiple"
          @click="handlePass"
          v-hasPermi="['gm:broadcast:pass']"
        >通过</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
          type="danger"
          plain
          icon="el-icon-close"
          size="mini"
          :disabled="multiple"
          @click="handleRefuse"
          v-hasPermi="['gm:broadcast:pass']"
        >拒绝</el-button>
      </el-col>
      <right-toolbar :showSearch.sync="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <el-table v-loading="loading" :data="broadcastList" @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="55" align="center" />
      <el-table-column label="唯一id" align="center" prop="id" />
      <el-table-column label="服务器" align="center" prop="serverId" >
        <template slot-scope="scope">
            <div v-html="getServerName(scope.row.serverId)"></div>
        </template>
      </el-table-column>
      <el-table-column label="间隔时间" align="center" prop="interval" />
      <el-table-column label="广播次数" align="center" prop="times" />
      <el-table-column label="广播内容" align="center" prop="bcContent" />
      <el-table-column label="状态" align="center" prop="status">
        <template slot-scope="scope">
          <dict-tag :options="dict.type.check_auto" :value="scope.row.status"/>
        </template>
      </el-table-column>
      <el-table-column label="创建时间" align="center" prop="createTime" width="180">
        <template slot-scope="scope">
          <span>{{ parseTime(scope.row.createTime) }}</span>
        </template>
      </el-table-column>
      <el-table-column label="操作" align="center" class-name="small-padding fixed-width">
        <template slot-scope="scope">
          <el-button
            size="mini"
            type="text"
            icon="el-icon-edit"
            @click="handleUpdate(scope.row)"
            v-hasPermi="['gm:broadcast:edit']"
            v-if="scope.row.status === undefined || scope.row.status === 0 "
          >修改</el-button>
          <el-button
            size="mini"
            type="text"
            icon="el-icon-delete"
            @click="handleDelete(scope.row)"
            v-hasPermi="['gm:broadcast:remove']"
            v-if="scope.row.status === undefined || scope.row.status === 0 "
          >删除</el-button>
          <el-button
            size="mini"
            type="text"
            icon="el-icon-edit"
            @click="handlePass(scope.row)"
            v-hasPermi="['gm:broadcast:pass']"
          >通过</el-button>
          <el-button
            size="mini"
            type="text"
            icon="el-icon-delete"
            @click="handleRefuse(scope.row)"
            v-hasPermi="['gm:broadcast:pass']"
            v-if="scope.row.status === undefined || scope.row.status === 0 "
          >拒绝</el-button>
          <el-button
            size="mini"
            type="text"
            icon="el-icon-delete"
            @click="handleCancel(scope.row)"
            v-hasPermi="['gm:broadcast:pass']"
            v-if="scope.row.status !== undefined && scope.row.status > 0 && scope.row.status === 1"
          >取消</el-button>
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

    <!-- 添加或修改游戏管理  跑马灯对话框 -->
    <el-dialog :title="title" :visible.sync="open" width="800px" append-to-body>
      <el-form ref="form" :model="form" :rules="rules" label-width="80px">
        <el-form-item label="服务器" prop="serverId">
            <el-select
              v-model="form.servers"
              placeholder="选择服务器"
              multiple
              collapse-tags
              v-bind="$attrs"
              v-on="$listeners"
              style="width: 240px"
            >
              <el-checkbox
              v-model="check"
              class="m-l-20"
              :indeterminate="form.servers.length !== serverList.length"
              @change="selectAll"
            >
              全选
            </el-checkbox>
              <el-option
                v-for="item in serverList"
                :key="item.serverKeyId"
                :label="item.serverId + '_' + item.serverName"
                :value="item.serverKeyId"
              />
            </el-select>
        </el-form-item>
        <!-- <el-form-item label="服务器" prop="serverId">
          <el-input v-model="form.serverId" placeholder="请输入服务器" />
        </el-form-item> -->
        <el-form-item label="间隔时间" prop="interval">
          <el-input v-model="form.interval" placeholder="请输入间隔时间" />
        </el-form-item>
        <el-form-item label="广播次数" prop="times">
          <el-input v-model="form.times" placeholder="请输入广播次数" />
        </el-form-item>
        <el-form-item label="广播内容">
          <editor v-model="form.bcContent" :min-height="192"/>
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
import { listBroadcast, getBroadcast, delBroadcast, addBroadcast, updateBroadcast, pass } from "@/api/gameGm/broadcast";
import { listServerAll } from "@/api/gameGm/server";

export default {
  name: "Broadcast",
  dicts: ['check_auto'],
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
      // 游戏管理  跑马灯表格数据
      broadcastList: [],
      // 弹出层标题
      title: "",
      // 是否显示弹出层
      open: false,
      // 查询参数
      queryParams: {
        pageNum: 1,
        pageSize: 10,
      },
      // 表单参数
      form: {
        servers:[],
        allServer: undefined,
      },
      // 表单校验
      rules: {
        interval: [
          { required: true, message: "间隔时间不能为空", trigger: "blur" }
        ],
        times: [
          { required: true, message: "广播次数不能为空", trigger: "blur" }
        ],
        status: [
          { required: true, message: "邮件状态不能为空", trigger: "blur" }
        ],
      },
      serverList: [],
    };
  },
  created() {
    this.getList();
    this.getServerList();
  },
  computed: {
    check: {
      get () {
        if (this.form.servers.length === this.serverList.length) {
          return true
        }
        return false
      },
      set () {}
    }
  },
  methods: {
    /** 查询游戏管理  跑马灯列表 */
    getList() {
      this.loading = true;
      listBroadcast(this.queryParams).then(response => {
        this.broadcastList = response.rows;
        this.total = response.total;
        this.loading = false;
      });
    },

    getServerList() {
      listServerAll().then(response => {
        this.serverList = response.rows;
      });
    },
    // 取消按钮
    cancel() {
      this.open = false;
      this.reset();
    },
    // 表单重置
    reset() {
      this.form = {
        id: null,
        serverId: null,
        serverName: null,
        interval: null,
        times: null,
        bcContent: null,
        status: 0,
        servers:[],
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
      this.resetForm("queryForm");
      this.handleQuery();
    },
    // 多选框选中数据
    handleSelectionChange(selection) {
      this.ids = selection.map(item => item.id)
      this.single = selection.length!==1
      this.multiple = !selection.length
    },
    /** 新增按钮操作 */
    handleAdd() {
      this.reset();
      this.open = true;
      this.title = "添加游戏管理  跑马灯";
    },
    /** 修改按钮操作 */
    handleUpdate(row) {
      this.reset();
      const id = row.id || this.ids
      getBroadcast(id).then(response => {
        this.form = response.data;
         if (this.form.servers === undefined) {
          this.form.servers = [];
        }
        if (this.form.servers.includes("-1")) {
          this.form.servers = this.serverList.map(d => d.serverKeyId);
        }
        this.open = true;
        this.title = "修改游戏管理  跑马灯";
      });
    },
    /** 提交按钮 */
    submitForm() {
      this.$refs["form"].validate(valid => {
        if (valid) {
          if (this.form.id != null) {
            updateBroadcast(this.form).then(response => {
              this.$modal.msgSuccess("修改成功");
              this.open = false;
              this.getList();
            });
          } else {
            addBroadcast(this.form).then(response => {
              this.$modal.msgSuccess("新增成功");
              this.open = false;
              this.getList();
            });
          }
        }
      });
    },
    /** 删除按钮操作 */
    handleDelete(row) {
      const ids = row.id || this.ids;
      this.$modal.confirm('是否确认删除游戏管理  跑马灯编号为"' + ids + '"的数据项？').then(function() {
        return delBroadcast(ids);
      }).then(() => {
        this.getList();
        this.$modal.msgSuccess("删除成功");
      }).catch(() => {});
    },
    /** 导出按钮操作 */
    handleExport() {
      this.download('gameGm/broadcast/export', {
        ...this.queryParams
      }, `broadcast_${new Date().getTime()}.xlsx`)
    },

    /** 通过按钮操作 */
    handlePass(row) {
      this.reset();
      const id = row.id || this.ids;
      this.$modal.confirm('是否确认通过["' + id + '"]？').then(function() {
        return pass(id, 1);
      }).then(() => {
        this.getList();
        this.$modal.msgSuccess("已通过");
      }).catch(() => {});
    },

        /** 通过按钮操作 */
    handleCancel(row) {
      this.reset();
      const id = row.id || this.ids;
      this.$modal.confirm('是否确认取消["' + id + '"]？').then(function() {
        return pass(id, 3);
      }).then(() => {
        this.getList();
        this.$modal.msgSuccess("已取消");
      }).catch(() => {});
    },

    /** 拒绝按钮操作 */
    handleRefuse(row) {
      this.reset();
      const id = row.id || this.ids;
      this.$modal.confirm('是否确认拒绝["' + id + '"]？').then(function() {
        return pass(id, 2);
      }).then(() => {
        this.getList();
        this.$modal.msgSuccess("已拒绝");
      }).catch(() => {});
    },

    selectAll (checked) {
      if (checked) {
        this.form.servers = this.serverList.map(d => d.serverKeyId);
        this.form.allServer = "ALL";
      } else {
        this.form.servers = [];
        this.form.allServer = "";
      }
    },

    getServerName(serverList) {
      if (serverList === "-1" || serverList.includes("-1")) {
        return "ALL";
      } else {
        if (serverList !== undefined && serverList !== "") {
          return this.serverList.filter(item => serverList.includes(item.serverKeyId)).map(d => d.serverId + '_' + d.serverName).join(",");
        }
      }
      return "";
    },
  }
};
</script>
