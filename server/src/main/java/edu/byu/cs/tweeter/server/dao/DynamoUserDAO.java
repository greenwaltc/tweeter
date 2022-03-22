package edu.byu.cs.tweeter.server.dao;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.document.DynamoDB;
import com.amazonaws.services.dynamodbv2.document.Item;
import com.amazonaws.services.dynamodbv2.document.PutItemOutcome;
import com.amazonaws.services.dynamodbv2.document.Table;
import com.amazonaws.services.dynamodbv2.document.spec.GetItemSpec;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.AmazonS3Exception;
import com.amazonaws.services.s3.model.Bucket;

import java.sql.Timestamp;
import java.util.List;
import java.util.UUID;

import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.User;
import edu.byu.cs.tweeter.model.net.request.LoginRequest;
import edu.byu.cs.tweeter.model.net.request.LogoutRequest;
import edu.byu.cs.tweeter.model.net.request.RegisterRequest;
import edu.byu.cs.tweeter.model.net.request.SimpleUserRequest;
import edu.byu.cs.tweeter.model.net.response.AuthenticateResponse;
import edu.byu.cs.tweeter.model.net.response.GetUserResponse;
import edu.byu.cs.tweeter.model.net.response.SimpleResponse;
import edu.byu.cs.tweeter.util.FakeData;

public class DynamoUserDAO implements UserDAO{

    private static final String Region = "us-west-2";

    private static final String UsersTableName = "users";
    private static final String UsersTablePrimaryKey = "alias",
            UsersTableFirstNameKey = "first_name",
            UsersTableLastNameKey = "last_name",
            UsersTablePasswordKey = "password",
            UsersTableImageURLKey = "image_url";
    private static final String ProfilePhotosBucketName = "cs340cgreenw5-tweeter-profile-photos";

    private static final String AuthtokenTableName = "authtoken";
    private static final String AuthTokenTablePrimaryKey = "token_value",
            AuthTokenTableDatetimeKey = "datetime";

    // DynamoDB client
    private static AmazonDynamoDB amazonDynamoDB = AmazonDynamoDBClientBuilder
            .standard()
            .withRegion("us-west-2")
            .build();
    private static DynamoDB dynamoDB = new DynamoDB(amazonDynamoDB);

    @Override
    public AuthenticateResponse register(RegisterRequest request) {

        // Verify that the provided alias is not already taken
        Table userTable = dynamoDB.getTable(UsersTableName);

        GetItemSpec spec = new GetItemSpec()
                .withPrimaryKey(UsersTablePrimaryKey, request.getUsername());

        Item getUserOutcome = null;
        try {
            getUserOutcome = userTable.getItem(spec);
        } catch (Exception e) {
            System.out.println(e.getMessage());
            throw new RuntimeException("[ServerError] Unable to read from users table");
        }
        if (getUserOutcome != null) {
            throw new RuntimeException("[BadRequest] Username already taken");
        }

        // Create the s3 bucket for user's profile photos if not already created, otherwise return
        // the previously created bucket.
        Bucket profilePicBucket = CreateBucket.createBucket(ProfilePhotosBucketName);

        // Add the string profile photo to the bucket
        // https://stackoverflow.com/questions/2499132/how-to-write-a-string-to-amazon-s3-bucket
        String key = request.getUsername();
        String contents = request.getImage();
        AmazonS3 s3client = AmazonS3ClientBuilder
                .standard()
                .withRegion(Region)
                .build();
        s3client.putObject(ProfilePhotosBucketName, key, contents);

        // Get the URL of the profile picture object in the s3 bucket
        String profilePhotoURL = s3client.getUrl(ProfilePhotosBucketName, key).toString();

        // Add the new user to the users table
        // todo: hash and salt passwords
        String firstName = request.getFirstName(),
                lastName = request.getLastName(),
                alias = request.getUsername(),
                password = request.getPassword();

        try {
            PutItemOutcome outcome = userTable
                    .putItem(new Item().withPrimaryKey(UsersTablePrimaryKey, alias)
                            .withString(UsersTableFirstNameKey, firstName)
                            .withString(UsersTableLastNameKey, lastName)
                            .withString(UsersTablePasswordKey, password)
                            .withString(UsersTableImageURLKey, profilePhotoURL));
        }
        catch (Exception e) {
            throw new RuntimeException("[ServerError] Unable to add new user to database");
        }

        // Everything successful. Create an authtoken, insert it into the database, and return a successful response object
        String tokenValue = UUID.randomUUID().toString();
        String datetime = new Timestamp(System.currentTimeMillis()).toString();

        AuthToken authToken = new AuthToken(tokenValue, datetime);
        User user = new User(firstName, lastName, alias, profilePhotoURL);
        AuthenticateResponse response = new AuthenticateResponse(user, authToken, true);

        Table authtokenTable = dynamoDB.getTable(AuthtokenTableName);

        try {
            PutItemOutcome outcome = authtokenTable
                    .putItem(new Item().withPrimaryKey(AuthTokenTablePrimaryKey, tokenValue)
                            .withString(AuthTokenTableDatetimeKey, datetime));
        }
        catch (Exception e) {
            throw new RuntimeException("[ServerError] Unable to add authtoken to database");
        }

        return response;
    }

    FakeData getFakeData() {
        return new FakeData();
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
    public GetUserResponse getUser(SimpleUserRequest request) {
        // todo: Uses dummy data, replace with real implementation
        assert request.getTargetUserAlias() != null;

        GetUserResponse response = new GetUserResponse(true,
                getFakeData().findUserByAlias(request.getTargetUserAlias()));

        return response;
    }

    @Override
    public SimpleResponse logout(LogoutRequest request) {
        return new SimpleResponse(true);
    }

    @Override
    public AuthenticateResponse login(LoginRequest request) {
        return null;
    }


    private static class CreateBucket {
        public static Bucket getBucket(String bucket_name) {
            final AmazonS3 s3 = AmazonS3ClientBuilder
                    .standard()
                    .withRegion(Region)
                    .build();
            Bucket named_bucket = null;
            List<Bucket> buckets = s3.listBuckets();
            for (Bucket b : buckets) {
                if (b.getName().equals(bucket_name)) {
                    named_bucket = b;
                }
            }
            return named_bucket;
        }

        public static Bucket createBucket(String bucket_name) {
            final AmazonS3 s3 = AmazonS3ClientBuilder
                    .standard()
                    .withRegion(Region)
                    .build();
            Bucket b = null;
            if (s3.doesBucketExistV2(bucket_name)) {
                b = getBucket(bucket_name);
            } else {
                try {
                    b = s3.createBucket(bucket_name);
                } catch (AmazonS3Exception e) {
                }
            }
            return b;
        }
    }
}
