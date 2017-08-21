package verifysystem.company.com.verifysystem.services;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import rx.Observable;
import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import verifysystem.company.com.verifysystem.AppApplication;
import verifysystem.company.com.verifysystem.R;
import verifysystem.company.com.verifysystem.database.RecordDao;
import verifysystem.company.com.verifysystem.global.Constant;
import verifysystem.company.com.verifysystem.model.DeviceBean;
import verifysystem.company.com.verifysystem.model.NetworkResult;
import verifysystem.company.com.verifysystem.model.RecordBean;
import verifysystem.company.com.verifysystem.network.AppModel;
import verifysystem.company.com.verifysystem.network.CustomException;
import verifysystem.company.com.verifysystem.utils.LogUtils;
import verifysystem.company.com.verifysystem.utils.MyDateUtils;
import verifysystem.company.com.verifysystem.utils.SharedPreferencesUser;
import verifysystem.company.com.verifysystem.utils.ToastUtils;

/**
 * 采集任务
 *
 * @author zhuj 2017/8/9 上午11:07.
 */
public class CollectService extends Service {

  private final String TAG = "collectService";

  /**
   * 采集任务
   */
  Subscription mSubscription;

  private AppModel mAppModel;

  @Nullable @Override public IBinder onBind(Intent intent) {
    return new CollectBind();
  }

  @Override public void onCreate() {
    super.onCreate();
  }

  @Override public int onStartCommand(Intent intent, int flags, int startId) {
    if (intent != null && intent.hasExtra("stop")) {
      stopCollect();
    } else {
      startCollect();
    }
    return super.onStartCommand(intent, flags, startId);
  }

  /**
   * 开始上传
   */
  public void startCollect() {
    if (mSubscription == null) {
      startCollectWork();
    }
  }

  @Override public void onDestroy() {
    stopCollect();
    super.onDestroy();
  }

  private void stopCollect() {
    LogUtils.e(TAG, " 停止采集工作");
    if (mSubscription != null) {
      if (!mSubscription.isUnsubscribed()) {
        mSubscription.unsubscribe();
      }
      mSubscription = null;
    }
  }

  private void startCollectWork() {
    LogUtils.d(TAG, " 开始任务 ");
    // TODO: 2017/8/13 测试模式修改时间
    mSubscription = Observable.interval(1, TimeUnit.MINUTES).subscribe(new Subscriber<Long>() {
      @Override public void onCompleted() {

      }

      @Override public void onError(Throwable e) {

      }

      @Override public void onNext(Long aLong) {
        int delayTime = (int) SharedPreferencesUser.get(CollectService.this,
                SharedPreferencesUser.KEY_TIME_DELAY_MINUTE, Constant.TIME_DELAY_DEFAULT);
        int collectTime = (int) SharedPreferencesUser.get(CollectService.this,
                SharedPreferencesUser.KEY_TIME_DELAY_MINUTE, Constant.TIME_COLLECT_DEFAULT);

        if (aLong <= delayTime) {
          LogUtils.d(TAG, " is onDelay time" + aLong + " / " + delayTime);
          return;
        }
        if (aLong % 5 == 0) {
          //5次，启动一次service 补传数据
          Intent intent = new Intent(CollectService.this, UploadService.class);
          startService(intent);
        }
        if (!AppApplication.getDeivceManager().isCollectWork()) {
          //没有采集工作了
          stopCollect();
          return;
        }
        if ((aLong - delayTime) % collectTime == 0) {
          //除去延时时间， 达到上传周期
          LogUtils.writeLogToFile(TAG, " startCollect 开始准备上传 上传次数 " + aLong);
          uploadRecord();
        } else {
          LogUtils.d(TAG, " ont onCollect time" + aLong + " / " + delayTime + " " + collectTime);
        }
      }
    });
  }

  /**
   * 开始上传
   */
  private void uploadRecord() {
    final List<RecordBean> allRecordBeanList = new ArrayList<>();

    List<DeviceBean> deviceBeenList = AppApplication.getDeivceManager().getDeviceBeanList();

    DeviceBean bean;
    String strTime = MyDateUtils.getTime(System.currentTimeMillis());
    for (int i = 0; i < deviceBeenList.size(); i++) {
      bean = deviceBeenList.get(i);
      if (bean.getReportNo() == null) continue;
      if (bean.getRecordBean() == null) continue;
      //不在线的不上传
      if (!bean.isOnlone()) continue;
      //对应的验证报告，是否在采集工作中
      if (AppApplication.getDeivceManager().isCollectWork(bean.getReportNo())) {
        bean.getRecordBean().setUploadTime(strTime);
        allRecordBeanList.add(bean.getRecordBean());
      }
    }
    if (mAppModel == null) {
      mAppModel = new AppModel();
    }
    mAppModel.uploadData(allRecordBeanList)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(new Subscriber<NetworkResult>() {
              @Override public void onCompleted() {

              }

              @Override public void onError(Throwable e) {
                e.printStackTrace();
                ToastUtils.toast(e.getMessage());
                RecordDao.saveOrUpdates(allRecordBeanList);
              }

              @Override public void onNext(NetworkResult networkResult) {
                if (networkResult.isNetworkSuccess()) {
                  ToastUtils.toast(R.string.toast_upload_success);
                } else {
                  onError(new CustomException(networkResult.getType(), networkResult.getMessage()));
                }
              }
            });
  }

  public class CollectBind extends Binder {
    public CollectService getService() {
      return CollectService.this;
    }
  }
}
