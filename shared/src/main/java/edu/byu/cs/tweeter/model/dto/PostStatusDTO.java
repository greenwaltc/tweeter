package edu.byu.cs.tweeter.model.dto;

import edu.byu.cs.tweeter.model.domain.Status;

public class PostStatusDTO {
    private String alias;
    private Status status;

    private PostStatusDTO(){}

    public PostStatusDTO(String alias, Status status) {
        this.alias = alias;
        this.status = status;
    }

    public String getAlias() {
        return alias;
    }

    public void setAlias(String alias) {
        this.alias = alias;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }
}
