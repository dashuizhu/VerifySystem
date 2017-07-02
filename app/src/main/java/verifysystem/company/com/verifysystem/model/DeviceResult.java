package verifysystem.company.com.verifysystem.model;

import java.util.List;

/**
 * Created by zhuj on 2017/5/17 20:45.
 */
public class DeviceResult extends NetworkResult {

    private DataBean data;

    public DataBean getData() {
        return data;
    }

    public void setData(DataBean data) {
        this.data = data;
    }

    public static class DataBean {
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

        private List<DeviceBean> arr;

        public List<DeviceBean> getArr() {
            return arr;
        }

        public void setArr(List<DeviceBean> list) {
            this.arr = list;
        }
    }
}
