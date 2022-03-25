package edu.byu.cs.tweeter.server.service;

import java.util.List;

import edu.byu.cs.tweeter.model.domain.Status;
import edu.byu.cs.tweeter.model.domain.User;
import edu.byu.cs.tweeter.model.net.request.PostStatusRequest;
import edu.byu.cs.tweeter.model.net.request.StatusesRequest;
import edu.byu.cs.tweeter.model.net.request.UsersRequest;
import edu.byu.cs.tweeter.model.net.response.SimpleResponse;
import edu.byu.cs.tweeter.model.net.response.StatusesResponse;
import edu.byu.cs.tweeter.server.dao.DAOFactory;
import edu.byu.cs.tweeter.util.Pair;

public class StatusService extends Service{

    public StatusService(DAOFactory daoFactory) {
        super(daoFactory);
    }

    public SimpleResponse postStatus(PostStatusRequest request) {
        if (request.getStatus() == null) {
            throw new RuntimeException("[BadRequest] Request needs to have a status");
        }
        if (request.getStatus().getUser() == null) {
            throw new RuntimeException(("[BadRequest] Request status needs to have a user"));
        }
        if (request.getStatus().getPost() == null) {
            throw new RuntimeException(("[BadRequest] Request status needs to have a post"));
        }

        verifyAuthToken(request.getAuthToken());
        String senderAlias = request.getStatus().getUser().getAlias();

        // Insert post into the user's story
        daoFactory.getStoryDAO().insert(senderAlias, request.getStatus());

        boolean hasMorePages = true;
        String lastReceiverAlias = null;
        do {
            // Get the aliases of users following the sender
            UsersRequest usersRequest = new UsersRequest(request.getAuthToken(), senderAlias, 25, lastReceiverAlias);
            Pair<List<User>, Boolean> getFollowersOutcome = daoFactory.getFollowsDAO().getFollowers(usersRequest);

            List<User> batchReceivers = getFollowersOutcome.getFirst();
            hasMorePages = getFollowersOutcome.getSecond();
            lastReceiverAlias = batchReceivers.get(batchReceivers.size() - 1).getAlias();

            // Add feed status to table for current batch of followers
            daoFactory.getFeedDAO().insertBatch(batchReceivers, request.getStatus());

        } while (hasMorePages == true);

        return new SimpleResponse(true);
    }

    public StatusesResponse getFeed(StatusesRequest request) {
        verifyStatusesRequest(request);

        Pair<List<Status>, Boolean> getFeedResult = daoFactory.getFeedDAO().getStatuses(request);
        List<Status> feed = getFeedResult.getFirst();
        Boolean hasMorePages = getFeedResult.getSecond();

        return new StatusesResponse(feed, hasMorePages);
    }

    public StatusesResponse getStory(StatusesRequest request) {
        verifyStatusesRequest(request);

        Pair<List<Status>, Boolean> getStoryResult = daoFactory.getStoryDAO().getStatuses(request);
        List<Status> story = getStoryResult.getFirst();
        Boolean hasMorePages = getStoryResult.getSecond();

        return new StatusesResponse(story, hasMorePages);
    }

    private void verifyStatusesRequest(StatusesRequest request) {
        if(request.getLimit() <= 0) {
            throw new RuntimeException("[BadRequest] Request needs to have a positive limit");
        }
        verifyAuthToken(request.getAuthToken());
    }
}
