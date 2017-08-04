package verifysystem.company.com.verifysystem.network;

import retrofit2.http.Field;
import retrofit2.http.Headers;
import verifysystem.company.com.verifysystem.model.DeviceResult;
import verifysystem.company.com.verifysystem.model.NetworkResult;
import verifysystem.company.com.verifysystem.model.VerifyResult;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;
import rx.Observable;

/**
 * 网络协议
 * Created by zhuj on 2017/5/17 20:39.
 */
public interface IHttpApi {

    /**
     * 获取主机验证信息
     */
    @Headers({
            "Connection:close"
    })
    @GET("guidedevice/deviceController/verifyList") Observable<VerifyResult> getVerifyList(
            @Query("mainFrameId") String mainFrameId);


    /**
     * 获取所有设备列表
     */
    @Headers({
            "Connection:close"
    })
    @GET("guidedevice/deviceController/deviceList") Observable<DeviceResult> getDeviceList(
            @Query("mainFrameId") String mainFrameId);

    /**
     * 获取验证对象关联设备列表
     */
    @Headers({
            "Connection:close"
    })
    @GET("guidedevice/deviceController/verifyDeviceList") Observable<DeviceResult> getVerifyDeviceList(
            @Query("verifyId") String verifyId, @Query("verifyType") String verifyType);

    /**
     * 获取验证对象关联设备列表
     * @param content    List<RecordBean> toJsonString
     * @return
     */
    @FormUrlEncoded @POST("guidedevice/deviceController/uploadDeviceData") Observable<NetworkResult> postDeivceRecord(
            @Field("deviceBean") String content);


}
