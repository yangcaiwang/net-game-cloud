<template>
  <div class="app-container home">
    <el-row :gutter="20">
      <el-col :sm="24" :lg="24">

        <hr />
      </el-col>
    </el-row>
    <el-row :gutter="20">

      <!-- <el-col :sm="24" :lg="12" style="padding-left: 50px">
        <el-row>
          <el-col :span="12">
            <h2>技术选型</h2>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span="6">
            <h4>后端技术</h4>
            <ul>
              <li>SpringBoot</li>
              <li>Spring Security</li>
              <li>JWT</li>
              <li>MyBatis</li>
              <li>Druid</li>
              <li>Fastjson</li>
              <li>...</li>
            </ul>
          </el-col>
          <el-col :span="6">
            <h4>前端技术</h4>
            <ul>
              <li>Vue</li>
              <li>Vuex</li>
              <li>Element-ui</li>
              <li>Axios</li>
              <li>Sass</li>
              <li>Quill</li>
              <li>...</li>
            </ul>
          </el-col>
        </el-row>
      </el-col> -->
    </el-row>
    <el-divider />
    <el-row :gutter="20">
      <el-col :xs="24" :sm="24" :md="12" :lg="8">
        <el-card class="update-log">
          <!-- <div slot="header" class="clearfix">
            <span>联系信息</span>
          </div> -->
          <div class="body">
            <!-- <p>
              <i class="el-icon-s-promotion"></i> 官网：<el-link
                href="http://www.ruoyi.vip"
                target="_blank"
                >http://www.ruoyi.vip</el-link
              >
            </p>
            <p>
              <i class="el-icon-user-solid"></i> QQ群：<s>满937441</s>
              <s>满887144332</s> <s>满180251782</s> <s>满104180207</s> <s>满186866453</s>
              <s>满201396349</s> <s>满101456076</s> <s>满101539465</s> <s>满264312783</s>
              <a href="https://jq.qq.com/?_wv=1027&k=SWCtLnMz" target="_blank">
                167385320</a
              >
            </p>
            <p>
              <i class="el-icon-chat-dot-round"></i> 微信：<a
                href="javascript:;"
                >/ *若依</a
              >
            </p>
            <p>
              <i class="el-icon-money"></i> 支付宝：<a
                href="javascript:;"
                class="支付宝信息"
                >/ *若依</a
              >
            </p> -->
          </div>
        </el-card>
      </el-col>
      <!-- <el-col :xs="24" :sm="24" :md="12" :lg="8">
        <el-card class="update-log">
          <div slot="header" class="clearfix">
            <span>更新日志</span>
          </div>
          <el-collapse accordion>
            <el-collapse-item title="v1.0.0 - 2019-10-08">
              <ol>
                <li>前后端分离系统</li>
              </ol>
            </el-collapse-item>
          </el-collapse>
        </el-card>
      </el-col> -->
      <el-col :xs="24" :sm="24" :md="12" :lg="8">
        <!-- <el-card class="update-log">
          <div slot="header" class="clearfix">
            <span>捐赠支持</span>
          </div>
          <div class="body">
            <img
              src="https://oscimg.oschina.net/oscnet/up-d6695f82666e5018f715c41cb7ee60d3b73.png"
              alt="donate"
              width="100%"
            />
            <span style="display: inline-block; height: 30px; line-height: 30px"
              >你可以请作者喝杯咖啡表示鼓励</span
            >
          </div>
        </el-card> -->
      </el-col>
    </el-row>
  </div>
</template>

<script>
export default {
  name: "Index",
  data() {
    return {
      // 版本号
      version: "1.0.1",
    };
  },
  methods: {
    goTarget(href) {
      window.open(href, "_blank");
    },
  },
};
</script>

<style scoped lang="scss">
.home {
  blockquote {
    padding: 10px 20px;
    margin: 0 0 20px;
    font-size: 17.5px;
    border-left: 5px solid #eee;
  }
  hr {
    margin-top: 20px;
    margin-bottom: 20px;
    border: 0;
    border-top: 1px solid #eee;
  }
  .col-item {
    margin-bottom: 20px;
  }

  ul {
    padding: 0;
    margin: 0;
  }

  font-family: "open sans", "Helvetica Neue", Helvetica, Arial, sans-serif;
  font-size: 13px;
  color: #676a6c;
  overflow-x: hidden;

  ul {
    list-style-type: none;
  }

  h4 {
    margin-top: 0px;
  }

  h2 {
    margin-top: 10px;
    font-size: 26px;
    font-weight: 100;
  }

  p {
    margin-top: 10px;

    b {
      font-weight: 700;
    }
  }

  .update-log {
    ol {
      display: block;
      list-style-type: decimal;
      margin-block-start: 1em;
      margin-block-end: 1em;
      margin-inline-start: 0;
      margin-inline-end: 0;
      padding-inline-start: 40px;
    }
  }
}
</style>

