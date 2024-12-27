//
// 由游戏编辑器自动创建修改,请勿修改
//

package com.ycw.gm.admin.base.config;

import java.io.Serializable;

public class GameArgConfig implements Serializable {

    /// <summary>    </summary>
    private int id;


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    /// <summary>  游戏ID  </summary>
    private int appId;


    public int getAppId() {
        return appId;
    }

    public void setAppId(int appId) {
        this.appId = appId;
    }

    /// <summary>    </summary>
    private String[] agent;


    public String[] getAgent() {
        return agent;
    }

    public void setAgent(String[] agent) {
        this.agent = agent;
    }

    /// <summary>  游戏密钥  </summary>
    private String gameKey;


    public String getGameKey() {
        return gameKey;
    }

    public void setGameKey(String gameKey) {
        this.gameKey = gameKey;
    }

    /// <summary>  支付秘钥  </summary>
    private String payKey;


    public String getPayKey() {
        return payKey;
    }

    public void setPayKey(String payKey) {
        this.payKey = payKey;
    }

    /// <summary>  登陆url  </summary>
    private String sdkLoginUrl;


    public String getSdkLoginUrl() {
        return sdkLoginUrl;
    }

    public void setSdkLoginUrl(String sdkLoginUrl) {
        this.sdkLoginUrl = sdkLoginUrl;
    }

    /// <summary>  充值查询接口  </summary>
    private String recallUrl;


    public String getRecallUrl() {
        return recallUrl;
    }

    public void setRecallUrl(String recallUrl) {
        this.recallUrl = recallUrl;
    }

    /// <summary>    </summary>
    private int gameId;


    public int getGameId() {
        return gameId;
    }

    public void setGameId(int gameId) {
        this.gameId = gameId;
    }

    /// <summary>    </summary>
    private int platformId;


    public int getPlatformId() {
        return platformId;
    }

    public void setPlatformId(int platformId) {
        this.platformId = platformId;
    }

    /// <summary>    </summary>
    private int methodType;


    public int getMethodType() {
        return methodType;
    }

    public void setMethodType(int methodType) {
        this.methodType = methodType;
    }

}