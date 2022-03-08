package edu.byu.cs.tweeter.client.presenter;

import edu.byu.cs.tweeter.client.model.service.FollowService;
import edu.byu.cs.tweeter.client.model.service.StatusService;
import edu.byu.cs.tweeter.client.model.service.UserService;
import edu.byu.cs.tweeter.client.model.service.backgroundTask.observer.ServiceObserverInterface;
import edu.byu.cs.tweeter.client.presenter.view.BaseViewInterface;

public abstract class BasePresenter {
    protected StatusService statusService;
    protected UserService userService;
    protected FollowService followService;

    public StatusService getStatusService() {
        if (statusService == null) {
            statusService = new StatusService();
        }
        return statusService;
    }

    public UserService getUserService() {
        if (userService == null) {
            userService = new UserService();
        }
        return userService;
    }

    public FollowService getFollowService() {
        if (followService == null) {
            followService = new FollowService();
        }
        return followService;
    }

    public abstract class BaseObserver<T extends BaseViewInterface> implements ServiceObserverInterface {

        private T view;

        public BaseObserver(T view) {
            this.view = view;
        }

        @Override
        public void handleFailure(String message) {
            view.displayMessage("Failed to " + getFailureTag() + ": " + message);
        }
        @Override
        public void handleException(Exception ex) {
            view.displayMessage("Failed to " + getFailureTag() + " because of exception: " + ex.getMessage());
        }

        protected abstract String getFailureTag();

    }
}
