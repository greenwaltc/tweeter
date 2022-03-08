package edu.byu.cs.tweeter.client.model.service.backgroundTask.handler;

import android.os.Bundle;

import edu.byu.cs.tweeter.client.model.service.backgroundTask.GetCountTask;
import edu.byu.cs.tweeter.client.model.service.backgroundTask.observer.GetCountObserverInterface;

public class GetCountHandler extends BackgroundTaskHandler<GetCountObserverInterface>{
    public GetCountHandler(GetCountObserverInterface observer) {
        super(observer);
    }

    @Override
    protected void handleSuccess(Bundle data, GetCountObserverInterface observer) {
        int count = data.getInt(GetCountTask.COUNT_KEY);
        observer.handleSuccess(count);
    }
}
