package edu.byu.cs.tweeter.server.dao;

import edu.byu.cs.tweeter.model.domain.AuthToken;

public interface AuthTokenDAO {
    void insertAuthToken(AuthToken authToken);
    AuthToken getAuthToken(String tokenValue);
    void deleteAuthToken(AuthToken authToken);

    void updateAuthToken(AuthToken authToken);
}
