package com.game.server;

import com.common.module.internal.db.annotation.*;
import com.common.module.internal.db.constant.DataType;
import com.common.module.internal.db.constant.KeyOffsetType;
import com.common.module.internal.db.entity.DBEntity;
import com.common.module.internal.db.entity.Repositories;
import com.common.module.util.DateUnit;

import java.util.Date;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

/**
 * 玩家表数据尽可能简单点，业务属性不能放在这个表，可以放到PlayerSingleDataEntity
 */
@DB()
@Table(name = "player", comment = "玩家数据表", deleteable = false, offset = KeyOffsetType.PLAYER_OFFSET)
public class PlayerEntity extends DBEntity {//} implements IClient {

    public static PlayerEntityRepository getRepository() {
        return Repositories.getRepository(PlayerEntity.class);
    }

    @PK
    @Persistent(name = "id", dataType = DataType.LONG, comment = "玩家id，主键")
    private long id;

    @Persistent(name = "p_id", dataType = DataType.LONG, comment = "平台id")
    private long pId;

    @Index(name = "idx_channel_id")
    @Persistent(name = "channel_id", dataType = DataType.STRING, len = 50, canBeNull = false, comment = "渠道")
    private String channelId;
    @Persistent(name = "udid", dataType = DataType.STRING, comment = "设备号")
    private String udid;

    @Persistent(name = "server_id", dataType = DataType.INT, comment = "服务器ID")
    private int serverId;

    @Index(name = "idx_name")
    @Persistent(name = "name", dataType = DataType.STRING, canBeNull = false, len = 50, comment = "名称")
    private String name;

    @Index(name = "idx_account")
    @Persistent(name = "account", dataType = DataType.STRING, len = 50, canBeNull = false, comment = "账户名")
    private String account;

    @Persistent(name = "head_id", dataType = DataType.INT, comment = "头像配置id")
    private int headId;

    @Persistent(name = "head_frame_id", dataType = DataType.INT, comment = "头像框配置id")
    private int headFrameId;

    @Persistent(name = "level", dataType = DataType.INT, comment = "等级")
    private AtomicInteger level = new AtomicInteger();

    @Persistent(name = "exp", dataType = DataType.LONG, comment = "经验值")
    private AtomicLong exp = new AtomicLong();

    @Persistent(name = "login_time", dataType = DataType.DATE, comment = "登入时间")
    private Date loginTime = DateUnit.firstDate0();

    @Persistent(name = "logout_time", dataType = DataType.DATE, comment = "登出时间")
    private Date logoutTime = DateUnit.firstDate0();

    @Persistent(name = "power", dataType = DataType.LONG, comment = "玩家战力")
    private AtomicLong power = new AtomicLong();
    @Persistent(name = "cz_money", dataType = DataType.INT, comment = "拥有最大游戏币")
    private AtomicInteger czMoney = new AtomicInteger(0);

    @Persistent(name = "cost_money", dataType = DataType.INT, comment = "消耗的总游戏币")
    private AtomicInteger costMoney = new AtomicInteger(0);

    @Persistent(name = "online", dataType = DataType.BOOL, comment = "离线或在线")
    private boolean online;

    @Persistent(name = "real_money_count", dataType = DataType.INT, comment = "真实充值金额")
    private AtomicInteger realMoneyCount = new AtomicInteger();
    @Persistent(name = "del_flag", dataType = DataType.BOOL, comment = "删除角色标志")
    private boolean delFlag;

    @Override
    public void update() {
        super.update();
        getAssetsEntity().update();
    }
    /**
     * 获得资产实体
     *
     * @return
     */
    public PlayerAssetsEntity getAssetsEntity() {
        PlayerAssetsEntityRepository repository = Repositories.getRepository(PlayerAssetsEntity.class);
        return repository.getOrMakeAssetsEntity(id);
    }

    @Override
    public String mergeRoleKey() {
        return null;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getpId() {
        return pId;
    }

    public void setpId(long pId) {
        this.pId = pId;
    }

    public String getChannelId() {
        return channelId;
    }

    public void setChannelId(String channelId) {
        this.channelId = channelId;
    }

    public String getUdid() {
        return udid;
    }

    public void setUdid(String udid) {
        this.udid = udid;
    }

    public int getServerId() {
        return serverId;
    }

    public void setServerId(int serverId) {
        this.serverId = serverId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAccount() {
        return account;
    }

    public void setAccount(String account) {
        this.account = account;
    }

    public int getHeadId() {
        return headId;
    }

    public void setHeadId(int headId) {
        this.headId = headId;
    }

    public int getHeadFrameId() {
        return headFrameId;
    }

    public void setHeadFrameId(int headFrameId) {
        this.headFrameId = headFrameId;
    }

    public AtomicInteger getLevel() {
        return level;
    }

    public void setLevel(AtomicInteger level) {
        this.level = level;
    }

    public AtomicLong getExp() {
        return exp;
    }

    public void setExp(AtomicLong exp) {
        this.exp = exp;
    }

    public Date getLoginTime() {
        return loginTime;
    }

    public void setLoginTime(Date loginTime) {
        this.loginTime = loginTime;
    }

    public Date getLogoutTime() {
        return logoutTime;
    }

    public void setLogoutTime(Date logoutTime) {
        this.logoutTime = logoutTime;
    }

    public AtomicLong getPower() {
        return power;
    }

    public void setPower(AtomicLong power) {
        this.power = power;
    }

    public AtomicInteger getCzMoney() {
        return czMoney;
    }

    public void setCzMoney(AtomicInteger czMoney) {
        this.czMoney = czMoney;
    }

    public AtomicInteger getCostMoney() {
        return costMoney;
    }

    public void setCostMoney(AtomicInteger costMoney) {
        this.costMoney = costMoney;
    }

    public boolean isOnline() {
        return online;
    }

    public void setOnline(boolean online) {
        this.online = online;
    }

    public AtomicInteger getRealMoneyCount() {
        return realMoneyCount;
    }

    public void setRealMoneyCount(AtomicInteger realMoneyCount) {
        this.realMoneyCount = realMoneyCount;
    }

    public boolean isDelFlag() {
        return delFlag;
    }

    public void setDelFlag(boolean delFlag) {
        this.delFlag = delFlag;
    }
}
