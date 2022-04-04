package edu.byu.cs.tweeter.server.dao.dynamo;

import com.amazonaws.services.dynamodbv2.document.BatchWriteItemOutcome;
import com.amazonaws.services.dynamodbv2.document.Item;
import com.amazonaws.services.dynamodbv2.document.ItemCollection;
import com.amazonaws.services.dynamodbv2.document.PrimaryKey;
import com.amazonaws.services.dynamodbv2.document.QueryOutcome;
import com.amazonaws.services.dynamodbv2.document.Table;
import com.amazonaws.services.dynamodbv2.document.TableWriteItems;
import com.amazonaws.services.dynamodbv2.document.spec.QuerySpec;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.amazonaws.services.dynamodbv2.model.QueryResult;
import com.amazonaws.services.dynamodbv2.model.WriteRequest;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import edu.byu.cs.tweeter.model.domain.Status;
import edu.byu.cs.tweeter.model.dto.PostStatusDTO;
import edu.byu.cs.tweeter.model.net.request.StatusesRequest;
import edu.byu.cs.tweeter.server.dao.StatusDAO;
import edu.byu.cs.tweeter.server.dao.util.JsonSerializer;
import edu.byu.cs.tweeter.util.Pair;

public abstract class AbstractStatusDAO extends DynamoDAO implements StatusDAO {

    protected static final String TableSortKey = "timestamp", TableStatusKey = "status";

    protected Table table = dynamoDB.getTable(getTableName());

    protected abstract String getTableName();
    protected abstract String getTablePrimaryKey();

    public void insert(String alias, Status status) {

        String timestamp = status.getDate();
        String json = JsonSerializer.serialize(status);

        try {
            Item item = new Item()
                    .withPrimaryKey(getTablePrimaryKey(), alias, TableSortKey, timestamp)
                    .withJSON(TableStatusKey, json);
            table.putItem(item);
        }
        catch (Exception e) {
            throw new RuntimeException("[ServerError] Unable to insert into " + getTableName() + " table");
        }
    }

    public Pair<List<Status>, Boolean> getStatuses(StatusesRequest request) {

        String alias = request.getUserAlias();
        String lastItemTimestamp = null;
        if (request.getLastItem() != null) {
            lastItemTimestamp = request.getLastItem().getDate();
        }
        int limit = request.getLimit();

        ArrayList<Status> statuses = new ArrayList<>();
        boolean hasMorePages = true;

        HashMap<String, Object> valueMap = new HashMap<>();
        valueMap.put(":handle", alias);

        PrimaryKey primaryKey;
        ItemCollection<QueryOutcome> items;
        Iterator<Item> iterator;
        Item item;

        QuerySpec querySpec = new QuerySpec().withKeyConditionExpression(getTablePrimaryKey() + " = :handle")
                .withValueMap(valueMap)
                .withScanIndexForward(false)
                .withMaxResultSize(limit);

        if (request.getLastItem() != null) {
            primaryKey = new PrimaryKey(getTablePrimaryKey(), alias, TableSortKey, lastItemTimestamp);
            querySpec.withExclusiveStartKey(primaryKey);
        }

        try {
            items = table.query(querySpec);
            iterator = items.iterator();

            while (iterator.hasNext()) {
                item = iterator.next();

                String statusJson = item.getJSON(TableStatusKey);
                Status status = JsonSerializer.deserialize(statusJson, Status.class);
                statuses.add(status);
            }

            QueryOutcome lastLowLevelResult = items.getLastLowLevelResult();
            QueryResult queryResult = lastLowLevelResult.getQueryResult();
            Map<String, AttributeValue> lastEvaluatedKey = queryResult.getLastEvaluatedKey();
            if (lastEvaluatedKey == null) hasMorePages = false;
        }
        catch (Exception e) {
            throw new RuntimeException("[ServerError] Unable to query " + getTableName());
        }
        return new Pair<>(statuses, hasMorePages);
    }

    public void insertBatch(List<PostStatusDTO> batch) {
        // Constructor for TableWriteItems takes the name of the table, which I have stored in TableName
        TableWriteItems items = new TableWriteItems(getTableName());

        // Add each status to the table
        for (PostStatusDTO postStatusDTO : batch) {
            String alias = postStatusDTO.getAlias();
            String timestamp = postStatusDTO.getStatus().getDate();
            String statusJson = JsonSerializer.serialize(postStatusDTO.getStatus());

            Item item = new Item()
                    .withPrimaryKey(getTablePrimaryKey(), alias, TableSortKey, timestamp)
                    .withJSON(TableStatusKey, statusJson);
            items.addItemToPut(item);

            // 25 is the maximum number of items allowed in a single batch write.
            // Attempting to write more than 25 items will result in an exception being thrown
            if (items.getItemsToPut() != null && items.getItemsToPut().size() == 25) {
                loopBatchWrite(items);
                items = new TableWriteItems(getTableName());
            }
        }

        // Write any leftover items
        if (items.getItemsToPut() != null && items.getItemsToPut().size() > 0) {
            loopBatchWrite(items);
        }
    }

    private void loopBatchWrite(TableWriteItems items) {

        // The 'dynamoDB' object is of type DynamoDB and is declared statically in this example
        BatchWriteItemOutcome outcome = dynamoDB.batchWriteItem(items);

        // Check the outcome for items that didn't make it onto the table
        // If any were not added to the table, try again to write the batch
        while (outcome.getUnprocessedItems().size() > 0) {
            Map<String, List<WriteRequest>> unprocessedItems = outcome.getUnprocessedItems();
            outcome = dynamoDB.batchWriteItemUnprocessed(unprocessedItems);
        }
    }
}
