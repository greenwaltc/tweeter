package edu.byu.cs.tweeter.model.net.request;

import edu.byu.cs.tweeter.model.domain.AuthToken;

public class SimpleUserRequest extends AuthenticatedRequest{

    private String targetUserAlias;

    private SimpleUserRequest(){}

    public SimpleUserRequest(AuthToken authToken, String targetUserAlias) {
        super(authToken);
        this.targetUserAlias = targetUserAlias;
    }

    public String getTargetUserAlias() {
        return targetUserAlias;
    }

    public void setTargetUserAlias(String targetUserAlias) {
        this.targetUserAlias = targetUserAlias;
    }
}
