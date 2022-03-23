package edu.byu.cs.tweeter.server.service;

import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.net.request.SimpleUserRequest;
import edu.byu.cs.tweeter.model.net.request.UsersRequest;
import edu.byu.cs.tweeter.server.dao.DAOFactory;
import edu.byu.cs.tweeter.server.service.util.AuthTokenUtils;

public abstract class Service {
    protected DAOFactory daoFactory;

    public Service(DAOFactory daoFactory) {
        this.daoFactory = daoFactory;
    }

    protected void verifyAuthToken(AuthToken authToken) {
        AuthToken dbAuthToken = daoFactory.getAuthTokenDAO().get(authToken.getToken());
        if (dbAuthToken == null) {
            throw new RuntimeException("[BadRequest] Access denied");
        }
        else if (AuthTokenUtils.verifyAuthToken(dbAuthToken)) {
            daoFactory.getAuthTokenDAO().update(dbAuthToken); // Update with new timestamp
        } else {
            daoFactory.getAuthTokenDAO().delete(dbAuthToken);
            throw new RuntimeException("[BadRequest] Access denied");
        }
    }

    protected void verifySimpleUserRequest(SimpleUserRequest request) {
        if (request.getTargetUserAlias() == null) {
            throw new RuntimeException("[BadRequest] Missing a target user alias");
        }
        verifyAuthToken(request.getAuthToken());
    }

    protected void verifyUsersRequest(UsersRequest request) {
        if(request.getUserAlias() == null) {
            throw new RuntimeException("[BadRequest] Request needs to have a user alias");
        } else if(request.getLimit() <= 0) {
            throw new RuntimeException("[BadRequest] Request needs to have a positive limit");
        }
        verifyAuthToken(request.getAuthToken());
    }
}
