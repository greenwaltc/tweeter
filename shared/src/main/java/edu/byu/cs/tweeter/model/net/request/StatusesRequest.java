package edu.byu.cs.tweeter.model.net.request;

import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.Status;

public class StatusesRequest extends PagedRequest<Status>{
    private StatusesRequest(){}
    public StatusesRequest(AuthToken authToken, String userAlias, int limit, Status lastStatus) {
        super(authToken, userAlias, limit, lastStatus);
    }
}
