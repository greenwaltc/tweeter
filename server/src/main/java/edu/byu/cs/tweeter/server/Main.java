package edu.byu.cs.tweeter.server;

import edu.byu.cs.tweeter.model.net.request.RegisterRequest;
import edu.byu.cs.tweeter.server.dao.DynamoUserDAO;

public class Main {
    public static void main(String[] args) {
        // todo: write test for registration
        DynamoUserDAO userDAO = new DynamoUserDAO();
        RegisterRequest request = new RegisterRequest("Cameron", "Greenwalt", "@afatchimp", "nine9998", "asdfasdfasdf");
        userDAO.register(request);
    }
}
