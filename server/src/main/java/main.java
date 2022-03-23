import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.net.request.LoginRequest;
import edu.byu.cs.tweeter.model.net.request.LogoutRequest;
import edu.byu.cs.tweeter.model.net.request.RegisterRequest;
import edu.byu.cs.tweeter.model.net.request.SimpleUserRequest;
import edu.byu.cs.tweeter.model.net.response.AuthenticateResponse;
import edu.byu.cs.tweeter.model.net.response.GetUserResponse;
import edu.byu.cs.tweeter.model.net.response.SimpleResponse;
import edu.byu.cs.tweeter.server.dao.DynamoDAOFactory;
import edu.byu.cs.tweeter.server.dao.DynamoUserDAO;
import edu.byu.cs.tweeter.server.service.UserService;

public class main {
    public static void main(String[] args) {
        RegisterRequest registerRequest = new RegisterRequest("asdf", "asdf", "@asdf", "asdf", "asdf");
        UserService service = new UserService(new DynamoDAOFactory());
//        AuthenticateResponse registerResponse = service.register(registerRequest);

        LoginRequest request = new LoginRequest("@asdf", "asdf");
        AuthenticateResponse response = service.login(request);
        System.out.println();
    }
}
