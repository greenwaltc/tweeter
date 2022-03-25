package edu.byu.cs.tweeter.server;

import edu.byu.cs.tweeter.server.dao.DAOFactory;
import edu.byu.cs.tweeter.server.dao.DynamoDAOFactory;

public class FactoryManager {
    public static DAOFactory getDAOFactory() {
        return new DynamoDAOFactory();
    }
}
