# TasteBuddies: A Food Discovery and Sharing Mobile Application

## Team Members
- [Your Name/Team Member 1 Name]
- [Team Member 2 Name]
- [Team Member 3 Name] (if applicable)

---

# Section 1: Introduction

## 1.1 Overview

TasteBuddies is a mobile application designed to help users discover, share, and explore food experiences in their local area. The application serves as a social platform where food enthusiasts can post reviews, photos, and recommendations of restaurants and dishes they've tried, while also discovering new dining options based on categories such as breakfast, lunch, dinner, coffee, bars, and desserts.

## 1.2 Problem Statement

In today's fast-paced world, finding the right restaurant or food option can be overwhelming. Users often struggle with:
- **Information Overload**: Too many options without proper filtering
- **Lack of Personalized Recommendations**: Difficulty finding restaurants that match specific meal preferences (breakfast, lunch, dinner)
- **Limited Local Discovery**: Challenges in discovering nearby restaurants with accurate location-based information
- **Social Food Sharing**: No easy way to share food experiences with a community of food lovers

## 1.3 Importance and Significance

TasteBuddies addresses these challenges by providing:

1. **Category-Based Discovery**: Users can easily filter restaurants by meal type (breakfast, lunch, dinner) and food categories (coffee, bars, desserts), making it easier to find exactly what they're looking for.

2. **Location-Aware Search**: Integration with Google Places API provides real-time, accurate information about nearby restaurants, including ratings, reviews, photos, hours, and contact information.

3. **Social Food Sharing**: Users can create posts with photos, ratings, and reviews, building a community-driven database of food recommendations.

4. **Fast Food Focus for Breakfast**: The application specifically targets fast food restaurants for breakfast searches, recognizing the need for quick morning meal options.

5. **Real Restaurant Data**: Unlike static databases, TasteBuddies uses live data from Google Places API, ensuring users always see current information about restaurant availability, hours, and ratings.

## 1.4 Application Scope

TasteBuddies focuses on:
- **Geographic Area**: Initially targeting areas around Georgia Gwinnett College, with the ability to expand to other locations
- **Restaurant Categories**: Breakfast (fast food), Lunch, Dinner, Coffee shops, Bars, and Dessert places
- **Key Features**: Search functionality, category filtering, restaurant listings with photos, ratings, and contact information, and social sharing capabilities

---

# Section 2: Background

## 2.1 Existing Food Discovery Applications

The mobile application market includes several food discovery and restaurant review platforms. This section reviews existing solutions and identifies gaps that TasteBuddies addresses.

### 2.1.1 Yelp

**Overview**: Yelp is one of the most popular restaurant discovery platforms, allowing users to search for restaurants, read reviews, view photos, and make reservations.

**Strengths**:
- Extensive database of restaurants worldwide
- User-generated reviews and ratings
- Integration with maps and directions
- Business information including hours, contact details, and menus

**Limitations**:
- Generic search without meal-specific filtering (breakfast, lunch, dinner)
- Overwhelming amount of information without category-based organization
- Limited focus on fast food chains for specific meal times
- Less emphasis on social food sharing and community building

### 2.1.2 Google Maps / Google Places

**Overview**: Google Maps provides restaurant search functionality with integrated reviews, photos, and business information.

**Strengths**:
- Accurate location-based search
- Real-time data from Google Places API
- High-quality restaurant photos
- Integration with navigation and directions

**Limitations**:
- Not designed as a dedicated food discovery platform
- Limited social features for food sharing
- No meal-specific category filtering (breakfast vs. lunch vs. dinner)
- Less community-focused experience

### 2.1.3 Zomato (formerly Urbanspoon)

**Overview**: Zomato is a restaurant discovery platform that provides menus, photos, reviews, and booking options.

**Strengths**:
- Comprehensive restaurant database
- Menu information and pricing
- User reviews and ratings
- Reservation capabilities

**Limitations**:
- Less emphasis on meal-specific categorization
- Limited fast food restaurant focus
- Not primarily designed for social food sharing
- May not be available in all regions

### 2.1.4 Instagram / Social Media Platforms

**Overview**: Social media platforms like Instagram are commonly used for sharing food photos and discovering restaurants.

**Strengths**:
- Strong social sharing capabilities
- Visual content (photos and videos)
- Hashtag-based discovery (#food, #breakfast, etc.)
- Large user base

**Limitations**:
- Not designed specifically for restaurant discovery
- Lack of structured restaurant information (hours, ratings, contact)
- No location-based filtering or category organization
- Difficult to find specific meal types (breakfast, lunch, dinner)

## 2.2 Research and Previous Works

### 2.2.1 Location-Based Restaurant Recommendation Systems

Research in location-based recommendation systems has shown that users prefer:
- **Proximity-based filtering**: Restaurants within a reasonable distance
- **Category-based organization**: Grouping by meal type or cuisine
- **Real-time information**: Current hours, availability, and ratings
- **Visual content**: Photos help users make decisions faster

### 2.2.2 User Behavior in Food Discovery Apps

Studies indicate that:
- Users search differently for breakfast (often seeking fast food) versus dinner (preferring sit-down restaurants)
- Category-based navigation reduces decision fatigue
- Integration of real restaurant data increases trust and usage
- Social sharing features encourage community engagement

## 2.3 Gap Analysis

Based on the review of existing applications, TasteBuddies addresses the following gaps:

1. **Meal-Specific Filtering**: Unlike generic restaurant apps, TasteBuddies provides dedicated filtering for breakfast (fast food), lunch, and dinner, recognizing that users have different needs for different meal times.

2. **Fast Food Focus for Breakfast**: Specifically targets fast food restaurants for breakfast searches, acknowledging the morning rush and need for quick options.

3. **Category-Based Discovery**: Organizes restaurants by meal type and food category (coffee, bars, desserts), making discovery more intuitive.

4. **Real-Time Data Integration**: Uses Google Places API to provide live, accurate restaurant information rather than static databases.

5. **Social Food Sharing**: Combines restaurant discovery with social features, allowing users to share their food experiences within the app.

6. **Simplified User Experience**: Focuses on essential features (search, category browsing, restaurant details) without overwhelming users with excessive options.

## 2.4 Technology Stack

TasteBuddies leverages modern mobile development technologies:
- **Android Native Development**: Built using Java and Android SDK for optimal performance
- **Google Places API**: For real-time restaurant data and location services
- **Google Maps SDK**: For location-based features and directions
- **Glide Library**: For efficient image loading and caching
- **Material Design**: Following Android design guidelines for consistent user experience

---

# Section 3: Technical Part

This section provides detailed information about the layout design and implementation of the TasteBuddies application. The implementation covers various Android development concepts including fragments, navigation, database management, intents, activities, and location services.

## 3.1 Fragments and Navigation

### 3.1.1 Fragment Architecture

TasteBuddies utilizes Android Fragments to create a modular and reusable user interface. The application implements five main fragments:

- **FragmentHome**: Displays the main feed of food posts
- **FragmentSearch**: Provides search functionality and category-based restaurant discovery
- **FragmentUpload**: Allows users to create and upload new food posts
- **FragmentSaved**: Shows saved posts with list and map view options
- **FragmentProfile**: Displays user profile information

### 3.1.2 Navigation Implementation

Navigation between fragments is managed through `MainActivity`, which serves as the host activity. The application uses `BottomNavigationView` from Material Design Components for intuitive bottom navigation.

**Implementation Details:**

```java
// MainActivity.java - Fragment Navigation
bottomNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        Fragment fragment = null;
        int itemId = item.getItemId();
        
        if (itemId == R.id.nav_home) {
            fragment = new FragmentHome();
        } else if (itemId == R.id.nav_search) {
            fragment = new FragmentSearch();
        } else if (itemId == R.id.nav_upload) {
            fragment = new FragmentUpload();
        } else if (itemId == R.id.nav_saved) {
            fragment = new FragmentSaved();
        } else if (itemId == R.id.nav_profile) {
            fragment = new FragmentProfile();
        }

        if (fragment != null) {
            loadFragment(fragment);
            return true;
        }
        return false;
    }
});
```

The `loadFragment()` method uses `FragmentManager` to replace fragments dynamically:

```java
private void loadFragment(Fragment fragment) {
    getSupportFragmentManager().beginTransaction()
            .replace(R.id.fragmentContainer, fragment)
            .commit();
}
```

**Benefits:**
- Modular code structure
- Efficient memory management
- Smooth navigation transitions
- Easy to maintain and extend

## 3.2 Bottom Navigation with Icons

### 3.2.1 Navigation Bar Design

Instead of a traditional ActionBar, TasteBuddies uses a `BottomNavigationView` positioned at the bottom of the screen, following modern Android design patterns. This provides easy thumb access and clear visual hierarchy.

**Layout Implementation:**

```xml
<com.google.android.material.bottomnavigation.BottomNavigationView
    android:id="@+id/bottomNavigationView"
    android:layout_width="0dp"
    android:layout_height="wrap_content"
    android:background="@color/tea_green"
    app:itemTextColor="@color/white"
    app:itemIconTint="@color/white"
    app:labelVisibilityMode="labeled"
    app:menu="@menu/bottom_navigation"
    app:layout_constraintBottom_toBottomOf="parent"
    app:layout_constraintStart_toStartOf="parent"
    app:layout_constraintEnd_toEndOf="parent" />
```

**Menu Configuration:**

The bottom navigation menu (`bottom_navigation.xml`) defines five navigation items:
- Home (with home icon)
- Search (with search icon)
- Upload (with upload icon)
- Saved (with bookmark icon)
- Profile (with user profile icon)

Each menu item includes both an icon and a text label for clarity.

## 3.3 SQLite Database

### 3.3.1 Database Architecture

TasteBuddies uses SQLite for local data storage through a custom `TasteBuddiesDatabaseManager` class. The database stores user information, posts, comments, likes, and bookmarks.

### 3.3.2 Database Schema

The application implements five main tables:

**1. Users Table:**
```sql
CREATE TABLE users (
    user_id INTEGER PRIMARY KEY AUTOINCREMENT,
    username TEXT NOT NULL,
    email TEXT NOT NULL UNIQUE,
    password TEXT NOT NULL,
    display_name TEXT,
    bio TEXT,
    profile_picture BLOB,
    created_at INTEGER
)
```

**2. Posts Table:**
```sql
CREATE TABLE posts (
    post_id INTEGER PRIMARY KEY AUTOINCREMENT,
    user_id INTEGER NOT NULL,
    food_name TEXT NOT NULL,
    review TEXT,
    rating INTEGER,
    post_image BLOB,
    restaurant_name TEXT,
    location TEXT,
    latitude REAL,
    longitude REAL,
    created_at INTEGER,
    like_count INTEGER DEFAULT 0,
    FOREIGN KEY(user_id) REFERENCES users(user_id) ON DELETE CASCADE
)
```

**3. Comments Table:**
- Stores user comments on posts with foreign key relationships

**4. Likes Table:**
- Tracks which users liked which posts (composite primary key)

**5. Bookmarks Table:**
- Manages saved posts for each user

### 3.3.3 Database Operations

The `TasteBuddiesDatabaseManager` provides methods for:
- User authentication and registration
- Post creation and retrieval
- Comment management
- Like and bookmark operations
- Data queries with proper indexing for performance

**Example Implementation:**

```java
public long createUser(String username, String email, String password) {
    SQLiteStatement statement = database.compileStatement(
        "INSERT INTO " + TABLE_USERS +
        "(username, email, password, display_name, bio, profile_picture, created_at) " +
        "VALUES (?, ?, ?, ?, ?, ?, ?)");
    // ... binding parameters
    long id = statement.executeInsert();
    statement.close();
    return id;
}
```

## 3.4 Intents (Camera and Gallery Integration)

### 3.4.1 Camera Integration

The `FragmentUpload` class implements camera functionality using Android Intents:

```java
private void takePhoto() {
    if (ContextCompat.checkSelfPermission(requireActivity(), Manifest.permission.CAMERA) 
            != PackageManager.PERMISSION_GRANTED) {
        ActivityCompat.requestPermissions(requireActivity(), 
                new String[]{Manifest.permission.CAMERA}, REQUEST_CAMERA_PERMISSION);
        return;
    }

    Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
    if (takePictureIntent.resolveActivity(requireActivity().getPackageManager()) != null) {
        startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
    }
}
```

### 3.4.2 Gallery Integration

Gallery access is implemented similarly:

```java
private void selectImageFromGallery() {
    Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
    startActivityForResult(intent, REQUEST_IMAGE_PICK);
}
```

### 3.4.3 Result Handling

The `onActivityResult()` method processes camera and gallery results:

```java
@Override
public void onActivityResult(int requestCode, int resultCode, Intent data) {
    super.onActivityResult(requestCode, resultCode, data);
    
    if (resultCode == Activity.RESULT_OK) {
        if (requestCode == REQUEST_IMAGE_CAPTURE && data != null) {
            Bundle extras = data.getExtras();
            if (extras != null) {
                selectedBitmap = (Bitmap) extras.get("data");
                imageViewFood.setImageBitmap(selectedBitmap);
            }
        } else if (requestCode == REQUEST_IMAGE_PICK && data != null) {
            try {
                selectedBitmap = MediaStore.Images.Media.getBitmap(
                        requireActivity().getContentResolver(), data.getData());
                imageViewFood.setImageBitmap(selectedBitmap);
            } catch (Exception e) {
                Toast.makeText(getActivity(), "Error loading image", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
```

**Permissions Required:**
- `CAMERA`: For taking photos
- `READ_EXTERNAL_STORAGE`: For accessing gallery images

## 3.5 Multiple Activities

### 3.5.1 Activity Structure

TasteBuddies implements multiple activities, each serving a specific purpose:

**1. LandingActivity**
- Entry point of the application
- Handles initial app launch and routing

**2. SignInActivity**
- User authentication interface
- Validates credentials against SQLite database

**3. SignUpActivity**
- New user registration
- Creates user accounts in the database

**4. MainActivity**
- Host activity for fragments
- Manages bottom navigation and fragment transactions
- Maintains user session state

**5. PostDetailActivity**
- Displays detailed view of individual posts
- Shows comments, likes, and post information
- Allows user interactions (like, comment, bookmark)

**6. UserProfileActivity**
- Displays user profile information
- Shows user's posts and activity
- Profile management features

**7. CategoryResultsActivity**
- Displays restaurant search results
- Shows restaurant cards with details
- Integrates with Google Places API

### 3.5.2 Activity Navigation

Activities communicate using Intents with extras:

```java
// Example: Navigating to PostDetailActivity
Intent intent = new Intent(getActivity(), PostDetailActivity.class);
intent.putExtra("postId", postId);
startActivity(intent);
```

## 3.6 ListView for Displaying Posts

### 3.6.1 ListView Implementation

The application uses `ListView` with custom adapters to display posts efficiently. `FragmentHome` implements a `ListView` that displays the main feed of food posts.

**Layout Structure:**

```xml
<ListView
    android:id="@+id/listViewFeed"
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    android:divider="@null"
    android:dividerHeight="0dp" />
```

### 3.6.2 Custom Adapter

`PostAdapter` extends `BaseAdapter` to provide custom post item rendering:

```java
public class PostAdapter extends BaseAdapter {
    private Context context;
    private List<Post> posts;
    private LayoutInflater inflater;
    
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.item_post, parent, false);
            holder = new ViewHolder();
            // Initialize views
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        
        Post post = posts.get(position);
        // Populate views with post data
        return convertView;
    }
}
```

**Features:**
- ViewHolder pattern for performance optimization
- Efficient view recycling
- Custom post item layout with images, ratings, and text
- Click listeners for post interactions

### 3.6.3 Post Item Layout

Each post item displays:
- User profile picture
- Food name and restaurant
- Rating bar
- Post image
- Caption/review text
- Like, bookmark, and comment buttons
- Timestamp

## 3.7 Map Integration for Tagging Restaurants

### 3.7.1 Google Maps Integration

TasteBuddies integrates Google Maps SDK to display restaurant locations. `FragmentSaved` implements a `MapView` that shows saved posts on a map.

**Implementation:**

```java
public class FragmentSaved extends Fragment implements OnMapReadyCallback {
    private MapView mapView;
    private GoogleMap googleMap;
    
    @Override
    public void onMapReady(GoogleMap map) {
        googleMap = map;
        // Configure map settings
        googleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        // Add markers for saved posts
        updateMapMarkers();
    }
}
```

### 3.7.2 Location Tagging

Posts can be tagged with restaurant locations using latitude and longitude coordinates. The database stores location data:

```java
// Posts table includes location fields
latitude REAL,
longitude REAL,
location TEXT
```

### 3.7.3 Map Markers

Restaurant locations are displayed as markers on the map:

```java
private void updateMapMarkers() {
    if (googleMap != null && savedPosts != null) {
        googleMap.clear();
        for (Post post : savedPosts) {
            if (post.getLatitude() != 0 && post.getLongitude() != 0) {
                LatLng location = new LatLng(post.getLatitude(), post.getLongitude());
                googleMap.addMarker(new MarkerOptions()
                    .position(location)
                    .title(post.getRestaurantName())
                    .snippet(post.getFoodName()));
            }
        }
    }
}
```

### 3.7.4 Toggle Between List and Map Views

Users can switch between list and map views in `FragmentSaved`:

```java
private void setMapView() {
    isListView = false;
    // Show MapView, hide ListView
    mapViewContainer.setVisibility(View.VISIBLE);
    listViewSaved.setVisibility(View.GONE);
    updateMapMarkers();
}
```

## 3.8 User Interface Design with ConstraintLayout and LinearLayout

### 3.8.1 ConstraintLayout Usage

`ConstraintLayout` is used for complex layouts requiring precise positioning and relationships between views. `MainActivity` uses `ConstraintLayout` as its root layout:

```xml
<androidx.constraintlayout.widget.ConstraintLayout
    android:layout_width="match_parent"
    android:layout_height="match_parent">
    
    <FrameLayout
        android:id="@+id/fragmentContainer"
        app:layout_constraintTop_toTopOf="parent"
        app:layout_constraintStart_toStartOf="parent"
        app:layout_constraintEnd_toEndOf="parent"
        app:layout_constraintBottom_toTopOf="@+id/bottomNavigationView" />
    
    <BottomNavigationView
        android:id="@+id/bottomNavigationView"
        app:layout_constraintBottom_toBottomOf="parent"
        app:layout_constraintStart_toStartOf="parent"
        app:layout_constraintEnd_toEndOf="parent" />
</androidx.constraintlayout.widget.ConstraintLayout>
```

**Benefits:**
- Flat view hierarchy (better performance)
- Flexible positioning with constraints
- Responsive design for different screen sizes

### 3.8.2 LinearLayout Usage

`LinearLayout` is used for simpler, linear arrangements of views. `FragmentSearch` and `FragmentUpload` utilize `LinearLayout`:

```xml
<LinearLayout
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    android:orientation="vertical">
    
    <!-- Header -->
    <LinearLayout
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        android:orientation="horizontal">
        <!-- Header content -->
    </LinearLayout>
    
    <!-- Category buttons -->
    <GridLayout
        android:layout_width="match_parent"
        android:layout_height="wrap_content">
        <!-- Category buttons -->
    </GridLayout>
</LinearLayout>
```

**Use Cases:**
- Vertical or horizontal arrangements
- Simple nested layouts
- Category button grids
- Form layouts

### 3.8.3 Design Principles

The UI design follows Material Design guidelines:
- Consistent color scheme (tea green, celtic blue, cream background)
- Appropriate spacing and padding
- Clear visual hierarchy
- Touch-friendly button sizes
- Responsive layouts for different screen sizes

## 3.9 Android Location Services (Special Feature)

### 3.9.1 FusedLocationProviderClient Implementation

TasteBuddies uses Google's `FusedLocationProviderClient` API to obtain the user's current location for showing nearby restaurants. This is implemented in `FragmentUpload` and `PlacesApiService`.

**Implementation in FragmentUpload:**

```java
private FusedLocationProviderClient fusedLocationClient;

@Override
public View onCreateView(...) {
    fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity());
    getCurrentLocation();
    // ...
}

private void getCurrentLocation() {
    if (ContextCompat.checkSelfPermission(requireActivity(), 
            Manifest.permission.ACCESS_FINE_LOCATION) 
            != PackageManager.PERMISSION_GRANTED) {
        ActivityCompat.requestPermissions(requireActivity(), 
                new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 
                REQUEST_LOCATION_PERMISSION);
        return;
    }

    fusedLocationClient.getLastLocation().addOnSuccessListener(requireActivity(), location -> {
        if (location != null) {
            latitude = location.getLatitude();
            longitude = location.getLongitude();
        }
    });
}
```

### 3.9.2 Google Places API Integration

The `PlacesApiService` class integrates with Google Places API to find top-rated restaurants near the user's location:

```java
public void searchNearbyRestaurants(LatLng location, String category, 
                                    int radiusMeters, PlacesCallback callback) {
    String urlString = PLACES_API_BASE + "?location=" + location.latitude + "," + location.longitude
            + "&radius=" + radiusMeters
            + "&type=" + type
            + "&key=" + API_KEY;
    
    // Make HTTP request and parse JSON response
    // Return list of RestaurantInfo objects
}
```

### 3.9.3 Location-Based Restaurant Discovery

**Key Features:**
- **Nearby Search**: Finds restaurants within a specified radius (default: 5000 meters around Georgia Gwinnett College)
- **Category Filtering**: Filters results by meal type (breakfast, lunch, dinner) and food category
- **Real-Time Data**: Retrieves current restaurant information including:
  - Ratings and review counts
  - Opening hours and status (open/closed)
  - Restaurant photos
  - Contact information (phone, website)
  - Distance from user location

**Implementation Flow:**

1. User selects a category (Breakfast, Lunch, Dinner, Coffee, Bar, Dessert)
2. Application uses `FusedLocationProviderClient` to get user location (or uses default: GGC coordinates)
3. `PlacesApiService` queries Google Places API with location and category filters
4. Results are parsed and displayed in `CategoryResultsActivity`
5. Additional details (phone, website, hours) are fetched via Place Details API
6. Restaurant cards are displayed with real-time information

### 3.9.4 Restaurant Information Display

Restaurants are displayed using `RestaurantCardAdapter` in a `ListView`, showing:
- Restaurant name and type
- Rating (stars) and review count
- Distance from user location
- Opening hours (simplified format: "8:00 AM – 10:00 PM" or "Open 24 hours")
- Phone number (clickable to dial)
- Website (clickable to open browser)
- Restaurant photo (loaded using Glide library)

**Special Feature Highlights:**
- **Fast Food Focus for Breakfast**: The application specifically filters for fast food restaurants when "Breakfast" is selected, recognizing the need for quick morning meal options
- **Accurate Category Filtering**: Uses meal-specific API filters (`serves_breakfast`, `serves_lunch`, `serves_dinner`) combined with keyword filtering for precise results
- **Real-Time Updates**: Restaurant hours and status are fetched in real-time, ensuring users see current information

### 3.9.5 Permissions

The application requests the following location permissions:
- `ACCESS_FINE_LOCATION`: For precise location services
- `ACCESS_COARSE_LOCATION`: For approximate location (fallback)

Permissions are requested at runtime following Android best practices.

---

# Section 4: Conclusion & Future Works

## 4.1 Conclusion

TasteBuddies represents a comprehensive solution to the challenges faced by users in discovering and sharing food experiences. Through the integration of Google Places API, the application provides real-time, accurate restaurant information including ratings, photos, hours, and contact details. The category-based filtering system, particularly the meal-specific approach (breakfast, lunch, dinner), addresses a significant gap in existing food discovery applications.

Key achievements of the TasteBuddies application include:

1. **Effective Category-Based Discovery**: The implementation of meal-specific filtering (breakfast for fast food, lunch, and dinner) provides users with targeted results that match their current needs and time of day.

2. **Real-Time Data Integration**: By leveraging Google Places API, the application ensures users always have access to current restaurant information, including opening hours, ratings, and availability, eliminating the frustration of outdated data.

3. **User-Friendly Interface**: The application's design focuses on simplicity and ease of use, allowing users to quickly find restaurants through category buttons or search functionality.

4. **Location-Aware Search**: The integration with location services enables users to discover restaurants near Georgia Gwinnett College, with the flexibility to expand to other geographic areas.

5. **Comprehensive Restaurant Information**: Each restaurant listing includes essential details such as ratings, review counts, distance, status (open/closed), hours, phone numbers, and websites, providing users with all the information needed to make informed decisions.

The application successfully combines the benefits of real-time data from Google Places API with an intuitive user interface, creating a valuable tool for food discovery and sharing.

## 4.2 Limitations

While TasteBuddies provides a solid foundation for food discovery, several limitations were identified during development:

1. **Geographic Scope**: Currently focused on areas around Georgia Gwinnett College. Expansion to other cities and regions would require additional configuration and testing.

2. **API Dependency**: The application relies heavily on Google Places API. Any changes to API pricing, rate limits, or availability could impact functionality.

3. **Category Accuracy**: While efforts were made to improve category filtering accuracy (especially for breakfast fast food), some edge cases may still result in less precise results.

4. **Social Features**: The current implementation includes basic post creation functionality, but full social features such as user profiles, following, and commenting are not yet fully implemented.

5. **Offline Functionality**: The application requires an active internet connection to fetch restaurant data, limiting usability in areas with poor connectivity.

## 4.3 Future Works

Several enhancements and features are planned for future versions of TasteBuddies:

### 4.3.1 Enhanced Social Features
- **User Profiles**: Complete user profile system with personal information, favorite restaurants, and posting history
- **Following System**: Allow users to follow other food enthusiasts and see their recommendations
- **Comments and Interactions**: Enable users to comment on posts and interact with the community
- **Notifications**: Push notifications for new posts from followed users, restaurant updates, and special offers

### 4.3.2 Advanced Search and Filtering
- **Price Range Filtering**: Add filters for budget-friendly, mid-range, and upscale restaurants
- **Cuisine Type Filtering**: Filter by specific cuisines (Italian, Mexican, Asian, etc.)
- **Dietary Restrictions**: Support for vegetarian, vegan, gluten-free, and other dietary preferences
- **Sorting Options**: Sort results by distance, rating, price, or popularity
- **Saved Searches**: Allow users to save frequently used search queries

### 4.3.3 Restaurant Features
- **Menu Integration**: Display restaurant menus with prices and item descriptions
- **Reservation System**: Integration with reservation platforms to allow in-app table booking
- **Special Offers and Deals**: Display current promotions, happy hours, and special offers
- **Wait Time Information**: Real-time wait times for popular restaurants
- **Restaurant Reviews**: Enhanced review system with detailed ratings for food, service, ambiance, and value

### 4.3.4 Personalization
- **Recommendation Engine**: Machine learning-based recommendations based on user preferences and past behavior
- **Personalized Feed**: Customized feed showing restaurants and posts relevant to the user
- **Favorite Restaurants**: Allow users to save and organize favorite restaurants
- **Food Preferences**: User-defined preferences for cuisine types, price ranges, and dietary restrictions

### 4.3.5 Technical Improvements
- **Offline Mode**: Cache restaurant data for offline access
- **Performance Optimization**: Improve app loading times and reduce API calls through better caching strategies
- **Cross-Platform Development**: Expand to iOS platform using cross-platform frameworks
- **Backend Integration**: Develop a dedicated backend server for user data, posts, and social features
- **Database Optimization**: Implement local database for faster data retrieval and offline functionality

### 4.3.6 Geographic Expansion
- **Multi-Location Support**: Expand beyond Georgia Gwinnett College to support multiple cities and regions
- **International Support**: Add support for restaurants in different countries with multi-language support
- **Location History**: Track and display user's restaurant visit history

### 4.3.7 Additional Features
- **Restaurant Comparison**: Side-by-side comparison of multiple restaurants
- **Group Planning**: Features for planning group meals and coordinating with friends
- **Food Challenges**: Gamification elements such as food challenges and badges
- **Integration with Food Delivery**: Partner with food delivery services for in-app ordering
- **Loyalty Programs**: Integration with restaurant loyalty programs and rewards

## 4.4 Impact and Significance

TasteBuddies has the potential to significantly impact how users discover and share food experiences. By combining real-time restaurant data with intuitive category-based filtering, the application addresses real-world needs that existing platforms have not fully met. The focus on meal-specific discovery (especially fast food for breakfast) demonstrates an understanding of user behavior and preferences.

The application's architecture and implementation provide a solid foundation for future enhancements, making it scalable and adaptable to changing user needs and technological advancements.

---

# Section 5: References

## 5.1 Academic and Research References

1. Ricci, F., Rokach, L., & Shapira, B. (2015). *Recommender Systems Handbook* (2nd ed.). Springer.  
   - Provides foundational knowledge on recommendation systems and user behavior analysis.

2. Chen, L., & Pu, P. (2012). "Critiquing-based recommenders: survey and emerging trends." *User Modeling and User-Adapted Interaction*, 22(1-2), 125-150.  
   - Discusses user preferences and filtering mechanisms relevant to restaurant recommendation systems.

3. Adomavicius, G., & Tuzhilin, A. (2005). "Toward the next generation of recommender systems: A survey of the state-of-the-art and possible extensions." *IEEE Transactions on Knowledge and Data Engineering*, 17(6), 734-749.  
   - Explores recommendation system architectures and personalization techniques.

## 5.2 Technical Documentation

4. Google. (2024). *Google Places API Documentation*. Google Cloud Platform.  
   Retrieved from: https://developers.google.com/maps/documentation/places  
   - Official documentation for Google Places API used for restaurant data integration.

5. Google. (2024). *Google Maps SDK for Android Documentation*. Google Developers.  
   Retrieved from: https://developers.google.com/maps/documentation/android-sdk  
   - Documentation for Google Maps SDK implementation in the application.

6. Android Developers. (2024). *Android Developer Guide*. Google.  
   Retrieved from: https://developer.android.com/guide  
   - Comprehensive guide for Android application development.

7. Bumptech. (2024). *Glide Image Loading Library Documentation*.  
   Retrieved from: https://bumptech.github.io/glide/  
   - Documentation for the Glide library used for image loading and caching.

## 5.3 Industry and Application References

8. Yelp Inc. (2024). *Yelp Platform*.  
   Retrieved from: https://www.yelp.com  
   - Reference for existing food discovery platform features and user experience.

9. Google LLC. (2024). *Google Maps*.  
   Retrieved from: https://www.google.com/maps  
   - Reference for location-based restaurant search and mapping features.

10. Zomato Media Private Limited. (2024). *Zomato Platform*.  
    Retrieved from: https://www.zomato.com  
    - Reference for restaurant discovery and review platform functionality.

## 5.4 Design and User Experience References

11. Google Material Design. (2024). *Material Design Guidelines*. Google.  
    Retrieved from: https://material.io/design  
    - Design guidelines and principles followed in the application's user interface.

12. Android Developers. (2024). *Android Design Guidelines*. Google.  
    Retrieved from: https://developer.android.com/design  
    - Android-specific design patterns and best practices.

## 5.5 Development Tools and Frameworks

13. JetBrains. (2024). *Android Studio Documentation*.  
    Retrieved from: https://developer.android.com/studio  
    - Integrated development environment used for application development.

14. Gradle. (2024). *Gradle Build Tool Documentation*.  
    Retrieved from: https://docs.gradle.org  
    - Build automation tool documentation for dependency management.

## 5.6 Additional Resources

15. Stack Overflow. (2024). *Android Development Community*.  
    Retrieved from: https://stackoverflow.com/questions/tagged/android  
    - Community resources for troubleshooting and best practices.

16. GitHub. (2024). *Open Source Android Projects*.  
    Retrieved from: https://github.com/topics/android  
    - Reference for open-source Android projects and code examples.

---

**Note**: All web references were accessed in 2024. For the most current information, please refer to the official documentation websites listed above.

