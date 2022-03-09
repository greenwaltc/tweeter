package edu.byu.cs.tweeter.server.dao;

import edu.byu.cs.tweeter.model.net.request.FollowRequest;
import edu.byu.cs.tweeter.model.net.request.GetUserRequest;
import edu.byu.cs.tweeter.model.net.request.LogoutRequest;
import edu.byu.cs.tweeter.model.net.request.UnfollowRequest;
import edu.byu.cs.tweeter.model.net.response.FollowResponse;
import edu.byu.cs.tweeter.model.net.response.GetUserResponse;
import edu.byu.cs.tweeter.model.net.response.LogoutResponse;
import edu.byu.cs.tweeter.model.net.response.UnfollowResponse;
import edu.byu.cs.tweeter.util.FakeData;

public class UserDAO {

    FakeData getFakeData() {
        return new FakeData();
    }

    public FollowResponse follow(FollowRequest request) {

        // todo: Uses dummy data, replace with real implementation
        return new FollowResponse(true);
    }

    public UnfollowResponse unfollow(UnfollowRequest request) {

        // todo: Uses dummy data, replace with real implementation
        return new UnfollowResponse(true);
    }

    public GetUserResponse getUser(GetUserRequest request) {
        // todo: Uses dummy data, replace with real implementation
        assert request.getTargetUserAlias() != null;

        GetUserResponse response = new GetUserResponse(true,
                getFakeData().findUserByAlias(request.getTargetUserAlias()));

        return response;
    }

    public LogoutResponse logout(LogoutRequest request) {
        return new LogoutResponse(true);
    }
}
