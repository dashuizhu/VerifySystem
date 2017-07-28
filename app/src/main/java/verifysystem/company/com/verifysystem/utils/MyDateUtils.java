package verifysystem.company.com.verifysystem.utils;

import android.text.TextUtils;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by zhuj on 2017/5/21 1:12.
 */
public class MyDateUtils {

    private static SimpleDateFormat mSdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    private static SimpleDateFormat mSdf2 = new SimpleDateFormat("yyyy-MM-dd HH:mm:00");

    public static String getNow() {
        return mSdf.format(new Date()).toString();
    }

    public static long getLong(String date) {
        if (TextUtils.isEmpty(date)) {
            return 0L;
        }
        try {
            return mSdf.parse(date).getTime();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return 0L;
    }

    public static String getTime(long time) {
        return mSdf2.format(new Date(time));
    }
}
