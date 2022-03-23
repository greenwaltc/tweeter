package edu.byu.cs.tweeter.server.dao;

import edu.byu.cs.tweeter.model.net.request.UsersRequest;
import edu.byu.cs.tweeter.model.net.response.UsersResponse;

public interface FollowsDAO {
    void insert(String followerAlias, String followeeAlias);
    UsersResponse getFollowees(UsersRequest request);
    UsersResponse getFollowers(UsersRequest request);
    void delete(String followerAlias, String followeeAlias);
    boolean isFollower(String followerAlias, String followeeAlias);
}
