package edu.byu.cs.tweeter.server.dao.dynamo;

import com.amazonaws.services.dynamodbv2.document.Item;
import com.amazonaws.services.dynamodbv2.document.ItemCollection;
import com.amazonaws.services.dynamodbv2.document.PrimaryKey;
import com.amazonaws.services.dynamodbv2.document.QueryOutcome;
import com.amazonaws.services.dynamodbv2.document.api.QueryApi;
import com.amazonaws.services.dynamodbv2.document.spec.QuerySpec;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.amazonaws.services.dynamodbv2.model.QueryResult;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import edu.byu.cs.tweeter.util.Pair;

public class PagedQuery {
    public static<T> Pair<List<T>, Boolean> getItems(PagedQueryStrategy strategy, String alias, int limit, String lastItemValue){
        List<T> queriedItems = new ArrayList<>();
        boolean hasMorePages = true;

        HashMap<String, Object> valueMap = new HashMap<>();
        valueMap.put(":handle", alias);

        PrimaryKey primaryKey;
        ItemCollection<QueryOutcome> items;
        Iterator<Item> iterator;
        Item item;

        QuerySpec querySpec = new QuerySpec().withKeyConditionExpression(strategy.getQuerySpecKeyConditionalExpression())
                .withValueMap(valueMap)
                .withScanIndexForward(strategy.isScanIndexForward())
                .withMaxResultSize(limit);

        if (lastItemValue != null) {
            primaryKey = strategy.getExclusiveStartKey();
            querySpec.withExclusiveStartKey(primaryKey);
        }

        QueryApi queriable = strategy.getQueriable();

        try {
            items = queriable.query(querySpec);
            iterator = items.iterator();

            while (iterator.hasNext()) {
                item = iterator.next();
                strategy.insertItem(queriedItems, item);
            }

            QueryOutcome lastLowLevelResult = items.getLastLowLevelResult();
            QueryResult queryResult = lastLowLevelResult.getQueryResult();
            Map<String, AttributeValue> lastEvaluatedKey = queryResult.getLastEvaluatedKey();
            if (lastEvaluatedKey == null) hasMorePages = false;
        }
        catch (Exception e) {
            throw new RuntimeException("[ServerError] Unable to query " + strategy.getTableName());
        }

        return new Pair<>(queriedItems, hasMorePages);
    }
}
