import edu.byu.cs.tweeter.model.net.request.LoginRequest;
import edu.byu.cs.tweeter.model.net.request.RegisterRequest;
import edu.byu.cs.tweeter.model.net.request.UsersRequest;
import edu.byu.cs.tweeter.model.net.response.AuthenticateResponse;
import edu.byu.cs.tweeter.model.net.response.UsersResponse;
import edu.byu.cs.tweeter.server.dao.DynamoDAOFactory;
import edu.byu.cs.tweeter.server.service.FollowService;
import edu.byu.cs.tweeter.server.service.UserService;

public class main {
    public static void main(String[] args) {
        FollowService followService = new FollowService(new DynamoDAOFactory());
        UserService userService = new UserService(new DynamoDAOFactory());


        LoginRequest request = new LoginRequest("@FredFlintstone", "asdf");
        AuthenticateResponse response = userService.login(request);

        UsersRequest usersRequest1 = new UsersRequest(response.getAuthToken(), "@FredFlintstone", 10, null);
        UsersResponse usersResponse1 = followService.getFollowees(usersRequest1);

        UsersRequest usersRequest2 = new UsersRequest(response.getAuthToken(), "@FredFlintstone", 10, usersResponse1.getItems().get(usersResponse1.getItems().size() - 1).getAlias());
        UsersResponse usersResponse2 = followService.getFollowees(usersRequest2);

        UsersRequest usersRequest3 = new UsersRequest(response.getAuthToken(), "@FredFlintstone", 10, usersResponse2.getItems().get(usersResponse2.getItems().size() - 1).getAlias());
        UsersResponse usersResponse3 = followService.getFollowees(usersRequest3);

        System.out.println();
    }
}
