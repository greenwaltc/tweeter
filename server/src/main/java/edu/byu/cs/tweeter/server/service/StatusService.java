package edu.byu.cs.tweeter.server.service;

import edu.byu.cs.tweeter.model.net.request.PostStatusRequest;
import edu.byu.cs.tweeter.model.net.request.StatusesRequest;
import edu.byu.cs.tweeter.model.net.response.StatusesResponse;
import edu.byu.cs.tweeter.model.net.response.SimpleResponse;
import edu.byu.cs.tweeter.model.net.response.StatusesResponse;
import edu.byu.cs.tweeter.server.dao.StatusDAO;

public class StatusService {
    public StatusesResponse getFeed(StatusesRequest request) {
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

    public StatusesResponse getStory(StatusesRequest request) {
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

    public SimpleResponse postStatus(PostStatusRequest request) {
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
