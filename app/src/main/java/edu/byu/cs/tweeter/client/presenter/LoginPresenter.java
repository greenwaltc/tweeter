package edu.byu.cs.tweeter.client.presenter;

import edu.byu.cs.tweeter.client.presenter.view.AuthenticateViewInterface;

public class LoginPresenter extends AuthenticatePresenter{

    public LoginPresenter(AuthenticateViewInterface view) {
        super(view);
    }

    public void validateLogin(String alias, String password) {
        validateAliasAndPassword(alias, password);
    }

    public void login(String alias, String password) {
        getUserService().login(alias, password, new LoginObserver());
    }

    public class LoginObserver extends BaseAuthenticateObserver {
        @Override
        protected String getAuthenticateActionTag() {
            return "login";
        }
    }
}
