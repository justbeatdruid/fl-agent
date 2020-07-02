package com.cmcc.algo.util;

import com.cmcc.algo.constant.CommonConstant;

import java.text.DateFormat;
import java.text.SimpleDateFormat;

public class TimeUtils {
    /**
     * 转化毫秒级时间戳为标准格式时间
     *
     * @param timestamp
     * @return
     */
    public static String getTimeStrByTimestamp(Long timestamp) {
        DateFormat sdf = new SimpleDateFormat(CommonConstant.DATE_FORMAT);
        return sdf.format(timestamp);
    }

    public static String getDurationStrByTimestamp(Long timestamp) {
        long time = timestamp / 1000;
        long hour = time / 3600;
        long minute = (time - 3600 * hour) / 60;
        long second = time - 3600 * hour - 60 * minute;
        StringBuilder sb = new StringBuilder();
        if (hour >= 1.0) {
            sb.append(hour + "h");
        }
        if (minute >= 1.0) {
            sb.append(minute + "min");
        }
        sb.append(second + "s");
        return sb.toString();
    }

    public static void main(String[] args) {
        System.out.println(System.currentTimeMillis());
        System.out.println(1593413513653L);
        System.out.println(getTimeStrByTimestamp(1593413513653L));
        System.out.println(getDurationStrByTimestamp(System.currentTimeMillis()-1593413513653L));
    }
}
