package verifysystem.company.com.verifysystem.model;

import lombok.Data;
import verifysystem.company.com.verifysystem.global.Constant;

/**
 * 设备列表
 * Created by zhuj on 2017/5/17 20:56.
 */
@Data
public class DeviceBean {

    /**
     * id : 1
     * no : 1234
     * snNo : 1234567890
     * types : type
     * checkUnit : ss
     * certificateNo : 911
     * createDate : 2017-03-16 14:20:36
     * validDate : 2017-04-19 14:20:36
     */

    //@SerializedName("id")
    private String id;
    private String no;
    private String snNo;
    private String types;
    private String checkUnit;
    private String certificateNo;
    private String createDate;
    private String validDate;

    private long lastRecordTime;

    private RecordBean recordBean = new RecordBean();

    public boolean isOnlone() {
        long now = System.currentTimeMillis();
        long timeRece = recordBean.getDateMiss();
        return (now -timeRece) < Constant.DATE_TIME_OUT;
    }

    public void setSnNo(String snNo) {
        this.snNo = snNo;
        recordBean.setSnNo(snNo);
    }


    public void setReportNo(String reportNo) {
        recordBean.setReportNo(reportNo);
    }

    public String getReportNo() {
        return recordBean.getReportNo();
    }
}
