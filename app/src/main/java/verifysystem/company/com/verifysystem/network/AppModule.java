/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package verifysystem.company.com.verifysystem.network;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import verifysystem.company.com.verifysystem.AppApplication;
import java.io.IOException;
import java.util.concurrent.TimeUnit;
import okhttp3.CacheControl;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

public class AppModule {

    private static final String DOMAIN = "http://221.0.91.34:801";
    private static final int DEFAULT_TIMEOUT = 25;  //超时时间，单位秒

    private final AppApplication mAppApplication;
    private Retrofit mRetrofit;
    private IHttpApi mIHttpApi;
    private OkHttpClient.Builder mHttpClientBuilder;
    private OkHttpClient mOkHttpClient;

    public AppModule(AppApplication appApplication) {
        this.mAppApplication = appApplication;
        //这里不需要设置缓存
        //File httpCacheDirectory = new File(appApplication.getCacheDir(),  "responses");
        //int cacheSize = 10 * 1024 * 1024; // 10 MiB
        //Cache cache = new Cache(httpCacheDirectory, cacheSize);

        //手动创建一个OkHttpClient并设置超时时间
        mHttpClientBuilder = new OkHttpClient.Builder();
        mHttpClientBuilder.connectTimeout(DEFAULT_TIMEOUT, TimeUnit.SECONDS);
        //-------------------------------------------
        //添加拦截器，切记注意是否有特殊接口会被影响，是否需要额外处理不做拦截
        //-------------------------------------------
        mOkHttpClient = mHttpClientBuilder
                //.addNetworkInterceptor(REWRITE_RESPONSE_INTERCEPTOR)
                //.addInterceptor(LOG_INTERCEPTOR)
                //.cache(cache)
                .retryOnConnectionFailure(false)
                .build();
        mRetrofit = new Retrofit.Builder().client(mOkHttpClient)
                .baseUrl(DOMAIN)
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .build();
    }

    public AppApplication provideAppApplicationContext() {
        return mAppApplication;
    }

    public IHttpApi provideAuthenticationService() {
        if (mIHttpApi == null) {
            mIHttpApi = mRetrofit.create(IHttpApi.class);
        }
        return mIHttpApi;
    }

    //@Provides @Singleton Retrofit provideRetrofit() {
    //    return mRetrofit;
    //}
    //
    //@Provides @Singleton OkHttpClient.Builder provideOkHttpClientBuilder() {
    //    return mHttpClientBuilder;
    //}
    //
    //@Provides @Singleton OkHttpClient provideOkHttpClient() {
    //    return mOkHttpClient;
    //}

    /**
     * 拦截器
     */
    //private static final Interceptor REWRITE_RESPONSE_INTERCEPTOR = new Interceptor() {
    //    @Override public Response intercept(Chain chain) throws IOException {
    //        Request originalRequest = chain.request();
    //        Request.Builder request = originalRequest.newBuilder();
    //        request.addHeader("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8");
    //        if (originalRequest.header("fresh") != null) {
    //            request.cacheControl(CacheControl.FORCE_NETWORK);
    //        }
    //        Response originalResponse = chain.proceed(chain.request());
    //        String cacheControl = originalResponse.header("Cache-Control");
    //
    //        if ((cacheControl == null
    //                || cacheControl.contains("no-store")
    //                || cacheControl.contains("no-cache")
    //                || cacheControl.contains("must-revalidate")
    //                || cacheControl.contains("max-age=0"))
    //                && originalRequest.header("CacheControlMaxAge") != null) {
    //            return originalResponse.newBuilder()
    //                    .header("Cache-Control",
    //                            "public, max-age=" + originalRequest.header("CacheControlMaxAge"))
    //                    .build();
    //        } else {
    //            return originalResponse;
    //        }
    //    }
    //};

    private static boolean isOnline() {
        ConnectivityManager cm = (ConnectivityManager) AppApplication.getAppContext()
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnectedOrConnecting();
    }
}
