package com.example.tastebuddies;

import android.content.Context;
import android.util.Log;

import com.google.android.gms.maps.model.LatLng;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class PlacesApiService {
    private static final String TAG = "PlacesApiService";
    private static final String API_KEY = "AIzaSyCZys_L6WW02hYQUB1mB37bMNMmguGF490";
    private static final String PLACES_API_BASE = "https://maps.googleapis.com/maps/api/place/nearbysearch/json";
    private static final String PLACE_DETAILS_API = "https://maps.googleapis.com/maps/api/place/details/json";
    private static final String PLACE_PHOTO_API = "https://maps.googleapis.com/maps/api/place/photo";

    private Context context;

    public PlacesApiService(Context context) {
        this.context = context;
    }

    public interface PlacesCallback {
        void onSuccess(List<RestaurantInfo> restaurants);
        void onError(String error);
        void onDetailsUpdated(RestaurantInfo restaurant); // Called when place details are fetched
    }

    private PlacesCallback currentCallback;
    
    public void searchNearbyRestaurants(LatLng location, String category, int radiusMeters, PlacesCallback callback) {
        this.currentCallback = callback;
        new Thread(() -> {
            try {
                String type = getPlaceTypeForCategory(category);
                String mealFilter = getMealFilterForCategory(category);
                String keyword = getKeywordForCategory(category);
                
                // Build URL with category-specific filters
                String urlString = PLACES_API_BASE + "?location=" + location.latitude + "," + location.longitude
                        + "&radius=" + radiusMeters
                        + "&type=" + type;
                
                // Add meal-specific filter if available
                if (!mealFilter.isEmpty()) {
                    urlString += "&" + mealFilter + "=true";
                }
                
                // Add keyword for better filtering
                if (!keyword.isEmpty()) {
                    urlString += "&keyword=" + java.net.URLEncoder.encode(keyword, "UTF-8");
                }
                
                urlString += "&key=" + API_KEY;

                String response = makeHttpRequest(urlString);
                JSONObject jsonResponse = new JSONObject(response);
                
                if (jsonResponse.getString("status").equals("OK")) {
                    JSONArray results = jsonResponse.getJSONArray("results");
                    List<RestaurantInfo> restaurants = new ArrayList<>();
                    java.util.Set<String> seenNames = new java.util.HashSet<>();
                    
                    int maxResults = Math.min(results.length(), 20); // Limit to 20 results
                    for (int i = 0; i < maxResults; i++) {
                        JSONObject place = results.getJSONObject(i);
                        RestaurantInfo restaurant = parsePlaceJson(place, location);
                        if (restaurant != null) {
                            // Additional filtering by category to ensure accuracy
                            if (matchesCategory(restaurant, category)) {
                                // Deduplicate by restaurant name (case-insensitive)
                                String nameKey = restaurant.getName().toLowerCase().trim();
                                if (!seenNames.contains(nameKey)) {
                                    seenNames.add(nameKey);
                                    // Fetch detailed information (hours, phone, website)
                                    fetchPlaceDetails(restaurant, place.getString("place_id"));
                                    restaurants.add(restaurant);
                                }
                            }
                        }
                    }
                    
                    callback.onSuccess(restaurants);
                } else {
                    callback.onError("API Error: " + jsonResponse.getString("status"));
                }
            } catch (Exception e) {
                Log.e(TAG, "Error searching places: " + e.getMessage());
                callback.onError(e.getMessage());
            }
        }).start();
    }

    private String getPlaceTypeForCategory(String category) {
        String lowerCategory = category.toLowerCase();
        
        if (lowerCategory.contains("breakfast")) {
            return "meal_takeaway"; // Fast food / takeaway for breakfast
        } else if (lowerCategory.contains("lunch")) {
            return "restaurant";
        } else if (lowerCategory.contains("dinner")) {
            return "restaurant";
        } else if (lowerCategory.contains("coffee")) {
            return "cafe";
        } else if (lowerCategory.contains("bar")) {
            return "bar";
        } else if (lowerCategory.contains("dessert")) {
            return "bakery";
        } else {
            return "restaurant";
        }
    }

    private String getMealFilterForCategory(String category) {
        String lowerCategory = category.toLowerCase();
        
        if (lowerCategory.contains("breakfast")) {
            return "serves_breakfast";
        } else if (lowerCategory.contains("lunch")) {
            return "serves_lunch";
        } else if (lowerCategory.contains("dinner")) {
            return "serves_dinner";
        }
        return "";
    }

    private String getKeywordForCategory(String category) {
        String lowerCategory = category.toLowerCase();
        
        if (lowerCategory.contains("breakfast")) {
            return "fast food breakfast"; // Focus on fast food for breakfast
        } else if (lowerCategory.contains("lunch")) {
            return "lunch";
        } else if (lowerCategory.contains("dinner")) {
            return "dinner";
        } else if (lowerCategory.contains("coffee")) {
            return "coffee";
        } else if (lowerCategory.contains("bar")) {
            return "bar";
        } else if (lowerCategory.contains("dessert")) {
            return "dessert";
        }
        return "";
    }

    private RestaurantInfo parsePlaceJson(JSONObject place, LatLng userLocation) {
        try {
            String name = place.getString("name");
            double rating = place.optDouble("rating", 0.0);
            int reviewCount = place.optInt("user_ratings_total", 0);
            
            String address = place.optString("vicinity", "");
            if (address.isEmpty() && place.has("formatted_address")) {
                address = place.getString("formatted_address");
            }
            
            // Get place types
            JSONArray types = place.optJSONArray("types");
            String type = "Restaurant";
            if (types != null && types.length() > 0) {
                type = types.getString(0).replace("_", " ");
            }
            
            // Get location
            JSONObject location = place.getJSONObject("geometry").getJSONObject("location");
            double latitude = location.getDouble("lat");
            double longitude = location.getDouble("lng");
            LatLng placeLocation = new LatLng(latitude, longitude);
            
            // Calculate distance
            double distanceKm = calculateDistance(
                userLocation.latitude, userLocation.longitude,
                latitude, longitude
            );
            String distance = String.format("%.1f mi", distanceKm * 0.621371);
            
            // Get opening hours - simplified format
            String status = "Hours not available";
            String hours = "Hours not available";
            if (place.has("opening_hours")) {
                JSONObject openingHours = place.getJSONObject("opening_hours");
                boolean isOpen = openingHours.optBoolean("open_now", false);
                status = isOpen ? "Open" : "Closed";
                
                // Get weekday text for hours
                if (openingHours.has("weekday_text")) {
                    JSONArray weekdayText = openingHours.getJSONArray("weekday_text");
                    if (weekdayText.length() > 0) {
                        // Parse to get opening and closing times
                        hours = parseHoursFromWeekdayText(weekdayText);
                    }
                }
            }
            
            // Service options (default)
            String serviceOptions = "Dine-in · Takeout";
            
            // Get place_id for details
            String placeId = place.getString("place_id");
            
            // Get photo references
            JSONArray photos = place.optJSONArray("photos");
            String imageUrl1 = "";
            String imageUrl2 = "";
            String imageUrl3 = "";
            
            if (photos != null && photos.length() > 0) {
                if (photos.length() > 0) {
                    String photoRef = photos.getJSONObject(0).getString("photo_reference");
                    imageUrl1 = PLACE_PHOTO_API + "?maxwidth=400&photoreference=" + photoRef + "&key=" + API_KEY;
                }
                if (photos.length() > 1) {
                    String photoRef = photos.getJSONObject(1).getString("photo_reference");
                    imageUrl2 = PLACE_PHOTO_API + "?maxwidth=400&photoreference=" + photoRef + "&key=" + API_KEY;
                }
                if (photos.length() > 2) {
                    String photoRef = photos.getJSONObject(2).getString("photo_reference");
                    imageUrl3 = PLACE_PHOTO_API + "?maxwidth=400&photoreference=" + photoRef + "&key=" + API_KEY;
                }
            }
            
            // Get phone and website from place details (will be fetched separately)
            String phone = "";
            String website = "";
            
            return new RestaurantInfo(
                0,
                name,
                (float) rating,
                reviewCount,
                type,
                distance,
                status,
                serviceOptions,
                latitude,
                longitude,
                phone,
                website,
                hours,
                imageUrl1,
                imageUrl2,
                imageUrl3
            );
        } catch (Exception e) {
            Log.e(TAG, "Error parsing place: " + e.getMessage());
            return null;
        }
    }

    private String makeHttpRequest(String urlString) throws Exception {
        URL url = new URL(urlString);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");
        
        BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
        StringBuilder response = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null) {
            response.append(line);
        }
        reader.close();
        
        return response.toString();
    }

    private boolean matchesCategory(RestaurantInfo restaurant, String category) {
        String lowerCategory = category.toLowerCase();
        String restaurantName = restaurant.getName().toLowerCase();
        String restaurantType = restaurant.getType().toLowerCase();
        
        // Additional filtering to ensure category accuracy
        if (lowerCategory.contains("breakfast")) {
            // Filter for FAST FOOD only for breakfast
            // Common fast food chains
            return restaurantType.contains("meal_takeaway") ||
                   restaurantType.contains("fast") ||
                   restaurantType.contains("takeaway") ||
                   restaurantName.contains("mcdonald") ||
                   restaurantName.contains("burger king") ||
                   restaurantName.contains("wendy") ||
                   restaurantName.contains("taco bell") ||
                   restaurantName.contains("kfc") ||
                   restaurantName.contains("chick-fil-a") ||
                   restaurantName.contains("chick fil a") ||
                   restaurantName.contains("subway") ||
                   restaurantName.contains("pizza hut") ||
                   restaurantName.contains("domino") ||
                   restaurantName.contains("papa john") ||
                   restaurantName.contains("dunkin") ||
                   restaurantName.contains("starbucks") ||
                   restaurantName.contains("tim hortons") ||
                   restaurantName.contains("arby") ||
                   restaurantName.contains("jack in the box") ||
                   restaurantName.contains("sonic") ||
                   restaurantName.contains("white castle") ||
                   restaurantName.contains("in-n-out") ||
                   restaurantName.contains("five guys") ||
                   restaurantName.contains("chipotle") ||
                   restaurantName.contains("qdoba") ||
                   restaurantName.contains("panda express") ||
                   restaurantName.contains("popeyes") ||
                   restaurantName.contains("bojangles");
        } else if (lowerCategory.contains("lunch")) {
            // Filter for lunch places (sandwich shops, fast casual, etc.)
            return restaurantType.contains("restaurant") ||
                   restaurantType.contains("food") ||
                   restaurantName.contains("lunch") ||
                   restaurantType.contains("meal");
        } else if (lowerCategory.contains("dinner")) {
            // Filter for dinner places (sit-down restaurants, exclude fast food)
            return restaurantType.contains("restaurant") &&
                   !restaurantType.contains("fast") &&
                   !restaurantType.contains("takeaway") &&
                   !restaurantName.contains("fast") &&
                   !restaurantName.contains("mcdonald") &&
                   !restaurantName.contains("burger king") &&
                   !restaurantName.contains("wendy") &&
                   !restaurantName.contains("taco bell");
        }
        
        return true; // For other categories, accept all
    }

    private void fetchPlaceDetails(RestaurantInfo restaurant, String placeId) {
        // Fetch detailed information in background
        new Thread(() -> {
            try {
                String urlString = PLACE_DETAILS_API + "?place_id=" + placeId
                        + "&fields=formatted_phone_number,website,opening_hours"
                        + "&key=" + API_KEY;
                
                String response = makeHttpRequest(urlString);
                JSONObject jsonResponse = new JSONObject(response);
                
                if (jsonResponse.getString("status").equals("OK")) {
                    JSONObject result = jsonResponse.getJSONObject("result");
                    
                    // Update phone number
                    if (result.has("formatted_phone_number")) {
                        restaurant.setPhone(result.getString("formatted_phone_number"));
                    }
                    
                    // Update website
                    if (result.has("website")) {
                        restaurant.setWebsite(result.getString("website"));
                    }
                    
                    // Update hours
                    if (result.has("opening_hours")) {
                        JSONObject openingHours = result.getJSONObject("opening_hours");
                        if (openingHours.has("weekday_text")) {
                            JSONArray weekdayText = openingHours.getJSONArray("weekday_text");
                            if (weekdayText.length() > 0) {
                                // Parse to get simplified hours format
                                String hours = parseHoursFromWeekdayText(weekdayText);
                                restaurant.setHours(hours);
                            }
                        }
                    }
                                
                                // Notify callback that details were updated
                                if (currentCallback != null) {
                                    currentCallback.onDetailsUpdated(restaurant);
                                }
                            }
                        } catch (Exception e) {
                            Log.e(TAG, "Error fetching place details: " + e.getMessage());
                        }
                    }).start();
                }

    private String parseHoursFromWeekdayText(JSONArray weekdayText) {
        try {
            // Check if it's 24 hours
            boolean is24Hours = true;
            String firstDayHours = "";
            
            for (int i = 0; i < weekdayText.length(); i++) {
                String dayHours = weekdayText.getString(i);
                if (i == 0) {
                    firstDayHours = dayHours;
                }
                // Check if any day is not 24 hours
                if (!dayHours.toLowerCase().contains("24") && 
                    !dayHours.toLowerCase().contains("open 24")) {
                    is24Hours = false;
                }
            }
            
            if (is24Hours) {
                return "Open 24 hours";
            }
            
            // Extract opening and closing time from first day
            // Format is usually "Monday: 8:00 AM – 10:00 PM" or "Monday: 8:00 AM - 10:00 PM"
            if (!firstDayHours.isEmpty()) {
                // Remove day name and colon
                String hoursPart = firstDayHours;
                int colonIndex = firstDayHours.indexOf(':');
                if (colonIndex > 0) {
                    hoursPart = firstDayHours.substring(colonIndex + 1).trim();
                }
                
                // Check for 24 hours in the hours part
                if (hoursPart.toLowerCase().contains("24") || 
                    hoursPart.toLowerCase().contains("open 24")) {
                    return "Open 24 hours";
                }
                
                // Extract time range (e.g., "8:00 AM – 10:00 PM" or "8:00 AM - 10:00 PM")
                // Look for patterns like "AM" to "PM" or "PM" to "AM"
                if (hoursPart.contains("–") || hoursPart.contains("-")) {
                    // Return the time range
                    return hoursPart.trim();
                }
                
                // If no range found, return the whole hours part
                return hoursPart.trim();
            }
            
            return "Hours not available";
        } catch (Exception e) {
            Log.e(TAG, "Error parsing hours: " + e.getMessage());
            return "Hours not available";
        }
    }

    private double calculateDistance(double lat1, double lon1, double lat2, double lon2) {
        final int R = 6371; // Radius of the earth in km
        double latDistance = Math.toRadians(lat2 - lat1);
        double lonDistance = Math.toRadians(lon2 - lon1);
        double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                * Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        return R * c;
    }
}
