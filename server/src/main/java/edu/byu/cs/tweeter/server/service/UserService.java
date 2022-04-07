package edu.byu.cs.tweeter.server.service;

import java.util.List;

import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.User;
import edu.byu.cs.tweeter.model.dto.AuthTokenDTO;
import edu.byu.cs.tweeter.model.dto.UserDTO;
import edu.byu.cs.tweeter.model.net.request.LoginRequest;
import edu.byu.cs.tweeter.model.net.request.LogoutRequest;
import edu.byu.cs.tweeter.model.net.request.RegisterRequest;
import edu.byu.cs.tweeter.model.net.request.SimpleUserRequest;
import edu.byu.cs.tweeter.model.net.response.AuthenticateResponse;
import edu.byu.cs.tweeter.model.net.response.CountResponse;
import edu.byu.cs.tweeter.model.net.response.GetUserResponse;
import edu.byu.cs.tweeter.model.net.response.SimpleResponse;
import edu.byu.cs.tweeter.server.dao.DAOFactory;
import edu.byu.cs.tweeter.server.service.util.AuthTokenUtils;
import edu.byu.cs.tweeter.server.service.util.PasswordHasher;
import edu.byu.cs.tweeter.util.Pair;

public class UserService extends Service{

    private static final String USER_ALIAS_NOT_FOUND = "User with given alias not found";
    private static final String UNAME_OR_PASS_INCORR = "Username or password incorrect";

    public UserService(DAOFactory daoFactory) {
        super(daoFactory);
    }

    public AuthenticateResponse register(RegisterRequest request) {
        if(request.getUsername() == null){
            throw new RuntimeException("[BadRequest] Missing a username");
        } else if(request.getPassword() == null) {
            throw new RuntimeException("[BadRequest] Missing a password");
        } else if (request.getFirstName() == null) {
            throw new RuntimeException("[BadRequest] Missing a first name");
        } else if (request.getLastName() == null) {
            throw new RuntimeException("[BadRequest] Missing a last name");
        } else if (request.getImage() == null) {
            throw new RuntimeException("[BadRequest] Missing an image");
        }

        // Verify that the provided alias is not already taken
        UserDTO dbUser = daoFactory.getUserDAO().get(request.getUsername());
        if (dbUser != null) {
            return new AuthenticateResponse(false, "Username already taken");
        }

        // Add image to s3 and get the URL of the profile picture object
        String profilePhotoURL = daoFactory.getBucketDAO().uploadImage(request.getUsername(), request.getImage());

        // Insert new user into the database
        User newUser = new User(request.getFirstName(), request.getLastName(), request.getUsername(), profilePhotoURL);
        // Hash the password
        Pair<byte[], byte[]> hashSaltPair = PasswordHasher.hashPassword(request.getPassword());

        UserDTO userDTO = new UserDTO(newUser, hashSaltPair.getFirst(), hashSaltPair.getSecond(), 0, 0);
        daoFactory.getUserDAO().insert(userDTO);

        // Success. Add new authToken to database
        AuthTokenDTO authTokenDTO = AuthTokenUtils.generateAuthToken(newUser.getAlias());
        daoFactory.getAuthTokenDAO().insert(authTokenDTO);

        // Return successful response
        return new AuthenticateResponse(newUser, authTokenDTO.getAuthToken(), true);
    }

    public AuthenticateResponse login(LoginRequest request) {
        if(request.getUsername() == null){
            throw new RuntimeException("[BadRequest] Missing a username");
        } else if(request.getPassword() == null) {
            throw new RuntimeException("[BadRequest] Missing a password");
        }

        // Verify user exists in database
        UserDTO dbUser = daoFactory.getUserDAO().get(request.getUsername());
        if (dbUser == null) {
            return new AuthenticateResponse(false, UNAME_OR_PASS_INCORR); // Generic message for information hiding
        }

        // Verify that the passwords are the same
        String loginPassword = request.getPassword();
        byte[] hash = (byte[]) dbUser.getHash();
        byte[] salt = (byte[]) dbUser.getSalt();

        if (!PasswordHasher.verifyPassword(loginPassword, salt, hash)) { // Unsuccessful login attempt
            return new AuthenticateResponse(false, UNAME_OR_PASS_INCORR);
        }

        // Success. Add new authToken to database
        AuthTokenDTO authTokenDTO = AuthTokenUtils.generateAuthToken(dbUser.getUser().getAlias());
        daoFactory.getAuthTokenDAO().insert(authTokenDTO);

        // Return successful response
        return new AuthenticateResponse(dbUser.getUser(), authTokenDTO.getAuthToken(), true);
    }

    public GetUserResponse getUser(SimpleUserRequest request) {
        UserDTO dbUser = getTargetUser(request);
        return new GetUserResponse(true, dbUser.getUser());
    }

    public SimpleResponse logout(LogoutRequest request) {
        if (request.getAuthToken() == null) {
            throw new RuntimeException("[BadRequest] Missing authToken");
        }
        verifyAuthToken(request.getAuthToken());
        return new SimpleResponse(true);
    }

    public CountResponse getFollowersCount(SimpleUserRequest request) {
        UserDTO dbUser = getTargetUser(request);
        return new CountResponse(true, dbUser.getFollowersCount());
    }

    public CountResponse getFollowingCount(SimpleUserRequest request) {
        UserDTO dbUser = getTargetUser(request);
        return new CountResponse(true, dbUser.getFollowingCount());
    }

    private UserDTO getTargetUser(SimpleUserRequest request) {
        verifySimpleUserRequest(request);
        UserDTO dbUser = daoFactory.getUserDAO().get(request.getTargetUserAlias());
        if (dbUser == null) {
            throw new RuntimeException("[NotFound] " + USER_ALIAS_NOT_FOUND);
        }
        return dbUser;
    }
}
