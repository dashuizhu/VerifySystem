package verifysystem.company.com.verifysystem.network;

import android.text.format.DateUtils;
import com.google.gson.Gson;
import java.util.List;
import org.json.JSONArray;
import org.json.JSONObject;
import rx.Observable;
import rx.Subscriber;
import verifysystem.company.com.verifysystem.AppApplication;
import verifysystem.company.com.verifysystem.model.DeviceResult;
import verifysystem.company.com.verifysystem.model.NetworkResult;
import verifysystem.company.com.verifysystem.model.RecordBean;
import verifysystem.company.com.verifysystem.model.VerifyResult;
import verifysystem.company.com.verifysystem.utils.MyDateUtils;

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

        JSONArray array = new JSONArray();
        JSONObject obj;
        RecordBean bean;
        long time = getDate(list);
        for (int i=0; i<list.size(); i++) {
            bean = list.get(i);
            try {
                obj = new JSONObject();
                obj.put("snNo", bean.getSnNo());
                obj.put("reportNo", bean.getReportNo());
                obj.put("temperature", bean.getTemperature());
                obj.put("humidity", bean.getHumidity());
                obj.put("voltage", bean.getVoltage());
                obj.put("date", MyDateUtils.getTime(time));
                obj.put("style", bean.getStyle());
                array.put(obj);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        Gson gson = new Gson();
        String jsonArray = gson.toJson(array);
        return httpApi.postDeivceRecord(jsonArray);
    }

    private long getDate(List<RecordBean> list) {
        long time = 0L;
        if (list == null || list.size()==0) {
            time = System.currentTimeMillis();
        } else if (list.size() == 1) {
            time = list.get(0).getDateMiss();
        } else if (list.size() == 2) {
            time += list.get(0).getDateMiss();
            time += list.get(1).getDateMiss();
            time = time/2;
        } else {
            time += list.get(0).getDateMiss();
            time += list.get(list.size()-1).getDateMiss();
            time += list.get(list.size()/2).getDateMiss();
            time = time/3;
        }
        return time;
    }


}
