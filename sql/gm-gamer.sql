
create database if not exists db_gm default charset utf8mb4 collate utf8mb4_general_ci;
use db_gm;

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for activity_open
-- ----------------------------
DROP TABLE IF EXISTS `activity_open`;
CREATE TABLE `activity_open`  (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '唯一id',
  `act_id` int(11) NOT NULL DEFAULT 0 COMMENT '活动id',
  `act_type` int(11) NOT NULL DEFAULT 0 COMMENT '活动类型',
  `act_time` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL COMMENT '活动时间',
  `not_open_day` int(11) NOT NULL DEFAULT 0 COMMENT '开服x天不开',
  `force_end` int(11) NOT NULL DEFAULT 0 COMMENT '>0强制结束活动',
  `open_channel` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '开启渠道（|分隔）',
  `status` int(11) NOT NULL DEFAULT 0 COMMENT '状态',
  `server_id` varchar(4096) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '服务器id',
  `create_by` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '创建名',
  `create_time` datetime NULL DEFAULT NULL COMMENT '创建时间',
  `update_by` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '更新名',
  `update_time` datetime NULL DEFAULT NULL COMMENT '更新时间',
  `open_hour` int(11) NOT NULL DEFAULT 0 COMMENT '开启时间点',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '运营活动开启时间' ROW_FORMAT = Compact;


-- ----------------------------
-- Table structure for exception_log
-- ----------------------------
DROP TABLE IF EXISTS `exception_log`;
CREATE TABLE `exception_log`  (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '唯一id',
  `player_id` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '玩家ID',
  `server_id` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '服务器id',
  `io_session` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL COMMENT '网络连接句柄',
  `exception` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL COMMENT '异常信息',
  `req` int(11) NOT NULL DEFAULT 0 COMMENT '上行协议ID',
  `resp` int(11) NOT NULL DEFAULT 0 COMMENT '下行协议ID',
  `create_by` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '创建名',
  `create_time` datetime NULL DEFAULT NULL COMMENT '创建时间',
  `update_by` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '更新名',
  `update_time` datetime NULL DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_player_id`(`player_id`) USING BTREE,
  INDEX `idx_server_id`(`server_id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '服务器异常记录' ROW_FORMAT = Compact;

-- ----------------------------
-- Table structure for gen_table
-- ----------------------------
DROP TABLE IF EXISTS `gen_table`;
CREATE TABLE `gen_table`  (
  `table_id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '编号',
  `table_name` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT '表名称',
  `table_comment` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT '表描述',
  `sub_table_name` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '关联子表的表名',
  `sub_table_fk_name` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '子表关联的外键名',
  `class_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT '实体类名称',
  `tpl_category` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT 'crud' COMMENT '使用的模板（crud单表操作 tree树表操作）',
  `package_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '生成包路径',
  `module_name` varchar(30) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '生成模块名',
  `business_name` varchar(30) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '生成业务名',
  `function_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '生成功能名',
  `function_author` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '生成功能作者',
  `gen_type` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '0' COMMENT '生成代码方式（0zip压缩包 1自定义路径）',
  `gen_path` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '/' COMMENT '生成路径（不填默认项目路径）',
  `options` varchar(1000) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '其它生成选项',
  `create_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT '创建者',
  `create_time` datetime NULL DEFAULT NULL COMMENT '创建时间',
  `update_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT '更新者',
  `update_time` datetime NULL DEFAULT NULL COMMENT '更新时间',
  `remark` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`table_id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '代码生成业务表' ROW_FORMAT = Compact;

-- ----------------------------
-- Table structure for gen_table_column
-- ----------------------------
DROP TABLE IF EXISTS `gen_table_column`;
CREATE TABLE `gen_table_column`  (
  `column_id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '编号',
  `table_id` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '归属表编号',
  `column_name` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '列名称',
  `column_comment` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '列描述',
  `column_type` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '列类型',
  `java_type` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT 'JAVA类型',
  `java_field` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT 'JAVA字段名',
  `is_pk` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '是否主键（1是）',
  `is_increment` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '是否自增（1是）',
  `is_required` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '是否必填（1是）',
  `is_insert` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '是否为插入字段（1是）',
  `is_edit` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '是否编辑字段（1是）',
  `is_list` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '是否列表字段（1是）',
  `is_query` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '是否查询字段（1是）',
  `query_type` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT 'EQ' COMMENT '查询方式（等于、不等于、大于、小于、范围）',
  `html_type` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '显示类型（文本框、文本域、下拉框、复选框、单选框、日期控件）',
  `dict_type` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT '字典类型',
  `sort` int(11) NULL DEFAULT NULL COMMENT '排序',
  `create_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT '创建者',
  `create_time` datetime NULL DEFAULT NULL COMMENT '创建时间',
  `update_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT '更新者',
  `update_time` datetime NULL DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`column_id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '代码生成业务表字段' ROW_FORMAT = Compact;

-- ----------------------------
-- Table structure for gm_add_item
-- ----------------------------
DROP TABLE IF EXISTS `gm_add_item`;
CREATE TABLE `gm_add_item`  (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '唯一id',
  `server_id` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '服务器',
  `role_ids` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '玩家',
  `item` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '道具',
  `status` int(11) NOT NULL DEFAULT 0 COMMENT '状态',
  `create_by` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '创建名',
  `create_time` datetime NULL DEFAULT NULL COMMENT '创建时间',
  `update_by` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '更新名',
  `update_time` datetime NULL DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '游戏管理  添加道具' ROW_FORMAT = Compact;

-- ----------------------------
-- Table structure for gm_broadcast
-- ----------------------------
DROP TABLE IF EXISTS `gm_broadcast`;
CREATE TABLE `gm_broadcast`  (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '唯一id',
  `server_id` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '服务器',
  `interval` int(11) NOT NULL DEFAULT 0 COMMENT '间隔时间',
  `times` int(11) NOT NULL DEFAULT 0 COMMENT '广播次数',
  `bc_content` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '广播内容',
  `status` int(11) NOT NULL DEFAULT 0 COMMENT '邮件状态',
  `create_by` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '创建名',
  `create_time` datetime NULL DEFAULT NULL COMMENT '创建时间',
  `update_by` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '更新名',
  `update_time` datetime NULL DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '游戏管理  跑马灯' ROW_FORMAT = Compact;

-- ----------------------------
-- Table structure for gm_execute_command
-- ----------------------------
DROP TABLE IF EXISTS `gm_execute_command`;
CREATE TABLE `gm_execute_command`  (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `role_id` bigint(20) NULL DEFAULT NULL,
  `command` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
  `create_by` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
  `create_time` datetime NULL DEFAULT NULL,
  `update_by` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
  `update_time` datetime NULL DEFAULT NULL,
  `status` int(2) NULL DEFAULT NULL,
  `server_id` int(10) NULL DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = Compact;

-- ----------------------------
-- Table structure for gm_game_version
-- ----------------------------
DROP TABLE IF EXISTS `gm_game_version`;
CREATE TABLE `gm_game_version`  (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '唯一id',
  `version` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '版本号',
  `version_type` int(11) NOT NULL DEFAULT 0 COMMENT '版本类型',
  `create_by` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '创建名',
  `create_time` datetime NULL DEFAULT NULL COMMENT '创建时间',
  `update_by` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '更新名',
  `update_time` datetime NULL DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '游戏版本管理' ROW_FORMAT = Compact;

-- ----------------------------
-- Table structure for gm_mail
-- ----------------------------
DROP TABLE IF EXISTS `gm_mail`;
CREATE TABLE `gm_mail`  (
  `id` bigint(21) NOT NULL AUTO_INCREMENT,
  `server_list` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
  `target_ids` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
  `title` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
  `content` varchar(2000) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
  `send_type` int(11) NULL DEFAULT NULL,
  `items` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
  `mail_status` int(4) NULL DEFAULT NULL,
  `create_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
  `create_time` datetime NULL DEFAULT NULL,
  `update_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
  `update_time` datetime NULL DEFAULT NULL,
  `remark` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
  `valid_time` datetime NULL DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = Compact;

-- ----------------------------
-- Table structure for gm_platform
-- ----------------------------
DROP TABLE IF EXISTS `gm_platform`;
CREATE TABLE `gm_platform`  (
  `platform_id` bigint(20) NOT NULL,
  `platform_name` varchar(30) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
  `sort` int(11) NULL DEFAULT NULL,
  `white_list_status` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
  `create_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
  `create_time` datetime NULL DEFAULT NULL,
  `update_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
  `update_time` datetime NULL DEFAULT NULL,
  `remark` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
  `auto_register_switch` int(4) NULL DEFAULT NULL,
  PRIMARY KEY (`platform_id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = Compact;


-- ----------------------------
-- Table structure for gm_recharge
-- ----------------------------
DROP TABLE IF EXISTS `gm_recharge`;
CREATE TABLE `gm_recharge`  (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `role_ids` varchar(1000) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
  `recharge_ids` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
  `server_id` int(10) NULL DEFAULT NULL,
  `status` int(2) NULL DEFAULT NULL,
  `create_by` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
  `create_time` datetime NULL DEFAULT NULL,
  `update_by` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
  `update_time` datetime NULL DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = Compact;

-- ----------------------------
-- Table structure for gm_server
-- ----------------------------
DROP TABLE IF EXISTS `gm_server`;
CREATE TABLE `gm_server`  (
  `server_key_id` bigint(20) NOT NULL,
  `server_id` bigint(20) NULL DEFAULT NULL,
  `server_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
  `platform_id` bigint(20) NULL DEFAULT NULL,
  `sort` int(11) NULL DEFAULT NULL,
  `out_host` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
  `in_host` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
  `client_port` int(11) NULL DEFAULT NULL,
  `in_port` int(11) NULL DEFAULT NULL,
  `open_time` datetime NULL DEFAULT NULL,
  `db_url` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
  `db_log_url` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
  `db_user` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
  `db_pass` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
  `run_status` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
  `server_status` int(4) NULL DEFAULT NULL,
  `create_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
  `create_time` datetime NULL DEFAULT NULL,
  `update_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
  `update_time` datetime NULL DEFAULT NULL,
  `remark` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
  `show_out` char(2) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
  `server_type` char(4) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
  `home_path` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
  `register_switch` int(4) NULL DEFAULT NULL,
  PRIMARY KEY (`server_key_id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = Compact;

-- ----------------------------
-- Table structure for gm_white_list
-- ----------------------------
DROP TABLE IF EXISTS `gm_white_list`;
CREATE TABLE `gm_white_list`  (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '唯一id',
  `white_type` int(11) NOT NULL DEFAULT 0 COMMENT '白名单类型',
  `platform_id` int(11) NOT NULL DEFAULT 0 COMMENT '平台id',
  `args` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL COMMENT '白名单参数，根据类型定值',
  `create_by` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '创建名',
  `create_time` datetime NULL DEFAULT NULL COMMENT '创建时间',
  `update_by` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '更新名',
  `update_time` datetime NULL DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '白名单' ROW_FORMAT = Compact;


-- ----------------------------
-- Table structure for recharge_order___1
-- ----------------------------
DROP TABLE IF EXISTS `recharge_order___1`;
CREATE TABLE `recharge_order___1`  (
  `order` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '订单号',
  `uid` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '账户',
  `product_id` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '商品id',
  `channel_id` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '渠道id',
  `game_id` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '游戏id',
  `attach_platform_id` int(11) NOT NULL DEFAULT 0 COMMENT '透传参数_平台id',
  `attach_server_id` int(11) NOT NULL DEFAULT 0 COMMENT '透传参数_服务器id',
  `request` varchar(1024) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '请求内容',
  `result_code` int(11) NOT NULL DEFAULT 0 COMMENT '1成功 -1失败',
  `error_msg` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '失败原因',
  `amount` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '充值金额',
  `role_id` bigint(20) NOT NULL DEFAULT 0 COMMENT '玩家id',
  `bill_no` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '渠道订单id',
  `test_type` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '是否是测试',
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '自增唯一long型主键',
  `create_time` datetime NULL DEFAULT NULL COMMENT '创建时间',
  `app_id` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '游戏ID',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '充值订单记录' ROW_FORMAT = Compact;

-- ----------------------------
-- Table structure for recharge_order___2
-- ----------------------------
DROP TABLE IF EXISTS `recharge_order___2`;
CREATE TABLE `recharge_order___2`  (
  `order` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '订单号',
  `uid` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '账户',
  `product_id` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '商品id',
  `channel_id` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '渠道id',
  `game_id` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '游戏id',
  `attach_platform_id` int(11) NOT NULL DEFAULT 0 COMMENT '透传参数_平台id',
  `attach_server_id` int(11) NOT NULL DEFAULT 0 COMMENT '透传参数_服务器id',
  `request` varchar(1024) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '请求内容',
  `result_code` int(11) NOT NULL DEFAULT 0 COMMENT '1成功 -1失败',
  `error_msg` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '失败原因',
  `amount` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '充值金额',
  `role_id` bigint(20) NOT NULL DEFAULT 0 COMMENT '玩家id',
  `bill_no` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '渠道订单id',
  `test_type` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '是否是测试',
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '自增唯一long型主键',
  `create_time` datetime NULL DEFAULT NULL COMMENT '创建时间',
  `app_id` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '游戏ID',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '充值订单记录' ROW_FORMAT = Compact;

-- ----------------------------
-- Table structure for recharge_order___3
-- ----------------------------
DROP TABLE IF EXISTS `recharge_order___3`;
CREATE TABLE `recharge_order___3`  (
  `order` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '订单号',
  `uid` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '账户',
  `product_id` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '商品id',
  `channel_id` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '渠道id',
  `game_id` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '游戏id',
  `attach_platform_id` int(11) NOT NULL DEFAULT 0 COMMENT '透传参数_平台id',
  `attach_server_id` int(11) NOT NULL DEFAULT 0 COMMENT '透传参数_服务器id',
  `request` varchar(1024) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '请求内容',
  `result_code` int(11) NOT NULL DEFAULT 0 COMMENT '1成功 -1失败',
  `error_msg` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '失败原因',
  `amount` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '充值金额',
  `role_id` bigint(20) NOT NULL DEFAULT 0 COMMENT '玩家id',
  `bill_no` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '渠道订单id',
  `test_type` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '是否是测试',
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '自增唯一long型主键',
  `create_time` datetime NULL DEFAULT NULL COMMENT '创建时间',
  `app_id` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '游戏ID',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '充值订单记录' ROW_FORMAT = Compact;

-- ----------------------------
-- Table structure for recharge_order___4
-- ----------------------------
DROP TABLE IF EXISTS `recharge_order___4`;
CREATE TABLE `recharge_order___4`  (
  `order` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '订单号',
  `uid` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '账户',
  `product_id` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '商品id',
  `channel_id` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '渠道id',
  `game_id` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '游戏id',
  `attach_platform_id` int(11) NOT NULL DEFAULT 0 COMMENT '透传参数_平台id',
  `attach_server_id` int(11) NOT NULL DEFAULT 0 COMMENT '透传参数_服务器id',
  `request` varchar(1024) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '请求内容',
  `result_code` int(11) NOT NULL DEFAULT 0 COMMENT '1成功 -1失败',
  `error_msg` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '失败原因',
  `amount` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '充值金额',
  `role_id` bigint(20) NOT NULL DEFAULT 0 COMMENT '玩家id',
  `bill_no` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '渠道订单id',
  `test_type` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '是否是测试',
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '自增唯一long型主键',
  `create_time` datetime NULL DEFAULT NULL COMMENT '创建时间',
  `app_id` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '游戏ID',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '充值订单记录' ROW_FORMAT = Compact;

-- ----------------------------
-- Table structure for recharge_order___5
-- ----------------------------
DROP TABLE IF EXISTS `recharge_order___5`;
CREATE TABLE `recharge_order___5`  (
  `order` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '订单号',
  `uid` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '账户',
  `product_id` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '商品id',
  `channel_id` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '渠道id',
  `game_id` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '游戏id',
  `attach_platform_id` int(11) NOT NULL DEFAULT 0 COMMENT '透传参数_平台id',
  `attach_server_id` int(11) NOT NULL DEFAULT 0 COMMENT '透传参数_服务器id',
  `request` varchar(1024) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '请求内容',
  `result_code` int(11) NOT NULL DEFAULT 0 COMMENT '1成功 -1失败',
  `error_msg` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '失败原因',
  `amount` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '充值金额',
  `role_id` bigint(20) NOT NULL DEFAULT 0 COMMENT '玩家id',
  `bill_no` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '渠道订单id',
  `test_type` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '是否是测试',
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '自增唯一long型主键',
  `create_time` datetime NULL DEFAULT NULL COMMENT '创建时间',
  `app_id` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '游戏ID',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '充值订单记录' ROW_FORMAT = Compact;

-- ----------------------------
-- Table structure for sys_config
-- ----------------------------
DROP TABLE IF EXISTS `sys_config`;
CREATE TABLE `sys_config`  (
  `config_id` int(5) NOT NULL AUTO_INCREMENT COMMENT '参数主键',
  `config_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT '参数名称',
  `config_key` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT '参数键名',
  `config_value` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT '参数键值',
  `config_type` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT 'N' COMMENT '系统内置（Y是 N否）',
  `create_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT '创建者',
  `create_time` datetime NULL DEFAULT NULL COMMENT '创建时间',
  `update_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT '更新者',
  `update_time` datetime NULL DEFAULT NULL COMMENT '更新时间',
  `remark` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`config_id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 6 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '参数配置表' ROW_FORMAT = Compact;

-- ----------------------------
-- Records of sys_config
-- ----------------------------
INSERT INTO `sys_config` VALUES (1, '主框架页-默认皮肤样式名称', 'sys.index.skinName', 'skin-blue', 'Y', 'admin', '2022-04-26 13:52:27', '', NULL, '蓝色 skin-blue、绿色 skin-green、紫色 skin-purple、红色 skin-red、黄色 skin-yellow');
INSERT INTO `sys_config` VALUES (2, '用户管理-账号初始密码', 'sys.user.initPassword', '123456', 'Y', 'admin', '2022-04-26 13:52:27', '', NULL, '初始化密码 123456');
INSERT INTO `sys_config` VALUES (3, '主框架页-侧边栏主题', 'sys.index.sideTheme', 'theme-dark', 'Y', 'admin', '2022-04-26 13:52:27', '', NULL, '深色主题theme-dark，浅色主题theme-light');
INSERT INTO `sys_config` VALUES (4, '账号自助-验证码开关', 'sys.account.captchaOnOff', 'true', 'Y', 'admin', '2022-04-26 13:52:27', '', NULL, '是否开启验证码功能（true开启，false关闭）');
INSERT INTO `sys_config` VALUES (5, '账号自助-是否开启用户注册功能', 'sys.account.registerUser', 'false', 'Y', 'admin', '2022-04-26 13:52:27', '', NULL, '是否开启注册用户功能（true开启，false关闭）');

-- ----------------------------
-- Table structure for sys_dept
-- ----------------------------
DROP TABLE IF EXISTS `sys_dept`;
CREATE TABLE `sys_dept`  (
  `dept_id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '部门id',
  `parent_id` bigint(20) NULL DEFAULT 0 COMMENT '父部门id',
  `ancestors` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT '祖级列表',
  `dept_name` varchar(30) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT '部门名称',
  `order_num` int(4) NULL DEFAULT 0 COMMENT '显示顺序',
  `leader` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '负责人',
  `phone` varchar(11) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '联系电话',
  `email` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '邮箱',
  `status` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '0' COMMENT '部门状态（0正常 1停用）',
  `del_flag` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '0' COMMENT '删除标志（0代表存在 2代表删除）',
  `create_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT '创建者',
  `create_time` datetime NULL DEFAULT NULL COMMENT '创建时间',
  `update_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT '更新者',
  `update_time` datetime NULL DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`dept_id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 110 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '部门表' ROW_FORMAT = Compact;

-- ----------------------------
-- Records of sys_dept
-- ----------------------------
INSERT INTO `sys_dept` VALUES (100, 0, '0', 'pp科技', 0, 'admin', '15888888888', 'admin@qq.com', '0', '0', 'admin', '2022-04-26 13:52:23', 'admin', '2022-07-20 18:45:31');
INSERT INTO `sys_dept` VALUES (101, 100, '0,100', 'gzpp', 1, 'admin', '15888888888', 'admin@qq.com', '0', '0', 'admin', '2022-04-26 13:52:23', '51gamer_admin', '2022-08-03 10:50:05');
INSERT INTO `sys_dept` VALUES (103, 101, '0,100,101', '研发部门', 1, 'admin', '15888888888', 'admin@qq.com', '0', '0', 'admin', '2022-04-26 13:52:23', '51gamer_admin', '2022-08-03 10:50:01');
INSERT INTO `sys_dept` VALUES (105, 101, '0,100,101', '测试部门', 3, 'admin', '15888888888', 'admin@qq.com', '0', '0', 'admin', '2022-04-26 13:52:23', '51gamer_admin', '2022-08-03 10:49:54');


-- ----------------------------
-- Table structure for sys_dict_data
-- ----------------------------
DROP TABLE IF EXISTS `sys_dict_data`;
CREATE TABLE `sys_dict_data`  (
  `dict_code` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '字典编码',
  `dict_sort` int(4) NULL DEFAULT 0 COMMENT '字典排序',
  `dict_label` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT '字典标签',
  `dict_value` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT '字典键值',
  `dict_type` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT '字典类型',
  `css_class` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '样式属性（其他样式扩展）',
  `list_class` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '表格回显样式',
  `is_default` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT 'N' COMMENT '是否默认（Y是 N否）',
  `status` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '0' COMMENT '状态（0正常 1停用）',
  `create_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT '创建者',
  `create_time` datetime NULL DEFAULT NULL COMMENT '创建时间',
  `update_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT '更新者',
  `update_time` datetime NULL DEFAULT NULL COMMENT '更新时间',
  `remark` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`dict_code`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 50 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '字典数据表' ROW_FORMAT = Compact;

-- ----------------------------
-- Records of sys_dict_data
-- ----------------------------
INSERT INTO `sys_dict_data` VALUES (1, 1, '男', '0', 'sys_user_sex', '', '', 'Y', '0', 'admin', '2022-04-26 13:52:27', '', NULL, '性别男');
INSERT INTO `sys_dict_data` VALUES (2, 2, '女', '1', 'sys_user_sex', '', '', 'N', '0', 'admin', '2022-04-26 13:52:27', '', NULL, '性别女');
INSERT INTO `sys_dict_data` VALUES (3, 3, '未知', '2', 'sys_user_sex', '', '', 'N', '0', 'admin', '2022-04-26 13:52:27', '', NULL, '性别未知');
INSERT INTO `sys_dict_data` VALUES (4, 1, '显示', '0', 'sys_show_hide', '', 'primary', 'Y', '0', 'admin', '2022-04-26 13:52:27', '', NULL, '显示菜单');
INSERT INTO `sys_dict_data` VALUES (5, 2, '隐藏', '1', 'sys_show_hide', '', 'danger', 'N', '0', 'admin', '2022-04-26 13:52:27', '', NULL, '隐藏菜单');
INSERT INTO `sys_dict_data` VALUES (6, 1, '正常', '0', 'sys_normal_disable', '', 'primary', 'Y', '0', 'admin', '2022-04-26 13:52:27', '', NULL, '正常状态');
INSERT INTO `sys_dict_data` VALUES (7, 2, '停用', '1', 'sys_normal_disable', '', 'danger', 'N', '0', 'admin', '2022-04-26 13:52:27', '', NULL, '停用状态');
INSERT INTO `sys_dict_data` VALUES (8, 1, '正常', '0', 'sys_job_status', '', 'primary', 'Y', '0', 'admin', '2022-04-26 13:52:27', '', NULL, '正常状态');
INSERT INTO `sys_dict_data` VALUES (9, 2, '暂停', '1', 'sys_job_status', '', 'danger', 'N', '0', 'admin', '2022-04-26 13:52:27', '', NULL, '停用状态');
INSERT INTO `sys_dict_data` VALUES (10, 1, '默认', 'DEFAULT', 'sys_job_group', '', '', 'Y', '0', 'admin', '2022-04-26 13:52:27', '', NULL, '默认分组');
INSERT INTO `sys_dict_data` VALUES (11, 2, '系统', 'SYSTEM', 'sys_job_group', '', '', 'N', '0', 'admin', '2022-04-26 13:52:27', '', NULL, '系统分组');
INSERT INTO `sys_dict_data` VALUES (12, 1, '是', 'Y', 'sys_yes_no', '', 'primary', 'Y', '0', 'admin', '2022-04-26 13:52:27', '', NULL, '系统默认是');
INSERT INTO `sys_dict_data` VALUES (13, 2, '否', 'N', 'sys_yes_no', '', 'danger', 'N', '0', 'admin', '2022-04-26 13:52:27', '', NULL, '系统默认否');
INSERT INTO `sys_dict_data` VALUES (14, 1, '通知', '1', 'sys_notice_type', '', 'warning', 'Y', '0', 'admin', '2022-04-26 13:52:27', '', NULL, '通知');
INSERT INTO `sys_dict_data` VALUES (15, 2, '公告', '2', 'sys_notice_type', '', 'success', 'N', '0', 'admin', '2022-04-26 13:52:27', '', NULL, '公告');
INSERT INTO `sys_dict_data` VALUES (16, 1, '正常', '0', 'sys_notice_status', '', 'primary', 'Y', '0', 'admin', '2022-04-26 13:52:27', '', NULL, '正常状态');
INSERT INTO `sys_dict_data` VALUES (17, 2, '关闭', '1', 'sys_notice_status', '', 'danger', 'N', '0', 'admin', '2022-04-26 13:52:27', '', NULL, '关闭状态');
INSERT INTO `sys_dict_data` VALUES (18, 1, '新增', '1', 'sys_oper_type', '', 'info', 'N', '0', 'admin', '2022-04-26 13:52:27', '', NULL, '新增操作');
INSERT INTO `sys_dict_data` VALUES (19, 2, '修改', '2', 'sys_oper_type', '', 'info', 'N', '0', 'admin', '2022-04-26 13:52:27', '', NULL, '修改操作');
INSERT INTO `sys_dict_data` VALUES (20, 3, '删除', '3', 'sys_oper_type', '', 'danger', 'N', '0', 'admin', '2022-04-26 13:52:27', '', NULL, '删除操作');
INSERT INTO `sys_dict_data` VALUES (21, 4, '授权', '4', 'sys_oper_type', '', 'primary', 'N', '0', 'admin', '2022-04-26 13:52:27', '', NULL, '授权操作');
INSERT INTO `sys_dict_data` VALUES (22, 5, '导出', '5', 'sys_oper_type', '', 'warning', 'N', '0', 'admin', '2022-04-26 13:52:27', '', NULL, '导出操作');
INSERT INTO `sys_dict_data` VALUES (23, 6, '导入', '6', 'sys_oper_type', '', 'warning', 'N', '0', 'admin', '2022-04-26 13:52:27', '', NULL, '导入操作');
INSERT INTO `sys_dict_data` VALUES (24, 7, '强退', '7', 'sys_oper_type', '', 'danger', 'N', '0', 'admin', '2022-04-26 13:52:27', '', NULL, '强退操作');
INSERT INTO `sys_dict_data` VALUES (25, 8, '生成代码', '8', 'sys_oper_type', '', 'warning', 'N', '0', 'admin', '2022-04-26 13:52:27', '', NULL, '生成操作');
INSERT INTO `sys_dict_data` VALUES (26, 9, '清空数据', '9', 'sys_oper_type', '', 'danger', 'N', '0', 'admin', '2022-04-26 13:52:27', '', NULL, '清空操作');
INSERT INTO `sys_dict_data` VALUES (27, 1, '成功', '0', 'sys_common_status', '', 'primary', 'N', '0', 'admin', '2022-04-26 13:52:27', '', NULL, '正常状态');
INSERT INTO `sys_dict_data` VALUES (28, 2, '失败', '1', 'sys_common_status', '', 'danger', 'N', '0', 'admin', '2022-04-26 13:52:27', '', NULL, '停用状态');
INSERT INTO `sys_dict_data` VALUES (29, 0, '否', '0', 'white_list_status', NULL, 'default', 'N', '0', 'admin', '2022-05-06 17:17:53', '', NULL, NULL);
INSERT INTO `sys_dict_data` VALUES (30, 1, '是', '1', 'white_list_status', NULL, 'default', 'N', '0', 'admin', '2022-05-06 17:18:01', '', NULL, NULL);
INSERT INTO `sys_dict_data` VALUES (31, 0, '指定玩家', '1', 'gm_mail_sendtype', NULL, 'default', 'N', '0', 'admin', '2022-05-10 13:55:18', '', NULL, NULL);
INSERT INTO `sys_dict_data` VALUES (32, 0, '指定帮派', '2', 'gm_mail_sendtype', NULL, 'default', 'N', '0', 'admin', '2022-05-10 13:55:27', '', NULL, NULL);
INSERT INTO `sys_dict_data` VALUES (33, 0, '全服', '3', 'gm_mail_sendtype', NULL, 'default', 'N', '0', 'admin', '2022-05-10 13:55:34', '', NULL, NULL);
INSERT INTO `sys_dict_data` VALUES (34, 0, '无', '0', 'game_server_type', NULL, 'default', 'N', '0', 'admin', '2022-07-26 15:03:52', '', NULL, NULL);
INSERT INTO `sys_dict_data` VALUES (35, 0, '新服', '1', 'game_server_type', NULL, 'success', 'N', '0', 'admin', '2022-07-26 15:04:14', '', NULL, NULL);
INSERT INTO `sys_dict_data` VALUES (36, 0, '火爆', '2', 'game_server_type', NULL, 'danger', 'N', '0', 'admin', '2022-07-26 15:04:30', '', NULL, NULL);
INSERT INTO `sys_dict_data` VALUES (37, 0, '维护', '6', 'game_server_type', NULL, 'warning', 'N', '0', 'admin', '2022-07-26 15:04:53', '', NULL, NULL);
INSERT INTO `sys_dict_data` VALUES (38, 0, '通过', '1', 'check_auto', NULL, 'default', 'N', '0', 'admin', '2022-07-26 16:09:28', 'admin', '2022-07-26 16:09:43', NULL);
INSERT INTO `sys_dict_data` VALUES (39, 0, '拒绝', '2', 'check_auto', NULL, 'default', 'N', '0', 'admin', '2022-07-26 16:09:38', '', NULL, NULL);
INSERT INTO `sys_dict_data` VALUES (40, 0, 'IP白名单', '1', 'white_list_type', NULL, 'default', 'N', '0', 'admin', '2022-07-29 16:01:32', '', NULL, NULL);
INSERT INTO `sys_dict_data` VALUES (41, 0, '账号白名单', '2', 'white_list_type', NULL, 'default', 'N', '0', 'admin', '2022-07-29 16:01:41', '', NULL, NULL);
INSERT INTO `sys_dict_data` VALUES (42, 0, '待审核', '0', 'check_auto', NULL, 'default', 'N', '0', 'admin', '2022-08-01 13:46:25', '', NULL, NULL);
INSERT INTO `sys_dict_data` VALUES (43, 0, '取消', '3', 'check_auto', NULL, 'default', 'N', '0', 'admin', '2022-08-01 15:27:15', '', NULL, NULL);
INSERT INTO `sys_dict_data` VALUES (44, 0, '创角活动', '1', 'activity_open_type', NULL, 'default', 'N', '0', 'admin', '2022-08-02 13:33:43', '', NULL, NULL);
INSERT INTO `sys_dict_data` VALUES (45, 0, '开服活动', '2', 'activity_open_type', NULL, 'default', 'N', '0', 'admin', '2022-08-02 13:33:51', '', NULL, NULL);
INSERT INTO `sys_dict_data` VALUES (46, 0, '限时活动', '3', 'activity_open_type', NULL, 'default', 'N', '0', 'admin', '2022-08-02 13:34:03', '', NULL, NULL);
INSERT INTO `sys_dict_data` VALUES (47, 0, '永久活动', '4', 'activity_open_type', NULL, 'default', 'N', '0', 'admin', '2022-08-02 13:34:10', '', NULL, NULL);
INSERT INTO `sys_dict_data` VALUES (48, 0, '多开活动', '5', 'activity_open_type', NULL, 'default', 'N', '0', 'admin', '2022-08-02 13:34:10', '', NULL, NULL);
INSERT INTO `sys_dict_data` VALUES (49, 0, '周活动', '6', 'activity_open_type', NULL, 'default', 'N', '0', 'admin', '2022-08-02 13:34:10', '', NULL, NULL);

-- ----------------------------
-- Table structure for sys_dict_type
-- ----------------------------
DROP TABLE IF EXISTS `sys_dict_type`;
CREATE TABLE `sys_dict_type`  (
  `dict_id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '字典主键',
  `dict_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT '字典名称',
  `dict_type` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT '字典类型',
  `status` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '0' COMMENT '状态（0正常 1停用）',
  `create_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT '创建者',
  `create_time` datetime NULL DEFAULT NULL COMMENT '创建时间',
  `update_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT '更新者',
  `update_time` datetime NULL DEFAULT NULL COMMENT '更新时间',
  `remark` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`dict_id`) USING BTREE,
  UNIQUE INDEX `dict_type`(`dict_type`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 17 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '字典类型表' ROW_FORMAT = Compact;

-- ----------------------------
-- Records of sys_dict_type
-- ----------------------------
INSERT INTO `sys_dict_type` VALUES (1, '用户性别', 'sys_user_sex', '0', 'admin', '2022-04-26 13:52:26', '', NULL, '用户性别列表');
INSERT INTO `sys_dict_type` VALUES (2, '菜单状态', 'sys_show_hide', '0', 'admin', '2022-04-26 13:52:26', '', NULL, '菜单状态列表');
INSERT INTO `sys_dict_type` VALUES (3, '系统开关', 'sys_normal_disable', '0', 'admin', '2022-04-26 13:52:26', '', NULL, '系统开关列表');
INSERT INTO `sys_dict_type` VALUES (4, '任务状态', 'sys_job_status', '0', 'admin', '2022-04-26 13:52:26', '', NULL, '任务状态列表');
INSERT INTO `sys_dict_type` VALUES (5, '任务分组', 'sys_job_group', '0', 'admin', '2022-04-26 13:52:26', '', NULL, '任务分组列表');
INSERT INTO `sys_dict_type` VALUES (6, '系统是否', 'sys_yes_no', '0', 'admin', '2022-04-26 13:52:26', '', NULL, '系统是否列表');
INSERT INTO `sys_dict_type` VALUES (7, '通知类型', 'sys_notice_type', '0', 'admin', '2022-04-26 13:52:26', '', NULL, '通知类型列表');
INSERT INTO `sys_dict_type` VALUES (8, '通知状态', 'sys_notice_status', '0', 'admin', '2022-04-26 13:52:26', '', NULL, '通知状态列表');
INSERT INTO `sys_dict_type` VALUES (9, '操作类型', 'sys_oper_type', '0', 'admin', '2022-04-26 13:52:26', '', NULL, '操作类型列表');
INSERT INTO `sys_dict_type` VALUES (10, '系统状态', 'sys_common_status', '0', 'admin', '2022-04-26 13:52:26', '', NULL, '登录状态列表');
INSERT INTO `sys_dict_type` VALUES (11, '白名单状态', 'white_list_status', '0', 'admin', '2022-05-06 17:17:20', '', NULL, NULL);
INSERT INTO `sys_dict_type` VALUES (12, '邮件类型', 'gm_mail_sendtype', '0', 'admin', '2022-05-10 13:54:08', '', NULL, NULL);
INSERT INTO `sys_dict_type` VALUES (13, '服务器状态', 'game_server_type', '0', 'admin', '2022-07-26 14:58:54', '', NULL, NULL);
INSERT INTO `sys_dict_type` VALUES (14, '审核权限', 'check_auto', '0', 'admin', '2022-07-26 16:09:02', '', NULL, NULL);
INSERT INTO `sys_dict_type` VALUES (15, '白名单类型', 'white_list_type', '0', 'admin', '2022-07-29 16:01:16', '', NULL, NULL);
INSERT INTO `sys_dict_type` VALUES (16, '活动开启类型', 'activity_open_type', '0', 'admin', '2022-08-02 13:32:31', '', NULL, NULL);

-- ----------------------------
-- Table structure for sys_job
-- ----------------------------
DROP TABLE IF EXISTS `sys_job`;
CREATE TABLE `sys_job`  (
  `job_id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '任务ID',
  `job_name` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '任务名称',
  `job_group` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT 'DEFAULT' COMMENT '任务组名',
  `invoke_target` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '调用目标字符串',
  `cron_expression` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT 'cron执行表达式',
  `misfire_policy` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '3' COMMENT '计划执行错误策略（1立即执行 2执行一次 3放弃执行）',
  `concurrent` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '1' COMMENT '是否并发执行（0允许 1禁止）',
  `status` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '0' COMMENT '状态（0正常 1暂停）',
  `create_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT '创建者',
  `create_time` datetime NULL DEFAULT NULL COMMENT '创建时间',
  `update_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT '更新者',
  `update_time` datetime NULL DEFAULT NULL COMMENT '更新时间',
  `remark` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT '备注信息',
  PRIMARY KEY (`job_id`, `job_name`, `job_group`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 4 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '定时任务调度表' ROW_FORMAT = Compact;

-- ----------------------------
-- Records of sys_job
-- ----------------------------
INSERT INTO `sys_job` VALUES (1, '系统默认（无参）', 'DEFAULT', 'ryTask.ryNoParams', '0/10 * * * * ?', '3', '1', '1', 'admin', '2022-04-26 13:52:28', '', NULL, '');
INSERT INTO `sys_job` VALUES (2, '系统默认（有参）', 'DEFAULT', 'ryTask.ryParams(\'ry\')', '0/15 * * * * ?', '3', '1', '1', 'admin', '2022-04-26 13:52:28', '', NULL, '');
INSERT INTO `sys_job` VALUES (3, '系统默认（多参）', 'DEFAULT', 'ryTask.ryMultipleParams(\'ry\', true, 2000L, 316.50D, 100)', '0/20 * * * * ?', '3', '1', '1', 'admin', '2022-04-26 13:52:28', '', NULL, '');

-- ----------------------------
-- Table structure for sys_job_log
-- ----------------------------
DROP TABLE IF EXISTS `sys_job_log`;
CREATE TABLE `sys_job_log`  (
  `job_log_id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '任务日志ID',
  `job_name` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '任务名称',
  `job_group` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '任务组名',
  `invoke_target` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '调用目标字符串',
  `job_message` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '日志信息',
  `status` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '0' COMMENT '执行状态（0正常 1失败）',
  `exception_info` varchar(2000) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT '异常信息',
  `create_time` datetime NULL DEFAULT NULL COMMENT '创建时间',
  PRIMARY KEY (`job_log_id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '定时任务调度日志表' ROW_FORMAT = Compact;

-- ----------------------------
-- Table structure for sys_logininfor
-- ----------------------------
DROP TABLE IF EXISTS `sys_logininfor`;
CREATE TABLE `sys_logininfor`  (
  `info_id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '访问ID',
  `user_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT '用户账号',
  `ipaddr` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT '登录IP地址',
  `login_location` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT '登录地点',
  `browser` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT '浏览器类型',
  `os` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT '操作系统',
  `status` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '0' COMMENT '登录状态（0成功 1失败）',
  `msg` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT '提示消息',
  `login_time` datetime NULL DEFAULT NULL COMMENT '访问时间',
  PRIMARY KEY (`info_id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '系统访问记录' ROW_FORMAT = Compact;

-- ----------------------------
-- Table structure for sys_menu
-- ----------------------------
DROP TABLE IF EXISTS `sys_menu`;
CREATE TABLE `sys_menu`  (
  `menu_id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '菜单ID',
  `menu_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '菜单名称',
  `parent_id` bigint(20) NULL DEFAULT 0 COMMENT '父菜单ID',
  `order_num` int(4) NULL DEFAULT 0 COMMENT '显示顺序',
  `path` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT '路由地址',
  `component` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '组件路径',
  `query` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '路由参数',
  `is_frame` int(1) NULL DEFAULT 1 COMMENT '是否为外链（0是 1否）',
  `is_cache` int(1) NULL DEFAULT 0 COMMENT '是否缓存（0缓存 1不缓存）',
  `menu_type` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT '菜单类型（M目录 C菜单 F按钮）',
  `visible` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '0' COMMENT '菜单状态（0显示 1隐藏）',
  `status` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '0' COMMENT '菜单状态（0正常 1停用）',
  `perms` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '权限标识',
  `icon` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '#' COMMENT '菜单图标',
  `create_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT '创建者',
  `create_time` datetime NULL DEFAULT NULL COMMENT '创建时间',
  `update_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT '更新者',
  `update_time` datetime NULL DEFAULT NULL COMMENT '更新时间',
  `remark` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT '备注',
  PRIMARY KEY (`menu_id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1141 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '菜单权限表' ROW_FORMAT = Compact;

-- ----------------------------
-- Records of sys_menu
-- ----------------------------
INSERT INTO `sys_menu` VALUES (1, '系统管理', 0, 1, 'system', NULL, '', 1, 0, 'M', '0', '0', '', 'system', 'admin', '2022-04-26 13:52:24', '', NULL, '系统管理目录');
INSERT INTO `sys_menu` VALUES (2, '系统监控', 0, 2, 'monitor', NULL, '', 1, 0, 'M', '0', '0', '', 'monitor', 'admin', '2022-04-26 13:52:24', '', NULL, '系统监控目录');
INSERT INTO `sys_menu` VALUES (3, '系统工具', 0, 3, 'tool', NULL, '', 1, 0, 'M', '0', '0', '', 'tool', 'admin', '2022-04-26 13:52:24', '', NULL, '系统工具目录');
INSERT INTO `sys_menu` VALUES (100, '用户管理', 1, 1, 'user', 'system/user/index', '', 1, 0, 'C', '0', '0', 'system:user:list', 'user', 'admin', '2022-04-26 13:52:24', '', NULL, '用户管理菜单');
INSERT INTO `sys_menu` VALUES (101, '角色管理', 1, 2, 'role', 'system/role/index', '', 1, 0, 'C', '0', '0', 'system:role:list', 'peoples', 'admin', '2022-04-26 13:52:24', '', NULL, '角色管理菜单');
INSERT INTO `sys_menu` VALUES (102, '菜单管理', 1, 3, 'menu', 'system/menu/index', '', 1, 0, 'C', '0', '0', 'system:menu:list', 'tree-table', 'admin', '2022-04-26 13:52:24', '', NULL, '菜单管理菜单');
INSERT INTO `sys_menu` VALUES (103, '部门管理', 1, 4, 'dept', 'system/dept/index', '', 1, 0, 'C', '0', '0', 'system:dept:list', 'tree', 'admin', '2022-04-26 13:52:24', '', NULL, '部门管理菜单');
INSERT INTO `sys_menu` VALUES (104, '岗位管理', 1, 5, 'post', 'system/post/index', '', 1, 0, 'C', '0', '0', 'system:post:list', 'post', 'admin', '2022-04-26 13:52:24', '', NULL, '岗位管理菜单');
INSERT INTO `sys_menu` VALUES (105, '字典管理', 1, 6, 'dict', 'system/dict/index', '', 1, 0, 'C', '0', '0', 'system:dict:list', 'dict', 'admin', '2022-04-26 13:52:24', '', NULL, '字典管理菜单');
INSERT INTO `sys_menu` VALUES (106, '参数设置', 1, 7, 'config', 'system/config/index', '', 1, 0, 'C', '0', '0', 'system:config:list', 'edit', 'admin', '2022-04-26 13:52:24', '', NULL, '参数设置菜单');
INSERT INTO `sys_menu` VALUES (107, '通知公告', 1, 8, 'notice', 'system/notice/index', '', 1, 0, 'C', '0', '0', 'system:notice:list', 'message', 'admin', '2022-04-26 13:52:24', '', NULL, '通知公告菜单');
INSERT INTO `sys_menu` VALUES (108, '日志管理', 1, 9, 'log', '', '', 1, 0, 'M', '0', '0', '', 'log', 'admin', '2022-04-26 13:52:24', '', NULL, '日志管理菜单');
INSERT INTO `sys_menu` VALUES (109, '在线用户', 2, 1, 'online', 'monitor/online/index', '', 1, 0, 'C', '0', '0', 'monitor:online:list', 'online', 'admin', '2022-04-26 13:52:24', '', NULL, '在线用户菜单');
INSERT INTO `sys_menu` VALUES (110, '定时任务', 2, 2, 'job', 'monitor/job/index', '', 1, 0, 'C', '0', '0', 'monitor:job:list', 'job', 'admin', '2022-04-26 13:52:24', '', NULL, '定时任务菜单');
INSERT INTO `sys_menu` VALUES (111, '数据监控', 2, 3, 'druid', 'monitor/druid/index', '', 1, 0, 'C', '0', '0', 'monitor:druid:list', 'druid', 'admin', '2022-04-26 13:52:24', '', NULL, '数据监控菜单');
INSERT INTO `sys_menu` VALUES (112, '服务监控', 2, 4, 'server', 'monitor/server/index', '', 1, 0, 'C', '0', '0', 'monitor:server:list', 'server', 'admin', '2022-04-26 13:52:24', '', NULL, '服务监控菜单');
INSERT INTO `sys_menu` VALUES (113, '缓存监控', 2, 5, 'cache', 'monitor/cache/index', '', 1, 0, 'C', '0', '0', 'monitor:cache:list', 'redis', 'admin', '2022-04-26 13:52:24', '', NULL, '缓存监控菜单');
INSERT INTO `sys_menu` VALUES (114, '表单构建', 3, 1, 'build', 'tool/build/index', '', 1, 0, 'C', '0', '0', 'tool:build:list', 'build', 'admin', '2022-04-26 13:52:24', '', NULL, '表单构建菜单');
INSERT INTO `sys_menu` VALUES (115, '代码生成', 3, 2, 'gen', 'tool/gen/index', '', 1, 0, 'C', '0', '0', 'tool:gen:list', 'code', 'admin', '2022-04-26 13:52:24', '', NULL, '代码生成菜单');
INSERT INTO `sys_menu` VALUES (116, '系统接口', 3, 3, 'swagger', 'tool/swagger/index', '', 1, 0, 'C', '0', '0', 'tool:swagger:list', 'swagger', 'admin', '2022-04-26 13:52:24', '', NULL, '系统接口菜单');
INSERT INTO `sys_menu` VALUES (500, '操作日志', 108, 1, 'operlog', 'monitor/operlog/index', '', 1, 0, 'C', '0', '0', 'monitor:operlog:list', 'form', 'admin', '2022-04-26 13:52:24', '', NULL, '操作日志菜单');
INSERT INTO `sys_menu` VALUES (501, '登录日志', 108, 2, 'logininfor', 'monitor/logininfor/index', '', 1, 0, 'C', '0', '0', 'monitor:logininfor:list', 'logininfor', 'admin', '2022-04-26 13:52:24', '', NULL, '登录日志菜单');
INSERT INTO `sys_menu` VALUES (1001, '用户查询', 100, 1, '', '', '', 1, 0, 'F', '0', '0', 'system:user:query', '#', 'admin', '2022-04-26 13:52:24', '', NULL, '');
INSERT INTO `sys_menu` VALUES (1002, '用户新增', 100, 2, '', '', '', 1, 0, 'F', '0', '0', 'system:user:add', '#', 'admin', '2022-04-26 13:52:24', '', NULL, '');
INSERT INTO `sys_menu` VALUES (1003, '用户修改', 100, 3, '', '', '', 1, 0, 'F', '0', '0', 'system:user:edit', '#', 'admin', '2022-04-26 13:52:24', '', NULL, '');
INSERT INTO `sys_menu` VALUES (1004, '用户删除', 100, 4, '', '', '', 1, 0, 'F', '0', '0', 'system:user:remove', '#', 'admin', '2022-04-26 13:52:24', '', NULL, '');
INSERT INTO `sys_menu` VALUES (1005, '用户导出', 100, 5, '', '', '', 1, 0, 'F', '0', '0', 'system:user:export', '#', 'admin', '2022-04-26 13:52:24', '', NULL, '');
INSERT INTO `sys_menu` VALUES (1006, '用户导入', 100, 6, '', '', '', 1, 0, 'F', '0', '0', 'system:user:import', '#', 'admin', '2022-04-26 13:52:24', '', NULL, '');
INSERT INTO `sys_menu` VALUES (1007, '重置密码', 100, 7, '', '', '', 1, 0, 'F', '0', '0', 'system:user:resetPwd', '#', 'admin', '2022-04-26 13:52:24', '', NULL, '');
INSERT INTO `sys_menu` VALUES (1008, '角色查询', 101, 1, '', '', '', 1, 0, 'F', '0', '0', 'system:role:query', '#', 'admin', '2022-04-26 13:52:24', '', NULL, '');
INSERT INTO `sys_menu` VALUES (1009, '角色新增', 101, 2, '', '', '', 1, 0, 'F', '0', '0', 'system:role:add', '#', 'admin', '2022-04-26 13:52:24', '', NULL, '');
INSERT INTO `sys_menu` VALUES (1010, '角色修改', 101, 3, '', '', '', 1, 0, 'F', '0', '0', 'system:role:edit', '#', 'admin', '2022-04-26 13:52:24', '', NULL, '');
INSERT INTO `sys_menu` VALUES (1011, '角色删除', 101, 4, '', '', '', 1, 0, 'F', '0', '0', 'system:role:remove', '#', 'admin', '2022-04-26 13:52:24', '', NULL, '');
INSERT INTO `sys_menu` VALUES (1012, '角色导出', 101, 5, '', '', '', 1, 0, 'F', '0', '0', 'system:role:export', '#', 'admin', '2022-04-26 13:52:24', '', NULL, '');
INSERT INTO `sys_menu` VALUES (1013, '菜单查询', 102, 1, '', '', '', 1, 0, 'F', '0', '0', 'system:menu:query', '#', 'admin', '2022-04-26 13:52:24', '', NULL, '');
INSERT INTO `sys_menu` VALUES (1014, '菜单新增', 102, 2, '', '', '', 1, 0, 'F', '0', '0', 'system:menu:add', '#', 'admin', '2022-04-26 13:52:24', '', NULL, '');
INSERT INTO `sys_menu` VALUES (1015, '菜单修改', 102, 3, '', '', '', 1, 0, 'F', '0', '0', 'system:menu:edit', '#', 'admin', '2022-04-26 13:52:24', '', NULL, '');
INSERT INTO `sys_menu` VALUES (1016, '菜单删除', 102, 4, '', '', '', 1, 0, 'F', '0', '0', 'system:menu:remove', '#', 'admin', '2022-04-26 13:52:24', '', NULL, '');
INSERT INTO `sys_menu` VALUES (1017, '部门查询', 103, 1, '', '', '', 1, 0, 'F', '0', '0', 'system:dept:query', '#', 'admin', '2022-04-26 13:52:24', '', NULL, '');
INSERT INTO `sys_menu` VALUES (1018, '部门新增', 103, 2, '', '', '', 1, 0, 'F', '0', '0', 'system:dept:add', '#', 'admin', '2022-04-26 13:52:24', '', NULL, '');
INSERT INTO `sys_menu` VALUES (1019, '部门修改', 103, 3, '', '', '', 1, 0, 'F', '0', '0', 'system:dept:edit', '#', 'admin', '2022-04-26 13:52:24', '', NULL, '');
INSERT INTO `sys_menu` VALUES (1020, '部门删除', 103, 4, '', '', '', 1, 0, 'F', '0', '0', 'system:dept:remove', '#', 'admin', '2022-04-26 13:52:24', '', NULL, '');
INSERT INTO `sys_menu` VALUES (1021, '岗位查询', 104, 1, '', '', '', 1, 0, 'F', '0', '0', 'system:post:query', '#', 'admin', '2022-04-26 13:52:24', '', NULL, '');
INSERT INTO `sys_menu` VALUES (1022, '岗位新增', 104, 2, '', '', '', 1, 0, 'F', '0', '0', 'system:post:add', '#', 'admin', '2022-04-26 13:52:24', '', NULL, '');
INSERT INTO `sys_menu` VALUES (1023, '岗位修改', 104, 3, '', '', '', 1, 0, 'F', '0', '0', 'system:post:edit', '#', 'admin', '2022-04-26 13:52:24', '', NULL, '');
INSERT INTO `sys_menu` VALUES (1024, '岗位删除', 104, 4, '', '', '', 1, 0, 'F', '0', '0', 'system:post:remove', '#', 'admin', '2022-04-26 13:52:24', '', NULL, '');
INSERT INTO `sys_menu` VALUES (1025, '岗位导出', 104, 5, '', '', '', 1, 0, 'F', '0', '0', 'system:post:export', '#', 'admin', '2022-04-26 13:52:24', '', NULL, '');
INSERT INTO `sys_menu` VALUES (1026, '字典查询', 105, 1, '#', '', '', 1, 0, 'F', '0', '0', 'system:dict:query', '#', 'admin', '2022-04-26 13:52:24', '', NULL, '');
INSERT INTO `sys_menu` VALUES (1027, '字典新增', 105, 2, '#', '', '', 1, 0, 'F', '0', '0', 'system:dict:add', '#', 'admin', '2022-04-26 13:52:24', '', NULL, '');
INSERT INTO `sys_menu` VALUES (1028, '字典修改', 105, 3, '#', '', '', 1, 0, 'F', '0', '0', 'system:dict:edit', '#', 'admin', '2022-04-26 13:52:24', '', NULL, '');
INSERT INTO `sys_menu` VALUES (1029, '字典删除', 105, 4, '#', '', '', 1, 0, 'F', '0', '0', 'system:dict:remove', '#', 'admin', '2022-04-26 13:52:24', '', NULL, '');
INSERT INTO `sys_menu` VALUES (1030, '字典导出', 105, 5, '#', '', '', 1, 0, 'F', '0', '0', 'system:dict:export', '#', 'admin', '2022-04-26 13:52:24', '', NULL, '');
INSERT INTO `sys_menu` VALUES (1031, '参数查询', 106, 1, '#', '', '', 1, 0, 'F', '0', '0', 'system:config:query', '#', 'admin', '2022-04-26 13:52:24', '', NULL, '');
INSERT INTO `sys_menu` VALUES (1032, '参数新增', 106, 2, '#', '', '', 1, 0, 'F', '0', '0', 'system:config:add', '#', 'admin', '2022-04-26 13:52:24', '', NULL, '');
INSERT INTO `sys_menu` VALUES (1033, '参数修改', 106, 3, '#', '', '', 1, 0, 'F', '0', '0', 'system:config:edit', '#', 'admin', '2022-04-26 13:52:24', '', NULL, '');
INSERT INTO `sys_menu` VALUES (1034, '参数删除', 106, 4, '#', '', '', 1, 0, 'F', '0', '0', 'system:config:remove', '#', 'admin', '2022-04-26 13:52:24', '', NULL, '');
INSERT INTO `sys_menu` VALUES (1035, '参数导出', 106, 5, '#', '', '', 1, 0, 'F', '0', '0', 'system:config:export', '#', 'admin', '2022-04-26 13:52:24', '', NULL, '');
INSERT INTO `sys_menu` VALUES (1036, '公告查询', 107, 1, '#', '', '', 1, 0, 'F', '0', '0', 'system:notice:query', '#', 'admin', '2022-04-26 13:52:24', '', NULL, '');
INSERT INTO `sys_menu` VALUES (1037, '公告新增', 107, 2, '#', '', '', 1, 0, 'F', '0', '0', 'system:notice:add', '#', 'admin', '2022-04-26 13:52:24', '', NULL, '');
INSERT INTO `sys_menu` VALUES (1038, '公告修改', 107, 3, '#', '', '', 1, 0, 'F', '0', '0', 'system:notice:edit', '#', 'admin', '2022-04-26 13:52:24', '', NULL, '');
INSERT INTO `sys_menu` VALUES (1039, '公告删除', 107, 4, '#', '', '', 1, 0, 'F', '0', '0', 'system:notice:remove', '#', 'admin', '2022-04-26 13:52:24', '', NULL, '');
INSERT INTO `sys_menu` VALUES (1040, '操作查询', 500, 1, '#', '', '', 1, 0, 'F', '0', '0', 'monitor:operlog:query', '#', 'admin', '2022-04-26 13:52:24', '', NULL, '');
INSERT INTO `sys_menu` VALUES (1041, '操作删除', 500, 2, '#', '', '', 1, 0, 'F', '0', '0', 'monitor:operlog:remove', '#', 'admin', '2022-04-26 13:52:24', '', NULL, '');
INSERT INTO `sys_menu` VALUES (1042, '日志导出', 500, 4, '#', '', '', 1, 0, 'F', '0', '0', 'monitor:operlog:export', '#', 'admin', '2022-04-26 13:52:24', '', NULL, '');
INSERT INTO `sys_menu` VALUES (1043, '登录查询', 501, 1, '#', '', '', 1, 0, 'F', '0', '0', 'monitor:logininfor:query', '#', 'admin', '2022-04-26 13:52:24', '', NULL, '');
INSERT INTO `sys_menu` VALUES (1044, '登录删除', 501, 2, '#', '', '', 1, 0, 'F', '0', '0', 'monitor:logininfor:remove', '#', 'admin', '2022-04-26 13:52:24', '', NULL, '');
INSERT INTO `sys_menu` VALUES (1045, '日志导出', 501, 3, '#', '', '', 1, 0, 'F', '0', '0', 'monitor:logininfor:export', '#', 'admin', '2022-04-26 13:52:24', '', NULL, '');
INSERT INTO `sys_menu` VALUES (1046, '在线查询', 109, 1, '#', '', '', 1, 0, 'F', '0', '0', 'monitor:online:query', '#', 'admin', '2022-04-26 13:52:24', '', NULL, '');
INSERT INTO `sys_menu` VALUES (1047, '批量强退', 109, 2, '#', '', '', 1, 0, 'F', '0', '0', 'monitor:online:batchLogout', '#', 'admin', '2022-04-26 13:52:24', '', NULL, '');
INSERT INTO `sys_menu` VALUES (1048, '单条强退', 109, 3, '#', '', '', 1, 0, 'F', '0', '0', 'monitor:online:forceLogout', '#', 'admin', '2022-04-26 13:52:24', '', NULL, '');
INSERT INTO `sys_menu` VALUES (1049, '任务查询', 110, 1, '#', '', '', 1, 0, 'F', '0', '0', 'monitor:job:query', '#', 'admin', '2022-04-26 13:52:24', '', NULL, '');
INSERT INTO `sys_menu` VALUES (1050, '任务新增', 110, 2, '#', '', '', 1, 0, 'F', '0', '0', 'monitor:job:add', '#', 'admin', '2022-04-26 13:52:24', '', NULL, '');
INSERT INTO `sys_menu` VALUES (1051, '任务修改', 110, 3, '#', '', '', 1, 0, 'F', '0', '0', 'monitor:job:edit', '#', 'admin', '2022-04-26 13:52:24', '', NULL, '');
INSERT INTO `sys_menu` VALUES (1052, '任务删除', 110, 4, '#', '', '', 1, 0, 'F', '0', '0', 'monitor:job:remove', '#', 'admin', '2022-04-26 13:52:24', '', NULL, '');
INSERT INTO `sys_menu` VALUES (1053, '状态修改', 110, 5, '#', '', '', 1, 0, 'F', '0', '0', 'monitor:job:changeStatus', '#', 'admin', '2022-04-26 13:52:24', '', NULL, '');
INSERT INTO `sys_menu` VALUES (1054, '任务导出', 110, 7, '#', '', '', 1, 0, 'F', '0', '0', 'monitor:job:export', '#', 'admin', '2022-04-26 13:52:24', '', NULL, '');
INSERT INTO `sys_menu` VALUES (1055, '生成查询', 115, 1, '#', '', '', 1, 0, 'F', '0', '0', 'tool:gen:query', '#', 'admin', '2022-04-26 13:52:24', '', NULL, '');
INSERT INTO `sys_menu` VALUES (1056, '生成修改', 115, 2, '#', '', '', 1, 0, 'F', '0', '0', 'tool:gen:edit', '#', 'admin', '2022-04-26 13:52:24', '', NULL, '');
INSERT INTO `sys_menu` VALUES (1057, '生成删除', 115, 3, '#', '', '', 1, 0, 'F', '0', '0', 'tool:gen:remove', '#', 'admin', '2022-04-26 13:52:24', '', NULL, '');
INSERT INTO `sys_menu` VALUES (1058, '导入代码', 115, 2, '#', '', '', 1, 0, 'F', '0', '0', 'tool:gen:import', '#', 'admin', '2022-04-26 13:52:24', '', NULL, '');
INSERT INTO `sys_menu` VALUES (1059, '预览代码', 115, 4, '#', '', '', 1, 0, 'F', '0', '0', 'tool:gen:preview', '#', 'admin', '2022-04-26 13:52:24', '', NULL, '');
INSERT INTO `sys_menu` VALUES (1060, '生成代码', 115, 5, '#', '', '', 1, 0, 'F', '0', '0', 'tool:gen:code', '#', 'admin', '2022-04-26 13:52:24', '', NULL, '');
INSERT INTO `sys_menu` VALUES (1061, 'GM工具', 0, 5, 'gameGm', NULL, NULL, 1, 0, 'M', '0', '0', '', 'clipboard', 'admin', '2022-05-06 10:37:10', 'admin', '2022-05-06 10:37:31', '');
INSERT INTO `sys_menu` VALUES (1062, '平台管理', 1061, 1, 'platform', 'gameGm/platform/index', NULL, 1, 0, 'C', '0', '0', 'gm:platform:list', 'build', 'admin', '2022-05-06 10:38:24', '', NULL, '');
INSERT INTO `sys_menu` VALUES (1063, '服务器管理', 1061, 2, 'server', 'gameGm/server/index', NULL, 1, 0, 'C', '0', '0', 'gm:server:list', 'code', 'admin', '2022-05-06 10:38:53', '', NULL, '');
INSERT INTO `sys_menu` VALUES (1064, '玩家管理', 1061, 3, 'player', 'gameGm/player/index', NULL, 1, 0, 'C', '0', '0', 'gm:player:list', 'documentation', 'admin', '2022-05-06 10:39:24', '', NULL, '');
INSERT INTO `sys_menu` VALUES (1065, '帮派管理', 1061, 4, 'gangs', 'gameGm/gangs/index', NULL, 1, 0, 'C', '0', '0', 'gm:gangs:list', 'component', 'admin', '2022-05-09 17:22:37', 'admin', '2022-05-10 13:53:01', '');
INSERT INTO `sys_menu` VALUES (1066, '邮件管理', 1061, 5, 'mail', 'gameGm/mail/index', NULL, 1, 0, 'C', '0', '0', 'gm:mail:list', 'email', 'admin', '2022-05-10 13:52:52', '', NULL, '');
INSERT INTO `sys_menu` VALUES (1067, '道具管理', 1061, 6, 'item', 'gameGm/item/index', NULL, 1, 0, 'C', '0', '0', 'gm:item:list', 'tool', 'admin', '2022-07-26 16:14:46', '', NULL, '');
INSERT INTO `sys_menu` VALUES (1068, '活动管理', 0, 6, 'activity', NULL, NULL, 1, 0, 'M', '0', '0', '', 'peoples', 'admin', '2022-07-26 16:48:50', 'admin', '2022-07-26 16:51:01', '');
INSERT INTO `sys_menu` VALUES (1069, '活动列表', 1068, 1, 'activity', 'activity/activity/index', NULL, 1, 0, 'C', '0', '0', 'gm:activity:list', 'component', 'admin', '2022-07-26 16:50:08', 'admin', '2022-07-28 10:28:41', '');
INSERT INTO `sys_menu` VALUES (1070, '充值管理', 0, 7, 'rechargelog', NULL, NULL, 1, 0, 'M', '0', '0', NULL, 'money', 'admin', '2022-07-27 16:30:53', '', NULL, '');
INSERT INTO `sys_menu` VALUES (1071, '后台充值列表', 1070, 1, 'rechargelog', 'rechargelog/rechargelog/index', NULL, 1, 0, 'C', '0', '0', 'gm:recuargelog:list', 'money', 'admin', '2022-07-27 16:32:43', '', NULL, '');
INSERT INTO `sys_menu` VALUES (1072, '白名单', 1061, 7, 'whitelist', 'gameGm/whitelist/index', NULL, 1, 0, 'C', '0', '0', 'gm:whitelist:list', 'rate', 'admin', '2022-07-29 16:00:28', '', NULL, '');
INSERT INTO `sys_menu` VALUES (1073, 'GM命令行', 1061, 8, 'command', 'gameGm/command/index', NULL, 1, 0, 'C', '0', '0', 'gm:command:list', 'textarea', 'admin', '2022-08-01 11:51:55', '', NULL, '');
INSERT INTO `sys_menu` VALUES (1074, '异常管理', 1061, 10, 'exception', 'gameGm/exception/index', NULL, 1, 0, 'C', '0', '0', 'gm:exception:list', 'input', 'admin', '2022-08-01 14:21:43', '', NULL, '');
INSERT INTO `sys_menu` VALUES (1075, '跑马灯管理', 1061, 9, 'broadcast', 'gameGm/broadcast/index', NULL, 1, 0, 'C', '0', '0', 'gm:broadcast:list', 'textarea', 'admin', '2022-08-01 14:22:58', '', NULL, '');
INSERT INTO `sys_menu` VALUES (1076, 'GM充值', 1061, 5, 'recharge', 'gameGm/recharge/index', NULL, 1, 0, 'C', '0', '0', 'gm:recharge:list', 'nested', 'admin', '2022-08-02 09:40:25', '', NULL, '');
INSERT INTO `sys_menu` VALUES (1077, '平台新增', 1062, 1, '', NULL, NULL, 1, 0, 'F', '0', '0', 'gm:platform:add', '#', '51gamer_admin', '2022-08-03 10:52:14', '', NULL, '');
INSERT INTO `sys_menu` VALUES (1078, '平台修改', 1062, 2, '', NULL, NULL, 1, 0, 'F', '0', '0', 'gm:platform:edit', '#', '51gamer_admin', '2022-08-03 10:52:46', '', NULL, '');
INSERT INTO `sys_menu` VALUES (1079, '平台删除', 1062, 3, '', NULL, NULL, 1, 0, 'F', '0', '0', 'gm:player:remove', '#', '51gamer_admin', '2022-08-03 10:52:59', '', NULL, '');
INSERT INTO `sys_menu` VALUES (1080, '服务器新增', 1063, 1, '', NULL, NULL, 1, 0, 'F', '0', '0', 'gm:server:add', '#', '51gamer_admin', '2022-08-03 10:53:38', '', NULL, '');
INSERT INTO `sys_menu` VALUES (1081, '服务器修改', 1063, 2, '', NULL, NULL, 1, 0, 'F', '0', '0', 'gm:server:edit', '#', '51gamer_admin', '2022-08-03 10:53:51', '', NULL, '');
INSERT INTO `sys_menu` VALUES (1082, '服务器删除', 1063, 3, '', NULL, NULL, 1, 0, 'F', '0', '0', 'gm:server:remove', '#', '51gamer_admin', '2022-08-03 10:54:05', '', NULL, '');
INSERT INTO `sys_menu` VALUES (1083, '服务器导出', 1063, 4, '', NULL, NULL, 1, 0, 'F', '0', '0', 'gm:server:export', '#', '51gamer_admin', '2022-08-03 10:54:21', '', NULL, '');
INSERT INTO `sys_menu` VALUES (1084, '玩家删除', 1064, 1, '', NULL, NULL, 1, 0, 'F', '0', '0', 'gm:player:remove', '#', '51gamer_admin', '2022-08-03 10:55:28', '', NULL, '');
INSERT INTO `sys_menu` VALUES (1085, '玩家导出', 1064, 2, '', NULL, NULL, 1, 0, 'F', '0', '0', 'gm:player:export', '#', '51gamer_admin', '2022-08-03 10:55:43', '', NULL, '');
INSERT INTO `sys_menu` VALUES (1086, '玩家查看', 1064, 3, '', NULL, NULL, 1, 0, 'F', '0', '0', 'gm:player:show', '#', '51gamer_admin', '2022-08-03 10:56:29', '', NULL, '');
INSERT INTO `sys_menu` VALUES (1087, '玩家踢下线', 1064, 4, '', NULL, NULL, 1, 0, 'F', '0', '0', 'gm:player:kitout', '#', '51gamer_admin', '2022-08-03 10:56:48', '', NULL, '');
INSERT INTO `sys_menu` VALUES (1088, '帮派导出', 1065, 1, '', NULL, NULL, 1, 0, 'F', '0', '0', 'gm:gangs:export', '#', '51gamer_admin', '2022-08-03 10:57:28', '', NULL, '');
INSERT INTO `sys_menu` VALUES (1089, '帮派成员查看', 1065, 2, '', NULL, NULL, 1, 0, 'F', '0', '0', 'gm:gangs:player', '#', '51gamer_admin', '2022-08-03 10:57:48', '51gamer_admin', '2022-08-03 10:59:00', '');
INSERT INTO `sys_menu` VALUES (1090, '帮派公告修改', 1065, 3, '', NULL, NULL, 1, 0, 'F', '0', '0', 'gm:gangs:noticeedit', '#', '51gamer_admin', '2022-08-03 10:59:19', '', NULL, '');
INSERT INTO `sys_menu` VALUES (1091, '邮件发送', 1066, 1, '', NULL, NULL, 1, 0, 'F', '0', '0', 'gm:mail:add', '#', '51gamer_admin', '2022-08-03 11:00:03', '', NULL, '');
INSERT INTO `sys_menu` VALUES (1092, '邮件删除', 1066, 2, '', NULL, NULL, 1, 0, 'F', '0', '0', 'gm:mail:remove', '#', '51gamer_admin', '2022-08-03 11:00:16', '', NULL, '');
INSERT INTO `sys_menu` VALUES (1093, '邮件通过', 1066, 3, '', NULL, NULL, 1, 0, 'F', '0', '0', 'gm:mail:pass', '#', '51gamer_admin', '2022-08-03 11:00:30', '', NULL, '');
INSERT INTO `sys_menu` VALUES (1095, '充值新增', 1076, 1, '', NULL, NULL, 1, 0, 'F', '0', '0', 'gm:recharge:add', '#', '51gamer_admin', '2022-08-03 11:02:36', '', NULL, '');
INSERT INTO `sys_menu` VALUES (1096, '充值修改', 1076, 2, '', NULL, NULL, 1, 0, 'F', '0', '0', 'gm:recharge:edit', '#', '51gamer_admin', '2022-08-03 11:02:51', '', NULL, '');
INSERT INTO `sys_menu` VALUES (1097, '充值移除', 1076, 3, '', NULL, NULL, 1, 0, 'F', '0', '0', 'gm:recharge:remove', '#', '51gamer_admin', '2022-08-03 11:03:12', '', NULL, '');
INSERT INTO `sys_menu` VALUES (1098, '充值导出', 1076, 4, '', NULL, NULL, 1, 0, 'F', '0', '0', 'gm:recharge:export', '#', '51gamer_admin', '2022-08-03 11:03:27', '', NULL, '');
INSERT INTO `sys_menu` VALUES (1099, '充值通过拒绝', 1076, 5, '', NULL, NULL, 1, 0, 'F', '0', '0', 'gm:recharge:pass', '#', '51gamer_admin', '2022-08-03 11:03:48', '', NULL, '');
INSERT INTO `sys_menu` VALUES (1100, '道具新增', 1067, 1, '', NULL, NULL, 1, 0, 'F', '0', '0', 'gm:item:add', '#', '51gamer_admin', '2022-08-03 11:04:57', '', NULL, '');
INSERT INTO `sys_menu` VALUES (1101, '道具删除', 1067, 2, '', NULL, NULL, 1, 0, 'F', '0', '0', 'gm:item:remove', '#', '51gamer_admin', '2022-08-03 11:05:14', '', NULL, '');
INSERT INTO `sys_menu` VALUES (1102, '道具通过拒绝', 1067, 3, '', NULL, NULL, 1, 0, 'F', '0', '0', 'gm:item:pass', '#', '51gamer_admin', '2022-08-03 11:05:30', '', NULL, '');
INSERT INTO `sys_menu` VALUES (1103, '道具修改', 1067, 4, '', NULL, NULL, 1, 0, 'F', '0', '0', 'gm:item:edit', '#', '51gamer_admin', '2022-08-03 11:05:47', '', NULL, '');
INSERT INTO `sys_menu` VALUES (1104, '白名单新增', 1072, 1, '', NULL, NULL, 1, 0, 'F', '0', '0', 'gm:whitelist:add', '#', '51gamer_admin', '2022-08-03 11:06:21', '', NULL, '');
INSERT INTO `sys_menu` VALUES (1105, '白名单修改', 1072, 2, '', NULL, NULL, 1, 0, 'F', '0', '0', 'gm:whitelist:edit', '#', '51gamer_admin', '2022-08-03 11:06:39', '', NULL, '');
INSERT INTO `sys_menu` VALUES (1106, '白名单删除', 1072, 3, '', NULL, NULL, 1, 0, 'F', '0', '0', 'gm:whitelist:remove', '#', '51gamer_admin', '2022-08-03 11:06:58', '', NULL, '');
INSERT INTO `sys_menu` VALUES (1107, '白名单导出', 1072, 4, '', NULL, NULL, 1, 0, 'F', '0', '0', 'gm:whitelist:export', '#', '51gamer_admin', '2022-08-03 11:07:16', '', NULL, '');
INSERT INTO `sys_menu` VALUES (1108, '命令新增', 1073, 1, '', NULL, NULL, 1, 0, 'F', '0', '0', 'gm:command:add', '#', '51gamer_admin', '2022-08-03 11:07:51', '', NULL, '');
INSERT INTO `sys_menu` VALUES (1109, '命令修改', 1073, 2, '', NULL, NULL, 1, 0, 'F', '0', '0', 'gm:command:edit', '#', '51gamer_admin', '2022-08-03 11:08:05', '', NULL, '');
INSERT INTO `sys_menu` VALUES (1110, '命令删除', 1073, 3, '', NULL, NULL, 1, 0, 'F', '0', '0', 'gm:command:remove', '#', '51gamer_admin', '2022-08-03 11:08:19', '', NULL, '');
INSERT INTO `sys_menu` VALUES (1111, '命令通过拒绝', 1073, 4, '', NULL, NULL, 1, 0, 'F', '0', '0', 'gm:command:pass', '#', '51gamer_admin', '2022-08-03 11:08:35', '', NULL, '');
INSERT INTO `sys_menu` VALUES (1112, '跑马灯新增', 1075, 1, '', NULL, NULL, 1, 0, 'F', '0', '0', 'gm:broadcast:add', '#', '51gamer_admin', '2022-08-03 11:09:07', '', NULL, '');
INSERT INTO `sys_menu` VALUES (1113, '跑马灯修改', 1075, 2, '', NULL, NULL, 1, 0, 'F', '0', '0', 'gm:broadcast:edit', '#', '51gamer_admin', '2022-08-03 11:09:20', '', NULL, '');
INSERT INTO `sys_menu` VALUES (1114, '跑马灯删除', 1075, 3, '', NULL, NULL, 1, 0, 'F', '0', '0', 'gm:broadcast:remove', '#', '51gamer_admin', '2022-08-03 11:09:34', '', NULL, '');
INSERT INTO `sys_menu` VALUES (1115, '跑马灯导出', 1075, 4, '', NULL, NULL, 1, 0, 'F', '0', '0', 'gm:broadcast:export', '#', '51gamer_admin', '2022-08-03 11:09:51', '', NULL, '');
INSERT INTO `sys_menu` VALUES (1116, '跑马灯通过拒绝', 1075, 5, '', NULL, NULL, 1, 0, 'F', '0', '0', 'gm:broadcast:pass', '#', '51gamer_admin', '2022-08-03 11:10:07', '', NULL, '');
INSERT INTO `sys_menu` VALUES (1117, '异常导出', 1074, 1, '', NULL, NULL, 1, 0, 'F', '0', '0', 'gm:exception:export', '#', '51gamer_admin', '2022-08-03 11:10:31', '51gamer_admin', '2022-08-03 11:24:17', '');
INSERT INTO `sys_menu` VALUES (1118, '活动新增', 1069, 1, '', NULL, NULL, 1, 0, 'F', '0', '0', 'gm:activity:add', '#', '51gamer_admin', '2022-08-03 11:11:08', '', NULL, '');
INSERT INTO `sys_menu` VALUES (1119, '活动修改', 1069, 2, '', NULL, NULL, 1, 0, 'F', '0', '0', 'gm:activity:edit', '#', '51gamer_admin', '2022-08-03 11:11:24', '', NULL, '');
INSERT INTO `sys_menu` VALUES (1120, '活动删除', 1069, 3, '', NULL, NULL, 1, 0, 'F', '0', '0', 'gm:activity:remove', '#', '51gamer_admin', '2022-08-03 11:11:39', '', NULL, '');
INSERT INTO `sys_menu` VALUES (1121, '活动导出', 1069, 4, '', NULL, NULL, 1, 0, 'F', '0', '0', 'gm:activity:export', '#', '51gamer_admin', '2022-08-03 11:11:55', '', NULL, '');
INSERT INTO `sys_menu` VALUES (1122, '活动查询', 1069, 5, '', NULL, NULL, 1, 0, 'F', '0', '0', 'gm:activity:query', '#', '51gamer_admin', '2022-08-03 11:13:12', '', NULL, '');
INSERT INTO `sys_menu` VALUES (1123, '充值订单导出', 1071, 1, '', NULL, NULL, 1, 0, 'F', '0', '0', 'gm:rechargelog:export', '#', '51gamer_admin', '2022-08-03 11:15:01', '', NULL, '');
INSERT INTO `sys_menu` VALUES (1124, '平台查询', 1062, 4, '', NULL, NULL, 1, 0, 'F', '0', '0', 'gm:platform:query', '#', '51gamer_admin', '2022-08-03 11:15:29', '', NULL, '');
INSERT INTO `sys_menu` VALUES (1125, '服务器查询', 1063, 5, '', NULL, NULL, 1, 0, 'F', '0', '0', 'gm:server:query', '#', '51gamer_admin', '2022-08-03 11:15:55', '', NULL, '');
INSERT INTO `sys_menu` VALUES (1126, '玩家查询', 1064, 5, '', NULL, NULL, 1, 0, 'F', '0', '0', 'gm:player:query', '#', '51gamer_admin', '2022-08-03 11:16:52', '', NULL, '');
INSERT INTO `sys_menu` VALUES (1127, '玩家将领导出', 1064, 6, '', NULL, NULL, 1, 0, 'F', '0', '0', 'gm:player:general:export', '#', '51gamer_admin', '2022-08-03 11:17:17', '', NULL, '');
INSERT INTO `sys_menu` VALUES (1128, '玩家道具导出', 1064, 7, '', NULL, NULL, 1, 0, 'F', '0', '0', 'gm:player:item:export', '#', '51gamer_admin', '2022-08-03 11:17:34', '', NULL, '');
INSERT INTO `sys_menu` VALUES (1129, '玩家流水导出', 1064, 8, '', NULL, NULL, 1, 0, 'F', '0', '0', 'gm:player:itemlog:export', '#', '51gamer_admin', '2022-08-03 11:18:01', '', NULL, '');
INSERT INTO `sys_menu` VALUES (1130, '帮派查询', 1065, 4, '', NULL, NULL, 1, 0, 'F', '0', '0', 'gm:gangs:query', '#', '51gamer_admin', '2022-08-03 11:18:31', '', NULL, '');
INSERT INTO `sys_menu` VALUES (1131, '邮件查询', 1066, 4, '', NULL, NULL, 1, 0, 'F', '0', '0', 'gm:mail:query', '#', '51gamer_admin', '2022-08-03 11:19:02', '', NULL, '');
INSERT INTO `sys_menu` VALUES (1132, '充值查询', 1076, 6, '', NULL, NULL, 1, 0, 'F', '0', '0', 'gm:recharge:query', '#', '51gamer_admin', '2022-08-03 11:20:04', '', NULL, '');
INSERT INTO `sys_menu` VALUES (1133, '道具查询', 1067, 5, '', NULL, NULL, 1, 0, 'F', '0', '0', 'gm:item:query', '#', '51gamer_admin', '2022-08-03 11:21:13', '', NULL, '');
INSERT INTO `sys_menu` VALUES (1134, '白名单查询', 1072, 5, '', NULL, NULL, 1, 0, 'F', '0', '0', 'gm:whitelist:query', '#', '51gamer_admin', '2022-08-03 11:21:47', '', NULL, '');
INSERT INTO `sys_menu` VALUES (1135, '命令查询', 1073, 5, '', NULL, NULL, 1, 0, 'F', '0', '0', 'gm:command:query', '#', '51gamer_admin', '2022-08-03 11:22:17', '', NULL, '');
INSERT INTO `sys_menu` VALUES (1136, '跑马灯查询', 1075, 6, '', NULL, NULL, 1, 0, 'F', '0', '0', 'gm:broadcast:query', '#', '51gamer_admin', '2022-08-03 11:22:54', '51gamer_admin', '2022-08-03 11:23:02', '');
INSERT INTO `sys_menu` VALUES (1137, '异常查询', 1074, 2, '', NULL, NULL, 1, 0, 'F', '0', '0', 'gm:exception:query', '#', '51gamer_admin', '2022-08-03 11:23:35', '51gamer_admin', '2022-08-03 11:24:23', '');
INSERT INTO `sys_menu` VALUES (1138, '充值账单', 1070, 2, 'rechargesum', 'rechargelog/rechargesum/index', NULL, 1, 0, 'C', '0', '0', 'gm:rechargesum:list', 'clipboard', 'admin', '2022-08-03 17:49:51', '', NULL, '');
INSERT INTO `sys_menu` VALUES (1139, '充值导出', 1138, 1, '', NULL, NULL, 1, 0, 'F', '0', '0', 'gm:rechargesum:export', '#', 'admin', '2022-08-03 19:01:57', '', NULL, '');
INSERT INTO `sys_menu` VALUES (1140, '充值详情', 1138, 2, '', NULL, NULL, 1, 0, 'F', '0', '0', 'gm:rechargesum:detail', '#', 'admin', '2022-08-03 19:02:28', '', NULL, '');

-- ----------------------------
-- Table structure for sys_notice
-- ----------------------------
DROP TABLE IF EXISTS `sys_notice`;
CREATE TABLE `sys_notice`  (
  `notice_id` int(4) NOT NULL AUTO_INCREMENT COMMENT '公告ID',
  `notice_title` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '公告标题',
  `notice_type` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '公告类型（1通知 2公告）',
  `notice_content` longblob NULL COMMENT '公告内容',
  `status` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '0' COMMENT '公告状态（0正常 1关闭）',
  `create_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT '创建者',
  `create_time` datetime NULL DEFAULT NULL COMMENT '创建时间',
  `update_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT '更新者',
  `update_time` datetime NULL DEFAULT NULL COMMENT '更新时间',
  `remark` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`notice_id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 3 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '通知公告表' ROW_FORMAT = Compact;

-- ----------------------------
-- Table structure for sys_oper_log
-- ----------------------------
DROP TABLE IF EXISTS `sys_oper_log`;
CREATE TABLE `sys_oper_log`  (
  `oper_id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '日志主键',
  `title` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT '模块标题',
  `business_type` int(2) NULL DEFAULT 0 COMMENT '业务类型（0其它 1新增 2修改 3删除）',
  `method` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT '方法名称',
  `request_method` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT '请求方式',
  `operator_type` int(1) NULL DEFAULT 0 COMMENT '操作类别（0其它 1后台用户 2手机端用户）',
  `oper_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT '操作人员',
  `dept_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT '部门名称',
  `oper_url` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT '请求URL',
  `oper_ip` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT '主机地址',
  `oper_location` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT '操作地点',
  `oper_param` varchar(2000) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT '请求参数',
  `json_result` varchar(2000) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT '返回参数',
  `status` int(1) NULL DEFAULT 0 COMMENT '操作状态（0正常 1异常）',
  `error_msg` varchar(2000) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT '错误消息',
  `oper_time` datetime NULL DEFAULT NULL COMMENT '操作时间',
  PRIMARY KEY (`oper_id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '操作日志记录' ROW_FORMAT = Compact;

-- ----------------------------
-- Table structure for sys_post
-- ----------------------------
DROP TABLE IF EXISTS `sys_post`;
CREATE TABLE `sys_post`  (
  `post_id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '岗位ID',
  `post_code` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '岗位编码',
  `post_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '岗位名称',
  `post_sort` int(4) NOT NULL COMMENT '显示顺序',
  `status` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '状态（0正常 1停用）',
  `create_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT '创建者',
  `create_time` datetime NULL DEFAULT NULL COMMENT '创建时间',
  `update_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT '更新者',
  `update_time` datetime NULL DEFAULT NULL COMMENT '更新时间',
  `remark` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`post_id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 5 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '岗位信息表' ROW_FORMAT = Compact;

-- ----------------------------
-- Records of sys_post
-- ----------------------------
INSERT INTO `sys_post` VALUES (1, 'ceo', '董事长', 1, '0', 'admin', '2022-04-26 13:52:24', '', NULL, '');
INSERT INTO `sys_post` VALUES (2, 'se', '项目经理', 2, '0', 'admin', '2022-04-26 13:52:24', '', NULL, '');
INSERT INTO `sys_post` VALUES (3, 'hr', '人力资源', 3, '0', 'admin', '2022-04-26 13:52:24', '', NULL, '');
INSERT INTO `sys_post` VALUES (4, 'user', '普通员工', 4, '0', 'admin', '2022-04-26 13:52:24', '', NULL, '');

-- ----------------------------
-- Table structure for sys_role
-- ----------------------------
DROP TABLE IF EXISTS `sys_role`;
CREATE TABLE `sys_role`  (
  `role_id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '角色ID',
  `role_name` varchar(30) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '角色名称',
  `role_key` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '角色权限字符串',
  `role_sort` int(4) NOT NULL COMMENT '显示顺序',
  `data_scope` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '1' COMMENT '数据范围（1：全部数据权限 2：自定数据权限 3：本部门数据权限 4：本部门及以下数据权限）',
  `menu_check_strictly` tinyint(1) NULL DEFAULT 1 COMMENT '菜单树选择项是否关联显示',
  `dept_check_strictly` tinyint(1) NULL DEFAULT 1 COMMENT '部门树选择项是否关联显示',
  `status` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '角色状态（0正常 1停用）',
  `del_flag` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '0' COMMENT '删除标志（0代表存在 2代表删除）',
  `create_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT '创建者',
  `create_time` datetime NULL DEFAULT NULL COMMENT '创建时间',
  `update_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT '更新者',
  `update_time` datetime NULL DEFAULT NULL COMMENT '更新时间',
  `remark` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`role_id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 3 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '角色信息表' ROW_FORMAT = Compact;

-- ----------------------------
-- Records of sys_role
-- ----------------------------
INSERT INTO `sys_role` VALUES (1, '超级管理员', 'admin', 1, '1', 1, 1, '0', '0', 'admin', '2022-04-26 13:52:24', '', NULL, '超级管理员');
INSERT INTO `sys_role` VALUES (2, '普通角色', 'common', 2, '2', 1, 1, '0', '0', 'admin', '2022-04-26 13:52:24', 'admin', '2022-07-19 18:28:01', '普通角色');

-- ----------------------------
-- Table structure for sys_role_dept
-- ----------------------------
DROP TABLE IF EXISTS `sys_role_dept`;
CREATE TABLE `sys_role_dept`  (
  `role_id` bigint(20) NOT NULL COMMENT '角色ID',
  `dept_id` bigint(20) NOT NULL COMMENT '部门ID',
  PRIMARY KEY (`role_id`, `dept_id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '角色和部门关联表' ROW_FORMAT = Compact;

-- ----------------------------
-- Records of sys_role_dept
-- ----------------------------
INSERT INTO `sys_role_dept` VALUES (2, 100);
INSERT INTO `sys_role_dept` VALUES (2, 101);
INSERT INTO `sys_role_dept` VALUES (2, 105);

-- ----------------------------
-- Table structure for sys_role_menu
-- ----------------------------
DROP TABLE IF EXISTS `sys_role_menu`;
CREATE TABLE `sys_role_menu`  (
  `role_id` bigint(20) NOT NULL COMMENT '角色ID',
  `menu_id` bigint(20) NOT NULL COMMENT '菜单ID',
  PRIMARY KEY (`role_id`, `menu_id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '角色和菜单关联表' ROW_FORMAT = Compact;

-- ----------------------------
-- Records of sys_role_menu
-- ----------------------------
INSERT INTO `sys_role_menu` VALUES (2, 1);
INSERT INTO `sys_role_menu` VALUES (2, 2);
INSERT INTO `sys_role_menu` VALUES (2, 3);
INSERT INTO `sys_role_menu` VALUES (2, 100);
INSERT INTO `sys_role_menu` VALUES (2, 101);
INSERT INTO `sys_role_menu` VALUES (2, 102);
INSERT INTO `sys_role_menu` VALUES (2, 103);
INSERT INTO `sys_role_menu` VALUES (2, 104);
INSERT INTO `sys_role_menu` VALUES (2, 105);
INSERT INTO `sys_role_menu` VALUES (2, 106);
INSERT INTO `sys_role_menu` VALUES (2, 107);
INSERT INTO `sys_role_menu` VALUES (2, 108);
INSERT INTO `sys_role_menu` VALUES (2, 109);
INSERT INTO `sys_role_menu` VALUES (2, 110);
INSERT INTO `sys_role_menu` VALUES (2, 111);
INSERT INTO `sys_role_menu` VALUES (2, 112);
INSERT INTO `sys_role_menu` VALUES (2, 113);
INSERT INTO `sys_role_menu` VALUES (2, 114);
INSERT INTO `sys_role_menu` VALUES (2, 115);
INSERT INTO `sys_role_menu` VALUES (2, 116);
INSERT INTO `sys_role_menu` VALUES (2, 500);
INSERT INTO `sys_role_menu` VALUES (2, 501);
INSERT INTO `sys_role_menu` VALUES (2, 1001);
INSERT INTO `sys_role_menu` VALUES (2, 1002);
INSERT INTO `sys_role_menu` VALUES (2, 1003);
INSERT INTO `sys_role_menu` VALUES (2, 1004);
INSERT INTO `sys_role_menu` VALUES (2, 1005);
INSERT INTO `sys_role_menu` VALUES (2, 1006);
INSERT INTO `sys_role_menu` VALUES (2, 1007);
INSERT INTO `sys_role_menu` VALUES (2, 1008);
INSERT INTO `sys_role_menu` VALUES (2, 1009);
INSERT INTO `sys_role_menu` VALUES (2, 1010);
INSERT INTO `sys_role_menu` VALUES (2, 1011);
INSERT INTO `sys_role_menu` VALUES (2, 1012);
INSERT INTO `sys_role_menu` VALUES (2, 1013);
INSERT INTO `sys_role_menu` VALUES (2, 1014);
INSERT INTO `sys_role_menu` VALUES (2, 1015);
INSERT INTO `sys_role_menu` VALUES (2, 1016);
INSERT INTO `sys_role_menu` VALUES (2, 1017);
INSERT INTO `sys_role_menu` VALUES (2, 1018);
INSERT INTO `sys_role_menu` VALUES (2, 1019);
INSERT INTO `sys_role_menu` VALUES (2, 1020);
INSERT INTO `sys_role_menu` VALUES (2, 1021);
INSERT INTO `sys_role_menu` VALUES (2, 1022);
INSERT INTO `sys_role_menu` VALUES (2, 1023);
INSERT INTO `sys_role_menu` VALUES (2, 1024);
INSERT INTO `sys_role_menu` VALUES (2, 1025);
INSERT INTO `sys_role_menu` VALUES (2, 1026);
INSERT INTO `sys_role_menu` VALUES (2, 1027);
INSERT INTO `sys_role_menu` VALUES (2, 1028);
INSERT INTO `sys_role_menu` VALUES (2, 1029);
INSERT INTO `sys_role_menu` VALUES (2, 1030);
INSERT INTO `sys_role_menu` VALUES (2, 1031);
INSERT INTO `sys_role_menu` VALUES (2, 1032);
INSERT INTO `sys_role_menu` VALUES (2, 1033);
INSERT INTO `sys_role_menu` VALUES (2, 1034);
INSERT INTO `sys_role_menu` VALUES (2, 1035);
INSERT INTO `sys_role_menu` VALUES (2, 1036);
INSERT INTO `sys_role_menu` VALUES (2, 1037);
INSERT INTO `sys_role_menu` VALUES (2, 1038);
INSERT INTO `sys_role_menu` VALUES (2, 1039);
INSERT INTO `sys_role_menu` VALUES (2, 1040);
INSERT INTO `sys_role_menu` VALUES (2, 1041);
INSERT INTO `sys_role_menu` VALUES (2, 1042);
INSERT INTO `sys_role_menu` VALUES (2, 1043);
INSERT INTO `sys_role_menu` VALUES (2, 1044);
INSERT INTO `sys_role_menu` VALUES (2, 1045);
INSERT INTO `sys_role_menu` VALUES (2, 1046);
INSERT INTO `sys_role_menu` VALUES (2, 1047);
INSERT INTO `sys_role_menu` VALUES (2, 1048);
INSERT INTO `sys_role_menu` VALUES (2, 1049);
INSERT INTO `sys_role_menu` VALUES (2, 1050);
INSERT INTO `sys_role_menu` VALUES (2, 1051);
INSERT INTO `sys_role_menu` VALUES (2, 1052);
INSERT INTO `sys_role_menu` VALUES (2, 1053);
INSERT INTO `sys_role_menu` VALUES (2, 1054);
INSERT INTO `sys_role_menu` VALUES (2, 1055);
INSERT INTO `sys_role_menu` VALUES (2, 1056);
INSERT INTO `sys_role_menu` VALUES (2, 1057);
INSERT INTO `sys_role_menu` VALUES (2, 1058);
INSERT INTO `sys_role_menu` VALUES (2, 1059);
INSERT INTO `sys_role_menu` VALUES (2, 1060);

-- ----------------------------
-- Table structure for sys_user
-- ----------------------------
DROP TABLE IF EXISTS `sys_user`;
CREATE TABLE `sys_user`  (
  `user_id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '用户ID',
  `dept_id` bigint(20) NULL DEFAULT NULL COMMENT '部门ID',
  `user_name` varchar(30) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '用户账号',
  `nick_name` varchar(30) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '用户昵称',
  `user_type` varchar(2) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '00' COMMENT '用户类型（00系统用户）',
  `email` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT '用户邮箱',
  `phonenumber` varchar(11) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT '手机号码',
  `sex` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '0' COMMENT '用户性别（0男 1女 2未知）',
  `avatar` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT '头像地址',
  `password` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT '密码',
  `status` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '0' COMMENT '帐号状态（0正常 1停用）',
  `del_flag` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '0' COMMENT '删除标志（0代表存在 2代表删除）',
  `login_ip` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT '最后登录IP',
  `login_date` datetime NULL DEFAULT NULL COMMENT '最后登录时间',
  `create_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT '创建者',
  `create_time` datetime NULL DEFAULT NULL COMMENT '创建时间',
  `update_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT '更新者',
  `update_time` datetime NULL DEFAULT NULL COMMENT '更新时间',
  `remark` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`user_id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 5 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '用户信息表' ROW_FORMAT = Compact;

-- ----------------------------
-- Records of sys_user
-- ----------------------------
INSERT INTO `sys_user` VALUES (1, 103, 'admin', 'admin', '00', 'ycw123129@163.com', '13521515438', '1', '', '$2a$10$BJJfF3.ImWufmN87752IUObAkpExqi9Kq6XEz5l5cy72vQgeoAeTm', '0', '0', '14.145.50.48', '2024-04-13 08:59:00', 'admin', '2022-04-26 13:52:23', '', '2024-04-13 08:59:00', '管理员');

-- ----------------------------
-- Table structure for sys_user_post
-- ----------------------------
DROP TABLE IF EXISTS `sys_user_post`;
CREATE TABLE `sys_user_post`  (
  `user_id` bigint(20) NOT NULL COMMENT '用户ID',
  `post_id` bigint(20) NOT NULL COMMENT '岗位ID',
  PRIMARY KEY (`user_id`, `post_id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '用户与岗位关联表' ROW_FORMAT = Compact;

-- ----------------------------
-- Records of sys_user_post
-- ----------------------------
INSERT INTO `sys_user_post` VALUES (1, 1);
INSERT INTO `sys_user_post` VALUES (4, 4);

-- ----------------------------
-- Table structure for sys_user_role
-- ----------------------------
DROP TABLE IF EXISTS `sys_user_role`;
CREATE TABLE `sys_user_role`  (
  `user_id` bigint(20) NOT NULL COMMENT '用户ID',
  `role_id` bigint(20) NOT NULL COMMENT '角色ID',
  PRIMARY KEY (`user_id`, `role_id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '用户和角色关联表' ROW_FORMAT = Compact;

-- ----------------------------
-- Records of sys_user_role
-- ----------------------------
INSERT INTO `sys_user_role` VALUES (1, 1);
INSERT INTO `sys_user_role` VALUES (4, 1);

SET FOREIGN_KEY_CHECKS = 1;
