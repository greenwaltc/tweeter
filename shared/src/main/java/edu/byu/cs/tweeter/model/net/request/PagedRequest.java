package edu.byu.cs.tweeter.model.net.request;

import edu.byu.cs.tweeter.model.domain.AuthToken;

public abstract class PagedRequest<T> extends AuthenticatedRequest{
    private String userAlias;
    private int limit;
    private T lastItem;

    protected PagedRequest(){}

    public PagedRequest(AuthToken authToken, String userAlias, int limit, T lastItem) {
        super(authToken);
        this.userAlias = userAlias;
        this.limit = limit;
        this.lastItem = lastItem;
    }

    public String getUserAlias() {
        return userAlias;
    }

    public void setUserAlias(String userAlias) {
        this.userAlias = userAlias;
    }

    public int getLimit() {
        return limit;
    }

    public void setLimit(int limit) {
        this.limit = limit;
    }

    public T getLastItem() {
        return lastItem;
    }

    public void setLastItem(T lastItem) {
        this.lastItem = lastItem;
    }
}
