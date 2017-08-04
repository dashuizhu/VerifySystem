package verifysystem.company.com.verifysystem.activity;

import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;
import de.greenrobot.event.EventBus;
import java.util.List;
import java.util.concurrent.TimeUnit;
import org.apache.commons.collections.CollectionUtils;
import rx.Observable;
import rx.Subscriber;
import rx.functions.Action1;
import verifysystem.company.com.verifysystem.AppApplication;
import verifysystem.company.com.verifysystem.R;
import verifysystem.company.com.verifysystem.connection.ConnectAction;
import verifysystem.company.com.verifysystem.connection.agreement.CmdPackage;
import verifysystem.company.com.verifysystem.eventbus.Event;
import verifysystem.company.com.verifysystem.fragment.AboutFragment;
import verifysystem.company.com.verifysystem.fragment.SystemSettingFragment;
import verifysystem.company.com.verifysystem.fragment.VerifyDataFragment;
import verifysystem.company.com.verifysystem.fragment.VerifyDeviceFragment;
import verifysystem.company.com.verifysystem.fragment.VerifyProjectFragment;
import verifysystem.company.com.verifysystem.global.Constant;
import verifysystem.company.com.verifysystem.interfaces.TopTitleBarActionListener;
import verifysystem.company.com.verifysystem.model.DeviceResult;
import verifysystem.company.com.verifysystem.network.AppModel;
import verifysystem.company.com.verifysystem.receiver.BatteryReceiver;
import verifysystem.company.com.verifysystem.receiver.WifiReceiver;
import verifysystem.company.com.verifysystem.services.UploadService;
import verifysystem.company.com.verifysystem.ui.SinkButton;
import verifysystem.company.com.verifysystem.ui.TopTitleBar;
import verifysystem.company.com.verifysystem.utils.SharedPreferencesUser;

public class MainActivity extends BaseActivity {

  private final String TAG_VERIFY_PROJECT = "verifyProject";
  private final String TAG_VERIFY_DATA = "verifyData";
  private final String TAG_VERIFY_DEVICE = "verifyDevice";
  private final String TAG_ABOUT = "about";
  private final String TAG_SETTING = "setting";

  private FragmentManager mFragMgr;
    private FragmentTransaction mTransaction;
    private VerifyProjectFragment mVerifyProjectFragment;
    private VerifyDeviceFragment mVerifyDeviceFragment;
    private SystemSettingFragment mSystemSettingFragment;
    private AboutFragment mAboutFragment;
    private VerifyDataFragment mVerifyDataFragment;
    private Fragment mCurrentFragment;

    private LinearLayout mHomePageLayout;
    private LinearLayout mTopTitleBackLy;

    private SinkButton mVerifyEngineeringBtn;
    private SinkButton mVerifyEquipmentMgmtBtn;
    private SinkButton mSystemSettingBtn;
    private SinkButton mAboutBtn;

    private TopTitleBarActionListener mTopTitleBarActionListener;
    private BatteryReceiver mBatteryReceiver;
    private WifiReceiver mWifiReceiver;

    private int mLastItemId;
    public static final int SHOW_HOME_PAGE = 0;
    public static final int SHOW_FRAGMENT_PAGE = 1;

    public static final int FLAG_HOMEKEY_DISPATCHED = 0x80000000;

    private AppModel mAppModel;

    private View.OnClickListener mOnClickListener = new View.OnClickListener() {
        @Override public void onClick(View view) {
            Message message = Message.obtain();
            message.what = SHOW_FRAGMENT_PAGE;
            int itemId = view.getId();
            message.obj = itemId;
            mLastItemId = itemId;
            mSwitchHandler.sendMessageDelayed(message, 200);
        }
    };

    private Handler mSwitchHandler = new Handler(new Handler.Callback() {
        @Override public boolean handleMessage(Message message) {
            switch (message.what) {
                case SHOW_HOME_PAGE:
                    showHomePage();
                    break;
                case SHOW_FRAGMENT_PAGE:
                    int i = (Integer) message.obj;
                    showFragmentPage(i);
                    break;
            }
            return false;
        }
    });

    @Override protected void onCreate(Bundle savedInstanceState) {
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        this.getWindow().setFlags(FLAG_HOMEKEY_DISPATCHED, FLAG_HOMEKEY_DISPATCHED);
        setContentView(R.layout.activity_main);

        //电量监听只能用动态注册
        mBatteryReceiver = new BatteryReceiver();
        IntentFilter batteryFilter = new IntentFilter();
        batteryFilter.addAction(Intent.ACTION_BATTERY_CHANGED);
        registerReceiver(mBatteryReceiver, batteryFilter);

        //WIFI监听
        mWifiReceiver = new WifiReceiver();
        IntentFilter wifiFilter = new IntentFilter();
        wifiFilter.addAction(WifiManager.NETWORK_STATE_CHANGED_ACTION);
        wifiFilter.addAction(WifiManager.WIFI_STATE_CHANGED_ACTION);
        wifiFilter.addAction(WifiManager.RSSI_CHANGED_ACTION);
        wifiFilter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
        registerReceiver(mWifiReceiver, wifiFilter);

        mFragMgr = getSupportFragmentManager();
      if (savedInstanceState != null) {
        mVerifyProjectFragment =
                (VerifyProjectFragment) getSupportFragmentManager().findFragmentByTag(
                        TAG_VERIFY_PROJECT);
        mVerifyDeviceFragment =
                (VerifyDeviceFragment) getSupportFragmentManager().findFragmentByTag(
                        TAG_VERIFY_DEVICE);
        mSystemSettingFragment =
                (SystemSettingFragment) getSupportFragmentManager().findFragmentByTag(TAG_SETTING);
        mAboutFragment = (AboutFragment) getSupportFragmentManager().findFragmentByTag(TAG_ABOUT);
        mVerifyDataFragment =
                (VerifyDataFragment) getSupportFragmentManager().findFragmentByTag(TAG_VERIFY_DATA);
      }
        initFragment();
        initView();
        EventBus.getDefault().registerSticky(this);

        linkLastDevice();

        int coloetTime = (int) SharedPreferencesUser.get(this, SharedPreferencesUser.KEY_TIME_COLLECT_MINUTE, 1);
        Constant.DATE_TIME_OUT = coloetTime * 3 * 60 * 1000 +10000;

        //test
        //showFragmentPage(R.id.home_verify_equipment_management_btn);
    }

    private void linkLastDevice() {
        BluetoothAdapter adapter = BluetoothAdapter.getDefaultAdapter();
      if (adapter == null) {
        Log.e("main", "no  blue adapter");
        return;
      }
        if (AppApplication.getDeivceManager().getConnect().isLink()) {
            return;
        }
        int time = 0;
        if (!adapter.isEnabled()) {
            adapter.enable();
            time = 1;
        }
        Observable.timer(time, TimeUnit.SECONDS).subscribe(new Action1<Long>() {
            @Override public void call(Long aLong) {
                String lastAddress =
                        (String) SharedPreferencesUser.get(MainActivity.this, SharedPreferencesUser.KEY_LAST_BLUETOOTH,
                                "");
                if (!TextUtils.isEmpty(lastAddress)) {
                    AppApplication.getDeivceManager().getConnect().connect(lastAddress, "");
                }
            }
        });
    }

    @Override protected void onResume() {
        super.onResume();
    }

    @Override protected void onRestoreInstanceState(Bundle savedInstanceState) {
        mLastItemId = savedInstanceState.getInt("position");
        showFragmentPage(mLastItemId);
        super.onRestoreInstanceState(savedInstanceState);
    }

    @Override protected void onSaveInstanceState(Bundle outState) {
        //记录当前的position
        outState.putInt("position", mLastItemId);
      super.onSaveInstanceState(outState);
    }

    @Override public void onDestroy() {
        unregisterReceiver(mBatteryReceiver);
        unregisterReceiver(mWifiReceiver);
        EventBus.getDefault().unregister(this);
        super.onDestroy();
    }

    private void initView() {
        mTopTitleBarActionListener = (TopTitleBar) findViewById(R.id.top_title_bar);
        mHomePageLayout = (LinearLayout) findViewById(R.id.home_page_layout);

        mTopTitleBackLy = (LinearLayout) findViewById(R.id.title_bar_back_ly);
        mTopTitleBackLy.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View view) {
                backHomePage();
            }
        });

        mVerifyEngineeringBtn = (SinkButton) findViewById(R.id.home_verify_engineering_btn);
        mVerifyEquipmentMgmtBtn =
                (SinkButton) findViewById(R.id.home_verify_equipment_management_btn);
        mSystemSettingBtn = (SinkButton) findViewById(R.id.home_system_setting_btn);
        mAboutBtn = (SinkButton) findViewById(R.id.home_about_btn);

        mVerifyEngineeringBtn.setOnClickListener(mOnClickListener);
        mVerifyEquipmentMgmtBtn.setOnClickListener(mOnClickListener);
        mSystemSettingBtn.setOnClickListener(mOnClickListener);
        mAboutBtn.setOnClickListener(mOnClickListener);
    }

    private void backHomePage() {
        Message message = Message.obtain();
        message.what = SHOW_HOME_PAGE;
        mSwitchHandler.sendMessageDelayed(message, 200);
    }

    private void showFragmentPage(int itemId) {
        hideHomePage();
        setFunctionTitle(itemId);
        setSelectionFragment(itemId);
    }

    private void showHomePage() {
        hideFragments();
        mTopTitleBarActionListener.showHomeTitle();
        mHomePageLayout.setVisibility(View.VISIBLE);
        mLastItemId = Constant.ITEMID_HOME_PAGER;
    }

    private void hideHomePage() {
        mHomePageLayout.setVisibility(View.GONE);
    }

    private void setFunctionTitle(int itemId) {
        mTopTitleBarActionListener.showFunctionTitle(itemId);
    }

    private void initFragment() {
      if (mVerifyProjectFragment == null) {
        mVerifyProjectFragment = new VerifyProjectFragment();
      }
      if (mVerifyDeviceFragment == null) {
        mVerifyDeviceFragment = new VerifyDeviceFragment();
      }
      if (mSystemSettingFragment == null) {
        mSystemSettingFragment = new SystemSettingFragment();
      }
      if (mAboutFragment == null) {
        mAboutFragment = new AboutFragment();
      }
      if (mVerifyDataFragment == null) {
        mVerifyDataFragment = new VerifyDataFragment();
      }
    }

    public void setSelectionFragment(int itemId) {
        mTransaction = mFragMgr.beginTransaction();
        switch (itemId) {
            case Constant.ITEMID_VERFIY_ENGINEERING:
                if (mVerifyProjectFragment == null) {
                    mVerifyProjectFragment = new VerifyProjectFragment();
                }
              addOrShowFragment(mTransaction, mVerifyProjectFragment, TAG_VERIFY_PROJECT);
                linkLastDevice();
              //启动service 补传数据
              Intent intent = new Intent(this, UploadService.class);
              startService(intent);
                break;
            case Constant.ITEMID_VERIFY_EQUIPMENT:
                if (mVerifyDeviceFragment == null) {
                    mVerifyDeviceFragment = new VerifyDeviceFragment();
                }
              addOrShowFragment(mTransaction, mVerifyDeviceFragment, TAG_VERIFY_DEVICE);
                break;
            case Constant.ITEMID_SYSTEM_SETTING:
                if (mSystemSettingFragment == null) {
                    mSystemSettingFragment = new SystemSettingFragment();
                }
              addOrShowFragment(mTransaction, mSystemSettingFragment, TAG_SETTING);
                break;
            case Constant.ITEMID_ABOUT:
                if (mAboutFragment == null) {
                    mAboutFragment = new AboutFragment();
                }
              addOrShowFragment(mTransaction, mAboutFragment, TAG_ABOUT);
                break;
            case Constant.ITEMID_HOME_PAGER:
                showHomePage();
                break;
            case Constant.ITEMID_VERFIY_DATA:
                //if (mVerifyDataFragment == null) {
                //    mVerifyDataFragment = new VerifyDataFragment();
                //}
                //addOrShowFragment(mTransaction,
                //        mVerifyDataFragment);
                break;
            default:
                break;
        }
        mTransaction.commitAllowingStateLoss();
    }

    private void hideFragments() {
        mTransaction = mFragMgr.beginTransaction();
        List<Fragment> fragments = mFragMgr.getFragments();
        if (CollectionUtils.isEmpty(fragments)) {
            return;
        }
        for (Fragment fragment : fragments) {
            mTransaction.hide(fragment);
        }
        mTransaction.commitAllowingStateLoss();
    }

    public void showDataFragment(String reportNo, String verifyId) {
        mTransaction = mFragMgr.beginTransaction();
        if (mVerifyDataFragment == null) {
            mVerifyDataFragment = new VerifyDataFragment();
        }
        mVerifyDataFragment.setReportNo(reportNo, verifyId);
      addOrShowFragment(mTransaction, mVerifyDataFragment, TAG_VERIFY_DATA);
        mTransaction.commitAllowingStateLoss();
    }

    /**
     * 添加或者显示碎片
     */
    private void addOrShowFragment(FragmentTransaction transaction, Fragment fragment,
            String tagName) {
        if (mCurrentFragment == null) {
            if (!fragment.isAdded()) {
              mFragMgr.beginTransaction()
                      .add(R.id.main_container_layout, fragment, tagName)
                      .commitAllowingStateLoss();
                mCurrentFragment = fragment;
            }
        }

        if (mCurrentFragment == fragment) {
            if (mCurrentFragment.isHidden()) {
                transaction.show(mCurrentFragment);
            }
            return;
        }

        if (!fragment.isAdded()) { // 如果当前fragment未被添加，则添加到Fragment管理器中
          transaction.hide(mCurrentFragment).add(R.id.main_container_layout, fragment, tagName);
        } else {
            transaction.hide(mCurrentFragment).show(fragment);
        }
        mCurrentFragment = fragment;
    }

    @Override public void onBackPressed() {
        backHomePage();
    }

    public void onEventMainThread(Event.TimeEvent timeEvent) {
        mTopTitleBarActionListener.syncTime();
    }

    public void onEventMainThread(Event.BatteryEvent event) {
        mTopTitleBarActionListener.updateBatteryInfo(event.getBatteryLevel(), event.isCharging());
    }

    public void onEventMainThread(Event.WifiEvent event) {
        int wifiStrengthlevel = event.getWifiStrengthLevel();
        mTopTitleBarActionListener.updateWifiInfo(wifiStrengthlevel);
    }

    public void onEventMainThread(Event.TelePhoneEvent event) {
        int signalLevel = event.getSignalLevel();
        mTopTitleBarActionListener.updateTelephoneInfo(signalLevel);
    }

    public void onEventMainThread(Event.CmdEvent event) {
        int cmdType = event.getCmdType();
        switch (cmdType) {
            case CmdPackage.TYPE_TIME:
                showToast(R.string.toast_setTime_success);
                break;
            case CmdPackage.TYPE_DEIVCE:
                showToast(R.string.toast_setDevice_success);
                break;
        }
    }

    public void onEventMainThread(Event.DeviceLinkStatus event) {
        if (event.getAction().equals(ConnectAction.ACTION_GATT_CONNECTED)) {
            mTopTitleBarActionListener.updateDeviceStatus(R.string.label_device_linked);
        } else if (event.getAction().equals(ConnectAction.ACTION_GATT_DISCONNECTED)) {
            mTopTitleBarActionListener.updateDeviceStatus(R.string.label_device_unlink);
        }  else if (event.getAction().equals(ConnectAction.ACTION_GATT_CONNECTING)) {
            mTopTitleBarActionListener.updateDeviceStatus(R.string.label_device_linking);
        }
    }

    @Override public boolean onKeyDown(int keyCode, KeyEvent event) {
        // TODO Auto-
        //
        // method stub
        if (keyCode == event.KEYCODE_HOME) {
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    private void getAllDevice() {
        if (mAppModel == null) {
            mAppModel = new AppModel();
        }
        mAppModel.getDeviceList(AppApplication.getMainId())
                .subscribe(new Subscriber<DeviceResult>() {
                    @Override public void onCompleted() {

                    }

                    @Override public void onError(Throwable e) {

                    }

                    @Override public void onNext(DeviceResult deviceResult) {
                        if (deviceResult.isNetworkSuccess()) {
                            AppApplication.getDeivceManager()
                                    .addOrUpdateDevice(deviceResult.getData().getArr());
                        }
                    }
                });
    }
}