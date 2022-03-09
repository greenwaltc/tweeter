package edu.byu.cs.tweeter.client.model.service.backgroundTask.handler;

import android.os.Bundle;

import java.util.List;

import edu.byu.cs.tweeter.client.model.service.backgroundTask.authenticatedTask.pagedTask.PagedTask;
import edu.byu.cs.tweeter.client.model.service.backgroundTask.observer.PagedObserverInterface;

public class PagedTaskHandler<T> extends BackgroundTaskHandler<PagedObserverInterface> {

    public PagedTaskHandler(PagedObserverInterface observer) {
        super(observer);
    }

    @Override
    protected void handleSuccess(Bundle data, PagedObserverInterface observer) {
        List<T> statuses = (List<T>) data.getSerializable(PagedTask.ITEMS_KEY);
        boolean hasMorePages = data.getBoolean(PagedTask.MORE_PAGES_KEY);
        observer.handleSuccess(statuses, hasMorePages);
    }
}
