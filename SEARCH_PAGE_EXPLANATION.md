# Search Page Screenshot Explanation

## What to Write in Your Report

### **3.6.1 Search Fragment: Category-Based Discovery Interface**

The Search page (`FragmentSearch`) serves as the primary discovery interface, providing users with two methods to find restaurants: text-based search and category-based navigation. This implementation demonstrates the use of `LinearLayout`, `GridLayout`, and interactive UI components.

**Layout Architecture:**

The fragment utilizes a vertical `LinearLayout` as its root container, creating a hierarchical structure:

1. **Header Section**: A horizontal `LinearLayout` displays the application title "TasteBuddies" with centered alignment and elevation for visual depth.

2. **Main Content Area**: A nested vertical `LinearLayout` with a cream background (`@color/background_cream`) contains the interactive elements:
   - Search bar component
   - Category button grid

**Search Bar Implementation:**

The search functionality is implemented using an `EditText` component wrapped in a `LinearLayout` container. Key features include:

- **Placeholder Text**: "What are you looking for?" guides user input
- **Search Icon**: A magnifying glass icon (`@android:drawable/ic_menu_search`) positioned at the right end
- **IME Action**: Configured with `imeOptions="actionSearch"` to display a search button on the keyboard
- **Clickable Container**: The entire search bar container is clickable, automatically focusing the `EditText` when tapped

**Search Functionality:**

The search bar implements intelligent query mapping through the `mapSearchToCategory()` method, which analyzes user input and maps common search terms to appropriate categories:

```java
private String mapSearchToCategory(String searchQuery) {
    String lowerQuery = searchQuery.toLowerCase();
    if (lowerQuery.contains("breakfast") || lowerQuery.contains("morning") || 
        lowerQuery.contains("pancake") || lowerQuery.contains("waffle")) {
        return "Breakfast";
    }
    // ... additional category mappings
}
```

When users press Enter or the search button, the application:
1. Retrieves the search query
2. Maps it to the appropriate category using keyword matching
3. Launches `CategoryResultsActivity` with the mapped category

**Category Grid Layout:**

The six category buttons are arranged using a `GridLayout` with the following configuration:
- **Column Count**: 2 columns
- **Row Count**: 3 rows
- **Alignment Mode**: `alignMargins` for consistent spacing
- **Equal Weight Distribution**: Each button uses `layout_columnWeight="1"` for uniform sizing

**Category Buttons:**

Each category button is implemented as a `LinearLayout` with:
- **Visual Design**: Light green background (`@drawable/button_background`) with rounded corners
- **Icon**: Category-specific icon (40dp × 40dp) with ivory tint
- **Text Label**: Category name in bold, ivory text
- **Interactive Properties**: `clickable="true"` and `focusable="true"` for touch feedback

The six categories are:
1. **Breakfast** - Fast food restaurants for morning meals
2. **Lunch** - Midday dining options
3. **Dinner** - Evening restaurant experiences
4. **Coffee** - Coffee shops and cafes
5. **Bar** - Bars and drinking establishments
6. **Dessert** - Dessert shops and bakeries

**Event Handling:**

Each category button is programmatically configured with click listeners:

```java
private void setupCategoryButton(View parentView, int viewId, String category) {
    View categoryView = parentView.findViewById(viewId);
    categoryView.setOnClickListener(v -> {
        openCategoryResults(category);
    });
}
```

When a category button is clicked, the application launches `CategoryResultsActivity` via an Intent, passing the selected category as an extra. This activity then queries the Google Places API to retrieve nearby restaurants matching the category.

**Navigation Integration:**

The bottom navigation bar is visible, with the "Search" icon highlighted (indicated by the light purple oval background), providing clear visual feedback about the current active screen. This demonstrates the integration between fragment-based navigation and the main activity's bottom navigation system.

**Design Principles:**

The Search page follows Material Design guidelines:
- **Consistent Color Scheme**: Light green buttons, dark green text, cream background
- **Visual Hierarchy**: Clear separation between header, search bar, and category grid
- **Touch Targets**: Adequate button sizes (120dp height) for comfortable interaction
- **Accessibility**: Content descriptions for icons, proper text contrast

**User Experience Flow:**

1. User navigates to Search tab (bottom navigation)
2. User can either:
   - Type a search query and press Enter → Query is mapped to category → Results displayed
   - Click a category button → Direct navigation to category results
3. Both methods launch `CategoryResultsActivity` with the appropriate category
4. Restaurant results are displayed using Google Places API integration

This dual-input approach (text search and category buttons) accommodates different user preferences: quick category selection for fast browsing, or text search for specific queries.

---

## Alternative Shorter Version (If Space is Limited)

### **3.6.1 Search Fragment: Discovery Interface**

The Search page (`FragmentSearch`) provides a dual-method discovery system combining text search and category-based navigation. The layout uses a vertical `LinearLayout` containing a search bar (`EditText` with search icon) and a `GridLayout` with six category buttons (Breakfast, Lunch, Dinner, Coffee, Bar, Dessert) arranged in a 2×3 grid.

**Key Features:**
- **Search Bar**: `EditText` with `imeOptions="actionSearch"` that maps user queries to categories using keyword matching
- **Category Grid**: `GridLayout` with equal-weight buttons, each containing an icon and label
- **Event Handling**: Both search and category buttons launch `CategoryResultsActivity` via Intent with category parameter
- **Visual Design**: Light green buttons with ivory icons/text, following Material Design principles

The implementation demonstrates efficient use of `LinearLayout` for vertical arrangement and `GridLayout` for responsive button layout, providing an intuitive interface for restaurant discovery.

---

## Key Technical Points to Highlight

1. ✅ **Layout Design**: LinearLayout (vertical) + GridLayout (2×3) for category buttons
2. ✅ **Search Functionality**: EditText with IME action and query mapping
3. ✅ **Category Navigation**: 6 clickable category buttons with icons
4. ✅ **Intent Navigation**: Launches CategoryResultsActivity with category data
5. ✅ **User Experience**: Dual input methods (text search + category buttons)
6. ✅ **Visual Design**: Material Design principles, consistent color scheme

---

## Screenshot Caption Suggestion

**Figure X.X: Search Fragment Interface**

The Search page demonstrates the category-based discovery system with a search bar and six category buttons arranged in a 2×3 grid. Users can either type a search query or click a category button to discover restaurants. The bottom navigation bar shows the Search tab as active (highlighted in purple).

