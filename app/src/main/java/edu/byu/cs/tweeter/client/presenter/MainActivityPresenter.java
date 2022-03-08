package edu.byu.cs.tweeter.client.presenter;

import android.util.Log;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

import edu.byu.cs.tweeter.client.cache.Cache;
import edu.byu.cs.tweeter.client.model.service.backgroundTask.observer.GetCountObserverInterface;
import edu.byu.cs.tweeter.client.model.service.backgroundTask.observer.IsFollowerObserverInterface;
import edu.byu.cs.tweeter.client.model.service.backgroundTask.observer.SimpleNotificationObserverInterface;
import edu.byu.cs.tweeter.client.presenter.view.MainViewInterface;
import edu.byu.cs.tweeter.model.domain.Status;
import edu.byu.cs.tweeter.model.domain.User;

public class MainActivityPresenter extends BasePresenter{

    private static final String LOG_TAG = "MainActivity";

    private MainViewInterface view;

    public MainActivityPresenter(MainViewInterface view) {
        this.view = view;
    }

    public void updateSelectedUserFollowingAndFollowers(User selectedUser) {
        getFollowService().getFollowersCount(Cache.getInstance().getCurrUserAuthToken(), selectedUser, new GetFollowersCountObserver());
        getFollowService().getFollowingCount(Cache.getInstance().getCurrUserAuthToken(), selectedUser, new GetFollowingCountObserver());
    }

    public void updateFollowVisibility(User selectedUser) {
        if (selectedUser.compareTo(Cache.getInstance().getCurrUser()) == 0) {
            view.setFollowVisibility(false);
        } else {
            view.setFollowVisibility(true);
            getFollowService().getIsFollower(Cache.getInstance().getCurrUserAuthToken(), Cache.getInstance().getCurrUser(), selectedUser, new GetIsFollowerObserver());
        }
    }

    public void unfollow(User selectedUser) {
        view.displayMessage("Removing " + selectedUser.getName() + "...");
        getFollowService().unfollow(Cache.getInstance().getCurrUserAuthToken(), selectedUser, new UnfollowObserver());
    }

    public int findUrlEndIndex(String word) {
        if (word.contains(".com")) {
            int index = word.indexOf(".com");
            index += 4;
            return index;
        } else if (word.contains(".org")) {
            int index = word.indexOf(".org");
            index += 4;
            return index;
        } else if (word.contains(".edu")) {
            int index = word.indexOf(".edu");
            index += 4;
            return index;
        } else if (word.contains(".net")) {
            int index = word.indexOf(".net");
            index += 4;
            return index;
        } else if (word.contains(".mil")) {
            int index = word.indexOf(".mil");
            index += 4;
            return index;
        } else {
            return word.length();
        }
    }

    public void follow(User selectedUser) {
        view.displayMessage("Adding " + selectedUser.getName() + "...");
        getFollowService().follow(Cache.getInstance().getCurrUserAuthToken(), selectedUser, new FollowObserver());
    }

    public void logout() {
        view.displayMessage("Logging Out...");
        getUserService().logout(Cache.getInstance().getCurrUserAuthToken(), new LogoutObserver());
    }

    public void postStatus(String post)  {
        view.displayMessage("Posting Status...");
        try {
            Status newStatus = statusFactory(post, Cache.getInstance().getCurrUser(), getFormattedDateTime(), parseURLs(post), parseMentions(post));
            getStatusService().postStatus(Cache.getInstance().getCurrUserAuthToken(), newStatus, new PostStatusObserver());
        } catch (Exception ex) {
            Log.e(LOG_TAG, ex.getMessage(), ex);
            view.displayMessage("Failed to post status because of exception: " + ex.getMessage());
        }
    }

    protected Status statusFactory(String post, User user, String datetime, List<String> urls, List<String> mentions) {
        return new Status(post, user, datetime, urls, mentions);
    }

    public String getFormattedDateTime() throws ParseException {
        SimpleDateFormat userFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        SimpleDateFormat statusFormat = new SimpleDateFormat("MMM d yyyy h:mm aaa");

        return statusFormat.format(userFormat.parse(LocalDate.now().toString() + " " + LocalTime.now().toString().substring(0, 8)));
    }

    public List<String> parseURLs(String post) {
        List<String> containedUrls = new ArrayList<>();
        for (String word : post.split("\\s")) {
            if (word.startsWith("http://") || word.startsWith("https://")) {

                int index = findUrlEndIndex(word);

                word = word.substring(0, index);

                containedUrls.add(word);
            }
        }

        return containedUrls;
    }

    public List<String> parseMentions(String post) {
        List<String> containedMentions = new ArrayList<>();

        for (String word : post.split("\\s")) {
            if (word.startsWith("@")) {
                word = word.replaceAll("[^a-zA-Z0-9]", "");
                word = "@".concat(word);

                containedMentions.add(word);
            }
        }

        return containedMentions;
    }

    public class GetIsFollowerObserver extends BaseObserver implements IsFollowerObserverInterface {

        public GetIsFollowerObserver() {
            super(view);
        }

        @Override
        public void handleSuccess(boolean isFollower) {
            view.setIsFollower(isFollower);
        }

        @Override
        protected String getFailureTag() {
            return "determine following relationship";
        }
    }

    public class GetFollowersCountObserver extends BaseObserver implements GetCountObserverInterface {

        public GetFollowersCountObserver() {
            super(view);
        }

        @Override
        public void handleSuccess(int count) {
            view.setFollowerCount(count);
        }

        @Override
        protected String getFailureTag() {
            return "get followers count";
        }
    }

    public class GetFollowingCountObserver extends BaseObserver implements GetCountObserverInterface {

        public GetFollowingCountObserver() {
            super(view);
        }

        @Override
        public void handleSuccess(int count) {
            view.setFolloweeCount(count);
        }

        @Override
        protected String getFailureTag() {
            return "get following count";
        }
    }

    public abstract class BaseFollowObserver extends BaseObserver implements SimpleNotificationObserverInterface {

        public BaseFollowObserver() {
            super(view);
        }

        @Override
        public void handleSuccess() {
            view.updateSelectedUserFollowingAndFollowers();
            updateFollowButton();
            view.enableFollowButton(true);
        }

        protected abstract void updateFollowButton();

        @Override
        public void handleFailure(String message) {
            view.enableFollowButton(true);
            super.handleFailure(message);
        }

        @Override
        public void handleException(Exception ex) {
            view.enableFollowButton(true);
            super.handleException(ex);
        }
    }

    public class UnfollowObserver extends BaseFollowObserver implements SimpleNotificationObserverInterface {

        @Override
        protected void updateFollowButton() {
            view.updateFollowButton(true);
        }

        @Override
        protected String getFailureTag() {
            return "unfollow";
        }
    }

    public class FollowObserver extends BaseFollowObserver implements SimpleNotificationObserverInterface {

        @Override
        protected void updateFollowButton() {
            view.updateFollowButton(false);
        }

        @Override
        protected String getFailureTag() {
            return "follow";
        }
    }

    public class LogoutObserver extends BaseObserver implements SimpleNotificationObserverInterface {

        public LogoutObserver() {
            super(view);
        }

        @Override
        public void handleSuccess() {
            Cache.getInstance().clearCache();
            view.cancelMessageToast();
            view.logoutUser();
        }

        @Override
        protected String getFailureTag() {
            return "logout";
        }
    }

    public class PostStatusObserver extends BaseObserver implements SimpleNotificationObserverInterface {

        public PostStatusObserver() {
            super(view);
        }

        @Override
        public void handleSuccess() {
            view.cancelMessageToast();
            view.displayMessage("Successfully Posted!");
        }

        @Override
        protected String getFailureTag() {
            return "post status";
        }
    }
}
