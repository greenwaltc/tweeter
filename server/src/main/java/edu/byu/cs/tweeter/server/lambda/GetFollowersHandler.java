package edu.byu.cs.tweeter.server.lambda;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;

import edu.byu.cs.tweeter.model.net.request.UsersRequest;
import edu.byu.cs.tweeter.model.net.response.UsersResponse;
import edu.byu.cs.tweeter.server.service.FollowService;

public class GetFollowersHandler implements RequestHandler<UsersRequest, UsersResponse>  {
    @Override
    public UsersResponse handleRequest(UsersRequest request, Context context) {
        FollowService service = new FollowService();
        return service.getFollowers(request);
    }
}
