package verifysystem.company.com.verifysystem.network;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * Created by zhuj on 2017/5/19 19:04.
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class CustomException extends RuntimeException {

    private String message;
    private String type;

    public CustomException (String type ,String message) {
        this.message = message;
        this.type = type;
    }

    @Override public String getMessage() {
        if (message != null) {
            return message;
        }
        return super.getMessage();
    }

}
