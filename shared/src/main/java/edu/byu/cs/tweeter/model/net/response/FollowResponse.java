package edu.byu.cs.tweeter.model.net.response;

public class FollowResponse {
    String message;
    boolean success;

    public FollowResponse(String message, boolean success) {
        this.message = message;
        this.success = success;
    }

    public FollowResponse(boolean success) {
        this.success = success;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }
}
