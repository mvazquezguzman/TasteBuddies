package com.example.tastebuddies;

public class User {
    private int userId;
    private String username;
    private String email;
    private String displayName;
    private String bio;
    private byte[] profilePicture;

    public User(int userId, String username, String email, String displayName, String bio, byte[] profilePicture) {
        this.userId = userId;
        this.username = username;
        this.email = email;
        this.displayName = displayName;
        this.bio = bio;
        this.profilePicture = profilePicture;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getBio() {
        return bio;
    }

    public void setBio(String bio) {
        this.bio = bio;
    }

    public byte[] getProfilePicture() {
        return profilePicture;
    }

    public void setProfilePicture(byte[] profilePicture) {
        this.profilePicture = profilePicture;
    }
}

