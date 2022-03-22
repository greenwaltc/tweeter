package edu.byu.cs.tweeter.model.net.response;

public class IsFollowerResponse extends Response{
    private boolean follower;

    private IsFollowerResponse(){}

    /**
     * Failure constructor.
     * @param success
     * @param message
     */
    public IsFollowerResponse(boolean success, String message) {
        super(success, message);
    }

    /**
     * Success constructor.
     * @param success
     * @param follower
     */
    public IsFollowerResponse(boolean success, boolean follower) {
        super(success);
        this.follower = follower;
    }

    public boolean isFollower() {
        return follower;
    }

    public void setFollower(boolean follower) {
        this.follower = follower;
    }
}
