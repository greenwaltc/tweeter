package edu.byu.cs.tweeter.server.dao;

public interface DAOFactory {
    UserDAO getUserDAO();
    AuthTokenDAO getAuthTokenDAO();
    FollowsDAO getFollowsDAO();
    StatusDAO getStoryDAO();
    BucketDAO getBucketDAO();
    StatusDAO getFeedDAO();
}
