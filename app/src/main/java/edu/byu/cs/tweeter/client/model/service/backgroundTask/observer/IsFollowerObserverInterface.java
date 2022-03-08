package edu.byu.cs.tweeter.client.model.service.backgroundTask.observer;

public interface IsFollowerObserverInterface extends ServiceObserverInterface {
    void handleSuccess(boolean isFollower);
}
