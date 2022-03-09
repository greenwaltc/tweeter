package edu.byu.cs.tweeter.client.model.service.backgroundTask.handler;

import android.os.Bundle;

import edu.byu.cs.tweeter.client.model.service.backgroundTask.authenticatedTask.GetUserTask;
import edu.byu.cs.tweeter.client.model.service.backgroundTask.observer.GetUserObserverInterface;
import edu.byu.cs.tweeter.model.domain.User;

/**
 * Message handler (i.e., observer) for GetUserTask.
 */
public class GetUserHandler extends BackgroundTaskHandler<GetUserObserverInterface> {
    public GetUserHandler(GetUserObserverInterface observer) {
        super(observer);
    }

    @Override
    protected void handleSuccess(Bundle data, GetUserObserverInterface observer) {
        User user = (User) data.getSerializable(GetUserTask.USER_KEY);
        observer.handleSuccess(user);
    }
}
