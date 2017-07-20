package erp.chain.api;


import erp.chain.constants.Constants;

import java.util.UUID;

/**
 * Created by liuyandong on 2017/3/20.
 */
public class ApiRest {
    private boolean successful;
    private Object data;
    private String message;
    private String error;
    private String result;
    private String requestId;

    public ApiRest() {
        this.requestId = UUID.randomUUID().toString();
    }

    public ApiRest(Exception e) {
        this.requestId = UUID.randomUUID().toString();
        this.error = e.getMessage();
        this.setSuccessful(false);
    }

    public ApiRest(Object data, String message) {
        this.data = data;
        this.message = message;
        this.requestId = UUID.randomUUID().toString();
        this.successful = true;
    }

    public ApiRest(String error) {
        this.error = error;
        this.requestId = UUID.randomUUID().toString();
        this.successful = false;
    }

    public void setSuccessful(boolean successful) {
        this.successful = successful;
        if (successful) {
            this.result = Constants.SUCCESS;
        } else {
            this.result = Constants.FAILURE;
        }
    }

    public boolean isSuccessful() {
        return successful;
    }

    public void setData(Object data) {
        this.data = data;
    }

    public Object getData() {
        return data;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

    public void setError(String error) {
        this.error = error;
    }

    public String getError() {
        return error;
    }

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
        if (Constants.SUCCESS.equals(result)) {
            this.successful = true;
        } else if (Constants.FAILURE.equals(result)) {
            this.successful = false;
        }
    }

    public String getRequestId() {
        return requestId;
    }

    public void setRequestId(String requestId) {
        this.requestId = requestId;
    }
}
