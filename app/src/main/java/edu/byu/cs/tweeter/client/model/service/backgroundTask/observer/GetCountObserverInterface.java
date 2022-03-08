package edu.byu.cs.tweeter.client.model.service.backgroundTask.observer;

public interface GetCountObserverInterface extends ServiceObserverInterface {
    void handleSuccess(int count);
}
