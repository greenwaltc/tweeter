package edu.byu.cs.tweeter.server.dao.dynamo;

public class DynamoFeedDAO extends AbstractStatusDAO {

    private static final String TableName = "feed";
    private static final String TablePrimaryKey = "receiveralias";

    @Override
    protected String getTableName() {
        return TableName;
    }

    @Override
    protected String getTablePrimaryKey() {
        return TablePrimaryKey;
    }
}
