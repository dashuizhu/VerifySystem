package verifysystem.company.com.verifysystem.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import butterknife.ButterKnife;
import butterknife.OnClick;
import com.simplecityapps.recyclerview_fastscroll.views.FastScrollRecyclerView;
import de.greenrobot.event.EventBus;
import java.util.ArrayList;
import java.util.List;
import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;
import rx.schedulers.Schedulers;
import verifysystem.company.com.verifysystem.AppApplication;
import verifysystem.company.com.verifysystem.R;
import verifysystem.company.com.verifysystem.adapter.VerifyDeviceAdapter;
import verifysystem.company.com.verifysystem.connection.agreement.CmdPackage;
import verifysystem.company.com.verifysystem.eventbus.Event;
import verifysystem.company.com.verifysystem.model.DeviceBean;
import verifysystem.company.com.verifysystem.model.DeviceResult;
import verifysystem.company.com.verifysystem.network.AppModel;
import verifysystem.company.com.verifysystem.utils.LogUtils;
import verifysystem.company.com.verifysystem.utils.MyDateUtils;
import verifysystem.company.com.verifysystem.utils.SharedPreferencesUser;

/**
 * Created by hasee on 2017/4/15.
 * 验证设备管理页面
 */
public class VerifyDeviceFragment extends BaseFragment {
    private Context mContext;
    private LayoutInflater mInflater;

    private FastScrollRecyclerView mRecyclerView;
    private VerifyDeviceAdapter mVerifyDeviceAdapter;
    private List<DeviceBean> mDeviceInfoList = new ArrayList<DeviceBean>();
    private DeviceResult mDeviceResult;
    private AppModel mAppModel;

    @Override public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        ViewGroup rootView =
                (ViewGroup) inflater.inflate(R.layout.verify_device_fragment_layout, container,
                        false);
        mInflater = inflater;
        this.mContext = getActivity();
        ButterKnife.bind(this, rootView);
        initView(rootView);
        initData();
        EventBus.getDefault().registerSticky(this);
        return rootView;
    }

    private void initData() {
        mAppModel = new AppModel();
        loadData();
        //test
        //DeviceBean db;
        //for (int i = 0; i < 15; i++) {
        //    db = new DeviceBean();
        //    db.setSnNo("" + i);
        //    mDeviceInfoList.add(db);
        //}
        //mVerifyDeviceAdapter.notifyDataSetChanged();
    }

    /**
     * 从服务器拉取数据， 然后跟内存中缓存的 recerd， 给到数据
     */
    private void loadData() {
        showProgress();
        mDeviceInfoList.clear();
        final StringBuffer stringBuffer = new StringBuffer();
        mAppModel.getDeviceList(AppApplication.getMainId())
                .flatMap(new Func1<DeviceResult, Observable<List<DeviceBean>>>() {
                    @Override public Observable<List<DeviceBean>> call(DeviceResult deviceResult) {
                        mDeviceResult = deviceResult;
                        return Observable.just(deviceResult.getData().getArr());
                    }
                })
                .flatMap(new Func1<List<DeviceBean>, Observable<DeviceBean>>() {
                    @Override public Observable<DeviceBean> call(List<DeviceBean> list) {
                        return Observable.from(list);
                    }
                })
                .flatMap(new Func1<DeviceBean, Observable<DeviceBean>>() {
                    @Override public Observable<DeviceBean> call(DeviceBean deviceBean) {
                        DeviceBean dBean;
                        //更新recod信息给到列表
                        for (int i = 0;
                                i < AppApplication.getDeivceManager().getDeviceBeanList().size();
                                i++) {
                            dBean = AppApplication.getDeivceManager().getDeviceBeanList().get(i);
                            if (TextUtils.isEmpty(dBean.getSnNo())) continue;
                            if (dBean.getSnNo().equals(deviceBean.getSnNo())) {
                                //将本地记录的record信息给到 列表
                                deviceBean.setRecordBean(dBean.getRecordBean());
                                break;
                            }
                        }
                        return Observable.just(deviceBean);
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<DeviceBean>() {
                    @Override public void onCompleted() {
                        hideProgress();
                        mVerifyDeviceAdapter.notifyDataSetChanged();
                        //// TODO: 2017/5/22 似乎不能发， 已发就设备部上传数据了
                        //if (AppApplication.getDeivceManager().getConnect().isLink()) {
                        //    AppApplication.getDeivceManager().getConnect().write(CmdPackage.setSnNo(mDeviceInfoList));
                        //}
                        String lastDevice = (String) SharedPreferencesUser.get(getContext(),
                                SharedPreferencesUser.KEY_LAST_DEVICE, "");
                        //如果之前没有设置过
                        if (TextUtils.isEmpty(lastDevice) | !lastDevice.equals(
                                stringBuffer.toString())) {
                            if (AppApplication.getDeivceManager().getConnect().isLink()) {
                                AppApplication.getDeivceManager()
                                        .getConnect()
                                        .write(CmdPackage.setSnNo(mDeviceInfoList));
                                SharedPreferencesUser.put(getContext(),
                                        SharedPreferencesUser.KEY_LAST_DEVICE,
                                        stringBuffer.toString());
                            }
                        }
                    }

                    @Override public void onError(Throwable e) {
                        hideProgress();
                        e.printStackTrace();
                    }

                    @Override public void onNext(DeviceBean deviceBean) {
                        showToast(R.string.toast_sync_success);
                        LogUtils.writeLogToFile(TAG,
                                " " + deviceBean.getSnNo() + " 最新时间 " + deviceBean.getRecordBean()
                                        .getDate());
                        mDeviceInfoList.add(deviceBean);
                        stringBuffer.append(deviceBean.getSnNo());
                    }
                });
    }

    @Override public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if (hidden) {
            EventBus.getDefault().unregister(this);
        } else {
            EventBus.getDefault().registerSticky(this);
            if (mDeviceResult == null) {
                loadData();
            } else {
                if (mVerifyDeviceAdapter!=null) {
                    mVerifyDeviceAdapter.notifyDataSetChanged();
                }
            }
        }
    }

    /**
     * 接收到数据时， 更新接收数据时间
     */
    public void onEventMainThread(final Event.RecordEvent recordEvent) {
        DeviceBean dBean;
        //更新recod信息给到列表
        for (int i = 0; i < mDeviceInfoList.size(); i++) {
            dBean = mDeviceInfoList.get(i);
            if (TextUtils.isEmpty(dBean.getSnNo())) {
                continue;
            }
            if (dBean.getSnNo().equals(recordEvent.getSnNo())) {
                dBean.getRecordBean().setDate(MyDateUtils.getNow());
                LogUtils.writeLogToFile(TAG,
                        " ~~~ " + dBean.getSnNo() + " 最新时间 " + dBean.getRecordBean().getDate());
                break;
            }
        }
        mVerifyDeviceAdapter.notifyDataSetChanged();
    }

    /**
     * 更新蓝牙传送上来的数据 时间给到list
     */
    //private void initRecordTime() {
    //    Observable.from(AppApplication.getDeivceManager().getDeviceBeanList())
    //            .subscribe(new Subscriber<DeviceBean>() {
    //                @Override public void onCompleted() {
    //                    mVerifyDeviceAdapter.notifyDataSetChanged();
    //                }
    //
    //                @Override public void onError(Throwable e) {
    //
    //                }
    //
    //                @Override public void onNext(DeviceBean deviceBean) {
    //                    LogUtils.d(TAG, " device "
    //                            + deviceBean.getSnNo()
    //                            + " "
    //                            + deviceBean.getReportNo());
    //                    DeviceBean dBean;
    //                    //更新recod信息给到列表
    //                    for (int i = 0; i < mDeviceInfoList.size(); i++) {
    //                        dBean = mDeviceInfoList.get(i);
    //                        if (dBean.getSnNo().equals(deviceBean.getSnNo())) {
    //                            dBean.setRecordBean(deviceBean.getRecordBean());
    //                            break;
    //                        }
    //                    }
    //                }
    //            });
    //}
    private void initView(ViewGroup rootView) {
        mRecyclerView = (FastScrollRecyclerView) rootView.findViewById(R.id.verify_device_recycler);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this.getActivity()));
        mVerifyDeviceAdapter = new VerifyDeviceAdapter(this.getActivity(), mDeviceInfoList);
        mRecyclerView.setAdapter(mVerifyDeviceAdapter);
    }

    @OnClick(R.id.layout_sync) public void onClick () {
            loadData();
        }
    }
