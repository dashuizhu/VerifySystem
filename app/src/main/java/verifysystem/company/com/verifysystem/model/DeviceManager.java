package verifysystem.company.com.verifysystem.model;

import android.content.Context;
import android.text.TextUtils;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
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
    private  Set<String> mWorkVerifyIdSet = new HashSet<>();
    /**
     * 开始采集工作的
     */
    private  Set<String> mWorkConnectSet = new HashSet<>();
    /**
     * 验证报告的 采集 停止采集状态
     */
    private Map<String, Integer> mCollectStatus = new HashMap<>();

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

    /**
     * 查找设备
     * @param snNo
     * @return
     */
    public DeviceBean getDevcieBeanBySn(String snNo) {
        if (TextUtils.isEmpty(snNo)) return null;
        DeviceBean deviceBean;
        for (int i=0; i<mDeviceBeanList.size(); i++) {
            deviceBean = mDeviceBeanList.get(i);
            if (TextUtils.isEmpty(deviceBean.getSnNo())) {
                continue;
            }
            if (deviceBean.getSnNo().equals(snNo)) {
                return deviceBean;
            }
        }
        return null;
    }

    public boolean isContains(String verifyNo) {
        return mWorkVerifyIdSet.contains(verifyNo);
    }

    public void removeIdSet(String reportno) {
        mWorkVerifyIdSet.remove(reportno);
        //只有在采集工作中， 才停止采集工作
        if (isCollectWork(reportno)) {
            mCollectStatus.put(reportno, 0);
        }
    }

    public void addIdSet(String reportno) {
        mWorkVerifyIdSet.add(reportno);
    }

    /**
     * 是否有在采集工作
     * @param reportNo
     * @return
     */
    public boolean isCollectWork(String reportNo) {
        return getCollectStatus(reportNo) ==1;
    }

    /**
     * 是否有在采集工作
     * @return
     */
    public boolean isCollectWork() {
        return mCollectStatus.containsValue(1);
    }

    /**
     * 纪录采集工作状态
     * @param reportNo
     * @param status
     */
    public void putCollectStatus(String reportNo, int status) {
        mCollectStatus.put(reportNo, status);
    }

    /**
     * 验证报告的工作状态  0普通状态 1开始采集  2停止采集
     * @param reportNo
     * @return
     */
    public int getCollectStatus(String reportNo) {
        if (mCollectStatus.containsKey(reportNo)) {
           return mCollectStatus.get(reportNo);
        }
        return 0;
    }

    /**
     * 停止所有工作
     */
    public void cleanCollectWork() {
        Set<String> keySet =mCollectStatus.keySet();
        Iterator<String> iterable = keySet.iterator();
        while (iterable.hasNext()) {
            String key = iterable.next();
            //如果在工作状态，就停止
            if (isCollectWork(key)) {
                mCollectStatus.put(key, 0);
            }
        }
    }

    /**
     * 获得验证报告 ，同时工作的个数
     * @return
     */
  public int getCollectWorkSize() {
      int workSize =0;
      Set<String> keySet =mCollectStatus.keySet();
      Iterator<String> iterable = keySet.iterator();
      if (iterable.hasNext()) {
          String key = iterable.next();
          //如果在工作状态，就停止
          if (isCollectWork(key)) {
              workSize ++;
          }
      }
    return workSize;
  }
}
