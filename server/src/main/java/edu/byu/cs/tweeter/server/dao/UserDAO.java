package edu.byu.cs.tweeter.server.dao;

import edu.byu.cs.tweeter.model.net.request.LoginRequest;
import edu.byu.cs.tweeter.model.net.request.LogoutRequest;
import edu.byu.cs.tweeter.model.net.request.RegisterRequest;
import edu.byu.cs.tweeter.model.net.request.SimpleUserRequest;
import edu.byu.cs.tweeter.model.net.response.AuthenticateResponse;
import edu.byu.cs.tweeter.model.net.response.GetUserResponse;
import edu.byu.cs.tweeter.model.net.response.SimpleResponse;

public interface UserDAO {
    GetUserResponse getUser(SimpleUserRequest request);
    SimpleResponse logout(LogoutRequest request);
    AuthenticateResponse register(RegisterRequest request);
    AuthenticateResponse login(LoginRequest request);
}
