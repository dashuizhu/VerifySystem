package verifysystem.company.com.verifysystem.fragment;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.text.TextUtils;
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
import verifysystem.company.com.verifysystem.ui.MyAlertDialog;

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
     *
     * 如果已经有在采集的报告， 要判断两个报告是否有 重复的  测点
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
                .flatMap(new Func1<DeviceResult, Observable<List<String>>>() {
                    @Override public Observable<List<String>> call(DeviceResult deviceResult) {
                        List<String> conflictList = new ArrayList<>();
                        List<DeviceBean> list = deviceResult.getData().getArr();
                        DeviceBean deviceBean;
                        //遍历服务器返回的设备列表， 更新到本地数据， 并且检查有没有 和已经开启手机工作的报告，有冲突的设备测点
                        for (int i = 0; i < list.size(); i++) {
                            deviceBean = list.get(i);
                            if (TextUtils.isEmpty(deviceBean.getSnNo())) {
                                continue;
                            }
                            if (checkConflict(verifyPorjectBean, deviceBean)) {
                                // TODO: 2017/8/12 标记一下 ，这个是否有必要 ，添加了验证，没开始工作 也能进去，
                                //不添加验证， 就不能进去
                                //有冲突，但是这个当前报告 点击了开始， 就提示冲突
                                //没有点击 开始验证， 就不刷新reportNo就行。
                                //但是这样，就算收到了这个测点的设备， 但是也不会显示出来
                                //if (verifyPorjectBean.isWorked()) {
                                conflictList.add(deviceBean.getSnNo());
                                //}
                                continue;
                            }
                                //刷新本地的设备表， 刷新所属的 reportNo
                                AppApplication.getDeivceManager()
                                        .updateDevice(deviceBean, verifyPorjectBean.getReportNo());
                        }

                        return Observable.just(conflictList);
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<List<String>>() {
                    @Override public void onCompleted() {
                    }

                    @Override public void onError(Throwable e) {
                        hideProgress();
                        if (!TextUtils.isEmpty(e.getMessage())) {
                            showToast(e.getMessage());
                        }
                    }

                    @Override public void onNext(List<String> conflictList) {
                        hideProgress();
                        if (conflictList.size() > 0) {
                            //有冲突
                            showDeviceConflict(conflictList);
                        } else {
                            //没有冲突 ， 直接跳转页面
                            ((MainActivity) getContext()).showDataFragment(
                                    verifyPorjectBean.getReportNo(),
                                    verifyPorjectBean.getVerifyId());
                        }
                    }
                });
    }

    /**
     * 检查设备测点， 所在的验证报告， 是否已经在工作，并且其他报告也有相同的测点。
     *
     * @return false没有冲突   true 已经冲突了
     */
    private boolean checkConflict(VerifyPorjectBean verifyPorjectBean, DeviceBean deviceBean) {
        //开始了验证工作状态
        //if (!verifyPorjectBean.isWorked()) {
        //    return false;
        //}
        //查找缓存里的 设备数据
        DeviceBean dbin = AppApplication.getDeivceManager().getDevcieBeanBySn(deviceBean.getSnNo());
        if (dbin != null && !TextUtils.isEmpty(dbin.getReportNo())) {
            //不是同一个验证报告，才去检查是否冲突
            if (!verifyPorjectBean.getReportNo().equals(dbin.getReportNo())) {
                //节点属于其他报告了， 并且所属报告正在采集工作中
                if (AppApplication.getDeivceManager().isCollectWork(dbin.getReportNo())) {
                    return true;
                }
            }
        }
        return false;
    }

    private void showDeviceConflict(List<String> list) {
        StringBuffer sb = new StringBuffer();
        sb.append(getString(R.string.label_conflict_device));
        for (String sn: list) {
            sb.append(sn);
            sb.append("\t");
        }
        MyAlertDialog dialog = new MyAlertDialog(getContext(), "提示", sb.toString());
        dialog.show();


    }
}
