package edu.byu.cs.tweeter.server.dao;

import com.amazonaws.services.dynamodbv2.document.Item;
import com.amazonaws.services.dynamodbv2.document.PutItemOutcome;
import com.amazonaws.services.dynamodbv2.document.Table;

import edu.byu.cs.tweeter.model.domain.AuthToken;

public class DynamoAuthTokenDAO extends DynamoDAO implements AuthTokenDAO{
    
    private static final String TableName = "authtoken";
    private static final String TablePrimaryKey = "token_value",
            DatetimeKey = "datetime";
    
    @Override
    public void insertAuthToken(AuthToken authToken) {
        Table authtokenTable = dynamoDB.getTable(TableName);
        try {
            PutItemOutcome outcome = authtokenTable
                    .putItem(new Item().withPrimaryKey(TablePrimaryKey, authToken.getToken())
                            .withString(DatetimeKey, authToken.getDatetime()));
        } catch (Exception e) {
            throw new RuntimeException("[ServerError] Unable to add authtoken to database");
        }
    }

    @Override
    public AuthToken getAuthToken(String tokenValue) {
        return null;
    }
}
