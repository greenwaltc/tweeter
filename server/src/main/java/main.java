import edu.byu.cs.tweeter.model.domain.Status;
import edu.byu.cs.tweeter.model.domain.User;
import edu.byu.cs.tweeter.model.net.request.LoginRequest;
import edu.byu.cs.tweeter.model.net.request.SimpleUserRequest;
import edu.byu.cs.tweeter.model.net.request.StatusesRequest;
import edu.byu.cs.tweeter.model.net.response.AuthenticateResponse;
import edu.byu.cs.tweeter.model.net.response.GetUserResponse;
import edu.byu.cs.tweeter.model.net.response.StatusesResponse;
import edu.byu.cs.tweeter.server.dao.dynamo.DynamoDAOFactory;
import edu.byu.cs.tweeter.server.service.FollowService;
import edu.byu.cs.tweeter.server.service.StatusService;
import edu.byu.cs.tweeter.server.service.UserService;

public class main {

    public static void main(String[] args) {
        DynamoDAOFactory dynamoDAOFactory = new DynamoDAOFactory();
        FollowService followService = new FollowService(dynamoDAOFactory);
        UserService userService = new UserService(dynamoDAOFactory);
        StatusService statusService = new StatusService(dynamoDAOFactory);

        LoginRequest request = new LoginRequest("@JoeyLittle", "password");
        AuthenticateResponse loginResponse = userService.login(request);

        SimpleUserRequest getUserRequest = new SimpleUserRequest(loginResponse.getAuthToken(), "@JoeyLittle");
        GetUserResponse getUserResponse = userService.getUser(getUserRequest);
        User user = getUserResponse.getUser();

//        PostStatusRequest postStatusRequest = new PostStatusRequest(loginResponse.getAuthToken(),
//                new Status("This is a test post",
//                        user,
//                        (new Timestamp(System.currentTimeMillis())).toString(),
//                        null, null));
//        SimpleResponse postStatusResponse = statusService.postStatus(postStatusRequest);


        StatusesRequest statusesRequest = new StatusesRequest(loginResponse.getAuthToken(), "@JoeyLittle", 10, null);
        StatusesResponse storyResponse = statusService.getStory(statusesRequest);
        Status storyStatus = storyResponse.getItems().get(0);
        System.out.println();
    }
}
