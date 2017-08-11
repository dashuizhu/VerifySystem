package verifysystem.company.com.verifysystem.utils;

import android.content.Context;
import android.support.annotation.StringRes;
import android.widget.Toast;
import verifysystem.company.com.verifysystem.AppApplication;

public class ToastUtils extends Toast {
  private volatile static Toast mToast;

  public ToastUtils(Context context) {
    super(context);
  }

  private static Toast getToast() {
    if (mToast == null) {
      synchronized (ToastUtils.class) {
        mToast = Toast.makeText(AppApplication.getAppContext(), "", Toast.LENGTH_LONG);
      }
    }
    return mToast;
  }

  public static void toast(String message) {
    Toast toast = getToast();
    //View view = LayoutInflater.from(AppApplication.getAppContext()).inflate(R.layout_welcome_page1.toast, null);
    //toast.setView(view);
    //TextView tvMsg = (TextView) view.findViewById(R.videoId.tv_toast_message);
    //tvMsg.setText(message);
    toast.setText(message);
    //toast.setGravity(Gravity.CENTER, 0, 0);
    toast.setDuration(Toast.LENGTH_SHORT);
    toast.show();
  }

  public static void toast(@StringRes int message) {
    toast(AppApplication.getAppContext().getString(message));
  }
}
