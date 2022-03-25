package edu.byu.cs.tweeter.server.dao;

import edu.byu.cs.tweeter.model.dto.UserDTO;
import edu.byu.cs.tweeter.model.domain.User;

public interface UserDAO {
    void updateFollowingCount(String alias, int newCount);
    void updateFollowersCount(String alias, int newCount);
    UserDTO get(String alias);
    void insert(User user, byte[] hashWord, byte[] salt);
}
