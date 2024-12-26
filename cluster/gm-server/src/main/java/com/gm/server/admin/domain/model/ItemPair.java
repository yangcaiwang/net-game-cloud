package com.gm.server.admin.domain.model;

import java.io.Serializable;

/**
 * @Auther: wuwei
 * @Date: 2019/7/15 17:28
 * @Description:
 */
public class ItemPair implements Serializable {
    private int refId;
    private int num;

    public ItemPair() {
    }

    public ItemPair(int refId, int num) {
	this.refId = refId;
	this.num = num;
    }

    public int getRefId() {
	return refId;
    }

    public void setRefId(int refId) {
	this.refId = refId;
    }

    public int getNum() {
	return num;
    }

    public void setNum(int num) {
	this.num = num;
    }

    public void addNum(int num) {
	this.num += num;
    }

}
