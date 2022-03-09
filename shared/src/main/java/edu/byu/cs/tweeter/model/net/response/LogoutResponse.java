package edu.byu.cs.tweeter.model.net.response;

public class LogoutResponse {
    private boolean success;
    private String message;

    /**
     * Failure constructor
     * @param message
     */
    public LogoutResponse(boolean success, String message) {
        this.message = message;
        this.success = success;
    }

    /**
     * Success constructor
     * @param success
     */
    public LogoutResponse(boolean success) {
        this.success = success;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
