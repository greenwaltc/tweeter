package edu.byu.cs.tweeter.client.model.service.backgroundTask.authenticatedTask.getCountTask;

import android.os.Handler;
import android.util.Log;

import java.io.IOException;

import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.User;
import edu.byu.cs.tweeter.model.net.TweeterRemoteException;
import edu.byu.cs.tweeter.model.net.request.SimpleUserRequest;
import edu.byu.cs.tweeter.model.net.response.CountResponse;

/**
 * Background task that queries how many followers a user has.
 */
public class GetFollowersCountTask extends GetCountTask {
    private static final String LOG_TAG = "GetFollowersCountTask";
    static final String URL_PATH = "/getfollowerscount";

    public GetFollowersCountTask(AuthToken authToken, User targetUser, Handler messageHandler) {
        super(authToken, targetUser, messageHandler);
    }

    @Override
    protected void runTask() {
        try {

            String targetUserAlias = getTargetUser() == null ? null : getTargetUser().getAlias();

            SimpleUserRequest request = new SimpleUserRequest(getAuthToken(), targetUserAlias);
            CountResponse response = getServerFacade().getFollowersCount(request, URL_PATH);

            if (response.isSuccess()) {
                setCount(response.getCount());
                sendSuccessMessage();
            } else {
                sendFailedMessage(response.getMessage());
            }

        } catch (IOException | TweeterRemoteException ex) {
            Log.e(LOG_TAG, "Failed to get followers count", ex);
            sendExceptionMessage(ex);
        }
    }
}
