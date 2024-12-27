package com.ycw.gm.admin.tcp.constant;

public interface Commands {

	// 心跳
	int req_hearbeat = 1000;
	int resp_hearbeat = 2000;

	// 注册帐号
	int req_registerACT = 1001;
	int resp_registerACT = 2001;

	// 查看服务器列表指定sid区间
	int res_gsrvlist = 1002;
	int resp_gsrvlist = 2002;

	// 查看服务器列表指定编号区间
	int res_gsrvlimit = 1003;
	int resp_gsrvlimit = 2003;

	// 查看游戏公告
	int req_gameContent = 1004;
	int resp_gameContent = 2004;

	int req_closeConnect = 1005;
	int resp_closeConnect = 2005;

	//// 0=常规 1=审核 2=正式 3=测试
	public static final int NONE_SERVER_TYPE = 0;
	public static final int SHENHE_SERVER_TYPE = 1;
	public static final int NORMAL_SERVER_TYPE = 2;
	public static final int TEST_SERVER_TYPE = 3;

	// sdk Type
	// 一元
	int SDK_TYPE_1 = 1;
	// 小7
	int SDK_TYPE_2 = 2;
	// qq
	int SDK_TYPE_3 = 3;
}
