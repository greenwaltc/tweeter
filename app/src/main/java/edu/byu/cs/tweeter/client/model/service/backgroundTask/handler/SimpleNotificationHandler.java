package edu.byu.cs.tweeter.client.model.service.backgroundTask.handler;

import android.os.Bundle;

import edu.byu.cs.tweeter.client.model.service.backgroundTask.observer.SimpleNotificationObserverInterface;

public class SimpleNotificationHandler extends BackgroundTaskHandler<SimpleNotificationObserverInterface> {

    public SimpleNotificationHandler(SimpleNotificationObserverInterface observer) {
        super(observer);
    }

    @Override
    protected void handleSuccess(Bundle data, SimpleNotificationObserverInterface observer) {
        observer.handleSuccess();
    }
}
