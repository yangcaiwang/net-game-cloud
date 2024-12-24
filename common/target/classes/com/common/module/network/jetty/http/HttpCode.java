package com.common.module.network.jetty.http;

public enum HttpCode {
	SUCCESS_NO_RETURN("成功无返回值", -1),
	SUCCESS("成功", 0),
	AUTH_FAILURE("鉴权失败", 1),
	NO_PERMISSION("无权访问", 2),
	METHOD_WRONG ("方法错误", 3),
	SYSTEM_ERROR("内部异常", 4);
	// 成员变量
	private String name;
	private int index;
	// 构造方法
	private HttpCode(String name, int index) {
		this.name = name;
		this.index = index;
	}
	// 普通方法
	public static String getName(int index) {
		for (HttpCode c : HttpCode.values()) {
			if (c.getIndex() == index) {
				return c.name;
			}
		}
		return null;
	}
	// get set 方法
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public int getIndex() {
		return index;
	}
	public void setIndex(int index) {
		this.index = index;
	}
}

