package edu.byu.cs.tweeter.client.model.service;

import static org.mockito.Mockito.spy;

import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;

import edu.byu.cs.tweeter.client.model.service.backgroundTask.observer.PagedObserverInterface;
import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.Status;
import edu.byu.cs.tweeter.model.domain.User;

public class StatusServiceTest {

    private AuthToken authToken;
    private User user;
    private int pageSize;
    private Status status;

    StatusService spyStatusService;
//    GetStoryTask spyGetStoryTask;
    TestGetStoryObserver spyGetStoryObserver;
//    PagedTaskHandler<Status> spyPagedTaskHandler;

//    private Bundle spyBundle;
//    private Message mockMessage;

    private CountDownLatch countDownLatch;

    @Before
    public void setup() {
        authToken = new AuthToken();
        user = new User("test-firstName", "test-lastName", "@test-alias", "test-imageURL");
        pageSize = 10;
        status = new Status("test-post", user, "test-datetime", new ArrayList<String>(), new ArrayList<String>());

        spyStatusService = spy(new StatusService());
        spyGetStoryObserver = new TestGetStoryObserver();
//        spyPagedTaskHandler = new PagedTaskHandler<Status>(spyGetStoryObserver);
//        spyGetStoryTask = spy(new GetStoryTask(authToken, user, pageSize, status, spyPagedTaskHandler));
//        spyBundle = spy(new Bundle());
//
//        mockMessage = mock(Message.class);

//        when(spyStatusService.getStoryTask(any(), any(), anyInt(), any(), any())).thenReturn(spyGetStoryTask);
//        when(spyStatusService.getPagedTaskHandler(any())).thenReturn(spyPagedTaskHandler);
//        when(spyGetStoryTask.getBundle()).thenReturn(spyBundle);
//        when(Message.obtain()).thenReturn(mockMessage);

        resetCountDownLatch();
    }

    private void resetCountDownLatch() {
        countDownLatch = new CountDownLatch(1);
    }

    private void awaitCountDownLatch() throws InterruptedException {
        countDownLatch.await();
        resetCountDownLatch();
    }

    public class TestGetStoryObserver implements PagedObserverInterface<Status> {

        @Override
        public void handleSuccess(List<Status> items, boolean hasMorePages) {
            System.out.println("in handle success");
            countDownLatch.countDown();
        }

        @Override
        public void handleFailure(String message) {
            countDownLatch.countDown();
        }

        @Override
        public void handleException(Exception ex) {
            System.out.println("in handle exception");
            countDownLatch.countDown();
        }
    }

    @Test

    public void testGetStory() throws InterruptedException {

        spyStatusService.getStory(authToken, user, pageSize, status, spyGetStoryObserver);
        awaitCountDownLatch();

//        verify(spyGetStoryObserver, times(1)).handleSuccess(anyList(), anyBoolean());
    }
}
