package edu.byu.cs.tweeter.client.model.net;

import org.junit.*;
import static org.mockito.Mockito.*;

import java.io.IOException;

import edu.byu.cs.tweeter.model.domain.User;
import edu.byu.cs.tweeter.model.net.TweeterRemoteException;
import edu.byu.cs.tweeter.model.net.request.RegisterRequest;
import edu.byu.cs.tweeter.model.net.response.RegisterResponse;

public class ServerFacadeTest {

    private static final String SERVER_URL = "https://uk5n5iv1kg.execute-api.us-west-2.amazonaws.com/milestone3";
    private static final String URL_PATH = "/register";

    ServerFacade spyServerFacade;

    ClientCommunicator spyClientCommunicator;

    RegisterRequest registerRequest = new RegisterRequest("test-firstName", "test-lastName", "@test-username", "test-password", "test-image");
    User registeredUser = new User("Allen", "Anderson", "@allen", "https://faculty.cs.byu.edu/~jwilkerson/cs340/tweeter/images/donald_duck.png");
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
            RegisterResponse response = spyServerFacade.register(registerRequest, URL_PATH);

            verify(spyServerFacade, times(1)).register(registerRequest, URL_PATH);
            verify(spyClientCommunicator, times(1)).doPost(URL_PATH, registerRequest, null, RegisterResponse.class);

            Assert.assertNotNull(response);
            Assert.assertNotNull(response.getUser());
            Assert.assertNotNull(response.getAuthToken());
            Assert.assertTrue(response.isSuccess());
            Assert.assertEquals(response.getUser(), registeredUser);

        } catch (IOException | TweeterRemoteException e) {
            e.printStackTrace();
            Assert.fail("testRegister threw exception " + e.getMessage());
        }

    }

    @Test
    public void testGetFollowers() {

    }

    @Test
    public void testGetFollowingCount() {

    }
    @Test
    public void testGetFollowersCount() {

    }

}
