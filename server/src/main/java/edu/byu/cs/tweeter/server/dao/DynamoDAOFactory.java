package edu.byu.cs.tweeter.server.dao;

public class DynamoDAOFactory implements DAOFactory{

    private DynamoUserDAO dynamoUserDAO;
    private DynamoAuthTokenDAO dynamoAuthTokenDAO;
    private DynamoFeedDAO dynamoFeedDAO;
    private S3DAO s3DAO;
    private DynamoFollowsDAO dynamoFollowsDAO;
    private DynamoStoryDAO dynamoStoryDAO;

    @Override
    public UserDAO getUserDAO() {
        if (dynamoUserDAO == null) {
            dynamoUserDAO = new DynamoUserDAO();
        }
        return dynamoUserDAO;
    }

    @Override
    public AuthTokenDAO getAuthTokenDAO() {
        if (dynamoAuthTokenDAO == null) {
            dynamoAuthTokenDAO = new DynamoAuthTokenDAO();
        }
        return dynamoAuthTokenDAO;
    }

    @Override
    public FollowsDAO getFollowsDAO() {
        if (dynamoFollowsDAO == null) {
            dynamoFollowsDAO = new DynamoFollowsDAO();
        }
        return dynamoFollowsDAO;
    }

    @Override
    public StoryDAO getStoryDAO() {
        if (dynamoStoryDAO == null) {
            dynamoStoryDAO = new DynamoStoryDAO();
        }
        return dynamoStoryDAO;
    }

    @Override
    public BucketDAO getBucketDAO() {
        if (s3DAO == null) {
            s3DAO = new S3DAO();
        }
        return s3DAO;
    }

    @Override
    public FeedDAO getFeedDAO() {
        if (dynamoFeedDAO == null) {
            dynamoFeedDAO = new DynamoFeedDAO();
        }
        return dynamoFeedDAO;
    }
}
