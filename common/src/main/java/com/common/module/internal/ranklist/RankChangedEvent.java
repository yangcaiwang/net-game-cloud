
package com.common.module.internal.ranklist;


import com.common.module.internal.event.AbstractEvent;

/**
 * <排行榜变化事件类>
 * <p>
 *
 * @author <yangcaiwang>
 * @version <1.0>
 */
public class RankChangedEvent extends AbstractEvent {

    public static enum RankModifyType {
        UP, // 排名上升
        DOWN,// 排名下降
    }

    /**
     * 排行榜名称
     */
    public String rankName;

    /**
     * 成员ID
     */
    public String memberId;

    /**
     * 变化的名次
     */
    public int modified;

    /**
     * 当前排名 ,值越大排名越靠后
     */
    public int currentRank;

    public RankModifyType modifyType;

    public RankChangedEvent(String rankName, String memberId, int modified, int currentRank, RankModifyType modifyType) {
        super();
        this.rankName = rankName;
        this.memberId = memberId;
        this.modified = modified;
        this.currentRank = currentRank;
        this.modifyType = modifyType;
    }

    public String getRankName() {

        return rankName;
    }

    public void setRankName(String rankName) {

        this.rankName = rankName;
    }

    public String getMemberId() {

        return memberId;
    }

    public void setMemberId(String memberId) {

        this.memberId = memberId;
    }

    public int getModified() {

        return modified;
    }

    public void setModified(int modified) {

        this.modified = modified;
    }

    public int getCurrentRank() {
        return currentRank;
    }

    public void setCurrentRank(int currentRank) {
        this.currentRank = currentRank;
    }

    public RankModifyType getModifyType() {
        return modifyType;
    }

    public void setModifyType(RankModifyType modifyType) {
        this.modifyType = modifyType;
    }

}
