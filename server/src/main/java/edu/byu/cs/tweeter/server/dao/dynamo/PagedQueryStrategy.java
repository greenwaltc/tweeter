package edu.byu.cs.tweeter.server.dao.dynamo;

import com.amazonaws.services.dynamodbv2.document.Item;
import com.amazonaws.services.dynamodbv2.document.PrimaryKey;
import com.amazonaws.services.dynamodbv2.document.api.QueryApi;

import java.util.List;

public interface PagedQueryStrategy<T> {
    String getTableName();
    String getQuerySpecKeyConditionalExpression();
    boolean isScanIndexForward();
    PrimaryKey getExclusiveStartKey();
    void insertItem(List<T> queriedItems, Item item);
    QueryApi getQueriable();
}
