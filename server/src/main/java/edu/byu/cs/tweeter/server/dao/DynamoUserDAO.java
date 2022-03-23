package edu.byu.cs.tweeter.server.dao;

import com.amazonaws.services.dynamodbv2.document.Item;
import com.amazonaws.services.dynamodbv2.document.PutItemOutcome;
import com.amazonaws.services.dynamodbv2.document.Table;
import com.amazonaws.services.dynamodbv2.document.spec.GetItemSpec;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.Bucket;
import com.amazonaws.services.s3.model.ObjectMetadata;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.Base64;

import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.User;
import edu.byu.cs.tweeter.model.net.request.LoginRequest;
import edu.byu.cs.tweeter.model.net.request.LogoutRequest;
import edu.byu.cs.tweeter.model.net.request.RegisterRequest;
import edu.byu.cs.tweeter.model.net.request.SimpleUserRequest;
import edu.byu.cs.tweeter.model.net.response.AuthenticateResponse;
import edu.byu.cs.tweeter.model.net.response.GetUserResponse;
import edu.byu.cs.tweeter.model.net.response.SimpleResponse;
import edu.byu.cs.tweeter.server.dao.util.AuthTokenUtils;
import edu.byu.cs.tweeter.server.dao.util.CreateBucket;
import edu.byu.cs.tweeter.server.dao.util.PasswordHasher;
import edu.byu.cs.tweeter.util.FakeData;
import edu.byu.cs.tweeter.util.Pair;

public class DynamoUserDAO extends DynamoDAO implements UserDAO {

    private static final String Region = "us-west-2";

    private static final String TableName = "users";
    private static final String TablePrimaryKey = "alias",
            TableFirstNameKey = "first_name",
            TableLastNameKey = "last_name",
            TablePasswordKey = "password",
            TableSaltKey = "salt",
            TableImageURLKey = "image_url";
    private static final String ProfilePhotosBucketName = "cs340cgreenw5-tweeter-profile-photos";
    
    private AuthTokenDAO authTokenDAO;
    
    public DynamoUserDAO(DAOFactory daoFactory) {
        this.authTokenDAO = daoFactory.getAuthTokenDAO();
    }

    @Override
    public AuthenticateResponse register(RegisterRequest request) {

        // Verify that the provided alias is not already taken
        Item getUserOutcome = getUserByAlias(request.getUsername());
        if (getUserOutcome != null) {
            return new AuthenticateResponse(false, "Username already taken");
        }

        // Add image to s3 and get the URL of the profile picture object
        String profilePhotoURL = addImageToS3(request.getUsername(), request.getImage());

        // Insert new user into the database
        insertNewUser(request, profilePhotoURL);

        return createSuccessAuthenticateResponse(request.getFirstName(), request.getLastName(),
                request.getUsername(), profilePhotoURL);
    }

    @Override
    public AuthenticateResponse login(LoginRequest request) {
        // Verify user exists in database
        Item getUserOutcome = getUserByAlias(request.getUsername());
        if (getUserOutcome == null) {
            return new AuthenticateResponse(false, "Username or password incorrect"); // Generic message for information hiding
        }

        // Verify that the passwords are the same
        String loginPassword = request.getPassword();
        byte[] databaseHash = (byte[]) getUserOutcome.get(TablePasswordKey);
        byte[] salt = (byte[]) getUserOutcome.get(TableSaltKey);

        if (!PasswordHasher.verifyPassword(loginPassword, salt, databaseHash)) { // Unsuccessful login attempt
            return new AuthenticateResponse(false, "Username or password incorrect");
        }

        return createSuccessAuthenticateResponse((String) getUserOutcome.get(TableFirstNameKey),
                (String) getUserOutcome.get(TableLastNameKey),
                (String) getUserOutcome.get(TablePrimaryKey),
                (String) getUserOutcome.get(TableImageURLKey));
    }

    @Override
    public GetUserResponse getUser(SimpleUserRequest request) {

        // Verify authToken
        AuthToken authToken = authTokenDAO.getAuthToken(request.getAuthToken().getToken());
        if (authToken == null) {
            throw new RuntimeException("[BadRequest] Access denied");
        }
        else if (AuthTokenUtils.verifyAuthToken(authToken)) {
            authTokenDAO.updateAuthToken(authToken); // Update with new timestamp
        } else {
            authTokenDAO.deleteAuthToken(authToken);
            throw new RuntimeException("[BadRequest] Access denied");
        }

        Item getUserOutcome = getUserByAlias(request.getTargetUserAlias());
        if (getUserOutcome == null) {
            return new GetUserResponse(false, "User with given alias not found");
        }

        return new GetUserResponse(true,
                new User((String) getUserOutcome.get(TableFirstNameKey),
                        (String) getUserOutcome.get(TableLastNameKey),
                        (String) getUserOutcome.get(TablePrimaryKey),
                        (String) getUserOutcome.get(TableImageURLKey)));
    }

    @Override
    public SimpleResponse follow(SimpleUserRequest request) {

        // todo: Uses dummy data, replace with real implementation
        return new SimpleResponse(true);
    }

    @Override
    public SimpleResponse unfollow(SimpleUserRequest request) {

        // todo: Uses dummy data, replace with real implementation
        return new SimpleResponse(true);
    }

    @Override
    public SimpleResponse logout(LogoutRequest request) {
        return new SimpleResponse(true);
    }

    private String addImageToS3(String key, String contents) {
        // Converting String to image byte array
        byte[] imageBytes = Base64.getDecoder().decode(contents);
        InputStream stream = new ByteArrayInputStream(imageBytes);
        ObjectMetadata meta = new ObjectMetadata();
        meta.setContentLength(imageBytes.length);
        meta.setContentType("image/jpeg");

        // Create the s3 bucket for user's profile photos if not already created, otherwise return
        // the previously created bucket.
        Bucket profilePicBucket = CreateBucket.createBucket(ProfilePhotosBucketName);

        // Add the string profile photo to the bucket
        // https://stackoverflow.com/questions/2499132/how-to-write-a-string-to-amazon-s3-bucket
        AmazonS3 s3client = AmazonS3ClientBuilder
                .standard()
                .withRegion(Region)
                .build();
        s3client.putObject(ProfilePhotosBucketName, key, stream, meta);

        // Get the URL of the profile picture object in the s3 bucket
        return s3client.getUrl(ProfilePhotosBucketName, key).toString();
    }

    private void insertNewUser(RegisterRequest request, String profilePhotoURL) {
        // Add the new user to the users table
        String firstName = request.getFirstName(),
                lastName = request.getLastName(),
                alias = request.getUsername(),
                plainTxtPassword = request.getPassword();

        // Hash the password
        Pair<byte[], byte[]> hashSaltPair = PasswordHasher.hashPassword(plainTxtPassword);
        byte[] hash = hashSaltPair.getFirst();
        byte[] salt = hashSaltPair.getSecond();

        // Add new user to the users table
        Table userTable = dynamoDB.getTable(TableName);

        try {
            PutItemOutcome outcome = userTable
                    .putItem(new Item().withPrimaryKey(TablePrimaryKey, alias)
                            .withString(TableFirstNameKey, firstName)
                            .withString(TableLastNameKey, lastName)
                            .withBinary(TablePasswordKey, hash)
                            .withBinary(TableSaltKey, salt)
                            .withString(TableImageURLKey, profilePhotoURL));
        }
        catch (Exception e) {
            throw new RuntimeException("[ServerError] Unable to add new user to database");
        }
    }

    private Item getUserByAlias(String alias) {
        Table userTable = dynamoDB.getTable(TableName);
        GetItemSpec spec = new GetItemSpec()
                .withPrimaryKey(TablePrimaryKey, alias);

        try {
            Item getUserOutcome = userTable.getItem(spec);
            return getUserOutcome;
        } catch (Exception e) {
            throw new RuntimeException("[ServerError] Unable to read from users table");
        }
    }

    private AuthenticateResponse createSuccessAuthenticateResponse(String firstName, String lastName, String username, String imageURL) {
        // Success. Add new authToken to database
        AuthToken authToken = AuthTokenUtils.generateAuthToken();
        authTokenDAO.insertAuthToken(authToken);

        // Return successful response
        User user = new User(firstName, lastName, username, imageURL);
        return new AuthenticateResponse(user, authToken, true);
    }

    FakeData getFakeData() {
        return new FakeData();
    }
}
