package edu.byu.cs.tweeter.server.dao;

import com.amazonaws.services.dynamodbv2.document.Item;
import com.amazonaws.services.dynamodbv2.document.ItemCollection;
import com.amazonaws.services.dynamodbv2.document.PrimaryKey;
import com.amazonaws.services.dynamodbv2.document.QueryOutcome;
import com.amazonaws.services.dynamodbv2.document.Table;
import com.amazonaws.services.dynamodbv2.document.spec.QuerySpec;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.amazonaws.services.dynamodbv2.model.QueryResult;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import edu.byu.cs.tweeter.model.domain.Status;
import edu.byu.cs.tweeter.model.net.request.StatusesRequest;
import edu.byu.cs.tweeter.server.dao.util.JsonSerializer;
import edu.byu.cs.tweeter.util.Pair;

public class DynamoStoryDAO extends DynamoDAO implements StoryDAO {

    private static final String TableName = "story";
    private static final String TablePrimaryKey = "senderalias",
            TableSortKey = "timestamp",
            TableStatusKey = "status";

    Table table = dynamoDB.getTable(TableName);

    @Override
    public void insert(Status status) {

        String senderAlias = status.getUser().getAlias();
        String timestamp = status.getDate();

        String json = JsonSerializer.serialize(status);

        try {
            Item item = new Item()
                    .withPrimaryKey(TablePrimaryKey, senderAlias, TableSortKey, timestamp)
                    .withJSON(TableStatusKey, json);
            table.putItem(item);
        }
        catch (Exception e) {
            throw new RuntimeException("[ServerError] Unable to insert into story table");
        }
    }

    @Override
    public Pair<List<Status>, Boolean> getStory(StatusesRequest request) {

        String senderAlias = request.getUserAlias();
        String lastItemTimestamp = null;
        if (request.getLastItem() != null) {
            lastItemTimestamp = request.getLastItem().getDate();
        }
        int limit = request.getLimit();

        ArrayList<Status> story = new ArrayList<>();
        boolean hasMorePages = true;

        HashMap<String, Object> valueMap = new HashMap<>();
        valueMap.put(":handle", senderAlias);

        PrimaryKey primaryKey;
        ItemCollection<QueryOutcome> items;
        Iterator<Item> iterator;
        Item item;

        QuerySpec querySpec = new QuerySpec().withKeyConditionExpression(TablePrimaryKey + " = :handle")
                .withValueMap(valueMap)
                .withScanIndexForward(false)
                .withMaxResultSize(limit);

        if (request.getLastItem() != null) {
            primaryKey = new PrimaryKey(TablePrimaryKey, senderAlias, TableSortKey, lastItemTimestamp);
            querySpec.withExclusiveStartKey(primaryKey);
        }

        try {
            items = table.query(querySpec);
            iterator = items.iterator();

            while (iterator.hasNext()) {
                item = iterator.next();

                String statusJson = item.getJSON(TableStatusKey);
                Status status = JsonSerializer.deserialize(statusJson, Status.class);
                story.add(status);
            }

            QueryOutcome lastLowLevelResult = items.getLastLowLevelResult();
            QueryResult queryResult = lastLowLevelResult.getQueryResult();
            Map<String, AttributeValue> lastEvaluatedKey = queryResult.getLastEvaluatedKey();
            if (lastEvaluatedKey == null) hasMorePages = false;
        }
        catch (Exception e) {
            throw new RuntimeException("[ServerError] Unable to query story");
        }
        return new Pair<>(story, hasMorePages);
    }
}
