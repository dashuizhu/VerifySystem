package verifysystem.company.com.verifysystem.activity;

import android.app.Activity;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import verifysystem.company.com.verifysystem.global.Constant;
import com.facebook.drawee.view.SimpleDraweeView;
import verifysystem.company.com.verifysystem.R;

public class PictureShowActivity extends Activity {

    @BindView(R.id.draweeView) SimpleDraweeView mDraweeView;

    @Override protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_picture_show);
        ButterKnife.bind(this);
        String url = getIntent().getStringExtra(Constant.KEY_PICTURE_URL);
        if (!TextUtils.isEmpty(url)) {
            if (!url.startsWith("http://")) {
                url = "http://"+url;
            }
            mDraweeView.setImageURI(Uri.parse(url));
        }
    }

    @OnClick(R.id.draweeView) public void onClick() {
        finish();
    }

    @Override public void finish() {
        super.finish();
        //修改跳转动画，退出缩小效果
        overridePendingTransition(R.anim.anim_null, R.anim.anim_samll);
    }
}
