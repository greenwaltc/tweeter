package edu.byu.cs.tweeter.server.dao.dynamo;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.Bucket;
import com.amazonaws.services.s3.model.ObjectMetadata;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.Base64;

import edu.byu.cs.tweeter.server.dao.BucketDAO;
import edu.byu.cs.tweeter.server.dao.util.CreateBucket;

public class S3DAO implements BucketDAO {
    private static final String ProfilePhotosBucketName = "cs340cgreenw5-tweeter-profile-photos";
    private static final String Region = "us-west-2";

    @Override
    public String uploadImage(String key, String contents) {

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
}
