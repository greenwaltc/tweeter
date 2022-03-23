import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.net.request.LoginRequest;
import edu.byu.cs.tweeter.model.net.request.LogoutRequest;
import edu.byu.cs.tweeter.model.net.request.SimpleUserRequest;
import edu.byu.cs.tweeter.model.net.response.AuthenticateResponse;
import edu.byu.cs.tweeter.model.net.response.GetUserResponse;
import edu.byu.cs.tweeter.model.net.response.SimpleResponse;
import edu.byu.cs.tweeter.server.dao.DynamoDAOFactory;
import edu.byu.cs.tweeter.server.dao.DynamoUserDAO;

public class main {
    public static void main(String[] args) {
        LoginRequest request = new LoginRequest("@afatchimp", "1234");
        DynamoUserDAO userDAO = new DynamoUserDAO(new DynamoDAOFactory());
        AuthenticateResponse response = userDAO.login(request);


//        AuthToken authToken = response.getAuthToken();
//        SimpleUserRequest getUserRequest = new SimpleUserRequest(authToken, "test-alias");
//        GetUserResponse getUserResponse = userDAO.getUser(getUserRequest);
//        LogoutRequest logoutRequest = new LogoutRequest(authToken);
//        SimpleResponse logoutResponse = userDAO.logout(logoutRequest);
    }
}
