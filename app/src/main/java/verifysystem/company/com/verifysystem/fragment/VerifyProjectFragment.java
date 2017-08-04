package verifysystem.company.com.verifysystem.fragment;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioGroup;
import butterknife.ButterKnife;
import butterknife.OnClick;
import com.simplecityapps.recyclerview_fastscroll.views.FastScrollRecyclerView;
import java.util.ArrayList;
import java.util.List;
import rx.Observable;
import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;
import rx.schedulers.Schedulers;
import verifysystem.company.com.verifysystem.AppApplication;
import verifysystem.company.com.verifysystem.R;
import verifysystem.company.com.verifysystem.activity.MainActivity;
import verifysystem.company.com.verifysystem.adapter.VerifyProjectAdapter;
import verifysystem.company.com.verifysystem.model.DeviceBean;
import verifysystem.company.com.verifysystem.model.DeviceResult;
import verifysystem.company.com.verifysystem.model.VerifyPorjectBean;
import verifysystem.company.com.verifysystem.model.VerifyResult;
import verifysystem.company.com.verifysystem.network.AppModel;

/**
 * Created by hasee on 2017/4/15.
 * 验证工程页面
 */
public class VerifyProjectFragment extends BaseFragment {

    private FastScrollRecyclerView mRecyclerView;
    private RadioGroup mRadioGroup;

    private VerifyProjectAdapter mVerifyProjectAdapter;
    private VerifyResult mVerifyResult;
    private List<VerifyPorjectBean> mVerifyPorjectBeanList = new ArrayList<>();
    private AppModel mAppModel;

    @Override public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        ViewGroup rootView =
                (ViewGroup) inflater.inflate(R.layout.verify_engineering_fragment_layout, container,
                        false);
        ButterKnife.bind(this, rootView);
        initView(rootView);
        initData();
        return rootView;
    }

    private void initData() {
        mAppModel = new AppModel();
        loadData();
    }

    private void loadData() {
        //if (!AppUtils.isNetworkAvailable(getContext())) {
        //    showToast(R.string.toast_network_error);
        //    return;
        //}
        showProgress();
        mAppModel.getVerifyList(AppApplication.getMainId())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<VerifyResult>() {
                    @Override public void onCompleted() {

                    }

                    @Override public void onError(Throwable e) {
                        hideProgress();
                        //if (e instanceof CustomException) {
                            showToast(e.getMessage());
                        //} else {
                        //    showToast(R.string.toast_network_error);
                        //}
                    }

                    @Override public void onNext(VerifyResult verifyResult) {
                        hideProgress();
                        if (verifyResult.getData().getArr().size()>0) {
                            showToast(R.string.toast_sync_success);
                            mVerifyResult = verifyResult;
                            mVerifyProjectAdapter.setDataList(verifyResult.getData().getArr());
                            mVerifyProjectAdapter.notifyDataSetChanged();
                        } else {
                            showToast(R.string.toast_sync_empty);
                        }
                    }
                });
    }

    @Override public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if (!hidden) {
            if (mVerifyResult == null) {
                loadData();
            }
        }
    }

    private void initView(ViewGroup rootView) {
        mRadioGroup = (RadioGroup) rootView.findViewById(R.id.verify_report_attribute_group);
        mRadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override public void onCheckedChanged(RadioGroup radioGroup, int itemId) {
                loadDataByAttribute(itemId);
                mVerifyProjectAdapter.notifyDataSetChanged();
            }
        });

        mRecyclerView = (FastScrollRecyclerView) rootView.findViewById(R.id.verify_project_recycler);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this.getActivity()));
        mVerifyProjectAdapter =
                new VerifyProjectAdapter(this.getActivity(), mVerifyPorjectBeanList);
        mRecyclerView.setAdapter(mVerifyProjectAdapter);
        mVerifyProjectAdapter.onVerifyProjectClickListener(new VerifyProjectAdapter.VerifyProjectClickListener() {
            @Override public void onVerifyProject(VerifyPorjectBean bean) {
                loadVerifyDeviceList(bean);
            }
        });
    }

    private void loadDataByAttribute(int itemId) {
        if (mVerifyResult == null) return;
        List<VerifyPorjectBean> list = new ArrayList<VerifyPorjectBean>();
        switch (itemId) {
            case R.id.verify_report_attribute_all_rb:
                mVerifyProjectAdapter.setDataList(mVerifyResult.getData().getArr());
                break;
            case R.id.verify_report_attribute_incubators_rb:
                for (VerifyPorjectBean project : mVerifyResult.getData().getArr()) {
                    if (project.getVerifyTypeInt() == VerifyPorjectBean.TYPE_INCUBATORS) {
                        list.add(project);
                    }
                }
                mVerifyProjectAdapter.setDataList(list);
                break;
            case R.id.verify_report_attribute_refrigerated_car_rb:
                for (VerifyPorjectBean project : mVerifyResult.getData().getArr()) {
                    if (project.getVerifyTypeInt() == VerifyPorjectBean.TYPE_REFRIGERATED_CAR) {
                        list.add(project);
                    }
                }
                mVerifyProjectAdapter.setDataList(list);
                break;
            case R.id.verify_report_attribute_cold_stores_rb:
                for (VerifyPorjectBean project : mVerifyResult.getData().getArr()) {
                    if (project.getVerifyTypeInt() == VerifyPorjectBean.TYPE_COLD_STORES) {
                        list.add(project);
                    }
                }
                mVerifyProjectAdapter.setDataList(list);
                break;
            default:
                break;
        }
    }

    @OnClick(R.id.btn_sync) public void onClick() {
        loadData();
    }

    private Subscription mVerifyDeviceSubscription;

    /**
     * 获得验证报告的  设备列表，并且跳转页面
     */
    public void loadVerifyDeviceList(final VerifyPorjectBean verifyPorjectBean) {
        if (mAppModel == null) {
            mAppModel = new AppModel();
        }
        if (mVerifyDeviceSubscription != null) {
            if (!mVerifyDeviceSubscription.isUnsubscribed()) {
                mVerifyDeviceSubscription.unsubscribe();
            }
            mVerifyDeviceSubscription = null;
        }
        showProgress();

        mVerifyDeviceSubscription = mAppModel.getVerifyDeviceList(verifyPorjectBean.getVerifyId(), verifyPorjectBean.getVerifyType())
                .flatMap(new Func1<DeviceResult, Observable<DeviceBean>>() {
                    @Override public Observable<DeviceBean> call(DeviceResult deviceResult) {
                        return Observable.from(deviceResult.getData().getArr());
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<DeviceBean>() {
                    @Override public void onCompleted() {
                        hideProgress();
                        ((MainActivity)getContext()).showDataFragment(verifyPorjectBean.getReportNo(),
                                verifyPorjectBean.getVerifyId());
                    }

                    @Override public void onError(Throwable e) {
                        hideProgress();
                        showToast(e.getMessage());
                    }

                    @Override public void onNext(DeviceBean deviceBean) {
                        AppApplication.getDeivceManager()
                                .updateDevice(deviceBean, verifyPorjectBean.getReportNo());
                    }
                });
    }

}
