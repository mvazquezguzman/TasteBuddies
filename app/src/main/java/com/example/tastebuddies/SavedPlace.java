package com.example.tastebuddies;

public class SavedPlace {
    private int savedPlaceId;
    private int userId;
    private String placeName;
    private String placeType;
    private float rating;
    private int reviewCount;
    private String distance;
    private String status;
    private String serviceOptions;
    private String phone;
    private String website;
    private String hours;
    private double latitude;
    private double longitude;
    private String imageUrl;
    private long createdAt;

    public SavedPlace(int savedPlaceId, int userId, String placeName, String placeType,
                     float rating, int reviewCount, String distance, String status,
                     String serviceOptions, String phone, String website, String hours,
                     double latitude, double longitude, String imageUrl, long createdAt) {
        this.savedPlaceId = savedPlaceId;
        this.userId = userId;
        this.placeName = placeName;
        this.placeType = placeType;
        this.rating = rating;
        this.reviewCount = reviewCount;
        this.distance = distance;
        this.status = status;
        this.serviceOptions = serviceOptions;
        this.phone = phone;
        this.website = website;
        this.hours = hours;
        this.latitude = latitude;
        this.longitude = longitude;
        this.imageUrl = imageUrl;
        this.createdAt = createdAt;
    }

    public int getSavedPlaceId() {
        return savedPlaceId;
    }

    public int getUserId() {
        return userId;
    }

    public String getPlaceName() {
        return placeName;
    }

    public String getPlaceType() {
        return placeType;
    }

    public float getRating() {
        return rating;
    }

    public int getReviewCount() {
        return reviewCount;
    }

    public String getDistance() {
        return distance;
    }

    public String getStatus() {
        return status;
    }

    public String getServiceOptions() {
        return serviceOptions;
    }

    public String getPhone() {
        return phone;
    }

    public String getWebsite() {
        return website;
    }

    public String getHours() {
        return hours;
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public long getCreatedAt() {
        return createdAt;
    }
}

