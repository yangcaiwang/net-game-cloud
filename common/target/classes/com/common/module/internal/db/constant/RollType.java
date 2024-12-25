
package com.common.module.internal.db.constant;

import com.common.module.internal.db.entity.DBEntityUtils;
import com.common.module.util.DateUnit;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * <表生成策略枚举类>
 * <p>
 * ps: 持久化日志数据滚表类型 如果有滚表,那么通过父类提供了快捷查询数据方法只能查当前最新生成那张表的数据,建议使用DaoImpl的api来操作数据库
 *
 * @author <yangcaiwang>
 * @version <1.0>
 */
public enum RollType {

    /**
     * 不滚动生成新表，也就是只使用一张表
     */
    NONE(null), //
    /**
     * 每小时生成一张新表
     */
    EVERY_HOUR("yyyyMMddHH"), //
    /**
     * 每天生成一张新表，注意如果那天没有任何日志数据产生是不会生成新表的
     */
    EVERY_DAY("yyyyMMdd"), //
    /**
     * 每月生成一张新表
     */
    EVERY_MONTH("yyyyMM"), //
    /**
     * 每年生成一张新表
     */
    EVERY_YEAR("yyyy"), //
    /**
     * 按月每周生成一张新表
     */
    EVERY_WEEK_OF_MONTH("yyyyMM"), //
    /**
     * 按年每周生成一张新表
     */
    EVERY_WEEK_OF_YEAR("yyyy"), //
    ;

    public final String format;

    private RollType(String format) {
        this.format = format;
    }

    /**
     * 滚表表名前缀(按不同日期策略滚表) {@link RollType}
     */
    public static final String PREFIX = "@";

    public static String fixedTableName(String tabName, RollType rollType) {

        return parsedTableName(tabName, rollType, System.currentTimeMillis());
    }

    public static String parsedTableName(String tabName, RollType rollType, long t) {
        if (rollType != RollType.NONE)
            return DBEntityUtils.merged(PREFIX, tabName, suffix(rollType, t));
        return tabName;
    }

    private static String suffix(RollType rollType, long t) {

        SimpleDateFormat formatter = new SimpleDateFormat(rollType.format);
        switch (rollType) {
            case EVERY_HOUR:
            case EVERY_DAY:
            case EVERY_MONTH:
            case EVERY_YEAR:
                return formatter.format(new Date(t));
            case EVERY_WEEK_OF_MONTH:
            case EVERY_WEEK_OF_YEAR:
                int weekIndex = rollType == EVERY_WEEK_OF_MONTH ? DateUnit.weekOfMonth(t) : DateUnit.weekOfYear(t);
                String dateStr = formatter.format(new Date(t));
                return dateStr + (weekIndex < 10 ? (0 + "" + weekIndex) : weekIndex);
            default:
                throw new RuntimeException("suffix unknown rollType[" + rollType + "]");
        }
    }
}
