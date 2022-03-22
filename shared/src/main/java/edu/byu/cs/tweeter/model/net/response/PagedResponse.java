package edu.byu.cs.tweeter.model.net.response;

import java.util.List;
import java.util.Objects;

/**
 * A response that can indicate whether there is more data available from the server.
 */
public abstract class PagedResponse<T> extends Response {

    protected List<T> items;
    private boolean hasMorePages;

    protected PagedResponse(){}

    PagedResponse(List<T> items, boolean success, boolean hasMorePages) {
        super(success);
        this.items = items;
        this.hasMorePages = hasMorePages;
    }

    PagedResponse(boolean success, String message, boolean hasMorePages) {
        super(success, message);
        this.hasMorePages = hasMorePages;
    }

    /**
     * An indicator of whether more data is available from the server. A value of true indicates
     * that the result was limited by a maximum value in the request and an additional request
     * would return additional data.
     *
     * @return true if more data is available; otherwise, false.
     */
    public boolean getHasMorePages() {
        return hasMorePages;
    }

    public List<T> getItems() {
        return items;
    }

    public void setItems(List<T> items) {
        this.items = items;
    }

    @Override
    public boolean equals(Object param) {
        if (this == param) {
            return true;
        }

        if (param == null || getClass() != param.getClass()) {
            return false;
        }

        UsersResponse that = (UsersResponse) param;

        return (Objects.equals(items, that.items) &&
                Objects.equals(this.getMessage(), that.getMessage()) &&
                this.isSuccess() == that.isSuccess());
    }

    @Override
    public int hashCode() {
        return Objects.hash(items);
    }
}
