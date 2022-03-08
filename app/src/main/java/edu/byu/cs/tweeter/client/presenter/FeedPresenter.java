package edu.byu.cs.tweeter.client.presenter;

import edu.byu.cs.tweeter.client.cache.Cache;
import edu.byu.cs.tweeter.client.presenter.view.PagedViewInterface;
import edu.byu.cs.tweeter.model.domain.Status;
import edu.byu.cs.tweeter.model.domain.User;

public class FeedPresenter extends PagedPresenter{

    public FeedPresenter(PagedViewInterface<Status> view) {
        super(view);
    }

    @Override
    protected void getItems(User user) {
        getStatusService().getFeed(Cache.getInstance().getCurrUserAuthToken(), user, PAGE_SIZE, (Status) getLastItem(), new GetFeedObserver());
    }

    public class GetFeedObserver extends BaseGetItemsObserver {
        @Override
        protected String getItemsTag() {
            return "feed";
        }
    }
}
