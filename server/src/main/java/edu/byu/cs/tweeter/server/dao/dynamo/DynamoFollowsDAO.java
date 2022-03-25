package edu.byu.cs.tweeter.server.dao.dynamo;

import com.amazonaws.services.dynamodbv2.document.Index;
import com.amazonaws.services.dynamodbv2.document.Item;
import com.amazonaws.services.dynamodbv2.document.ItemCollection;
import com.amazonaws.services.dynamodbv2.document.PrimaryKey;
import com.amazonaws.services.dynamodbv2.document.QueryOutcome;
import com.amazonaws.services.dynamodbv2.document.Table;
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
import edu.byu.cs.tweeter.model.net.request.UsersRequest;
import edu.byu.cs.tweeter.server.dao.FollowsDAO;
import edu.byu.cs.tweeter.server.dao.UserDAO;
import edu.byu.cs.tweeter.util.Pair;

/**
 * A DAO for accessing 'following' data from the database.
 */
public class DynamoFollowsDAO extends DynamoDAO implements FollowsDAO {

    private static final String TableName = "follows";
    private static final String TablePrimaryKey = "follower_handle",
            TableSortKey = "followee_handle",
            TableIndex = "followee_handle-follower_handle-index";

    Table table = dynamoDB.getTable(TableName);

    @Override
    public void insert(String followerAlias, String followeeAlias) {
        try {
            table.putItem(new Item()
                    .withPrimaryKey(new PrimaryKey(TablePrimaryKey, followerAlias,
                                            TableSortKey, followeeAlias)));
        } catch (Exception e) {
            throw new RuntimeException("[ServerError] Unable to insert into follows table");
        }
    }

    @Override
    public void delete(String followerAlias, String followeeAlias) {
        DeleteItemSpec deleteItemSpec = new DeleteItemSpec()
                .withPrimaryKey(
                        new PrimaryKey(TablePrimaryKey, followerAlias,
                                TableSortKey, followeeAlias));
        try {
            table.deleteItem(deleteItemSpec);
        } catch (Exception e) {
            throw new RuntimeException("[ServerError] Unable to delete from follows table");
        }
    }

    @Override
    public boolean isFollower(String followerAlias, String followeeAlias) {
        try {
            Item item = table.getItem(new PrimaryKey(TablePrimaryKey, followerAlias, TableSortKey, followeeAlias));
            if (item != null) {
                return true;
            } else {
                return false;
            }
        } catch (Exception e) {
            throw new RuntimeException("[ServerError] Unable to read from follows table");
        }
    }

//    private abstract class GetUsers<T extends QueryApi> {
//
//        protected Pair<List<User>, Boolean> getUsers(UsersRequest request) {
//            ArrayList<User> users = new ArrayList<>();
//            boolean hasMorePages = true;
//
//            HashMap<String, Object> valueMap = new HashMap<>();
//            valueMap.put(":handle", request.getUserAlias());
//
//            PrimaryKey primaryKey;
//            ItemCollection<QueryOutcome> items;
//            Iterator<Item> iterator;
//            Item item;
//
//            QuerySpec querySpec = new QuerySpec().withKeyConditionExpression(TableSortKey + " = :handle")
//                    .withValueMap(valueMap)
//                    .withScanIndexForward(true)
//                    .withMaxResultSize(request.getLimit());
//
//            if (request.getLastItem() != null) {
//                primaryKey = new PrimaryKey(TablePrimaryKey, request.getLastItem(), TableSortKey, request.getUserAlias());
//                querySpec.withExclusiveStartKey(primaryKey);
//            }
//
//            Index follows_index = table.getIndex(TableIndex);
//
//            try {
//                items = follows_index.query(querySpec);
//                iterator = items.iterator();
//
//                while (iterator.hasNext()) {
//                    item = iterator.next();
//
//                    UserDAO userDao = new DynamoUserDAO();
//                    User user = userDao.get(item.getString(TablePrimaryKey)).getUser();
//
//                    users.add(user);
//                }
//
//                QueryOutcome lastLowLevelResult = items.getLastLowLevelResult();
//                QueryResult queryResult = lastLowLevelResult.getQueryResult();
//                Map<String, AttributeValue> lastEvaluatedKey = queryResult.getLastEvaluatedKey();
//                if (lastEvaluatedKey == null) hasMorePages = false;
//            }
//            catch (Exception e) {
//                throw new RuntimeException("[ServerError] Unable to query follows");
//            }
//            return new Pair<>(users, hasMorePages);
//        }
//
//        protected abstract
//    }

    @Override
    public Pair<List<User>, Boolean> getFollowers(UsersRequest request) {

        ArrayList<User> users = new ArrayList<>();
        boolean hasMorePages = true;

        HashMap<String, Object> valueMap = new HashMap<>();
        valueMap.put(":handle", request.getUserAlias());

        PrimaryKey primaryKey;
        ItemCollection<QueryOutcome> items;
        Iterator<Item> iterator;
        Item item;

        QuerySpec querySpec = new QuerySpec().withKeyConditionExpression(TableSortKey + " = :handle")
                .withValueMap(valueMap)
                .withScanIndexForward(true)
                .withMaxResultSize(request.getLimit());

        if (request.getLastItem() != null) {
            primaryKey = new PrimaryKey(TablePrimaryKey, request.getLastItem(), TableSortKey, request.getUserAlias());
            querySpec.withExclusiveStartKey(primaryKey);
        }

        Index follows_index = table.getIndex(TableIndex);

        try {
            items = follows_index.query(querySpec);
            iterator = items.iterator();

            while (iterator.hasNext()) {
                item = iterator.next();

                UserDAO userDao = new DynamoUserDAO();
                User user = userDao.get(item.getString(TablePrimaryKey)).getUser();

                users.add(user);
            }

            QueryOutcome lastLowLevelResult = items.getLastLowLevelResult();
            QueryResult queryResult = lastLowLevelResult.getQueryResult();
            Map<String, AttributeValue> lastEvaluatedKey = queryResult.getLastEvaluatedKey();
            if (lastEvaluatedKey == null) hasMorePages = false;
        }
        catch (Exception e) {
            throw new RuntimeException("[ServerError] Unable to query follows");
        }
        return new Pair<>(users, hasMorePages);
    }

    /**
     * Gets the users from the database that the user specified in the request is following. Uses
     * information in the request object to limit the number of followees returned and to return the
     * next set of following after any that were returned in a previous request. The current
     * implementation returns generated data and doesn't actually access a database.
     *
     * @param request contains information about the user whose followees are to be returned and any
     *                other information required to satisfy the request.
     * @return the following.
     */
    @Override
    public Pair<List<User>, Boolean> getFollowing(UsersRequest request) {

        ArrayList<User> users = new ArrayList<>();
        boolean hasMorePages = true;

        HashMap<String, Object> valueMap = new HashMap<>();
        valueMap.put(":handle", request.getUserAlias());

        PrimaryKey primaryKey;
        ItemCollection<QueryOutcome> items;
        Iterator<Item> iterator;
        Item item;

        QuerySpec querySpec = new QuerySpec().withKeyConditionExpression(TablePrimaryKey + " = :handle")
                .withValueMap(valueMap)
                .withScanIndexForward(true)
                .withMaxResultSize(request.getLimit());

        if (request.getLastItem() != null) {
            primaryKey = new PrimaryKey(TablePrimaryKey, request.getUserAlias(), TableSortKey, request.getLastItem());
            querySpec.withExclusiveStartKey(primaryKey);
        }

        try {
            items = table.query(querySpec);
            iterator = items.iterator();

            while (iterator.hasNext()) {
                item = iterator.next();

                UserDAO userDao = new DynamoUserDAO();
                User user = userDao.get(item.getString(TableSortKey)).getUser();

                users.add(user);
            }

            QueryOutcome lastLowLevelResult = items.getLastLowLevelResult();
            QueryResult queryResult = lastLowLevelResult.getQueryResult();
            Map<String, AttributeValue> lastEvaluatedKey = queryResult.getLastEvaluatedKey();
            if (lastEvaluatedKey == null) hasMorePages = false;
        }
        catch (Exception e) {
            throw new RuntimeException("[ServerError] Unable to query follows");
        }

        return new Pair<>(users, hasMorePages);
    }
}
