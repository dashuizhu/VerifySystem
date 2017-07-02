package verifysystem.company.com.verifysystem.model;

import android.content.Context;
import android.text.TextUtils;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import verifysystem.company.com.verifysystem.AppApplication;
import verifysystem.company.com.verifysystem.connection.ICmdParseInterface;
import verifysystem.company.com.verifysystem.connection.IConnectInterface;
import verifysystem.company.com.verifysystem.connection.agreement.CmdParseImpl;
import verifysystem.company.com.verifysystem.utils.MyDateUtils;
import verifysystem.company.com.verifysystem.utils.SharedPreferencesUser;

/**
 * Created by zhuj on 2017/5/19 23:30.
 */
public class DeviceManager {

    private final static String TAG = DeviceManager.class.getSimpleName();

    private Context mContext;

    private List<DeviceBean> mDeviceBeanList = new ArrayList<>();

    public IConnectInterface mConnect;
    public ICmdParseInterface mParse;

    private int mRECORD_type = 0;
    /**
     * 用来记录验证报告 ，是否已经开始
     */
    public  Set<String> mWorkVerifyIdSet = new HashSet<>();

    public void setConnectionInterface(IConnectInterface connectionInterface, Context context) {
        this.mContext = context;
        this.mConnect = connectionInterface;
        if(mParse==null) {
          mParse = new CmdParseImpl(this, context);
        }
        this.mConnect.setCmdParse(mParse);
    }

    public IConnectInterface getConnect() {
        return mConnect;
    }

    public ICmdParseInterface getParse() {
        return mParse;
    }

    public void addOrUpdateDevice(String snNo, double temp, double hum) {
        DeviceBean deviceBean;
        for (int i=0; i<mDeviceBeanList.size(); i++) {
            deviceBean = mDeviceBeanList.get(i);
            if (TextUtils.isEmpty(deviceBean.getSnNo())) {
                continue;
            }
            if (deviceBean.getSnNo().equals(snNo)) {
                deviceBean.getRecordBean().setHumidity((float) hum);
                deviceBean.getRecordBean().setTemperature((float) temp);
                deviceBean.getRecordBean().setDate(MyDateUtils.getNow());
                deviceBean.getRecordBean().setStyle(getRecordType());
                return;
            }
        }
        deviceBean = new DeviceBean();
        deviceBean.setSnNo(snNo);
        deviceBean.getRecordBean().setHumidity((float) hum);
        deviceBean.getRecordBean().setTemperature((float) temp);
        deviceBean.getRecordBean().setDate(MyDateUtils.getNow());
        deviceBean.getRecordBean().setStyle(getRecordType());
        mDeviceBeanList.add(deviceBean);
    }

    public void addOrUpdateDevice(List<DeviceBean> list) {
        mDeviceBeanList.clear();
        mDeviceBeanList.addAll(list);
    }

    public List<DeviceBean> getDeviceBeanList() {
        return mDeviceBeanList;
    }

    private int getRecordType() {
        if (mRECORD_type == 0) {
            mRECORD_type = (int) SharedPreferencesUser.get(AppApplication.getAppContext(), SharedPreferencesUser.KEY_RECORD_TYPE, 1);
        }
        return mRECORD_type;
    }

    public void setRecordType(int recordType) {
        mRECORD_type = recordType;
    }

    /**
     * 更新内容
     */
    public void updateDevice(DeviceBean dBin, String reportNo) {
        DeviceBean deviceBean;
        for (int i=0; i<mDeviceBeanList.size(); i++) {
            deviceBean = mDeviceBeanList.get(i);
            if (deviceBean.getSnNo().equals(dBin.getSnNo())) {
                deviceBean.setReportNo(reportNo);
                return;
            }
        }
        dBin.setReportNo(reportNo);
        dBin.getRecordBean().setSnNo(dBin.getSnNo());
        mDeviceBeanList.add(dBin);
    }

}
