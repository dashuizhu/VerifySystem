package verifysystem.company.com.verifysystem.model;

/**
 * Created by zhuj on 2017/5/17 20:42.
 */
public class NetworkResult {

    /**
     * result : Success
     * type : NoDataException
     * message :
     * data : {"array":[]}
     */

    private String result;
    private String type;
    private String message;

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public boolean isNetworkSuccess() {
        if (result!=null && result.toLowerCase().equals("success")) {
            return true;
        }
        return false;
    }
}
