package edu.byu.cs.tweeter.server.dao.dynamo;

import com.amazonaws.services.dynamodbv2.document.Item;
import com.amazonaws.services.dynamodbv2.document.PrimaryKey;
import com.amazonaws.services.dynamodbv2.document.spec.DeleteItemSpec;
import com.amazonaws.services.dynamodbv2.document.spec.GetItemSpec;
import com.amazonaws.services.dynamodbv2.document.spec.UpdateItemSpec;
import com.amazonaws.services.dynamodbv2.document.utils.ValueMap;
import com.amazonaws.services.dynamodbv2.model.ReturnValue;

import java.sql.Timestamp;
import java.util.List;

import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.dto.AuthTokenDTO;
import edu.byu.cs.tweeter.server.dao.AuthTokenDAO;

public class DynamoAuthTokenDAO extends DynamoDAO implements AuthTokenDAO {
    
    private static final String TablePrimaryKey = "token_value",
            TableDatetimeKey = "date_time",
            TableAliasKey = "alias";

    @Override
    public AuthTokenDTO get(String tokenValue) {
        GetItemSpec spec = new GetItemSpec()
                .withPrimaryKey(TablePrimaryKey, tokenValue);
        try {
            Item outcome = table.getItem(spec);
            if (outcome == null) {
                return null;
            }
            AuthToken authToken = new AuthToken((String) outcome.get(TablePrimaryKey), (String) outcome.get(TableDatetimeKey));
            AuthTokenDTO authTokenDTO = new AuthTokenDTO(authToken, (String) outcome.get(TableAliasKey));
            return authTokenDTO;
        } catch (Exception e) {
            throw new RuntimeException("[ServerError] Unable to read from " + getTableName());
        }
    }

    @Override
    public void delete(AuthToken authToken) {
        DeleteItemSpec deleteItemSpec = new DeleteItemSpec()
                .withPrimaryKey(new PrimaryKey(TablePrimaryKey, authToken.getToken()));
        try {
            table.deleteItem(deleteItemSpec);
        } catch (Exception e) {
            throw new RuntimeException("[ServerError] Unable to delete from " + getTableName());
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
            table.updateItem(updateItemSpec);
        }
        catch (Exception e) {
            throw new RuntimeException("[ServerError] Unable to update " + getTableName());
        }
    }

    @Override
    protected String getTableName() {
        return "authtoken";
    }

    @Override
    protected Item batchCreateItem(Object ob) {
        AuthTokenDTO authTokenDTO = (AuthTokenDTO) ob;
        return new Item().withPrimaryKey(TablePrimaryKey, authTokenDTO.getAuthToken().getToken())
                .withString(TableDatetimeKey, authTokenDTO.getAuthToken().getDatetime())
                .withString(TableAliasKey, authTokenDTO.getAlias());
    }
}
