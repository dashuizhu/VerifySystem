package verifysystem.company.com.verifysystem.connection.agreement;

import java.util.List;
import verifysystem.company.com.verifysystem.model.DeviceBean;
import verifysystem.company.com.verifysystem.utils.MyByteUtils;
import verifysystem.company.com.verifysystem.utils.MyHexUtils;

/**
 * Created by zhuj on 2017/5/20 14:01.
 */
public class CmdPackage {

    public final static byte TYPE_TIME = 0x03;
    public final static byte TYPE_DEIVCE = 0x02;


    public static byte[] setSnNo(List<DeviceBean> list) {
        byte[] buff = new byte[6+5*list.size()];
        buff[0] = (byte) 0xFC;
        buff[1] = (byte) TYPE_DEIVCE;
        buff[2] = (byte) 0xAA;
        buff[3] = (byte) 0xBB;
        buff[4] = (byte) list.size();
        DeviceBean deviceBean;
        byte[] buffId;
        //// TODO: 2017/5/21 这里暂时少设置一个
        for (int i=0; i< list.size(); i++) {
            deviceBean = list.get(i);
            buff[5 + i*5 ] = (byte) Integer.parseInt(deviceBean.getNo());
            buffId = MyHexUtils.hexStringToByte(deviceBean.getSnNo());
            System.arraycopy(buffId, 0 , buff, 5+i*5+1, buffId.length);
        }
        return buff;
    }

    /**
     * 设置采集时间
     * @param time 单位分钟
     * @return
     */
    public static byte[] setCollectTime(int time) {
        byte[] buff = new byte[5];
        buff[0] = (byte) 0xFC;
        buff[1] = (byte) TYPE_TIME;
        buff[2] = (byte) (time * 60 /256);
        buff[3] = (byte) (time * 60 %256);
        buff[4] = (byte) 0xDD;
        return buff;
    }
}
