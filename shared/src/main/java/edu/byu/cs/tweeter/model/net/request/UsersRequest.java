package edu.byu.cs.tweeter.model.net.request;

import edu.byu.cs.tweeter.model.domain.AuthToken;

public class UsersRequest extends PagedRequest<String>{
    private UsersRequest(){}
    public UsersRequest(AuthToken authToken, String followerAlias, int limit, String lastUserAlias) {
        super(authToken, followerAlias, limit, lastUserAlias);
    }
}
