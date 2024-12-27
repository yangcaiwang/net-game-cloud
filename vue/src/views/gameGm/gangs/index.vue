<template>
  <div class="app-container">
    <el-form :model="queryParams" ref="queryForm" size="small" :inline="true" v-show="showSearch">
      <el-form-item label="服务器" prop="serverId">
        <el-select
          v-model="queryParams.serverId"
          placeholder="选择服务器"
          clearable
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
      <el-form-item label="帮派编号" prop="gangsId">
        <el-input
          v-model="queryParams.gangsId"
          placeholder="请输入帮派编号"
          clearable
          style="width: 240px"
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="帮派名" prop="gangsName">
        <el-input
          v-model="queryParams.gangsName"
          placeholder="请输入帮派名"
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
      <!-- <el-col :span="1.5">
        <el-button
          type="danger"
          plain
          icon="el-icon-delete"
          size="mini"
          :disabled="multiple"
          @click="handleDelete"
          v-hasPermi="['gm:gangs:remove']"
        >删除</el-button>
      </el-col> -->
      <el-col :span="1.5">
        <el-button
          type="warning"
          plain
          icon="el-icon-download"
          size="mini"
          @click="handleExport"
          v-hasPermi="['gm:gangs:export']"
        >导出</el-button>
      </el-col>
      <right-toolbar :showSearch.sync="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <el-table v-loading="loading" :data="roleList" >
      <!-- <el-table-column type="selection" width="55" align="center" /> -->
      <el-table-column label="帮派编号" prop="gangsId" align="center" width="180" />
      <el-table-column label="帮派名" prop="gangsName" align="center" :show-overflow-tooltip="true" width="90" />
      <el-table-column label="帮主ID" prop="gangsAdminId" align="center" :show-overflow-tooltip="true" width="180" />
      <el-table-column label="帮派等级" prop="level" align="center" width="90" />
      <el-table-column label="帮派公告" prop="notice" align="center" :show-overflow-tooltip="true" width="320" />
      <el-table-column label="帮派活跃" prop="active" align="center" width="90" />
      <el-table-column label="周活跃值" prop="weekActive" align="center" width="90" />
      <el-table-column label="日活跃值" prop="dailyActive" width="90" />
      <el-table-column label="官职信息" prop="positionMap" align="center" :show-overflow-tooltip="true" width="180" />

      <el-table-column label="创建时间" align="center" prop="createTime" width="180">
        <template slot-scope="scope">
          <span>{{ parseTime(scope.row.createTime) }}</span>
        </template>
      </el-table-column>
      <el-table-column label="操作" align="center" class-name="small-padding fixed-width" width="240">
        <template slot-scope="scope" v-if="scope.row.roleId !== 1">
          <el-button
            size="mini"
            type="text"
            icon="el-icon-search"
            @click="handleMemberShow(scope.row)"
            v-hasPermi="['gm:gangs:player']"
          >查看成员</el-button>
          <!-- <el-button
            size="mini"
            type="text"
            icon="el-icon-delete"
            @click="handleDelete(scope.row)"
            v-hasPermi="['gm:gangs:remove']"
          >删除</el-button> -->
          <el-button
            size="mini"
            type="text"
            icon="el-icon-edit"
            @click="handleModNotice(scope.row)"
            v-hasPermi="['gm:gangs:noticeedit']"
          >改公告</el-button>
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

    <!-- 查看成员对话框 -->
    <el-dialog :title="title" :visible.sync="open" width="60%" append-to-body>
      <el-table v-loading="loading" :data="gangsMemberList" >
          <el-table-column label="玩家标识" prop="roleId" align="center" width="180" />
          <el-table-column label="帮贡" prop="donate" align="center" width="120" />
          <el-table-column label="军团技能" prop="skills" align="center" :show-overflow-tooltip="true" />
          <el-table-column label="活跃数据" prop="legionActiveData" align="center" :show-overflow-tooltip="true" />
          <el-table-column label="加入时间" align="center" prop="joinLegionTime" width="180">
            <template slot-scope="scope">
              <span>{{ parseTime(scope.row.joinLegionTime) }}</span>
            </template>
          </el-table-column>
      </el-table>
    </el-dialog>

    <!-- 修改公告对话框 -->
    <el-dialog :title="titleNotice" :visible.sync="openNotice" width="50%" append-to-body>
      <el-form ref="modNotice" :model="form" label-width="100px">
          <el-form-item label="联盟公告" prop="notice">
            <el-input v-model="form.notice" type="textarea" placeholder="请输入公告内容" :autosize="{ minRows: 4, maxRows: 4}" />
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
import {getGangsMember, listGangs, modifyGangsNotice} from "@/api/gameGm/gangs";
import {listServerAll} from "@/api/gameGm/server";

// import { treeselect as menuTreeselect, roleMenuTreeselect } from "@/api/system/menu";
// import { treeselect as deptTreeselect, roleDeptTreeselect } from "@/api/system/dept";

export default {
  name: "Gangs",
  dicts: ['sys_normal_disable'],
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
      titleNotice: "",
      // 是否显示弹出层
      open: false,
      // 是否显示弹出层
      openNotice: false,
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
        gangsId: undefined,
        gangsName: undefined,
        serverId: undefined
      },
      // 表单参数
      form: {},
      defaultProps: {
        children: "children",
        label: "label"
      },
      // 表单校验
      // rules: {
      //   roleName: [
      //     { required: true, message: "角色名称不能为空", trigger: "blur" }
      //   ],
      //   roleKey: [
      //     { required: true, message: "权限字符不能为空", trigger: "blur" }
      //   ],
      //   roleSort: [
      //     { required: true, message: "角色顺序不能为空", trigger: "blur" }
      //   ]
      // },

      gangsMemberList: [],
      serverList: [],
      gangsId: 0,
    };
  },
  created() {
    this.getServerList();
    this.getList();
  },
  methods: {
    /** 查询角色列表 */
    getList() {
      this.loading = true;
      listGangs(this.addDateRange(this.queryParams, this.dateRange)).then(response => {
          this.roleList = response.rows;
          this.total = response.total;
          this.loading = false;
        }
      );
    },

    getServerList() {
      listServerAll().then(response => {
        this.serverList = response.rows;
      });
    },

    // 表单重置
    reset() {
      if (this.$refs.menu != undefined) {
        this.$refs.menu.setCheckedKeys([]);
      }
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
      this.ids = selection.map(item => item.roleId)
      this.single = selection.length!=1
      this.multiple = !selection.length
    },

    /** 修改按钮操作 */
    handleMemberShow(row) {
      this.reset();
      const gangsId = row.gangsId;
      getGangsMember(gangsId, this.addDateRange(this.queryParams, this.dateRange)).then(response => {
        this.gangsMemberList = response.rows;
        this.open = true;
        this.title = "查看帮派成员";
      });
    },

    /** 修改公告按钮操作 */
    handleModNotice(row) {
      this.form = row;
      this.titleNotice = "修改公告";
      this.gangsId = row.gangsId;
      this.openNotice = true;
    },

    // 取消公告按钮
    cancel() {
      this.openNotice = false;
      this.gangsId = 0;
      this.reset();
    },

    /** 提交公告按钮 */
    submitForm: function() {
      const gangId = this.gangsId;
      this.form.serverId = this.queryParams.serverId;
      this.$refs["modNotice"].validate(valid => {
        if (valid) {
          console.log(this.form);
          modifyGangsNotice(gangId, this.form).then(response => {
            this.$modal.msgSuccess("修改成功");
            this.openNotice = false;
            this.getList();
          });
        }
      });


    },

    /** 导出按钮操作 */
    handleExport() {
      this.download('gm/gangs/export', {
        ...this.queryParams
      }, `gangs_${new Date().getTime()}.xlsx`)
    }
  }
};
</script>
