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
          v-hasPermi="['gm:activity:add']"
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
          v-hasPermi="['gm:activity:edit']"
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
          v-hasPermi="['gm:activity:remove']"
        >删除</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
          type="warning"
          plain
          icon="el-icon-download"
          size="mini"
          @click="handleExport"
          v-hasPermi="['gm:activity:export']"
        >导出</el-button>
      </el-col>
      <right-toolbar :showSearch.sync="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <el-table v-loading="loading" :data="openList" @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="55" align="center" />
      <el-table-column label="唯一id" align="center" prop="id" />
      <el-table-column label="服务器" align="center" prop="serverId" :show-overflow-tooltip="true" >
        <template slot-scope="scope">
            <div v-html="getServerName(scope.row.serverId)"></div>
        </template>
      </el-table-column>
      <el-table-column label="活动" align="center" prop="actId" >
        <template slot-scope="scope">
          <div v-html="getActName(scope.row.actId)"></div>
        </template>
      </el-table-column>
      <el-table-column label="活动类型" align="center" prop="actType" >
        <template slot-scope="scope">
          <dict-tag :options="dict.type.activity_open_type" :value="scope.row.actType"/>
        </template>
      </el-table-column>
      <el-table-column label="活动时间" align="center" prop="actTime" />
      <el-table-column label="开服不开天数" align="center" prop="notOpenDay" />
      <el-table-column label="强制结束活动" align="center" prop="forceEnd" />
      <el-table-column label="开启渠道" align="center" prop="openChannel" />
      <el-table-column label="开启时间点（小时）" align="center" prop="openHour" />
      <el-table-column label="状态" align="center" prop="status">
        <template slot-scope="scope">
          <el-switch
            v-model="scope.row.status"
            :active-value=1
            :inactive-value=0
            @change="handleStatusChange(scope.row)"
          ></el-switch>
        </template>
      </el-table-column>
      <el-table-column label="创建人" align="center" prop="createBy" width="180"/>
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
            v-hasPermi="['gm:activity:edit']"
          >修改</el-button>
          <el-button
            size="mini"
            type="text"
            icon="el-icon-delete"
            @click="handleDelete(scope.row)"
            v-hasPermi="['gm:activity:remove']"
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

    <!-- 添加或修改运营活动开启时间对话框 -->
    <el-dialog :title="title" :visible.sync="open" width="500px" append-to-body>
      <el-form ref="form" :model="form" :rules="rules" label-width="120px">
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
        <el-form-item label="活动id" prop="actId">
          <el-select
            v-model="form.actId"
            placeholder="选择活动ID"
            style="width: 240px"
            filterable
          >
            <el-option
              v-for="activity in activityConfigs"
              :key="activity.id"
              :label="activity.id + '_' + activity.name"
              :value="activity.id"
            />
          </el-select>
          <!-- <el-input v-model="form.actId" placeholder="请输入活动id" /> -->
        </el-form-item>
        <el-form-item label="活动类型" prop="actType">
          <el-select
              v-model="form.actType"
              placeholder="选择活动类型"
              style="width: 240px"
            >
              <el-option
                v-for="openType in dict.type.activity_open_type"
                :key="openType.value"
                :label="openType.label"
                :value="openType.value"
              />
            </el-select>
        </el-form-item>
        <el-form-item label="开始时间" >
            <el-date-picker
              v-model="form.startTime"
              style="width: 240px"
              value-format="yyyy-MM-dd HH:mm:ss"
              type="datetime"
              v-if="form.actType == 3"
            ></el-date-picker>
            <el-input v-model="form.startTime" placeholder="请输入活动时间" v-if="form.actType != 3"/>
        </el-form-item>
        <el-form-item label="活动持续时间" >
          <span slot="label">
            <el-tooltip content="活动持续时间,天数,多个参数用‘|’隔开" placement="top">
              <i class="el-icon-question"></i>
            </el-tooltip>
            活动持续时间
          </span>
          <el-input v-model="form.actDays" placeholder="请输入活动时间" />
        </el-form-item>
        <el-form-item label="多开活动类型" v-if="form.actType == 5">
          <el-select
              v-model="form.mulActType"
              placeholder="选择活动类型"
              style="width: 240px"
            >
            <div v-for="optType in dict.type.activity_open_type" :key="optType.value">
              <el-option :label="optType.label" :value="optType.value" v-if="optType.value == 2 || optType.value == 3"/>
            </div>
              <!-- <el-option
                v-for="openType in dict.type.activity_open_type"
                :key="openType.value"
                :label="openType.label"
                :value="openType.value"
                
              /> -->
          </el-select>
          <!-- <span slot="label">
            <el-tooltip content="多开活动类型" placement="top">
              <i class="el-icon-question"></i>
            </el-tooltip>
            多开活动类型
          </span>
          <el-input v-model="form.mulActType" placeholder="请输入多开活动类型" /> -->
        </el-form-item>
        <el-form-item label="多开活动期" v-if="form.actType == 5">
          <span slot="label">
            <el-tooltip content="多开活动期" placement="top">
              <i class="el-icon-question"></i>
            </el-tooltip>
            多开活动期
          </span>
          <el-input v-model="form.roundNum" placeholder="请输入多开活动期" />
        </el-form-item>
        <el-form-item label="开服不开天数" prop="notOpenDay">
          <span slot="label">
            <el-tooltip content="开服前多少天内不会开启活动" placement="top">
              <i class="el-icon-question"></i>
            </el-tooltip>
            开服不开天数
          </span>
          <el-input v-model="form.notOpenDay" placeholder="请输入开服不开天数" />
        </el-form-item>
        <el-form-item label="开启时间点" prop="notOpenDay">
          <span slot="label">
            <el-tooltip content="开启当天几点开启" placement="top">
              <i class="el-icon-question"></i>
            </el-tooltip>
            开启时间点（小时）
          </span>
          <el-input v-model="form.openHour" placeholder="请输入开启时间点" />
        </el-form-item>
        <el-form-item label="开启渠道" prop="openChannel">
          <span slot="label">
            <el-tooltip content="当存在此渠道时,则只会在对应的渠道开启活动,其他渠道不开,如果不限制渠道,则不需要填渠道信息" placement="top">
              <i class="el-icon-question"></i>
            </el-tooltip>
            开启渠道
          </span>
          <el-input v-model="form.openChannel" placeholder="请输入开启渠道" />
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
import { listOpen, getOpen, delOpen, addOpen, updateOpen, changeStatus } from "@/api/gameGm/activityOpen";
import { listServerAll } from "@/api/gameGm/server";
import axios from "axios";

export default {
  name: "Open",
  dicts: ['sys_normal_disable','activity_open_type'],
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
      // 运营活动开启时间表格数据
      openList: [],
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
        actId: [
          { required: true, message: "活动id不能为空", trigger: "blur" }
        ],
        actType: [
          { required: true, message: "活动类型不能为空", trigger: "change" }
        ],
        status: [
          { required: true, message: "状态不能为空", trigger: "blur" }
        ],
      },
      serverList: [],
      activityConfigs: {},
    };
  },
  created() {
    this.getList();
    this.getServerList();
    this.getActivityConfig();
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
    /** 查询运营活动开启时间列表 */
    getList() {
      this.loading = true;
      listOpen(this.queryParams).then(response => {
        this.openList = response.rows;
        this.total = response.total;
        this.loading = false;
        console.log(this.openList);
      });
    },

    getServerList() {
      listServerAll().then(response => {
        this.serverList = response.rows;
      });
    },

    getActivityConfig() {
      axios.get("/json/ActivityOpen.json").then(result => {
        this.activityConfigs = result.data;
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
        actId: null,
        actType: null,
        actTime: null,
        notOpenDay: null,
        forceEnd: null,
        openChannel: null,
        openHour: 0,
        status: 0,
        serverId: null,
        startTime: null,
        actDays: '',
        servers:[],
        allServer: undefined,
        mulActType: undefined,
        roundNum: 0,
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
      this.title = "活动开启时间";
    },
    /** 修改按钮操作 */
    handleUpdate(row) {
      this.reset();
      const id = row.id || this.ids
      getOpen(id).then(response => {
        var actStr = this.form.actDays;
        console.log(response.data);
        this.form.id =  response.data.id;
        this.form.actId = response.data.actId;
        this.form.actType = response.data.actType;
        this.form.notOpenDay = response.data.notOpenDay;
        this.form.openChannel = response.data.openChannel;
        this.form.openHour = response.data.openHour;
        this.form.actId = response.data.actId;
        this.form.servers = response.data.servers;
        this.form.allServer = response.data.allServer;
        if (this.form.servers === undefined) {
          this.form.servers = [];
        }
        if (response.data.servers.includes("-1")) {
          this.form.servers = this.serverList.map(d => d.serverKeyId);
        }
        const arr = response.data.actTime.split("|");
        if (this.form.actType === "3") {
          this.form.startTime = new Date(arr[0] * 1000);
          this.form.actDays = arr[1];
        } else if (this.form.actType === "5") {
          this.form.startTime = arr[0];
          console.log(arr);
          for (let index = 1; index < arr.length; index++) {
            const element = arr[index];
            if (index == 1) {
              this.form.actDays = element;
            } else if (index == 2) {
              this.form.mulActType = element;
            } else if (index == 3) {
              this.form.roundNum = element;
            }
            // actStr += element;
            // if (index != arr.length - 1) {
            //   actStr += "|";
            // }
          }
          // actStr += "|";
          // actStr += this.form.mulActType;
          // actStr += "|";
          // actStr += this.form.roundNum;
          // this.form.actDays = actStr;
        } else {
          this.form.startTime = arr[0];
          console.log(arr);
          for (let index = 1; index < arr.length; index++) {
            const element = arr[index];
            actStr += element;
            if (index != arr.length - 1) {
              actStr += "|";
            }
          }
          this.form.actDays = actStr;

        }
        this.open = true;
        this.title = "活动开启时间";
      });
    },
    /** 提交按钮 */
    submitForm() {
      if (this.form.actType === "3") {
        const date = new Date(this.form.startTime);
        console.log(date);
        const time = date.getTime() / 1000;
        this.form.actTime = time + "|" + this.form.actDays;
      } else if (this.form.actType === "5") {
        this.form.actTime = this.form.startTime + "|" + this.form.actDays + "|" + this.form.mulActType + "|" + this.form.roundNum;
      } else {
        this.form.actTime = this.form.startTime + "|" + this.form.actDays;
      }
      this.$refs["form"].validate(valid => {
        if (valid) {
          if (this.form.id != null) {
            updateOpen(this.form).then(response => {
              this.$modal.msgSuccess("修改成功");
              this.open = false;
              this.getList();
            });
          } else {
            addOpen(this.form).then(response => {
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
      this.$modal.confirm('是否确认删除活动开启时间编号为"' + ids + '"的数据项？').then(function() {
        return delOpen(ids);
      }).then(() => {
        this.getList();
        this.$modal.msgSuccess("删除成功");
      }).catch(() => {});
    },
    /** 导出按钮操作 */
    handleExport() {
      this.download('activity/open/export', {
        ...this.queryParams
      }, `open_${new Date().getTime()}.xlsx`)
    },

    // 活动状态修改
    handleStatusChange(row) {
      let text = row.status == 1 ? "开启" : "关闭";
      this.$modal.confirm('确认要"' + text + '""' + row.actId + '"活动吗？').then(function() {
        return changeStatus(row.id, row.status);
      }).then(() => {
        this.$modal.msgSuccess(text + "成功");
      }).catch(function() {
        row.status = row.status == 1 ? 0 : 1;
      });
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
      if (serverList.includes("-1") || serverList === "-1" || serverList === -1) {
        return "ALL";
      } else {
        if (serverList !== undefined && serverList !== "") {
          return this.serverList.filter(item => serverList.includes(item.serverKeyId)).map(d => d.serverId + '_' + d.serverName).join(",");
        }
      }
      return "";
    },

    getActName(actId) {
      if (actId === undefined) {
        return 0;
      }
      const activityConfig = this.activityConfigs.find(item => item.id == actId);
      if (activityConfig === undefined) {
        return actId;
      }
      return actId + "_" + activityConfig.name;
    },
  }
};
</script>
