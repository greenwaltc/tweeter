package edu.byu.cs.tweeter.server.dao;

import java.util.List;

import edu.byu.cs.tweeter.model.domain.User;
import edu.byu.cs.tweeter.model.net.request.UsersRequest;
import edu.byu.cs.tweeter.util.Pair;

public interface FollowsDAO {
    void insert(String followerAlias, String followeeAlias);
    void delete(String followerAlias, String followeeAlias);
    boolean isFollower(String followerAlias, String followeeAlias);
    Pair<List<String>, Boolean> getFollowers(String userAlias, int limit, String lastFollowerAlias);
    Pair<List<User>, Boolean> getFollowing(UsersRequest request);
    void batchInsert(List<String> batchUsers, String followeeAlias);
}
