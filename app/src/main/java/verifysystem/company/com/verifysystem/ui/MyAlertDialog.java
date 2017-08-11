package verifysystem.company.com.verifysystem.ui;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.TextView;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import verifysystem.company.com.verifysystem.R;

/**
 * 只有确定的窗口
 * @author zhuj 2017/7/25 上午11:03
 */
public class MyAlertDialog extends Dialog {

  @BindView(R.id.tv_tips_title) TextView mTvTipsTitle;
  @BindView(R.id.tv_tips_content) TextView mTvTipsContent;
  @BindView(R.id.tv_tips_confirm) TextView mTvTipsConfirm;
  private String mTipsTitle;//提示框标题
  private String mTipsContent;//提示框内容

  public MyAlertDialog(Context context, String tipsTitle, String tipsContent) {
    super(context, R.style.dialogStyle);
    requestWindowFeature(Window.FEATURE_NO_TITLE);
    mTipsContent = tipsContent;
    mTipsTitle = tipsTitle;
  }

  @Override protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.dialog_alert);
    ButterKnife.bind(this);

    initView();
  }

  private void initView() {
    mTvTipsTitle.setText(mTipsTitle);
    mTvTipsContent.setText(mTipsContent);
  }

  @OnClick({R.id.tv_tips_confirm }) public void onViewClicked(View view) {
    switch (view.getId()) {
      case R.id.tv_tips_confirm:
        mOnOkClickListener.clickConfirm();
        dismiss();
        break;
    }
  }

  public interface OnOkClickListener {
    void clickConfirm();
  }

  private OnOkClickListener mOnOkClickListener;

  public void setOnOkClickListener(OnOkClickListener listener) {
    mOnOkClickListener = listener;
  }
}