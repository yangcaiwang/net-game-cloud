
package com.ycw.core.util;

import com.alibaba.fastjson.JSON;
import com.ycw.core.cluster.property.PropertyConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.*;
import java.time.temporal.ChronoUnit;
import java.time.temporal.WeekFields;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;


/**
 * <时间工具类>
 * <p>
 *
 * @author <yangcaiwang>
 * @version <1.0>
 */
public final class DateUnit {

    /**
     * 日志
     */
    private static final Logger log = LoggerFactory.getLogger(DateUnit.class);

    /**
     * 一秒钟
     */
    public static final int MILLISECOND_OF_ONE_SECONDS = 1000;

    /**
     * 一分钟
     */
    public static final int MILLISECOND_OF_ONE_MINUTE = MILLISECOND_OF_ONE_SECONDS * 60;

    /**
     * 一小时
     */
    public static final int MILLISECOND_OF_ONE_HOUR = MILLISECOND_OF_ONE_MINUTE * 60;

    /**
     * 一天
     */
    public static final int MILLISECOND_OF_ONE_DAY = MILLISECOND_OF_ONE_HOUR * 24;

    /**
     * 一周
     */
    public static final int MILLISECOND_OF_ONE_WEEK = MILLISECOND_OF_ONE_DAY * 7;

    public static final int SECOND_OF_ONE_DAY = 60 * 60 * 24;

    /**
     * 精确到秒的日期格式
     */
    public static final String DATE_FORMAT = "yyyy-MM-dd HH:mm:ss";
    public static final String DATE_FORMAT_OPEN = "yyyy-MM-ddHH:mm:ss";
    /**
     * 精确到秒的日期格式
     */
    public static final String DATEFORMAT = "yyyyMMddHHmmss";
    /**
     * 精确到毫秒的日期格式
     */
    public static final String DATE_FORMAT_SSS = "yyyy-MM-dd HH:mm:ss:SSS";
    /**
     * 精确到毫秒的日期格式
     */
    public static final String DATEFORMATSSS = "yyyyMMddHHmmssSSS";

    /**
     * 星期枚举 "星期日,1", "星期一,2", "星期二,3", "星期三,4", "星期四,5", "星期五,6", "星期六,7"
     *
     * @author lijishun
     */
    public static enum EnumWeek {
        /**
         * 周日
         */
        SUN(Calendar.SUNDAY, "星期日"),
        /**
         * 周一
         */
        MON(Calendar.MONDAY, "星期一"),
        /**
         * 周二
         */
        TUE(Calendar.TUESDAY, "星期二"),
        /**
         * 周三
         */
        WED(Calendar.WEDNESDAY, "星期三"),
        /**
         * 周四
         */
        THU(Calendar.THURSDAY, "星期四"),
        /**
         * 周五
         */
        FRI(Calendar.FRIDAY, "星期五"),
        /**
         * 周六
         */
        SAT(Calendar.SATURDAY, "星期六"),
        ;

        /**
         * 星期几的数学表达式
         */
        public final int value;

        /**
         * 星期几的中文表达式
         */
        public final String wstr;

        private EnumWeek(int value, String wstr) {
            this.value = value;
            this.wstr = wstr;
        }

        private static Map<Integer, EnumWeek> map = new HashMap<>();

        static {
            for (EnumWeek weekEnum : EnumWeek.values()) {
                map.put(weekEnum.value, weekEnum);
            }
        }

        /**
         * 根据星期几的数学表达式转成枚举
         */
        static public EnumWeek valueOf(int week) {
            return map.get(week);
        }
    }

    /**
     * 月份天数定义
     */
    public static final int[] MONTHS = { //
            31, // 1月
            28, // 2月
            31, // 3月
            30, // 4月
            31, // 5月
            30, // 6月
            31, // 7月
            31, // 8月
            30, // 9月
            31, // 10月
            30, // 11月
            31, // 12月
    };

    /**
     * 格林尼治时间偏移量 1970-01-01 00:00:00 时间戳
     */
    public static long offsetTime;

    static {
        try {
            offsetTime = time0();
        } catch (ParseException e) {
            log.error("Parse time for [1970-01-01 00:00:00]", e);
        }
    }

    /**
     * 时间戳转换
     */
    public static long convert(long duration, TimeUnit unit) {
        return unit.convert(duration, unit);
    }

    public static Duration castToDuration(long duration, TimeUnit unit) {
        long t = convert(duration, unit);
        if (unit == TimeUnit.NANOSECONDS) {
            return Duration.ofNanos(t);
        } else if (unit == TimeUnit.MILLISECONDS) {
            return Duration.ofMillis(t);
        } else if (unit == TimeUnit.SECONDS) {
            return Duration.ofSeconds(t);
        } else if (unit == TimeUnit.MINUTES) {
            return Duration.ofMinutes(t);
        } else if (unit == TimeUnit.HOURS) {
            return Duration.ofHours(t);
        } else if (unit == TimeUnit.DAYS) {
            return Duration.ofDays(t);
        } else {
            throw new RuntimeException("Can not cast to Duration,unknown :" + unit);
        }
    }

    /**
     * 将指定时间单位的时间戳转成纳秒时间戳
     */
    public static long castToNanos(long duration, TimeUnit unit) {
        return unit.toNanos(duration);
    }

    /**
     * 将指定时间单位的时间戳转成微秒时间戳
     */
    public static long castMicros(long duration, TimeUnit unit) {
        return unit.toMicros(duration);
    }

    /**
     * 将指定时间单位的时间戳转成毫秒时间戳
     */
    public static long castToMillis(long duration, TimeUnit unit) {
        return unit.toMillis(duration);
    }

    /**
     * 将指定时间单位的时间戳转成秒时间戳
     */
    public static long castToSeconds(long duration, TimeUnit unit) {
        return unit.toSeconds(duration);
    }

    /**
     * 将指定时间单位的时间戳转成分钟时间戳
     */
    public static long castToMinutes(long duration, TimeUnit unit) {
        return unit.toMinutes(duration);
    }

    /**
     * 将指定时间单位的时间戳转成小时时间戳
     */
    public static long castToHours(long duration, TimeUnit unit) {
        return unit.toHours(duration);
    }

    /**
     * 将指定时间单位的时间戳转成天时间戳
     */
    public static long castToDays(long duration, TimeUnit unit) {
        return unit.toDays(duration);
    }

    /**
     * 时间为null或者time<1
     */
    public static boolean isBlank(Date date) {

        return date == null || date.getTime() < 1;
    }

    /**
     * 时间为<1
     */
    public static boolean isBlank(long time) {

        return time < 1;
    }

    /**
     * 时间t是否处于当天h1:00:00~h2:00:00之间
     */
    public static boolean inHourTimes(long t, int h1, int h2) {

        return inTimes(t, h1 * MILLISECOND_OF_ONE_HOUR, h2 * MILLISECOND_OF_ONE_HOUR);
    }

    /**
     * 时间t是否处于当天h1:m1~h2:m2之间
     *
     * @param t  时间戳
     * @param h1 开始小时点
     * @param m1 开始分钟
     * @param h2 结束小时点
     * @param m2 结束分钟
     */
    public static boolean inHourMinuteTimes(long t, int h1, int m1, int h2, int m2) {

        return inTimes(t, h1 * MILLISECOND_OF_ONE_HOUR + m1 * MILLISECOND_OF_ONE_MINUTE, h2 * MILLISECOND_OF_ONE_HOUR + m2 * MILLISECOND_OF_ONE_MINUTE);
    }

    /**
     * 检查时间t是否处于当天t1~t2之间
     *
     * @param t  待检查的时间
     * @param t1 当天00:00:00 ~ (t1)xx:xx:xx 的时间戳
     * @param t2 当天00:00:00 ~ (t2)xx:xx:xx 的时间戳n
     */
    public static boolean inTimes(long t, int t1, int t2) {

        long t0 = timeAt0(t);
        int passTime = (int) (t - t0);
        int beginHour = t1 > t2 ? t2 : t1;
        int endHour = t1 > t2 ? t1 : t2;
        return passTime >= beginHour && passTime <= endHour;
    }

    /**
     * 获取起始日期的0点时间戳,取1970-01-01 00:00:00
     */
    public static long time0() throws ParseException {

        return timeMills(JSON.DEFFAULT_DATE_FORMAT, "1970-01-01 00:00:00");
    }

    /**
     * 1970-01-01 00:00:00
     */
    public static Date firstDate0() {

        return new Date(offsetTime);
    }

    /**
     * 1970-01-01 08:00:00
     */
    public static Date firstDate() {

        return new Date(0);
    }

    /**
     * 周一0点时间戳
     */
    public static int monZeroSecond() {
        return (int) (DateUnit.nextWeekInMon(System.currentTimeMillis(), EnumWeek.MON, 0).getTime() / 1000L);
    }

    /**
     * 根据当前时间,获取下几周指定星期几的0点时间(从周日开始算第一天)
     *
     * @param nowTime 当前时间
     * @param week    星期几
     * @param value   隔几周,如果是本周,就传0
     */
    public static int zeroWeekSec(long nowTime, EnumWeek week, int value) {
        return (int) (nextWeek(nowTime, week, value).getTime() / 1000L);
    }

    /**
     * 根据当前时间,获取下几周指定星期几的0点时间(从周日开始算第一天)
     *
     * @param time      当前时间
     * @param week      星期几
     * @param nextValue 隔几周,如果是本周,就传0
     */
    public static Date nextWeek(long time, EnumWeek week, int nextValue) {
        long concurrentTime0 = timeAt0(new Date(time));
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(concurrentTime0);

        cal.set(Calendar.DAY_OF_WEEK, week.value);
        cal.set(Calendar.HOUR, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        return new Date(cal.getTimeInMillis() + TimeUnit.DAYS.toMillis(7L * nextValue));
    }

    /**
     * 根据当前时间,获取下几周指定星期几的0点时间(从周一开始算第一天)
     *
     * @param time      当前时间
     * @param week      星期几
     * @param nextValue 隔几周,如果是本周,就传0
     */
    public static Date nextWeekInMon(long time, EnumWeek week, int nextValue) {
        return nextWeekInMonHour(time, week, nextValue, 0);
    }

    /**
     * 根据当前时间,获取下几周指定星期几的x点时间(从周一开始算第一天),按照小时算
     *
     * @param time      当前时间
     * @param week      星期几
     * @param nextValue 隔几周,如果是本周,就传0
     */
    public static Date nextWeekInMonHour(long time, EnumWeek week, int nextValue, int hour) {
        if (hour < 0 || hour > 23) {
            hour = 0;
        }
        long concurrentTime0 = timeAt0(new Date(time));
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(concurrentTime0);
        EnumWeek weekEnum = dayOfWeek(time);
        if (weekEnum == EnumWeek.SUN) {
            cal.add(Calendar.WEEK_OF_YEAR, -1);
        }

        cal.set(Calendar.DAY_OF_WEEK, week.value);
        cal.set(Calendar.HOUR, hour);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);

        return new Date(cal.getTimeInMillis() + TimeUnit.DAYS.toMillis(7L * nextValue));
    }

    /**
     * 获取这个时间是星期几
     */
    public static EnumWeek dayOfWeek(long time) {

        Calendar c = Calendar.getInstance();
        c.setTimeInMillis(time);
        int dayOfweek = c.get(Calendar.DAY_OF_WEEK);
        return EnumWeek.valueOf(dayOfweek);
    }

    /**
     * 获取这个时间是星期几,周一=1 周日=7
     */
    public static int weekDay(long time) {
        Calendar c = Calendar.getInstance();
        c.setTimeInMillis(time);
        int dayOfweek = c.get(Calendar.DAY_OF_WEEK);
        if (dayOfweek == EnumWeek.SUN.value) {
            dayOfweek = 7;
        } else {
            dayOfweek--;
        }
        return dayOfweek;
    }


    /**
     * 7为星期天,1为星期1
     *
     * @param weekDay 星期几
     * @return 星期枚举
     */
    public static EnumWeek weekOfEnumWeek(int weekDay) {
        if (weekDay >= 7) {
            return EnumWeek.SUN;
        } else if (weekDay <= 1) {
            return EnumWeek.MON;
        } else {
            return EnumWeek.valueOf(weekDay + 1);
        }
    }


    /**
     * 获取今天是本年的第几周 从周日开始算
     */
    public static int weekOfYear(long time) {

        Calendar c = Calendar.getInstance();
        c.setTimeInMillis(time);
        return c.get(Calendar.WEEK_OF_YEAR);
    }

    /**
     * 获取今天是本年的第几周 从周一开始算 如果新年第一周不包含星期一 周期则为前一年的最后一周周期
     */
    public static int getWeekNumberOfYear(long timestamp) {
        // 将时间戳转换为LocalDate
        LocalDate date = Instant.ofEpochMilli(timestamp).atZone(ZoneId.systemDefault()).toLocalDate();

        // 定义一周从周一开始 新年第一周不是从周一开始的 则属前一年的最后一周期数
        WeekFields weekFields = WeekFields.of(DayOfWeek.MONDAY, 7);

        int week = date.get(weekFields.weekOfYear());
        if (week == 0) {
            long time = nextWeekInMon(timestamp, EnumWeek.MON, 0).getTime();

            LocalDate preWeekDate = Instant.ofEpochMilli(time).atZone(ZoneId.systemDefault()).toLocalDate();

            return preWeekDate.get(weekFields.weekOfYear());
        }

        return date.get(weekFields.weekOfYear());
    }

    public static boolean isSameWeek(long time1, long time2) {
        if (time1 == 0L || time2 == 0L) {
            return false;
        }
        Calendar c = Calendar.getInstance();
        c.setTimeInMillis(time1);

        Calendar c1 = Calendar.getInstance();
        c1.setTimeInMillis(time2);
        return c.get(Calendar.YEAR) == c1.get(Calendar.YEAR) && c.get(Calendar.WEEK_OF_YEAR) == c1.get(Calendar.WEEK_OF_YEAR);
    }

    /**
     * 获取今天是本月的第几周
     */
    public static int weekOfMonth(long time) {

        Calendar c = Calendar.getInstance();
        c.setTimeInMillis(time);
        return c.get(Calendar.WEEK_OF_MONTH) + 1;
    }

    /**
     * 年份
     */
    public static int year(long time) {

        Calendar c = Calendar.getInstance();
        c.setTimeInMillis(time);
        int year = c.get(Calendar.YEAR);
        return year;
    }

    /**
     * 月份
     */
    public static int month(long time) {

        Calendar c = Calendar.getInstance();
        c.setTimeInMillis(time);
        int month = c.get(Calendar.MONTH);
        return month;
    }

    /**
     * 获取两个时间点间隔相差的天数
     *
     * @param smallTime 开始时间
     * @param largeTime 结束时间
     * @return 天数, 从1开始, intervalDay 从0开始
     */
    public static int getDifferenceDay(long smallTime, long largeTime) {
        return intervalDay(smallTime, largeTime) + 1;
    }

    /**
     * 真实相隔时间天，以每天5点一天
     *
     * @param smallTime 开始时间
     * @param largeTime 结束时间
     * @return 天数, 从1开始, intervalDay 从0开始
     */
    public static int getRealDiffTimeDayBy5(long smallTime, long largeTime) {
        long lastDay5 = timeAt5(smallTime);
        if (smallTime < lastDay5) {
            lastDay5 = timeAt5(getDiffDayMorning(smallTime, -1));
        }
        Date date1 = new Date();
        date1.setTime(lastDay5);

        Date date2 = new Date();
        date2.setTime(largeTime);
        long diffInMillies = date2.getTime() - date1.getTime();
        return (int) TimeUnit.DAYS.convert(diffInMillies, TimeUnit.MILLISECONDS);
    }

    /**
     * 今天几号
     */
    public static int day(long time) {

        Calendar c = Calendar.getInstance();
        c.setTimeInMillis(time);
        int day = c.get(Calendar.DAY_OF_MONTH);
        return day;
    }

    /**
     * @param time 时间戳
     * @return [月，日，时，分]
     */
    public static int[] getDayTimeArr(long time) {
        Calendar c = Calendar.getInstance();
        c.setTimeInMillis(time);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);
        int hour = c.get(Calendar.HOUR_OF_DAY);
        int min = c.get(Calendar.MINUTE);
        return new int[]{month, day, hour, min};
    }

    /**
     * 获取本月且间隔月数的开始时间戳
     *
     * @param interval 间隔月数 0:本月
     * @return 时间戳
     */
    public static long getMonthStartTime(int interval) {
        LocalDate now = LocalDate.now();

        // 设置为当前月的第一天
        LocalDate firstDayOfMonth = now.withDayOfMonth(1);

        // 添加间隔月数
        LocalDate firstDayOfTargetMonth = firstDayOfMonth.plusMonths(interval);

        // 将时间设置为00:00:00
        ZonedDateTime zonedDateTime = firstDayOfTargetMonth.atStartOfDay(ZoneId.systemDefault());

        // 获取时间戳（毫秒）
        return zonedDateTime.toInstant().toEpochMilli();
    }

    /**
     * 按指定格式转换日期表达式
     */
    public static String timeFormat(String format, long time) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(format);
        simpleDateFormat.setTimeZone(PropertyConfig.getGameTimeZone());
        return simpleDateFormat.format(time);
    }

    /**
     * 按yyyy-MM-dd HH:mm:ss 格式转换日期表达式
     */
    public static String timeFormat(long time) {
        return timeFormat(JSON.DEFFAULT_DATE_FORMAT, time);
    }

    /**
     * h时m分s秒
     */
    public static String timeFormatSecond(final int time) {
        final int finalTime = time * MILLISECOND_OF_ONE_SECONDS;
        int hour = finalTime / MILLISECOND_OF_ONE_HOUR;
        int minute = (finalTime - hour * MILLISECOND_OF_ONE_HOUR) / MILLISECOND_OF_ONE_MINUTE;
        int second = (finalTime - hour * MILLISECOND_OF_ONE_HOUR - minute * MILLISECOND_OF_ONE_MINUTE) / MILLISECOND_OF_ONE_SECONDS;
        StringBuilder result = new StringBuilder();
        if (hour > 0) {
            result.append(hour).append("时");
        }
        if (minute > 0) {
            result.append(minute).append("分");
        }
        if (second > 0) {
            result.append(second).append("秒");
        }
        return result.toString();
    }

    /**
     * 根据日期表达式转换时间戳
     */
    public static long timeMills(String format, String date) throws ParseException {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(format);
        simpleDateFormat.setTimeZone(PropertyConfig.getGameTimeZone());
        return simpleDateFormat.parse(date).getTime();
    }

    /**
     * 根据日期表达式(yyyy-MM-dd HH:mm:ss)转换时间戳
     */
    public static long timeMills(String date) throws ParseException {

        return timeMills(JSON.DEFFAULT_DATE_FORMAT, date);
    }

    /**
     * 根据日期表达式转换日期
     */
    public static Date timeDate(String format, String date) throws ParseException {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(format);
        simpleDateFormat.setTimeZone(PropertyConfig.getGameTimeZone());
        return simpleDateFormat.parse(date);
    }

    /**
     * 根据日期表达式(yyyy-MM-dd HH:mm:ss)转换日期
     */
    public static Date timeDate(String date) throws ParseException {

        return timeDate(JSON.DEFFAULT_DATE_FORMAT, date);
    }

    /**
     * 两个时间相隔天数,头一天23:59:59和下一天00:00:00也差一天,以两个时间在0点的时间戳对比得出
     */
    public static int intervalDay(long begin, long end) {
        return (int) (LocalDateTime.ofInstant(Instant.ofEpochSecond(end / 1000), PropertyConfig.getGameTimeZoneId()).toLocalDate().toEpochDay() - LocalDateTime.ofInstant(Instant.ofEpochSecond(begin / 1000), PropertyConfig.getGameTimeZoneId()).toLocalDate().toEpochDay());
    }

    /**
     * 是否同一天
     */
    public static boolean isSameDay(long t1, long t2) {

        return intervalDay(t1, t2) == 0;
    }

    public static boolean isSameMinute(int startTime, int endTime) {
        return endTime >= startTime && (endTime - startTime) < 60;
    }

    /**
     * ？年？月的天数
     */
    public static int monthOfDays(int year, int month) {

        if (month == 2) {
            int tmp = year % 4;
            if (tmp != 0) {
                // 平年 28天
                return MONTHS[1];
            }
            // 闰年 29天
            return MONTHS[1] + 1;
        }
        return MONTHS[month - 1];
    }

    /**
     * 取？时？分？秒的时间戳
     */
    public static long timeAt(long timeMills, int hour, int minute, int second) {

        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(timeMills);
        cal.set(Calendar.HOUR_OF_DAY, hour);
        cal.set(Calendar.MINUTE, minute);
        cal.set(Calendar.SECOND, second);
        cal.set(Calendar.MILLISECOND, 0);
        return cal.getTimeInMillis();
    }

    /**
     * 取日期？时？分？秒的时间戳
     */
    public static long timeAt(Date date, int hour, int minute, int second) {
        return timeAt(date.getTime(), hour, minute, second);
    }

    /**
     * 取当天 ？时？分？秒的时间戳
     */
    public static long timeAt(int hour, int minute, int second) {

        return timeAt(System.currentTimeMillis(), hour, minute, second);
    }

    /**
     * 取日期所在0点的时间戳
     */
    public static long timeAt5(Date date) {

        return timeAt5(date.getTime());
    }

    public static long timeAt5(long time) {

        return timeAt(time, 5, 0, 0);
    }

    /**
     * 取日期所在0点的时间戳
     */
    public static long timeAt0(Date date) {

        return timeAt(date.getTime(), 0, 0, 0);
    }

    /**
     * 取时间戳所在0点的时间戳
     */
    public static long timeAt0(long timeMills) {

        return timeAt(timeMills, 0, 0, 0);
    }

    /**
     * 获取开服当天0点时间戳
     */
    public static int openZero() {
        long result = timeAt(PropertyConfig.getOpenTime() * 1000L, 0, 0, 0);
        return (int) (result / 1000L);
    }

    /**
     * 取当天日期所在0点的时间戳
     */
    public static long timeAt0() {

        return timeAt(System.currentTimeMillis(), 0, 0, 0);
    }

    /**
     * 取明天0点的时间戳
     */
    public static long getNextDayMorning(long time) {
        return getDiffDayMorning(time, 1);
    }

    /**
     * 取指定时间偏移天数0点的时间戳
     */
    public static long getDiffDayMorning(long time, int diffDay) {
        long morning = timeAt0(time);
        return getDiffDayTime(morning, diffDay);
    }

    /**
     * 取指定时间偏移天数0点的时间戳
     */
    public static long getDiffDayTime(long time, int diffDay) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(time);
        calendar.add(Calendar.DATE, diffDay);
        calendar.set(Calendar.MILLISECOND, 0);
        return calendar.getTimeInMillis();
    }

    /**
     * 取指定时间下月1号0点的时间戳
     */
    public static long getNextMonthTimeMorning(long time) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(time);
        calendar.add(Calendar.MONTH, 1);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        calendar.set(Calendar.DAY_OF_MONTH, 1);
        return calendar.getTimeInMillis();
    }

    public static int getTimeHour(long time) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(time);
        return calendar.get(Calendar.HOUR_OF_DAY);
    }

    /**
     * 获取时间模版
     */
    public static DateTemplate getDateTemplate(long millis) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(millis);
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH) + 1;
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);
        int second = calendar.get(Calendar.SECOND);
        int millSecond = calendar.get(Calendar.MILLISECOND);
        EnumWeek week = dayOfWeek(millis);
        return DateTemplate.getDateTemplateClone(millis, year, month, day, hour, minute, second, millSecond, week);
    }

    /**
     * 获取时间模版(单例对象，定时器里面使用)
     */
    public static DateTemplate getDateTemplateInstance(long millis) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(millis);
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH) + 1;
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);
        int second = calendar.get(Calendar.SECOND);
        int millSecond = calendar.get(Calendar.MILLISECOND);
        EnumWeek week = dayOfWeek(millis);
        return DateTemplate.getDateTemplateInstance(millis, year, month, day, hour, minute, second, millSecond, week);
    }

    /**
     * 获取时间模版
     */
    public static DateTemplate getDateTemplate() {

        return getDateTemplate(System.currentTimeMillis());
    }

    public static long nowTimeSec() {
        return System.currentTimeMillis() / 1000;
    }

    /**
     * 获取当前时间从0点开始计算的总秒数
     */
    public static int getDaySecond() {

        long millisSecond = System.currentTimeMillis() - timeAt0();
        return (int) (millisSecond / MILLISECOND_OF_ONE_SECONDS);
    }

    /**
     * 获取开服时间
     */
    public static int getOpenDays() {
        int day = getOpenDays(PropertyConfig.getOpenTime());
        return Math.max(day, 1);
    }

    /**
     * 获取开服时间
     */
    public static int getMergeDays() {
        long serverMergeTime = PropertyConfig.getServerMergeTime();
        if (serverMergeTime <= 0) {
            return 0;
        }
        int day = getOpenDays(serverMergeTime);
        return Math.max(day, 1);
    }

    /**
     * 获取开服时间
     */
    public static int getOpenDays(long openTime) {
        return DateUnit.intervalDay(TimeUnit.SECONDS.toMillis(openTime), System.currentTimeMillis()) + 1;
    }

    /**
     * 日期的模板
     */
    public static class DateTemplate implements Cloneable {

        public long t;

        public int year;

        public int month;

        public int day;

        public int hour;

        public int minute;

        public int second;

        public int millSecond;

        public EnumWeek week;

        private static DateTemplate instance = new DateTemplate();

        private DateTemplate() {
        }

        private static DateTemplate getDateTemplateClone(long t, int year, int month, int day, int hour, int minute, int second, int millSecond, EnumWeek week) {
            try {
                DateTemplate clone = (DateTemplate) instance.clone();
                clone.setDateTemplateData(t, year, month, day, hour, minute, second, millSecond, week);
                return clone;
            } catch (Exception e) {
                log.error(e.getMessage());
            }
            DateTemplate dateTemplate = new DateTemplate();
            dateTemplate.setDateTemplateData(t, year, month, day, hour, minute, second, millSecond, week);
            return dateTemplate;
        }

        private static DateTemplate getDateTemplateInstance(long t, int year, int month, int day, int hour, int minute, int second, int millSecond, EnumWeek week) {
            instance.setDateTemplateData(t, year, month, day, hour, minute, second, millSecond, week);
            return instance;
        }

        private void setDateTemplateData(long t, int year, int month, int day, int hour, int minute, int second, int millSecond, EnumWeek week) {
            this.t = t;
            this.year = year;
            this.month = month;
            this.day = day;
            this.hour = hour;
            this.minute = minute;
            this.second = second;
            this.millSecond = millSecond;
            this.week = week;
        }

        @Override
        public String toString() {

            return timeFormat(DATE_FORMAT_SSS, t);
        }

        public String getStringTemplete() {

            return StringUtils.merged(year, month, day, hour, minute, second, millSecond, week);
        }
    }

    /**
     * 获取相隔周 周日开始算
     *
     * @param startTime 开始时间
     * @param endTime   结束时间
     * @return 相隔周数
     */
    public static int getDiffWeek(long startTime, long endTime) {
        long startTimeMi = nextWeek(startTime, EnumWeek.SUN, 0).getTime();
        long endTimeMi = nextWeek(endTime, EnumWeek.SUN, 0).getTime();
        int differenceDay = getDifferenceDay(startTimeMi, endTimeMi);
        return differenceDay / 7;
    }

    /**
     * 获取相隔周 周一开始算
     *
     * @param startTime 开始时间
     * @param endTime   结束时间
     * @return 相隔周数
     */
    public static int getDiffWeekByMon(long startTime, long endTime) {
        long time = DateUnit.nextWeekInMon(startTime, EnumWeek.MON, 0).getTime();
        long time2 = DateUnit.nextWeekInMon(endTime, EnumWeek.MON, 0).getTime();

        return (int) (Math.abs(time - time2) / DateUnit.MILLISECOND_OF_ONE_WEEK);
    }

    /**
     * 判断两个时间是否是同一个月
     */
    public static boolean isSameMonth(long time1, long time2) {
        Calendar cal1 = Calendar.getInstance();
        cal1.setTimeInMillis(time1);
        Calendar cal2 = Calendar.getInstance();
        cal2.setTimeInMillis(time2);
        return cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR) &&
                cal1.get(Calendar.MONTH) == cal2.get(Calendar.MONTH);
    }

    /**
     * @param date1 日期1
     * @param date2 日期2
     * @return 判断两个日期是否是同一个月
     */
    public static boolean isSameMonth(Date date1, Date date2) {
        Calendar cal1 = Calendar.getInstance();
        cal1.setTimeInMillis(date1.getTime());
        Calendar cal2 = Calendar.getInstance();
        cal2.setTimeInMillis(date2.getTime());
        return cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR) &&
                cal1.get(Calendar.MONTH) == cal2.get(Calendar.MONTH);
    }

    /**
     * 从周一开始计算两个时间点相差的周数
     *
     * @param startTime 开始时间
     * @param endTime   结束时间
     * @return 相差周数
     */
    public static long calculateWeeks(long startTime, long endTime) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(startTime);
        int sYear = calendar.get(Calendar.YEAR);
        int sMonth = calendar.get(Calendar.MONTH) + 1;
        int sDay = calendar.get(Calendar.DAY_OF_MONTH);

        calendar.setTimeInMillis(endTime);
        int eYear = calendar.get(Calendar.YEAR);
        int eMonth = calendar.get(Calendar.MONTH) + 1;
        int eDay = calendar.get(Calendar.DAY_OF_MONTH);

        LocalDate startDate = LocalDate.of(sYear, sMonth, sDay);  // 起始日期
        LocalDate endDate = LocalDate.of(eYear, eMonth, eDay);  // 结束日期

        return calculateWeeks(startDate, endDate);
    }

    private static long calculateWeeks(LocalDate startDate, LocalDate endDate) {
        // 将日期调整到所在周的周一
        LocalDate adjustedStartDate = startDate.with(DayOfWeek.MONDAY);
        LocalDate adjustedEndDate = endDate.with(DayOfWeek.MONDAY);

        // 计算相差的周数
        return ChronoUnit.WEEKS.between(adjustedStartDate, adjustedEndDate);
    }
}
