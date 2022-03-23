package edu.byu.cs.tweeter.model.domain;

public class DBUser {
    private User user;
    private byte[] hash, salt;
    private int followersCount, followingCount;

    public DBUser(User user, byte[] hash, byte[] salt, int followersCount, int followingCount) {
        this.user = user;
        this.hash = hash;
        this.salt = salt;
        this.followersCount = followersCount;
        this.followingCount = followingCount;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public byte[] getHash() {
        return hash;
    }

    public void setHash(byte[] hash) {
        this.hash = hash;
    }

    public byte[] getSalt() {
        return salt;
    }

    public void setSalt(byte[] salt) {
        this.salt = salt;
    }

    public int getFollowersCount() {
        return followersCount;
    }

    public void setFollowersCount(int followersCount) {
        this.followersCount = followersCount;
    }

    public int getFollowingCount() {
        return followingCount;
    }

    public void setFollowingCount(int followingCount) {
        this.followingCount = followingCount;
    }
}
