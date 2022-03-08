package edu.byu.cs.tweeter.model.net.request;

import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.User;

public class FollowRequest {
    AuthToken authToken;
    User user;

    public FollowRequest(AuthToken authToken, User user) {
        this.authToken = authToken;
        this.user = user;
    }
}
