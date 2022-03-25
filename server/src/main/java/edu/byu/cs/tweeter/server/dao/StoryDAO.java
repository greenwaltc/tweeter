package edu.byu.cs.tweeter.server.dao;

import java.util.List;

import edu.byu.cs.tweeter.model.domain.Status;
import edu.byu.cs.tweeter.model.net.request.StatusesRequest;
import edu.byu.cs.tweeter.util.Pair;

public interface StoryDAO {
    Pair<List<Status>, Boolean> getStory(StatusesRequest request);
    void insert(Status status);
}
