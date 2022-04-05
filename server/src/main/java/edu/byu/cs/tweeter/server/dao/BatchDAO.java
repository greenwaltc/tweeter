package edu.byu.cs.tweeter.server.dao;

import java.util.List;

public interface BatchDAO<T> {
    void batchInsert(List<T> batch);
    void insert(T ob);
}
