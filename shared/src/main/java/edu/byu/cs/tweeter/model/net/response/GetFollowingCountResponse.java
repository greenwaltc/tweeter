package edu.byu.cs.tweeter.model.net.response;

public class GetFollowingCountResponse {
    private boolean success;
    private String message;
    private int count;


    /**
     * Failure constructor.
     * @param success
     * @param message
     */
    public GetFollowingCountResponse(boolean success, String message) {
        this.success = success;
        this.message = message;
    }

    /**
     * Success constructor.
     * @param success
     * @param count
     */
    public GetFollowingCountResponse(boolean success, int count) {
        this.success = success;
        this.count = count;
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

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }
}
