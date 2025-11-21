package com.example.tastebuddies;

public class RestaurantInfo {
    private int id;
    private String name;
    private float rating;
    private int reviewCount;
    private String type;
    private String distance;
    private String status;
    private String serviceOptions;
    private double latitude;
    private double longitude;
    private String phone;
    private String website;
    private String hours;
    private String imageUrl1;
    private String imageUrl2;
    private String imageUrl3;

    public RestaurantInfo(int id, String name, float rating, int reviewCount, String type,
                         String distance, String status, String serviceOptions,
                         double latitude, double longitude, String phone, String website,
                         String hours, String imageUrl1, String imageUrl2, String imageUrl3) {
        this.id = id;
        this.name = name;
        this.rating = rating;
        this.reviewCount = reviewCount;
        this.type = type;
        this.distance = distance;
        this.status = status;
        this.serviceOptions = serviceOptions;
        this.latitude = latitude;
        this.longitude = longitude;
        this.phone = phone;
        this.website = website;
        this.hours = hours;
        this.imageUrl1 = imageUrl1;
        this.imageUrl2 = imageUrl2;
        this.imageUrl3 = imageUrl3;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public float getRating() {
        return rating;
    }

    public int getReviewCount() {
        return reviewCount;
    }

    public String getType() {
        return type;
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

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public String getPhone() {
        return phone;
    }

    public String getWebsite() {
        return website;
    }

    public String getImageUrl1() {
        return imageUrl1;
    }

    public String getImageUrl2() {
        return imageUrl2;
    }

    public String getImageUrl3() {
        return imageUrl3;
    }

    public String getHours() {
        return hours;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public void setWebsite(String website) {
        this.website = website;
    }

    public void setHours(String hours) {
        this.hours = hours;
    }
}

