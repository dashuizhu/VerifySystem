package verifysystem.company.com.verifysystem.utils;

import android.telephony.TelephonyManager;

/**
 * Created by hasee on 2017/4/18.
 */
public class TelephonyUtils {
        public static final int NETWORK_TYPE_UNKNOWN = -1;
        public static final int NETWORK_TYPE_2G = 2;       //2G
        public static final int NETWORK_TYPE_3G = 3;       //3G
        public static final int NETWORK_TYPE_4G = 4;       //4G


    public static int getNetworkType(int type){
        switch (type) {
            case TelephonyManager.NETWORK_TYPE_GPRS:
            case TelephonyManager.NETWORK_TYPE_EDGE:
            case TelephonyManager.NETWORK_TYPE_CDMA:
            case TelephonyManager.NETWORK_TYPE_1xRTT:
            case TelephonyManager.NETWORK_TYPE_IDEN:
                return NETWORK_TYPE_2G;

            case TelephonyManager.NETWORK_TYPE_UMTS:
            case TelephonyManager.NETWORK_TYPE_EVDO_0:
            case TelephonyManager.NETWORK_TYPE_EVDO_A:
            case TelephonyManager.NETWORK_TYPE_HSDPA:
            case TelephonyManager.NETWORK_TYPE_HSUPA:
            case TelephonyManager.NETWORK_TYPE_HSPA:
            case TelephonyManager.NETWORK_TYPE_EVDO_B:
            case TelephonyManager.NETWORK_TYPE_EHRPD:
            case TelephonyManager.NETWORK_TYPE_HSPAP:
                return NETWORK_TYPE_3G;

            case TelephonyManager.NETWORK_TYPE_LTE:
                return NETWORK_TYPE_4G;

            default:
                return NETWORK_TYPE_UNKNOWN;
        }
    };
}
