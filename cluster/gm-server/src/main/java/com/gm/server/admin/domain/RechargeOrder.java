package com.gm.server.admin.domain;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.gm.server.common.annotation.Excel;
import com.gm.server.common.core.domain.BaseEntity;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import java.util.Date;

/**
 * 充值订单记录对象 recharge_order___1
 * 
 * @author gamer
 * @date 2022-07-27
 */
public class RechargeOrder extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 订单号 */
    @Excel(name = "订单号")
    private String order;

    /** 账户 */
    @Excel(name = "账户")
    private String uid;

    /** 商品id */
    @Excel(name = "商品id")
    private String productId;

    /** 渠道id */
    @Excel(name = "渠道id")
    private String channelId;

    /** 游戏id */
    private String gameId;

    /** 透传参数_平台id */
    private Integer attachPlatformId;

    /** 透传参数_服务器id */
    @Excel(name = "透传参数_服务器id")
    private Integer attachServerId;

    /** 请求内容 */
    private String request;

    /** 1成功 -1失败 */
    @Excel(name = "1成功 -1失败")
    private Integer resultCode;

    /** 失败原因 */
    @Excel(name = "失败原因")
    private String errorMsg;

    /** 充值金额 */
    @Excel(name = "充值金额")
    private String amount;

    /** 玩家id */
    @Excel(name = "玩家id")
    private Long roleId;

    /** 渠道订单id */
    @Excel(name = "渠道订单id")
    private String billNo;

    /** 是否是测试 */
    @Excel(name = "是否是测试")
    private String testType;

    /** 自增唯一long型主键 */
    private Long id;

    /** 创建时间 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Excel(name = "充值时间", dateFormat = "yyyy-MM-dd HH:mm:ss")
    private Date createTime;

    @Excel(name = "游戏ID")
    private String appId;

    public void setOrder(String order)
    {
        this.order = order;
    }

    public String getOrder() 
    {
        return order;
    }
    public void setUid(String uid) 
    {
        this.uid = uid;
    }

    public String getUid() 
    {
        return uid;
    }
    public void setProductId(String productId) 
    {
        this.productId = productId;
    }

    public String getProductId() 
    {
        return productId;
    }
    public void setChannelId(String channelId) 
    {
        this.channelId = channelId;
    }

    public String getChannelId() 
    {
        return channelId;
    }
    public void setGameId(String gameId) 
    {
        this.gameId = gameId;
    }

    public String getGameId() 
    {
        return gameId;
    }
    public void setAttachPlatformId(Integer attachPlatformId)
    {
        this.attachPlatformId = attachPlatformId;
    }

    public Integer getAttachPlatformId()
    {
        return attachPlatformId;
    }
    public void setAttachServerId(Integer attachServerId)
    {
        this.attachServerId = attachServerId;
    }

    public Integer getAttachServerId()
    {
        return attachServerId;
    }
    public void setRequest(String request) 
    {
        this.request = request;
    }

    public String getRequest() 
    {
        return request;
    }
    public void setResultCode(Integer resultCode)
    {
        this.resultCode = resultCode;
    }

    public Integer getResultCode()
    {
        return resultCode;
    }
    public void setErrorMsg(String errorMsg) 
    {
        this.errorMsg = errorMsg;
    }

    public String getErrorMsg() 
    {
        return errorMsg;
    }
    public void setAmount(String amount) 
    {
        this.amount = amount;
    }

    public String getAmount() 
    {
        return amount;
    }
    public void setRoleId(Long roleId) 
    {
        this.roleId = roleId;
    }

    public Long getRoleId() 
    {
        return roleId;
    }
    public void setBillNo(String billNo) 
    {
        this.billNo = billNo;
    }

    public String getBillNo() 
    {
        return billNo;
    }
    public void setTestType(String testType) 
    {
        this.testType = testType;
    }

    public String getTestType() 
    {
        return testType;
    }
    public void setId(Long id) 
    {
        this.id = id;
    }

    public Long getId() 
    {
        return id;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
            .append("order", getOrder())
            .append("uid", getUid())
            .append("productId", getProductId())
            .append("channelId", getChannelId())
            .append("gameId", getGameId())
            .append("attachPlatformId", getAttachPlatformId())
            .append("attachServerId", getAttachServerId())
            .append("request", getRequest())
            .append("resultCode", getResultCode())
            .append("errorMsg", getErrorMsg())
            .append("amount", getAmount())
            .append("roleId", getRoleId())
            .append("billNo", getBillNo())
            .append("testType", getTestType())
                .append("appId", getAppId())
            .append("id", getId())
            .toString();
    }

    @Override
    public Date getCreateTime() {
        return createTime;
    }

    @Override
    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public String getAppId() {
        return appId;
    }

    public void setAppId(String appId) {
        this.appId = appId;
    }
}
