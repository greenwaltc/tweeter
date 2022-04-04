package edu.byu.cs.tweeter.server.lambda.sqs;

import com.amazonaws.services.dynamodbv2.xspec.S;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.SQSEvent;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import edu.byu.cs.tweeter.model.dto.PostStatusDTO;
import edu.byu.cs.tweeter.model.dto.UpdateFeedsDTO;
import edu.byu.cs.tweeter.server.dao.StatusDAO;
import edu.byu.cs.tweeter.server.dao.dynamo.DynamoFeedDAO;
import edu.byu.cs.tweeter.server.dao.util.JsonSerializer;

public class UpdateFeeds implements RequestHandler<SQSEvent, Void> {
    @Override
    public Void handleRequest(SQSEvent event, Context context) {

        StatusDAO feedDAO = new DynamoFeedDAO();

        for (SQSEvent.SQSMessage msg : event.getRecords()) {

            // Get batch list of PostStatusDTOs
            System.out.println("DEBUGTAG:" + msg.getBody());
            Type listType = new TypeToken<ArrayList<PostStatusDTO>>(){}.getType();
            List<PostStatusDTO> batch = new Gson().fromJson(msg.getBody(), listType);

            // DEBUG
            System.out.println("DEBUGTAG: " + batch.get(0).getAlias());

            // Insert the batch into the table
            feedDAO.insertBatch(batch);
        }
        return null;
    }
}
