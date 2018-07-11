package com.standards.libhikvision.util;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * 字符转化的工具类
 */
public class StringUtils {
    private static SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm:ss");
    private static SimpleDateFormat ymdhmsformat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    private static SimpleDateFormat ymdformat = new SimpleDateFormat("yyyy-MM-dd");
    public static String stringForTime(int timeMs) {
        int totalSeconds = timeMs ;

        int seconds = totalSeconds % 60;
        int minutes = (totalSeconds / 60) % 60;
        int hours = totalSeconds / 3600;

        if (hours > 0) {
            return String.format(Locale.getDefault(), "%d:%02d:%02d", hours, minutes, seconds);
        } else {
            return String.format(Locale.getDefault(), "%02d:%02d", minutes, seconds);
        }
    }

    public static String millToTime(long timeMs) {
        return simpleDateFormat.format(new Date(timeMs));
    }

    public static String formatDate(long timeStamp) {
        return ymdhmsformat.format(timeStamp);
    }
    public static String formatDateYYMMDD(long timeStamp) {
        return ymdformat.format(timeStamp);
    }
}
