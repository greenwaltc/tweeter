package edu.byu.cs.tweeter.server.dao.dynamo;

import com.amazonaws.services.dynamodbv2.document.Item;
import com.amazonaws.services.dynamodbv2.document.TableWriteItems;
import com.amazonaws.services.dynamodbv2.document.spec.GetItemSpec;
import com.amazonaws.services.dynamodbv2.document.spec.UpdateItemSpec;
import com.amazonaws.services.dynamodbv2.document.utils.ValueMap;
import com.amazonaws.services.dynamodbv2.model.ReturnValue;

import java.util.List;

import edu.byu.cs.tweeter.model.domain.User;
import edu.byu.cs.tweeter.model.dto.UserDTO;
import edu.byu.cs.tweeter.server.dao.UserDAO;

public class DynamoUserDAO extends DynamoDAO implements UserDAO {

    private static final String TablePrimaryKey = "alias",
            TableFirstNameKey = "first_name",
            TableLastNameKey = "last_name",
            TablePasswordKey = "password",
            TableSaltKey = "salt",
            TableImageURLKey = "image_url",
            TableFollowersCountKey = "followers_count",
            TableFollowingCountKey = "following_count";

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
            table.updateItem(updateItemSpec);
        }
        catch (Exception e) {
            throw new RuntimeException("[ServerError] Unable to update " + getTableName());
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
            throw new RuntimeException("[ServerError] Unable to read from " + getTableName());
        }
    }

    @Override
    protected String getTableName() {
        return "users";
    }

    @Override
    protected Item batchCreateItem(Object ob) {
        UserDTO userDTO = (UserDTO) ob;
        return new Item()
                .withPrimaryKey(TablePrimaryKey, userDTO.getUser().getAlias())
                .withString(TableFirstNameKey, userDTO.getUser().getFirstName())
                .withString(TableLastNameKey, userDTO.getUser().getLastName())
                .withString(TableImageURLKey, userDTO.getUser().getImageUrl())
                .withBinary(TablePasswordKey, userDTO.getHash())
                .withBinary(TableSaltKey, userDTO.getSalt())
                .withInt(TableFollowersCountKey, userDTO.getFollowersCount())
                .withInt(TableFollowingCountKey, userDTO.getFollowingCount());
    }
}
