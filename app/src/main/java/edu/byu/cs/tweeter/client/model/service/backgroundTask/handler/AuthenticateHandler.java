package edu.byu.cs.tweeter.client.model.service.backgroundTask.handler;

import android.os.Bundle;

import edu.byu.cs.tweeter.client.model.service.backgroundTask.authenticateTask.RegisterTask;
import edu.byu.cs.tweeter.client.model.service.backgroundTask.observer.AuthenticateObserverInterface;
import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.User;

public class AuthenticateHandler extends BackgroundTaskHandler<AuthenticateObserverInterface> {

    public AuthenticateHandler(AuthenticateObserverInterface observer) {
        super(observer);
    }

    @Override
    protected void handleSuccess(Bundle data, AuthenticateObserverInterface observer) {
        User registeredUser = (User) data.getSerializable(RegisterTask.USER_KEY);
        AuthToken authToken = (AuthToken) data.getSerializable(RegisterTask.AUTH_TOKEN_KEY);
        observer.handleSuccess(registeredUser, authToken);
    }
}
