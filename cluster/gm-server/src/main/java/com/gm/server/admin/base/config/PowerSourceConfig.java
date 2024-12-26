package com.gm.server.admin.base.config;

import java.io.Serializable;

/** 工具自动生成的pojo **/
public class PowerSourceConfig implements Serializable {

	/**  **/
	private int id;

	/** 流水名 **/
	private String sourceName;

	public void setId(int id) {
		this.id = id;
	}

	public int getId() {
		return id;
	}

	public void setSourceName(String sourceName) {
		this.sourceName = sourceName;
	}

	public String getSourceName() {
		return sourceName;
	}

}