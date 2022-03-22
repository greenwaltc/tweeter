package edu.byu.cs.tweeter.client.model.service;

import static org.mockito.Mockito.anyBoolean;
import static org.mockito.Mockito.anyList;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import org.junit.Assert;
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
    TestGetStoryObserver spyGetStoryObserver;

    private CountDownLatch countDownLatch;

    @Before
    public void setup() {
        authToken = new AuthToken();
        user = new User("test-firstName", "test-lastName", "@test-alias", "test-imageURL");
        pageSize = 10;
        status = new Status("test-post", user, "test-datetime", new ArrayList<String>(), new ArrayList<String>());

        spyStatusService = spy(new StatusService());
        spyGetStoryObserver = spy(new TestGetStoryObserver());

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

        private List<Status> statuses;
        private boolean hasMorePages;

        @Override
        public void handleSuccess(List<Status> statuses, boolean hasMorePages) {
            this.statuses = statuses;
            this.hasMorePages = hasMorePages;

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

        public List<Status> getStatuses() {
            return statuses;
        }

        public boolean isHasMorePages() {
            return hasMorePages;
        }
    }

    @Test

    public void testGetStory() throws InterruptedException {

        spyStatusService.getStory(authToken, user, pageSize, status, spyGetStoryObserver);
        awaitCountDownLatch();

        verify(spyGetStoryObserver, times(1)).handleSuccess(anyList(), anyBoolean());

        Assert.assertNotNull(spyGetStoryObserver.getStatuses());
        Assert.assertTrue(spyGetStoryObserver.getStatuses().size() > 0);
        Assert.assertTrue(spyGetStoryObserver.isHasMorePages());
    }
}
