package edu.byu.cs.tweeter.model.net.request;

public class RegisterRequest extends AuthenticateRequest{
    private String firstName, lastName, image;

    private RegisterRequest(){}

    public RegisterRequest(String firstName, String lastName, String username, String password, String image) {
        super(username, password);
        this.firstName = firstName;
        this.lastName = lastName;
        this.image = image;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }
}
