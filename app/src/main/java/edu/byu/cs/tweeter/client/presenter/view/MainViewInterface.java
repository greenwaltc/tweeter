package edu.byu.cs.tweeter.client.presenter.view;

public interface MainViewInterface extends BaseViewInterface {
    void setFollowerCount(int count);

    void setFolloweeCount(int count);

    void setFollowVisibility(boolean value);

    void setIsFollower(boolean isFollower);

    void enableFollowButton(boolean value);

    void updateFollowButton(boolean removed);

    void updateSelectedUserFollowingAndFollowers();

    void logoutUser();

    void cancelMessageToast();
}
