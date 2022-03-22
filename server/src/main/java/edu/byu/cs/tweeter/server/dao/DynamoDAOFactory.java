package edu.byu.cs.tweeter.server.dao;

public class DynamoDAOFactory implements DAOFactory{
    @Override
    public UserDAO getUserDAO() {
        return new DynamoUserDAO();
    }
}
