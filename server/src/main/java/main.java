import edu.byu.cs.tweeter.model.net.request.LoginRequest;
import edu.byu.cs.tweeter.server.dao.DynamoDAOFactory;
import edu.byu.cs.tweeter.server.dao.DynamoUserDAO;

public class main {
    public static void main(String[] args) {
        LoginRequest request = new LoginRequest("@afatchimp", "12345");
        DynamoUserDAO userDAO = new DynamoUserDAO(new DynamoDAOFactory());
        userDAO.login(request);
    }
}
