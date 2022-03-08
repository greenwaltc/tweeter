package edu.byu.cs.tweeter.client.presenter;

import edu.byu.cs.tweeter.client.cache.Cache;
import edu.byu.cs.tweeter.client.model.service.backgroundTask.observer.AuthenticateObserverInterface;
import edu.byu.cs.tweeter.client.presenter.view.AuthenticateViewInterface;
import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.User;

public abstract class AuthenticatePresenter extends BasePresenter{

    protected AuthenticateViewInterface view;

    public AuthenticatePresenter(AuthenticateViewInterface view) {
        this.view = view;
    }

    protected void validateAliasAndPassword(String alias, String password) {
        if (alias.length() == 0) {
            throw new IllegalArgumentException("Alias cannot be empty.");
        }
        if (alias.length() < 2) {
            throw new IllegalArgumentException("Alias must contain 1 or more characters after the @.");
        }
        if (alias.charAt(0) != '@') {
            throw new IllegalArgumentException("Alias must begin with @.");
        }
        if (password.length() == 0) {
            throw new IllegalArgumentException("Password cannot be empty.");
        }
    }

    public abstract class BaseAuthenticateObserver extends BaseObserver implements AuthenticateObserverInterface {

        public BaseAuthenticateObserver() {
            super(view);
        }

        @Override
        public void handleSuccess(User authenticatedUser, AuthToken authToken) {

            Cache.getInstance().setCurrUser(authenticatedUser);
            Cache.getInstance().setCurrUserAuthToken(authToken);

            view.cancelAuthenticateToast();
            view.startMainActivity(authenticatedUser);
            view.displayMessage("Hello " + Cache.getInstance().getCurrUser().getName());
        }

        @Override
        protected String getFailureTag() {
            return getAuthenticateActionTag();
        }

        protected abstract String getAuthenticateActionTag();
    }
}