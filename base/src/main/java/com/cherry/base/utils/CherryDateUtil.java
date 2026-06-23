package com.cherry.base.utils;


import cn.hutool.core.date.DatePattern;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.date.format.FastDateFormat;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @author cherry
 * @version 1.0.0
 */
@SuppressWarnings("unused")
public class CherryDateUtil extends DateUtil {

    /**
     * 根据指定格式获取当前日期
     *
     * @param formatDateStr 格式示例
     * @return 当前日期
     */
    public static String getNowDate(String formatDateStr) {
        SimpleDateFormat format = new SimpleDateFormat(formatDateStr);
        return format.format(new Date(System.currentTimeMillis()));
    }

    /**
     * 获取当前日期的 Timestamp 实例
     *
     * @return 当前日期 Timestamp
     */
    public static Timestamp getNowTimestamp() {
        return new Timestamp(System.currentTimeMillis());
    }

    /**
     * 根据指定格式获取当前日期
     *
     * @param formatDateStr 格式示例
     * @return 当前日期
     */
    public static String format(Object object, String formatDateStr) {
        return new SimpleDateFormat(formatDateStr).format(object);
    }

    /**
     * 格式化 Timestamp
     *
     * @param formatDateStr 格式示例
     * @return 当前日期
     */
    public static String formatTimestamp(Timestamp timestamp, String formatDateStr) {
        return format(new Date(timestamp.getTime()), formatDateStr);
    }


    /**
     * timestamp > date
     *
     * @param timestamp 时间戳1
     * @return timestamp >= date ? true : false
     */
    public static boolean timestampThenNow(Timestamp timestamp) {
        return compare(new Date(timestamp.getTime()), new Date()) >= 0;
    }


    public static class CherryDatePattern extends DatePattern {

        public static final String CUSTOMER_PURE_DATETIME_MS_PATTERN = "yyMMddHHmmssSSS";

        public static final String CUSTOMER_PURE_DATETIME_PATTERN = "yyMMddHHmmss";

        public static final FastDateFormat CUSTOMER_PURE_DATETIME_FORMAT = FastDateFormat.getInstance("yyMMddHHmmss");

    }

}
