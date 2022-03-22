package edu.byu.cs.tweeter.client.model.net;

import java.io.IOException;

import edu.byu.cs.tweeter.model.net.TweeterRemoteException;
import edu.byu.cs.tweeter.model.net.request.IsFollowerRequest;
import edu.byu.cs.tweeter.model.net.request.LoginRequest;
import edu.byu.cs.tweeter.model.net.request.LogoutRequest;
import edu.byu.cs.tweeter.model.net.request.PostStatusRequest;
import edu.byu.cs.tweeter.model.net.request.RegisterRequest;
import edu.byu.cs.tweeter.model.net.request.SimpleUserRequest;
import edu.byu.cs.tweeter.model.net.request.StatusesRequest;
import edu.byu.cs.tweeter.model.net.request.UsersRequest;
import edu.byu.cs.tweeter.model.net.response.AuthenticateResponse;
import edu.byu.cs.tweeter.model.net.response.CountResponse;
import edu.byu.cs.tweeter.model.net.response.StatusesResponse;
import edu.byu.cs.tweeter.model.net.response.UsersResponse;
import edu.byu.cs.tweeter.model.net.response.UsersResponse;
import edu.byu.cs.tweeter.model.net.response.GetUserResponse;
import edu.byu.cs.tweeter.model.net.response.IsFollowerResponse;
import edu.byu.cs.tweeter.model.net.response.SimpleResponse;
import edu.byu.cs.tweeter.model.net.response.StatusesResponse;

/**
 * Acts as a Facade to the Tweeter server. All network requests to the server should go through
 * this class.
 */
public class ServerFacade {

    // TODO: Set this to the invoke URL of your API. Find it by going to your API in AWS, clicking
    //  on stages in the right-side menu, and clicking on the stage you deployed your API to.
    private static final String SERVER_URL = "https://uk5n5iv1kg.execute-api.us-west-2.amazonaws.com/milestone3";

    private ClientCommunicator clientCommunicator = getClientCommunicator();

    public ClientCommunicator getClientCommunicator() {
        if (clientCommunicator == null) {
            clientCommunicator = new ClientCommunicator(SERVER_URL);
        }
        return clientCommunicator;
    }

    /**
     * Performs a login and if successful, returns the logged in user and an auth token.
     *
     * @param request contains all information needed to perform a login.
     * @return the login response.
     */
    public AuthenticateResponse login(LoginRequest request, String urlPath) throws IOException, TweeterRemoteException {
        AuthenticateResponse response = getClientCommunicator().doPost(urlPath, request, null, AuthenticateResponse.class);

        if(response.isSuccess()) {
            return response;
        } else {
            throw new RuntimeException(response.getMessage());
        }
    }

    /**
     * Returns the users that the user specified in the request is following. Uses information in
     * the request object to limit the number of followees returned and to return the next set of
     * followees after any that were returned in a previous request.
     *
     * @param request contains information about the user whose followees are to be returned and any
     *                other information required to satisfy the request.
     * @return the followees.
     */
    public UsersResponse getFollowees(UsersRequest request, String urlPath)
            throws IOException, TweeterRemoteException {

        UsersResponse response = getClientCommunicator().doPost(urlPath, request, null, UsersResponse.class);

        if(response.isSuccess()) {
            return response;
        } else {
            throw new RuntimeException(response.getMessage());
        }
    }

    public UsersResponse getFollowers(UsersRequest request, String urlPath) throws IOException, TweeterRemoteException {
        UsersResponse response = getClientCommunicator().doPost(urlPath, request, null, UsersResponse.class);

        if(response.isSuccess()) {
            return response;
        } else {
            throw new RuntimeException(response.getMessage());
        }
    }

    public StatusesResponse getFeed(StatusesRequest request, String urlPath) throws IOException, TweeterRemoteException {
        StatusesResponse response = getClientCommunicator().doPost(urlPath, request, null, StatusesResponse.class);

        if(response.isSuccess()) {
            return response;
        } else {
            throw new RuntimeException(response.getMessage());
        }
    }

    public StatusesResponse getStory(StatusesRequest request, String urlPath) throws IOException, TweeterRemoteException {
        StatusesResponse response = getClientCommunicator().doPost(urlPath, request, null, StatusesResponse.class);

        if(response.isSuccess()) {
            return response;
        } else {
            throw new RuntimeException(response.getMessage());
        }
    }

    public SimpleResponse follow(SimpleUserRequest request, String urlPath) throws IOException, TweeterRemoteException {
        SimpleResponse response = getClientCommunicator().doPost(urlPath, request, null, SimpleResponse.class);

        if(response.isSuccess()) {
            return response;
        } else {
            throw new RuntimeException(response.getMessage());
        }
    }

    public SimpleResponse unfollow(SimpleUserRequest request, String urlPath) throws IOException, TweeterRemoteException {
        SimpleResponse response = getClientCommunicator().doPost(urlPath, request, null, SimpleResponse.class);

        if(response.isSuccess()) {
            return response;
        } else {
            throw new RuntimeException(response.getMessage());
        }
    }

    public CountResponse getFollowersCount(SimpleUserRequest request, String urlPath) throws IOException, TweeterRemoteException {
        CountResponse response = getClientCommunicator().doPost(urlPath, request, null, CountResponse.class);

        if(response.isSuccess()) {
            return response;
        } else {
            throw new RuntimeException(response.getMessage());
        }
    }

    public CountResponse getFollowingCount(SimpleUserRequest request, String urlPath) throws IOException, TweeterRemoteException {
        CountResponse response = getClientCommunicator().doPost(urlPath, request, null, CountResponse.class);

        if(response.isSuccess()) {
            return response;
        } else {
            throw new RuntimeException(response.getMessage());
        }
    }

    public GetUserResponse getUser(SimpleUserRequest request, String urlPath) throws IOException, TweeterRemoteException {
        GetUserResponse response = getClientCommunicator().doPost(urlPath, request, null, GetUserResponse.class);

        if(response.isSuccess()) {
            return response;
        } else {
            throw new RuntimeException(response.getMessage());
        }
    }

    public IsFollowerResponse isFollower(IsFollowerRequest request, String urlPath) throws IOException, TweeterRemoteException {
        IsFollowerResponse response = getClientCommunicator().doPost(urlPath, request, null, IsFollowerResponse.class);

        if(response.isSuccess()) {
            return response;
        } else {
            throw new RuntimeException(response.getMessage());
        }
    }

    public SimpleResponse logout(LogoutRequest request, String urlPath) throws IOException, TweeterRemoteException {
        SimpleResponse response = getClientCommunicator().doPost(urlPath, request, null, SimpleResponse.class);

        if(response.isSuccess()) {
            return response;
        } else {
            throw new RuntimeException(response.getMessage());
        }
    }

    public SimpleResponse postStatus(PostStatusRequest request, String urlPath) throws IOException, TweeterRemoteException {
        SimpleResponse response = getClientCommunicator().doPost(urlPath, request, null, SimpleResponse.class);

        if(response.isSuccess()) {
            return response;
        } else {
            throw new RuntimeException(response.getMessage());
        }
    }

    public AuthenticateResponse register(RegisterRequest request, String urlPath) throws IOException, TweeterRemoteException {
        AuthenticateResponse response = getClientCommunicator().doPost(urlPath, request, null, AuthenticateResponse.class);

        if(response.isSuccess()) {
            return response;
        } else {
            throw new RuntimeException(response.getMessage());
        }
    }
}