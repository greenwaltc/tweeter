package edu.byu.cs.tweeter.client.presenter;

import edu.byu.cs.tweeter.client.cache.Cache;
import edu.byu.cs.tweeter.client.presenter.view.PagedViewInterface;
import edu.byu.cs.tweeter.model.domain.User;

public class FollowersPresenter extends PagedPresenter {

    public FollowersPresenter(PagedViewInterface<User> view) {
        super(view);
    }

    public void getUser(String userAlias) {
        view.displayMessage("Getting user's profile...");
        getUserService().getUser(Cache.getInstance().getCurrUserAuthToken(), userAlias, new GetUserObserver());
    }

    @Override
    protected void getItems(User user) {
        getFollowService().getFollowers(Cache.getInstance().getCurrUserAuthToken(), user, PAGE_SIZE, (User) getLastItem(), new GetFollowerObserver());
    }

    public class GetFollowerObserver extends BaseGetItemsObserver {

        @Override
        protected String getItemsTag() {
            return "followers";
        }
    }
}
