package com.styx.common.utils;

import java.util.*;

public class TimeUtil {

    public static final int SECONDS_IN_DAY = 60 * 60 * 24;
    public static final long MILLIS_IN_DAY = 1000L * SECONDS_IN_DAY;

    private static final TimeZone timeZone = TimeZone.getDefault();

    /**
     * 快速判断两个时间是否同一天
     *
     * @param d1 日期
     * @param d2 日期
     * @return 同一天true，否在false
     */
    public static boolean isSameDay(final Date d1, final Date d2) {
        if (d1 == null || d2 == null) {
            return false;
        }
        return isSameDay(d1.getTime(), d2.getTime());
    }

    /**
     * 快速判断两个时间是否同一天
     *
     * @param ms1 毫秒数1
     * @param ms2 毫秒数2
     * @return 同一天true，否在false
     */
    public static boolean isSameDay(final long ms1, final long ms2) {
        final long interval = ms1 - ms2;
        return interval < MILLIS_IN_DAY && interval > -1L * MILLIS_IN_DAY && toDay(ms1) == toDay(ms2);
    }

    /**
     * 转换为相对（1970）天数
     *
     * @param millis 毫秒数
     * @return 相对1970年1月1日计算机时间起始日期过了多少天
     */
    public static long toDay(long millis) {
        // 除以一天的毫秒数等于相对的天数，但是需要考虑时区问题
        return (millis + timeZone.getOffset(millis)) / MILLIS_IN_DAY;
    }

    /**
     * 获取去除了时分秒的日期
     *
     * @param time 日期
     * @return 去除了时分秒毫秒数的日期
     */
    public static Date toDayTime(Date time) {
        if (time == null)
            return null;
        long millis = time.getTime();
        millis = millis - ((millis + timeZone.getOffset(millis)) % MILLIS_IN_DAY);
        return new Date(millis);
    }

    /**
     * 获取时间段相差天数
     *
     * @param start 开始日期
     * @param end   结束日期
     * @return 日期间隔天数
     */
    public static long getIntervalDays(long start, long end) {
        start = start - ((start + timeZone.getOffset(start)) % MILLIS_IN_DAY);
        long endd = (end + timeZone.getOffset(end)) % MILLIS_IN_DAY;
        if (endd == 0) {
            return (end - start) / MILLIS_IN_DAY;
        } else {
            return (end - endd - start) / MILLIS_IN_DAY + 1;
        }
    }

    /**
     * 获取时间段内流水号列表
     *
     * @param startTime 开始时间
     * @param endTime   结束时间
     * @return 时间间隔内流水号
     */
    public static List<Integer> getSerialNumberByDay(Date startTime, Date endTime) {
        if (startTime == null || endTime == null) {
            return null;
        }

        List<Integer> sns = new ArrayList<>();
        long endMillis = endTime.getTime();

        long millis = startTime.getTime();
        millis = millis - ((millis + timeZone.getOffset(millis)) % MILLIS_IN_DAY);

        Calendar c = Calendar.getInstance();
        c.setTimeInMillis(millis);

        do {
            sns.add(getSerialNumberByDay(c));
            millis += MILLIS_IN_DAY;
            c.setTimeInMillis(millis);

        } while (millis < endMillis);

        return sns;
    }

    /**
     * 获取流水号
     *
     * @param date 日期
     * @return 日期流水号
     */
    public static int getSerialNumberByDay(Date date) {
        Calendar c = Calendar.getInstance();
        c.setTime(date);
        return getSerialNumberByDay(c);
    }

    /**
     * 获取流水号
     */
    public static int getSerialNumberByDay(Calendar c) {
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH) + 1;
        int day = c.get(Calendar.DAY_OF_MONTH);

        return year * 10000 + month * 100 + day;
    }

    /**
     * 获取月流水号
     */
    public static int getSerialNumberByMonth(Date date) {
        Calendar c = Calendar.getInstance();
        c.setTime(date);

        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH) + 1;

        return year * 100 + month;
    }

    /**
     * 是否今天
     */
    public static boolean isToday(Date time) {
        if (time == null) {
            return false;
        }

        long millis = time.getTime();
        millis = millis - ((millis + timeZone.getOffset(millis)) % MILLIS_IN_DAY);

        long nowMillis = System.currentTimeMillis();
        nowMillis = nowMillis - ((nowMillis + timeZone.getOffset(nowMillis)) % MILLIS_IN_DAY);

        return millis == nowMillis;
    }

    /**
     * 是否今天或以后的时间
     */
    public static boolean isAfterOrEqualToday(Date time) {
        if (time == null) {
            return false;
        }

        long millis = time.getTime();
        millis = millis - ((millis + timeZone.getOffset(millis)) % MILLIS_IN_DAY);

        long nowMillis = System.currentTimeMillis();
        nowMillis = nowMillis - ((nowMillis + timeZone.getOffset(nowMillis)) % MILLIS_IN_DAY);

        return millis >= nowMillis;
    }

    /**
     * 获取今天过去某天
     *
     * @param pastDays 过去多少天，负数则为未来多少天
     */
    public static Date getTodayBefore(int pastDays) {
        long millis = System.currentTimeMillis();
        millis = millis - ((millis + timeZone.getOffset(millis)) % MILLIS_IN_DAY);
        return new Date(millis - MILLIS_IN_DAY * pastDays);
    }

    /**
     * 获取某天的过去几天
     *
     * @param millis   时间戳
     * @param pastDays 过去多少天，负数则为未来多少天
     */
    public static Date getDateBefore(long millis, int pastDays) {
        millis = millis - ((millis + timeZone.getOffset(millis)) % MILLIS_IN_DAY);
        return new Date(millis - MILLIS_IN_DAY * pastDays);
    }

    /**
     * 获取昨天
     */
    public static Date getYesterday() {
        return getDateBefore(System.currentTimeMillis(), 1);
    }

    /**
     * 获取年份
     */
    public static int getYear(Date date) {
        Calendar c = Calendar.getInstance();
        c.setTime(date);
        return c.get(Calendar.YEAR);
    }

    /**
     * 获取月份（12）
     */
    public static int getMonth(Date date) {
        Calendar c = Calendar.getInstance();
        c.setTime(date);
        return c.get(Calendar.MONTH) + 1;
    }

    /**
     * 获取月中日期
     */
    public static int getDay(Date date) {
        Calendar c = Calendar.getInstance();
        c.setTime(date);
        return c.get(Calendar.DAY_OF_MONTH);
    }

    /**
     * 获取星期几(周一到周7)
     */
    public static int getWeekDay(Date date) {
        Calendar c = Calendar.getInstance();
        c.setTime(date);
        int w = c.get(Calendar.DAY_OF_WEEK);
        return w == 1 ? 7 : w - 1;
    }

    /**
     * 获取小时（24）
     */
    public static int getHour(Date date) {
        Calendar ca = Calendar.getInstance();
        ca.setTime(date);
        return ca.get(Calendar.HOUR_OF_DAY);
    }

    /**
     * 获取当前时间最近一个星期几
     */
    public static Date getLastWeekDay(int weekDay) {
        return getLastWeekDay(null, weekDay);
    }

    /**
     * 获取时间最近一个星期几
     */
    public static Date getLastWeekDay(Date date, int weekDay) {
        Calendar ca = Calendar.getInstance();
        if (date != null) {
            ca.setTime(date);
        }
        int dayWeek = ca.get(Calendar.DAY_OF_WEEK);
        int before = dayWeek - weekDay + 6;
        if (before >= 7) {
            before = before - 7;
        }
        return getDateBefore(ca.getTimeInMillis(), before);
    }

    /**
     * 是否日期为星期几
     */
    public static boolean isWeekDay(Date date, int weekDay) {
        Calendar ca = Calendar.getInstance();
        ca.setTime(date);
        int dayWeek = ca.get(Calendar.DAY_OF_WEEK) - 1;
        if (dayWeek == 0) {
            dayWeek = 7;
        }
        return dayWeek == weekDay;
    }

    /**
     * 两个日期是否在同一个星期
     */
    public static boolean inSameWeek(Date d1, Date d2) {

        long t1 = d1.getTime();
        long t2 = d2.getTime();

        t1 -= t1 % MILLIS_IN_DAY;
        t2 -= t2 % MILLIS_IN_DAY;

        long d = Math.abs(t1 - t2);

        if (d > MILLIS_IN_DAY * 7)
            return false;

        int day1 = (int) ((t1 / MILLIS_IN_DAY + 4) % 7);
        int day2 = (int) ((t2 / MILLIS_IN_DAY + 4) % 7);

        if (day1 == 0) day1 = 7;
        if (day2 == 0) day2 = 7;

        int x = Math.abs(day1 - day2);

        return x == (int) (d / MILLIS_IN_DAY);

    }

    /**
     * 根据出生日期计算年龄
     */
    public static int getAge(Date birthDay) {

        Calendar cal = Calendar.getInstance();

        if (cal.before(birthDay)) {
            return 0;
        }

        int yearNow = cal.get(Calendar.YEAR);
        int monthNow = cal.get(Calendar.MONTH);
        int dayOfMonthNow = cal.get(Calendar.DAY_OF_MONTH);
        cal.setTime(birthDay);

        int yearBirth = cal.get(Calendar.YEAR);
        int monthBirth = cal.get(Calendar.MONTH);
        int dayOfMonthBirth = cal.get(Calendar.DAY_OF_MONTH);

        int age = yearNow - yearBirth;

        if (monthNow <= monthBirth) {
            if (monthNow == monthBirth) {
                if (dayOfMonthNow < dayOfMonthBirth)
                    age--;
            } else {
                age--;
            }
        }
        return age;

    }

    /**
     * 获取某年某月中天数
     */
    public static int getDaysOfMonth(int year, int month) {
        int days = 0;
        switch (month) {
            case 1:
            case 3:
            case 5:
            case 7:
            case 8:
            case 10:
            case 12:
                days = 31;
                break;
            case 4:
            case 6:
            case 9:
            case 11:
                days = 30;
                break;
            case 2:
                if (year % 400 == 0 && (year % 4 == 0 || year % 100 != 0)) {
                    days = 29;
                } else {
                    days = 28;
                }
                break;
        }
        return days;
    }


}
