package edu.byu.cs.tweeter.server.dao;

import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.dto.AuthTokenDTO;

public interface AuthTokenDAO extends BatchDAO{
    AuthTokenDTO get(String tokenValue);
    void delete(AuthToken authToken);
    void update(AuthToken authToken);
}
