<template>
  <div class="app-container">

    <el-row :gutter="10" class="mb8">
      <el-col :span="1.5">
        <el-button
          type="primary"
          plain
          icon="el-icon-plus"
          size="mini"
          @click="handleSendMail"
          v-hasPermi="['gm:mail:add']"
        >发送邮件</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
          type="danger"
          plain
          icon="el-icon-delete"
          size="mini"
          :disabled="multiple"
          @click="handleDelete"
          v-hasPermi="['gm:mail:remove']"
        >删除</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
          type="primary"
          plain
          icon="el-icon-check"
          size="mini"
          :disabled="multiple"
          @click="handlePassMail"
          v-hasPermi="['gm:mail:pass']"
        >通过</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
          type="primary"
          plain
          icon="el-icon-close"
          size="mini"
          :disabled="multiple"
          @click="handleRefuseMail"
          v-hasPermi="['gm:mail:pass']"
        >拒绝</el-button>
      </el-col>
      <!-- <el-col :span="1.5">
        <el-button
          type="warning"
          plain
          icon="el-icon-download"
          size="mini"
          @click="handleExport"
          v-hasPermi="['gm:mail:export']"
        >导出</el-button>
      </el-col> -->
      <right-toolbar :showSearch.sync="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <el-table v-loading="loading" :data="roleList" @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="55" align="center" />
      <el-table-column label="编号" prop="id" width="90" align="center"/>
      <el-table-column label="服务器" prop="serverList" :show-overflow-tooltip="true" width="120" >
        <template slot-scope="scope">
            <div v-html="getServerName(scope.row.serverList)"></div>
        </template>
      </el-table-column>
      <el-table-column label="邮件类型" prop="sendType" width="120" align="center">
        <template slot-scope="scope">
            <div v-html="setSendType(scope.row.sendType)"></div>
        </template>
      </el-table-column>
      <el-table-column label="邮件标题" prop="title" :show-overflow-tooltip="true" width="120" />
      <el-table-column label="邮件内容" prop="content" :show-overflow-tooltip="true" width="120" />
      <el-table-column label="附件内容" prop="items" :show-overflow-tooltip="true" >
        <template slot-scope="scope">
            <div v-html="getItemFormat(scope.row.items)"></div>
        </template>
      </el-table-column>
      <el-table-column label="邮件目标" prop="targetIds" width="180" align="center" />
      <el-table-column label="有效时间" align="center" prop="validTime" width="180">
        <template slot-scope="scope">
          <span>{{ parseTime(scope.row.validTime) }}</span>
        </template>
      </el-table-column>
      <el-table-column label="创建时间" align="center" prop="createTime" width="180">
        <template slot-scope="scope">
          <span>{{ parseTime(scope.row.createTime) }}</span>
        </template>
      </el-table-column>
      <el-table-column label="操作人" prop="createBy" width="120" />
      <el-table-column label="邮件状态" prop="mailStatus" width="80" align="center">
        <template slot-scope="scope">
            <div v-html="setMailStatus(scope.row.mailStatus)"></div>
        </template>
      </el-table-column>
      <el-table-column label="操作" align="center" class-name="small-padding fixed-width" width="150">
        <template slot-scope="scope" v-if="scope.row.mailStatus === undefined">
          <el-button
            size="mini"
            type="text"
            icon="el-icon-check"
            @click="handlePassMail(scope.row)"
            v-hasPermi="['gm:mail:pass']"
          >通过</el-button>
          <el-button
            size="mini"
            type="text"
            icon="el-icon-close"
            @click="handleRefuseMail(scope.row)"
            v-hasPermi="['gm:mail:pass']"
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

    <!-- 添加或修改角色配置对话框 -->
    <el-dialog :title="title" :visible.sync="open" width="600px" append-to-body>
      <el-form ref="form" :model="form" :rules="rules" label-width="80px">
        <el-row>
          <el-col :span="12">
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
          </el-col>
        </el-row>
        <el-row>
          <el-col :span="20">
           <el-form-item label="邮件标题" prop="title">
              <el-input v-model="form.title" placeholder="请输入邮件标题"/>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span="20">
            <el-form-item label="邮件内容" prop="content">
              <el-input v-model="form.content" placeholder="请输入邮件内容" type="textarea" resize="none" :autosize="{ minRows: 5, maxRows: 5}"></el-input>
              <!-- <textarea v-model="form.content" rows="5" cols="60" placeholder="请输入邮件内容" minlength="300" maxlength="2000"></textarea> -->
              <!-- <el-input v-model="form.phonenumber" placeholder="请输入手机号码" maxlength="11" /> -->
            </el-form-item>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span="12">
            <el-form-item label="邮件类型">
              <el-select v-model="form.sendType" placeholder="请选择邮件类型">
                <el-option
                  v-for="dict in dict.type.gm_mail_sendtype"
                  :key="dict.value"
                  :label="dict.label"
                  :value="dict.value"
                ></el-option>
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>
         <el-row>
          <el-col :span="24">
            <el-form-item v-if="form.sendType == 1" label="玩家ID" prop="targetIds">
              <el-input  v-model="form.targetIds" placeholder="请输入玩家ID" maxlength="2000" />
            </el-form-item>
            <el-form-item v-if="form.sendType == 2" label="帮派ID" prop="targetIds">
              <el-input  v-model="form.targetIds" placeholder="请输入帮派ID" maxlength="2000" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span="12">
            <el-form-item label="有效时间">
              <el-date-picker
                v-model="form.validTime"
                style="width: 240px"
                value-format="yyyy-MM-dd HH:mm:ss"
                type="datetime"
              ></el-date-picker>
            </el-form-item>
          </el-col>
        </el-row>

        <el-row v-for="(item, index) in form.dynamicItem" :key="index">
          <el-col :span="12">
            <el-form-item label="道具" :prop="'dynamicItem.' + index + '.itemId'">
              <el-select v-model="form.dynamicItem[index].refId" placeholder="请选择" filterable>
                <el-option
                  v-for="item in itemOptions"
                  :key="item.id"
                  :label="item.id + '_' +item.name"
                  :value="item.id"
                ></el-option>
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="数量" :prop="'dynamicItem.' + index + '.num'"
            :rules="[{ pattern: /\d/, message: '只能输入数字' }]">
              <el-input  v-model="form.dynamicItem[index].num" placeholder="请输入数量" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item>
              <el-button v-if="index+1 == form.dynamicItem.length" @click="handleAddItem" type="primary">增加</el-button>
              <el-button v-if="index !== 0" @click="handleDelItem(item, index)" type="danger">删除</el-button>
            </el-form-item>
          </el-col>
        </el-row>

      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button type="primary" @click="submitForm">确 定</el-button>
        <el-button @click="cancel">取 消</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import { listMail, delMail, addMail, updateMail, passMail } from "@/api/gameGm/mail";
import { listServerAll } from "@/api/gameGm/server";
import axios from "axios";
// import { treeselect as menuTreeselect, roleMenuTreeselect } from "@/api/system/menu";
// import { treeselect as deptTreeselect, roleDeptTreeselect } from "@/api/system/dept";

export default {
  name: "GmMail",
  dicts: ['gm_mail_sendtype'],
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
      // 查询参数
      queryParams: {
        pageNum: 1,
        pageSize: 20,
        // playerName: undefined,
        // playerId: undefined,
        // account: undefined,
        // channelId: undefined
      },
      // 表单参数
      form: {
        dynamicItem: [
          {
          refId: "",
          num: "",
        }
        ],
        servers:[],
        allServer: undefined,
      },
      defaultProps: {
        children: "children",
        label: "label"
      },
      // 表单校验
      rules: {
        server: [
          { required: true, message: "服务器不能为空", trigger: "blur" }
        ],
        title: [
          { required: true, message: "标题不能为空", trigger: "blur" }
        ],
        content: [
          { required: true, message: "内容不能为空", trigger: "blur" }
        ]
      },
      // colList: ["基本信息", "背包信息", "武将信息", "道具流水"],
      serverList: [],
      itemOptions:[
        {
          id : 1,
          name: "1",
        },
        {
          id : 2,
          name: "2",
        },
        {
          id : 3,
          name: "3",
        },
      ],
    };
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
  created() {
    this.getServerList();
    this.getList();
    this.getItemConfig();
  },
  methods: {
    /** 查询角色列表 */
    getList() {
      this.loading = true;
      listMail(this.addDateRange(this.queryParams, this.dateRange)).then(response => {
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

    getItemConfig() {
      axios.get("/json/Item.json").then(result => {
        this.itemOptions = result.data;
      });
    },

    setSendType(sendType) {
      switch (sendType) {
        case "1":
          return "指定玩家";
        case "2":
          return "指定联盟";
        case "3":
          return "全服";
        default:
          return "无"
      }
    },

    setMailStatus(mailStatus) {
      switch (mailStatus) {
        case "1":
          return "通过";
        case "2":
          return "拒绝";
        default:
      }
      return "待审核";
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

    getItemFormat(items) {
      if (items === undefined) {
        return "";
      }
      const itemArr = items.split("|");
      if (itemArr.length % 2 === 1) {
        itemArr.splice(itemArr.length - 1, 1);
      }
      let resultStr = "";
      for (let index = 0; index < itemArr.length; index+=2) {
        const element = itemArr[index];
        if (element === "" || element === 0) {
          continue;
        }
        const itemConst = this.itemOptions.find(item => item.id > 0 && item.id == element);
        if (itemConst === undefined) {
          continue;
        }
        resultStr += itemConst.name + "*" + itemArr[index + 1];
        if (index + 2 < itemArr.length) {
          resultStr += ",";
        }
      }
      return resultStr;
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
        servers: [],
        title: undefined,
        content: undefined,
        remark: undefined,
        allServer: undefined,
        dynamicItem: [
          {
          refId: "",
          num: "",
        }
        ],
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
      this.ids = selection.map(item => item.id)
      this.single = selection.length!=1
      this.multiple = !selection.length
    },

    /** 通过按钮操作 */
    handlePassMail(row) {
      this.reset();
      const ids = row.id || this.ids;
      this.$modal.confirm('是否确认通过邮件["' + ids + '"]？').then(function() {
        return passMail(ids, 1);
      }).then(() => {
        this.getList();
        this.$modal.msgSuccess("已通过");
      }).catch(() => {});
    },

    /** 拒绝按钮操作 */
    handleRefuseMail(row) {
      this.reset();
      const ids = row.id || this.ids;
      this.$modal.confirm('是否确认拒绝邮件["' + ids + '"]？').then(function() {
        return passMail(ids, 2);
      }).then(() => {
        this.getList();
        this.$modal.msgSuccess("已拒绝");
      }).catch(() => {});
    },

    /** 删除按钮操作 */
    handleSendMail() {
      // const roleIds = row.playerId || this.ids;
      this.open = true;
      this.reset();
      this.title = "发送邮件";
      // this.$modal.confirm('是否确认删除玩家ID为"' + roleIds + '"的数据项？').then(function() {
      //   return delRole(roleIds);
      // }).then(() => {
      //   this.getList();
      //   this.$modal.msgSuccess("删除成功");
      // }).catch(() => {});
    },

    /** 删除按钮操作 */
    handleDelete() {
      const userIds = this.ids;
      this.$modal.confirm('是否确认删除邮件编号为"' + userIds + '"的数据项？').then(function() {
        return delMail(userIds);
      }).then(() => {
        this.getList();
        this.$modal.msgSuccess("删除成功");
      }).catch(() => {});
    },

        /** 提交按钮 */
    submitForm: function() {
      this.$refs["form"].validate(valid => {
        if (valid) {
          if (this.form.id != undefined) {
            updateMail(this.form).then(response => {
              this.$modal.msgSuccess("修改成功");
              this.open = false;
              this.getList();
            });
          } else {
            console.log(this.form);
            addMail(this.form).then(response => {
              this.$modal.msgSuccess("新增成功");
              this.open = false;
              this.getList();
            });
          }
        }
      });
    },

    handleAddItem() {
      this.form.dynamicItem.push({
        refId: "",
        num: "",
      });
    },

    handleDelItem(item, index) {
      this.form.dynamicItem.splice(index, 1);
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

  }
};
</script>
