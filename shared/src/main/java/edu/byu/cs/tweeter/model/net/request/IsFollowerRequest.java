package edu.byu.cs.tweeter.model.net.request;

import edu.byu.cs.tweeter.model.domain.AuthToken;

public class IsFollowerRequest extends AuthenticatedRequest{
    private String followerAlias, followeeAlias;

    private IsFollowerRequest(){}

    public IsFollowerRequest(AuthToken authToken, String followerAlias, String followeeAlias) {
        super(authToken);
        this.followerAlias = followerAlias;
        this.followeeAlias = followeeAlias;
    }

    public String getFollowerAlias() {
        return followerAlias;
    }

    public void setFollowerAlias(String followerAlias) {
        this.followerAlias = followerAlias;
    }

    public String getFolloweeAlias() {
        return followeeAlias;
    }

    public void setFolloweeAlias(String followeeAlias) {
        this.followeeAlias = followeeAlias;
    }
}
