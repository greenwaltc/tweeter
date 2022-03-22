package edu.byu.cs.tweeter.client.model.net;

import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;

import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.User;
import edu.byu.cs.tweeter.model.net.TweeterRemoteException;
import edu.byu.cs.tweeter.model.net.request.RegisterRequest;
import edu.byu.cs.tweeter.model.net.request.SimpleUserRequest;
import edu.byu.cs.tweeter.model.net.request.UsersRequest;
import edu.byu.cs.tweeter.model.net.response.AuthenticateResponse;
import edu.byu.cs.tweeter.model.net.response.CountResponse;
import edu.byu.cs.tweeter.model.net.response.UsersResponse;

public class ServerFacadeTest {

    private static final String
            SERVER_URL = "https://uk5n5iv1kg.execute-api.us-west-2.amazonaws.com/milestone3",
            REGISTER_URL_PATH = "/register",
            FOLLOWERS_URL_PATH = "/getfollowers",
            FOLLOWING_COUNT_URL_PATH = "/getfollowingcount",
            FOLLOWERS_COUNT_URL_PATH = "/getfollowerscount";

    ServerFacade spyServerFacade;

    ClientCommunicator spyClientCommunicator;

    @Before
    public void setup() {
        spyServerFacade = spy(new ServerFacade());
        spyClientCommunicator = spy(new ClientCommunicator(SERVER_URL));
        when(spyServerFacade.getClientCommunicator()).thenReturn(spyClientCommunicator);
    }

    @After
    public void tearDown() {

    }

    @Test
    public void testRegister() {
        try {

            User registeredUser = new User("Allen", "Anderson", "@allen",
                    "https://faculty.cs.byu.edu/~jwilkerson/cs340/tweeter/images/donald_duck.png");
            RegisterRequest registerRequest = new RegisterRequest("test-firstName",
                    "test-lastName", "@test-username",
                    "test-password", "test-image");

            AuthenticateResponse response = spyServerFacade.register(registerRequest, REGISTER_URL_PATH);

            verify(spyServerFacade, times(1)).register(registerRequest, REGISTER_URL_PATH);
            verify(spyClientCommunicator, times(1)).doPost(REGISTER_URL_PATH, registerRequest, null, AuthenticateResponse.class);

            Assert.assertNotNull(response);
            Assert.assertTrue(response.isSuccess());
            Assert.assertNotNull(response.getUser());
            Assert.assertNotNull(response.getAuthToken());
            Assert.assertEquals(response.getUser(), registeredUser);

        } catch (IOException | TweeterRemoteException e) {
            e.printStackTrace();
            Assert.fail("testRegister threw exception " + e.getMessage());
        }
    }

    @Test
    public void testGetFollowers() {
        try {
            UsersRequest request = new UsersRequest(new AuthToken("test-token", "test-datetime"),
                    "test-followee-alias", 10, "test-last-follower-alias");
            UsersResponse response = spyServerFacade.getFollowers(request, FOLLOWERS_URL_PATH);

            verify(spyServerFacade, times(1)).getFollowers(request, FOLLOWERS_URL_PATH);
            verify(spyClientCommunicator, times(1)).doPost(FOLLOWERS_URL_PATH, request, null, UsersResponse.class);

            Assert.assertNotNull(response);
            Assert.assertTrue(response.isSuccess());
            Assert.assertNotNull(response.getItems());
            Assert.assertTrue(response.getItems().size() >= 0);
            if (response.getItems().size() > 0) {
                Assert.assertNotNull(response.getItems().get(0));
            }
        } catch (IOException | TweeterRemoteException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testGetFollowingCount() {
        try {
            SimpleUserRequest request = new SimpleUserRequest(new AuthToken("test-token", "test-datetime"),
                    "test-target-user-alias");
            CountResponse response = spyServerFacade.getFollowingCount(request, FOLLOWING_COUNT_URL_PATH);

            verify(spyServerFacade, times(1)).getFollowingCount(request, FOLLOWING_COUNT_URL_PATH);
            verify(spyClientCommunicator, times(1)).doPost(FOLLOWING_COUNT_URL_PATH, request, null, CountResponse.class);

            Assert.assertNotNull(response);
            Assert.assertTrue(response.isSuccess());
            Assert.assertNotNull(response.getCount());
            Assert.assertTrue(response.getCount() >= 0);
        } catch (IOException | TweeterRemoteException e) {
            e.printStackTrace();
        }
    }
    @Test
    public void testGetFollowersCount() {
        try {
            SimpleUserRequest request = new SimpleUserRequest(new AuthToken("test-token", "test-datetime"),
                    "test-target-user-alias");
            CountResponse response = spyServerFacade.getFollowersCount(request, FOLLOWERS_COUNT_URL_PATH);

            verify(spyServerFacade, times(1)).getFollowersCount(request, FOLLOWERS_COUNT_URL_PATH);
            verify(spyClientCommunicator, times(1)).doPost(FOLLOWERS_COUNT_URL_PATH, request, null, CountResponse.class);

            Assert.assertNotNull(response);
            Assert.assertTrue(response.isSuccess());
            Assert.assertNotNull(response.getCount());
            Assert.assertTrue(response.getCount() >= 0);
        } catch (IOException | TweeterRemoteException e) {
            e.printStackTrace();
        }
    }

}
