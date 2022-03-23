package edu.byu.cs.tweeter.server.dao;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.document.DynamoDB;

import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.server.dao.util.AuthTokenUtils;

public abstract class DynamoDAO {

    protected AuthTokenDAO authTokenDAO;

    public DynamoDAO(){}

    public DynamoDAO(AuthTokenDAO authTokenDAO) {
        this.authTokenDAO = authTokenDAO;
    }

    // DynamoDB client
    protected static AmazonDynamoDB amazonDynamoDB = AmazonDynamoDBClientBuilder
            .standard()
            .withRegion("us-west-2")
            .build();
    protected static DynamoDB dynamoDB = new DynamoDB(amazonDynamoDB);

    protected void verifyAuthToken(AuthToken authToken) {
        AuthToken dbAuthToken = authTokenDAO.getAuthToken(authToken.getToken());
        if (dbAuthToken == null) {
            throw new RuntimeException("[BadRequest] Access denied");
        }
        else if (AuthTokenUtils.verifyAuthToken(dbAuthToken)) {
            authTokenDAO.updateAuthToken(dbAuthToken); // Update with new timestamp
        } else {
            authTokenDAO.deleteAuthToken(dbAuthToken);
            throw new RuntimeException("[BadRequest] Access denied");
        }
    }
}
