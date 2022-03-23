package edu.byu.cs.tweeter.server.dao;

import com.amazonaws.services.dynamodbv2.document.Item;
import com.amazonaws.services.dynamodbv2.document.PrimaryKey;
import com.amazonaws.services.dynamodbv2.document.PutItemOutcome;
import com.amazonaws.services.dynamodbv2.document.Table;
import com.amazonaws.services.dynamodbv2.document.UpdateItemOutcome;
import com.amazonaws.services.dynamodbv2.document.spec.DeleteItemSpec;
import com.amazonaws.services.dynamodbv2.document.spec.GetItemSpec;
import com.amazonaws.services.dynamodbv2.document.spec.UpdateItemSpec;
import com.amazonaws.services.dynamodbv2.document.utils.ValueMap;
import com.amazonaws.services.dynamodbv2.model.ReturnValue;

import java.sql.Timestamp;

import edu.byu.cs.tweeter.model.domain.AuthToken;

public class DynamoAuthTokenDAO extends DynamoDAO implements AuthTokenDAO{
    
    private static final String TableName = "authtoken";
    private static final String TablePrimaryKey = "token_value",
            TableDatetimeKey = "date_time";
    
    @Override
    public void insertAuthToken(AuthToken authToken) {
        Table authtokenTable = dynamoDB.getTable(TableName);
        try {
            PutItemOutcome outcome = authtokenTable
                    .putItem(new Item().withPrimaryKey(TablePrimaryKey, authToken.getToken())
                            .withString(TableDatetimeKey, authToken.getDatetime()));
        } catch (Exception e) {
            throw new RuntimeException("[ServerError] Unable to add authtoken to database");
        }
    }

    @Override
    public AuthToken getAuthToken(String tokenValue) {
        Table userTable = dynamoDB.getTable(TableName);
        GetItemSpec spec = new GetItemSpec()
                .withPrimaryKey(TablePrimaryKey, tokenValue);

        try {
            Item getUserOutcome = userTable.getItem(spec);
            if (getUserOutcome == null) {
                return null;
            }
            return new AuthToken((String) getUserOutcome.get(TablePrimaryKey),
                    (String) getUserOutcome.get(TableDatetimeKey));
        } catch (Exception e) {
            throw new RuntimeException("[ServerError] Unable to read from authtoken table");
        }
    }

    @Override
    public void deleteAuthToken(AuthToken authToken) {
        Table table = dynamoDB.getTable(TableName);

        DeleteItemSpec deleteItemSpec = new DeleteItemSpec()
                .withPrimaryKey(new PrimaryKey(TablePrimaryKey, authToken.getToken()));

        try {
            table.deleteItem(deleteItemSpec);
        } catch (Exception e) {
            throw new RuntimeException("[ServerError] Unable to delete from authtoken table");
        }
    }

    @Override
    public void updateAuthToken(AuthToken authToken) {
        Table table = dynamoDB.getTable(TableName);

        Timestamp currTime = new Timestamp(System.currentTimeMillis());

        UpdateItemSpec updateItemSpec = new UpdateItemSpec().withPrimaryKey(TablePrimaryKey, authToken.getToken())
                .withUpdateExpression("set " + TableDatetimeKey + " = :r")
                .withValueMap(new ValueMap().withString(":r", currTime.toString()))
                .withReturnValues(ReturnValue.UPDATED_NEW);

        try {
            UpdateItemOutcome outcome = table.updateItem(updateItemSpec);
        }
        catch (Exception e) {
            throw new RuntimeException("[ServerError] Unable to update authToken");
        }
    }
}
