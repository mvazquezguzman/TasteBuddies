# Search to Results Flow: Screenshot Explanation

## What to Write in Your Report

### **3.6.2 Search-to-Results Navigation Flow**

This screenshot demonstrates the complete user flow from category selection to restaurant results display, showcasing the integration between `FragmentSearch` and `CategoryResultsActivity`, along with Google Places API integration.

**Left Screen: Search Fragment (`FragmentSearch`)**

The Search Fragment serves as the discovery entry point, implementing a dual-input system:

1. **Layout Structure**: Uses a vertical `LinearLayout` containing:
   - Header with application title
   - Search bar (`EditText` with `imeOptions="actionSearch"`)
   - Category button grid (`GridLayout` with 2 columns, 3 rows)

2. **Category Buttons**: Six interactive buttons arranged in a `GridLayout`:
   - Each button is a `LinearLayout` containing an `ImageView` (category icon) and `TextView` (category label)
   - Buttons use `clickable="true"` and `focusable="true"` for touch interaction
   - Visual design: Light green background with ivory-colored icons and text

3. **Event Handling**: When a category button is clicked (e.g., "Dessert"), the `setupCategoryButton()` method triggers:
   ```java
   categoryView.setOnClickListener(v -> {
       openCategoryResults(category);
   });
   ```

4. **Navigation**: The `openCategoryResults()` method creates an Intent to launch `CategoryResultsActivity`:
   ```java
   Intent intent = new Intent(getActivity(), CategoryResultsActivity.class);
   intent.putExtra("category", category);
   startActivity(intent);
   ```

**Right Screen: Category Results Activity (`CategoryResultsActivity`)**

The Category Results Activity displays restaurant search results fetched from Google Places API:

1. **Activity Initialization**:
   - Receives the selected category via Intent extra: `getIntent().getStringExtra("category")`
   - Sets the title dynamically: `category + " near Georgia Gwinnett College"`
   - Initializes `PlacesApiService` for API calls

2. **API Integration**:
   - Uses fixed location coordinates (Georgia Gwinnett College: 33.9425, -84.0686)
   - Searches within 5km radius using Google Places Nearby Search API
   - Implements `PlacesCallback` interface for asynchronous API response handling

3. **ListView Implementation**:
   - Uses `ListView` with custom `RestaurantCardAdapter` to display results
   - Shows `ProgressBar` during API loading
   - Handles empty state with fallback to sample data if API fails

4. **Restaurant Card Layout** (`item_restaurant_card.xml`):
   Each card displays comprehensive restaurant information:
   - **Restaurant Name**: Bold `TextView` (20sp)
   - **Rating Display**: `RatingBar` (5 stars) + `TextView` showing rating value and review count
   - **Type and Status**: Restaurant type (e.g., "bakery") and distance (e.g., "3.5 mi")
   - **Service Options**: "Dine-in · Takeout" indicators
   - **Operating Hours**: Simplified format (e.g., "8:00 AM - 9:00 PM")
   - **Contact Information**: Phone number (clickable to dial) and website (clickable to open browser)
   - **Restaurant Image**: `ImageView` loaded using Glide library from Google Places Photo API
   - **Action Buttons**: Three buttons for "Directions", "Call", and "Website" functionality

5. **Image Loading**:
   - Restaurant photos are fetched from Google Places Photo API
   - Glide library handles asynchronous image loading and caching
   - Placeholder image shown during loading
   - Error handling displays default image if photo unavailable

6. **Interactive Elements**:
   - **Directions Button**: Opens Google Maps with restaurant location using Intent
   - **Call Button**: Launches phone dialer with restaurant phone number
   - **Website Button**: Opens restaurant website in browser
   - **Phone/Website TextViews**: Also clickable for direct interaction

**Technical Implementation Details:**

**Data Flow:**
1. User clicks "Dessert" category button in Search Fragment
2. Intent created with category="Dessert"
3. `CategoryResultsActivity` launched
4. Activity calls `PlacesApiService.searchNearbyRestaurants()`
5. API request sent to Google Places with:
   - Location: GGC coordinates
   - Type: Category-specific filter (e.g., "bakery" for dessert)
   - Radius: 5000 meters
   - Meal filters: Category-specific (e.g., `serves_dessert`)
6. JSON response parsed into `RestaurantInfo` objects
7. Additional Place Details API call fetches phone, website, and hours
8. `RestaurantCardAdapter` inflates `item_restaurant_card.xml` for each restaurant
9. ListView displays scrollable list of restaurant cards

**Adapter Implementation:**
```java
public class RestaurantCardAdapter extends BaseAdapter {
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // ViewHolder pattern for performance
        // Load image with Glide
        // Set click listeners for action buttons
        // Display all restaurant information
    }
}
```

**Key Features Demonstrated:**
- ✅ Fragment-to-Activity navigation using Intents
- ✅ Google Places API integration (Nearby Search + Place Details)
- ✅ Custom ListView adapter with ViewHolder pattern
- ✅ Asynchronous API calls with callback handling
- ✅ Image loading with Glide library
- ✅ Multiple Intent actions (Maps, Dialer, Browser)
- ✅ Real-time restaurant data (ratings, hours, status)
- ✅ Responsive UI with loading indicators

**User Experience:**
This flow provides users with:
- Quick category-based discovery (one-tap access)
- Real-time restaurant information from Google Places
- Visual restaurant cards with photos
- Multiple interaction options (directions, call, website)
- Accurate location-based results near Georgia Gwinnett College

---

## Screenshot Caption

**Figure 3.2: Search-to-Results Navigation Flow**

This screenshot illustrates the complete user journey from category selection to restaurant results display. The left screen shows the Search Fragment with six category buttons, while the right screen displays the Category Results Activity showing dessert restaurants near Georgia Gwinnett College, fetched from Google Places API and displayed in a scrollable ListView with detailed restaurant cards.

---

## Alternative Shorter Version

### **3.6.2 Search-to-Results Flow**

This screenshot demonstrates the navigation flow from `FragmentSearch` to `CategoryResultsActivity`. When a user clicks a category button (e.g., "Dessert"), an Intent launches `CategoryResultsActivity` with the selected category. The activity queries Google Places API for nearby restaurants, displaying results in a `ListView` using `RestaurantCardAdapter`. Each restaurant card shows comprehensive information including name, rating, hours, phone, website, and a photo loaded via Glide. Action buttons enable users to get directions, call, or visit the restaurant's website, demonstrating Intent-based navigation and API integration for real-time restaurant discovery.

