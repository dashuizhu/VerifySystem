package verifysystem.company.com.verifysystem.ui;

import android.app.Dialog;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.StringRes;
import android.widget.TextView;
import butterknife.BindView;
import butterknife.ButterKnife;
import verifysystem.company.com.verifysystem.R;

/**
 * 加载窗口
 * Created by zhuj on 2017/3/29 10:07.
 */
public class MyProgressDialog extends Dialog {

  @BindView(R.id.tv_content) TextView mTvContent;

  public MyProgressDialog(@NonNull Context context) {
    super(context, R.style.dialogStyle);
    setContentView(R.layout.dialog_progress);
    ButterKnife.bind(this);
    setCanceledOnTouchOutside(false);
  }

  public void setContext(String content) {
    mTvContent.setText(content);
  }

  public void setContext(@StringRes int resId) {
    mTvContent.setText(resId);
  }
}
