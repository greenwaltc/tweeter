package edu.byu.cs.tweeter.server.service;

import com.amazonaws.services.dynamodbv2.document.Item;
import com.amazonaws.services.dynamodbv2.document.ItemCollection;
import com.amazonaws.services.dynamodbv2.document.PrimaryKey;
import com.amazonaws.services.dynamodbv2.document.QueryOutcome;
import com.amazonaws.services.dynamodbv2.document.spec.QuerySpec;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.amazonaws.services.dynamodbv2.model.QueryResult;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.DBUser;
import edu.byu.cs.tweeter.model.net.request.IsFollowerRequest;
import edu.byu.cs.tweeter.model.net.request.SimpleUserRequest;
import edu.byu.cs.tweeter.model.net.request.UsersRequest;
import edu.byu.cs.tweeter.model.net.response.IsFollowerResponse;
import edu.byu.cs.tweeter.model.net.response.SimpleResponse;
import edu.byu.cs.tweeter.model.net.response.UsersResponse;
import edu.byu.cs.tweeter.server.dao.DAOFactory;
import edu.byu.cs.tweeter.server.dao.DynamoFollowsDAO;

/**
 * Contains the business logic for getting the users a user is following.
 */
public class FollowService extends Service{

    public FollowService(DAOFactory daoFactory) {
        super(daoFactory);
    }

    /**
     * Returns the users that the user specified in the request is following. Uses information in
     * the request object to limit the number of followees returned and to return the next set of
     * followees after any that were returned in a previous request. Uses the {@link DynamoFollowsDAO} to
     * get the followees.
     *
     * @param request contains the data required to fulfill the request.
     * @return the followees.
     */
    public UsersResponse getFollowees(UsersRequest request) {
        verifyAuthToken(request.getAuthToken());

        HashMap<String, Object> valueMap = new HashMap<String, Object>();
        valueMap.put(":handle", "@FredFlintstone");

        PrimaryKey primaryKey = null;
        QuerySpec querySpec = new QuerySpec().withKeyConditionExpression("follower_handle = :handle")
                .withValueMap(valueMap)
                .withScanIndexForward(true)
                .withMaxResultSize(10);

        do {
            ItemCollection<QueryOutcome> items = null;
            Iterator<Item> iterator = null;
            Item item = null;

            try {
                System.out.println("Users @FredFlintstone follows");
                items = table.query(querySpec);
                iterator = items.iterator();
                while (iterator.hasNext()) {
                    item = iterator.next();
                    System.out.println(item.getString("followee_handle"));
                }

                QueryOutcome lastLowLevelResult = items.getLastLowLevelResult();
                QueryResult queryResult = lastLowLevelResult.getQueryResult();
                Map<String, AttributeValue> lastEvaluatedKey = queryResult.getLastEvaluatedKey();
                if (lastEvaluatedKey == null) break;

                String key = lastEvaluatedKey.get("follower_handle").getS();
                String sortKey = lastEvaluatedKey.get("followee_handle").getS();

                primaryKey = new PrimaryKey()
                        .addComponent("follower_handle", key)
                        .addComponent("followee_handle", sortKey);

                querySpec.withExclusiveStartKey(primaryKey);
            }
            catch (Exception e) {
                System.err.println("Unable to query follows");
                System.err.println(e.getMessage());
            }

        } while (primaryKey != null);


//        verifyUsersRequest(request);
//
//        return daoFactory.getFollowsDAO().getFollowees(request);
        return null;
    }

    /**
     * Returns the followers of the user specified. Uses information in
     * the request object to limit the number of followers returned and to return the next set of
     * followers after any that were returned in a previous request. Uses the {@link DynamoFollowsDAO} to
     * get the followers.
     *
     * @param request contains the data required to fulfill the request.
     * @return the followers.
     */
    public UsersResponse getFollowers(UsersRequest request) {
        verifyUsersRequest(request);
        return daoFactory.getFollowsDAO().getFollowers(request);
    }

    public IsFollowerResponse isFollower(IsFollowerRequest request) {
        if(request.getFollowerAlias() == null) {
            throw new RuntimeException("[BadRequest] Request needs to have a follower alias");
        }
        if(request.getFolloweeAlias() == null) {
            throw new RuntimeException("[BadRequest] Request needs to have a followee alias");
        }
        verifyAuthToken(request.getAuthToken());

        String followerAlias = request.getFollowerAlias();
        String followeeAlias = request.getFolloweeAlias();

        // Verify both users exist
        DBUser follower = daoFactory.getUserDAO().get(followerAlias);
        DBUser followee = daoFactory.getUserDAO().get(followeeAlias);

        verifyUsersExist(follower, followee);

        if (daoFactory.getFollowsDAO().isFollower(followerAlias, followeeAlias)) {
            return new IsFollowerResponse(true, true);
        } else {
            return new IsFollowerResponse(true, false);
        }
    }

    public SimpleResponse updateFollowingRelationship(SimpleUserRequest request, boolean follow) {
        verifySimpleUserRequest(request);

        AuthToken dbAuthToken = daoFactory.getAuthTokenDAO().get(request.getAuthToken().getToken());
        String followerAlias = dbAuthToken.getAlias();
        String followeeAlias = request.getTargetUserAlias();

        // Verify both users exist
        DBUser follower = daoFactory.getUserDAO().get(followerAlias);
        DBUser followee = daoFactory.getUserDAO().get(followeeAlias);

        verifyUsersExist(follower, followee);

        if (follow) { // Follow
            // Insert the new following relationship to database
            daoFactory.getFollowsDAO().insert(followerAlias, followeeAlias);

            // Increment followers/following counts, respectively
            daoFactory.getUserDAO().updateFollowingCount(followerAlias, follower.getFollowingCount()+1);
            daoFactory.getUserDAO().updateFollowersCount(followeeAlias, followee.getFollowersCount()+1);
        } else { // Unfollow
            // Remove the following relationship to database
            daoFactory.getFollowsDAO().delete(followerAlias, followeeAlias);

            // Decrement followers/following counts, respectively
            daoFactory.getUserDAO().updateFollowingCount(followerAlias, follower.getFollowingCount()-1);
            daoFactory.getUserDAO().updateFollowersCount(followeeAlias, followee.getFollowersCount()-1);
        }
        return new SimpleResponse(true);
    }

    private void verifyUsersExist(DBUser follower, DBUser followee) {
        if (follower == null) {
            throw new RuntimeException("[NotFound] Follower not found in database");
        }
        if (followee == null) {
            throw new RuntimeException("[NotFound] Followee not found in database");
        }
    }
}
