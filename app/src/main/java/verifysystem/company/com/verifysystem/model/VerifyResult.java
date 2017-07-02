package verifysystem.company.com.verifysystem.model;

import java.util.List;

/**
 * Created by zhuj on 2017/5/17 20:45.
 */
public class VerifyResult extends NetworkResult {

    private DataBean data;

    public DataBean getData() {
        return data;
    }

    public void setData(DataBean data) {
        this.data = data;
    }

    public static class DataBean {
        /**
         * verifyId : 1
         * verifyName : 验证对象 1
         * verifyType : 1
         * reportNo : 1
         * reportName : 1
         * creatDate : 2017-03-16 14:20:36
         * url : http://
         */

        private List<VerifyPorjectBean> arr;

        public List<VerifyPorjectBean> getArr() {
            return arr;
        }

        public void setArr(List<VerifyPorjectBean> list) {
            this.arr = list;
        }
    }
}
