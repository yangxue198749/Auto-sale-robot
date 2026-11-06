package com.kdx.vms.commons.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

/**
 *
 * Created by liyang on 16-12-6.
 */
public class DateUtils {

//    static SimpleDateFormat new SimpleDateFormat("yyyy-MM-dd") = new SimpleDateFormat("yyyy-MM-dd");
//    static SimpleDateFormat new SimpleDateFormat("yyyy-MM-dd HH:mm:ss") = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//    static SimpleDateFormat new SimpleDateFormat("HH:mm") = new SimpleDateFormat("HH:mm");
//    static SimpleDateFormat new SimpleDateFormat("HH:mm:ss") = new SimpleDateFormat("HH:mm:ss");

    private static final int reserveEndTime = Integer.parseInt((String) PropertiesLoader.map.get("reserveEndTime"));


    /**
     * Date 转成 yyyy-MM-dd格式字符串
     * @param date 时间
     * @return string
     */
    public static String getTime1(Date date) {
        return new SimpleDateFormat("yyyy-MM-dd").format(date);
    }

    /**
     * 获取当前时间yyyy-MM-dd格式字符串
     * @return string
     */
    public static String getTime1(){
        return getTime1(new Date());
    }

    /**
     * Date 转成 yyyy-MM-dd HH:mm:ss格式字符串
     * @param date 时间
     * @return string
     */
    public static String getTime2(Date date){
        if(null !=date){
            return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(date);
        }
        return null;
    }

    /**
     * 获取当前时间yyyy-MM-dd HH:mm:ss格式字符串
     * @return string
     */
    public static String getTime2(){
        return getTime2(new Date());
    }

    /**
     * Date 转成 HH:mm格式字符串
     * @param date 时间
     * @return string
     */
    public static String getTime3(Date date){
        return new SimpleDateFormat("HH:mm").format(date);
    }

    /**
     * 获取当前时间HH:mm格式字符串
     * @return string
     */
    public static String getTime3(){
        return getTime3(new Date());
    }

    /**
     * Date 转成 HH:mm:ss格式字符串
     * @param date 时间
     * @return string
     */
    public static String getTime4(Date date){
        return new SimpleDateFormat("HH:mm:ss").format(date);
    }

    /**
     * 获取当前时间HH:mm:ss格式字符串
     * @return string
     */
    public static String getTime4(){
        return getTime4(new Date());
    }

    /**
     * 将字符串转成时间 yyyy-MM-dd
     * @param date 时间字符串 yyyy-MM-dd
     * @return date
     */
    public static Date getDate1(String date) {
        try {
            return new SimpleDateFormat("yyyy-MM-dd").parse(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 将字符串转成时间 yyyy-MM-dd HH:mm:ss
     * @param date 时间字符串 yyyy-MM-dd HH:mm:ss
     * @return date
     */
    public static Date getDate2(String date) {
        try {
            return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 获取当天00:00:00.000
     * @return date
     */
    public static Date getToDayMin() {
        return getDayAfterMin(0);
    }

    /**
     * 获取当天23:59:59.999
     * @return date
     */
    public static Date getToDayMax() {
        return getDayAfterMax(0);
    }

    /**
     * 获取当月最后一天00:00:00.000
     * @return date
     */
    public static Date getMonthLastDayMin() {
        Calendar calendar = new GregorianCalendar();
        calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMaximum(Calendar.DAY_OF_MONTH));
        setMinTimeForDay(calendar);
        return calendar.getTime();
    }

    /**
     * 获取当月最后一天23:59:59.999
     * @return date
     */
    public static Date getMonthLastDayMax() {
        Calendar calendar = new GregorianCalendar();
        calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMaximum(Calendar.DAY_OF_MONTH));
        setMaxTimeForDay(calendar);
        return calendar.getTime();
    }

    /**
     * 获取当月最后一天23:59:59:999
     * @return date
     */
    public static Date getMonthLastDayMax(int year ,int month) {
    	Calendar calendar = Calendar.getInstance();
    	calendar.set(Calendar.YEAR,year);
    	calendar.set(Calendar.MONTH, month);
    	calendar.set(Calendar.DAY_OF_MONTH, 1);
    	calendar.add(Calendar.DAY_OF_MONTH, -1);
    	setMaxTimeForDay(calendar);
		return calendar.getTime();
    }

    /**
     * 获取当月第一天00:00:00.000
     * @return date
     */
    public static Date getMonthFirstDayMin() {
        Calendar calendar = new GregorianCalendar();
        calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMinimum(Calendar.DAY_OF_MONTH));
        setMinTimeForDay(calendar);
        return calendar.getTime();
    }

    /**
     * 获取当月第一天00:00:00.000
     * @return date
     */
    public static Date getMonthFirstDayMin(int year ,int month) {
    	Calendar calendar = Calendar.getInstance();
    	calendar.set(Calendar.YEAR,year);
    	calendar.set(Calendar.MONTH, month - 1);
    	calendar.set(Calendar.DAY_OF_MONTH, 1);
    	setMinTimeForDay(calendar);
		return calendar.getTime();
    }


    /**
     * 获取当月第一天23:59:59:999
     * @return date
     */
    public static Date getMonthFirstDayMax() {
        Calendar calendar = new GregorianCalendar();
        calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMinimum(Calendar.DAY_OF_MONTH));
        setMaxTimeForDay(calendar);
        return calendar.getTime();
    }

    /**
     * 获取当月第day天00:00:00.000
     * @return date
     */
    public static Date getMonthOfDayMin(int year ,int month ,int day) {
    	Calendar calendar = Calendar.getInstance();
    	calendar.set(Calendar.YEAR,year);
    	calendar.set(Calendar.MONTH, month - 1);
    	calendar.set(Calendar.DAY_OF_MONTH, day);
    	setMinTimeForDay(calendar);
    	return calendar.getTime();
    }

    /**
     * 获取当月第day天23:59:59:999
     * @return date
     */
    public static Date getMonthOfDayMax(int year ,int month ,int day) {
    	Calendar calendar = Calendar.getInstance();
    	calendar.set(Calendar.YEAR,year);
    	calendar.set(Calendar.MONTH, month -1);
    	calendar.set(Calendar.DAY_OF_MONTH, day);
    	setMaxTimeForDay(calendar);
		return calendar.getTime();
    }

    /**
     * 获取某月的天数
     * @return date
     */
    public static int getDaysOfMonth(int year ,int month){
    	Calendar calendar = Calendar.getInstance();
    	calendar.set(Calendar.YEAR,year);
    	calendar.set(Calendar.MONTH, month-1);
    	calendar.set(Calendar.DAY_OF_MONTH, 1);
    	calendar.add(Calendar.MONTH, 1);
    	calendar.add(Calendar.DAY_OF_MONTH, -1);
    	return calendar.get(Calendar.DAY_OF_MONTH);
    }

    /**
     * 获取月份
     * @return date
     */
    public static int getMonth(Date date){
    	Calendar calendar = Calendar.getInstance();
    	calendar.setTime(date);
    	return calendar.get(Calendar.MONTH) + 1;
    }

    /**
     * 获取年份
     * @return date
     */
    public static int getYear(Date date){
    	Calendar calendar = Calendar.getInstance();
    	calendar.setTime(date);
    	return calendar.get(Calendar.YEAR);
    }

    /**
     * 获取几天之前时间
     * @param day 天
     * @return date
     */
    public static Date getDayBefore(Integer day) {
        return getDayAfter(null == day ? 0 : -day);
    }

    /**
     * 获取几天之前00:00:00.000时间
     * @param day 天
     * @return date
     */
    public static Date getDayBeforeMin(Integer day) {
        return getDayAfterMin(null == day ? 0 : -day);
    }

    /**
     * 获取几天之前23:59:59.999时间
     * @param day 天
     * @return date
     */
    public static Date getDayBeforeMax(Integer day) {
        return getDayAfterMax(null == day ? 0 : -day);
    }

    /**
     * 获取几天之后时间
     * @param day 天
     * @return date
     */
    public static Date getDayAfter(Integer day) {
        Calendar calendar = new GregorianCalendar();
        calendar.set(Calendar.DAY_OF_MONTH, calendar.get(Calendar.DAY_OF_MONTH) + (null == day ? 0 : day));
        return calendar.getTime();
    }

    /**
     * 获取几天之后00:00:00.000时间
     * @param day 天
     * @return date
     */
    public static Date getDayAfterMin(Integer day) {
        Calendar calendar = new GregorianCalendar();
        calendar.set(Calendar.DAY_OF_MONTH, calendar.get(Calendar.DAY_OF_MONTH) + (null == day ? 0 : day));
        setMinTimeForDay(calendar);
        return calendar.getTime();
    }

    /**
     * 获取几天之后23:59:59.999时间
     * @param day 天
     * @return date
     */
    public static Date getDayAfterMax(Integer day) {
        Calendar calendar = new GregorianCalendar();
        calendar.set(Calendar.DAY_OF_MONTH, calendar.get(Calendar.DAY_OF_MONTH) + (null == day ? 0 : day));
        setMaxTimeForDay(calendar);
        return calendar.getTime();
    }

    /**
     * 获取几分钟之前的时间
     * @param minutesBefore 分钟
     * @return date
     */
    public static Date getMinutesBefore(Integer minutesBefore) {
        return getMinutesAfter(null == minutesBefore ? 0 : -minutesBefore);
    }

    /**
     * 获取几分钟之后的时间
     * @param minutesBefore 分钟
     * @return date
     */
    public static Date getMinutesAfter(Integer minutesBefore) {
        Calendar calendar = new GregorianCalendar();
        calendar.set(Calendar.MINUTE, calendar.get(Calendar.MINUTE) + (null == minutesBefore ? 0 : minutesBefore));
        return calendar.getTime();
    }

    private static void setMinTimeForDay(Calendar calendar) {
        calendar.set(Calendar.HOUR_OF_DAY, calendar.getActualMinimum(Calendar.HOUR_OF_DAY));
        calendar.set(Calendar.MINUTE, calendar.getActualMinimum(Calendar.MINUTE));
        calendar.set(Calendar.SECOND, calendar.getActualMinimum(Calendar.SECOND));
        calendar.set(Calendar.MILLISECOND, calendar.getActualMinimum(Calendar.MILLISECOND));
    }

    private static void setMaxTimeForDay(Calendar calendar) {
        calendar.set(Calendar.HOUR_OF_DAY, calendar.getActualMaximum(Calendar.HOUR_OF_DAY));
        calendar.set(Calendar.MINUTE, calendar.getActualMaximum(Calendar.MINUTE));
        calendar.set(Calendar.SECOND, calendar.getActualMaximum(Calendar.SECOND));
        calendar.set(Calendar.MILLISECOND, calendar.getActualMaximum(Calendar.MILLISECOND));
    }

    /**
     * 转换为时间（天,时:分:秒）
     * @param timeMillis
     * @return
     */
    public static String formatDateTime(long timeMillis){
        long day = timeMillis/(24*60*60*1000);
        long hour = (timeMillis/(60*60*1000)-day*24);
        long min = ((timeMillis/(60*1000))-day*24*60-hour*60);
        long s = (timeMillis/1000-day*24*60*60-hour*60*60-min*60);
        long sss = (timeMillis-day*24*60*60*1000-hour*60*60*1000-min*60*1000-s*1000);
        return (day>0?day+"天,":"")+hour+"小时"+min+"分"+s+"秒";
    }

    public static Integer getDay(String date) {
        return StringUtil.getIntValue(date.split("-")[2], 0);
    }

    public static int daysBetween(String time1,String time2) {
        Date date1 = getDate1(time1);
        Date date2 = getDate1(time2);
        return daysBetween(date1,date2);
    }

    public static int daysBetween(Date date1,Date date2) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date1);
        long time1 = cal.getTimeInMillis();
        cal.setTime(date2);
        long time2 = cal.getTimeInMillis();
        long between_days=(time2-time1)/(1000*3600*24);

        return Integer.parseInt(String.valueOf(between_days));
    }
    /**
     * 当前时间+天数
     * @param num
     * @return
     */
    public static Date plusDay1(int num){
        Calendar ca =Calendar.getInstance();
        ca.add(Calendar.DATE,num);
        Date d =ca.getTime();
        return d;
    }

    /**
     * 当前时间+月数
     * @param num
     * @return
     */
    public static Date plusDay2(int num){
        Calendar ca =Calendar.getInstance();
        ca.add(Calendar.MONTH,num);
        Date d =ca.getTime();
        return d;
    }

    /**
     * 指定日期+天数
     * @param date
     * @param day
     * @return
     */
    public static Date plusDay3(Date date ,int day){
        Calendar ca =Calendar.getInstance();
        ca.setTime(date);
        ca.add(Calendar.DATE,day);
        Date d =ca.getTime();
        return d;
    }

    /**
     * 指定日期+月
     * @param date
     * @param month
     * @return
     */
    public static Date plusMonth1(Date date ,int month){
        Calendar ca =Calendar.getInstance();
        ca.setTime(date);
        ca.add(Calendar.MONTH,month);
        Date d =ca.getTime();
        return d;
    }

    public static Date findTheLastDate(){
        LocalDateTime time = LocalDateTime.now().withHour(reserveEndTime).withMinute(0).withSecond(0).withNano(0);
        if(LocalDateTime.now().isAfter(time)){
            return localDateTimeFmtDateToString(time);
        }else {
            LocalDateTime beforeTime = LocalDateTime.now().minusHours(24).withHour(reserveEndTime).withMinute(0).withSecond(0).withNano(0);
            return localDateTimeFmtDateToString(beforeTime);
        }
    }

    private static Date localDateTimeFmtDateToString(LocalDateTime localDateTime){
        ZoneId zone = ZoneId.systemDefault();
        Instant instant = localDateTime.atZone(zone).toInstant();
        Date date = Date.from(instant);
        return date;
    }


    public static void main(String[] args) {
        LocalDateTime localDateTime = LocalDateTime.now();
        ZoneId zone = ZoneId.systemDefault();
        Instant instant = localDateTime.atZone(zone).toInstant();
        java.util.Date date = Date.from(instant);
        System.out.println(date);
    }
}
