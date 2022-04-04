package edu.byu.cs.tweeter.server.lambda.sqs;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.SQSEvent;
import com.amazonaws.services.sqs.AmazonSQS;
import com.amazonaws.services.sqs.AmazonSQSClientBuilder;
import com.amazonaws.services.sqs.model.SendMessageRequest;
import com.amazonaws.services.sqs.model.SendMessageResult;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

import edu.byu.cs.tweeter.model.domain.Status;
import edu.byu.cs.tweeter.model.dto.PostStatusDTO;
import edu.byu.cs.tweeter.server.dao.FollowsDAO;
import edu.byu.cs.tweeter.server.dao.dynamo.DynamoFollowsDAO;
import edu.byu.cs.tweeter.server.dao.util.JsonSerializer;
import edu.byu.cs.tweeter.util.Pair;

public class PostUpdateFeedMessages implements RequestHandler<SQSEvent, Void> {

    private final int maxBatchSize = 25; // AWS supports maximum batch size writes of 25
    private final String updateFeedQueueURL = "https://sqs.us-west-2.amazonaws.com/449579377599/UpdateFeedQueue";

    @Override
    public Void handleRequest(SQSEvent event, Context context) {

        for (SQSEvent.SQSMessage msg : event.getRecords()) {

            // Get the DTO object
            PostStatusDTO postStatusDTO = JsonSerializer.deserialize(msg.getBody(), PostStatusDTO.class);

            // Save sender and status
            String senderAlias = postStatusDTO.getAlias();
            Status status = postStatusDTO.getStatus();

            boolean hasMorePages = true;
            String lastReceiverAlias = null;
            FollowsDAO followsDAO = new DynamoFollowsDAO();
            List<PostStatusDTO> batch = new ArrayList<>();

            do {

                // Get the followers of the sender
                Pair<List<String>, Boolean> getFollowersOutcome =
                        followsDAO.getFollowers(senderAlias, maxBatchSize, lastReceiverAlias);

                List<String> batchReceivers = getFollowersOutcome.getFirst();
                hasMorePages = getFollowersOutcome.getSecond();

                // Don't bother if the user doesn't have any followers
                if (batchReceivers.size() == 0) {
                    break;
                }

                // Set last item for paginated querying
                lastReceiverAlias = batchReceivers.get(batchReceivers.size() - 1);

                // Add all the receivers w/ status to batch
                for (String receiverAlias : batchReceivers) {
                    batch.add(new PostStatusDTO(receiverAlias, status));
                }

                // Send the batch to the UpdateFeeds Queue
                String messageBody = new Gson().toJson(batch);

                SendMessageRequest send_msg_request = new SendMessageRequest()
                        .withQueueUrl(updateFeedQueueURL)
                        .withMessageBody(messageBody);

                AmazonSQS sqs = AmazonSQSClientBuilder.defaultClient();
                SendMessageResult send_msg_result = sqs.sendMessage(send_msg_request);

                // Reset batch
                batch = new ArrayList<>();

            } while (hasMorePages == true);
        }
        return null;
    }
}
