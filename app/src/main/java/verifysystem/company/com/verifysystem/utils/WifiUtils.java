package verifysystem.company.com.verifysystem.utils;

import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;

/**
 * Created by hasee on 2017/4/19.
 */
public class WifiUtils {
    public static int getStrength(WifiManager mWifiMgr) {
        WifiInfo info = mWifiMgr.getConnectionInfo();
        if (info.getBSSID() != null) {
            int strength = WifiManager.calculateSignalLevel(info.getRssi(), 5);
            // 链接速度
            // int speed = info.getLinkSpeed();
            // // 链接速度单位
            // String units = WifiInfo.LINK_SPEED_UNITS;
            // // Wifi源名称
            // String ssid = info.getSSID();
            return strength;
        }
        return 0;
    }
}
