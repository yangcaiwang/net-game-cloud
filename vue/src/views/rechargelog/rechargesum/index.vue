<template>
  <div class="app-container">

    <el-row :gutter="10" class="mb8">
      <el-col :span="1.5">
        <el-button
          type="warning"
          plain
          icon="el-icon-download"
          size="mini"
          @click="handleExport"
          v-hasPermi="['gm:rechargesum:export']"
        >导出</el-button>
      </el-col>
      <right-toolbar :showSearch.sync="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <el-table v-loading="loading" :data="rechargeList" @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="55" align="center" />
      <el-table-column label="服务器编号" align="center" prop="serverKeyId" />
      <el-table-column label="服务器" align="center" prop="serverName" />
      <el-table-column label="总充值" align="center" prop="sumMoney" />
      <el-table-column label="操作" align="center" class-name="small-padding fixed-width">
        <template slot-scope="scope">
          <el-button
            size="mini"
            type="text"
            icon="el-icon-edit"
            @click="handleDetail(scope.row)"
            v-hasPermi="['gm:rechargesum:detail']"
          >充值详情</el-button>
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

    <!-- 添加或修改充值订单记录对话框 -->
    <el-dialog :title="title" :visible.sync="open" width="60%" append-to-body>
      <el-form :model="queryDetailParams" ref="queryForm" size="small" :inline="true" v-show="showSearch">
        <el-form-item label="玩家ID" prop="roleId">
          <el-input
            v-model="queryDetailParams.roleId"
            placeholder="请输入玩家ID"
            clearable
            style="width: 240px"
            @keyup.enter.native="handleDetaillQuery"
          />
        </el-form-item>
        <el-form-item label="支付配置ID" prop="payId">
          <el-input
            v-model="queryDetailParams.payId"
            placeholder="请输入支付配置ID"
            clearable
            style="width: 240px"
            @keyup.enter.native="handleDetaillQuery"
          />
        </el-form-item>
        <el-form-item label="充值类型" prop="payType">
          <el-input
            v-model="queryDetailParams.payType"
            placeholder="请输入充值类型"
            clearable
            style="width: 240px"
            @keyup.enter.native="handleDetaillQuery"
          />
        </el-form-item>
        <el-form-item label="SDK订单" prop="sdkOrder">
          <el-input
            v-model="queryDetailParams.sdkOrder"
            placeholder="请输入SDK订单"
            clearable
            style="width: 240px"
            @keyup.enter.native="handleDetaillQuery"
          />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" icon="el-icon-search" size="mini" @click="handleDetaillQuery">搜索</el-button>
          <el-button icon="el-icon-refresh" size="mini" @click="resetDetailQuery">重置</el-button>
        </el-form-item>
      </el-form>
      <el-row :gutter="10" class="mb8">
        <el-col :span="1.5">
          <el-button
            type="warning"
            plain
            icon="el-icon-download"
            size="mini"
            @click="handleDetailExport"
            v-hasPermi="['gm:rechargesum:detail:export']"
          >导出</el-button>
        </el-col>
        <right-toolbar @queryTable="getItemLogList"></right-toolbar>
      </el-row>
      <el-table v-loading="detailLoading" :data="detailList" >
          <el-table-column label="玩家ID" prop="roleId" align="center" width="180" :show-overflow-tooltip="true" />
          <el-table-column label="充值配置ID" prop="payId" align="center" />
          <el-table-column label="充值类型" prop="payType" align="center" width="90" />
          <el-table-column label="充值金额" prop="payMoney" align="center"  />
          <el-table-column label="充值后台表索引" prop="orderLogId" align="center"  />
          <el-table-column label="SDK订单ID" prop="sdkOrder" align="center" :show-overflow-tooltip="true" />
          <el-table-column label="充值时间" align="center" prop="firstCreateTime" width="180">
            <template slot-scope="scope">
              <span>{{ parseTime(scope.row.firstCreateTime) }}</span>
            </template>
          </el-table-column>
          <!-- <el-table-column label="充值时间" prop="createTime" align="center"  /> -->
      </el-table>
      <pagination
          v-show="detailTotal>0"
          :total="detailTotal"
          :page.sync="queryDetailParams.pageNum"
          :limit.sync="queryDetailParams.pageSize"
          @pagination="getRechargeList"
      />
    </el-dialog>
  </div>
</template>

<script>
import { getRechargeSum, getRechargeDetailList } from "@/api/gameGm/rechargeLog";

export default {
  name: "RechargeLog",
  data() {
    return {
      // 遮罩层
      loading: true,
      detailLoading: true,
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
      // 充值订单记录表格数据
      rechargeList: [],
      // 弹出层标题
      title: "",
      // 是否显示弹出层
      open: false,
      // 查询参数
      queryParams: {
        pageNum: 1,
        pageSize: 20,
        serverId: 0,
      },

      queryDetailParams: {
        pageNum: 1,
        pageSize: 20,
        serverId: 0,
      },
      detailTotal: 0,

      detailList:[],
      midTotle: 0,
      serverId: 0,
    };
  },
  created() {
    this.getList();
  },
  methods: {
    /** 查询充值订单记录列表 */
    getList() {
      this.loading = true;
      getRechargeSum(this.queryParams).then(response => {
        this.rechargeList = response.rows;
        this.total = response.total;
        this.loading = false;
      });
    },
    // 取消按钮
    cancel() {
      this.detailTotal = 0;
      this.open = false;
      this.reset();
    },
    // 表单重置
    reset() {
      this.form = {

      };
      this.detailList =[],
      this.detailTotal = 0;
      this.resetForm("form");
    },
    /** 搜索按钮操作 */
    // handleQuery() {
    //   this.queryParams.pageNum = 1;
    //   this.getList();
    // },

        /** 搜索按钮操作 */
    handleDetaillQuery() {
      this.queryDetailParams.pageNum = 1;
      this.getRechargeList();
    },
    /** 重置按钮操作 */
    // resetQuery() {
    //   this.resetForm("queryForm");
    //   this.handleQuery();
    // },

    /** 重置按钮操作 */
    resetDetailQuery() {
      this.resetForm("queryForm");
      this.handleDetaillQuery();
    },

    // 多选框选中数据
    handleSelectionChange(selection) {
      this.ids = selection.map(item => item.id)
      this.single = selection.length!==1
      this.multiple = !selection.length
    },

    /** 细节按钮操作 */
    handleDetail(row) {
      this.reset();
      this.queryDetailParams.serverId = row.serverKeyId;
      this.getRechargeList();
    },

    getRechargeList() {
      this.detailLoading = true;
      getRechargeDetailList(this.queryDetailParams).then(response => {
        this.detailList = response.rows;
        this.detailTotal = response.total;
        this.open = true;
        this.title = "充值详情";
        this.detailLoading = false;
      });
    },
    /** 新增按钮操作 */
    // handleAdd() {
    //   this.reset();
    //   this.open = true;
    //   this.title = "添加充值订单记录";
    // },
    /** 修改按钮操作 */
    // handleUpdate(row) {
    //   this.reset();
    //   const id = row.id || this.ids
    //   getRecharge(id).then(response => {
    //     this.form = response.data;
    //     this.open = true;
    //     this.title = "修改充值订单记录";
    //   });
    // },
    /** 提交按钮 */
    // submitForm() {
    //   this.$refs["form"].validate(valid => {
    //     if (valid) {
    //       if (this.form.id != null) {

    //       } else {
    //         addRecharge(this.form).then(response => {
    //           this.$modal.msgSuccess("新增成功");
    //           this.open = false;
    //           this.getList();
    //         });
    //       }
    //     }
    //   });
    // },
    /** 删除按钮操作 */
    // handleDelete(row) {
    //   const ids = row.id || this.ids;
    //   this.$modal.confirm('是否确认删除充值订单记录编号为"' + ids + '"的数据项？').then(function() {
    //     return del1(ids);
    //   }).then(() => {
    //     this.getList();
    //     this.$modal.msgSuccess("删除成功");
    //   }).catch(() => {});
    // },
    /** 导出按钮操作 */
    handleExport() {
      this.download('rechargelog/rechargesum/export', {
        ...this.queryParams
      }, `1_${new Date().getTime()}.xlsx`)
    },

        /** 导出按钮操作 */
    handleDetailExport() {
      this.download('rechargelog/rechargesum/detail/export', {
        ...this.queryDetailParams
      }, `${this.queryDetailParams.serverId}_${new Date().getTime()}.xlsx`)
    },
  }
};
</script>
