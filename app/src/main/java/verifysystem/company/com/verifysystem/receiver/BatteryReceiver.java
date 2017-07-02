package verifysystem.company.com.verifysystem.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.BatteryManager;
import android.util.Log;

import verifysystem.company.com.verifysystem.eventbus.Event;

import de.greenrobot.event.EventBus;

/**
 * Created by hasee on 2017/4/18.
 */
public class BatteryReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            Log.d("BatteryReceiver", "onReceive");
            if (Intent.ACTION_BATTERY_CHANGED.equals(action)) {
                int status = intent.getIntExtra("status", -1);
                boolean isCharging = false;
                int level = intent.getIntExtra("level", 0);
                int scale = intent.getIntExtra("scale", 100);
                if (status == BatteryManager.BATTERY_STATUS_CHARGING) {
                    isCharging = true;
                }
                int batteryLevel = level * 100 / scale;
                EventBus.getDefault().post(new Event.BatteryEvent(isCharging, batteryLevel));
            }
        }
}
