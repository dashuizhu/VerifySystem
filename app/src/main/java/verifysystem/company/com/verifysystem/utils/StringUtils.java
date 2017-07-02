package verifysystem.company.com.verifysystem.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by Administrator on 2016/5/25.
 */
public class StringUtils {

    private static SimpleDateFormat mSimpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    public static String value2String(int value) {
        if(value ==0 ) {
            return "OFF";
        } else {
            return ""+ value;
        }
    }

    public static String getTime(byte[] bytes) {
        int year = MyByteUtils.byteToInt(bytes[0]) * 256 + MyByteUtils.byteToInt(bytes[1]);
        //月份从0开始
        int month = MyByteUtils.byteToInt(bytes[2])-1;
        int day = MyByteUtils.byteToInt(bytes[3]);
        int hour = MyByteUtils.byteToInt(bytes[4]);
        int minute = MyByteUtils.byteToInt(bytes[5]);
        int second = MyByteUtils.byteToInt(bytes[6]);
        Calendar calendar = Calendar.getInstance();
        calendar.set(year, month, day, hour, minute, second);
        return mSimpleDateFormat.format(calendar.getTime());
    }

    public static byte[] time2byte(String time) {
        byte[] bytes = new byte[7];
        Date date;
        try {
            date = mSimpleDateFormat.parse(time);
        } catch (ParseException e) {
            e.printStackTrace();
            date = new Date();
        }
        bytes[0] = (byte) ((date.getYear()+1900) /256);
        bytes[1] = (byte) ((date.getYear()+1900) %256);
        bytes[2] = (byte) (date.getMonth()+1);
        bytes[3] = (byte) (date.getDate());
        bytes[4] = (byte) (date.getHours());
        bytes[5] = (byte) (date.getMinutes());
        bytes[6] = (byte) (date.getSeconds());
        return  bytes;
    }

    public static String timeFormat(String longStr) {
        String time = "";
        try {
            time = mSimpleDateFormat.format(new Date(Long.parseLong(longStr)));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return time;
    }

    /**
     * 获得yyyy-MM-dd HH:mm:ss
     * @return
     */
    public static String getTimeString() {
        return mSimpleDateFormat.format(new Date());
    }
}
