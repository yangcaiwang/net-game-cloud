<template>
  <div class="app-container">
    <el-container>
      <el-main style="width:40%">
          <el-form ref="form" :model="form" :rules="rules" label-width="80px">
            <el-form-item label="服务器" prop="serverId">
              <el-select
                v-model="form.serverId"
                placeholder="选择服务器"
                clearable
                style="width: 240px"
              >
              <!-- <el-option
                v-for="item in serverList"
                :key="item.serverKeyId"
                :label= "item.serverId + '_'+ item.serverName"
                :value="item.serverKeyId"
              /> -->
              <el-option
                key="1"
                label= "内网-142-三国"
                value="142"
              />
              <!-- <el-option
                key="2"
                label= "内网-145-单服"
                value="2"
              />
              <el-option
                key="3"
                label= "内网-145-中心服"
                value="3"
              />
              <el-option
                key="3"
                label= "内网-145-地图服"
                value="3"
              /> -->
              </el-select>
            </el-form-item>
            <el-form-item label="操作" prop="opt">
              <el-select
                v-model="form.opt"
                placeholder="选择操作"
                clearable
                style="width: 240px"
              >
              <!-- <el-option
                v-for="item in serverList"
                :key="item.serverKeyId"
                :label= "item.serverId + '_'+ item.serverName"
                :value="item.serverKeyId"
              /> -->
              <el-option
                key="1"
                label= "更新配置"
                value="1"
              />
              <!-- <el-option
                key="2"
                label= "更新&重启"
                value="2"
              /> -->

              </el-select>
            </el-form-item>
          </el-form>
          <el-button type="primary" @click="submitForm">确 定</el-button>
            <el-button @click="cancel">取 消</el-button>
        </el-main>
        <el-main style="width:60%">
          <p style="text-align:left;color: blue;">操作日志</p>
          <div v-html="getResult()" :style="{'max-height': timeLineHeight + 'px' }" style="overflow-y:scroll;"></div>
        </el-main>
    </el-container>

  </div>
</template>

<script>
// import { listBroadcast, getBroadcast, delBroadcast, addBroadcast, updateBroadcast, pass } from "@/api/gameGm/broadcast";
// import { listServerAll } from "@/api/gameGm/server";
import {commitOpt,} from "@/api/devTools/devTools";

export default {
  name: "UpdateCnf",
  mounted() {
    this.timeLineHeight = document.documentElement.clientHeight - 210;
    window.onresize = () => {
      this.timeLineHeight = document.documentElement.clientHeight - 210;
    };
  },
  data() {
    return {
      // 遮罩层
      loading: true,
      // 弹出层标题
      title: "",
      // 是否显示弹出层
      open: false,
      // 表单参数
      form: {
      },
      // 表单校验
      rules: {
      },
      executeResult: [],
      timeLineHeight: "",
    };
  },
  created() {
    // this.getList();
    // this.getServerList();
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
    /** 提交按钮 */
    submitForm() {
      this.$refs["form"].validate(valid => {
        if (valid) {
          commitOpt(this.form).then(resp => {
            if (resp.code == 200) {
              if(this.executeResult.length >= 5) {
                this.executeResult.splice(0, 1);
              }
              this.$message.success(resp.msg);
              this.executeResult.push(resp.data);
            }
          });
        }
      });
    },

    getResult() {
      var str = "";
      for (let index = 0; index < this.executeResult.length; index++) {
        const element = this.executeResult[index];
        str += element;
      }
      return str;
    }

  }
};
</script>
