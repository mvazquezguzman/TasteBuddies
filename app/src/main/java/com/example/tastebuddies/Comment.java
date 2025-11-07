package com.example.tastebuddies;

public class Comment {
    private int commentId;
    private int postId;
    private int userId;
    private String username;
    private byte[] profilePicture;
    private String commentText;
    private long createdAt;

    public Comment(int commentId, int postId, int userId, String username, 
                   byte[] profilePicture, String commentText, long createdAt)
    {
        this.commentId = commentId;
        this.postId = postId;
        this.userId = userId;
        this.username = username;
        this.profilePicture = profilePicture;
        this.commentText = commentText;
        this.createdAt = createdAt;
    }

    public int getCommentId() {
        return commentId;
    }

    public void setCommentId(int commentId) {
        this.commentId = commentId;
    }

    public int getPostId() {
        return postId;
    }

    public void setPostId(int postId) {
        this.postId = postId;
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

    public byte[] getProfilePicture() {
        return profilePicture;
    }

    public void setProfilePicture(byte[] profilePicture) {
        this.profilePicture = profilePicture;
    }

    public String getCommentText() {
        return commentText;
    }

    public void setCommentText(String commentText) {
        this.commentText = commentText;
    }

    public long getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(long createdAt) {
        this.createdAt = createdAt;
    }
}
