package edu.byu.cs.tweeter.client.model.service;

import edu.byu.cs.tweeter.client.model.service.backgroundTask.BackgroundTaskUtils;
import edu.byu.cs.tweeter.client.model.service.backgroundTask.GetUserTask;
import edu.byu.cs.tweeter.client.model.service.backgroundTask.LoginTask;
import edu.byu.cs.tweeter.client.model.service.backgroundTask.LogoutTask;
import edu.byu.cs.tweeter.client.model.service.backgroundTask.RegisterTask;
import edu.byu.cs.tweeter.client.model.service.backgroundTask.handler.AuthenticateHandler;
import edu.byu.cs.tweeter.client.model.service.backgroundTask.handler.GetUserHandler;
import edu.byu.cs.tweeter.client.model.service.backgroundTask.handler.SimpleNotificationHandler;
import edu.byu.cs.tweeter.client.model.service.backgroundTask.observer.AuthenticateObserverInterface;
import edu.byu.cs.tweeter.client.model.service.backgroundTask.observer.GetUserObserverInterface;
import edu.byu.cs.tweeter.client.model.service.backgroundTask.observer.SimpleNotificationObserverInterface;
import edu.byu.cs.tweeter.model.domain.AuthToken;

public class UserService {

    public void getUser(AuthToken currUserAuthToken, String userAlias, GetUserObserverInterface getUserObserver) {
        GetUserTask getUserTask = new GetUserTask(currUserAuthToken, userAlias, new GetUserHandler(getUserObserver));
        BackgroundTaskUtils.runTask(getUserTask);
    }

    public void login(String alias, String password, AuthenticateObserverInterface loginObserver) {
        LoginTask loginTask = new LoginTask(alias, password, new AuthenticateHandler(loginObserver));
        BackgroundTaskUtils.runTask(loginTask);
    }

    public void register(String firstName, String lastName, String alias, String password, String imageBytesBase64, AuthenticateObserverInterface registerObserver) {
        RegisterTask registerTask = new RegisterTask(firstName, lastName, alias, password, imageBytesBase64, new AuthenticateHandler(registerObserver));
        BackgroundTaskUtils.runTask(registerTask);
    }

    public void logout(AuthToken currUserAuthToken, SimpleNotificationObserverInterface logoutObserver) {
        LogoutTask logoutTask = new LogoutTask(currUserAuthToken, new SimpleNotificationHandler(logoutObserver));
        BackgroundTaskUtils.runTask(logoutTask);
    }
}
