package verifysystem.company.com.verifysystem.ui;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.RadioButton;

/**
 * Created by hasee on 2017/4/16.
 * 有下沉效果的RadioButton
 */
public class SinkRadioButton extends RadioButton{
    private SinkRadioButton mSinkRadioButton;
    private Animator mScaleAnim;
    private Animator mRestoreAnim;
    private Handler mAnimatorHandler = new Handler(Looper.getMainLooper());

    private int mHeight;
    private int mWidth;
    private float mLeft;
    private float mTop;

    public static final float SCALE_RATIO = 0.9f;
    public static final int DURATION = 120;
    public static final int DELAYED_TIME = 180;

    public SinkRadioButton(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public SinkRadioButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        initSinkAnimator();
        mSinkRadioButton = this;
    }

    private void initSinkAnimator() {
        PropertyValuesHolder valueHolder_1 = PropertyValuesHolder.ofFloat(
                "scaleX", 1f, SCALE_RATIO);
        PropertyValuesHolder valuesHolder_2 = PropertyValuesHolder.ofFloat(
                "scaleY", 1f, SCALE_RATIO);
        mScaleAnim = ObjectAnimator.ofPropertyValuesHolder(this, valueHolder_1,
                valuesHolder_2);
        mScaleAnim.setDuration(DURATION);
        PropertyValuesHolder valueHolder_3 = PropertyValuesHolder.ofFloat(
                "scaleX", SCALE_RATIO, 1f);
        PropertyValuesHolder valuesHolder_4 = PropertyValuesHolder.ofFloat(
                "scaleY", SCALE_RATIO, 1f);
        mRestoreAnim = ObjectAnimator.ofPropertyValuesHolder(this,
                valueHolder_3, valuesHolder_4);
        mRestoreAnim.setDuration(DURATION);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mHeight = getHeight() - getPaddingTop() - getPaddingBottom();
        mWidth = getWidth() - getPaddingLeft() - getPaddingRight();
        mLeft = getLeft();
        mTop = getTop();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                mRestoreAnim.end();
                mScaleAnim.start();
                break;
            case MotionEvent.ACTION_MOVE:
                break;
            case MotionEvent.ACTION_UP:
                mScaleAnim.end();
                mRestoreAnim.start();
                boolean innerButton = innerButton(event.getX(), event.getY());
                if (innerButton) {
                    mSinkRadioButton.setChecked(true);
                }else{
                    mSinkRadioButton.setChecked(false);
                }

            case MotionEvent.ACTION_CANCEL:
                mScaleAnim.end();
                mRestoreAnim.start();
                break;
        }
        return true;
    }

    // 按下的点是否在button内
    protected boolean innerButton(float x, float y) {
        // mLeft,mTop是磁块相对于父控件(磁屏)的坐标. x,y是触摸点当对于磁块的坐标
        x = x + mLeft;
        y = y + mTop;
        if ((x >= mLeft) && (x <= mLeft + mWidth)) {
            if (y >= mTop && y <= mTop + mHeight) {
                return true;
            }
        }
        return false;
    }

}
