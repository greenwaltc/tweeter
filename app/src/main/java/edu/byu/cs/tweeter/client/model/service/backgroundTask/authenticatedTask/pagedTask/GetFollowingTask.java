package edu.byu.cs.tweeter.client.model.service.backgroundTask.authenticatedTask.pagedTask;

import android.os.Handler;
import android.util.Log;

import java.io.IOException;
import java.util.List;

import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.User;
import edu.byu.cs.tweeter.model.net.TweeterRemoteException;
import edu.byu.cs.tweeter.model.net.request.UsersRequest;
import edu.byu.cs.tweeter.model.net.response.UsersResponse;
import edu.byu.cs.tweeter.util.Pair;

/**
 * Background task that retrieves a page of other users being followed by a specified user.
 */
public class GetFollowingTask extends PagedUserTask {

    private static final String LOG_TAG = "GetFollowingTask";

    static final String URL_PATH = "/getfollowing";

    public GetFollowingTask(AuthToken authToken, User targetUser, int limit, User lastFollowee,
                            Handler messageHandler) {
        super(authToken, targetUser, limit, lastFollowee, messageHandler);
    }

    @Override
    protected void runTask() throws IOException {
        try {
            String targetUserAlias = getTargetUser() == null ? null : getTargetUser().getAlias();
            String lastFolloweeAlias = getLastItem() == null ? null : getLastItem().getAlias();

            UsersRequest request = new UsersRequest(getAuthToken(), targetUserAlias, getLimit(), lastFolloweeAlias);
            UsersResponse response = getServerFacade().getFollowees(request, URL_PATH);

            if(response.isSuccess()) {
                setItems(response.getItems());
                setHasMorePages(response.getHasMorePages());
                sendSuccessMessage();
            }
            else {
                sendFailedMessage(response.getMessage());
            }
        } catch (IOException | TweeterRemoteException ex) {
            Log.e(LOG_TAG, "Failed to get followees", ex);
            sendExceptionMessage(ex);
        }
    }

    @Override
    protected Pair<List<User>, Boolean> getItems() {
        return getFakeData().getPageOfUsers(getLastItem(), getLimit(), getTargetUser());
    }
}
