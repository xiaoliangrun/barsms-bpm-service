package com.cpic.barsms.bpm.common.utils;

import com.cpic.barsms.bpm.common.exception.BizBatchException;
import lombok.extern.slf4j.Slf4j;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAdjusters;
import java.util.Calendar;
import java.util.Date;

@Slf4j
public class DateFormatUtils {

    static final String DEFAULT_DATE_FORMAT = "yyyy-MM-dd HH:mm:ss";
    static final String DEFAULT_DATE_FORMAT_ID = "yyMMddHHmmssSSS";
    static final String DEFAULT_DATE_FORMAT_DATE = "yyMMdd";
    static final String DEFAULT_DATE_FORMAT_2 = "yyyy/MM/dd HH:mm:ss";
    static final String DATE_FORMAT = "yyyy-MM-dd";
    static final String DATE_FORMAT_2 = "yyyy/MM/dd";
    static final String MONTH_FORMAT = "yyyy-MM";
    static final String MONTH_FORMAT_2 = "yyyy/MM";

    private static DateTimeFormatter DEFAULT_FORMATTER = DateTimeFormatter.ofPattern(DEFAULT_DATE_FORMAT);
    private static DateTimeFormatter DEFAULT_FORMATTER_ID = DateTimeFormatter.ofPattern(DEFAULT_DATE_FORMAT_ID);
    private static DateTimeFormatter DEFAULT_FORMATTER_DATE = DateTimeFormatter.ofPattern(DEFAULT_DATE_FORMAT_DATE);

    public static LocalDateTime convert2LocalDateTime(Date date) {
        if (date == null) {
            throw new RuntimeException("convert2LocalDateTime date is null");
        }
        return date.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
    }

    public static String formatDefault(Date date) {
        LocalDateTime localDateTime = convert2LocalDateTime(date);
        String format = localDateTime.format(DEFAULT_FORMATTER);
        return format;
    }

    public static String customFormat(Date date, String custom) {
        LocalDateTime localDateTime = convert2LocalDateTime(date);
        DateTimeFormatter format = DateTimeFormatter.ofPattern(custom);
        String dateStr = localDateTime.format(format);
        return dateStr;
    }

    public static String formatTimestamp(Object obj, String custom) {
        try {
            if (obj instanceof Date) {
                SimpleDateFormat sdf = new SimpleDateFormat(custom);
                return sdf.format((Date) obj);
            }
            return null;
        } catch (Exception e) {
            return null;
        }
    }

    public static String getDateId() {
        LocalDateTime localDateTime = convert2LocalDateTime(new Date());
        String format = localDateTime.format(DEFAULT_FORMATTER_ID);
        return format;
    }

    public static String getDateDay() {
        LocalDateTime localDateTime = convert2LocalDateTime(new Date());
        String format = localDateTime.format(DEFAULT_FORMATTER_DATE);
        return format;
    }

    public static String addOneMonthAndFormat(Date date, int month, String custom) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(Calendar.MONTH, month);
        SimpleDateFormat sdf = new SimpleDateFormat(custom);
        return sdf.format(calendar.getTime());
    }

    /**
     * 获取下月第一天，返回 yyyy-MM-dd 格式字符串
     *
     * @return 下月第一天字符串
     */
    public static String getNextMonthFirstDay() {
        LocalDate tDay = LocalDate.now()
                .with(TemporalAdjusters.firstDayOfMonth())
                .plusMonths(1);
        return tDay.format(DateTimeFormatter.ofPattern(DATE_FORMAT));
    }

    public static Date parseDate(String dateStr) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        try {
            return sdf.parse(dateStr);
        } catch (ParseException e) {
            throw new BizBatchException("日期格式错误: " + dateStr);
        }
    }

    /**
     * 日期加一个月
     */
    public static Date addOneMonth(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(Calendar.MONTH, 1);
        return calendar.getTime();
    }

    /**
     * 格式化日期为 yyyy-MM-dd 格式
     */
    public static String formatDate(Date date) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        return sdf.format(date);
    }

}
