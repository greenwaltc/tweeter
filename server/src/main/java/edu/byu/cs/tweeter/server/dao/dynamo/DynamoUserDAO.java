package edu.byu.cs.tweeter.server.dao.dynamo;

import com.amazonaws.services.dynamodbv2.document.BatchWriteItemOutcome;
import com.amazonaws.services.dynamodbv2.document.Item;
import com.amazonaws.services.dynamodbv2.document.PutItemOutcome;
import com.amazonaws.services.dynamodbv2.document.Table;
import com.amazonaws.services.dynamodbv2.document.TableWriteItems;
import com.amazonaws.services.dynamodbv2.document.UpdateItemOutcome;
import com.amazonaws.services.dynamodbv2.document.spec.GetItemSpec;
import com.amazonaws.services.dynamodbv2.document.spec.UpdateItemSpec;
import com.amazonaws.services.dynamodbv2.document.utils.ValueMap;
import com.amazonaws.services.dynamodbv2.model.ReturnValue;
import com.amazonaws.services.dynamodbv2.model.WriteRequest;

import java.util.List;
import java.util.Map;

import edu.byu.cs.tweeter.model.domain.User;
import edu.byu.cs.tweeter.model.dto.UserDTO;
import edu.byu.cs.tweeter.server.dao.UserDAO;

public class DynamoUserDAO extends DynamoDAO implements UserDAO {

    private static final String TableName = "users";
    private static final String TablePrimaryKey = "alias",
            TableFirstNameKey = "first_name",
            TableLastNameKey = "last_name",
            TablePasswordKey = "password",
            TableSaltKey = "salt",
            TableImageURLKey = "image_url",
            TableFollowersCountKey = "followers_count",
            TableFollowingCountKey = "following_count";

    Table table = dynamoDB.getTable(TableName);

    @Override
    public void updateFollowingCount(String alias, int newCount) {
        updateCount(alias, newCount, TableFollowingCountKey);
    }

    @Override
    public void updateFollowersCount(String alias, int newCount) {
        updateCount(alias, newCount, TableFollowersCountKey);
    }

    private void updateCount(String alias, int newCount, String key) {
        UpdateItemSpec updateItemSpec = new UpdateItemSpec().withPrimaryKey(TablePrimaryKey, alias)
                .withUpdateExpression("set " + key + " = :r")
                .withValueMap(new ValueMap().withInt(":r", newCount))
                .withReturnValues(ReturnValue.UPDATED_NEW);

        try {
            UpdateItemOutcome outcome = table.updateItem(updateItemSpec);
        }
        catch (Exception e) {
            throw new RuntimeException("[ServerError] Unable to update user followers or following count");
        }
    }

    @Override
    public void insert(User user, byte[] hashWord, byte[] salt) {
        try {
            PutItemOutcome outcome = table
                    .putItem(new Item().withPrimaryKey(TablePrimaryKey, user.getAlias())
                            .withString(TableFirstNameKey, user.getFirstName())
                            .withString(TableLastNameKey, user.getLastName())
                            .withBinary(TablePasswordKey, hashWord)
                            .withBinary(TableSaltKey, salt)
                            .withString(TableImageURLKey, user.getImageUrl())
                            .withInt(TableFollowersCountKey, 0)
                            .withInt(TableFollowingCountKey, 0));
        }
        catch (Exception e) {
            throw new RuntimeException("[ServerError] Unable to add new user to database");
        }
    }

    @Override
    public UserDTO get(String alias) {
        GetItemSpec spec = new GetItemSpec()
                .withPrimaryKey(TablePrimaryKey, alias);

        try {
            Item getUserOutcome = table.getItem(spec);

            if (getUserOutcome == null) {
                return null;
            }

            User user = new User((String) getUserOutcome.get(TableFirstNameKey),
                    (String) getUserOutcome.get(TableLastNameKey),
                    (String) getUserOutcome.get(TablePrimaryKey),
                    (String) getUserOutcome.get(TableImageURLKey));
            return new UserDTO(user,
                    (byte[]) getUserOutcome.get(TablePasswordKey),
                    (byte[]) getUserOutcome.get(TableSaltKey),
                    getUserOutcome.getInt(TableFollowersCountKey),
                    getUserOutcome.getInt(TableFollowingCountKey));
        } catch (Exception e) {
            throw new RuntimeException("[ServerError] Unable to read from users table");
        }
    }

    @Override
    public void batchInsert(List<UserDTO> batch) {
        // Constructor for TableWriteItems takes the name of the table, which I have stored in TableName
        TableWriteItems items = new TableWriteItems(TableName);

        // Add each status to the table
        for (UserDTO userDTO : batch) {
            Item item = new Item()
                    .withPrimaryKey(TablePrimaryKey, userDTO.getUser().getAlias())
                    .withString(TableFirstNameKey, userDTO.getUser().getFirstName())
                    .withString(TableLastNameKey, userDTO.getUser().getLastName())
                    .withString(TableImageURLKey, userDTO.getUser().getImageUrl())
                    .withBinary(TablePasswordKey, userDTO.getHash())
                    .withBinary(TableSaltKey, userDTO.getSalt())
                    .withInt(TableFollowersCountKey, userDTO.getFollowersCount())
                    .withInt(TableFollowingCountKey, userDTO.getFollowingCount());
            items.addItemToPut(item);

            // 25 is the maximum number of items allowed in a single batch write.
            // Attempting to write more than 25 items will result in an exception being thrown
            if (items.getItemsToPut() != null && items.getItemsToPut().size() == 25) {
                loopBatchWrite(items);
                items = new TableWriteItems(TableName);
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
