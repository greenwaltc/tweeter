package edu.byu.cs.tweeter.server.lambda;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;

import edu.byu.cs.tweeter.model.net.request.StatusesRequest;
import edu.byu.cs.tweeter.model.net.response.StatusesResponse;
import edu.byu.cs.tweeter.server.dao.dynamo.DynamoDAOFactory;
import edu.byu.cs.tweeter.server.service.StatusService;

public class GetStoryHandler implements RequestHandler<StatusesRequest, StatusesResponse> {
    @Override
    public StatusesResponse handleRequest(StatusesRequest request, Context context) {
        StatusService service = new StatusService(new DynamoDAOFactory());
        return service.getStory(request);
    }
}
