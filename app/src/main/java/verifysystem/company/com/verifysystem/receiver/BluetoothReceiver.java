package verifysystem.company.com.verifysystem.receiver;

import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import de.greenrobot.event.EventBus;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import rx.Observable;
import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.schedulers.Schedulers;
import verifysystem.company.com.verifysystem.AppApplication;
import verifysystem.company.com.verifysystem.connection.ConnectAction;
import verifysystem.company.com.verifysystem.connection.agreement.CmdPackage;
import verifysystem.company.com.verifysystem.eventbus.Event;
import verifysystem.company.com.verifysystem.global.Constant;
import verifysystem.company.com.verifysystem.model.DeviceBean;
import verifysystem.company.com.verifysystem.model.DeviceResult;
import verifysystem.company.com.verifysystem.network.AppModel;
import verifysystem.company.com.verifysystem.utils.LogUtils;
import verifysystem.company.com.verifysystem.utils.SharedPreferencesUser;

/**
 * Created by zhuj on 2017/5/19 23:54.
 */
public class BluetoothReceiver extends BroadcastReceiver {
    private static final String TAG = BluetoothReceiver.class.getSimpleName();

    @Override public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        LogUtils.d(TAG, "get ACTION:"+action);
        //Bundle b = intent.getExtras();
        //Object[] lstName = b.keySet().toArray();
        //
        //// 显示所有收到的消息及其细节
        //for (int i = 0; i < lstName.length; i++) {
        //    String keyName = lstName[i].toString();
        //    Log.e("bluetooth", keyName + ">>>" + String.valueOf(b.get(keyName)));
        //}
        BluetoothDevice device = null;
        if (intent.hasExtra(BluetoothDevice.EXTRA_DEVICE)) {
            device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
        }
        // 搜索发现设备时，取得设备的信息；注意，这里有可能重复搜索同一设备
        if (BluetoothDevice.ACTION_FOUND.equals(action)) {
        }
        //状态改变时
        else if (BluetoothDevice.ACTION_BOND_STATE_CHANGED.equals(action)) {
            switch (device.getBondState()) {
                case BluetoothDevice.BOND_BONDING://正在配对
                    Log.d("BlueToothTestActivity", "正在配对......");
                    break;
                case BluetoothDevice.BOND_BONDED://配对结束
                    AppApplication.getDeivceManager().getConnect().connect(device.getAddress(),"");
                    Log.d("BlueToothTestActivity", "完成配对");
                    break;
                case BluetoothDevice.BOND_NONE://取消配对/未配对
                    Log.d("BlueToothTestActivity", "取消配对");
                default:
                    break;
            }
        } else if(BluetoothDevice.ACTION_ACL_CONNECTED.equals(action)) {
            SharedPreferencesUser.put(context, SharedPreferencesUser.KEY_LAST_BLUETOOTH, device.getAddress());
            getDeviceList(context);
            unSubscribeTimeOut();
        } else if (BluetoothDevice.ACTION_ACL_DISCONNECTED.equals(action)) {
            unSubscribeTimeOut();
        } else if (action.equals(ConnectAction.ACTION_GATT_CONNECTING)) {
            startTimeOutRx();
        }
    }

    /**
     * 连接超时
     */
    private Subscription mLinkTimeOutSubscription;
    private AppModel mAppModel;
    private List<DeviceBean> mDeviceBeanList = new ArrayList<>();

    /**
     * 拉取服务器设备列表，设置白名单
     * @param context
     */
    private void  getDeviceList (final Context context) {
        final String lastDevice = (String) SharedPreferencesUser.get(context,
                SharedPreferencesUser.KEY_LAST_DEVICE, "");
        if (!TextUtils.isEmpty(lastDevice)) {
            LogUtils.d(TAG, "已经设置过白名单了");
            return;
        }
        if (mAppModel == null) {
            mAppModel = new AppModel();
        }
        final StringBuffer stringBuffer = new StringBuffer();
        mAppModel.getDeviceList(AppApplication.getMainId())
                .flatMap(new Func1<DeviceResult, Observable<List<DeviceBean>>>() {
                    @Override public Observable<List<DeviceBean>> call(DeviceResult deviceResult) {
                        return Observable.just(deviceResult.getData().getArr());
                    }
                })
                .flatMap(new Func1<List<DeviceBean>, Observable<DeviceBean>>() {
                    @Override public Observable<DeviceBean> call(List<DeviceBean> list) {
                        return Observable.from(list);
                    }
                })
                .flatMap(new Func1<DeviceBean, Observable<DeviceBean>>() {
                    @Override public Observable<DeviceBean> call(DeviceBean deviceBean) {
                        //DeviceBean dBean;
                        //更新recod信息给到列表
                        //for (int i = 0;
                        //        i < AppApplication.getDeivceManager().getDeviceBeanList().size();
                        //        i++) {
                        //    dBean = AppApplication.getDeivceManager().getDeviceBeanList().get(i);
                        //    if (TextUtils.isEmpty(dBean.getSnNo())) continue;
                        //    if (dBean.getSnNo().equals(deviceBean.getSnNo())) {
                        //        //将本地记录的record信息给到 列表
                        //        deviceBean.setRecordBean(dBean.getRecordBean());
                        //        break;
                        //    }
                        //}
                        return Observable.just(deviceBean);
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<DeviceBean>() {
                    @Override public void onCompleted() {
                        //如果之前没有设置过
                        String nowStr = stringBuffer.toString();
                        if (TextUtils.isEmpty(lastDevice) | !lastDevice.equals(
                                nowStr)) {
                            if (AppApplication.getDeivceManager().getConnect().isLink()) {
                                AppApplication.getDeivceManager()
                                        .getConnect()
                                        .write(CmdPackage.setSnNo(mDeviceBeanList));
                                SharedPreferencesUser.put(context,
                                        SharedPreferencesUser.KEY_LAST_DEVICE, nowStr);
                            }
                        }
                    }

                    @Override public void onError(Throwable e) {
                        e.printStackTrace();
                    }

                    @Override public void onNext(DeviceBean deviceBean) {
                        mDeviceBeanList.add(deviceBean);
                        stringBuffer.append(deviceBean.getSnNo());
                    }
                });
    }

    /**
     * 设备连接中，超时判断
     */
    private void startTimeOutRx() {
        unSubscribeTimeOut();
        mLinkTimeOutSubscription = Observable.timer(Constant.DEVICE_LINK_TIME_OUT, TimeUnit.SECONDS).subscribe(
                new Action1<Long>() {
                    @Override public void call(Long aLong) {
                        EventBus.getDefault().post(new Event.DeviceLinkStatus(BluetoothDevice.ACTION_ACL_DISCONNECTED));
                    }
                });
    }

    private void unSubscribeTimeOut() {
        if (mLinkTimeOutSubscription!=null) {
            if (!mLinkTimeOutSubscription.isUnsubscribed()) {
                mLinkTimeOutSubscription.unsubscribe();
            }
            mLinkTimeOutSubscription = null;
        }
    }
}
