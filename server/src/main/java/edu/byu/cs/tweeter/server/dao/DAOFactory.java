package edu.byu.cs.tweeter.server.dao;

public interface DAOFactory {
    UserDAO getUserDAO();
    AuthTokenDAO getAuthTokenDAO();
    FollowDAO getFollowDAO();
    StatusDAO getStatusDAO();
}
