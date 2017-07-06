package verifysystem.company.com.verifysystem.network;

import com.google.gson.Gson;
import java.util.List;
import rx.Observable;
import rx.Subscriber;
import verifysystem.company.com.verifysystem.AppApplication;
import verifysystem.company.com.verifysystem.model.DeviceResult;
import verifysystem.company.com.verifysystem.model.NetworkResult;
import verifysystem.company.com.verifysystem.model.RecordBean;
import verifysystem.company.com.verifysystem.model.VerifyResult;

/**
 * Created by zhuj on 2017/5/17 21:44.
 */
public class AppModel {

    public Observable<DeviceResult> getDeviceList(final String mainId) {
        return Observable.create(new Observable.OnSubscribe<DeviceResult>() {
            @Override public void call(final Subscriber<? super DeviceResult> subscriber) {
                IHttpApi httpApi = AppApplication.getIHttpApi();
                httpApi.getDeviceList(mainId).subscribe(new Subscriber<DeviceResult>() {
                    @Override public void onCompleted() {
                        subscriber.onCompleted();
                    }

                    @Override public void onError(Throwable e) {
                        subscriber.onError(e);
                    }

                    @Override public void onNext(DeviceResult verifyResult) {
                        if (verifyResult.isNetworkSuccess()) {
                            subscriber.onNext(verifyResult);
                        } else {
                            subscriber.onError(new CustomException(verifyResult.getType(),
                                    verifyResult.getMessage()));
                        }
                    }
                });
            }
        });
    }

    public Observable<VerifyResult> getVerifyList(final String mainId) {
        return Observable.create(new Observable.OnSubscribe<VerifyResult>() {
            @Override public void call(final Subscriber<? super VerifyResult> subscriber) {
                IHttpApi httpApi = AppApplication.getIHttpApi();
                httpApi.getVerifyList(mainId).subscribe(new Subscriber<VerifyResult>() {
                    @Override public void onCompleted() {
                        subscriber.onCompleted();
                    }

                    @Override public void onError(Throwable e) {
                        subscriber.onError(e);
                    }

                    @Override public void onNext(VerifyResult verifyResult) {
                        if (verifyResult.isNetworkSuccess()) {
                            subscriber.onNext(verifyResult);
                        } else {
                            subscriber.onError(new CustomException(verifyResult.getType(),
                                    verifyResult.getMessage()));
                        }
                    }
                });
            }
        });
    }

    public Observable<DeviceResult> getVerifyDeviceList(final String verifyId, final String verifyType) {
        return Observable.create(new Observable.OnSubscribe<DeviceResult>() {
            @Override public void call(final Subscriber<? super DeviceResult> subscriber) {
                IHttpApi httpApi = AppApplication.getIHttpApi();
                httpApi.getVerifyDeviceList(verifyId, verifyType).subscribe(new Subscriber<DeviceResult>() {
                    @Override public void onCompleted() {
                        subscriber.onCompleted();
                    }

                    @Override public void onError(Throwable e) {
                        subscriber.onError(e);
                    }

                    @Override public void onNext(DeviceResult verifyResult) {
                        if (verifyResult.isNetworkSuccess()) {
                            subscriber.onNext(verifyResult);
                        } else {
                            subscriber.onError(new CustomException(verifyResult.getType(),
                                    verifyResult.getMessage()));
                        }
                    }
                });
            }
        });
    }

    public Observable<NetworkResult> uploadData(final List<RecordBean> list) {
        IHttpApi httpApi = AppApplication.getIHttpApi();
        Gson gson = new Gson();
        String jsonArray = gson.toJson(list);
        return httpApi.postDeivceRecord(jsonArray);
    }
}
