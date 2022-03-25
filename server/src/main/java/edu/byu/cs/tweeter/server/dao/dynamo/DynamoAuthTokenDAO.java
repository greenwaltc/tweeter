package edu.byu.cs.tweeter.server.dao.dynamo;

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
import edu.byu.cs.tweeter.server.dao.AuthTokenDAO;

public class DynamoAuthTokenDAO extends DynamoDAO implements AuthTokenDAO {
    
    private static final String TableName = "authtoken";
    private static final String TablePrimaryKey = "token_value",
            TableDatetimeKey = "date_time",
            TableAliasKey = "alias";

    private Table table = dynamoDB.getTable(TableName);
    
    @Override
    public void insert(AuthToken authToken, String alias) {
        try {
            PutItemOutcome outcome = table
                    .putItem(new Item().withPrimaryKey(TablePrimaryKey, authToken.getToken())
                            .withString(TableDatetimeKey, authToken.getDatetime())
                            .withString(TableAliasKey, alias));
        } catch (Exception e) {
            throw new RuntimeException("[ServerError] Unable to add authtoken to database");
        }
    }

    @Override
    public AuthToken get(String tokenValue) {
        GetItemSpec spec = new GetItemSpec()
                .withPrimaryKey(TablePrimaryKey, tokenValue);
        try {
            Item getUserOutcome = table.getItem(spec);
            if (getUserOutcome == null) {
                return null;
            }
            return new AuthToken((String) getUserOutcome.get(TablePrimaryKey),
                    (String) getUserOutcome.get(TableDatetimeKey),
                    (String) getUserOutcome.get(TableAliasKey));
        } catch (Exception e) {
            throw new RuntimeException("[ServerError] Unable to read from authtoken table");
        }
    }

    @Override
    public void delete(AuthToken authToken) {
        DeleteItemSpec deleteItemSpec = new DeleteItemSpec()
                .withPrimaryKey(new PrimaryKey(TablePrimaryKey, authToken.getToken()));
        try {
            table.deleteItem(deleteItemSpec);
        } catch (Exception e) {
            throw new RuntimeException("[ServerError] Unable to delete from authtoken table");
        }
    }

    @Override
    public void update(AuthToken authToken) {
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
