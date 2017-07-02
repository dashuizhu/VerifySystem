package verifysystem.company.com.verifysystem.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.RadioGroup;
import android.widget.Spinner;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnCheckedChanged;
import butterknife.OnClick;
import butterknife.Unbinder;
import com.simplecityapps.recyclerview_fastscroll.views.FastScrollRecyclerView;
import de.greenrobot.event.EventBus;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;
import rx.Observable;
import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.schedulers.Schedulers;
import verifysystem.company.com.verifysystem.AppApplication;
import verifysystem.company.com.verifysystem.R;
import verifysystem.company.com.verifysystem.activity.MainActivity;
import verifysystem.company.com.verifysystem.adapter.VerifyDataAdapter;
import verifysystem.company.com.verifysystem.eventbus.Event;
import verifysystem.company.com.verifysystem.global.Constant;
import verifysystem.company.com.verifysystem.model.DeviceBean;
import verifysystem.company.com.verifysystem.model.NetworkResult;
import verifysystem.company.com.verifysystem.model.RecordBean;
import verifysystem.company.com.verifysystem.network.AppModel;
import verifysystem.company.com.verifysystem.network.CustomException;
import verifysystem.company.com.verifysystem.utils.LogUtils;
import verifysystem.company.com.verifysystem.utils.SharedPreferencesUser;

/**
 * Created by hasee on 2017/5/05.
 * 查看验证数据页面
 */
public class VerifyDataFragment extends BaseFragment {
    @BindView(R.id.cb_start_collect) CheckBox mCbStartCollect;
    @BindView(R.id.cb_stop_collect) CheckBox mCbStopCollect;
    private Context mContext;
    private LayoutInflater mInflater;

    private FastScrollRecyclerView mRecyclerView;
    private RadioGroup mRadioOneGroup;
    private RadioGroup mRadioTwoGroup;
    private Button mTempConfirmBtn;
    private Button mHumConfirmBtn;
    private Button mBackBtn;

    private Spinner mTempStartSpinner;
    private Spinner mTempStopSpinner;
    private Spinner mHumStartSpinner;
    private Spinner mHumStopSpinner;

    private VerifyDataAdapter mVerifyDataAdapter;

    private ArrayAdapter<String> mTempSpinnerAdapter;
    private ArrayAdapter<String> mHumSpinnerAdapter;
    private List<String> mTempSpinnerDataList = new ArrayList<>();
    private List<String> mHumSpinnerDataList = new ArrayList<>();
    private AppModel mAppModel;
    private List<RecordBean> allRecordBeanList = new ArrayList<>();

    private Subscription mDelaySubscription;
    private Subscription mCollectSubscription;
    private String mReportNo;
    private String mVerifyId;
    //private Set<String> mDeviceSet = new HashSet<>(); //用来记录蓝牙收到的snNo集合
    //显示在列表中
    private Unbinder mUnbinder;

    @Override public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        ViewGroup rootView =
                (ViewGroup) inflater.inflate(R.layout.verify_data_fragment_layout, container,
                        false);
        mUnbinder = ButterKnife.bind(this, rootView);
        mInflater = inflater;
        this.mContext = getActivity();
        initSpinnerData();
        initView(rootView);
        initData();
        initCollectBtnEnable();
        EventBus.getDefault().registerSticky(this);
        return rootView;
    }

    @Override public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if (hidden) {
            EventBus.getDefault().unregister(this);
        } else {
            initCollectBtnEnable();
            initRecordList();
            EventBus.getDefault().register(this);
        }
    }

    public void setReportNo(String reportNo, String verifyId) {
        //避免同一个验证报告查看时， 数据清空问题。
        //if (mReportNo!=null && !mReportNo.equals(reportNo)) {
        //    mDeviceSet.clear();
        //}
        this.mReportNo = reportNo;
        this.mVerifyId = verifyId;
    }

    private void initCollectBtnEnable() {
        if (AppApplication.getDeivceManager().mWorkVerifyIdSet.contains(mVerifyId)) {
            mCbStartCollect.setEnabled(true);
        } else {
            mCbStartCollect.setEnabled(false);
        }
    }

    public void onEventMainThread(final Event.RecordEvent recordEvent) {
        if (mCbStopCollect.isChecked()) return;
        //mDeviceSet.add(recordEvent.getSnNo());
        addRecordList(recordEvent.getSnNo());
    }

    /**
     * 遍历已经记录的记录， 做reportNo过滤
     */
    private void initRecordList() {
        final List<RecordBean> recordBeanList = new ArrayList<>();
        Observable.from(AppApplication.getDeivceManager().getDeviceBeanList())
                .subscribe(new Subscriber<DeviceBean>() {
                    @Override public void onCompleted() {
                        allRecordBeanList = recordBeanList;
                        mVerifyDataAdapter.setDataList(allRecordBeanList);
                        mVerifyDataAdapter.notifyDataSetChanged();
                    }

                    @Override public void onError(Throwable e) {

                    }

                    @Override public void onNext(DeviceBean deviceBean) {
                        LogUtils.d(TAG,
                                " device " + deviceBean.getSnNo() + " " + deviceBean.getReportNo());
                        if (TextUtils.isEmpty(deviceBean.getReportNo())) {

                        } else if (deviceBean.getReportNo().equals(mReportNo)) { //验证报告过滤
                            //只显示有数据的
                            if (!TextUtils.isEmpty(deviceBean.getRecordBean().getDate())) {
                                //deviceBean.getRecordBean().setStyle(getRecordStyle());
                                recordBeanList.add(deviceBean.getRecordBean());
                            }
                        }
                    }
                });
    }

    /**
     * 遍历已经记录的记录， 做reportNo过滤
     */
    private void addRecordList(final String deviceSnNo) {
        if (TextUtils.isEmpty(deviceSnNo)) return;
        Observable.from(AppApplication.getDeivceManager().getDeviceBeanList())
                .subscribe(new Subscriber<DeviceBean>() {
                    @Override public void onCompleted() {
                        mVerifyDataAdapter.notifyDataSetChanged();
                    }

                    @Override public void onError(Throwable e) {

                    }

                    @Override public void onNext(DeviceBean deviceBean) {
                        LogUtils.d(TAG,
                                " device " + deviceBean.getSnNo() + " " + deviceBean.getReportNo()+ " "+ deviceSnNo);
                        if (TextUtils.isEmpty(deviceBean.getReportNo())) {

                        } else if (deviceBean.getReportNo().equals(mReportNo)) { //验证报告过滤
                            //只显示有数据的
                            if (deviceSnNo.equals(deviceBean.getSnNo())) {
                                addOrUpdate(deviceBean.getRecordBean());
                                onCompleted();
                            }
                        }
                    }
                });
    }

    private void addOrUpdate(RecordBean recordBean) {
        RecordBean rd;
        for (int i=0; i<allRecordBeanList.size(); i++) {
            rd = allRecordBeanList.get(i);
            if (rd.getSnNo().equals(recordBean.getSnNo())) {
                allRecordBeanList.remove(i);
                allRecordBeanList.add(i, recordBean);
                mVerifyDataAdapter.notifyDataSetChanged();
                return;
            }
        }
        allRecordBeanList.add(recordBean);
        mVerifyDataAdapter.notifyDataSetChanged();

    }

    //private int getRecordStyle() {
    //    int checkId = mRadioOneGroup.getCheckedRadioButtonId();
    //    int type = 0;
    //    switch (checkId) {
    //        case R.id.verifydata_normal_rb:
    //            type = RecordBean.TYPE_NORMAL;
    //            break;
    //        case R.id.verify_data_opendoor_rb:
    //            type = RecordBean.TYPE_OPEN;
    //            break;
    //        case R.id.verify_data_outage_rb:
    //            type = RecordBean.TYPE_BLACK_OUT;
    //            break;
    //    }
    //    return type;
    //}

    private void initData() {
        if (mAppModel == null) {
            mAppModel = new AppModel();
        }
        initTempRange();
        initHumRange();
    }

    private void initSpinnerData() {
        String[] tempData = mContext.getResources().getStringArray(R.array.temperature);
        mTempSpinnerDataList = Arrays.asList(tempData);
        String[] humData = mContext.getResources().getStringArray(R.array.humidity);
        mHumSpinnerDataList = Arrays.asList(humData);
    }

    private void initView(ViewGroup rootView) {
        mRadioOneGroup = (RadioGroup) rootView.findViewById(R.id.verify_data_one_group);
        mRadioOneGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override public void onCheckedChanged(RadioGroup radioGroup, int itemId) {
                loadDataByOneGroup(itemId);
            }
        });

        mRadioTwoGroup = (RadioGroup) rootView.findViewById(R.id.verify_data_two_group);
        mRadioTwoGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override public void onCheckedChanged(RadioGroup radioGroup, int itemId) {
                loadDataByTwoGroup(itemId);
                mVerifyDataAdapter.notifyDataSetChanged();
            }
        });

        mRecyclerView = (FastScrollRecyclerView) rootView.findViewById(R.id.verify_data_recycler);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this.getActivity()));
        mVerifyDataAdapter = new VerifyDataAdapter(this.getActivity(), allRecordBeanList);
        mRecyclerView.setAdapter(mVerifyDataAdapter);

        mBackBtn = (Button) rootView.findViewById(R.id.verify_data_back_btn);
        mTempConfirmBtn = (Button) rootView.findViewById(R.id.temp_confirm_btn);
        mHumConfirmBtn = (Button) rootView.findViewById(R.id.hum_confirm_btn);
        //mBackBtn.setOnClickListener(mOnClickListener);
        //mTempConfirmBtn.setOnClickListener(mOnClickListener);
        //mHumConfirmBtn.setOnClickListener(mOnClickListener);

        mTempStartSpinner = (Spinner) rootView.findViewById(R.id.verify_data_temp_start_spinner);
        mTempStopSpinner = (Spinner) rootView.findViewById(R.id.verify_data_temp_stop_spinner);
        mHumStartSpinner = (Spinner) rootView.findViewById(R.id.verify_data_hum_start_spinner);
        mHumStopSpinner = (Spinner) rootView.findViewById(R.id.verify_data_hum_stop_spinner);
        mTempSpinnerAdapter =
                new ArrayAdapter<String>(mContext, R.layout.spinner_style, mTempSpinnerDataList);
        mHumSpinnerAdapter =
                new ArrayAdapter<String>(mContext, R.layout.spinner_style, mHumSpinnerDataList);
        mTempStartSpinner.setAdapter(mTempSpinnerAdapter);
        mTempStopSpinner.setAdapter(mTempSpinnerAdapter);
        mHumStartSpinner.setAdapter(mHumSpinnerAdapter);
        mHumStopSpinner.setAdapter(mHumSpinnerAdapter);

        int min_temp_position =
                (Integer) SharedPreferencesUser.get(mContext, SharedPreferencesUser.KEY_MIN_TEMP,
                        0);
        int max_temp_position =
                (Integer) SharedPreferencesUser.get(mContext, SharedPreferencesUser.KEY_MAX_TEMP,
                        mTempSpinnerDataList.size() / 3);
        int min_hum_position =
                (Integer) SharedPreferencesUser.get(mContext, SharedPreferencesUser.KEY_MIN_HUM, 0);
        int max_hum_position =
                (Integer) SharedPreferencesUser.get(mContext, SharedPreferencesUser.KEY_MAX_HUM,
                        mHumSpinnerDataList.size() / 3);
        mTempStartSpinner.setSelection(min_temp_position);
        mTempStopSpinner.setSelection(max_temp_position);
        mHumStartSpinner.setSelection(min_hum_position);
        mHumStopSpinner.setSelection(max_hum_position);
    }

    private void loadDataByOneGroup(int itemId) {
        switch (itemId) {
            case R.id.verifydata_normal_rb:
                AppApplication.getDeivceManager().setRecordType(RecordBean.TYPE_NORMAL);
                SharedPreferencesUser.put(getContext(), SharedPreferencesUser.KEY_RECORD_TYPE,
                        RecordBean.TYPE_NORMAL);
                mCbStopCollect.setChecked(true);
                break;
            case R.id.verify_data_opendoor_rb:
                AppApplication.getDeivceManager().setRecordType(RecordBean.TYPE_NORMAL);
                SharedPreferencesUser.put(getContext(), SharedPreferencesUser.KEY_RECORD_TYPE,
                        RecordBean.TYPE_OPEN);
                mCbStopCollect.setChecked(false);
                break;
            case R.id.verify_data_outage_rb:
                AppApplication.getDeivceManager().setRecordType(RecordBean.TYPE_NORMAL);
                SharedPreferencesUser.put(getContext(), SharedPreferencesUser.KEY_RECORD_TYPE,
                        RecordBean.TYPE_BLACK_OUT);
                mCbStopCollect.setChecked(false);
                break;
        }
        mVerifyDataAdapter.notifyDataSetChanged();
    }

    private void loadDataByTwoGroup(int itemId) {
        switch (itemId) {
            case R.id.verify_data_no_load_rb:
                break;
            case R.id.verify_data_full_load_rb:
                break;
            default:
                break;
        }
    }

    @OnClick({
            R.id.temp_confirm_btn, R.id.hum_confirm_btn
    }) public void onClick(View view) {
        switch (view.getId()) {
            case R.id.temp_confirm_btn:
                if (mTempStartSpinner.getSelectedItemPosition()
                        > mTempStopSpinner.getSelectedItemPosition()) {
                    showToast(R.string.toast_must_bigger_min);
                    return;
                }
                initTempRange();
                break;
            case R.id.hum_confirm_btn:
                if (mHumStartSpinner.getSelectedItemPosition()
                        > mHumStopSpinner.getSelectedItemPosition()) {
                    showToast(R.string.toast_must_bigger_min);
                    return;
                }
                initHumRange();
                break;
        }
    }

    @OnCheckedChanged({ R.id.cb_start_collect, R.id.cb_stop_collect })
    public void onCheck(CheckBox view) {
        switch (view.getId()) {
            case R.id.cb_start_collect:
                if (view.isChecked()) {
                    startDelay();
                } else {
                    stopDelay();
                    stopCollect();
                }
                break;
            case R.id.cb_stop_collect:
                if (view.isChecked()) {
                    mCbStartCollect.setChecked(false);
                    mCbStartCollect.setEnabled(false);
                } else {
                    initCollectBtnEnable();
                }
                break;
        }
    }

    /**
     * 开始定时上传
     */
    private void startCollect() {
        int collectTime = (int) SharedPreferencesUser.get(getContext(),
                SharedPreferencesUser.KEY_TIME_COLLECT_MINUTE, 5);
        LogUtils.d(TAG, " 开始Collect 间隔时间分钟 " + collectTime);
        mCollectSubscription = Observable.interval(collectTime, TimeUnit.MINUTES)
                .flatMap(new Func1<Long, Observable<NetworkResult>>() {
                    @Override public Observable<NetworkResult> call(Long aLong) {
                        LogUtils.writeLogToFile(TAG, " startCollect 开始准备上传 上传次数 " + aLong);
                        if (allRecordBeanList.size() > 0) {
                            return mAppModel.uploadData(allRecordBeanList);
                        }
                        return null;
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<NetworkResult>() {
                    @Override public void onCompleted() {

                    }

                    @Override public void onError(Throwable e) {
                        showToast(e.getMessage());
                    }

                    @Override public void onNext(NetworkResult networkResult) {
                        if (networkResult.isNetworkSuccess()) {
                            showToast(R.string.toast_upload_success);
                        } else {
                            onError(new CustomException(networkResult.getType(),
                                    networkResult.getMessage()));
                        }
                    }
                });
    }

    /**
     * 开始延迟
     */
    private void startDelay() {
        getActivity().getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        int delayTime = (int) SharedPreferencesUser.get(getContext(),
                SharedPreferencesUser.KEY_TIME_DELAY_MINUTE, 5);
        LogUtils.d(TAG, " 开始延时任务 时间分钟 " + delayTime);
        mDelaySubscription =
                Observable.timer(delayTime, TimeUnit.MINUTES).subscribe(new Action1<Long>() {
                    @Override public void call(Long aLong) {
                        startCollect();
                    }
                });
    }

    private void stopDelay() {
        getActivity().getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        LogUtils.d(TAG, "停止延时任务");
        if (mDelaySubscription != null) {
            if (!mDelaySubscription.isUnsubscribed()) {
                mDelaySubscription.unsubscribe();
            }
            mDelaySubscription = null;
        }
    }

    private void stopCollect() {
        LogUtils.d(TAG, "停止上传任务");
        if (mCollectSubscription != null) {
            if (!mCollectSubscription.isUnsubscribed()) {
                mCollectSubscription.unsubscribe();
            }
            mCollectSubscription = null;
        }
    }

    /**
     * 设置报警范围
     */
    private void initTempRange() {
        SharedPreferencesUser.put(mContext, SharedPreferencesUser.KEY_MIN_TEMP,
                mTempStartSpinner.getSelectedItemPosition());
        SharedPreferencesUser.put(mContext, SharedPreferencesUser.KEY_MAX_TEMP,
                mTempStopSpinner.getSelectedItemPosition());
        float minTemp =
                Float.parseFloat(mTempStartSpinner.getSelectedItem().toString().replace("℃", ""));
        float maxTemp =
                Float.parseFloat(mTempStopSpinner.getSelectedItem().toString().replace("℃", ""));
        mVerifyDataAdapter.setTempRange(minTemp, maxTemp);
        mVerifyDataAdapter.notifyDataSetChanged();
    }

    private void initHumRange() {
        SharedPreferencesUser.put(mContext, SharedPreferencesUser.KEY_MIN_HUM,
                mHumStartSpinner.getSelectedItemPosition());
        SharedPreferencesUser.put(mContext, SharedPreferencesUser.KEY_MAX_HUM,
                mHumStopSpinner.getSelectedItemPosition());
        float minHum =
                Float.parseFloat(mHumStartSpinner.getSelectedItem().toString().replace("%", ""));
        float maxHum =
                Float.parseFloat(mHumStopSpinner.getSelectedItem().toString().replace("%", ""));
        mVerifyDataAdapter.setHumRange(minHum, maxHum);
        mVerifyDataAdapter.notifyDataSetChanged();
    }

    @OnClick(R.id.verify_data_back_btn) public void onClick() {
        ((MainActivity) mContext).setSelectionFragment(Constant.ITEMID_VERFIY_ENGINEERING);
    }

    @Override public void onDestroyView() {
        super.onDestroyView();
        if (mUnbinder!=null) {
            mUnbinder.unbind();
        }
    }

    @OnClick(R.id.verify_data_clean) public void onViewClicked() {
        if ( allRecordBeanList != null) {
            allRecordBeanList.clear();
            mVerifyDataAdapter.notifyDataSetChanged();
        }
    }
}
