package edu.byu.cs.tweeter.model.net.response;

import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.User;
import edu.byu.cs.tweeter.model.net.request.AuthenticateRequest;

public class AuthenticateResponse extends Response{

    private User user;
    private AuthToken authToken;

    private AuthenticateResponse(){}

    public AuthenticateResponse(User user, AuthToken authToken, boolean success) {
        super(success);
        this.user = user;
        this.authToken = authToken;
    }

    public AuthenticateResponse(boolean success, String message) {
        super(success, message);
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public AuthToken getAuthToken() {
        return authToken;
    }

    public void setAuthToken(AuthToken authToken) {
        this.authToken = authToken;
    }
}
