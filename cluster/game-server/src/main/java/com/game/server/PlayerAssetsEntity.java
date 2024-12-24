package com.game.server;

import com.common.module.internal.db.annotation.DB;
import com.common.module.internal.db.annotation.PK;
import com.common.module.internal.db.annotation.Persistent;
import com.common.module.internal.db.annotation.Table;
import com.common.module.internal.db.constant.DataType;
import com.common.module.internal.db.entity.DBEntity;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

@DB()
@Table(name = "player_assets", comment = "玩家资产表")
public class PlayerAssetsEntity extends DBEntity {

    @PK
    @Persistent(name = "id", dataType = DataType.LONG, comment = "玩家id，主键")
    private long id;

    @Persistent(name = "voucher", dataType = DataType.LONG, comment = "代金券")
    private AtomicLong voucher = new AtomicLong();

    @Persistent(name = "gold", dataType = DataType.LONG, comment = "金币")
    private AtomicLong gold = new AtomicLong();

    @Persistent(name = "general_exp", dataType = DataType.LONG, comment = "将领经验")
    private AtomicLong generalExp = new AtomicLong();

    @Persistent(name = "copper", dataType = DataType.LONG, comment = "铜钱")
    private AtomicLong copper = new AtomicLong();

    @Persistent(name = "circus_coin", dataType = DataType.INT, comment = "竞技令")
    private AtomicInteger circusCoin = new AtomicInteger();

    @Persistent(name = "scheme_lottery_score", dataType = DataType.INT, comment = "计谋抽取积分")
    private AtomicInteger schemeLotteryScore = new AtomicInteger();

    @Persistent(name = "fish_coin", dataType = DataType.LONG, comment = "鱼币")
    private AtomicLong fishCoin = new AtomicLong();

    @Persistent(name = "exploit", dataType = DataType.LONG, comment = "功勋")
    private AtomicLong exploit = new AtomicLong();

    @Persistent(name = "intelligence", dataType = DataType.LONG, comment = "军团币")
    private AtomicLong intelligence = new AtomicLong();

    @Persistent(name = "guard_wall_coin", dataType = DataType.LONG, comment = "守卫币")
    private AtomicLong guardWallCoin = new AtomicLong();

    @Persistent(name = "cross_boss_coin", dataType = DataType.LONG, comment = "军演币")
    private AtomicLong crossBossCoin = new AtomicLong();

    @Persistent(name = "cross_competition_coin", dataType = DataType.LONG, comment = "争锋币")
    private AtomicLong crossCompetitionCoin = new AtomicLong();

    @Persistent(name = "army_brawling_coin", dataType = DataType.LONG, comment = "乱斗币")
    private AtomicLong armyBrawlingCoin = new AtomicLong();

    @Persistent(name = "transport_coin", dataType = DataType.LONG, comment = "漕运币")
    private AtomicLong transportCoin = new AtomicLong();

    @Persistent(name = "collectibles_get_coin", dataType = DataType.LONG, comment = "珍宝币")
    private AtomicLong collectiblesGetCoin = new AtomicLong();

    @Persistent(name = "pet_recruit_score", dataType = DataType.INT, comment = "灵宠招募积分")
    private AtomicInteger petRecruitScore = new AtomicInteger();


    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getGold() {
        return gold.get();
    }

    public void setGold(long gold) {
        this.gold.compareAndSet(this.gold.get(), gold);
    }

    public long getVoucher() {
        return voucher.get();
    }

    public void setVoucher(long voucher) {
        this.voucher.compareAndSet(this.voucher.get(), voucher);
    }

    public long getGeneralExp() {
        return generalExp.get();
    }

    public void setGeneralExp(long generalExp) {
        this.generalExp.compareAndSet(this.generalExp.get(), generalExp);
    }

    public long getCopper() {
        return copper.get();
    }

    public void setCopper(long copper) {
        this.copper.compareAndSet(this.copper.get(), copper);
    }

    public int getCircusCoin() {
        return circusCoin.get();
    }

    public void setCircusCoin(int circusCoin) {
        this.circusCoin.compareAndSet(getCircusCoin(), circusCoin);
    }

    public int getSchemeLotteryScore() {
        return schemeLotteryScore.get();
    }

    public void setSchemeLotteryScore(int schemeLotteryScore) {
        this.schemeLotteryScore.compareAndSet(getSchemeLotteryScore(), schemeLotteryScore);
    }

    public long getFishCoin() {
        return fishCoin.get();
    }

    public void setFishCoin(long fishCoin) {
        this.fishCoin.set(fishCoin);
    }

    public long getExploit() {
        return exploit.get();
    }

    public void setExploit(long exploit) {
        this.exploit.set(exploit);
    }

    public long getIntelligence() {
        return intelligence.get();
    }

    public void setIntelligence(long intelligence) {
        this.intelligence.set(intelligence);
    }

    @Override
    public String mergeRoleKey() {
        return "id";
    }

    public AtomicLong getGuardWallCoin() {
        return guardWallCoin;
    }

    public void setGuardWallCoin(AtomicLong guardWallCoin) {
        this.guardWallCoin = guardWallCoin;
    }

    public AtomicLong getCrossBossCoin() {
        return crossBossCoin;
    }

    public void setCrossBossCoin(AtomicLong crossBossCoin) {
        this.crossBossCoin = crossBossCoin;
    }

    public AtomicLong getCrossCompetitionCoin() {
        return crossCompetitionCoin;
    }

    public void setCrossCompetitionCoin(AtomicLong crossCompetitionCoin) {
        this.crossCompetitionCoin = crossCompetitionCoin;
    }

    public long getArmyBrawlingCoin() {
        return armyBrawlingCoin.get();
    }

    public void setArmyBrawlingCoin(long armyBrawlingCoin) {
        this.armyBrawlingCoin.compareAndSet(this.armyBrawlingCoin.get(), armyBrawlingCoin);
    }

    public long getTransportCoin() {
        return transportCoin.get();
    }

    public void setTransportCoin(long transportCoin) {
        this.transportCoin.compareAndSet(this.transportCoin.get(), transportCoin);
    }

    public long getCollectiblesGetCoin() {
        return collectiblesGetCoin.get();
    }

    public void setCollectiblesGetCoin(long collectiblesGetCoin) {
        this.collectiblesGetCoin.compareAndSet(this.collectiblesGetCoin.get(), collectiblesGetCoin);
    }

    public int getPetRecruitScore() {
        return petRecruitScore.get();
    }

    public void setPetRecruitScore(int petRecruitScore) {
        this.petRecruitScore.compareAndSet(getPetRecruitScore(), petRecruitScore);
    }
}
