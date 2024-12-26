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
          v-hasPermi="['gm:recharge:add']"
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
          v-hasPermi="['gm:recharge:edit']"
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
          v-hasPermi="['gm:recharge:remove']"
        >删除</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
          type="warning"
          plain
          icon="el-icon-download"
          size="mini"
          @click="handleExport"
          v-hasPermi="['gm:recharge:export']"
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
          v-hasPermi="['gm:recharge:pass']"
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
          v-hasPermi="['gm:recharge:pass']"
        >拒绝</el-button>
      </el-col>
      <right-toolbar :showSearch.sync="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <el-table v-loading="loading" :data="rechargeList" @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="55" align="center" />
      <el-table-column label="唯一ID" align="center" prop="id" />
      <el-table-column label="玩家" align="center" prop="roleIds" />
      <el-table-column label="充值档次" align="center" prop="rechargeIds" />
      <el-table-column label="服务器" align="center" prop="serverId" >
        <template slot-scope="scope">
            <div v-html="getServerName(scope.row.serverId)"></div>
        </template>
      </el-table-column>
      <el-table-column label="状态" align="center" prop="status">
        <template slot-scope="scope">
          <dict-tag :options="dict.type.check_auto" :value="scope.row.status"/>
        </template>
      </el-table-column>
      <el-table-column label="操作" align="center" class-name="small-padding fixed-width">
        <template slot-scope="scope">
          <el-button
            size="mini"
            type="text"
            icon="el-icon-edit"
            @click="handleUpdate(scope.row)"
            v-hasPermi="['gm:recharge:edit']"
            v-if="scope.row.status === undefined || scope.row.status === 0 "
          >修改</el-button>
          <el-button
            size="mini"
            type="text"
            icon="el-icon-delete"
            @click="handleDelete(scope.row)"
            v-hasPermi="['gm:recharge:remove']"
          >删除</el-button>
          <el-button
            size="mini"
            type="text"
            icon="el-icon-check"
            @click="handlePass(scope.row)"
            v-hasPermi="['gm:recharge:pass']"
            v-if="scope.row.status === undefined || scope.row.status === 0 "
          >通过</el-button>
          <el-button
            size="mini"
            type="text"
            icon="el-icon-close"
            @click="handleRefuse(scope.row)"
            v-hasPermi="['gm:recharge:pass']"
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

    <!-- 添加或修改GM充值对话框 -->
    <el-dialog :title="title" :visible.sync="open" width="500px" append-to-body>
      <el-form ref="form" :model="form" :rules="rules" label-width="120px">
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
        <el-form-item label="玩家" prop="roleIds">
          <span slot="label">
            <el-tooltip content="以(|)分隔,可以输入多个玩家ID,如:玩家ID|玩家ID" placement="top">
              <i class="el-icon-question"></i>
            </el-tooltip>
            玩家ID
          </span>
          <el-input v-model="form.roleIds" type="textarea" placeholder="请输入内容" />
        </el-form-item>
        <el-form-item label="充值档次" prop="rechargeIds">
          <span slot="label">
            <el-tooltip content="格式为:充值ID,次数|充值ID,次数" placement="top">
              <i class="el-icon-question"></i>
            </el-tooltip>
            充值配置ID
          </span>
          <el-input v-model="form.rechargeIds" placeholder="请输入充值档次" />
        </el-form-item>
        <!-- <el-form-item label="状态">
          <el-radio-group v-model="form.status">
            <el-radio
              v-for="dict in dict.type.check_auto"
              :key="dict.value"
              :label="parseInt(dict.value)"
            >{{dict.label}}</el-radio>
          </el-radio-group>
        </el-form-item> -->
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button type="primary" @click="submitForm">确 定</el-button>
        <el-button @click="cancel">取 消</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import { listRecharge, getRecharge, delRecharge, addRecharge, updateRecharge, pass } from "@/api/gameGm/recharge";
import { listServerAll } from "@/api/gameGm/server";

export default {
  name: "Recharge",
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
      // GM充值表格数据
      rechargeList: [],
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
        serverId: [
          { required: true, message: "服务器不能为空", trigger: "blur" }
        ],
        roleIds: [
          { required: true, message: "玩家ID不能为空", trigger: "blur" }
        ],
        rechargeIds: [
          { required: true, message: "充值内容不能为空", trigger: "blur" }
        ],
      },
      serverList: [],
    };
  },
  created() {
    this.getList();
    this.getServerList();
  },
  methods: {
    /** 查询GM充值列表 */
    getList() {
      this.loading = true;
      listRecharge(this.queryParams).then(response => {
        this.rechargeList = response.rows;
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
        roleIds: null,
        rechargeIds: null,
        serverId: null,
        status: 0,
        createBy: null,
        createTime: null,
        updateBy: null,
        updateTime: null
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
      this.title = "添加GM充值";
    },
    /** 修改按钮操作 */
    handleUpdate(row) {
      this.reset();
      const id = row.id || this.ids
      getRecharge(id).then(response => {
        this.form = response.data;
        this.open = true;
        this.title = "修改GM充值";
      });
    },
    /** 提交按钮 */
    submitForm() {
      this.$refs["form"].validate(valid => {
        if (valid) {
          if (this.form.id != null) {
            updateRecharge(this.form).then(response => {
              this.$modal.msgSuccess("修改成功");
              this.open = false;
              this.getList();
            });
          } else {
            addRecharge(this.form).then(response => {
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
      this.$modal.confirm('是否确认删除GM充值编号为"' + ids + '"的数据项？').then(function() {
        return delRecharge(ids);
      }).then(() => {
        this.getList();
        this.$modal.msgSuccess("删除成功");
      }).catch(() => {});
    },
    /** 导出按钮操作 */
    handleExport() {
      this.download('gm/recharge/export', {
        ...this.queryParams
      }, `recharge_${new Date().getTime()}.xlsx`)
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
