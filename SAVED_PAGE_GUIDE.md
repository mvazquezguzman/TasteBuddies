# Saved Page Screenshot and Explanation Guide

## Screenshots to Take

### 1. **List View (Default View)**
**When to take:**
- Navigate to the Saved tab (bottom navigation)
- Make sure you're in List view (List button should be highlighted in green)
- If you have saved posts, take a screenshot showing the list
- If you don't have saved posts, take a screenshot showing the empty state

**What to capture:**
- The "Saved" title at the top
- The List/Map toggle buttons (List should be highlighted)
- The "Want to Try" section with bookmark icon
- Either: List of saved posts OR "No saved posts yet" message
- Bottom navigation bar

**Why this screenshot:**
- Shows the default list view implementation
- Demonstrates the toggle button UI
- Shows empty state handling

---

### 2. **Map View**
**When to take:**
- Still on the Saved tab
- Click the "Map" button (it should turn green, List button turns white)
- Wait for the map to load
- If you have saved posts with location data, markers should appear on the map
- If no posts or no location data, just show the map view

**What to capture:**
- The Map button highlighted in green
- The Google Map view
- Map markers (if you have saved posts with coordinates)
- The map controls (zoom, compass, etc.)

**Why this screenshot:**
- Demonstrates the dual view functionality
- Shows Google Maps integration
- Shows location-based marker placement

---

### 3. **Toggle Button Close-up (Optional)**
**When to take:**
- On the Saved page
- Focus on the List/Map toggle buttons
- Show both states if possible (List selected and Map selected)

**What to capture:**
- The toggle button container
- The visual difference between selected (green) and unselected (white) states
- The divider between buttons

**Why this screenshot:**
- Shows the UI design of the toggle mechanism
- Demonstrates state management

---

## What to Explain in Your Report

### 1. **Dual View Architecture**
**Explain:**
- The Saved page implements a dual-view system allowing users to switch between List and Map views
- This provides flexibility: List view for detailed browsing, Map view for geographic visualization
- The toggle is implemented using two LinearLayout buttons with visual state indicators

**Code reference:**
```java
// Toggle between views
private void setListView() {
    isListView = true;
    buttonList.setBackgroundColor(tea_green); // Active state
    buttonMap.setBackgroundColor(white);      // Inactive state
    listViewSaved.setVisibility(View.VISIBLE);
    mapViewContainer.setVisibility(View.GONE);
}

private void setMapView() {
    isListView = false;
    buttonList.setBackgroundColor(white);      // Inactive state
    buttonMap.setBackgroundColor(tea_green);   // Active state
    listViewSaved.setVisibility(View.GONE);
    mapViewContainer.setVisibility(View.VISIBLE);
}
```

---

### 2. **Google Maps Integration**
**Explain:**
- The MapView is dynamically created and added to a FrameLayout container
- Implements OnMapReadyCallback to handle map initialization
- Properly manages MapView lifecycle (onCreate, onResume, onPause, onDestroy, onSaveInstanceState)

**Key points:**
- MapView is created programmatically, not in XML (allows dynamic addition)
- Lifecycle methods ensure proper resource management
- Map markers are added based on post coordinates (latitude/longitude)

**Code reference:**
```java
// MapView initialization
mapView = new MapView(requireContext());
mapView.onCreate(savedInstanceState);
mapView.getMapAsync(this); // Callback when map is ready

@Override
public void onMapReady(GoogleMap map) {
    googleMap = map;
    updateMapMarkers();
}
```

---

### 3. **Marker Placement and Location Tagging**
**Explain:**
- Each saved post can have location coordinates (latitude, longitude) stored in the database
- When switching to Map view, the app iterates through saved posts and places markers
- Markers show restaurant name (or food name) as title and location as snippet
- Camera automatically zooms to show all markers or defaults to a central location

**Key points:**
- Only posts with valid coordinates (not 0,0) are displayed as markers
- First location found becomes the camera focus point
- Markers are cleared and recreated each time the map view is activated

**Code reference:**
```java
private void updateMapMarkers() {
    googleMap.clear();
    for (Post post : savedPosts) {
        if (post.getLatitude() != 0 && post.getLongitude() != 0) {
            LatLng location = new LatLng(post.getLatitude(), post.getLongitude());
            googleMap.addMarker(new MarkerOptions()
                .position(location)
                .title(post.getRestaurantName())
                .snippet(post.getLocation()));
        }
    }
    // Move camera to first location
    if (firstLocation != null) {
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(firstLocation, 12f));
    }
}
```

---

### 4. **Empty State Handling**
**Explain:**
- The app gracefully handles the case when no posts are saved
- Shows "No saved posts yet" message in List view
- Hides the ListView and shows the empty state TextView
- In Map view, simply shows an empty map (no markers)

**Implementation:**
```java
if (savedPosts.isEmpty()) {
    textViewEmpty.setVisibility(View.VISIBLE);
    listViewSaved.setVisibility(View.GONE);
} else {
    textViewEmpty.setVisibility(View.GONE);
    listViewSaved.setVisibility(View.VISIBLE);
}
```

---

### 5. **Post Interaction**
**Explain:**
- Posts in the ListView are clickable
- Clicking a post navigates to PostDetailActivity
- Uses Intent with postId extra to pass data between activities
- This allows users to view full post details from saved posts

**Code reference:**
```java
listViewSaved.setOnItemClickListener((parent, view, position, id) -> {
    Post post = (Post) adapter.getItem(position);
    Intent intent = new Intent(getActivity(), PostDetailActivity.class);
    intent.putExtra("postId", post.getPostId());
    startActivity(intent);
});
```

---

### 6. **"Want to Try" Section**
**Explain:**
- Displays a count of saved posts (currently shows "0" but can be updated)
- Provides quick access to saved posts
- Uses a bookmark icon for visual consistency
- Clickable to refresh the saved posts list

---

### 7. **Layout Design**
**Explain:**
- Uses LinearLayout as root container for vertical arrangement
- Toggle buttons are in a horizontal LinearLayout with visual divider
- ListView and MapView share the same container space (using visibility toggling)
- Follows Material Design principles with proper spacing and elevation

---

## Suggested Report Section Text

### 3.7.1 Saved Page: Dual View Implementation

The Saved page (`FragmentSaved`) demonstrates a sophisticated dual-view architecture that allows users to view their saved posts in two distinct formats: a traditional list view and an interactive map view. This implementation showcases the integration of Google Maps SDK with ListView components.

**List View Implementation:**
The default view displays saved posts in a scrollable ListView, using the same `PostAdapter` employed in the Home feed. This ensures visual consistency across the application. The list view includes a "Want to Try" section that displays the count of saved posts, providing users with quick access to their bookmarked content.

**Map View Implementation:**
When users toggle to Map view, the application dynamically switches from ListView to Google Maps. The MapView is programmatically created and added to a FrameLayout container, allowing for flexible view management. Each saved post with valid location coordinates (latitude and longitude) is displayed as a marker on the map, with the restaurant name as the marker title and location as the snippet.

**Toggle Mechanism:**
The view switching is implemented through a custom toggle button interface. Two LinearLayout components serve as buttons, with visual state indicators: the active view button displays in tea green, while the inactive button remains white. This provides clear visual feedback about the current view mode.

**Marker Placement:**
The map markers are dynamically generated based on post location data stored in the SQLite database. The application iterates through saved posts, checks for valid coordinates, and places markers accordingly. The camera automatically zooms to encompass all markers, or defaults to a central location if no posts have coordinates.

**Lifecycle Management:**
Proper MapView lifecycle management is crucial for resource efficiency. The fragment implements all necessary lifecycle methods (onCreate, onResume, onPause, onDestroy, onSaveInstanceState, onLowMemory) to ensure the MapView is properly initialized, resumed, paused, and destroyed, preventing memory leaks and ensuring optimal performance.

**Empty State Handling:**
The application gracefully handles the empty state scenario. When no posts are saved, the ListView is hidden and a "No saved posts yet" message is displayed, providing clear feedback to users. In Map view, an empty map is shown without markers.

This dual-view implementation enhances user experience by providing multiple perspectives on saved content, combining the detailed information of a list view with the geographic context of a map view.

---

## Tips for Taking Good Screhots

1. **Use Android Studio's built-in screenshot tool** (camera icon in emulator controls)
2. **Ensure good lighting/visibility** - Make sure text and UI elements are clearly visible
3. **Show the full screen** - Capture the entire Saved page including navigation
4. **Take multiple angles** - Show both List and Map views
5. **Add annotations** (optional) - Use arrows or labels to highlight key features
6. **Consistent styling** - Use the same emulator/device for all screenshots

---

## Checklist Before Taking Screenshots

- [ ] App is running on emulator/device
- [ ] Navigated to Saved tab (bottom navigation)
- [ ] List view is visible (default state)
- [ ] If you have saved posts, they are displayed
- [ ] If no saved posts, empty state is visible
- [ ] Toggle buttons are visible
- [ ] Map view works when toggled
- [ ] Markers appear on map (if you have posts with location data)
- [ ] Screenshots are clear and professional

