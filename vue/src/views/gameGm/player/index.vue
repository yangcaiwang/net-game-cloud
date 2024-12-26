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
          :label= "item.serverId + '_'+ item.serverName"
          :value="item.serverKeyId"
        />
        </el-select>
      </el-form-item>
      <el-form-item label="玩家ID" prop="roleId">
        <el-input
          v-model="queryParams.roleId"
          placeholder="请输入玩家ID"
          clearable
          style="width: 240px"
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="玩家名" prop="roleName">
        <el-input
          v-model="queryParams.roleName"
          placeholder="请输入玩家名"
          clearable
          style="width: 240px"
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="账号" prop="account">
        <el-input
          v-model="queryParams.account"
          placeholder="请输入账号"
          clearable
          style="width: 240px"
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="渠道" prop="channelId">
        <el-input
          v-model="queryParams.channelId"
          placeholder="请输入渠道"
          clearable
          style="width: 240px"
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <!-- <el-form-item label="创建时间">
        <el-date-picker
          v-model="dateRange"
          style="width: 240px"
          value-format="yyyy-MM-dd"
          type="daterange"
          range-separator="-"
          start-placeholder="开始日期"
          end-placeholder="结束日期"
        ></el-date-picker>
      </el-form-item> -->
      <el-form-item>
        <el-button type="primary" icon="el-icon-search" size="mini" @click="handleQuery">搜索</el-button>
        <el-button icon="el-icon-refresh" size="mini" @click="resetQuery">重置</el-button>
      </el-form-item>
    </el-form>

    <el-row :gutter="10" class="mb8">
      <el-col :span="1.5">
        <el-button
          type="danger"
          plain
          icon="el-icon-delete"
          size="mini"
          :disabled="multiple"
          @click="handleDelete"
          v-hasPermi="['gm:player:remove']"
        >删除玩家</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
          type="warning"
          plain
          icon="el-icon-download"
          size="mini"
          @click="handleExport"
          v-hasPermi="['gm:player:export']"
        >导出</el-button>
      </el-col>
      <right-toolbar :showSearch.sync="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <el-table v-loading="loading" :data="roleList" @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="55" align="center" />
      <el-table-column label="玩家ID" prop="roleId" align="center" width="180" show-overflow-tooltip />
      <el-table-column label="玩家名" prop="roleName" align="center" width="120" show-overflow-tooltip />
      <el-table-column label="账号" prop="account" align="center" width="120" show-overflow-tooltip />
      <el-table-column label="渠道" prop="channelId" align="center" width="90" />
      <el-table-column label="战力" prop="power" align="center" width="110" />
      <el-table-column label="等级" prop="level" width="60" />
      <el-table-column label="经验" prop="exp" align="center" width="120" show-overflow-tooltip />
      <el-table-column label="头像" prop="headResId" width="90" />
      <el-table-column label="爵位" prop="titleLvResId" align="center" width="50" />
      <el-table-column label="在线" align="center" width="60">
        <template slot-scope="scope">
          <el-switch
            v-model="scope.row.online"
            active-value="1"
            inactive-value="0"
            disabled
          ></el-switch>
        </template>
      </el-table-column>
      <el-table-column label="删除" align="center" width="60">
        <template slot-scope="scope">
          <el-switch
            v-model="scope.row.delFlag"
            active-value="1"
            inactive-value="0"
            disabled
          ></el-switch>
        </template>
      </el-table-column>
      <el-table-column label="创建时间" align="center" prop="firstCreateTime" width="150">
        <template slot-scope="scope">
          <span>{{ parseTime(scope.row.firstCreateTime) }}</span>
        </template>
      </el-table-column>
      <el-table-column label="最近登录" align="center" prop="loginTime" width="150">
        <template slot-scope="scope">
          <span>{{ parseTime(scope.row.loginTime) }}</span>
        </template>
      </el-table-column>
      <el-table-column label="操作" align="center" class-name="small-padding fixed-width" width="240">
        <template slot-scope="scope" v-if="scope.row.roleId !== 0">
          <el-button
            size="mini"
            plain
            type="text"
            icon="el-icon-search"
            @click="handleShowDetail(scope.row)"
            v-hasPermi="['gm:player:show']"
          >查看</el-button>
          <el-button
            size="mini"
            type="text"
            icon="el-icon-delete"
            @click="handleDelete(scope.row)"
            v-hasPermi="['gm:player:remove']"
          >删除角色</el-button>
          <el-button
            size="mini"
            type="text"
            icon="el-icon-refresh"
            @click="handleKitout(scope.row)"
            v-hasPermi="['gm:player:kitout']"
          >踢下线</el-button>
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

    <!-- 查看玩家对话框 -->
    <el-dialog :title="title" :visible.sync="open" width="60%" @close="cancel" append-to-body>
      <el-tabs v-model="activeName" @tab-click="handleTabClick">
        <el-tab-pane label="基本信息" name="baseInfo">
          <el-form ref="playBase" :model="playBase" label-width="100px">
            <el-row>
              <el-col :span="12">
                <el-form-item label="帮派标识" prop="gangsId">
                  <el-input v-model="playBase.gangsId" readonly="readonly" maxlength="30" />
                </el-form-item>
              </el-col>
              <el-col :span="12">
                <el-form-item label="帮派名" prop="gangsName">
                  <el-input v-model="playBase.gangsName" readonly="readonly" maxlength="30" />
                </el-form-item>
              </el-col>
            </el-row>
            <el-row>
              <el-col :span="12">
                <el-form-item label="登陆时间" prop="loginTime">
                  <el-input v-model="playBase.loginTime" readonly="readonly" maxlength="30" />
                </el-form-item>
              </el-col>
              <el-col :span="12">
                <el-form-item label="退出时间" prop="logoutTime">
                  <el-input v-model="playBase.logoutTime" readonly="readonly" maxlength="30" />
                </el-form-item>
              </el-col>
            </el-row>
            <el-row>
              <el-col :span="12">
                <el-form-item label="注册IP" prop="registerIp">
                  <el-input v-model="playBase.registerIp" readonly="readonly" maxlength="30" />
                </el-form-item>
              </el-col>
              <el-col :span="12">
                <el-form-item label="最后登陆IP" prop="lastLoginIp">
                  <el-input v-model="playBase.lastLoginIp" readonly="readonly" maxlength="30" />
                </el-form-item>
              </el-col>
            </el-row>
            <el-row>
              <el-col :span="12">
                <el-form-item label="注册地址" prop="registerAddress">
                  <el-input v-model="playBase.registerAddress" readonly="readonly" maxlength="30" />
                </el-form-item>
              </el-col>
              <el-col :span="12">
                <el-form-item label="最后登陆地址" prop="lastLoginAddress">
                  <el-input v-model="playBase.lastLoginAddress" readonly="readonly" maxlength="30" />
                </el-form-item>
              </el-col>
            </el-row>
            <el-row>
              <el-col :span="12">
                <el-form-item label="金币" prop="gold">
                  <el-input v-model="playBase.gold" readonly="readonly" maxlength="30" />
                </el-form-item>
              </el-col>
              <el-col :span="12">
                <el-form-item label="铜钱" prop="copper">
                  <el-input v-model="playBase.copper" readonly="readonly" maxlength="30" />
                </el-form-item>
              </el-col>
            </el-row>
            <el-row>
              <el-col :span="12">
                <el-form-item label="总获得元宝" prop="sumGold">
                  <el-input v-model="playBase.sumGold" readonly="readonly" maxlength="30" />
                </el-form-item>
              </el-col>
              <el-col :span="12">
                <el-form-item label="总消耗元宝" prop="costGold">
                  <el-input v-model="playBase.costGold" readonly="readonly" maxlength="30" />
                </el-form-item>
              </el-col>
            </el-row>
            <el-row>
              <el-col :span="12">
                <el-form-item label="VIP等级" prop="vipLevel">
                  <el-input v-model="playBase.vipLevel" readonly="readonly" maxlength="30" />
                </el-form-item>
              </el-col>
              <el-col :span="12">
                <el-form-item label="VIP经验" prop="vipExp">
                  <el-input v-model="playBase.vipExp" readonly="readonly" maxlength="30" />
                </el-form-item>
              </el-col>
            </el-row>
            <el-row>
              <el-col :span="12">
                <el-form-item label="在线时长" prop="onlineTime">
                  {{parseOnlineTime(playBase.onlineTime)}}
                  <!-- <el-input :v-model="parseOnlineTime(playBase.onlineTime)" readonly="readonly" maxlength="30" /> -->
                </el-form-item>
              </el-col>
            </el-row>
          </el-form>
        </el-tab-pane>
        <el-tab-pane label="背包信息" name="itemInfo">
          <el-row :gutter="10" class="mb8">
            <el-col :span="1.5">
              <el-button
                type="warning"
                plain
                icon="el-icon-download"
                size="mini"
                @click="handleItemExport"
                v-hasPermi="['gm:player:export']"
              >导出</el-button>
            </el-col>
            <right-toolbar @queryTable="getItemList"></right-toolbar>
          </el-row>
          <el-table v-loading="loading" :data="itemList" >
            <el-table-column label="道具" prop="resId" align="center" width="150" show-overflow-tooltip >
              <template slot-scope="scope">
                  <div v-html="getItemName(scope.row.resId)"></div>
              </template>
            </el-table-column>
            <el-table-column label="道具数量" prop="num" align="center" width="135" />
            <!-- <el-table-column label="道具品质" prop="itemQuality" width="90" /> -->
            <el-table-column label="唯一索引" prop="index" align="center" width="120" show-overflow-tooltip />
            <el-table-column label="等级" prop="itemLevel" align="center" width="120" />
            <el-table-column label="经验" prop="itemExp" align="center" width="120" />
            <el-table-column label="装备目标" prop="targetId" width="180" align="center" :show-overflow-tooltip=true />
            <el-table-column label="目标类型" prop="targetEntityType" align="center" width="150" />
            <el-table-column label="完美度" prop="perfection" align="center" width="120" />
          </el-table>
          <pagination
              v-show="itemTotal>0"
              :total="itemTotal"
              :page.sync="queryItemParams.pageNum"
              :limit.sync="queryItemParams.pageSize"
              @pagination="getItemList"
          />
        </el-tab-pane>
        <el-tab-pane label="将领信息" name="genInfo">
          <el-row :gutter="10" class="mb8">
            <el-col :span="1.5">
              <el-button
                type="warning"
                plain
                icon="el-icon-download"
                size="mini"
                @click="handleGeneralExport"
                v-hasPermi="['gm:player:export']"
              >导出</el-button>
            </el-col>
            <right-toolbar @queryTable="getGeneralList"></right-toolbar>
          </el-row>
          <el-table v-loading="loading" :data="generalList" >
              <el-table-column label="将领唯一" prop="generalId" width="90" />
              <el-table-column label="将领配置" prop="resId" width="90" />
              <!-- <el-table-column label="将领名" prop="generalName" width="90" /> -->
              <el-table-column label="将领等级" prop="level" width="90" />
              <!-- <el-table-column label="将领经验" prop="generalexp" width="90" /> -->
              <el-table-column label="将领战力" prop="combatPower" width="90" />
              <el-table-column label="将领星级" prop="starLv" width="90" />
              <el-table-column label="将领属性" prop="attributeMap" :show-overflow-tooltip=true width="90" />
              <el-table-column label="将领装备" prop="equipArr" :show-overflow-tooltip=true width="90" />
              <el-table-column label="战魂装备" prop="soulEquipArr" :show-overflow-tooltip=true width="90" />
              <el-table-column label="是否无双" prop="onlySurmount" width="90" />
              <el-table-column label="统兵等级" prop="armsLevel" width="90" />
              <el-table-column label="激活时间" align="center" prop="createTime" width="180">
                <template slot-scope="scope">
                  <span>{{ parseTime(scope.row.createTime) }}</span>
                </template>
              </el-table-column>
          </el-table>
          <pagination
              v-show="genTotal>0"
              :total="genTotal"
              :page.sync="queryGenParams.pageNum"
              :limit.sync="queryGenParams.pageSize"
              @pagination="getGeneralList"
          />
        </el-tab-pane>
        <el-tab-pane label="道具流水" name="sourceInfo">
          <el-form :model="queryItemLogParams" ref="queryItemLogForm" size="small" :inline="true" v-show="showSearch">
            <el-form-item label="道具ID" prop="resId">
              <el-input
                v-model="queryItemLogParams.resId"
                placeholder="请输入道具ID"
                clearable
                style="width: 240px"
                @keyup.enter.native="handleItemLogQuery"
              />
            </el-form-item>
            <el-form-item label="背包索引" prop="itemId">
              <el-input
                v-model="queryItemLogParams.itemId"
                placeholder="请输入背包索引"
                clearable
                style="width: 240px"
                @keyup.enter.native="handleItemLogQuery"
              />
            </el-form-item>
            <el-form-item label="来源" prop="source">
              <el-input
                v-model="queryItemLogParams.source"
                placeholder="请输入来源"
                clearable
                style="width: 240px"
                @keyup.enter.native="handleItemLogQuery"
              />
            </el-form-item>
            <el-form-item label="记录时间">
              <el-date-picker
                v-model="dateItemLogRange"
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
              <el-button type="primary" icon="el-icon-search" size="mini" @click="handleItemLogQuery">搜索</el-button>
              <el-button icon="el-icon-refresh" size="mini" @click="resetItemLogQuery">重置</el-button>
            </el-form-item>
          </el-form>
          <el-row :gutter="10" class="mb8">
            <el-col :span="1.5">
              <el-button
                type="warning"
                plain
                icon="el-icon-download"
                size="mini"
                @click="handleItemLogExport"
                v-hasPermi="['gm:player:export']"
              >导出</el-button>
            </el-col>
            <right-toolbar @queryTable="getItemLogList"></right-toolbar>
          </el-row>
          <el-table v-loading="loading" :data="itemLogList" >
              <el-table-column label="道具ID" prop="resId" align="center" :show-overflow-tooltip=true width="120">
                <template slot-scope="scope">
                    <div v-html="getItemName(scope.row.resId)"></div>
                </template>
              </el-table-column>
              <el-table-column label="背包索引" prop="itemId" align="center" :show-overflow-tooltip=true />
              <!-- <el-table-column label="道具名" prop="itemName" width="90" /> -->
              <el-table-column label="道具数量" prop="quantity" align="center" width="120" />
              <el-table-column label="操作前" prop="before" width="120" />
              <el-table-column label="操作后" prop="current" width="120" />
              <el-table-column label="来源" prop="source" align="center" :show-overflow-tooltip=true >
                <template slot-scope="scope">
                    <div v-html="getItemSource(scope.row.source)"></div>
                </template>
              </el-table-column>
              <el-table-column label="操作描述" prop="description" align="center" width="240" />
              <el-table-column label="操作时间" align="center" prop="createTime" width="180">
                <template slot-scope="scope">
                  <span>{{ parseTime(scope.row.createTime) }}</span>
                </template>
              </el-table-column>
          </el-table>
          <pagination
              v-show="itemLogTotal>0"
              :total="itemLogTotal"
              :page.sync="queryItemLogParams.pageNum"
              :limit.sync="queryItemLogParams.pageSize"
              @pagination="getItemLogList"
          />
        </el-tab-pane>
        <el-tab-pane label="邮件信息" name="mailInfo">
          <el-row :gutter="10" class="mb8">
            <el-col :span="1.5">
              <el-button
                type="warning"
                plain
                icon="el-icon-delete"
                size="mini"
                :disabled="multipleMail"
                @click="handleMailInvalid"
                v-hasPermi="['gm:player:mailInvalid']"
              >邮件过期</el-button>
            </el-col>
          </el-row>
          <el-table v-loading="loading" :data="mailList" @selection-change="handleMailSelectionChange">
              <el-table-column type="selection" width="55" align="center" />
              <el-table-column label="邮件标识" prop="mailId" align="center" width="180" />
              <el-table-column label="邮件标题" prop="title" align="center" :show-overflow-tooltip=true width="120" />
              <el-table-column label="邮件内容" prop="content" align="center" :show-overflow-tooltip=true width="180" />
              <el-table-column label="发送时间" align="center" prop="sendTime" width="180">
                <template slot-scope="scope">
                  <span>{{ parseTime(scope.row.sendTime) }}</span>
                </template>
              </el-table-column>
              <el-table-column label="有效时间" align="center" prop="validDate" width="180">
                <template slot-scope="scope">
                  <span>{{ parseTime(scope.row.validDate) }}</span>
                </template>
              </el-table-column>
              <el-table-column label="附件" prop="annex" align="center" :show-overflow-tooltip=true width="120" >
                <template slot-scope="scope">
                    <div v-html="getItemFormat(scope.row.annex)"></div>
                </template>
              </el-table-column>
              <el-table-column label="邮件状态" align="center" width="90">
                <template slot-scope="scope">
                  <el-switch
                    v-model="scope.row.state"
                    active-value="0"
                    inactive-value="1"
                    disabled
                  ></el-switch>
                </template>
              </el-table-column>
              <el-table-column label="创建时间" align="center" prop="firstCreateTime" width="180">
                <template slot-scope="scope">
                  <span>{{ parseTime(scope.row.firstCreateTime) }}</span>
                </template>
              </el-table-column>
          </el-table>
          <pagination
              v-show="mailTotal>0"
              :total="mailTotal"
              :page.sync="queryParams.pageNum"
              :limit.sync="queryParams.pageSize"
              @pagination="getPlayerMailList"
          />
        </el-tab-pane>
        <el-tab-pane label="战力日志" name="powerLogInfo">
          <el-form :model="queryPowerParams" ref="queryPowerLogForm" size="small" :inline="true" v-show="showSearch">
            <el-form-item label="原因" prop="queryPowerParams">
              <el-input
                v-model="queryPowerParams.powerChangeReason"
                placeholder="请输入原因"
                clearable
                style="width: 240px"
                @keyup.enter.native="handlePowerLogQuery"
              />
            </el-form-item>
            <el-form-item label="记录时间">
              <el-date-picker
                v-model="datePowerRange"
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
              <el-button type="primary" icon="el-icon-search" size="mini" @click="handlePowerLogQuery">搜索</el-button>
              <el-button icon="el-icon-refresh" size="mini" @click="resetPowerLogQuery">重置</el-button>
            </el-form-item>
          </el-form>
          <el-row :gutter="10" class="mb8">
            <el-col :span="1.5">
              <el-button
                type="warning"
                plain
                icon="el-icon-download"
                size="mini"
                @click="handlePowerLogExport"
                v-hasPermi="['gm:player:export']"
              >导出</el-button>
            </el-col>
            <right-toolbar @queryTable="getPowerLogList"></right-toolbar>
          </el-row>
          <el-table v-loading="loading" :data="powerLogList" >
              <el-table-column label="玩家ID" prop="roleId" align="center" show-overflow-tooltip width="180" />
              <el-table-column label="变化前战力" prop="prePower" align="center" show-overflow-tooltip width="120" />
              <el-table-column label="战力" prop="power" align="center" show-overflow-tooltip width="120" />
              <el-table-column label="战力变化" align="center" width="180">
                <template slot-scope="scope">
                  <span>{{ powerChange(scope.row.prePower, scope.row.power) }}</span>
                </template>
              </el-table-column>
              <el-table-column label="战力变化原因" prop="powerChangeReason" align="center" show-overflow-tooltip width="180" />
              <el-table-column label="战力变化描述" prop="powerDesc" align="center" show-overflow-tooltip width="180" />
              <el-table-column label="记录时间" align="center" prop="firstCreateTime" width="180">
                <template slot-scope="scope">
                  <span>{{ parseTime(scope.row.firstCreateTime) }}</span>
                </template>
              </el-table-column>
          </el-table>
          <pagination
              v-show="powerLogTotal>0"
              :total="powerLogTotal"
              :page.sync="queryPowerParams.pageNum"
              :limit.sync="queryPowerParams.pageSize"
              @pagination="getPowerLogList"
          />
        </el-tab-pane>
        <el-tab-pane label="登陆日志" name="loginLogInfo">
          <el-form :model="queryLoginParams" ref="queryLoginLogForm" size="small" :inline="true" v-show="showSearch">
            <el-form-item label="等级" prop="queryLoginParams">
              <el-input
                v-model="queryLoginParams.roleLevel"
                placeholder="请输入等级"
                clearable
                style="width: 240px"
                @keyup.enter.native="handleLoginLogQuery"
              />
            </el-form-item>
            <el-form-item label="记录时间">
              <el-date-picker
                v-model="dateLoginRange"
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
              <el-button type="primary" icon="el-icon-search" size="mini" @click="handleLoginLogQuery">搜索</el-button>
              <el-button icon="el-icon-refresh" size="mini" @click="resetLoginLogQuery">重置</el-button>
            </el-form-item>
          </el-form>
          <el-row :gutter="10" class="mb8">
            <el-col :span="1.5">
              <el-button
                type="warning"
                plain
                icon="el-icon-download"
                size="mini"
                @click="handleLoginLogExport"
                v-hasPermi="['gm:player:export']"
              >导出</el-button>
            </el-col>
            <right-toolbar @queryTable="getLoginLogList"></right-toolbar>
          </el-row>
          <el-table v-loading="loading" :data="loginLogList" >
              <el-table-column label="玩家ID" prop="roleId" align="center" show-overflow-tooltip width="180" />
              <el-table-column label="玩家名" prop="roleName" align="center" show-overflow-tooltip width="120" />
              <el-table-column label="等级" prop="roleLevel" align="center" show-overflow-tooltip width="120" />
              <el-table-column label="登陆时间" align="center" prop="loginTime" width="180">
                <template slot-scope="scope">
                  <span>{{ parseTime(scope.row.loginTime) }}</span>
                </template>
              </el-table-column>
              <el-table-column label="退出时间" align="center" prop="logoutTime" width="180">
                <template slot-scope="scope">
                  <span>{{ parseTime(scope.row.logoutTime) }}</span>
                </template>
              </el-table-column>
              <el-table-column label="在线时长(s)" align="center" width="90">
                <template slot-scope="scope">
                  <span>{{ parseOnlineSec(scope.row.loginTime, scope.row.logoutTime) }}</span>
                </template>
              </el-table-column>
              <el-table-column label="创建时间" align="center" prop="firstCreateTime" width="180">
                <template slot-scope="scope">
                  <span>{{ parseTime(scope.row.firstCreateTime) }}</span>
                </template>
              </el-table-column>
          </el-table>
          <pagination
              v-show="loginLogTotal>0"
              :total="loginLogTotal"
              :page.sync="queryLoginParams.pageNum"
              :limit.sync="queryLoginParams.pageSize"
              @pagination="getLoginLogList"
          />
        </el-tab-pane>

        <el-tab-pane label="将领日志" name="generalLogInfo">
          <el-form :model="queryGeneralParams" ref="queryGeneralLogForm" size="small" :inline="true" v-show="showSearch">
            <el-form-item label="将领名" prop="queryGeneralParams">
              <el-input
                v-model="queryGeneralParams.genName"
                placeholder="请输入将领名"
                clearable
                style="width: 240px"
                @keyup.enter.native="handleGeneralLogQuery"
              />
            </el-form-item>
            <el-form-item label="原因" prop="queryGeneralParams">
              <el-input
                v-model="queryGeneralParams.reason"
                placeholder="请输入原因"
                clearable
                style="width: 240px"
                @keyup.enter.native="handleGeneralLogQuery"
              />
            </el-form-item>
            <el-form-item label="操作将领类型" prop="queryGeneralParams">
              <el-input
                v-model="queryGeneralParams.optType"
                placeholder="请输入操作将领类型"
                clearable
                style="width: 240px"
                @keyup.enter.native="handleGeneralLogQuery"
              />
            </el-form-item>
            <el-form-item label="记录时间">
              <el-date-picker
                v-model="dateGeneralRange"
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
              <el-button type="primary" icon="el-icon-search" size="mini" @click="handleGeneralLogQuery">搜索</el-button>
              <el-button icon="el-icon-refresh" size="mini" @click="resetGeneralLogQuery">重置</el-button>
            </el-form-item>
          </el-form>
          <el-row :gutter="10" class="mb8">
            <el-col :span="1.5">
              <el-button
                type="warning"
                plain
                icon="el-icon-download"
                size="mini"
                @click="handleGeneralLogExport"
                v-hasPermi="['gm:player:export']"
              >导出</el-button>
            </el-col>
            <right-toolbar @queryTable="getGeneralLogList"></right-toolbar>
          </el-row>
          <el-table v-loading="loading" :data="generalLogList" >
              <el-table-column label="玩家ID" prop="roleId" align="center" show-overflow-tooltip width="180" />
              <el-table-column label="将领唯一ID" prop="genId" align="center" width="90" />
              <el-table-column label="将领配置ID" prop="genResId" align="center"  width="90" />
              <el-table-column label="将领名" prop="genName" align="center"  width="120" />
              <el-table-column label="来源描述" prop="reason" align="center" show-overflow-tooltip width="120" />
              <el-table-column label="来源ID" prop="reasonId" align="center" show-overflow-tooltip width="120" />
              <el-table-column label="操作类型" prop="optType" align="center" width="60" >
                <template slot-scope="scope">
                  <span>{{ parseOptType(scope.row.optType) }}</span>
                </template>
              </el-table-column>
              <el-table-column label="参数" prop="args" align="center" show-overflow-tooltip width="120" />
              <el-table-column label="创建时间" align="center" prop="firstCreateTime" width="180">
                <template slot-scope="scope">
                  <span>{{ parseTime(scope.row.firstCreateTime) }}</span>
                </template>
              </el-table-column>
          </el-table>
          <pagination
              v-show="generalLogTotal>0"
              :total="generalLogTotal"
              :page.sync="queryGeneralParams.pageNum"
              :limit.sync="queryGeneralParams.pageSize"
              @pagination="getGeneralLogList"
          />
        </el-tab-pane>
      </el-tabs>
    </el-dialog>
  </div>
</template>

<script>
import { listPlayer, listPlayerGeneral, listPlayerItem, getPlayerBase, listPlayerItemLog, getPlayerMail, delPlayer, kitout, listPlayerPowerLog, listPlayerLoginLog, listPlayerGeneralLog, mailInvalid } from "@/api/gameGm/player";
import { listServerAll } from "@/api/gameGm/server";
import axios from "axios";
// import { treeselect as menuTreeselect, roleMenuTreeselect } from "@/api/system/menu";
// import { treeselect as deptTreeselect, roleDeptTreeselect } from "@/api/system/dept";

export default {
  name: "Player",
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
      // 查询参数
      queryParams: {
        pageNum: 1,
        pageSize: 20,
        roleName: undefined,
        roleId: undefined,
        account: undefined,
        channelId: undefined,
        serverId: undefined
      },

      // 日期范围
      dateItemLogRange: [],
      // 道具日志查询参数
      queryItemLogParams: {
        pageNum: 1,
        pageSize: 20,
      },
      itemLogTotal: 0,
      mailTotal: 0,
      // 将领查询参数
      queryGenParams: {
        pageNum: 1,
        pageSize: 20,
      },
      genTotal: 0,
      // 背包道具查询参数
      queryItemParams: {
        pageNum: 1,
        pageSize: 20,
      },
      itemTotal: 0,
      // 战力日志查询参数
      queryPowerParams: {
        pageNum: 1,
        pageSize: 20,
      },
      powerLogTotal: 0,
      datePowerRange: [],
      // 登陆日志查询参数
      queryLoginParams: {
        pageNum: 1,
        pageSize: 20,
      },
      loginLogTotal: 0,
      dateLoginRange: [],
      // 将领日志查询参数
      queryGeneralParams: {
        pageNum: 1,
        pageSize: 20,
      },
      generalLogTotal: 0,
      dateGeneralRange: [],
      // 表单参数
      form: {},
      // defaultProps: {
      //   children: "children",
      //   label: "label"
      // },
      // 表单校验
      // rules: {
      //   roleName: [
      //     { required: true, message: "角色名称不能为空", trigger: "blur" }
      //   ]
      // },
      // colList: ["基本信息", "背包信息", "武将信息", "道具流水"],
      serverList: [],
      activeName: 'baseInfo',
      generalList: [],
      // baseData: {},
      itemList: [],
      itemLogList: [],
      roleKeyId: undefined,
      mailList: [],
      // 基础信息
      playBase: {},
      itemOptions:[],
      itemSources:[],
      powerLogList:[],
      loginLogList:[],
      generalLogList:[],

      // 选中数组
      mailids: [],
      // 非多个禁用
      multipleMail: true,
    };
  },
  created() {
    this.getServerList();
    this.getList();
    this.getItemConfig();
    this.getItemSourceConfig();
  },
  methods: {
    /** 查询角色列表 */
    getList() {
      this.loading = true;
      listPlayer(this.addDateRange(this.queryParams, this.dateRange)).then(response => {
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

    getItemSourceConfig() {
      axios.get("/json/ItemSource.json").then(result => {
        this.itemSources = result.data;
      });
    },


    handleTabClick(tab, event) {
      this.handleTabByName(tab.name);
    },

    handleTabByName(tabName) {
      switch (tabName) {
        case "baseInfo":
          this.getPlayerBaseInfo();
          break;
        case "genInfo":
          this.getGeneralList();
          break;
        case "itemInfo":
          this.getItemList();
          break;
        case "sourceInfo":
          this.getItemLogList();
          break;
        case "mailInfo":
          this.getPlayerMailList();
          break;
        case "powerLogInfo":
          this.getPowerLogList();
          break;
        case "loginLogInfo":
          this.getLoginLogList();
          break;
        case "generalLogInfo":
          this.getGeneralLogList();
          break;
        default:
          break;
      }
    },

    getPlayerMailList() {
      getPlayerMail(this.roleKeyId, this.addDateRange(this.queryParams, this.dateRange)).then(response => {
        this.mailList = response.rows;
        this.mailTotal = response.total;
      });
    },

    getPlayerBaseInfo() {
      getPlayerBase(this.roleKeyId, this.queryParams.serverId).then(response => {
        this.playBase = response.data;
      });
    },

    getGeneralList() {
      this.queryGenParams.serverId = this.queryParams.serverId;
      listPlayerGeneral(this.roleKeyId, this.addDateRange(this.queryGenParams, this.dateRange)).then(response => {
        this.generalList = response.rows;
        this.genTotal = response.total;
      });
    },

    getPowerLogList() {
      this.queryPowerParams.serverId = this.queryParams.serverId;
      listPlayerPowerLog(this.roleKeyId, this.addDateRange(this.queryPowerParams, this.datePowerRange)).then(response => {
        this.powerLogList = response.rows;
        this.powerLogTotal = response.total;
      });
    },

    getLoginLogList() {
      this.queryLoginParams.serverId = this.queryParams.serverId;
      listPlayerLoginLog(this.roleKeyId, this.addDateRange(this.queryLoginParams, this.dateLoginRange)).then(response => {
        this.loginLogList = response.rows;
        this.loginLogTotal = response.total;
      });
    },

    getGeneralLogList() {
      this.queryGeneralParams.serverId = this.queryParams.serverId;
      this.queryGeneralParams.roleId = this.roleKeyId;
      listPlayerGeneralLog(this.roleKeyId, this.addDateRange(this.queryGeneralParams, this.dateGeneralRange)).then(response => {
        this.generalLogList = response.rows;
        this.generalLogTotal = response.total;
      });
    },

    getItemList() {
      this.queryItemParams.serverId = this.queryParams.serverId;
      listPlayerItem(this.roleKeyId, this.addDateRange(this.queryItemParams, this.dateRange)).then(response => {
        this.itemList = response.rows;
        this.itemTotal = response.total;
      });
    },

    getItemLogList() {
      this.queryItemLogParams.serverId = this.queryParams.serverId;
      listPlayerItemLog(this.roleKeyId, this.addDateRange(this.queryItemLogParams, this.dateItemLogRange)).then(response => {
        this.itemLogList = response.rows;
        this.itemLogTotal = response.total;
      });
    },

    // 角色在线状态修改
    handleOnlineStatusChange(row) {
      let text = row.online === "0" ? "离线" : "在线";
    },

    // 角色是否删除
    handlePlayerStatusDel(row) {
      let text = row.online === "0" ? "正常" : "删除";
    },

    // 表单重置
    reset() {
      if (this.$refs.menu != undefined) {
        this.$refs.menu.setCheckedKeys([]);
      }
      this.form = {
      };
      this.resetForm("form");
      this.roleKeyId = undefined;
    },

// 取消按钮
    cancel() {
      this.open = false;
      // this.activeName = 'baseInfo';
      this.generalList = [];
      this.itemLogList = [];
      // this.baseData = {};
      this.itemList = [];
      this.reset();
      this.roleKeyId = undefined;
      this.open = false;

      this.mailList = [];
      // 基础信息
      this.playBase = {};
      this.loginLogList = [];
      this.generalLogList = [];
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

    //     /** 将领搜索按钮操作 */
    // handleGenQuery() {
    //   this.queryGenParams.pageNum = 1;
    //   this.getList();
    // },
    // /** 将领重置按钮操作 */
    // resetGenQuery() {
    //         // 查询参数
    //   this.queryGenParams = {
    //     pageNum: 1,
    //     pageSize: 20,
    //   },
    //   this.dateRange = [];
    //   // this.resetForm("queryForm");
    //   this.handleGenQuery();
    // },

    /** 道具搜索按钮操作 */
    handleItemLogQuery() {
      this.queryItemLogParams.pageNum = 1;
      this.getItemLogList();
    },
    /** 道具重置按钮操作 */
    resetItemLogQuery() {
      // 查询参数
      this.queryItemLogParams = {
        pageNum: 1,
        pageSize: 20,
      },
      this.dateItemLogRange = [];
      this.resetForm("queryItemLogForm");
      this.handleItemLogQuery();
    },

        /** 战力搜索按钮操作 */
    handlePowerLogQuery() {
      this.queryPowerParams.pageNum = 1;
      this.getPowerLogList();
    },
    /** 战力重置按钮操作 */
    resetPowerLogQuery() {
      // 查询参数
      this.queryPowerParams = {
        pageNum: 1,
        pageSize: 20,
      },
      this.datePowerRange = [];
      this.resetForm("queryPowerLogForm");
      this.handlePowerLogQuery();
    },

    /** 登陆日志搜索按钮操作 */
    handleLoginLogQuery() {
      this.queryLoginParams.pageNum = 1;
      this.getLoginLogList();
    },
    /** 日志重置按钮操作 */
    resetLoginLogQuery() {
      // 查询参数
      this.queryLoginParams = {
        pageNum: 1,
        pageSize: 20,
      },
      this.dateLoginRange = [];
      this.resetForm("queryLoginLogForm");
      this.handleLoginLogQuery();
    },


    /** 将领日志搜索按钮操作 */
    handleGeneralLogQuery() {
      this.queryGeneralParams.pageNum = 1;
      this.getGeneralLogList();
    },
    /** 将领日志重置按钮操作 */
    resetGeneralLogQuery() {
      // 查询参数
      this.queryGeneralParams = {
        pageNum: 1,
        pageSize: 20,
      },
      this.dateGeneralRange = [];
      this.resetForm("queryGeneralLogForm");
      this.handleGeneralLogQuery();
    },

    // 多选框选中数据
    handleSelectionChange(selection) {
      this.ids = selection.map(item => item.roleId)
      this.single = selection.length!=1
      this.multiple = !selection.length
    },

    handleMailSelectionChange(selection) {
        this.mailids = selection.map(item => item.mailId)
        this.multipleMail = !selection.length
    },

    /** 查看按钮操作 */
    handleShowDetail(row) {
      this.reset();
      this.roleKeyId = row.roleId;
      this.handleTabByName(this.activeName);
      this.title = "查看角色";
      this.open = true;
      // getPlayerBase(roleId, this.queryParams.serverId).then(response => {
      //   this.form = response.data;
      //   this.open = true;
      //   this.title = "查看角色";
      // });
    },

    /** 删除按钮操作 */
    handleDelete(row) {
      const roleIds = row.roleId || this.ids;
      const serverId = this.queryParams.serverId;
      this.$modal.confirm('是否确认删除玩家ID为"' + roleIds + '"的数据项？').then(function() {
        return delPlayer(roleIds, serverId);
      }).then(() => {
        this.getList();
        this.$modal.msgSuccess("删除成功");
      }).catch(() => {});
    },

    /** 删除按钮操作 */
    handleKitout(row) {
      const roleIds = row.roleId || this.ids;
      const serverId = this.queryParams.serverId;
      this.$modal.confirm('是否确认踢出玩家ID为"' + roleIds + '"的数据项？').then(function() {
        return kitout(roleIds, serverId);
      }).then(() => {
        this.getList();
        this.$modal.msgSuccess("踢出成功");
      }).catch(() => {});
    },

    // itemList() {
      // this.loading = true;
      // listRole(this.addDateRange(this.queryParams, this.dateRange)).then(response => {
      //     this.roleList = response.rows;
      //     this.total = response.total;
      //     this.loading = false;
      //   }
      // );
    // },

    // generalList() {
    //   this.loading = true;
    // },

    // itemSourceList() {
    //   this.loading = true;
    // },

    /** 导出按钮操作 */
    handleExport() {
      this.download('gameGm/player/export', {
        ...this.queryParams
      }, `player_${new Date().getTime()}.xlsx`)
    },

    /** 导出按钮操作 */
    handleGeneralExport() {
      this.download('gameGm/player/general/export/' + this.roleKeyId, {
        ...this.queryParams
      }, `general_${new Date().getTime()}.xlsx`)
    },

    handleMailInvalid(row) {
      const mails = row.mailId || this.mailids;
      const serverId = this.queryParams.serverId;
      this.$modal.confirm('是否确认指定邮件ID为"' + mails + '"的数据过期？').then(function() {
        return mailInvalid(mails, serverId);
      }).then(() => {
        this.getList();
        this.$modal.msgSuccess("删除成功");
      }).catch(() => {});
    },

    /** 导出道具按钮操作 */
    handleItemExport() {
      this.download('gameGm/player/item/export/' + this.roleKeyId, {
        ...this.queryItemParams
      }, `item_${new Date().getTime()}.xlsx`)
    },


        /** 导出道具按钮操作 */
    handleItemLogExport() {
      this.download('gameGm/player/itemlog/export/' + this.roleKeyId, {
        ...this.queryItemLogParams
      }, `itemlog_${new Date().getTime()}.xlsx`)
    },

    /** 导出战力按钮操作 */
    handlePowerLogExport() {
      this.download('gameGm/player/powerlog/export/' + this.roleKeyId, {
        ...this.queryPowerParams
      }, `powerlog_${this.roleKeyId}_${new Date().getTime()}.xlsx`)
    },

    /** 导出登陆按钮操作 */
    handleLoginLogExport() {
      this.download('gameGm/player/loginlog/export/' + this.roleKeyId, {
        ...this.queryLoginParams
      }, `loginlog_${this.roleKeyId}_${new Date().getTime()}.xlsx`)
    },

    /** 导出将领日志按钮操作 */
    handleGeneralLogExport() {
      this.download('gameGm/player/generallog/export/' + this.roleKeyId, {
        ...this.queryGeneralParams
      }, `loginlog_${this.roleKeyId}_${new Date().getTime()}.xlsx`)
    },

    getItemFormat(items) {
      if (items === undefined || items.length <= 0) {
        return "";
      }
      const arrItem = eval('(' + items + ')');
      if (arrItem === undefined) {
        return "";
      }
      let resultStr = "";
      let count = 0;
      let len = Object.keys(arrItem).length;
      for (let index in arrItem) {
        const itemConst = this.itemOptions.find(item => item.id > 0 && item.id == index);
        if (itemConst === undefined) {
          continue;
        }
        resultStr += itemConst.name + "*" + arrItem[index];
        count++;
        if (count < len) {
          resultStr += ",";
        }
      }

      return resultStr;
    },

    getItemName(itemId) {
      if (itemId == 0) {
        return itemId;
      }
      const itemConst = this.itemOptions.find(item => item.id > 0 && item.id == itemId);
      if (itemConst === undefined) {
        return itemId + "_" + "undefined";
      }
      return itemId + "_" + itemConst.name;
    },

    getItemSource(sourceId) {
      if (sourceId == 0) {
        return sourceId;
      }
      const itemConst = this.itemSources.find(item => item.id > 0 && item.id == sourceId);
      if (itemConst === undefined) {
        return sourceId + "_" + "undefined";
      }
      return sourceId + "_" + itemConst.sourceName;
    },

    parseOnlineSec(loginTime, logoutTime) {
      var inTime = new Date(loginTime).getTime();
      var outTime = new Date(logoutTime).getTime();
      if (inTime > outTime) {
        return "";
      }
      return (outTime - inTime) / 1000;
    },

    parseOptType(optType) {
      if (optType === 1) {
        return "获得";
      } else {
        return "删除";
      }
    },

    powerChange(prePower, power) {
      var beforePower = parseInt(prePower);
      var currPower = parseInt(power);
      if (beforePower > currPower) {
        return "下降--" + (beforePower - currPower);
      } else {
        return "上升--" + (currPower - beforePower);
      }
    },

    parseOnlineTime(onlineTime) {
      var min = parseInt(onlineTime / 60);
      var hour = 0;
      var sec = parseInt(onlineTime % 60);
      if (min >= 60) {
        hour = parseInt(min / 60);
        min = parseInt(min % 60);
      }
      return hour + " 时 " + min + " 分 " + sec + " 秒 " + "(" + onlineTime + ")";
    },
  }
};
</script>
