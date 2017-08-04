package verifysystem.company.com.verifysystem.model;

import android.support.annotation.IntDef;
import android.support.v4.text.TextUtilsCompat;
import android.text.TextUtils;
import android.widget.TextView;
import lombok.Data;
import verifysystem.company.com.verifysystem.global.Constant;
import verifysystem.company.com.verifysystem.utils.MyDateUtils;

/**
 * Created by zhuj on 2017/5/17 21:00.
 */
@Data
public class RecordBean {

    public final static int TYPE_NORMAL =3;
    public final static int TYPE_OPEN = 2;
    public final static int TYPE_BLACK_OUT =1;

    /**
     * snNo : 1
     * reportNo : 2345
     * temperature : 23.5
     * humidity : 32
     * date : 2015-12-24 21:52:31
     * style : 据类型(1：断电； 2：开门； 3：环境)
     */

    private String id;

    private String snNo;
    private String reportNo;
    private float temperature;
    private float humidity;
    private float voltage;//电压
    private String date;
    private String uploadTime;//上传时间
    @RECORD_TYPE private int style;

    @IntDef ({TYPE_NORMAL, TYPE_OPEN, TYPE_BLACK_OUT})
    public @interface RECORD_TYPE{};

    public long getDateMiss() {
        return MyDateUtils.getLong(date);
    }

    public boolean isOnlone() {
        long now = System.currentTimeMillis();
        long timeRece = getDateMiss();
        return (now -timeRece) < Constant.DATE_TIME_OUT;
    }

}
