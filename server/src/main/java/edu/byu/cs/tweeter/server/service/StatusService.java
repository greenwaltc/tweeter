package edu.byu.cs.tweeter.server.service;

import edu.byu.cs.tweeter.model.net.request.PostStatusRequest;
import edu.byu.cs.tweeter.model.net.request.StatusesRequest;
import edu.byu.cs.tweeter.model.net.response.SimpleResponse;
import edu.byu.cs.tweeter.model.net.response.StatusesResponse;
import edu.byu.cs.tweeter.server.dao.DAOFactory;
import edu.byu.cs.tweeter.server.dao.DynamoStatusDAO;
import edu.byu.cs.tweeter.server.dao.StatusDAO;

public class StatusService {

    private StatusDAO statusDAO;

    public StatusService(DAOFactory daoFactory) {
        statusDAO = daoFactory.getStatusDAO();
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

        return statusDAO.postStatus(request);
    }

    public StatusesResponse getFeed(StatusesRequest request) {
        checkRequestLimit(request);
        return statusDAO.getFeed(request);
    }

    public StatusesResponse getStory(StatusesRequest request) {
        checkRequestLimit(request);
        return statusDAO.getStory(request);
    }

    private void checkRequestLimit(StatusesRequest request) {
        if(request.getLimit() <= 0) {
            throw new RuntimeException("[BadRequest] Request needs to have a positive limit");
        }
    }
}
