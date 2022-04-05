package edu.byu.cs.tweeter.server.dao.dynamo;

import com.amazonaws.services.dynamodbv2.document.Index;
import com.amazonaws.services.dynamodbv2.document.Item;
import com.amazonaws.services.dynamodbv2.document.ItemCollection;
import com.amazonaws.services.dynamodbv2.document.PrimaryKey;
import com.amazonaws.services.dynamodbv2.document.QueryOutcome;
import com.amazonaws.services.dynamodbv2.document.TableWriteItems;
import com.amazonaws.services.dynamodbv2.document.api.QueryApi;
import com.amazonaws.services.dynamodbv2.document.spec.DeleteItemSpec;
import com.amazonaws.services.dynamodbv2.document.spec.QuerySpec;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.amazonaws.services.dynamodbv2.model.QueryResult;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import edu.byu.cs.tweeter.model.domain.User;
import edu.byu.cs.tweeter.model.dto.FollowsDTO;
import edu.byu.cs.tweeter.server.dao.FollowsDAO;
import edu.byu.cs.tweeter.server.dao.UserDAO;
import edu.byu.cs.tweeter.util.Pair;

/**
 * A DAO for accessing 'following' data from the database.
 */
public class DynamoFollowsDAO extends DynamoDAO implements FollowsDAO {

    private static final String TableName = "follows",
            TablePrimaryKey = "follower_handle",
            TableSortKey = "followee_handle",
            TableIndex = "followee_handle-follower_handle-index";

    @Override
    public void delete(String primaryKey, String sortKey) {
        DeleteItemSpec deleteItemSpec = new DeleteItemSpec()
                .withPrimaryKey(
                        new PrimaryKey(TablePrimaryKey, primaryKey,
                                TableSortKey, sortKey));
        try {
            table.deleteItem(deleteItemSpec);
        } catch (Exception e) {
            throw new RuntimeException("[ServerError] Unable to delete from " + getTableName());
        }
    }

    @Override
    public boolean isFollower(String primaryKey, String sortKey) {
        try {
            Item item = table.getItem(new PrimaryKey(TablePrimaryKey, primaryKey, TableSortKey, sortKey));
            if (item != null) {
                return true;
            } else {
                return false;
            }
        } catch (Exception e) {
            throw new RuntimeException("[ServerError] Unable to read from " + getTableName());
        }
    }

    @Override
    public Pair<List<String>, Boolean> getFollowers(String alias, int limit, String lastItemValue) {

        PagedQueryStrategy<String> getFollowersStrategy = new PagedQueryStrategy<String>() {
            @Override
            public String getTableName() {
                return TableName;
            }

            @Override
            public String getQuerySpecKeyConditionalExpression() {
                return TableSortKey + " = :handle";
            }

            @Override
            public boolean isScanIndexForward() {
                return true;
            }

            @Override
            public PrimaryKey getExclusiveStartKey() {
                return new PrimaryKey(TablePrimaryKey, lastItemValue, TableSortKey, alias);
            }

            @Override
            public void insertItem(List<String> queriedItems, Item item) {
                queriedItems.add(item.getString(TablePrimaryKey));
            }

            @Override
            public QueryApi getQueriable() {
                return table.getIndex(TableIndex);
            }
        };
        return PagedQuery.getItems(getFollowersStrategy, alias, limit, lastItemValue);
    }

    @Override
    public Pair<List<String>, Boolean> getFollowing(String alias, int limit, String lastItemValue) {

        PagedQueryStrategy<String> getFollowingStrategy = new PagedQueryStrategy() {
            @Override
            public String getTableName() {
                return TableName;
            }

            @Override
            public String getQuerySpecKeyConditionalExpression() {
                return TablePrimaryKey + " = :handle";
            }

            @Override
            public boolean isScanIndexForward() {
                return true;
            }

            @Override
            public PrimaryKey getExclusiveStartKey() {
                return new PrimaryKey(TablePrimaryKey, alias, TableSortKey, lastItemValue);
            }

            @Override
            public void insertItem(List queriedItems, Item item) {
                queriedItems.add(item.getString(TableSortKey));
            }

            @Override
            public QueryApi getQueriable() {
                return table;
            }
        };
        return PagedQuery.getItems(getFollowingStrategy, alias, limit, lastItemValue);
    }

    @Override
    protected String getTableName() {
        return TableName;
    }

    @Override
    protected Item batchCreateItem(Object ob) {
        FollowsDTO followsDTO = (FollowsDTO) ob;
        return new Item().withPrimaryKey(TablePrimaryKey, followsDTO.getFollowerAlias(),
                TableSortKey, followsDTO.getFolloweeAlias());
    }
}
