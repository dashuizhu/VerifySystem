package verifysystem.company.com.verifysystem.fragment;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import butterknife.BindView;
import butterknife.ButterKnife;
import com.xys.libzxing.zxing.encoding.EncodingUtils;
import verifysystem.company.com.verifysystem.AppApplication;
import verifysystem.company.com.verifysystem.R;
import verifysystem.company.com.verifysystem.utils.AppUtils;

/**
 * Created by hasee on 2017/4/15.
 * 关于页面
 */
public class AboutFragment extends Fragment {

    @BindView(R.id.iv_qrcode) ImageView mIvQrcode;
    @BindView(R.id.tv_mainId) TextView mTvMainId;
    @BindView(R.id.tv_version) TextView mTvVersion;

    @Override public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        ViewGroup rootView =
                (ViewGroup) inflater.inflate(R.layout.about_fragment_layout, container, false);
        ButterKnife.bind(this, rootView);
        String mainId = AppApplication.getMainId();
        mTvMainId .setText(mainId);
        Bitmap bm = EncodingUtils.createQRCode(mainId, 200, 200, null);
        mIvQrcode.setImageBitmap(bm);

        mTvVersion.setText(AppUtils.getVersionName(getContext()));
        return rootView;
    }

    @Override public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if (!hidden) {
            if (TextUtils.isEmpty(mTvMainId.getText().toString())) {
                String mainId = AppApplication.getMainId();
                mTvMainId .setText(mainId);
                Bitmap bm = EncodingUtils.createQRCode(mainId, 200, 200, null);
                mIvQrcode.setImageBitmap(bm);
            }
        }
    }
}
