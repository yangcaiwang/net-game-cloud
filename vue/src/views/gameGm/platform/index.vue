<template>
  <div class="app-container">

    <el-row :gutter="10" class="mb8">
      <el-col :span="1.5">
        <el-button
          type="primary"
          plain
          icon="el-icon-plus"
          size="mini"
          @click="handleAdd"
          v-hasPermi="['gm:platform:add']"
        >新增</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
          type="danger"
          plain
          icon="el-icon-delete"
          size="mini"
          :disabled="multiple"
          @click="handleDelete"
          v-hasPermi="['gm:platform:remove']"
        >删除</el-button>
      </el-col>
      <right-toolbar :showSearch.sync="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <el-table v-loading="loading" :data="roleList" @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="55" align="center" />
      <el-table-column label="平台标识" prop="platformId" align="center" />
      <el-table-column label="平台名" prop="platformName" align="center" />
      <el-table-column label="平台排序" prop="sort" align="center" />
      <el-table-column label="开启白名单" align="center" width="100">
        <template slot-scope="scope">
          <el-switch
            v-model="scope.row.whiteListStatus"
            active-value="1"
            inactive-value="0"
            @change="handleWhiteStatusChange(scope.row)"
          ></el-switch>
        </template>
      </el-table-column>
      <el-table-column label="自动关闭注册" align="center" width="100">
        <template slot-scope="scope">
          <el-switch
            v-model="scope.row.autoRegisterSwitch"
            :active-value=1
            :inactive-value=0
            @change="changeAutoRegisterSwitch(scope.row)"
          ></el-switch>
        </template>
      </el-table-column>
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
            icon="el-icon-edit"
            @click="handleUpdate(scope.row)"
            v-hasPermi="['gm:platform:edit']"
          >修改</el-button>
          <el-button
            size="mini"
            type="text"
            icon="el-icon-delete"
            @click="handleDelete(scope.row)"
            v-hasPermi="['gm:player:remove']"
          >删除</el-button>
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

    <!-- 添加或修改角色配置对话框 -->
    <el-dialog :title="title" :visible.sync="open" width="500px" append-to-body>
      <el-form ref="form" :model="form" :rules="rules" label-width="100px">
        <el-form-item v-if="!showPlatformKey" label="平台标识" prop="platformId">
          <el-input v-model="form.platformId" placeholder="请输入平台标识" />
        </el-form-item>
        <el-form-item label="平台名" prop="platformName">
          <el-input v-model="form.platformName" placeholder="请输入平台名" />
        </el-form-item>
        <el-form-item label="平台排序" prop="sort">
          <el-input v-model="form.sort" placeholder="请输入平台排序" />
        </el-form-item>
        <el-form-item label="开启白名单">
          <el-radio-group v-model="form.whiteListStatus">
            <el-radio
              v-for="dict in dict.type.white_list_status"
              :key="dict.value"
              :label="dict.value"
            >{{dict.label}}</el-radio>
          </el-radio-group>
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
  addPlatform,
  changeAutoRegisterSwitch,
  changeWhiteListStatus,
  delPlatform,
  getPlatform,
  listPlatform,
  updatePlatform
} from "@/api/gameGm/platform";

export default {
  name: "Platform",
  dicts: ['white_list_status'],
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
      // openDataScope: false,
      // menuExpand: false,
      // menuNodeAll: false,
      // deptExpand: true,
      // deptNodeAll: false,
      // 日期范围
      dateRange: [],
      // 菜单列表
      // menuOptions: [],
      // 部门列表
      // deptOptions: [],
      // 查询参数
      queryParams: {
        pageNum: 1,
        pageSize: 20,
        platformId: undefined,
        platformName: undefined,
        whiteListStatus: undefined
      },
      // 表单参数
      form: {},
      defaultProps: {
        children: "children",
        label: "label"
      },
      showPlatformKey: false,
      // 1=新增 2=修改
      currOpt: 0,
      // 表单校验
      rules: {
        platformId: [
          { required: true, message: "平台标识不能为空", trigger: "blur" }
        ],
        platformName: [
          { required: true, message: "平台名不能为空", trigger: "blur" }
        ],
      },
    };
  },
  created() {
    this.getList();
  },
  methods: {
    /** 查询角色列表 */
    getList() {
      this.loading = true;
      listPlatform(this.addDateRange(this.queryParams, this.dateRange)).then(response => {
          this.roleList = response.rows;
          this.total = response.total;
          this.loading = false;
        }
      );
    },

    // 白名单状态修改
    handleWhiteStatusChange(row) {
      let text = row.whiteListStatus === "0" ? "关闭" : "开启";
       this.$modal.confirm('确认要"' + text + '""' + row.platformName + '"白名单状态吗？').then(function() {
        return changeWhiteListStatus(row.platformId, row.whiteListStatus);
      }).then(() => {
        this.$modal.msgSuccess(text + "成功");
      }).catch(function() {
        row.whiteListStatus = row.whiteListStatus === "0" ? "1" : "0";
      });
    },

    // 是否自动关闭注册修改
    changeAutoRegisterSwitch(row) {
      let text = row.autoRegisterSwitch === 0 ? "关闭" : "开启";
       this.$modal.confirm('确认要"' + text + '""' + row.platformName + '"自动角色注册开关吗？').then(function() {
        return changeAutoRegisterSwitch(row.platformId, row.autoRegisterSwitch);
      }).then(() => {
        this.$modal.msgSuccess(text + "成功");
      }).catch(function() {
        row.autoRegisterSwitch = row.autoRegisterSwitch === 1 ? 0 : 1;
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
        platformId: undefined,
        platformName: undefined,
        sort: undefined,
        whiteListStatus: "0",
        remark: undefined
      };
      this.resetForm("form");
      this.currOpt = 0;
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

    /** 新增按钮操作 */
    handleAdd() {
      this.reset();
      // this.getMenuTreeselect();
      this.open = true;
      this.title = "添加平台";
      this.currOpt = 1;
    },

    /** 修改按钮操作 */
    handleUpdate(row) {
      this.reset();
      this.currOpt = 2;
      this.showPlatformKey = true;
      const roleId = row.platformId || this.ids
      getPlatform(roleId).then(response => {
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
        this.title = "修改平台";
      });
    },

     /** 提交按钮 */
    submitForm: function() {
      this.$refs["form"].validate(valid => {
        if (valid) {
          if (this.form.platformId != undefined && this.currOpt == 2) {
            // this.form.menuIds = this.getMenuAllCheckedKeys();
            updatePlatform(this.form).then(response => {
              this.$modal.msgSuccess("修改成功");
              this.open = false;
              this.getList();
            });
          } else {
            // this.form.menuIds = this.getMenuAllCheckedKeys();
            addPlatform(this.form).then(response => {
              this.$modal.msgSuccess("新增成功");
              this.open = false;
              this.getList();
            });
          }
        }
      });
    },
    /** 提交按钮（数据权限） */
    // submitDataScope: function() {
    //   if (this.form.roleId != undefined) {
    //     this.form.deptIds = this.getDeptAllCheckedKeys();
    //     dataScope(this.form).then(response => {
    //       this.$modal.msgSuccess("修改成功");
    //       this.openDataScope = false;
    //       this.getList();
    //     });
    //   }
    // },

    /** 删除按钮操作 */
    handleDelete(row) {
      const roleIds = row.platformId || this.ids;
      this.$modal.confirm('是否确认删除平台ID为"' + roleIds + '"的数据项？').then(function() {
        return delPlatform(roleIds);
      }).then(() => {
        this.getList();
        this.$modal.msgSuccess("删除成功");
      }).catch(() => {});
    },
  }
};
</script>
