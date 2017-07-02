package verifysystem.company.com.verifysystem.receiver;

import android.telephony.PhoneStateListener;
import android.telephony.ServiceState;
import android.telephony.SignalStrength;

import verifysystem.company.com.verifysystem.eventbus.Event;

import de.greenrobot.event.EventBus;

/**
 * Created by hasee on 2017/4/18.
 */
public class TelePhoneReceiver extends PhoneStateListener {

    /* 从得到的信号强度,每个供应商有更新 */
    @Override
    public void onSignalStrengthsChanged(SignalStrength signalStrength) {
        super.onSignalStrengthsChanged(signalStrength);
        EventBus.getDefault().post(new Event.TelePhoneEvent(signalStrength.getGsmSignalStrength()));
    }

    @Override
    public void onServiceStateChanged(ServiceState serviceState) {
        super.onServiceStateChanged(serviceState);
    }
}
