package edu.byu.cs.tweeter.server.dao;

import java.util.List;

import edu.byu.cs.tweeter.model.domain.Status;
import edu.byu.cs.tweeter.model.domain.User;
import edu.byu.cs.tweeter.model.net.request.StatusesRequest;
import edu.byu.cs.tweeter.util.Pair;

public interface StatusDAO {
    Pair<List<Status>, Boolean> getStatuses(StatusesRequest request);
    void insert(String alias, Status status);
    void insertBatch(List<User> users, Status status);
}
