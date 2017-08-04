package verifysystem.company.com.verifysystem.connection.agreement;

import android.content.Context;
import de.greenrobot.event.EventBus;
import org.json.JSONException;
import org.json.JSONObject;
import verifysystem.company.com.verifysystem.AppApplication;
import verifysystem.company.com.verifysystem.connection.ICmdParseInterface;
import verifysystem.company.com.verifysystem.database.RecordLastDao;
import verifysystem.company.com.verifysystem.eventbus.Event;
import verifysystem.company.com.verifysystem.model.DeviceManager;
import verifysystem.company.com.verifysystem.model.RecordBean;
import verifysystem.company.com.verifysystem.utils.LogUtils;
import verifysystem.company.com.verifysystem.utils.MyDateUtils;
import verifysystem.company.com.verifysystem.utils.MyHexUtils;

/**
 * Created by Administrator on 2016/5/13.
 * 解析数据
 */
public class CmdParseImpl implements ICmdParseInterface {

    private final static String TAG = CmdParseImpl.class.getSimpleName();

    private DeviceManager mDeviceBean;
    private Context mContext;

    public CmdParseImpl(DeviceManager deviceBean, Context context) {
        this.mContext =context;
        this.mDeviceBean = deviceBean;
    }

    @Override
    public void parseData(byte[] dataBuff) {
        if(dataBuff==null) return;
        String strBuffer = MyHexUtils.buffer2String(dataBuff);
        LogUtils.v(TAG, "解析数据:"+strBuffer);
        if(dataBuff.length<2) {
            return;
        }
        switch (dataBuff[0]) {
            case CmdProcess.CMD_HEAD_JSON:
                String jsonStr = new String(dataBuff);
                try {
                    JSONObject object = new JSONObject(jsonStr);
                    String snNo = object.keys().next();
                    JSONObject dataObj = object.getJSONObject(snNo);
                    double tem = dataObj.getDouble("W");
                    double hum = dataObj.getDouble("S");
                    LogUtils.v(TAG, "收到设备"+jsonStr);
                    LogUtils.writeLogToFile(TAG, jsonStr);
                    snNo = snNo.substring(2, snNo.length());
                    //只用来保存最后时间
                    RecordBean rb = new RecordBean();
                    rb.setSnNo(snNo);
                    rb.setDate(MyDateUtils.getNow());
                    RecordLastDao.saveOrUpdate(rb);
                    //------------------------------
                    AppApplication.getDeivceManager().addOrUpdateDevice(snNo, tem, hum);
                    EventBus.getDefault().post(new Event.RecordEvent(snNo, tem, hum));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                break;
            case CmdProcess.CMD_HEAD:
                if (dataBuff[1] == CmdPackage.TYPE_TIME) { //时间设置成功

                } else if (dataBuff[1] == CmdPackage.TYPE_DEIVCE) { //设置设备成功

                }
                EventBus.getDefault().post(new Event.CmdEvent(dataBuff[1]));

                break;
        }


    }

}
