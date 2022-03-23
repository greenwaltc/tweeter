package edu.byu.cs.tweeter.model.net.response;

import java.util.List;

import edu.byu.cs.tweeter.model.domain.User;

public class UsersResponse extends PagedResponse<User> {

    private UsersResponse(){}

    /**
     * Creates a response indicating that the corresponding request was unsuccessful. Sets the
     * success and more pages indicators to false.
     *
     * @param message a message describing why the request was unsuccessful.
     */
    public UsersResponse(String message) {
        super(false, message, false);
    }

    /**
     * Creates a response indicating that the corresponding request was successful.
     *
     * @param users the users to be included in the result.
     * @param hasMorePages an indicator of whether more data is available for the request.
     */
    public UsersResponse(List<User> users, boolean hasMorePages) {
        super(users, true, hasMorePages);
    }
}
