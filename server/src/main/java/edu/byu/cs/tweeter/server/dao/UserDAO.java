package edu.byu.cs.tweeter.server.dao;

import java.util.List;

import edu.byu.cs.tweeter.model.dto.UserDTO;

public interface UserDAO extends BatchDAO{
    void updateFollowingCount(String alias, int newCount);
    void updateFollowersCount(String alias, int newCount);
    UserDTO get(String alias);
}
