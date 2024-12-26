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
          v-hasPermi="['gm:command:add']"
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
          v-hasPermi="['gm:command:edit']"
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
          v-hasPermi="['gm:command:remove']"
        >删除</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
          type="danger"
          plain
          icon="el-icon-check"
          size="mini"
          :disabled="multiple"
          @click="handlePass"
          v-hasPermi="['gm:command:pass']"
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
          v-hasPermi="['gm:command:pass']"
        >拒绝</el-button>
      </el-col>
      <!-- <el-col :span="1.5">
        <el-button
          type="warning"
          plain
          icon="el-icon-download"
          size="mini"
          @click="handleExport"
          v-hasPermi="['gm:command:export']"
        >导出</el-button> -->
      <!-- </el-col> -->
      <right-toolbar :showSearch.sync="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <el-table v-loading="loading" :data="commandList" @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="55" align="center" />
      <el-table-column label="唯一ID" align="center" prop="id" />
      <el-table-column label="服务器" align="center" prop="serverId" >
        <template slot-scope="scope">
            <div v-html="getServerName(scope.row.serverId)"></div>
        </template>
      </el-table-column>
      <el-table-column label="玩家ID" align="center" prop="roleId" />
      <el-table-column label="执行命令" align="center" prop="command" />
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
            v-hasPermi="['gm:command:edit']"
            v-if="scope.row.status === undefined || scope.row.status === 0 "
          >修改</el-button>
          <el-button
            size="mini"
            type="text"
            icon="el-icon-delete"
            @click="handleDelete(scope.row)"
            v-hasPermi="['gm:command:remove']"
          >删除</el-button>
          <el-button
            size="mini"
            type="text"
            icon="el-icon-check"
            @click="handlePass(scope.row)"
            v-hasPermi="['gm:command:pass']"
            v-if="scope.row.status === undefined || scope.row.status === 0 "
          >通过</el-button>
          <el-button
            size="mini"
            type="text"
            icon="el-icon-close"
            @click="handleRefuse(scope.row)"
            v-hasPermi="['gm:command:pass']"
            v-if="scope.row.status === undefined || scope.row.status === 0 "
          >拒绝</el-button>
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

    <!-- 添加或修改gm命令行对话框 -->
    <el-dialog :title="title" :visible.sync="open" width="500px" append-to-body>
      <el-form ref="form" :model="form" :rules="rules" label-width="80px">
        <el-form-item label="服务器" prop="serverId">
            <el-select
              v-model="form.serverId"
              placeholder="选择服务器"
              collapse-tags
              style="width: 240px"
            >
              <el-option
                v-for="item in serverList"
                :key="item.serverKeyId"
                :label="item.serverId + '_' +item.serverName"
                :value="item.serverKeyId"
              />
            </el-select>
          </el-form-item>
        <el-form-item label="玩家ID" prop="roleId">
          <el-input v-model="form.roleId" placeholder="请输入玩家ID" />
        </el-form-item>
        <el-form-item label="执行命令" prop="command">
          <el-input v-model="form.command" placeholder="请输入执行命令" />
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
import { listCommand, getCommand, delCommand, addCommand, updateCommand, pass } from "@/api/gameGm/command";
import { listServerAll } from "@/api/gameGm/server";

export default {
  name: "Command",
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
      // gm命令行表格数据
      commandList: [],
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
      form: {},
      // 表单校验
      rules: {
      },
      serverList: [],
    };
  },
  created() {
    this.getList();
    this.getServerList();
  },
  methods: {
    /** 查询gm命令行列表 */
    getList() {
      this.loading = true;
      listCommand(this.queryParams).then(response => {
        this.commandList = response.rows;
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
        roleId: null,
        command: null,
        status : 0,
        serverId: undefined,
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
      this.title = "添加gm命令行";
    },
    /** 修改按钮操作 */
    handleUpdate(row) {
      this.reset();
      const id = row.id || this.ids
      getCommand(id).then(response => {
        this.form = response.data;
        this.open = true;
        this.title = "修改gm命令行";
      });
    },
    /** 提交按钮 */
    submitForm() {
      this.$refs["form"].validate(valid => {
        if (valid) {
          if (this.form.id != null) {
            updateCommand(this.form).then(response => {
              this.$modal.msgSuccess("修改成功");
              this.open = false;
              this.getList();
            });
          } else {
            addCommand(this.form).then(response => {
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
      this.$modal.confirm('是否确认删除gm命令行编号为"' + ids + '"的数据项？').then(function() {
        return delCommand(ids);
      }).then(() => {
        this.getList();
        this.$modal.msgSuccess("删除成功");
      }).catch(() => {});
    },
    /** 导出按钮操作 */
    handleExport() {
      this.download('gameGm/command/export', {
        ...this.queryParams
      }, `command_${new Date().getTime()}.xlsx`)
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

    getServerName(serverId) {
      return this.serverList.filter(item => serverId == item.serverKeyId).map(d => d.serverId + '_' + d.serverName).join(",");
    },
  }
};
</script>
