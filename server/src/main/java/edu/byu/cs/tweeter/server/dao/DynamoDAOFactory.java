package edu.byu.cs.tweeter.server.dao;

public class DynamoDAOFactory implements DAOFactory{
    @Override
    public UserDAO getUserDAO() {
        return new DynamoUserDAO(this);
    }

    @Override
    public AuthTokenDAO getAuthTokenDAO() {
        return new DynamoAuthTokenDAO();
    }

    @Override
    public FollowDAO getFollowDAO() {
        return new DynamoFollowDAO(this);
    }
}
