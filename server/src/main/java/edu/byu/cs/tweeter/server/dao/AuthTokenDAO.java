package edu.byu.cs.tweeter.server.dao;

import edu.byu.cs.tweeter.model.domain.AuthToken;

public interface AuthTokenDAO {
    void insert(AuthToken authToken, String alias);
    AuthToken get(String tokenValue);
    void delete(AuthToken authToken);
    void update(AuthToken authToken);
}
