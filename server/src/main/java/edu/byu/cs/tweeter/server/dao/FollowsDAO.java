package edu.byu.cs.tweeter.server.dao;

import java.util.List;

import edu.byu.cs.tweeter.model.net.request.UsersRequest;
import edu.byu.cs.tweeter.model.net.response.UsersResponse;
import edu.byu.cs.tweeter.util.Pair;

public interface FollowsDAO {
    void insert(String followerAlias, String followeeAlias);
    Pair<List<String>, Boolean> getFollowees(UsersRequest request);
    Pair<List<String>, Boolean> getFollowers(UsersRequest request);
    void delete(String followerAlias, String followeeAlias);
    boolean isFollower(String followerAlias, String followeeAlias);
}
