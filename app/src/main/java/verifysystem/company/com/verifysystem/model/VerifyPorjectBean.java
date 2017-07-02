package verifysystem.company.com.verifysystem.model;

import android.support.annotation.IntDef;
import lombok.Data;
import lombok.EqualsAndHashCode;
import verifysystem.company.com.verifysystem.AppApplication;

/**
 * Created by zhuj on 2017/5/17 20:43.
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class VerifyPorjectBean {

    public final static int TYPE_INCUBATORS = 0;  //保温箱
    public static final int TYPE_REFRIGERATED_CAR = 1;  //冷藏车
    public static final int TYPE_COLD_STORES = 2;  //冷库

    //public final static int STATUS_NORMAL = 0;  //未验证
    //public final static int STATUS_VERIFING = 1;//验证中
    //public final static int STATUS_VERIFYED = 2;//验证完毕

    /**
     * verifyId : 1
     * verifyName : 验证对象 1
     * verifyType : 1
     * reportNo : 1
     * reportName : 1
     * creatDate : 2017-03-16 14:20:36
     * url : http://
     */

    private String verifyId;
    private String verifyName;
    private String verifyType;
    private String reportNo;
    private String reportName;
    private String creatDate;
    private String url;

    //@DEVICE_STATUS private int status;

    //@IntDef ({
    //    STATUS_NORMAL, STATUS_VERIFING, STATUS_VERIFYED
    //})
    //public @interface DEVICE_STATUS{}

    public int getVerifyTypeInt() {
        try {
            return Integer.parseInt(verifyType);
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }

    public boolean isWorked() {
       return AppApplication.getDeivceManager().mWorkVerifyIdSet.contains(verifyId);
    }

}
