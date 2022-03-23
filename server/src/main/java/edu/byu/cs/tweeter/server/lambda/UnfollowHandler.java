package edu.byu.cs.tweeter.server.lambda;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;

import edu.byu.cs.tweeter.model.net.request.SimpleUserRequest;
import edu.byu.cs.tweeter.model.net.response.SimpleResponse;
import edu.byu.cs.tweeter.server.dao.DynamoDAOFactory;
import edu.byu.cs.tweeter.server.service.FollowService;

public class UnfollowHandler implements RequestHandler<SimpleUserRequest, SimpleResponse> {
    @Override
    public SimpleResponse handleRequest(SimpleUserRequest request, Context context) {
        FollowService service = new FollowService(new DynamoDAOFactory());
        return service.updateFollowingRelationship(request, false);
    }
}
