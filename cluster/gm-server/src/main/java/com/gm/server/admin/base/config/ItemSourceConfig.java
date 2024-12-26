/** 工具自动生成的pojo **/
package com.gm.server.admin.base.config;

import java.io.Serializable;

public class ItemSourceConfig implements Serializable {

	/** 流水id **/
	private int id;

	/** 流水 **/
	private String sourceName;

	public void setId(int id) {
		this.id = id;
	}

	public int getId() {
		return id;
	}

	public String getSourceName() {
		return sourceName;
	}

	public void setSourceName(String sourceName) {
		this.sourceName = sourceName;
	}
}