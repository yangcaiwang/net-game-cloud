<template>
  <div class="app-container">
    <el-form :model="queryParams" ref="queryForm" size="small" :inline="true" v-show="showSearch" label-width="90px">
      <el-form-item label="订单号" prop="order">
        <el-input
          v-model="queryParams.order"
          placeholder="请输入订单号"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="账户" prop="uid">
        <el-input
          v-model="queryParams.uid"
          placeholder="请输入账户"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="商品id" prop="productId">
        <el-input
          v-model="queryParams.productId"
          placeholder="请输入商品id"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="渠道id" prop="channelId">
        <el-input
          v-model="queryParams.channelId"
          placeholder="请输入渠道id"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="玩家id" prop="roleId">
        <el-input
          v-model="queryParams.roleId"
          placeholder="请输入玩家id"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="渠道订单id" prop="billNo">
        <el-input
          v-model="queryParams.billNo"
          placeholder="请输入渠道订单id"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="游戏Id" prop="appId">
        <el-input
          v-model="queryParams.appId"
          placeholder="请输入游戏Id"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="服务器Id" prop="attachServerId">
        <el-input
          v-model="queryParams.attachServerId"
          placeholder="请输入服务器Id"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="创建时间">
          <el-date-picker
            v-model="dataRange"
            style="width: 360px"
            value-format="yyyy-MM-dd HH:mm:ss"
            format="yyyy-MM-dd HH:mm:ss"
            type="daterange"
            range-separator="-"
            start-placeholder="开始日期"
            end-placeholder="结束日期"
          ></el-date-picker>
        </el-form-item>
      <el-form-item>
        <el-button type="primary" icon="el-icon-search" size="mini" @click="handleQuery">搜索</el-button>
        <el-button icon="el-icon-refresh" size="mini" @click="resetQuery">重置</el-button>
      </el-form-item>
    </el-form>

    <el-row :gutter="10" class="mb8">
      <el-col :span="1.5">
        <el-button
          type="warning"
          plain
          icon="el-icon-download"
          size="mini"
          @click="handleExport"
          v-hasPermi="['gm:rechargelog:export']"
        >导出</el-button>
      </el-col>
      <right-toolbar :showSearch.sync="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <el-table v-loading="loading" :data="rechargeList" @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="55" align="center" />
      <el-table-column label="订单号" align="center" prop="order" />
      <el-table-column label="账户" align="center" prop="uid" />
      <el-table-column label="商品id" align="center" prop="productId" />
      <el-table-column label="渠道id" align="center" prop="channelId" />
      <el-table-column label="服务器" align="center" prop="attachServerId" />
      <el-table-column label="操作结果" align="center" prop="resultCode" >
        <template slot-scope="scope">
            <div v-html="getRechargeOrderResult(scope.row.resultCode)"></div>
        </template>
      </el-table-column>
      <el-table-column label="失败原因" align="center" prop="errorMsg" />
      <el-table-column label="充值金额" align="center" prop="amount" />
      <el-table-column label="玩家id" align="center" prop="roleId" />
      <el-table-column label="渠道订单id" align="center" prop="billNo" />
      <el-table-column label="游戏Id" align="center" prop="appId" />
      <el-table-column label="是否是测试" align="center" prop="testType" >
        <template slot-scope="scope">
            <div v-html="getRechargeTestType(scope.row.testType)"></div>
        </template>
      </el-table-column>
      <el-table-column label="创建时间" align="center" prop="createTime" width="180">
        <template slot-scope="scope">
          <span>{{ parseTime(scope.row.createTime) }}</span>
        </template>
      </el-table-column>
      <!-- <el-table-column label="操作" align="center" class-name="small-padding fixed-width"> -->
        <!-- <template slot-scope="scope"> -->
          <!-- <el-button
            size="mini"
            type="text"
            icon="el-icon-edit"
            @click="handleUpdate(scope.row)"
            v-hasPermi="['gm:1:edit']"
          >修改</el-button> -->
          <!-- <el-button
            size="mini"
            type="text"
            icon="el-icon-delete"
            @click="handleDelete(scope.row)"
            v-hasPermi="['gm:1:remove']"
          >删除</el-button> -->
        <!-- </template> -->
      <!-- </el-table-column> -->
    </el-table>

    <pagination
      v-show="total>0"
      :total="total"
      :page.sync="queryParams.pageNum"
      :limit.sync="queryParams.pageSize"
      @pagination="getList"
    />

    <!-- 添加或修改充值订单记录对话框 -->
    <!-- <el-dialog :title="title" :visible.sync="open" width="500px" append-to-body>
      <el-form ref="form" :model="form" :rules="rules" label-width="80px">
        <el-form-item label="订单号" prop="order">
          <el-input v-model="form.order" placeholder="请输入订单号" />
        </el-form-item>
        <el-form-item label="账户" prop="uid">
          <el-input v-model="form.uid" placeholder="请输入账户" />
        </el-form-item>
        <el-form-item label="商品id" prop="productId">
          <el-input v-model="form.productId" placeholder="请输入商品id" />
        </el-form-item>
        <el-form-item label="渠道id" prop="channelId">
          <el-input v-model="form.channelId" placeholder="请输入渠道id" />
        </el-form-item>
        <el-form-item label="游戏id" prop="gameId">
          <el-input v-model="form.gameId" placeholder="请输入游戏id" />
        </el-form-item>
        <el-form-item label="透传参数_平台id" prop="attachPlatformId">
          <el-input v-model="form.attachPlatformId" placeholder="请输入透传参数_平台id" />
        </el-form-item>
        <el-form-item label="透传参数_服务器id" prop="attachServerId">
          <el-input v-model="form.attachServerId" placeholder="请输入透传参数_服务器id" />
        </el-form-item>
        <el-form-item label="请求内容" prop="request">
          <el-input v-model="form.request" type="textarea" placeholder="请输入内容" />
        </el-form-item>
        <el-form-item label="1成功 -1失败" prop="resultCode">
          <el-input v-model="form.resultCode" placeholder="请输入1成功 -1失败" />
        </el-form-item>
        <el-form-item label="失败原因" prop="errorMsg">
          <el-input v-model="form.errorMsg" placeholder="请输入失败原因" />
        </el-form-item>
        <el-form-item label="充值金额" prop="amount">
          <el-input v-model="form.amount" placeholder="请输入充值金额" />
        </el-form-item>
        <el-form-item label="玩家id" prop="roleId">
          <el-input v-model="form.roleId" placeholder="请输入玩家id" />
        </el-form-item>
        <el-form-item label="渠道订单id" prop="billNo">
          <el-input v-model="form.billNo" placeholder="请输入渠道订单id" />
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button type="primary" @click="submitForm">确 定</el-button>
        <el-button @click="cancel">取 消</el-button>
      </div>
    </el-dialog> -->
  </div>
</template>

<script>
import {listRecharge} from "@/api/gameGm/rechargeLog";

export default {
  name: "RechargeLog",
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
        order: null,
        uid: null,
        productId: null,
        channelId: null,
        roleId: null,
        billNo: null,
      },
      dataRange: [],
      // 表单参数
      form: {},
      // 表单校验
      rules: {
        attachPlatformId: [
          { required: true, message: "透传参数_平台id不能为空", trigger: "blur" }
        ],
        attachServerId: [
          { required: true, message: "透传参数_服务器id不能为空", trigger: "blur" }
        ],
        resultCode: [
          { required: true, message: "1成功 -1失败不能为空", trigger: "blur" }
        ],
        roleId: [
          { required: true, message: "玩家id不能为空", trigger: "blur" }
        ],
      }
    };
  },
  created() {
    this.getList();
  },
  methods: {
    /** 查询充值订单记录列表 */
    getList() {
      this.loading = true;
      listRecharge(this.addDateRange(this.queryParams, this.dataRange)).then(response => {
        this.rechargeList = response.rows;
        this.total = response.total;
        this.loading = false;
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
        order: null,
        uid: null,
        productId: null,
        channelId: null,
        gameId: null,
        attachPlatformId: null,
        attachServerId: null,
        request: null,
        resultCode: null,
        errorMsg: null,
        amount: null,
        roleId: null,
        billNo: null,
        testType: null,
        id: null
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

    getRechargeOrderResult(result) {
      if (1 === result) {
        return "充值成功";
      } else {
        return "充值失败";
      }
    },

    getRechargeTestType(result) {
      if ("0" === result) {
        return "正式订单";
      } else {
        return "测试订单";
      }
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
      this.download('rechargelog/rechargelog/export', {
        ...this.queryParams
      }, `1_${new Date().getTime()}.xlsx`)
    }
  }
};
</script>
