package verifysystem.company.com.verifysystem.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;

import verifysystem.company.com.verifysystem.eventbus.Event;
import verifysystem.company.com.verifysystem.utils.WifiUtils;

import de.greenrobot.event.EventBus;

/**
 * Created by hasee on 2017/4/18.
 */
public class WifiReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals(WifiManager.RSSI_CHANGED_ACTION)) {
            WifiManager wifiMgr = (WifiManager) context.getSystemService(context.WIFI_SERVICE);
            int strength = WifiUtils.getStrength(wifiMgr);
            setLevel(strength);
        } else if (intent.getAction().equals(WifiManager.NETWORK_STATE_CHANGED_ACTION)) {
            NetworkInfo info = intent.getParcelableExtra(WifiManager.EXTRA_NETWORK_INFO);
            if (info.getState().equals(NetworkInfo.State.DISCONNECTED)) {//如果断开连接
                setLevel(0);
            }
        } else if (intent.getAction().equals(WifiManager.WIFI_STATE_CHANGED_ACTION)) {
            //WIFI开关
            int wifistate = intent.getIntExtra(WifiManager.EXTRA_WIFI_STATE, WifiManager.WIFI_STATE_DISABLED);
            if (wifistate == WifiManager.WIFI_STATE_DISABLED) {//如果关闭
                setLevel(0);
            }
        }
    }

    private void setLevel(int strength){
        EventBus.getDefault().post(new Event.WifiEvent(strength));
    }
}
