package edu.byu.cs.tweeter.model.net.request;

public abstract class AuthenticateRequest {

    protected String username;
    protected String password;

    protected AuthenticateRequest(){}

    public AuthenticateRequest(String username, String password) {
        this.username = username;
        this.password = password;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
