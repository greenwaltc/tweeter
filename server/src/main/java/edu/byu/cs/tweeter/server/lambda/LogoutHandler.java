package edu.byu.cs.tweeter.server.lambda;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;

import edu.byu.cs.tweeter.model.net.request.LogoutRequest;
import edu.byu.cs.tweeter.model.net.response.SimpleResponse;
import edu.byu.cs.tweeter.server.dao.dynamo.DynamoDAOFactory;
import edu.byu.cs.tweeter.server.service.UserService;

public class LogoutHandler implements RequestHandler<LogoutRequest, SimpleResponse> {
    @Override
    public SimpleResponse handleRequest(LogoutRequest request, Context context) {
        UserService service = new UserService(new DynamoDAOFactory());
        return service.logout(request);
    }
}
