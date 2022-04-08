package edu.byu.cs.tweeter.client.presenter;

import static org.mockito.Mockito.*;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;

import edu.byu.cs.tweeter.client.model.service.StatusService;
import edu.byu.cs.tweeter.client.model.service.UserService;
import edu.byu.cs.tweeter.client.model.service.backgroundTask.BackgroundTaskUtils;
import edu.byu.cs.tweeter.client.model.service.backgroundTask.authenticatedTask.PostStatusTask;
import edu.byu.cs.tweeter.client.model.service.backgroundTask.handler.SimpleNotificationHandler;
import edu.byu.cs.tweeter.client.model.service.backgroundTask.observer.AuthenticateObserverInterface;
import edu.byu.cs.tweeter.client.model.service.backgroundTask.observer.PagedObserverInterface;
import edu.byu.cs.tweeter.client.model.service.backgroundTask.observer.SimpleNotificationObserverInterface;
import edu.byu.cs.tweeter.client.presenter.view.MainViewInterface;
import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.Status;
import edu.byu.cs.tweeter.model.domain.User;

public class PostStatusTest {

    private MainViewInterface mockView;

    private MainActivityPresenter spyPresenter;

    private UserService spyUserService;

    private StatusService spyStatusService;

    private Status testStatus;

    private String alias = "@0-0", password = "password";

    private final String postIsSuccess = "Successfully Posted!";

    private User loggedInUser;

    private AuthToken loggedInAuthToken;

    private List<Status> story;

    private CountDownLatch countDownLatch;

    private void resetCountDownLatch() {
        countDownLatch = new CountDownLatch(1);
    }

    private void awaitCountDownLatch() throws InterruptedException {
        countDownLatch.await();
        resetCountDownLatch();
    }

    private class TestLoginObserver implements AuthenticateObserverInterface {

        @Override
        public void handleSuccess(User user, AuthToken authToken) {
            loggedInUser = user;
            loggedInAuthToken = authToken;

            countDownLatch.countDown();
        }

        @Override
        public void handleFailure(String message) {
            // Not needed for positive test case

            countDownLatch.countDown();
        }

        @Override
        public void handleException(Exception ex) {
            // Not needed for positive test case

            countDownLatch.countDown();
        }
    }

    private class TestPostStatusObserver implements SimpleNotificationObserverInterface {

        @Override
        public void handleFailure(String message) {
            countDownLatch.countDown();
        }

        @Override
        public void handleException(Exception ex) {
            countDownLatch.countDown();
        }

        @Override
        public void handleSuccess() {
            mockView.cancelMessageToast();
            mockView.displayMessage(postIsSuccess);
            countDownLatch.countDown();
        }
    }

    private class TestGetStoryObserver implements PagedObserverInterface<Status>{

        @Override
        public void handleSuccess(List<Status> items, boolean hasMorePages) {
            story = items;
            countDownLatch.countDown();
        }

        @Override
        public void handleFailure(String message) {
            countDownLatch.countDown();
        }

        @Override
        public void handleException(Exception ex) {
            countDownLatch.countDown();
        }
    }

    @Before
    public void setUp() throws Exception {

        mockView = mock(MainViewInterface.class);
        spyPresenter = spy(new MainActivityPresenter(mockView));
        spyUserService = spy(new UserService());
        spyStatusService = spy(new StatusService());

        // Mock presenter behavior
        doReturn(testStatus).when(spyPresenter) // testStatus is null right now but will be init after logging in
                .statusFactory(anyString(), any(), anyString(), anyList(), anyList());
        doReturn(spyStatusService).when(spyPresenter).getStatusService();

        // Mock view behavior
        doNothing().when(mockView).cancelMessageToast();
        doNothing().when(mockView).displayMessage(anyString());

        resetCountDownLatch();
    }

    @Test
    public void postStatus() throws InterruptedException, ParseException {
        // Log in a user
        spyUserService.login(alias, password, new TestLoginObserver());
        awaitCountDownLatch(); // After logging in, loggedInUser and loggedInAuthToken will be set

        // Post a status from the user to the server by calling the "post status" operation on the relevant Presenter.

        // Set the test status
        testStatus = new Status("test-post", loggedInUser, spyPresenter.getFormattedDateTime(),
                new ArrayList<>(), new ArrayList<>());


        // Override the default behavior of StatusService.postStatus so we can pass our own values in
        doAnswer(new Answer() {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                PostStatusTask statusTask = new PostStatusTask(loggedInAuthToken, testStatus,
                        new SimpleNotificationHandler(new TestPostStatusObserver()));
                BackgroundTaskUtils.runTask(statusTask);
                return null;
            }
        }).when(spyStatusService).postStatus(any(), any(), any());

        spyPresenter.postStatus("test-post");
        awaitCountDownLatch();

        // Verify that the "Successfully Posted!" message was displayed to the user.
        verify(mockView).cancelMessageToast();
        verify(mockView).displayMessage(postIsSuccess);

        // Retrieve the user's story from the server to verify that the new status was correctly
        // appended to the user's story, and that all status details are correct.
        spyStatusService.getStory(loggedInAuthToken, loggedInUser, 10, null, new TestGetStoryObserver());
        awaitCountDownLatch();
        Assert.assertNotNull(story);
        Assert.assertTrue(story.size() > 0);
        Assert.assertEquals(story.get(0), testStatus);
    }
}