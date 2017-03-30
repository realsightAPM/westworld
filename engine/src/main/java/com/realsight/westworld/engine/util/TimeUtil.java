package com.realsight.westworld.engine.util;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

/**
 * @author shizifan
 * @date 16/9/9
 */
public class TimeUtil {
    public static String formatUnixtime(long unixSeconds){
        Date date = new Date(unixSeconds);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
        sdf.setTimeZone(TimeZone.getTimeZone("GMT+0"));
        String formattedDate = sdf.format(date);
        return formattedDate;
    }

    public static String formatUnixtime2(long unixSeconds){
        Date date = new Date(unixSeconds);
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM HH:mm:ss");
        sdf.setTimeZone(TimeZone.getTimeZone("GMT+0"));
        String formattedDate = sdf.format(date);
        return formattedDate;
    }

    public static long timeConversion(String time)
    {
        DateFormat dfm = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
        dfm.setTimeZone(TimeZone.getTimeZone("GMT+8:00"));//Specify your timezone
        long unixtime = 0;
        try
        {
            unixtime = dfm.parse(time).getTime();
        }
        catch (ParseException e)
        {
            e.printStackTrace();
        }
        return unixtime;
    }

    public static String timeConversion2(String time)
    {
        SimpleDateFormat dfm1 = new SimpleDateFormat("yyyy/M/d");
        SimpleDateFormat dfm2 = new SimpleDateFormat("yyyyMM");
        String formattedDate = "";
        try
        {
            long unixtime = dfm1.parse(time).getTime();
            formattedDate = String.valueOf(unixtime/1000);
        }
        catch (ParseException e)
        {
            e.printStackTrace();
        }
        return formattedDate;
    }

    public static String timeConversion3(String time)
    {
        SimpleDateFormat dfm1 = new SimpleDateFormat("yyyy/M/d");
        SimpleDateFormat dfm2 = new SimpleDateFormat("yyyy-MM");
        String formattedDate = "";
        try
        {
            long unixtime = dfm1.parse(time).getTime();
            formattedDate = dfm2.format(new Date(unixtime));
        }
        catch (ParseException e)
        {
            e.printStackTrace();
        }
        return formattedDate;
    }

}
