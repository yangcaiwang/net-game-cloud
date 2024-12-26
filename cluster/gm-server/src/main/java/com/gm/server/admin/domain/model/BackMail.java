package com.gm.server.admin.domain.model;


import org.apache.commons.compress.utils.Lists;

import java.io.Serializable;
import java.util.List;

public class BackMail implements Serializable {

    private long addresseeId;
    private String channel;

    /** 邮件类型 1系统2产业3战报4军团 */
    private int genre;

    private String title;

    private String context;

    private long validTime;

    private List<ItemPair> annex = Lists.newArrayList();

    public long getAddresseeId() {
	return addresseeId;
    }

    public void setAddresseeId(long addresseeId) {
	this.addresseeId = addresseeId;
    }

    public String getChannel() {
	return channel;
    }

    public void setChannel(String channel) {
	this.channel = channel;
    }

    public int getGenre() {
	return genre;
    }

    public void setGenre(int genre) {
	this.genre = genre;
    }

    public String getTitle() {
	return title;
    }

    public void setTitle(String title) {
	this.title = title;
    }

    public String getContext() {
	return context;
    }

    public void setContext(String context) {
	this.context = context;
    }

    public long getValidTime() {
	return validTime;
    }

    public void setValidTime(long validTime) {
	this.validTime = validTime;
    }

    public List<ItemPair> getAnnex() {
	return annex;
    }

    public void setAnnex(List<ItemPair> annex) {
	this.annex = annex;
    }
}
