package verifysystem.company.com.verifysystem.ui;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.StringRes;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import verifysystem.company.com.verifysystem.global.Constant;
import verifysystem.company.com.verifysystem.interfaces.TopTitleBarActionListener;
import verifysystem.company.com.verifysystem.receiver.TelePhoneReceiver;
import verifysystem.company.com.verifysystem.utils.TelephonyUtils;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import verifysystem.company.com.verifysystem.R;

/**
 * Created by hasee on 2017/4/15.
 */
public class TopTitleBar extends RelativeLayout implements TopTitleBarActionListener {
    private Context mContext;
    private RelativeLayout mTopTitleHomely;
    private LinearLayout mTopTitleFunctionly;
    private ImageView mTitleFunctionIconIv;
    private TextView mTitleFunctionTv;
    private TextView mTvDeviceLink;

    /* battery*/
    private ImageView mBatteryIv;
    private boolean mIsCharging;
    private int mBatteryLevel;
    /* battery*/

    /* clock*/
    private TextView mClockTv;
    private Handler mClockHandler;
    private SimpleDateFormat mTimeFormat;
    private static final int MINUTE = 60;
    private static final int MINUTE_SCALE = 1000;
    /* clock*/

    /* telephony */
    private TelephonyManager mTelMgr;
    private RelativeLayout mHasSimLy;
    private ImageView mSignalLeveIv;
    private ImageView mNetworkTypeIv;
    private TextView mNoSimTv;
    private TelePhoneReceiver mPhoneReceiver;
    /* telephony */

    /*WIFI */
    private ImageView mWifiLevelIv;
    /*WIFI */


    public TopTitleBar(Context context) {
        super(context);
        init(context);
    }

    public TopTitleBar(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public TopTitleBar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        mContext = context;
        LayoutInflater inflater = (LayoutInflater) mContext
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.top_title_bar, this);
        mPhoneReceiver = new TelePhoneReceiver();
        mTelMgr = (TelephonyManager) mContext.getSystemService(Context.TELEPHONY_SERVICE);
        mTelMgr.listen(mPhoneReceiver, PhoneStateListener.LISTEN_SIGNAL_STRENGTHS);
        initView(view);
        initTelephonyStatus(0);
        initClock();
    }

    private void initView(View view) {
        mTitleFunctionIconIv = (ImageView) findViewById(R.id.title_bar_function_icon_iv);
        mTitleFunctionTv = (TextView) findViewById(R.id.title_bar_function_tv);
        mTopTitleHomely = (RelativeLayout) findViewById(R.id.top_left_title_bar_home_ly);
        mTopTitleFunctionly = (LinearLayout) findViewById(R.id.top_left_title_bar_function_ly);
        mTvDeviceLink = (TextView) findViewById(R.id.tv_device_status);

        mBatteryIv = (ImageView) findViewById(R.id.title_bar_battery_iv);

        mClockTv = (TextView) findViewById(R.id.title_bar_clock_tv);

        mHasSimLy = (RelativeLayout) findViewById(R.id.title_bar_has_simcard_ly);
        mSignalLeveIv = (ImageView) findViewById(R.id.title_bar_signal_level_iv);
        mNetworkTypeIv = (ImageView) findViewById(R.id.title_bar_network_type_iv);
        mNoSimTv = (TextView) findViewById(R.id.title_bar_no_simcard_tv);

        mWifiLevelIv = (ImageView) findViewById(R.id.title_bar_wifi_level_iv);
    }

    private void initTelephonyStatus(int signalLevel) {
        mNoSimTv.setVisibility(View.VISIBLE);
        mHasSimLy.setVisibility(View.GONE);
        int type = TelephonyUtils.getNetworkType(mTelMgr.getNetworkType());
        boolean hasSim = false;
        switch (type) {
            case TelephonyUtils.NETWORK_TYPE_2G:
                mNetworkTypeIv.setBackgroundResource(R.drawable.cellular_2g);
                hasSim = true;
                break;
            case TelephonyUtils.NETWORK_TYPE_3G:
                mNetworkTypeIv.setBackgroundResource(R.drawable.cellular_3g);
                hasSim = true;
                break;
            case TelephonyUtils.NETWORK_TYPE_4G:
                mNetworkTypeIv.setBackgroundResource(R.drawable.cellular_4g);
                hasSim = true;
                break;
            case TelephonyUtils.NETWORK_TYPE_UNKNOWN:
                break;
        }
        if (hasSim) {
            mNoSimTv.setVisibility(View.GONE);
            mHasSimLy.setVisibility(View.VISIBLE);
        }

        if (signalLevel > 30)
            signalLevel = 30;
        if (signalLevel < 0)
            signalLevel = 0;
        mSignalLeveIv.getDrawable().setLevel(signalLevel);
        mSignalLeveIv.postInvalidate();

    }

    private void initClock() {
        mClockHandler = new Handler(Looper.getMainLooper());
        mTimeFormat = new SimpleDateFormat("yyyy年MM月dd日 HH:mm");
        updateToCurrentTime();
        int currentSecond = Calendar.getInstance().get(Calendar.SECOND);
        mClockHandler.postDelayed(updateClock, (MINUTE - currentSecond)
                * MINUTE_SCALE);
    }

    private void updateToCurrentTime() {
        Date curDate = new Date(System.currentTimeMillis());
        String timeStr = mTimeFormat.format(curDate);

        mClockTv.setText(timeStr);
        mClockTv.invalidate();
    }

    private Runnable updateClock = new Runnable() {
        @Override
        public void run() {
            updateToCurrentTime();
            mClockHandler.postDelayed(this, MINUTE * MINUTE_SCALE);
        }
    };

    private void updateBatteryLevel() {
        if (mBatteryLevel < 0)
            mBatteryLevel = 0;
        if (mBatteryLevel > 100)
            mBatteryLevel = 100;

        if (mIsCharging) {
            mBatteryIv.setImageResource(R.drawable.battery_charge_level);
        } else {
            mBatteryIv.setImageResource(R.drawable.battery_nocharge_level);

        }
        mBatteryIv.getDrawable().setLevel(mBatteryLevel);
        mBatteryIv.postInvalidate();
    }

    @Override
    public void showHomeTitle() {
        mTopTitleFunctionly.setVisibility(View.GONE);
        mTopTitleHomely.setVisibility(View.VISIBLE);
    }

    @Override
    public void showFunctionTitle(int itemId) {
        setFunctionTitle(itemId);
        mTopTitleFunctionly.setVisibility(View.VISIBLE);
        mTopTitleHomely.setVisibility(View.GONE);
    }

    @Override
    public void syncTime() {
        updateToCurrentTime();
    }

    @Override
    public void updateTelephoneInfo(int signalLevel) {
        initTelephonyStatus(signalLevel);
    }

    @Override
    public void updateWifiInfo(int wifiStrengthlevel) {
        mWifiLevelIv.getDrawable().setLevel(wifiStrengthlevel);
    }

    @Override
    public void updateBatteryInfo(int batteryLevel, boolean isCharging) {
        mBatteryLevel = batteryLevel;
        mIsCharging = isCharging;
        updateBatteryLevel();
    }

    private void setFunctionTitle(int itemId) {
        String functionTitle = "";
        Drawable functionIcon = null;
        switch (itemId) {

            case Constant.ITEMID_VERFIY_ENGINEERING:
                functionTitle = getResources().getString(R.string.top_title_verify_engineering);
                functionIcon = getResources().getDrawable(R.drawable.top_title_verify_engineering_icon);
                break;
            case Constant.ITEMID_VERIFY_EQUIPMENT:
                functionTitle = getResources().getString(R.string.top_title_verify_equipment_management);
                functionIcon = getResources().getDrawable(R.drawable.top_title_verify_equipment_icon);
                break;
            case Constant.ITEMID_SYSTEM_SETTING:
                functionTitle = getResources().getString(R.string.top_title_system_setting);
                functionIcon = getResources().getDrawable(R.drawable.top_title_system_setting_icon);
                break;
            case Constant.ITEMID_ABOUT:
                functionTitle = getResources().getString(R.string.top_title_about);
                functionIcon = getResources().getDrawable(R.drawable.top_title_about_icon);
                break;
            default:
                break;
        }
        mTitleFunctionTv.setText(functionTitle);
        mTitleFunctionIconIv.setBackground(functionIcon);
    }

    /**
     * 更新设备连接状态信息
     * @param strResId
     */
    public void updateDeviceStatus(@StringRes int strResId) {
        mTvDeviceLink.setText(strResId);
    }

}
