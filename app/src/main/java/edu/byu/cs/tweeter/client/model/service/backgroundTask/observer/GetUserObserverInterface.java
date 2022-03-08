package edu.byu.cs.tweeter.client.model.service.backgroundTask.observer;

import edu.byu.cs.tweeter.model.domain.User;

public interface GetUserObserverInterface extends ServiceObserverInterface {
    void handleSuccess(User user);
}
