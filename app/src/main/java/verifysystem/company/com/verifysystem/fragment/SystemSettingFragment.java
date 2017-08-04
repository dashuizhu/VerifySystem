package verifysystem.company.com.verifysystem.fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Toast;
import butterknife.BindView;
import butterknife.ButterKnife;
import de.greenrobot.event.EventBus;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import verifysystem.company.com.verifysystem.AppApplication;
import verifysystem.company.com.verifysystem.R;
import verifysystem.company.com.verifysystem.connection.agreement.CmdPackage;
import verifysystem.company.com.verifysystem.eventbus.Event;
import verifysystem.company.com.verifysystem.global.Constant;
import verifysystem.company.com.verifysystem.utils.SharedPreferencesUser;

/**
 * Created by hasee on 2017/4/15.
 * 系统设置页面
 */
public class SystemSettingFragment extends BaseFragment {
    @BindView(R.id.et_time_collect) EditText mEtTimeCollect;
    @BindView(R.id.et_time_delay) EditText mEtTimeDelay;
    private Context mContext;
    private LayoutInflater mInflater;
    private Button mSystemSettigBtn;
    private Button mCollectSaveBtn;
    private Button mDelayedSaveBtn;
    private Button mStorageSaveBtn;
    private LinearLayout mSynTimeLy;
    private ArrayAdapter<String> mSpinnerAdapter;
    private Spinner mDataCollectSpinner;
    private Spinner mDataStorageSpinner;
    private Spinner mDataDelayedSpinner;
    private List<String> mDataList = new ArrayList<String>();

    @Override public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        ViewGroup rootView =
                (ViewGroup) inflater.inflate(R.layout.system_setting_fragment_layout, container,
                        false);
        ButterKnife.bind(this, rootView);
        mInflater = inflater;
        this.mContext = getActivity();
        initSpinnerData();
        initView(rootView);
        return rootView;
    }

    private void initSpinnerData() {
        String[] data = mContext.getResources().getStringArray(R.array.minutes);
        mDataList = Arrays.asList(data);
    }

    private void initView(ViewGroup rootView) {
        mSynTimeLy = (LinearLayout) rootView.findViewById(R.id.syn_time_ly);
        mSynTimeLy.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View view) {
                EventBus.getDefault().post(new Event.TimeEvent());
            }
        });

        mSystemSettigBtn = (Button) rootView.findViewById(R.id.system_setting_btn);
        mCollectSaveBtn = (Button) rootView.findViewById(R.id.data_collect_save_btn);
        mStorageSaveBtn = (Button) rootView.findViewById(R.id.data_storage_save_btn);
        mDelayedSaveBtn = (Button) rootView.findViewById(R.id.data_delayed_save_btn);

        mSystemSettigBtn.setOnClickListener(mOnClickListener);
        mCollectSaveBtn.setOnClickListener(mOnClickListener);
        mStorageSaveBtn.setOnClickListener(mOnClickListener);
        mDelayedSaveBtn.setOnClickListener(mOnClickListener);

        //这里没用AppCompatSpinner是因为在设备上点击后无法弹出选项
        mDataCollectSpinner = (Spinner) rootView.findViewById(R.id.collect_spinner);
        mDataStorageSpinner = (Spinner) rootView.findViewById(R.id.storage_spinner);
        mDataDelayedSpinner = (Spinner) rootView.findViewById(R.id.delayed_spinner);
        mSpinnerAdapter = new ArrayAdapter<String>(mContext, R.layout.spinner_style, mDataList);
        mSpinnerAdapter.setDropDownViewResource(R.layout.spinner_item_style);
        mDataCollectSpinner.setAdapter(mSpinnerAdapter);
        mDataStorageSpinner.setAdapter(mSpinnerAdapter);
        mDataDelayedSpinner.setAdapter(mSpinnerAdapter);

        int collectTime = (int) SharedPreferencesUser.get(getContext(),
                SharedPreferencesUser.KEY_TIME_COLLECT_MINUTE, 1);
        int delayTime = (int) SharedPreferencesUser.get(getContext(),
                SharedPreferencesUser.KEY_TIME_DELAY_MINUTE, 5);
        mEtTimeCollect.setText(String.valueOf(collectTime));
        mEtTimeDelay.setText(String.valueOf(delayTime));
        //初始化 不回调
        mDataDelayedSpinner.setSelection(0, false);
        mDataCollectSpinner.setSelection(0, false);
        mDataDelayedSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                mEtTimeDelay.setText(mDataDelayedSpinner.getSelectedItem().toString());
            }

            @Override public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        mDataCollectSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                mEtTimeCollect.setText(mDataCollectSpinner.getSelectedItem().toString());
            }

            @Override public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }

    private View.OnClickListener mOnClickListener = new View.OnClickListener() {
        @Override public void onClick(View v) {
            String str;
            int time;
            int selectPosition;
            switch (v.getId()) {
                case R.id.system_setting_btn:
                    Intent intent = new Intent(Settings.ACTION_SETTINGS);
                    mContext.startActivity(intent);
                    break;
                case R.id.data_collect_save_btn:
                    str =  mEtTimeCollect.getText().toString().replace("分钟","");
                    if (TextUtils.isEmpty(str)) {
                        Toast.makeText(mContext, R.string.toast_time_empty_error, Toast.LENGTH_SHORT)
                                .show();
                        return ;
                    }
                    time = Integer.parseInt(str);
                    if (time < 1) {
                        Toast.makeText(mContext, R.string.toast_time_error, Toast.LENGTH_SHORT)
                                .show();
                        return;
                    }
                    SharedPreferencesUser.put(getContext(),
                            SharedPreferencesUser.KEY_TIME_COLLECT_MINUTE, time);
                    Constant.DATE_TIME_OUT = time * 3 * 60 * 1000 +10000;
                    AppApplication.getDeivceManager()
                            .getConnect()
                            .write(CmdPackage.setCollectTime(time));
                    break;
                case R.id.data_storage_save_btn:
                    Toast.makeText(mContext, "存储数据保存成功!", Toast.LENGTH_SHORT).show();
                    str = mDataStorageSpinner.getSelectedItem().toString().replace("分钟", "");
                    time = Integer.parseInt(str);
                    selectPosition = mDataStorageSpinner.getSelectedItemPosition();
                    SharedPreferencesUser.put(getContext(),
                            SharedPreferencesUser.KEY_TIME_SAVE_POSITION, selectPosition);
                    SharedPreferencesUser.put(getContext(),
                            SharedPreferencesUser.KEY_TIME_SAVE_MINUTE, time);
                    AppApplication.getDeivceManager()
                            .getConnect()
                            .write(CmdPackage.setCollectTime(time));
                    break;
                case R.id.data_delayed_save_btn:
                     str =  mEtTimeDelay.getText().toString().replace("分钟","");
                    if (TextUtils.isEmpty(str)) {
                        Toast.makeText(mContext, R.string.toast_time_empty_error, Toast.LENGTH_SHORT)
                                .show();
                        return ;
                    }
                    time = Integer.parseInt(str);
                    if (time < 1) {
                        Toast.makeText(mContext, R.string.toast_time_error, Toast.LENGTH_SHORT)
                                .show();
                        return;
                    }
                    Toast.makeText(mContext, "延时数据保存成功!", Toast.LENGTH_SHORT).show();
                    SharedPreferencesUser.put(getContext(),
                            SharedPreferencesUser.KEY_TIME_DELAY_MINUTE, time);
                    break;
                default:
                    break;
            }
        }
    };

    @Override public void onDestroyView() {
        super.onDestroyView();
    }
}
