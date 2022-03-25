package edu.byu.cs.tweeter.server.dao.dynamo;

public class DynamoStoryDAO extends AbstractStatusDAO {

    private static final String TableName = "story";
    private static final String TablePrimaryKey = "senderalias";

    @Override
    protected String getTableName() {
        return TableName;
    }

    @Override
    protected String getTablePrimaryKey() {
        return TablePrimaryKey;
    }
}
