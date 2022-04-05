package edu.byu.cs.tweeter.server.dao;

import java.util.List;

import edu.byu.cs.tweeter.model.domain.Status;
import edu.byu.cs.tweeter.model.dto.StatusDTO;
import edu.byu.cs.tweeter.model.net.request.StatusesRequest;
import edu.byu.cs.tweeter.util.Pair;

public interface StatusDAO extends BatchDAO{
    Pair<List<Status>, Boolean> getStatuses(String alias, int limit, String lastItemTimestamp);
}
