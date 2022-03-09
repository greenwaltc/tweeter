package edu.byu.cs.tweeter.model.net.response;

import edu.byu.cs.tweeter.model.domain.User;

public class GetUserResponse {
    private boolean success;
    private String message;
    private User user;

    /**
     * Failure constructor
     * @param success
     * @param message
     */
    public GetUserResponse(boolean success, String message) {
        this.success = success;
        this.message = message;
    }

    /**
     * Success constructor
     * @param success
     * @param user
     */
    public GetUserResponse(boolean success, User user) {
        this.success = success;
        this.user = user;
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

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}
