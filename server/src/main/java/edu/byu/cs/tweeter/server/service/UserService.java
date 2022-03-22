package edu.byu.cs.tweeter.server.service;

import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.User;
import edu.byu.cs.tweeter.model.net.request.LoginRequest;
import edu.byu.cs.tweeter.model.net.request.LogoutRequest;
import edu.byu.cs.tweeter.model.net.request.RegisterRequest;
import edu.byu.cs.tweeter.model.net.request.SimpleUserRequest;
import edu.byu.cs.tweeter.model.net.response.AuthenticateResponse;
import edu.byu.cs.tweeter.model.net.response.GetUserResponse;
import edu.byu.cs.tweeter.model.net.response.SimpleResponse;
import edu.byu.cs.tweeter.server.dao.DAOFactory;
import edu.byu.cs.tweeter.server.dao.DynamoUserDAO;
import edu.byu.cs.tweeter.server.dao.UserDAO;
import edu.byu.cs.tweeter.util.FakeData;

public class UserService {

    private UserDAO userDAO;

    public UserService(DAOFactory daoFactory) {
        userDAO = daoFactory.getUserDAO();

    }

    public AuthenticateResponse login(LoginRequest request) {
        if(request.getUsername() == null){
            throw new RuntimeException("[BadRequest] Missing a username");
        } else if(request.getPassword() == null) {
            throw new RuntimeException("[BadRequest] Missing a password");
        }

        // TODO: Generates dummy data. Replace with a real implementation.
        User user = getDummyUser();
        AuthToken authToken = getDummyAuthToken();
        return new AuthenticateResponse(user, authToken, true);
    }

    /**
     * Returns the dummy user to be returned by the login operation.
     * This is written as a separate method to allow mocking of the dummy user.
     *
     * @return a dummy user.
     */
    User getDummyUser() {
        return getFakeData().getFirstUser();
    }

    /**
     * Returns the dummy auth token to be returned by the login operation.
     * This is written as a separate method to allow mocking of the dummy auth token.
     *
     * @return a dummy auth token.
     */
    AuthToken getDummyAuthToken() {
        return getFakeData().getAuthToken();
    }

    /**
     * Returns the {@link FakeData} object used to generate dummy users and auth tokens.
     * This is written as a separate method to allow mocking of the {@link FakeData}.
     *
     * @return a {@link FakeData} instance.
     */
    FakeData getFakeData() {
        return new FakeData();
    }

    public SimpleResponse follow(SimpleUserRequest request) {
        if (request.getTargetUserAlias() == null) {
            throw new RuntimeException("[BadRequest] Missing a target user alias");
        }
         return userDAO.follow(request);
    }

    public SimpleResponse unfollow(SimpleUserRequest request) {
        if (request.getTargetUserAlias() == null) {
            throw new RuntimeException("[BadRequest] Missing a target user alias");
        }
        return userDAO.unfollow(request);
    }

    public GetUserResponse getUser(SimpleUserRequest request) {
        if (request.getTargetUserAlias() == null) {
            throw new RuntimeException("[BadRequest] Missing a target user alias");
        }
        return userDAO.getUser(request);
    }

    public SimpleResponse logout(LogoutRequest request) {
        return userDAO.logout(request);
    }

    public AuthenticateResponse register(RegisterRequest request) {
        if(request.getUsername() == null){
            throw new RuntimeException("[BadRequest] Missing a username");
        } else if(request.getPassword() == null) {
            throw new RuntimeException("[BadRequest] Missing a password");
        } else if (request.getFirstName() == null) {
            throw new RuntimeException("[BadRequest] Missing a first name");
        } else if (request.getLastName() == null) {
            throw new RuntimeException("[BadRequest] Missing a last name");
        } else if (request.getImage() == null) {
            throw new RuntimeException("[BadRequest] Missing an image");
        }

        return userDAO.register(request);
    }
}
