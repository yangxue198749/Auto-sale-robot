package com.kdx.vms.commons.util;

import java.io.*;
import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class StringUtil {

    static SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm");
    public static String YMDPattern = "yyyy-MM-dd";
    public static String Pattern = "yyyy-MM-dd HH:mm:ss";
    public static String HMSPattern = "HH:mm:ss";

    private StringUtil() {
    }

    public static boolean isEmpty(String s) {
        return (s == null) || (s.trim().length() == 0);
    }

    public static String getPackage(String fqn) {
        int i = fqn.lastIndexOf('.');
        if (i <= 0) {
            return "";
        }
        return fqn.substring(0, i);
    }

    public static String getSimpleClassName(String fqn) {
        return fqn.substring(fqn.lastIndexOf('.') + 1);
    }

    public static String getRelativeSourcePath(String fqn) {
        if (isEmpty(fqn)) {
            throw new IllegalArgumentException("fqn is empty");
        }
        String relFilePath = fqn.replace(".", File.separator);
        int index = relFilePath.lastIndexOf(File.separator);
        if (index < 0)
            index = 0;
        int dollarIndex = relFilePath.indexOf("$", index);
        if (dollarIndex > 0) {
            relFilePath = relFilePath.substring(0, dollarIndex);
        }
        return relFilePath + ".java";
    }

    public static boolean isLowerCase(String s) {
        if (s == null)
            throw new IllegalArgumentException("String must not be null.");
        return s.toLowerCase(Locale.ENGLISH).equals(s);
    }

    public static Date getDate(String dates) {
        //时间转换格式
        Date date;
        try {
            date = formatter.parse(dates);
            return date;
        } catch (ParseException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return null;
    }

    public static Date FormatDate(Date d) {
        Date date;
        try {
            date = formatter.parse(formatter.format(d));
            return date;
        } catch (ParseException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return null;
    }

    public static String FormatStringDate(String date) {
        //String d = formatter.parse(date);
        return null;
    }

    public static String dateToStr(Date date, String pattern) {
        if (date == null || date.equals(""))
            return null;
        SimpleDateFormat formatter = new SimpleDateFormat(pattern);
        return formatter.format(date);
    }

    public static Date strToDate(String dateStr, String pattern) {
        if (dateStr == null || dateStr.equals(""))
            return null;
        SimpleDateFormat formatter = new SimpleDateFormat(pattern);
        try {
            return formatter.parse(dateStr);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String getUrlParams(InputStream in, String encoding) throws IOException {
        BufferedReader rd = null;
        String line;
        StringBuilder stringBuffer = new StringBuilder();
        try {
            rd = new BufferedReader(new InputStreamReader(in, encoding));
            while ((line = rd.readLine()) != null) {
                stringBuffer.append(line);
            }
        } catch (IOException e) {
        } finally {
            if (in != null) in.close();
            if (rd != null) rd.close();
        }
        return stringBuffer.toString();
    }

    /**
     * 防sql注入
     *
     * @param str
     * @return
     */
    public static String sqlStr(String str) {
        if (str != null) {
            str = replace(str, "'", "");
            str = replace(str, " ", "");
            str = replace(str, "\\", "&#92;");
        }
        return str;
    }

    /**
     * 字符串替换(src=源,oldstr=要替换的内容,newstr为要替换为的内容)
     *
     * @param src
     * @param oldstr
     * @param newstr
     * @return
     */
    public static String replace(String src, String oldstr, String newstr) {
        StringBuffer dest = new StringBuffer();
        int beginIndex = 0;
        int endIndex = 0;
        while (true) {
            endIndex = src.indexOf(oldstr, beginIndex);
            if (endIndex >= 0) {
                dest.append(src.substring(beginIndex, endIndex));
                dest.append(newstr);
                beginIndex = endIndex + oldstr.length();
            } else {
                dest.append(src.substring(beginIndex));
                break;
            }
        }
        return dest.toString();
    }

    /**
     * 如果字符串为空或null返回defaultValue（如果defaultValue为null返回0下同）
     * 删除字符串内的非数字字符，转为Integer返回
     *
     * @param str
     * @param defaultValue
     * @return
     */
    public static Integer getIntValue(String str, Integer defaultValue) {
        try {
            if (null == defaultValue) defaultValue = 0;
            if (isEmpty(str)) return defaultValue;
            boolean flag = false;
            if (str.charAt(0) == '-') {
                flag = true;
            }
            if (str.contains(".")) str = str.substring(0, str.indexOf("."));
            str = str.trim().replaceAll("\\D", "");
            if (isEmpty(str)) return defaultValue;
            if (flag) return 0 - Integer.parseInt(str);
            return Integer.parseInt(str);
        } catch (Exception e) {
            return defaultValue;
        }
    }

    /**
     * 如果字符串为空或null返回defaultValue（如果defaultValue为null返回0下同）
     * 删除字符串内的非数字字符，转为Integer返回
     *
     * @param str
     * @param defaultValue
     * @return
     */
    public static Double getDoubleValue(String str, Double defaultValue) {
        if (null == defaultValue) defaultValue = 0.0;
        if (isEmpty(str)) return defaultValue;
        if (str.contains(".")) {
            String str1;
            String str2;
            str1 = str.substring(0, str.indexOf(".")).trim().replaceAll("\\D", "");
            str2 = str.substring(str.indexOf(".")).trim().replaceAll("\\D", "");
            str = str1 + "." + str2;
        } else {
            str = str.trim().replaceAll("\\D", "");
        }
        if (isEmpty(str)) return defaultValue;
        return Double.parseDouble(str);
    }

    /**
     * 如果字符串为空或null返回defaultValue（如果defaultValue为null返回0下同）
     * 删除字符串内的非数字字符，转为Integer返回
     *
     * @param str
     * @param defaultValue
     * @return
     */
    public static Long getLongValue(String str, Long defaultValue) {
        if (null == defaultValue) defaultValue = 0L;
        if (isEmpty(str)) return defaultValue;
        if (str.contains(".")) str = str.substring(0, str.indexOf("."));
        str = str.trim().replaceAll("\\D", "");
        if (isEmpty(str)) return defaultValue;

        return Long.parseLong(str);
    }

    /**
     * 如果输入null或空串则返回defaultValue，否则返回原字符串.trim()
     *
     * @param str
     * @param defaultValue
     * @return
     */
    public static String getStringValue(String str, String defaultValue) {
        if (null == defaultValue) defaultValue = "";
        if (isEmpty(str)) return defaultValue;
        return str.trim();
    }

    /**
     * 如果输入true或false则返回true或false，否则返回defaultValue
     *
     * @param str
     * @param defaultValue
     * @return
     */
    public static boolean getBooleanValue(String str, boolean defaultValue) {
        if (isEmpty(str)) return defaultValue;
        return str.trim().toLowerCase().equals("true") || !str.trim().toLowerCase().equals("false") && defaultValue;
    }

    public static String join(String regex, Object... objects) {
        if (null == objects) return null;
        List list = Arrays.asList(objects);
        return join(regex, list);

    }

    public static String join(String regex, List list) {
        if (null == list || list.isEmpty()) return null;
        StringBuilder builder = new StringBuilder();
        for (Object obj : list) {
            if (null != obj) {
                if (builder.length() > 0) builder.append(regex);
                builder.append(obj.toString());
            }
        }
        return builder.toString();
    }

    public static Integer[] splitToIntegerArray(String regex, String str, Integer defaultValue) {
        String[] strs = str.split(regex);
        Integer[] integers = new Integer[strs.length];
        for (int i = 0; i < strs.length; i++) {
            integers[i] = getIntValue(strs[i], defaultValue);
        }
        return integers;
    }

    public static Double formatAmount(Integer price, Integer scale) {
        if (null != price) {
            return new BigDecimal(price).divide(new BigDecimal(100), scale, BigDecimal.ROUND_HALF_UP).doubleValue();
        }
        return 0.00D;
    }

    /**
     * 分转换成元 create by zhouxing
     *
     * @param amount
     * @return
     * @throws Exception
     */
    public static String changeF2Y(String amount) {
        try {
            return new BigDecimal(amount).divide(new BigDecimal(100), 6, BigDecimal.ROUND_HALF_UP).toString();
        } catch (Exception e) {

        }
        return "";
    }

    public static String formatAmount(Integer price) {
        if (null != price) {
            return new BigDecimal(price).divide(new BigDecimal(100), 2, BigDecimal.ROUND_HALF_UP).toString();
        }
        return "0.00";
    }

    public static void main(String[] args) {


    }
}