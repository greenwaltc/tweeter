package edu.byu.cs.tweeter.client.presenter;

import java.util.List;

import edu.byu.cs.tweeter.client.cache.Cache;
import edu.byu.cs.tweeter.client.model.service.backgroundTask.observer.GetUserObserverInterface;
import edu.byu.cs.tweeter.client.model.service.backgroundTask.observer.PagedObserverInterface;
import edu.byu.cs.tweeter.client.presenter.view.PagedViewInterface;
import edu.byu.cs.tweeter.model.domain.User;

public abstract class PagedPresenter<T> extends BasePresenter{

    protected static final int PAGE_SIZE = 10;

    protected PagedViewInterface<T> view;

    protected T lastItem;
    protected boolean hasMorePages;
    protected boolean isLoading = false;

    public PagedPresenter(PagedViewInterface<T> view) {
        this.view = view;
    }

    public boolean hasMorePages() {
        return hasMorePages;
    }

    public void setHasMorePages(boolean hasMorePages) {
        this.hasMorePages = hasMorePages;
    }

    public boolean isLoading() {
        return isLoading;
    }

    public void setLoading(boolean loading) {
        isLoading = loading;
    }

    public T getLastItem() {
        return lastItem;
    }

    public void setLastItem(T lastItem) {
        this.lastItem = lastItem;
    }

    public void loadMoreItems(User user) {
        if (!isLoading) {   // This guard is important for avoiding a race condition in the scrolling code.
            isLoading = true;
            view.setLoadingStatus(true);
            getItems(user);
        }
    }

    protected abstract void getItems(User user);

    public abstract class BaseGetItemsObserver extends BaseObserver implements PagedObserverInterface<T> {
        public BaseGetItemsObserver() {
            super(view);
        }

        @Override
        public void handleSuccess(List<T> items, boolean hasMorePages) {
            endLoadingState();
            lastItem = (items.size() > 0) ? items.get(items.size() - 1) : null;
            setHasMorePages(hasMorePages);
            view.addItems(items);
        }

        @Override
        public void handleFailure(String message) {
            endLoadingState();
            super.handleFailure(message);
        }

        @Override
        public void handleException(Exception ex) {
            endLoadingState();
            super.handleException(ex);
        }

        private void endLoadingState() {
            isLoading = false;
            view.setLoadingStatus(false);
        }

        @Override
        protected String getFailureTag() {
            return "get " + getItemsTag();
        }

        protected abstract String getItemsTag();
    }

    public class GetUserObserver extends BaseObserver implements GetUserObserverInterface {

        public GetUserObserver() {
            super(view);
        }

        @Override
        public void handleSuccess(User user) {
            view.startMainActivity(user);
        }

        @Override
        protected String getFailureTag() {
            return "get user's profile";
        }
    }

    public void getUser(String userAlias) {
        view.displayMessage("Getting user's profile...");
        getUserService().getUser(Cache.getInstance().getCurrUserAuthToken(), userAlias, new GetUserObserver());
    }
}
