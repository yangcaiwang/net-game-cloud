<template>
  <div class="app-container">
    <el-form :model="queryParams" ref="queryForm" size="small" :inline="true" v-show="showSearch" label-width="68px">
      <el-form-item label="服务器" prop="serverId">
        <el-select
          v-model="queryParams.serverId"
          placeholder="选择服务器"
          multiple
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
          v-hasPermi="['gm:item:add']"
        >新增</el-button>
      </el-col>
      <!-- <el-col :span="1.5">
        <el-button
          type="success"
          plain
          icon="el-icon-edit"
          size="mini"
          :disabled="single"
          @click="handleUpdate"
          v-hasPermi="['gm:item:edit']"
        >修改</el-button>
      </el-col> -->
      <el-col :span="1.5">
        <el-button
          type="danger"
          plain
          icon="el-icon-delete"
          size="mini"
          :disabled="multiple"
          @click="handleDelete"
          v-hasPermi="['gm:item:remove']"
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
          v-hasPermi="['gm:item:pass']"
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
          v-hasPermi="['gm:item:pass']"
        >拒绝</el-button>
      </el-col>
      <!-- <el-col :span="1.5">
        <el-button
          type="warning"
          plain
          icon="el-icon-download"
          size="mini"
          @click="handleExport"
          v-hasPermi="['gm:item:export']"
        >导出</el-button>
      </el-col> -->
      <right-toolbar :showSearch.sync="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <el-table v-loading="loading" :data="itemList" @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="55" align="center" />
      <el-table-column label="唯一id" align="center" prop="id" />
      <el-table-column label="服务器" align="center" prop="serverId" >
        <template slot-scope="scope">
            <div v-html="getServerName(scope.row.serverId)"></div>
        </template>
      </el-table-column>
      <!-- <el-table-column label="服务器名" align="center" prop="serverName" /> -->
      <!-- <el-table-column label="操作类型" align="center" prop="addType" /> -->
      <el-table-column label="玩家" align="center" prop="roleIds" />
      <el-table-column label="道具" align="center" prop="item" :show-overflow-tooltip="true" >
        <template slot-scope="scope">
            <div v-html="getItemFormat(scope.row.item)"></div>
        </template>
      </el-table-column>
      <!-- <el-table-column label="道具显示" align="center" prop="itemShow" /> -->
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
        <template slot-scope="scope" >
          <el-button
            size="mini"
            type="text"
            icon="el-icon-edit"
            @click="handleUpdate(scope.row)"
            v-hasPermi="['gm:item:edit']"
            v-if="scope.row.status === undefined || scope.row.status === 0 "
          >修改</el-button>
          <el-button
            size="mini"
            type="text"
            icon="el-icon-delete"
            @click="handleDelete(scope.row)"
            v-hasPermi="['gm:item:remove']"
          >删除</el-button>
          <el-button
            size="mini"
            type="text"
            icon="el-icon-edit"
            @click="handlePass(scope.row)"
            v-hasPermi="['gm:item:pass']"
            v-if="scope.row.status === undefined || scope.row.status === 0 "
          >通过</el-button>
          <el-button
            size="mini"
            type="text"
            icon="el-icon-delete"
            @click="handleRefuse(scope.row)"
            v-hasPermi="['gm:item:pass']"
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

    <!-- 添加或修改游戏管理  添加道具对话框 -->
    <el-dialog :title="title" :visible.sync="open" width="500px" append-to-body>
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
        <!-- <el-form-item label="服务器" prop="serverId">
          <el-input v-model="form.serverId" placeholder="请输入服务器" />
        </el-form-item> -->
        <el-form-item label="玩家" prop="roleIds">
          <el-input v-model="form.roleIds" placeholder="请输入玩家" />
        </el-form-item>
        <!-- <el-form-item label="道具" prop="item">
          <el-input v-model="form.item" placeholder="请输入道具" />
        </el-form-item> -->
        <!-- <el-form-item label="道具显示" prop="itemShow">
          <el-input v-model="form.itemShow" placeholder="请输入道具显示" />
        </el-form-item> -->

         <el-row v-for="(item, index) in form.dynamicItem" :key="index">
          <el-col :span="12">
            <el-form-item label="道具" :prop="'dynamicItem.' + index + '.itemId'">
              <el-select v-model="form.dynamicItem[index].refId" placeholder="请选择" filterable>
                <el-option
                  v-for="item in itemOptions"
                  :key="item.id"
                  :label="item.id + '_' + item.name"
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
import { listItem, getItem, delItem, addItem, updateItem, pass } from "@/api/gameGm/item";
import { listServerAll } from "@/api/gameGm/server";
import axios from "axios";

export default {
  name: "Item",
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
      // 游戏管理  添加道具表格数据
      itemList: [],
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
        dynamicItem: [
          {
          refId: "",
          num: "",
          }
        ],
        servers:[],
        allServer: undefined,
      },
      // 表单校验
      rules: {
        addType: [
          { required: true, message: "操作类型不能为空", trigger: "change" }
        ],
        status: [
          { required: true, message: "状态不能为空", trigger: "blur" }
        ],
      },
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
    this.getList();
    this.getServerList();
    this.getItemConfig();
  },
  methods: {
    /** 查询游戏管理  添加道具列表 */
    getList() {
      this.loading = true;
      listItem(this.queryParams).then(response => {
        this.itemList = response.rows;
        this.total = response.total;
        this.loading = false;
      });
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
        addType: null,
        roleIds: null,
        item: null,
        status: 0,

        dynamicItem: [
          {
          refId: "",
          num: "",
          }
        ],
        servers:[],
        allServer: undefined,
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
      this.title = "添加道具";
    },
    /** 修改按钮操作 */
    handleUpdate(row) {
      this.reset();
      const dnItem = this.form.dynamicItem;
      const id = row.id || this.ids
      getItem(id).then(response => {
        this.form = response.data;
        if (this.form.servers === undefined) {
          this.form.servers = [];
        }
        if (this.form.servers.includes("-1")) {
          this.form.servers = this.serverList.map(d => d.serverKeyId);
        }
        if (this.form.dynamicItem === undefined) {
          this.form.dynamicItem = dnItem;
        }
        this.open = true;
        this.title = "修改道具";
      });
    },
    /** 提交按钮 */
    submitForm() {
      this.$refs["form"].validate(valid => {
        if (valid) {
          if (this.form.id != null) {
            updateItem(this.form).then(response => {
              this.$modal.msgSuccess("修改成功");
              this.open = false;
              this.getList();
            });
          } else {
            addItem(this.form).then(response => {
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
      this.$modal.confirm('是否确认删除道具编号为"' + ids + '"的数据项？').then(function() {
        return delItem(ids);
      }).then(() => {
        this.getList();
        this.$modal.msgSuccess("删除成功");
      }).catch(() => {});
    },
    /** 导出按钮操作 */
    handleExport() {
      this.download('gameGm/item/export', {
        ...this.queryParams
      }, `item_${new Date().getTime()}.xlsx`)
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
  }
};
</script>
