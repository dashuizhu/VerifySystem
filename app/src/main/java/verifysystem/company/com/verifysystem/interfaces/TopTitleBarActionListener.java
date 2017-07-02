package verifysystem.company.com.verifysystem.interfaces;

/**
 * Created by hasee on 2017/4/21.
 */
public interface TopTitleBarActionListener {
    public void showHomeTitle();

    public void showFunctionTitle(int itemId);

    public void syncTime();

    public void updateTelephoneInfo(int signalLevel);

    public void updateWifiInfo(int wifiStrengthlevel);

    public void updateBatteryInfo(int batteryLevel, boolean isCharging);

    public void updateDeviceStatus(int resId);

}
