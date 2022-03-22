package edu.byu.cs.tweeter.server.lambda;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;

import edu.byu.cs.tweeter.model.net.request.SimpleUserRequest;
import edu.byu.cs.tweeter.model.net.response.CountResponse;
import edu.byu.cs.tweeter.server.service.FollowService;

public class GetFollowersCountHandler implements RequestHandler<SimpleUserRequest, CountResponse>  {
    @Override
    public CountResponse handleRequest(SimpleUserRequest request, Context context) {
        FollowService service = new FollowService();
        return service.getFollowersCount(request);
    }
}
