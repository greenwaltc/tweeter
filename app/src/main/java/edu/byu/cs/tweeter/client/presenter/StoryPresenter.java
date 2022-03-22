package edu.byu.cs.tweeter.client.presenter;

import edu.byu.cs.tweeter.client.cache.Cache;
import edu.byu.cs.tweeter.client.presenter.view.PagedViewInterface;
import edu.byu.cs.tweeter.model.domain.Status;
import edu.byu.cs.tweeter.model.domain.User;

public class StoryPresenter extends PagedPresenter{

    private GetStoryObserver getStoryObserver;

    public StoryPresenter(PagedViewInterface<Status> view) {
        super(view);
    }

    public void getUser(String userAlias) {
        view.displayMessage("Getting user's profile...");
        getUserService().getUser(Cache.getInstance().getCurrUserAuthToken(), userAlias, new GetUserObserver());
    }

    @Override
    protected void getItems(User user) {
        getStatusService().getStory(Cache.getInstance().getCurrUserAuthToken(), user, PAGE_SIZE, (Status) getLastItem(), getStoryObserver());
    }

    public GetStoryObserver getStoryObserver() {
        if (getStoryObserver == null) {
            getStoryObserver = new GetStoryObserver();
        }
        return getStoryObserver;
    }

    private class GetStoryObserver extends BaseGetItemsObserver {
        @Override
        protected String getItemsTag() {
            return "story";
        }
    }
}
