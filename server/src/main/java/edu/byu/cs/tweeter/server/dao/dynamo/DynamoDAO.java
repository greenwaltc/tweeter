package edu.byu.cs.tweeter.server.dao.dynamo;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.document.BatchWriteItemOutcome;
import com.amazonaws.services.dynamodbv2.document.DynamoDB;
import com.amazonaws.services.dynamodbv2.document.Item;
import com.amazonaws.services.dynamodbv2.document.Table;
import com.amazonaws.services.dynamodbv2.document.TableWriteItems;
import com.amazonaws.services.dynamodbv2.model.WriteRequest;

import java.util.List;
import java.util.Map;

import edu.byu.cs.tweeter.model.dto.StatusDTO;
import edu.byu.cs.tweeter.server.dao.BatchDAO;
import edu.byu.cs.tweeter.util.JsonSerializer;

public abstract class DynamoDAO<T> {

    private final int BATCH_SIZE = 25;

    // DynamoDB client
    protected static AmazonDynamoDB amazonDynamoDB = AmazonDynamoDBClientBuilder
            .standard()
            .withRegion("us-west-2")
            .build();
    protected static DynamoDB dynamoDB = new DynamoDB(amazonDynamoDB);

    protected Table table = dynamoDB.getTable(getTableName());

    protected abstract String getTableName();

    public void insert(T ob) {
        try {
            table.putItem(batchCreateItem(ob));
        }
        catch (Exception e) {
            throw new RuntimeException("[ServerError] Unable to insert into " + getTableName());
        }
    }

    public void batchInsert(List<T> batch) {
        // Constructor for TableWriteItems takes the name of the table, which I have stored in TableName
        TableWriteItems items = new TableWriteItems(getTableName());

        // Add each status to the table
        for (T ob : batch) {
            Item item = batchCreateItem(ob);
            items.addItemToPut(item);

            // 25 is the maximum number of items allowed in a single batch write.
            // Attempting to write more than 25 items will result in an exception being thrown
            if (items.getItemsToPut() != null && items.getItemsToPut().size() == BATCH_SIZE) {
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

    protected abstract Item batchCreateItem(T ob);
}
