package verifysystem.company.com.verifysystem.eventbus;

/**
 * Created by hasee on 2017/4/18.
 */
public class Event {
    public static class BatteryEvent {
        private static final String TAG = BatteryEvent.class.getSimpleName();
        private boolean isCharging;
        private int batteryLevel;

        public BatteryEvent(boolean isCharging, int batteryLevel) {
            this.isCharging = isCharging;
            this.batteryLevel = batteryLevel;
        }

        public boolean isCharging() {
            return isCharging;
        }

        public int getBatteryLevel() {
            return batteryLevel;
        }
    }

    public static class WifiEvent {
        private static final String TAG = WifiEvent.class.getSimpleName();
        private int wifiStrengthLevel;

        public WifiEvent(int wifiStrengthLevel) {
            this.wifiStrengthLevel = wifiStrengthLevel;
        }

        public int getWifiStrengthLevel() {
            return wifiStrengthLevel;
        }
    }

    public static class TelePhoneEvent {
        private static final String TAG = TelePhoneEvent.class.getSimpleName();
        private int signalLevel;

        public TelePhoneEvent(int signalLevel) {
            this.signalLevel = signalLevel;
        }

        public int getSignalLevel() {
            return signalLevel;
        }
    }

    public static class TimeEvent {

    }

    public static class CmdEvent {
        private int cmdType;
        public CmdEvent(byte cmdType) {
            this.cmdType = cmdType;
        }
        public int getCmdType() {
            return cmdType;
        }
    }

    public static class RecordEvent {
        private String snNo;
        private double temp;
        private double hum;
        public RecordEvent(String snNo, double temp, double hum) {
            this.snNo = snNo;
            this.temp = temp;
            this.hum = hum;
        }

        public String getSnNo() {
            return snNo;
        }

        public double getTemp() {
            return temp;
        }

        public double getHum() {
            return hum;
        }
    }

    public static class DeviceLinkStatus {
        private String action;
        public DeviceLinkStatus(String action) {
            this.action = action;
        }

        public String getAction() {
            return action;
        }
    }
}