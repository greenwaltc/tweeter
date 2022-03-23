package edu.byu.cs.tweeter.server.dao;

import com.amazonaws.services.dynamodbv2.document.Item;
import com.amazonaws.services.dynamodbv2.document.PutItemOutcome;
import com.amazonaws.services.dynamodbv2.document.Table;
import com.amazonaws.services.dynamodbv2.document.UpdateItemOutcome;
import com.amazonaws.services.dynamodbv2.document.spec.GetItemSpec;
import com.amazonaws.services.dynamodbv2.document.spec.UpdateItemSpec;
import com.amazonaws.services.dynamodbv2.document.utils.ValueMap;
import com.amazonaws.services.dynamodbv2.model.ReturnValue;

import edu.byu.cs.tweeter.model.domain.DBUser;
import edu.byu.cs.tweeter.model.domain.User;

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
    public DBUser get(String alias) {
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
            return new DBUser(user,
                    (byte[]) getUserOutcome.get(TablePasswordKey),
                    (byte[]) getUserOutcome.get(TableSaltKey),
                    getUserOutcome.getInt(TableFollowersCountKey),
                    getUserOutcome.getInt(TableFollowingCountKey));
        } catch (Exception e) {
            throw new RuntimeException("[ServerError] Unable to read from users table");
        }
    }
}
