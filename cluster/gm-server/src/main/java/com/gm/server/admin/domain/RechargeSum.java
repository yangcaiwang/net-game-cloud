package com.gm.server.admin.domain;

import com.gm.server.common.annotation.Excel;
import com.gm.server.common.core.domain.BaseEntity;


/**
 * @author wishcher tree
 * @date 2022/8/3 15:32
 */
public class RechargeSum extends BaseEntity {

    private Long serverKeyId;
    @Excel(name = "服务器名")
    private String serverName;

    @Excel(name = "总充值")
    private String sumMoney;


    public String getServerName() {
        return serverName;
    }

    public void setServerName(String serverName) {
        this.serverName = serverName;
    }

    public String getSumMoney() {
        return sumMoney;
    }

    public void setSumMoney(String sumMoney) {
        this.sumMoney = sumMoney;
    }

    public Long getServerKeyId() {
        return serverKeyId;
    }

    public void setServerKeyId(Long serverKeyId) {
        this.serverKeyId = serverKeyId;
    }
}
