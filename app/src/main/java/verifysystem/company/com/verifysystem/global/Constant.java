package verifysystem.company.com.verifysystem.global;

import verifysystem.company.com.verifysystem.R;

/**
 * Created by hasee on 2017/4/21.
 */
public class Constant {

    public static final boolean isDemo = false;

    public static final int ITEMID_VERFIY_ENGINEERING = R.id.home_verify_engineering_btn;
    public static final int ITEMID_VERIFY_EQUIPMENT = R.id.home_verify_equipment_management_btn;
    public static final int ITEMID_SYSTEM_SETTING = R.id.home_system_setting_btn;
    public static final int ITEMID_ABOUT = R.id.home_about_btn;
    public static final int ITEMID_VERFIY_DATA = 1;
    public static final int ITEMID_HOME_PAGER = -1;

    public static final String KEY_PICTURE_URL = "key_picture_url";

    public static final String KEY_REPORT_NO = "report_no";
    /**
     * 在线状态最大的超时时间， 收到数据时间 与当前时间 的最大间隔
     */
    public static final long DATE_TIME_OUT  =  10 *60 *1000;
    /**
     * 连接超时时间,
     */
    public static final long DEVICE_LINK_TIME_OUT = 15;

}
