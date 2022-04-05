package edu.byu.cs.tweeter.server.dao;

import java.util.List;

import edu.byu.cs.tweeter.model.domain.User;
import edu.byu.cs.tweeter.model.dto.FollowsDTO;
import edu.byu.cs.tweeter.util.Pair;

public interface FollowsDAO extends BatchDAO{
    void delete(String followerAlias, String followeeAlias);
    boolean isFollower(String followerAlias, String followeeAlias);
    Pair<List<String>, Boolean> getFollowers(String userAlias, int limit, String lastAlias);
    Pair<List<String>, Boolean> getFollowing(String userAlias, int limit, String lastAlias);
}
