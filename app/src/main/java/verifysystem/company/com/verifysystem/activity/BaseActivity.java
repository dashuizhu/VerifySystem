package verifysystem.company.com.verifysystem.activity;

import android.support.annotation.StringRes;
import android.support.v4.app.FragmentActivity;
import android.widget.Toast;
import verifysystem.company.com.verifysystem.R;
import verifysystem.company.com.verifysystem.ui.MyProgressDialog;

/**
 * Created by zhuj on 2017/5/21 18:33.
 */
public class BaseActivity extends FragmentActivity {

    private Toast mToast;
    private MyProgressDialog mProgressDialog;

    void showToast(String str) {
        if (mToast == null) {
            mToast = Toast.makeText(this, "", Toast.LENGTH_LONG);
        }
        mToast.setText(str);
        mToast.setDuration(Toast.LENGTH_LONG);
        mToast.show();
    }

    void showToast(@StringRes int strRes) {
        showToast(getString(strRes));
    }

    public void showProgress() {
        if (mProgressDialog == null) {
            synchronized (this) {
                if (mProgressDialog == null) {
                    mProgressDialog = new MyProgressDialog(this);
                    mProgressDialog.setContext(R.string.toast_loading);
                }
            }
        }
        if (!mProgressDialog.isShowing()) {
            mProgressDialog.show();
        }
    }

    public void hideProgress() {
        if (mProgressDialog != null) {
            if (mProgressDialog.isShowing()) {
                mProgressDialog.dismiss();
            }
        }
    }

    @Override public void onDestroy() {
        hideProgress();
        super.onDestroy();
    }
}
