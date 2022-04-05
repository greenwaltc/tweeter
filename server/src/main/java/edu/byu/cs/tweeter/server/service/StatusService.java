package edu.byu.cs.tweeter.server.service;

import com.amazonaws.services.sqs.AmazonSQS;
import com.amazonaws.services.sqs.AmazonSQSClientBuilder;
import com.amazonaws.services.sqs.model.SendMessageRequest;
import com.amazonaws.services.sqs.model.SendMessageResult;

import java.util.List;

import edu.byu.cs.tweeter.model.domain.Status;
import edu.byu.cs.tweeter.model.dto.StatusDTO;
import edu.byu.cs.tweeter.model.net.request.PostStatusRequest;
import edu.byu.cs.tweeter.model.net.request.StatusesRequest;
import edu.byu.cs.tweeter.model.net.response.SimpleResponse;
import edu.byu.cs.tweeter.model.net.response.StatusesResponse;
import edu.byu.cs.tweeter.server.dao.DAOFactory;
import edu.byu.cs.tweeter.util.JsonSerializer;
import edu.byu.cs.tweeter.util.Pair;

public class StatusService extends Service{

    private final String postStatusQueueURL = "https://sqs.us-west-2.amazonaws.com/449579377599/PostStatusQueue";

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
        StatusDTO statusDTO = new StatusDTO(senderAlias, request.getStatus());
        daoFactory.getStoryDAO().insert(statusDTO);

        StatusDTO postStatusDTO = new StatusDTO(senderAlias, request.getStatus());

        String messageBody = JsonSerializer.serialize(postStatusDTO);

        SendMessageRequest send_msg_request = new SendMessageRequest()
            .withQueueUrl(postStatusQueueURL)
            .withMessageBody(messageBody);

        AmazonSQS sqs = AmazonSQSClientBuilder.defaultClient();
        SendMessageResult send_msg_result = sqs.sendMessage(send_msg_request);
        System.out.println("Message ID: " + send_msg_result.getMessageId()); // log to see if it work

        return new SimpleResponse(true);
    }

    public StatusesResponse getFeed(StatusesRequest request) {
        verifyStatusesRequest(request);

        Pair<List<Status>, Boolean> getFeedResult = daoFactory.getFeedDAO().
                getStatuses(request.getUserAlias(), request.getLimit(), request.getLastItem().getDate());
        List<Status> feed = getFeedResult.getFirst();
        Boolean hasMorePages = getFeedResult.getSecond();

        return new StatusesResponse(feed, hasMorePages);
    }

    public StatusesResponse getStory(StatusesRequest request) {
        verifyStatusesRequest(request);

        Pair<List<Status>, Boolean> getStoryResult = daoFactory.getStoryDAO()
                .getStatuses(request.getUserAlias(), request.getLimit(), request.getLastItem().getDate());
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
