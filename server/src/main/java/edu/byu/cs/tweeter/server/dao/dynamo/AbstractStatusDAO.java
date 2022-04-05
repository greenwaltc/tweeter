package edu.byu.cs.tweeter.server.dao.dynamo;

import com.amazonaws.services.dynamodbv2.document.Item;
import com.amazonaws.services.dynamodbv2.document.ItemCollection;
import com.amazonaws.services.dynamodbv2.document.PrimaryKey;
import com.amazonaws.services.dynamodbv2.document.QueryOutcome;
import com.amazonaws.services.dynamodbv2.document.TableWriteItems;
import com.amazonaws.services.dynamodbv2.document.api.QueryApi;
import com.amazonaws.services.dynamodbv2.document.spec.QuerySpec;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.amazonaws.services.dynamodbv2.model.QueryResult;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import edu.byu.cs.tweeter.model.domain.Status;
import edu.byu.cs.tweeter.model.dto.StatusDTO;
import edu.byu.cs.tweeter.model.net.request.StatusesRequest;
import edu.byu.cs.tweeter.server.dao.StatusDAO;
import edu.byu.cs.tweeter.util.JsonSerializer;
import edu.byu.cs.tweeter.util.Pair;

public abstract class AbstractStatusDAO extends DynamoDAO implements StatusDAO {

    protected static final String TableSortKey = "timestamp", TableStatusKey = "status";

    protected abstract String getTablePrimaryKey();

    public Pair<List<Status>, Boolean> getStatuses(String alias, int limit, String lastItemValue) {

        PagedQueryStrategy<Status> getStatusesStrategy = new PagedQueryStrategy<Status>() {

            @Override
            public String getTableName() {
                return getTableName();
            }

            @Override
            public String getQuerySpecKeyConditionalExpression() {
                return getTablePrimaryKey() + " = :handle";
            }

            @Override
            public boolean isScanIndexForward() {
                return false;
            }

            @Override
            public PrimaryKey getExclusiveStartKey() {
                return new PrimaryKey(getTablePrimaryKey(), alias, TableSortKey, lastItemValue);
            }

            @Override
            public void insertItem(List<Status> queriedItems, Item item) {
                String statusJson = item.getJSON(TableStatusKey);
                Status status = JsonSerializer.deserialize(statusJson, Status.class);
                queriedItems.add(status);
            }

            @Override
            public QueryApi getQueriable() {
                return table;
            }
        };
        return PagedQuery.getItems(getStatusesStrategy, alias, limit, lastItemValue);
    }

    @Override
    protected Item batchCreateItem(Object ob) {
        StatusDTO statusDTO = (StatusDTO) ob;
        return new Item()
                .withPrimaryKey(getTablePrimaryKey(), statusDTO.getAlias(),
                            TableSortKey, statusDTO.getStatus().getDate())
                .withJSON(TableStatusKey, JsonSerializer.serialize(statusDTO.getStatus()));
    }
}
