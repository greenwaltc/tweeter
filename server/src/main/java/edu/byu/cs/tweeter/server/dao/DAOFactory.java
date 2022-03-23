package edu.byu.cs.tweeter.server.dao;

public interface DAOFactory {
    UserDAO getUserDAO();
    AuthTokenDAO getAuthTokenDAO();
    FollowsDAO getFollowsDAO();
    StoryDAO getStoryDAO();
    BucketDAO getBucketDAO();
    FeedDAO getFeedDAO();
}
