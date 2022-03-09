package edu.byu.cs.tweeter.server.service;

import edu.byu.cs.tweeter.model.net.request.FeedRequest;
import edu.byu.cs.tweeter.model.net.request.PostStatusRequest;
import edu.byu.cs.tweeter.model.net.request.StoryRequest;
import edu.byu.cs.tweeter.model.net.response.FeedResponse;
import edu.byu.cs.tweeter.model.net.response.PostStatusResponse;
import edu.byu.cs.tweeter.model.net.response.StoryResponse;
import edu.byu.cs.tweeter.server.dao.StatusDAO;

public class StatusService {
    public FeedResponse getFeed(FeedRequest request) {
//        if(request.getLastStatus() == null) {
//            throw new RuntimeException("[BadRequest] Request needs to have a status");
//        } else if(request.getLimit() <= 0) {
//            throw new RuntimeException("[BadRequest] Request needs to have a positive limit");
//        }
        if(request.getLimit() <= 0) {
            throw new RuntimeException("[BadRequest] Request needs to have a positive limit");
        }
        return getStatusDAO().getFeed(request);
    }

    public StoryResponse getStory(StoryRequest request) {
//        if(request.getLastStatus() == null) {
//            throw new RuntimeException("[BadRequest] Request needs to have a status");
//        } else if(request.getLimit() <= 0) {
//            throw new RuntimeException("[BadRequest] Request needs to have a positive limit");
//        }
        if(request.getLimit() <= 0) {
            throw new RuntimeException("[BadRequest] Request needs to have a positive limit");
        }
        return getStatusDAO().getStory(request);
    }

    StatusDAO getStatusDAO() {
        return new StatusDAO();
    }

    public PostStatusResponse postStatus(PostStatusRequest request) {
        if (request.getStatus() == null) {
            throw new RuntimeException("[BadRequest] Request needs to have a status");
        }
        if (request.getStatus().getUser() == null) {
            throw new RuntimeException(("[BadRequest] Request status needs to have a user"));
        }
        if (request.getStatus().getPost() == null) {
            throw new RuntimeException(("[BadRequest] Request status needs to have a post"));
        }

        return getStatusDAO().postStatus(request);
    }
}
