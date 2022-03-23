package edu.byu.cs.tweeter.server.lambda;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;

import edu.byu.cs.tweeter.model.net.request.SimpleUserRequest;
import edu.byu.cs.tweeter.model.net.response.CountResponse;
import edu.byu.cs.tweeter.server.dao.DynamoDAOFactory;
import edu.byu.cs.tweeter.server.service.UserService;

public class GetFollowersCountHandler implements RequestHandler<SimpleUserRequest, CountResponse>  {
    @Override
    public CountResponse handleRequest(SimpleUserRequest request, Context context) {
        UserService service = new UserService(new DynamoDAOFactory());
        return service.getFollowersCount(request);
    }
}
