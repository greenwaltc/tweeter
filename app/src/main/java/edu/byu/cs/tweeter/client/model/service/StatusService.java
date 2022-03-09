package edu.byu.cs.tweeter.client.model.service;

import edu.byu.cs.tweeter.client.model.service.backgroundTask.BackgroundTaskUtils;
import edu.byu.cs.tweeter.client.model.service.backgroundTask.authenticatedTask.pagedTask.GetFeedTask;
import edu.byu.cs.tweeter.client.model.service.backgroundTask.authenticatedTask.pagedTask.GetStoryTask;
import edu.byu.cs.tweeter.client.model.service.backgroundTask.authenticatedTask.PostStatusTask;
import edu.byu.cs.tweeter.client.model.service.backgroundTask.handler.PagedTaskHandler;
import edu.byu.cs.tweeter.client.model.service.backgroundTask.handler.SimpleNotificationHandler;
import edu.byu.cs.tweeter.client.model.service.backgroundTask.observer.PagedObserverInterface;
import edu.byu.cs.tweeter.client.model.service.backgroundTask.observer.SimpleNotificationObserverInterface;
import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.Status;
import edu.byu.cs.tweeter.model.domain.User;

public class StatusService {

    public void getFeed(AuthToken currUserAuthToken, User user, int pageSize, Status lastStatus, PagedObserverInterface<Status> getFeedObserver) {
        GetFeedTask getFeedTask = new GetFeedTask(currUserAuthToken, user, pageSize, lastStatus, new PagedTaskHandler(getFeedObserver));
        BackgroundTaskUtils.runTask(getFeedTask);
    }

    public void getStory(AuthToken currUserAuthToken, User user, int pageSize, Status lastStatus, PagedObserverInterface<Status> getStoryObserver) {
        GetStoryTask getStoryTask = new GetStoryTask(currUserAuthToken, user, pageSize, lastStatus, new PagedTaskHandler(getStoryObserver));
        BackgroundTaskUtils.runTask(getStoryTask);
    }

    public void postStatus(AuthToken currUserAuthToken, Status newStatus, SimpleNotificationObserverInterface postStatusObserver) {
        PostStatusTask statusTask = new PostStatusTask(currUserAuthToken, newStatus, new SimpleNotificationHandler(postStatusObserver));
        BackgroundTaskUtils.runTask(statusTask);
    }
}
