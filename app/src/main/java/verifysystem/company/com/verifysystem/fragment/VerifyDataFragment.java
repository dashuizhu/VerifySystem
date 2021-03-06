package verifysystem.company.com.verifysystem.fragment;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v7.widget.LinearLayoutManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.Spinner;
import butterknife.BindView;
import butterknife.ButterKnife;
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
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import verifysystem.company.com.verifysystem.AppApplication;
import verifysystem.company.com.verifysystem.R;
import verifysystem.company.com.verifysystem.activity.MainActivity;
import verifysystem.company.com.verifysystem.adapter.VerifyDataAdapter;
import verifysystem.company.com.verifysystem.eventbus.Event;
import verifysystem.company.com.verifysystem.global.Constant;
import verifysystem.company.com.verifysystem.model.DeviceBean;
import verifysystem.company.com.verifysystem.model.RecordBean;
import verifysystem.company.com.verifysystem.network.AppModel;
import verifysystem.company.com.verifysystem.services.CollectService;
import verifysystem.company.com.verifysystem.services.UploadService;
import verifysystem.company.com.verifysystem.utils.AppUtils;
import verifysystem.company.com.verifysystem.utils.LogUtils;
import verifysystem.company.com.verifysystem.utils.SharedPreferencesUser;

import static android.content.Context.BIND_AUTO_CREATE;

/**
 * Created by hasee on 2017/5/05.
 * 查看验证数据页面
 */
public class VerifyDataFragment extends BaseFragment
        implements CompoundButton.OnCheckedChangeListener {
  @BindView(R.id.cb_start_collect) CheckBox mCbStartCollect;
  @BindView(R.id.cb_stop_collect) CheckBox mCbStopCollect;
  @BindView(R.id.et_temp_start) EditText mEtTempMin;
  @BindView(R.id.et_temp_end) EditText mEtTempMax;
  @BindView(R.id.et_hum_start) EditText mEtHumMin;
  @BindView(R.id.et_hum_end) EditText mEtHumMax;
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

  private String mReportNo;
  private String mVerifyId;
  //private Set<String> mDeviceSet = new HashSet<>(); //用来记录蓝牙收到的snNo集合
  //显示在列表中
  private Unbinder mUnbinder;

  private long mLastClickId;

  private CollectService mCollectService;

  @Override public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
  }

  @Override public View onCreateView(LayoutInflater inflater, ViewGroup container,
          Bundle savedInstanceState) {
    ViewGroup rootView =
            (ViewGroup) inflater.inflate(R.layout.verify_data_fragment_layout, container, false);
    mUnbinder = ButterKnife.bind(this, rootView);
    mInflater = inflater;
    this.mContext = getActivity();
    initSpinnerData();
    initView(rootView);
    initData();
    initCollectBtnEnable();
    EventBus.getDefault().registerSticky(this);

    //启动service 补传数据
    Intent intent = new Intent(getContext(), UploadService.class);
    getActivity().startService(intent);

    // TODO: 2017/8/13 测试模式
    //testReceiveRecord();
    return rootView;
  }

  private ServiceConnection mServiceConnection = new ServiceConnection() {
    @Override public void onServiceConnected(ComponentName name, IBinder service) {
      Log.d(TAG, name + " onServiceConnected ");
      mCollectService = ((CollectService.CollectBind) service).getService();
    }

    @Override public void onServiceDisconnected(ComponentName name) {
      Log.d(TAG, name + " onServiceDisconnected ");
      mCollectService = null;
      Intent collectService = new Intent(getContext(), CollectService.class);
      getContext().bindService(collectService, mServiceConnection, BIND_AUTO_CREATE);
    }
  };

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

  /**
   * 模拟收到测试数据
   */
  private void testReceiveRecord() {
    Observable.interval(15, TimeUnit.SECONDS).subscribe(new Action1<Long>() {
      @Override public void call(Long aLong) {
        String snNo;
        if (aLong % 6 == 0) {
          snNo = "34003900";
        } else if (aLong % 6 == 1) {
          snNo = "54005400";
        } else if (aLong % 6 == 2) {
          snNo = "54004700";
        } else if (aLong % 6 == 3) {
          snNo = "34003100";
        } else if (aLong % 6 == 4) {
          snNo = "6B004D00";
        } else {
          snNo = "00000000";
        }
        AppApplication.getDeivceManager().addOrUpdateDevice(snNo, 20 + aLong, 20 + aLong);
        addRecordList(snNo);
      }
    });
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
                LogUtils.d(TAG, " device " + deviceBean.getSnNo() + " " + deviceBean.getReportNo());
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
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(new Subscriber<DeviceBean>() {
              @Override public void onCompleted() {
                mVerifyDataAdapter.notifyDataSetChanged();
              }

              @Override public void onError(Throwable e) {

              }

              @Override public void onNext(DeviceBean deviceBean) {
                LogUtils.d(TAG, " device "
                        + deviceBean.getSnNo()
                        + " "
                        + deviceBean.getReportNo()
                        + " "
                        + deviceSnNo);
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
    for (int i = 0; i < allRecordBeanList.size(); i++) {
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
        //loadDataByTwoGroup(itemId);
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

    mTempStartSpinner.setSelection(0, false);
    mTempStopSpinner.setSelection(0, false);
    mHumStopSpinner.setSelection(0, false);
    mHumStartSpinner.setSelection(0, false);
    mTempStartSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
      @Override
      public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        mEtTempMin.setText(String.valueOf(mTempStartSpinner.getSelectedItem()));
        AppUtils.initSelecton(mEtTempMin);
      }

      @Override public void onNothingSelected(AdapterView<?> parent) {

      }
    });
    mTempStopSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
      @Override
      public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        mEtTempMax.setText(String.valueOf(mTempStopSpinner.getSelectedItem()));
        AppUtils.initSelecton(mEtTempMax);
      }

      @Override public void onNothingSelected(AdapterView<?> parent) {

      }
    });
    mHumStartSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
      @Override
      public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        mEtHumMin.setText(String.valueOf(mHumStartSpinner.getSelectedItem()));
        AppUtils.initSelecton(mEtHumMin);
      }

      @Override public void onNothingSelected(AdapterView<?> parent) {

      }
    });
    mHumStopSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
      @Override
      public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        mEtHumMax.setText(String.valueOf(mHumStopSpinner.getSelectedItem()));
        AppUtils.initSelecton(mEtHumMax);
      }

      @Override public void onNothingSelected(AdapterView<?> parent) {

      }
    });

    String min_temp_position =
            (String) SharedPreferencesUser.get(mContext, SharedPreferencesUser.KEY_MIN_TEMP, "10");
    String max_temp_position =
            (String) SharedPreferencesUser.get(mContext, SharedPreferencesUser.KEY_MAX_TEMP, "30");
    String min_hum_position =
            (String) SharedPreferencesUser.get(mContext, SharedPreferencesUser.KEY_MIN_HUM, "20");
    String max_hum_position =
            (String) SharedPreferencesUser.get(mContext, SharedPreferencesUser.KEY_MAX_HUM, "40");

    mEtHumMax.setText(String.valueOf(max_hum_position));
    mEtHumMin.setText(String.valueOf(min_hum_position));
    mEtTempMin.setText(String.valueOf(min_temp_position));
    mEtTempMax.setText(String.valueOf(max_temp_position));

    mCbStartCollect.setOnCheckedChangeListener(this);
    mCbStopCollect.setOnCheckedChangeListener(this);
  }

  private void loadDataByOneGroup(int itemId) {
    if (itemId == mLastClickId) {
      return;
    }
    mLastClickId = itemId;
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
        mCbStopCollect.setChecked(true);
        break;
      case R.id.verify_data_outage_rb:
        AppApplication.getDeivceManager().setRecordType(RecordBean.TYPE_NORMAL);
        SharedPreferencesUser.put(getContext(), SharedPreferencesUser.KEY_RECORD_TYPE,
                RecordBean.TYPE_BLACK_OUT);
        mCbStopCollect.setChecked(true);
        break;
    }
    mVerifyDataAdapter.notifyDataSetChanged();
    //开关门，都要停止所有工作
    AppApplication.getDeivceManager().cleanCollectWork();
  }

  private void initCollectBtnEnable() {
    int status = AppApplication.getDeivceManager().getCollectStatus(mReportNo);
    switch (status) {
      case 0://初始状态
        mCbStartCollect.setChecked(false);
        mCbStopCollect.setChecked(false);
        break;
      case 1://采集工作状态
        mCbStopCollect.setChecked(false);
        mCbStartCollect.setChecked(true);
        break;
      case 2://停止采集
        mCbStartCollect.setChecked(false);
        mCbStopCollect.setChecked(true);
        //不可以点击
        mCbStartCollect.setEnabled(false);
        return;
    }
    if (AppApplication.getDeivceManager().isContains(mReportNo)) {
      mCbStartCollect.setEnabled(true);
    } else {
      mCbStartCollect.setEnabled(false);
    }
  }

  @OnClick({
          R.id.temp_confirm_btn, R.id.hum_confirm_btn
  }) public void onClick(View view) {
    int max;
    int min;
    switch (view.getId()) {
      case R.id.temp_confirm_btn:
        if (TextUtils.isEmpty(mEtTempMax.getText().toString())) {
          showToast(R.string.toast_time_empty_error);
          return;
        }
        if (TextUtils.isEmpty(mEtTempMin.getText().toString())) {
          showToast(R.string.toast_time_empty_error);
          return;
        }
        max = Integer.parseInt(mEtTempMax.getText().toString());
        min = Integer.parseInt(mEtTempMin.getText().toString());
        if (min >= max) {
          showToast(R.string.toast_must_bigger_min);
          return;
        }
        initTempRange();
        break;
      case R.id.hum_confirm_btn:
        if (TextUtils.isEmpty(mEtHumMax.getText().toString())) {
          showToast(R.string.toast_time_empty_error);
          return;
        }
        if (TextUtils.isEmpty(mEtHumMin.getText().toString())) {
          showToast(R.string.toast_time_empty_error);
          return;
        }
        max = Integer.parseInt(mEtHumMax.getText().toString());
        min = Integer.parseInt(mEtHumMin.getText().toString());
        if (min >= max) {
          showToast(R.string.toast_must_bigger_min);
          return;
        }
        initHumRange();
        break;
    }
  }

  @Override public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
    switch (buttonView.getId()) {
      case R.id.cb_start_collect:
        if (buttonView.isChecked()) {
          //最多只能同时验证两份报告
          if (AppApplication.getDeivceManager().getCollectWorkSize() >=2) {
            showToast(R.string.toast_project_collect_size_max);
            buttonView.setChecked(false);
          } else {
            startDelay();
          }
        } else {
          stopDelay();
          AppApplication.getDeivceManager().putCollectStatus(mReportNo, 0);
        }
        break;
      case R.id.cb_stop_collect:
        if (buttonView.isChecked()) {
          mCbStartCollect.setChecked(false);
          mCbStartCollect.setEnabled(false);
          AppApplication.getDeivceManager().putCollectStatus(mReportNo, 2);
        } else {
          AppApplication.getDeivceManager().putCollectStatus(mReportNo, 0);
           if (AppApplication.getDeivceManager().isContains(mReportNo)) {
             mCbStartCollect.setEnabled(true);
           } else {
             mCbStartCollect.setEnabled(false);
            }
        }
        break;
    }
  }

  /**
   * 开始延迟
   */
  private void startDelay() {
    getActivity().getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
    AppApplication.getDeivceManager().putCollectStatus(mReportNo, 1);
    if (mCollectService == null) {
      Intent collectService = new Intent(getContext(), CollectService.class);
      getContext().bindService(collectService, mServiceConnection, BIND_AUTO_CREATE);
    } else {
      mCollectService.startCollect();
    }
  }

  /**
   * 设置报警范围
   */
  private void initTempRange() {
    String min_tem = mEtTempMin.getText().toString();
    String max_tem = mEtTempMax.getText().toString();
    SharedPreferencesUser.put(mContext, SharedPreferencesUser.KEY_MIN_TEMP, min_tem);
    SharedPreferencesUser.put(mContext, SharedPreferencesUser.KEY_MAX_TEMP, max_tem);
    float minTempF = Float.parseFloat(min_tem);
    float maxTempF = Float.parseFloat(max_tem);
    mVerifyDataAdapter.setTempRange(minTempF, maxTempF);
    mVerifyDataAdapter.notifyDataSetChanged();
  }

  private void initHumRange() {
    String min_hum = mEtHumMin.getText().toString();
    String max_hum = mEtHumMax.getText().toString();
    SharedPreferencesUser.put(mContext, SharedPreferencesUser.KEY_MIN_HUM, min_hum);
    SharedPreferencesUser.put(mContext, SharedPreferencesUser.KEY_MAX_HUM, max_hum);
    float minHumF = Float.parseFloat(min_hum);
    float maxHumF = Float.parseFloat(max_hum);
    mVerifyDataAdapter.setHumRange(minHumF, maxHumF);
    mVerifyDataAdapter.notifyDataSetChanged();
  }

  @OnClick(R.id.verify_data_back_btn) public void onClick() {
    ((MainActivity) mContext).setSelectionFragment(Constant.ITEMID_VERFIY_ENGINEERING);
  }

  @Override public void onDestroyView() {
    super.onDestroyView();
    if (mUnbinder != null) {
      mUnbinder.unbind();
    }
  }

  @OnClick(R.id.verify_data_clean) public void onViewClicked() {
    if (allRecordBeanList != null) {
      allRecordBeanList.clear();
      mVerifyDataAdapter.notifyDataSetChanged();
    }
  }

  private void stopDelay() {
    getActivity().getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
    if (!AppApplication.getDeivceManager().isCollectWork()) { //没有在工作的
      if (mServiceConnection != null) {
        getContext().unbindService(mServiceConnection);
      }
    }
  }
}