package edu.byu.cs.tweeter.server.dao;

import edu.byu.cs.tweeter.model.net.request.IsFollowerRequest;
import edu.byu.cs.tweeter.model.net.request.SimpleUserRequest;
import edu.byu.cs.tweeter.model.net.request.UsersRequest;
import edu.byu.cs.tweeter.model.net.response.CountResponse;
import edu.byu.cs.tweeter.model.net.response.IsFollowerResponse;
import edu.byu.cs.tweeter.model.net.response.SimpleResponse;
import edu.byu.cs.tweeter.model.net.response.UsersResponse;

public interface FollowDAO {
    SimpleResponse follow(SimpleUserRequest request);
    SimpleResponse unfollow(SimpleUserRequest request);
    UsersResponse getFollowees(UsersRequest request);
    UsersResponse getFollowers(UsersRequest request);
    CountResponse getFollowersCount(SimpleUserRequest request);
    CountResponse getFollowingCount(SimpleUserRequest request);
    IsFollowerResponse isFollower(IsFollowerRequest request);
}
