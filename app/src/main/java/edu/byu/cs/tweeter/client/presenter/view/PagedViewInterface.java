package edu.byu.cs.tweeter.client.presenter.view;

import java.util.List;

public interface PagedViewInterface<T> extends UserViewInterface {
    void setLoadingStatus(boolean value);
    void addItems(List<T> items);
}
