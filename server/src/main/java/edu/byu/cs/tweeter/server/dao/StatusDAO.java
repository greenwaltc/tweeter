package edu.byu.cs.tweeter.server.dao;

import edu.byu.cs.tweeter.model.net.request.PostStatusRequest;
import edu.byu.cs.tweeter.model.net.request.StatusesRequest;
import edu.byu.cs.tweeter.model.net.response.SimpleResponse;
import edu.byu.cs.tweeter.model.net.response.StatusesResponse;

public interface StatusDAO {
    StatusesResponse getFeed(StatusesRequest request);
    StatusesResponse getStory(StatusesRequest request);
    SimpleResponse postStatus(PostStatusRequest request);
}
