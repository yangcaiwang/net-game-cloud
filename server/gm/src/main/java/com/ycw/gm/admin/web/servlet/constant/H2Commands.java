package com.ycw.gm.admin.web.servlet.constant;

public interface H2Commands {

	/**
	 * 检测帐号是否存在
	 */
	int CHECK_ACT = -10001;
	/**
	 * 获取帐号绑定信息
	 */
	int GET_ACT_BINDINFO = -10002;
	/**
	 * 帐号绑定信息
	 */
	int ACT_BINDINFO = -10003;
	/**
	 * sdk充值
	 */
	int SDK_RECHARGE = -10004;
	/**
	 * sdk登录校验
	 */
	int SDK_LOGIN_AUTH = -10005;
	
	/**
	 * 通知游戏服修改开服时间
	 */
	int UPDATE_OPENTIME = -10006;

	/**
	 * 更新服务器状态
	 */
	int UPDATE_SERVER_STATUS = -10007;

	/**
	 * 硬核sdk登录校验
	 */
	int YING_HE_SDK_LOGIN_AUTH = -10008;

	/**
	 * 服务器白名单状态
	 */
	int SERVER_WHITELIST_STATE = -10009;
	/**
	 * 删除游戏
	 */
	int DELETE_ROLE = -10010;

	/**
	 * 异常信息
	 */
	int EXCEPTION_MESSAGE = -10011;
	/**
	 * 更新服务器开服时间
	 */
	int UPDATE_SERVER_OPEN_TIME = -10012;
	/**
	 * 更新服务器状态
	 */
	int UPDATE_SERVER_STATE = -10013;

	/**
	 * 充值前，充值参数获取
	 * 获取qq url充值链接
	 */
	int RECHARGE_BEFORE_ARGS_GET = -10014;

	/**
	 * 商城商品发放
	 */
	int SHOP_GOODS_SEND = -10015;

	/**
	 * VIP客服
	 */
	int VIP_CUSTOMER_SERVICE_SEND = -10016;

	/**
	 * 礼包发放
	 */
	int GIFT_SEND = -10017;

	/**
	 * 服务器白名单状态-新的状态
	 */
	int SERVER_WHITELIST_STATE_TYPE = -10018;
}
