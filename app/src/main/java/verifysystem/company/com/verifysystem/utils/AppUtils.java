package verifysystem.company.com.verifysystem.utils;

import android.content.Context;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;

/**
 * Created by zhuj on 2017/5/21 13:38.
 */
public class AppUtils {

    public static String getMainId(Context context) {
        String mainId;
        if (SharedPreferencesUser.contains(context, SharedPreferencesUser.KEY_MAC)) {
            mainId = (String) SharedPreferencesUser.get(context, SharedPreferencesUser.KEY_MAC,"");
        } else {
            mainId = getMacAddress(context).replace(":","");
            SharedPreferencesUser.put(context, SharedPreferencesUser.KEY_MAC, mainId);
        }
        return mainId;
    }

    public static String getMacAddress(Context context) {
        WifiManager wifi = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        WifiInfo info = wifi.getConnectionInfo();
        return info.getMacAddress().toUpperCase();
    }
}
