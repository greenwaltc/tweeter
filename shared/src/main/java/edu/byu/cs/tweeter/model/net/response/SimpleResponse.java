package edu.byu.cs.tweeter.model.net.response;

public class SimpleResponse extends Response{

    private SimpleResponse(){}

    public SimpleResponse(String message, boolean success) {
        super(success, message);
    }

    public SimpleResponse(boolean success) {
        super(success);
    }
}
