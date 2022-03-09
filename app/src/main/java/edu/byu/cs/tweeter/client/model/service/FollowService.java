package edu.byu.cs.tweeter.client.model.service;

import edu.byu.cs.tweeter.client.model.service.backgroundTask.BackgroundTaskUtils;
import edu.byu.cs.tweeter.client.model.service.backgroundTask.authenticatedTask.FollowTask;
import edu.byu.cs.tweeter.client.model.service.backgroundTask.authenticatedTask.GetFollowersCountTask;
import edu.byu.cs.tweeter.client.model.service.backgroundTask.authenticatedTask.pagedTask.GetFollowersTask;
import edu.byu.cs.tweeter.client.model.service.backgroundTask.authenticatedTask.GetFollowingCountTask;
import edu.byu.cs.tweeter.client.model.service.backgroundTask.authenticatedTask.pagedTask.GetFollowingTask;
import edu.byu.cs.tweeter.client.model.service.backgroundTask.authenticatedTask.IsFollowerTask;
import edu.byu.cs.tweeter.client.model.service.backgroundTask.authenticatedTask.UnfollowTask;
import edu.byu.cs.tweeter.client.model.service.backgroundTask.handler.GetCountHandler;
import edu.byu.cs.tweeter.client.model.service.backgroundTask.handler.IsFollowerHandler;
import edu.byu.cs.tweeter.client.model.service.backgroundTask.handler.PagedTaskHandler;
import edu.byu.cs.tweeter.client.model.service.backgroundTask.handler.SimpleNotificationHandler;
import edu.byu.cs.tweeter.client.model.service.backgroundTask.observer.GetCountObserverInterface;
import edu.byu.cs.tweeter.client.model.service.backgroundTask.observer.IsFollowerObserverInterface;
import edu.byu.cs.tweeter.client.model.service.backgroundTask.observer.PagedObserverInterface;
import edu.byu.cs.tweeter.client.model.service.backgroundTask.observer.SimpleNotificationObserverInterface;
import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.User;

public class FollowService {

    public void getFollowing(AuthToken currUserAuthToken, User user, int pageSize, User lastFollowee, PagedObserverInterface<User> getFollowingObserver) {
        GetFollowingTask getFollowingTask = new GetFollowingTask(currUserAuthToken, user, pageSize, lastFollowee, new PagedTaskHandler(getFollowingObserver));
        BackgroundTaskUtils.runTask(getFollowingTask);
    }

    public void getFollowers(AuthToken currUserAuthToken, User user, int pageSize, User lastFollower, PagedObserverInterface<User> getFollowersObserver) {
        GetFollowersTask getFollowersTask = new GetFollowersTask(currUserAuthToken, user, pageSize, lastFollower, new PagedTaskHandler(getFollowersObserver));
        BackgroundTaskUtils.runTask(getFollowersTask);
    }

    public void getFollowersCount(AuthToken currUserAuthToken, User selectedUser, GetCountObserverInterface getFollowersCountObserver) {
        GetFollowersCountTask followersCountTask = new GetFollowersCountTask(currUserAuthToken, selectedUser, new GetCountHandler(getFollowersCountObserver));
        BackgroundTaskUtils.runTask(followersCountTask);
    }

    public void getFollowingCount(AuthToken currUserAuthToken, User selectedUser, GetCountObserverInterface getFollowingCountObserver) {
        GetFollowingCountTask followingCountTask = new GetFollowingCountTask(currUserAuthToken, selectedUser, new GetCountHandler(getFollowingCountObserver));
        BackgroundTaskUtils.runTask(followingCountTask);
    }

    public void getIsFollower(AuthToken currUserAuthToken, User currUser, User selectedUser, IsFollowerObserverInterface isFollowerObserver) {
        IsFollowerTask isFollowerTask = new IsFollowerTask(currUserAuthToken, currUser, selectedUser, new IsFollowerHandler(isFollowerObserver));
        BackgroundTaskUtils.runTask(isFollowerTask);
    }

    public void unfollow(AuthToken currUserAuthToken, User selectedUser, SimpleNotificationObserverInterface unfollowObserver) {
        UnfollowTask unfollowTask = new UnfollowTask(currUserAuthToken, selectedUser, new SimpleNotificationHandler(unfollowObserver));
        BackgroundTaskUtils.runTask(unfollowTask);
    }

    public void follow(AuthToken currUserAuthToken, User selectedUser, SimpleNotificationObserverInterface followObserver) {
        FollowTask followTask = new FollowTask(currUserAuthToken, selectedUser, new SimpleNotificationHandler(followObserver));
        BackgroundTaskUtils.runTask(followTask);
    }
}
