package edu.byu.cs.tweeter.server.service;

import edu.byu.cs.tweeter.model.net.request.LoginRequest;
import edu.byu.cs.tweeter.model.net.request.LogoutRequest;
import edu.byu.cs.tweeter.model.net.request.RegisterRequest;
import edu.byu.cs.tweeter.model.net.request.SimpleUserRequest;
import edu.byu.cs.tweeter.model.net.response.AuthenticateResponse;
import edu.byu.cs.tweeter.model.net.response.GetUserResponse;
import edu.byu.cs.tweeter.model.net.response.SimpleResponse;
import edu.byu.cs.tweeter.server.dao.DAOFactory;
import edu.byu.cs.tweeter.server.dao.UserDAO;

public class UserService {

    private UserDAO userDAO;

    public UserService(DAOFactory daoFactory) {
        userDAO = daoFactory.getUserDAO();
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

    public AuthenticateResponse login(LoginRequest request) {
        if(request.getUsername() == null){
            throw new RuntimeException("[BadRequest] Missing a username");
        } else if(request.getPassword() == null) {
            throw new RuntimeException("[BadRequest] Missing a password");
        }

        return userDAO.login(request);
    }

    public GetUserResponse getUser(SimpleUserRequest request) {
        if (request.getTargetUserAlias() == null) {
            throw new RuntimeException("[BadRequest] Missing a target user alias");
        }
        return userDAO.getUser(request);
    }

    public SimpleResponse logout(LogoutRequest request) {
        if (request.getAuthToken() == null) {
            throw new RuntimeException("[BadRequest] Missing authToken");
        }
        return userDAO.logout(request);
    }
}
