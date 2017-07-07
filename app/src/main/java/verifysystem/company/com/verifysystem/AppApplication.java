package verifysystem.company.com.verifysystem;

import android.app.Application;
import android.content.Context;
import android.text.TextUtils;
import com.crashlytics.android.Crashlytics;
import io.fabric.sdk.android.Fabric;
import io.realm.Realm;
import verifysystem.company.com.verifysystem.connection.IConnectInterface;
import verifysystem.company.com.verifysystem.connection.bluetooth.ConnectBluetoothImpl;
import verifysystem.company.com.verifysystem.database.VerifyRealm;
import verifysystem.company.com.verifysystem.model.DeviceManager;
import verifysystem.company.com.verifysystem.network.AppModule;
import verifysystem.company.com.verifysystem.network.IHttpApi;
import com.facebook.drawee.backends.pipeline.Fresco;
import verifysystem.company.com.verifysystem.utils.AppUtils;

/**
 * Created by zhuj on 2017/5/17 21:18.
 */
public class AppApplication extends Application {

    private static AppModule mAppModule;
    private static Context mContext;
    private static String mMainId = "00082267A660" ;//00082267A660 00082267A660
    private static DeviceManager mDeviceManager;
    IConnectInterface mIConnectInterface;

    public static DeviceManager getDeivceManager() {
        return mDeviceManager;
    }

    @Override public void onCreate() {
        super.onCreate();
        Fabric.with(this, new Crashlytics());
        mContext = this;
        Fresco.initialize(this);
        mAppModule = new AppModule(this);
        mIConnectInterface = new ConnectBluetoothImpl(this);
        mDeviceManager = new DeviceManager();
        mDeviceManager.setConnectionInterface(mIConnectInterface, this);

        Realm.init(this);
        VerifyRealm.setDefaultRealmForUser("verifySystem");
    }

    public static IHttpApi getIHttpApi() {
        return mAppModule.provideAuthenticationService();
    }

    public static Context getAppContext() {
        return mContext;
    }

    public static String getMainId() {
        if (TextUtils.isEmpty(mMainId)) {
            synchronized (AppApplication.class) {
                if (TextUtils.isEmpty(mMainId)) {
                    mMainId = AppUtils.getMainId(getAppContext());
                }
            }
        }
        return mMainId;
    }
}
