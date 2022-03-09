package edu.byu.cs.tweeter.client.presenter;

import androidx.annotation.NonNull;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import java.util.ArrayList;

import edu.byu.cs.tweeter.client.cache.Cache;
import edu.byu.cs.tweeter.client.model.service.StatusService;
import edu.byu.cs.tweeter.client.model.service.UserService;
import edu.byu.cs.tweeter.client.presenter.view.MainViewInterface;
import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.Status;
import edu.byu.cs.tweeter.model.domain.User;

public class MainPresenterUnitTest {
    private MainViewInterface mockView;
    private UserService mockUserService;
    private StatusService mockStatusService;
    private Cache mockCache;
    private Status mockStatus;

    private MainActivityPresenter mainActivityPresenterSpy;

    @Before
    public void setup() {
        // create mocks
        mockView = Mockito.mock(MainViewInterface.class);
        mockUserService = Mockito.mock(UserService.class);
        mockStatusService = Mockito.mock(StatusService.class);
        mockCache = Mockito.mock(Cache.class);
        mockStatus = Mockito.mock(Status.class);

        mainActivityPresenterSpy = Mockito.spy(new MainActivityPresenter(mockView));
        Mockito.when(mainActivityPresenterSpy.getUserService()).thenReturn(mockUserService);
        Mockito.when(mainActivityPresenterSpy.getStatusService()).thenReturn(mockStatusService);
        Mockito.when(mainActivityPresenterSpy.statusFactory(Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any())).thenReturn(mockStatus);

        Mockito.when(mockStatus.getDate()).thenReturn("00-00-0000");
        Mockito.when(mockStatus.getUser()).thenReturn(new User());
        Mockito.when(mockStatus.getPost()).thenReturn("this is a status");
        Mockito.when(mockStatus.getMentions()).thenReturn(new ArrayList<String>());
        Mockito.when(mockStatus.getUrls()).thenReturn(new ArrayList<String>());

        Cache.setInstance(mockCache);
        Mockito.when(mockCache.getCurrUserAuthToken()).thenReturn(new AuthToken("test-authtoken", "00-00-00T00:00:00"));
    }

    @Test
    public void testLogout_isSuccessful() {
        Answer<Void> answer =  new Answer<>() {
            @Override
            public Void answer(InvocationOnMock invocation) throws Throwable {
                MainActivityPresenter.LogoutObserver logoutObserver = invocation.getArgument(1, MainActivityPresenter.LogoutObserver.class);
                logoutObserver.handleSuccess();
                return null;
            }
        };

        spyLogout(answer);

        Mockito.verify(mockView).displayMessage("Logging Out...");
        Mockito.verify(mockView).logoutUser();
        Mockito.verify(mockCache).clearCache();
    }

    @Test
    public void testLogout_isFailure() {
        Answer<Void> answer =  new Answer<>() {
            @Override
            public Void answer(InvocationOnMock invocation) throws Throwable {
                MainActivityPresenter.LogoutObserver logoutObserver = invocation.getArgument(1, MainActivityPresenter.LogoutObserver.class);
                logoutObserver.handleFailure("error message");
                return null;
            }
        };
        testLogoutError(answer, "Failed to logout: error message");
    }

    @Test
    public void testLogout_isException() {
        Answer<Void> answer =  new Answer<>() {
            @Override
            public Void answer(InvocationOnMock invocation) throws Throwable {
                MainActivityPresenter.LogoutObserver logoutObserver = invocation.getArgument(1, MainActivityPresenter.LogoutObserver.class);
                logoutObserver.handleException(new Exception("exception message"));
                return null;
            }
        };

        testLogoutError(answer, "Failed to logout because of exception: exception message");
    }

    private void testLogoutError(Answer<Void> answer, String verificationString) {
        spyLogout(answer);
        Mockito.verify(mockView).displayMessage(verificationString);
        Mockito.verify(mockCache, Mockito.times(0)).clearCache();
    }

    private void spyLogout(Answer<Void> answer) {
        Mockito.doAnswer(answer).when(mockUserService).logout(Mockito.any(), Mockito.any());
        mainActivityPresenterSpy.logout();
    }

    @Test
    public void testPostStatus_isSuccess() {
        Answer<Void> answer = new Answer<Void>() {
            @Override
            public Void answer(InvocationOnMock invocation) throws Throwable {
                MainActivityPresenter.PostStatusObserver postStatusObserver = getPostStatusObserver(invocation);
                postStatusObserver.handleSuccess();
                return null;
            }
        };
        testPostStatus(answer, "Successfully Posted!");
        Mockito.verify(mockView).cancelMessageToast();
    }

    private void spyPostStatus(Answer<Void> answer) {
        Mockito.doAnswer(answer).when(mockStatusService).postStatus(Mockito.any(), Mockito.any(), Mockito.any());
        mainActivityPresenterSpy.postStatus("this is a post");
    }

    @Test
    public void testPostStatus_isFailure() {
        Answer<Void> answer = new Answer<Void>() {
            @Override
            public Void answer(InvocationOnMock invocation) throws Throwable {
                MainActivityPresenter.PostStatusObserver postStatusObserver = getPostStatusObserver(invocation);
                postStatusObserver.handleFailure("error message");
                return null;
            }
        };
        testPostStatus(answer, "Failed to post status: error message");
    }

    @Test
    public void testPostStatus_isException() {
        Answer<Void> answer = new Answer<Void>() {
            @Override
            public Void answer(InvocationOnMock invocation) throws Throwable {
                MainActivityPresenter.PostStatusObserver postStatusObserver = getPostStatusObserver(invocation);
                postStatusObserver.handleException(new Exception("exception message"));
                return null;
            }
        };
        testPostStatus(answer, "Failed to post status because of exception: exception message");
    }

    private void testPostStatus(Answer<Void> answer, String verificationString) {
        spyPostStatus(answer);
        verifyPostStatusMessages(verificationString);
    }

    private void verifyPostStatusMessages(String verificationString) {
        Mockito.verify(mainActivityPresenterSpy).postStatus("this is a post");
        Mockito.verify(mockView).displayMessage("Posting Status...");
        Mockito.verify(mockView).displayMessage(verificationString);
    }

    @NonNull
    private MainActivityPresenter.PostStatusObserver getPostStatusObserver(InvocationOnMock invocation) {
        AuthToken currUserAuthToken = invocation.getArgument(0, AuthToken.class);
        Status status = invocation.getArgument(1, Status.class);
        MainActivityPresenter.PostStatusObserver postStatusObserver = invocation.getArgument(2, MainActivityPresenter.PostStatusObserver.class);

        // Verify parameters passed to StatusService postStatus are correct
        Assert.assertNotNull(postStatusObserver);

        Assert.assertNotNull(currUserAuthToken);
        Assert.assertEquals(currUserAuthToken.getToken(), "test-authtoken");
        Assert.assertEquals(currUserAuthToken.getDatetime(), "00-00-00T00:00:00");

        Assert.assertNotNull(status);
        Assert.assertEquals(status.getDate(), "00-00-0000");
        Assert.assertNotNull(status.getUser());
        Assert.assertEquals(status.getPost(), "this is a status");
        Assert.assertNotNull(status.getMentions());
        Assert.assertEquals(status.getMentions().size(), 0);
        Assert.assertNotNull(status.getUrls());
        Assert.assertEquals(status.getUrls().size(), 0);
        return postStatusObserver;
    }
}
