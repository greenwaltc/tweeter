package edu.byu.cs.tweeter.server.dao;

import com.amazonaws.services.dynamodbv2.document.DeleteItemOutcome;
import com.amazonaws.services.dynamodbv2.document.Index;
import com.amazonaws.services.dynamodbv2.document.Item;
import com.amazonaws.services.dynamodbv2.document.ItemCollection;
import com.amazonaws.services.dynamodbv2.document.PrimaryKey;
import com.amazonaws.services.dynamodbv2.document.PutItemOutcome;
import com.amazonaws.services.dynamodbv2.document.QueryOutcome;
import com.amazonaws.services.dynamodbv2.document.Table;
import com.amazonaws.services.dynamodbv2.document.spec.DeleteItemSpec;
import com.amazonaws.services.dynamodbv2.document.spec.QuerySpec;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.amazonaws.services.dynamodbv2.model.QueryResult;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;

import edu.byu.cs.tweeter.model.domain.User;
import edu.byu.cs.tweeter.model.net.request.IsFollowerRequest;
import edu.byu.cs.tweeter.model.net.request.SimpleUserRequest;
import edu.byu.cs.tweeter.model.net.request.UsersRequest;
import edu.byu.cs.tweeter.model.net.response.IsFollowerResponse;
import edu.byu.cs.tweeter.model.net.response.SimpleResponse;
import edu.byu.cs.tweeter.model.net.response.UsersResponse;
import edu.byu.cs.tweeter.util.FakeData;
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
            PutItemOutcome outcome = table
                    .putItem(new Item()
                            .withPrimaryKey(
                                    new PrimaryKey(TablePrimaryKey, followerAlias, TableSortKey, followeeAlias)));
        }
        catch (Exception e) {
            throw new RuntimeException("[ServerError] Unable to add new follows relationship");
        }
    }

    @Override
    public void delete(String followerAlias, String followeeAlias) {
        DeleteItemSpec deleteItemSpec = new DeleteItemSpec()
                .withPrimaryKey(
                        new PrimaryKey(TablePrimaryKey, followerAlias, TableSortKey, followeeAlias));

        try {
            DeleteItemOutcome outcome = table.deleteItem(deleteItemSpec);
        }
        catch (Exception e) {
            throw new RuntimeException("[ServerError] Unable to delete follows relationship");
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

    /**
     * Gets the users from the database that the user specified in the request is following. Uses
     * information in the request object to limit the number of followees returned and to return the
     * next set of followees after any that were returned in a previous request. The current
     * implementation returns generated data and doesn't actually access a database.
     *
     * @param request contains information about the user whose followees are to be returned and any
     *                other information required to satisfy the request.
     * @return the followees.
     */
    public Pair<List<String>, Boolean> getFollowees(UsersRequest request) {

        ArrayList<String> followeesAliases = new ArrayList<>();
        boolean hasMorePages = true;

        HashMap<String, Object> valueMap = new HashMap<>();
        valueMap.put(":handle", request.getUserAlias());

        PrimaryKey primaryKey;
        ItemCollection<QueryOutcome> items;
        Iterator<Item> iterator;
        Item item;

        QuerySpec querySpec = new QuerySpec().withKeyConditionExpression("follower_handle = :handle")
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
                followeesAliases.add(item.getString(TableSortKey));
            }

            QueryOutcome lastLowLevelResult = items.getLastLowLevelResult();
            QueryResult queryResult = lastLowLevelResult.getQueryResult();
            Map<String, AttributeValue> lastEvaluatedKey = queryResult.getLastEvaluatedKey();
            if (lastEvaluatedKey == null) hasMorePages = false;
        }
        catch (Exception e) {
            throw new RuntimeException("[ServerError] Unable to query follows");
        }
        return new Pair<>(followeesAliases, hasMorePages);
    }

//    /**
//     * Gets the users from the database that the user specified in the request is following. Uses
//     * information in the request object to limit the number of followees returned and to return the
//     * next set of followees after any that were returned in a previous request. The current
//     * implementation returns generated data and doesn't actually access a database.
//     *
//     * @param request contains information about the user whose followees are to be returned and any
//     *                other information required to satisfy the request.
//     * @return the followees.
//     */
//    public UsersResponse getFollowees(UsersRequest request) {
//        // TODO: Generates dummy data. Replace with a real implementation.
//        assert request.getLimit() > 0;
//        assert request.getUserAlias() != null;
//
//        List<User> allFollowees = getDummyUsers();
//        List<User> responseFollowees = new ArrayList<>(request.getLimit());
//
//        boolean hasMorePages = false;
//
//        if(request.getLimit() > 0) {
//            if (allFollowees != null) {
//                int followeesIndex = getFolloweesStartingIndex(request.getUserAlias(), allFollowees);
//
//                for(int limitCounter = 0; followeesIndex < allFollowees.size() && limitCounter < request.getLimit(); followeesIndex++, limitCounter++) {
//                    responseFollowees.add(allFollowees.get(followeesIndex));
//                }
//
//                hasMorePages = followeesIndex < allFollowees.size();
//            }
//        }
//
//        return new UsersResponse(responseFollowees, hasMorePages);
//    }

    /**
     * Determines the index for the first followee in the specified 'allFollowees' list that should
     * be returned in the current request. This will be the index of the next followee after the
     * specified 'lastFollowee'.
     *
     * @param lastFolloweeAlias the alias of the last followee that was returned in the previous
     *                          request or null if there was no previous request.
     * @param allFollowees the generated list of followees from which we are returning paged results.
     * @return the index of the first followee to be returned.
     */
    private int getFolloweesStartingIndex(String lastFolloweeAlias, List<User> allFollowees) {

        int followeesIndex = 0;

        if(lastFolloweeAlias != null) {
            // This is a paged request for something after the first page. Find the first item
            // we should return
            for (int i = 0; i < allFollowees.size(); i++) {
                if(lastFolloweeAlias.equals(allFollowees.get(i).getAlias())) {
                    // We found the index of the last item returned last time. Increment to get
                    // to the first one we should return
                    followeesIndex = i + 1;
                    break;
                }
            }
        }

        return followeesIndex;
    }

    /**
     * Returns the list of dummy followee data. This is written as a separate method to allow
     * mocking of the followees.
     *
     * @return the followees.
     */
    List<User> getDummyUsers() {
        return getFakeData().getFakeUsers();
    }

    /**
     * Returns the {@link FakeData} object used to generate dummy followees.
     * This is written as a separate method to allow mocking of the {@link FakeData}.
     *
     * @return a {@link FakeData} instance.
     */
    FakeData getFakeData() {
        return new FakeData();
    }

    public Pair<List<String>, Boolean> getFollowers(UsersRequest request) {

        ArrayList<String> followersAliases = new ArrayList<>();
        boolean hasMorePages = true;

        HashMap<String, Object> valueMap = new HashMap<>();
        valueMap.put(":handle", request.getUserAlias());

        PrimaryKey primaryKey;
        ItemCollection<QueryOutcome> items;
        Iterator<Item> iterator;
        Item item;

        QuerySpec querySpec = new QuerySpec().withKeyConditionExpression("followee_handle = :handle")
                .withValueMap(valueMap)
                .withScanIndexForward(true)
                .withMaxResultSize(request.getLimit());

        if (request.getLastItem() != null) {
            primaryKey = new PrimaryKey(TablePrimaryKey, request.getUserAlias(), TableSortKey, request.getLastItem());
            querySpec.withExclusiveStartKey(primaryKey);
        }

        Index follows_index = table.getIndex(TableIndex);

        try {
            items = follows_index.query(querySpec);
            iterator = items.iterator();

            while (iterator.hasNext()) {
                item = iterator.next();
                followersAliases.add(item.getString(TablePrimaryKey));
            }

            QueryOutcome lastLowLevelResult = items.getLastLowLevelResult();
            QueryResult queryResult = lastLowLevelResult.getQueryResult();
            Map<String, AttributeValue> lastEvaluatedKey = queryResult.getLastEvaluatedKey();
            if (lastEvaluatedKey == null) hasMorePages = false;
        }
        catch (Exception e) {
            throw new RuntimeException("[ServerError] Unable to query follows");
        }
        return new Pair<>(followersAliases, hasMorePages);
    }

    private int getFollowersStartingIndex(String lastFollowerAlias, List<User> allFollowers) {
        int followersIndex = 0;

        if(lastFollowerAlias != null) {
            // This is a paged request for something after the first page. Find the first item
            // we should return
            for (int i = 0; i < allFollowers.size(); i++) {
                if(lastFollowerAlias.equals(allFollowers.get(i).getAlias())) {
                    // We found the index of the last item returned last time. Increment to get
                    // to the first one we should return
                    followersIndex = i + 1;
                    break;
                }
            }
        }

        return followersIndex;
    }

    public IsFollowerResponse isFollower(IsFollowerRequest request) {
        assert request.getFollowerAlias() != null;
        assert request.getFolloweeAlias() != null;

        boolean isFollower = new Random().nextInt() > 0;

        return new IsFollowerResponse(true, isFollower);
    }
}
