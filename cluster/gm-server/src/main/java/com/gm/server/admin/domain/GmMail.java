package com.gm.server.admin.domain;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.gm.server.admin.domain.model.ItemPair;
import com.gm.server.common.core.domain.BaseEntity;

import java.util.Date;
import java.util.List;

/**
 * @author wishcher tree
 * @date 2022/5/10 13:58
 */
public class GmMail extends BaseEntity {
    private static final long serialVersionUID = 1L;

    private Long id;

    private String serverList;

    private String targetIds;

    private String title;

    private String content;

    private String sendType;

    private String items;

    private String mailStatus;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date validTime;

    private List<String> servers;
    private String allServer;
    private List<ItemPair> dynamicItem;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getServerList() {
        return serverList;
    }

    public void setServerList(String serverList) {
        this.serverList = serverList;
    }

    public String getTargetIds() {
        return targetIds;
    }

    public void setTargetIds(String targetIds) {
        this.targetIds = targetIds;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getSendType() {
        return sendType;
    }

    public void setSendType(String sendType) {
        this.sendType = sendType;
    }

    public String getItems() {
        return items;
    }

    public void setItems(String items) {
        this.items = items;
    }

    public String getMailStatus() {
        return mailStatus;
    }

    public void setMailStatus(String mailStatus) {
        this.mailStatus = mailStatus;
    }

    public Date getValidTime() {
        return validTime;
    }

    public void setValidTime(Date validTime) {
        this.validTime = validTime;
    }

    public List<String> getServers() {
        return servers;
    }

    public void setServers(List<String> servers) {
        this.servers = servers;
    }

    public String getAllServer() {
        return allServer;
    }

    public void setAllServer(String allServer) {
        this.allServer = allServer;
    }

    public List<ItemPair> getDynamicItem() {
        return dynamicItem;
    }

    public void setDynamicItem(List<ItemPair> dynamicItem) {
        this.dynamicItem = dynamicItem;
    }
}
