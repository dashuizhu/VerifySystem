package verifysystem.company.com.verifysystem.utils;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.text.TextUtils;
import android.util.Log;
import android.widget.EditText;
import java.io.BufferedReader;
import java.io.InputStreamReader;

/**
 * Created by zhuj on 2017/5/21 13:38.
 */
public class AppUtils {

    public static String getMainId(Context context) {
        String mainId = null;
        if (SharedPreferencesUser.contains(context, SharedPreferencesUser.KEY_MAC)) {
            mainId = (String) SharedPreferencesUser.get(context, SharedPreferencesUser.KEY_MAC,"");
        }
        if (TextUtils.isEmpty(mainId)) {
            mainId = getMacAddress(context).replace(":","");
            SharedPreferencesUser.put(context, SharedPreferencesUser.KEY_MAC, mainId);
        }
        if (TextUtils.isEmpty(mainId)) {
            mainId = getMacAddress();
            SharedPreferencesUser.put(context, SharedPreferencesUser.KEY_MAC, mainId);
        }
        return mainId;
    }

    public static String getMacAddress(Context context) {
        WifiManager wifi = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        WifiInfo info = wifi.getConnectionInfo();
        return info.getMacAddress().toUpperCase();
    }

    public static String getMacAddress() {
        String result = "";
        String Mac = "";
        result = callCmd("busybox ifconfig", "HWaddr");

        if (result == null) {
            return "网络出错，请检查网络";
        }
        if (result.length() > 0 && result.contains("HWaddr")) {
            Mac = result.substring(result.indexOf("HWaddr") + 6, result.length() - 1);
            if (Mac.length() > 1) {
                result = Mac.toLowerCase();
            }
        }
        return result.trim();
    }

    public static String callCmd(String cmd, String filter) {
        String result = "";
        String line = "";
        try {
            Process proc = Runtime.getRuntime().exec(cmd);
            InputStreamReader is = new InputStreamReader(proc.getInputStream());
            BufferedReader br = new BufferedReader(is);

            //执行命令cmd，只取结果中含有filter的这一行
            while ((line = br.readLine()) != null && line.contains(filter) == false) {
                //result += line;
                Log.i("test", "line: " + line);
            }

            result = line;
            Log.i("test", "result: " + result);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    public static String getVersionName(Context context) {
        String version = "";
        PackageManager pm = context.getPackageManager();
        //getPackageName()是你当前类的包名，0代表是获取版本信息
        try {
            PackageInfo packInfo = pm.getPackageInfo(context.getPackageName(), 0);
            version = "v" + packInfo.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return version;
    }

    /**
     * 输入框 光标移动到最后一个文字处
     */
    public static void initSelecton(EditText et) {
        if (et != null) {
            String str = et.getText().toString();
            if (str.length() > 0) {
                et.setSelection(str.length());
            }
        }
    }
}
