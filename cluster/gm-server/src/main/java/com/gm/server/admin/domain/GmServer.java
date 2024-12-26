package com.gm.server.admin.domain;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.gm.server.common.annotation.Excel;
import com.gm.server.common.annotation.Excels;
import com.gm.server.common.core.domain.BaseEntity;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import java.util.Date;

/**
 * 角色表 gm_server
 * 
 * @author gamer
 */
public class GmServer extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    @Excel(name = "serverKeyId-唯一标识", cellType = Excel.ColumnType.NUMERIC)
    private Long serverKeyId;

    /** 服务器ID */
    @Excel(name = "serverId-服务器编号", cellType = Excel.ColumnType.NUMERIC)
    private Long serverId;

    @Excel(name = "platformId-平台编号", cellType = Excel.ColumnType.NUMERIC)
    private Long platformId;

    /** 服务器名称 */
    @Excel(name = "serverName-名称")
    private String serverName;

    /** 服务器排序 */
    @Excel(name = "sort-排序")
    private String sort;

    @Excel(name = "outHost-外网IP")
    private String outHost;

    @Excel(name = "inHost-内网IP")
    private String inHost;

    @Excel(name = "clientPort-客户端端口")
    private String clientPort;
    @Excel(name = "inPort-内网端口")
    private Integer inPort;

    @Excel(name = "openTime-开服时间", dateFormat = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date openTime;

    @Excel(name = "dbUrl-游戏数据库url")
    private String dbUrl;
    @Excel(name = "dbLogUrl-日志数据库url")
    private String dbLogUrl;
    @Excel(name = "dbUser-数据库用户名")
    private String dbUser;
    @Excel(name = "dbPass-数据库密码")
    private String dbPass;
    /**运行状态 1=运行,0=停服**/
    @Excel(name = "runStatus-运行状态（1 or 0）")
    private String runStatus;

    @Excel(name = "showOut-对外放开（1 or 0）")
    private String showOut;

    private String serverType;

    /** 服务器状态 */
    @Excel(name = "serverStatus-服务器状态(0,1,2,6)")
    private String serverStatus;

    private Integer timeOpen;

    @Excel(name = "homePath-所在路径")
    private String homePath;

    // 1=开启 0或者空=关闭
    private String clientLog;

    // 服务器注册
    private Integer registerSwitch;
    @Excels({
            @Excel(name = "平台Id", targetAttr = "platformId", type = Excel.Type.EXPORT),
            @Excel(name = "平台名", targetAttr = "platformName", type = Excel.Type.EXPORT)
    })
    private GmPlatform platform;

    public GmServer() {

    }

    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
                .append("serverKeyId", getServerKeyId())
                .append("serverId", getServerId())
                .append("serverName", getServerName())
                .append("platformId", getPlatformId())
            .append("outHost", getOutHost())
                .append("inHost", getInHost())
                .append("clientPort", getClientPort())
                .append("inPort", getInPort())
                .append("openTime", getOpenTime())
            .append("sort", getSort())
                .append("dbUrl", getDbUrl())
                .append("dbLogUrl", getDbLogUrl())
                .append("dbUser", getDbUser())
                .append("dbPass", getDbPass())
                .append("serverStatus", getServerStatus())
                .append("runStatus", getRunStatus())
                .append("showOut", getShowOut())
                .append("serverType", getServerType())
                .append("homePath", getHomePath())
            .append("createBy", getCreateBy())
            .append("createTime", getCreateTime())
            .append("updateBy", getUpdateBy())
            .append("updateTime", getUpdateTime())
            .append("remark", getRemark())
                .append("clientLog", getClientLog())
                .append("platform", getPlatform())
            .toString();
    }

    public Long getPlatformId() {
        return platformId;
    }

    public void setPlatformId(Long platformId) {
        this.platformId = platformId;
    }

    public String getSort() {
        return sort;
    }


    public Long getServerKeyId() {
        return serverKeyId;
    }

    public void setServerKeyId(Long serverKeyId) {
        this.serverKeyId = serverKeyId;
    }

    public Long getServerId() {
        return serverId;
    }

    public void setServerId(Long serverId) {
        this.serverId = serverId;
    }

    public String getServerName() {
        return serverName;
    }

    public void setServerName(String serverName) {
        this.serverName = serverName;
    }

    public String getServerStatus() {
        return serverStatus;
    }

    public void setServerStatus(String serverStatus) {
        this.serverStatus = serverStatus;
    }

    public void setSort(String sort) {
        this.sort = sort;
    }

    public String getOutHost() {
        return outHost;
    }

    public void setOutHost(String outHost) {
        this.outHost = outHost;
    }

    public String getInHost() {
        return inHost;
    }

    public void setInHost(String inHost) {
        this.inHost = inHost;
    }

    public String getClientPort() {
        return clientPort;
    }

    public void setClientPort(String clientPort) {
        this.clientPort = clientPort;
    }

    public Integer getInPort() {
        return inPort;
    }

    public void setInPort(Integer inPort) {
        this.inPort = inPort;
    }

    public Date getOpenTime() {
        return openTime;
    }

    public void setOpenTime(Date openTime) {
        this.openTime = openTime;
    }

    public String getRunStatus() {
        return runStatus;
    }

    public void setRunStatus(String runStatus) {
        this.runStatus = runStatus;
    }

    public String getDbUrl() {
        return dbUrl;
    }

    public void setDbUrl(String dbUrl) {
        this.dbUrl = dbUrl;
    }

    public String getDbLogUrl() {
        return dbLogUrl;
    }

    public void setDbLogUrl(String dbLogUrl) {
        this.dbLogUrl = dbLogUrl;
    }

    public String getDbUser() {
        return dbUser;
    }

    public void setDbUser(String dbUser) {
        this.dbUser = dbUser;
    }

    public String getDbPass() {
        return dbPass;
    }

    public void setDbPass(String dbPass) {
        this.dbPass = dbPass;
    }

    public GmPlatform getPlatform() {
        return platform;
    }

    public void setPlatform(GmPlatform platform) {
        this.platform = platform;
    }

    public String getShowOut() {
        return showOut;
    }

    public void setShowOut(String showOut) {
        this.showOut = showOut;
    }

    public String getServerType() {
        return serverType;
    }

    public void setServerType(String serverType) {
        this.serverType = serverType;
    }

    public Integer getTimeOpen() {
        return timeOpen;
    }

    public void setTimeOpen(Integer timeOpen) {
        this.timeOpen = timeOpen;
    }

    public String getHomePath() {
        return homePath;
    }

    public void setHomePath(String homePath) {
        this.homePath = homePath;
    }

    public Integer getRegisterSwitch() {
        return registerSwitch;
    }

    public void setRegisterSwitch(Integer registerSwitch) {
        this.registerSwitch = registerSwitch;
    }

    public String getClientLog() {
        return clientLog;
    }

    public void setClientLog(String clientLog) {
        this.clientLog = clientLog;
    }
}
