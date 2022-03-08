package edu.byu.cs.tweeter.client.presenter.view;

import edu.byu.cs.tweeter.model.domain.User;

public interface UserViewInterface extends BaseViewInterface {
    void startMainActivity(User user);
}
