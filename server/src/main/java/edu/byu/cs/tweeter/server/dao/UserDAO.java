package edu.byu.cs.tweeter.server.dao;

import edu.byu.cs.tweeter.model.domain.DBUser;
import edu.byu.cs.tweeter.model.domain.User;

public interface UserDAO {
    void updateFollowingCount(String alias, int newCount);
    void updateFollowersCount(String alias, int newCount);
    DBUser get(String alias);
    void insert(User user, byte[] hashWord, byte[] salt);
}
