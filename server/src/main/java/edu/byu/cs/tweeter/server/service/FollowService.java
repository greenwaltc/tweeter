package edu.byu.cs.tweeter.server.service;

import java.util.ArrayList;
import java.util.List;

import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.User;
import edu.byu.cs.tweeter.model.dto.AuthTokenDTO;
import edu.byu.cs.tweeter.model.dto.FollowsDTO;
import edu.byu.cs.tweeter.model.dto.UserDTO;
import edu.byu.cs.tweeter.model.net.request.IsFollowerRequest;
import edu.byu.cs.tweeter.model.net.request.SimpleUserRequest;
import edu.byu.cs.tweeter.model.net.request.UsersRequest;
import edu.byu.cs.tweeter.model.net.response.IsFollowerResponse;
import edu.byu.cs.tweeter.model.net.response.SimpleResponse;
import edu.byu.cs.tweeter.model.net.response.UsersResponse;
import edu.byu.cs.tweeter.server.dao.DAOFactory;
import edu.byu.cs.tweeter.server.dao.dynamo.DynamoFollowsDAO;
import edu.byu.cs.tweeter.util.Pair;

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
    public UsersResponse getFollowing(UsersRequest request) {
        verifyUsersRequest(request);
        Pair<List<String>, Boolean> getFollowingResult =
                daoFactory.getFollowsDAO().getFollowing(request.getUserAlias(),
                        request.getLimit(),
                        request.getLastItem());

        List<String> followingAliases = getFollowingResult.getFirst();
        Boolean hasMorePages = getFollowingResult.getSecond();

        List<User> following = new ArrayList<>();

        for (String followingAlias : followingAliases) {
            User follower = daoFactory.getUserDAO().get(followingAlias).getUser();
            following.add(follower);
        }

        return new UsersResponse(following, hasMorePages);
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
        Pair<List<String>, Boolean> getFollowersResult = daoFactory.getFollowsDAO().getFollowers(request.getUserAlias(), request.getLimit(), request.getLastItem());

        List<String> followersAliases = getFollowersResult.getFirst();
        Boolean hasMorePages = getFollowersResult.getSecond();

        List<User> followers = new ArrayList<>();

        for (String followerAlias : followersAliases) {
            User follower = daoFactory.getUserDAO().get(followerAlias).getUser();
            followers.add(follower);
        }

        return new UsersResponse(followers, hasMorePages);
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
        UserDTO follower = daoFactory.getUserDAO().get(followerAlias);
        UserDTO followee = daoFactory.getUserDAO().get(followeeAlias);

        verifyUsersExist(follower, followee);

        if (daoFactory.getFollowsDAO().isFollower(followerAlias, followeeAlias)) {
            return new IsFollowerResponse(true, true);
        } else {
            return new IsFollowerResponse(true, false);
        }
    }

    public SimpleResponse updateFollowingRelationship(SimpleUserRequest request, boolean follow) {
        verifySimpleUserRequest(request);

        AuthTokenDTO dbAuthToken = daoFactory.getAuthTokenDAO().get(request.getAuthToken().getToken());
        String followerAlias = dbAuthToken.getAlias();
        String followeeAlias = request.getTargetUserAlias();

        // Verify both users exist
        UserDTO follower = daoFactory.getUserDAO().get(followerAlias);
        UserDTO followee = daoFactory.getUserDAO().get(followeeAlias);

        verifyUsersExist(follower, followee);

        if (follow) { // Follow
            // Insert the new following relationship to database
            FollowsDTO followsDTO = new FollowsDTO(followerAlias, followeeAlias);
            daoFactory.getFollowsDAO().insert(followsDTO);

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

    private void verifyUsersExist(UserDTO follower, UserDTO followee) {
        if (follower == null) {
            throw new RuntimeException("[NotFound] Follower not found in database");
        }
        if (followee == null) {
            throw new RuntimeException("[NotFound] Followee not found in database");
        }
    }
}
