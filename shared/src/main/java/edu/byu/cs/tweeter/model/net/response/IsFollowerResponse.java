package edu.byu.cs.tweeter.model.net.response;

public class IsFollowerResponse {
    private boolean success;
    private String message;
    private boolean follower;

    /**
     * Failure constructor.
     * @param success
     * @param message
     */
    public IsFollowerResponse(boolean success, String message) {
        this.success = success;
        this.message = message;
    }

    /**
     * Success constructor.
     * @param success
     * @param follower
     */
    public IsFollowerResponse(boolean success, boolean follower) {
        this.success = success;
        this.follower = follower;
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

    public boolean isFollower() {
        return follower;
    }

    public void setFollower(boolean follower) {
        this.follower = follower;
    }
}
