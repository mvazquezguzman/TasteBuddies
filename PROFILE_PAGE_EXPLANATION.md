# Profile Page Screenshot Explanation

## What to Write in Your Report

### **3.7 Profile Fragment: User Profile Management with Modal Dialogs**

This screenshot demonstrates the Profile page (`FragmentProfile`) implementation, showcasing user profile display, modal dialog interactions, and RecyclerView for post grid display. The three screens illustrate the main profile view, Edit Profile dialog, and Menu dialog.

**Screen 1 (Left): Main Profile View**

**Layout Architecture:**

The Profile Fragment uses a vertical `LinearLayout` as the root container, organized into distinct sections:

1. **Header Bar (Action Bar)**:
   - Horizontal `LinearLayout` with three elements:
     - **Edit Button**: `ImageButton` with pencil icon (`@drawable/ic_edit`) on the left
     - **Username Display**: Centered `TextView` showing "MyUser Name" with `layout_weight="1"` for centering
     - **Menu Button**: `ImageButton` with hamburger menu icon (`@drawable/ic_menu`) on the right
   - Uses `elevation="2dp"` for visual depth

2. **ScrollView Container**:
   - Wraps the main content for scrollable profile information
   - Contains a nested vertical `LinearLayout` with padding

3. **Profile Picture**:
   - Circular `ImageView` (100dp × 100dp) centered horizontally
   - Uses `UiUtils.makeCircular()` to create circular shape
   - Default Android icon displayed as placeholder
   - `scaleType="centerCrop"` for proper image scaling

4. **User Statistics Row**:
   - Horizontal `LinearLayout` displaying three statistics:
     - Posts count: "300 posts"
     - Followers count: "100 followers"
     - Following count: "103 following"
   - Each statistic uses two `TextView`s: one for the number (bold) and one for the label
   - Centered alignment with spacing between items

5. **User Information**:
   - **Full Name**: `TextView` centered below statistics
   - **Bio**: Multi-line `TextView` with user biography text
   - Both use appropriate text sizes and styling

6. **Content Tabs**:
   - Horizontal `LinearLayout` with two `ImageButton`s:
     - **Posts Tab**: Grid icon highlighted in tea green (active)
     - **Saved Tab**: Bookmark icon in gray (inactive)
   - Visual indicator shows which tab is active

7. **Posts Grid**:
   - `RecyclerView` with `GridLayoutManager` (3 columns)
   - Uses `PostGridAdapter` to display user posts in a grid format
   - Clickable posts navigate to `PostDetailActivity`

**Screen 2 (Middle): Edit Profile Dialog**

**Dialog Implementation:**

The Edit Profile functionality uses `AlertDialog` with a custom layout:

1. **Dialog Creation**:
   ```java
   AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
   builder.setTitle("Edit Profile");
   View dialogView = LayoutInflater.from(getActivity())
       .inflate(R.layout.dialog_edit_profile, null);
   builder.setView(dialogView);
   ```

2. **Custom Dialog Layout** (`dialog_edit_profile.xml`):
   - Contains two `EditText` fields:
     - **Username Field**: Single-line input for username
     - **Bio Field**: Multi-line input for biography text
   - Pre-populated with current user data

3. **Dialog Buttons**:
   - **Positive Button ("Save")**: Validates input and saves changes
     - Checks if username is empty
     - Updates profile information (currently disabled in implementation)
   - **Negative Button ("Cancel")**: Dismisses dialog without saving
   - **Neutral Button ("Change Photo")**: Opens gallery to select new profile picture
     - Uses Intent: `Intent.ACTION_PICK` with `MediaStore.Images.Media.EXTERNAL_CONTENT_URI`
     - Handles result in `onActivityResult()` method

4. **Image Selection**:
   ```java
   private void selectProfilePicture() {
       Intent intent = new Intent(Intent.ACTION_PICK, 
           MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
       startActivityForResult(intent, 100);
   }
   ```
   - Opens device gallery
   - Selected image is loaded as Bitmap and set to profile `ImageView`

**Screen 3 (Right): Menu Dialog**

**Menu Dialog Implementation:**

The Menu dialog provides additional options:

1. **Dialog Structure**:
   ```java
   AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
   builder.setTitle("Menu");
   builder.setItems(new String[]{"Sign Out"}, (dialog, which) -> {
       if (which == 0) {
           signOut();
       }
   });
   builder.show();
   ```

2. **Sign Out Functionality**:
   - Displays confirmation dialog: "Are you sure you want to sign out?"
   - On confirmation:
     - Clears `SharedPreferences` (user session data)
     - Launches `SignInActivity` with flags to clear activity stack
     - Finishes current activity
   ```java
   SharedPreferences sharedPreferences = requireActivity()
       .getSharedPreferences("TasteBuddiesPrefs", MODE_PRIVATE);
   SharedPreferences.Editor editor = sharedPreferences.edit();
   editor.clear();
   editor.apply();
   
   Intent intent = new Intent(getActivity(), SignInActivity.class);
   intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
   startActivity(intent);
   requireActivity().finish();
   ```

**Technical Implementation Details:**

**Layout Design:**
- **LinearLayout**: Primary layout manager for vertical and horizontal arrangements
- **ScrollView**: Enables scrolling for long profile content
- **RecyclerView**: Efficient grid display of posts using `GridLayoutManager`
- **ImageButton**: Interactive buttons with icon resources
- **Circular ImageView**: Custom utility method (`UiUtils.makeCircular()`) creates circular profile picture

**Dialog Management:**
- **AlertDialog**: Standard Android dialog for user interactions
- **Custom Layout Inflation**: `LayoutInflater.inflate()` for custom dialog views
- **Button Configuration**: Positive, negative, and neutral buttons for different actions
- **Modal Behavior**: Dialogs dim background and block interaction until dismissed

**Data Handling:**
- **SharedPreferences**: Stores user session data (userId, login status)
- **Intent Actions**: 
  - `Intent.ACTION_PICK` for gallery selection
  - Activity navigation with flags for sign-out flow
- **onActivityResult**: Handles image selection from gallery

**User Interaction Flow:**

1. **View Profile**: User navigates to Profile tab → Profile fragment displays
2. **Edit Profile**: User clicks edit icon → Edit Profile dialog appears
   - User can modify username and bio
   - User can change profile picture via "Change Photo" button
   - Changes saved or cancelled
3. **Menu Access**: User clicks menu icon → Menu dialog appears
   - User selects "Sign Out" → Confirmation dialog → User logged out

**Key Features Demonstrated:**
- ✅ Fragment-based profile display
- ✅ AlertDialog for modal interactions
- ✅ Custom dialog layouts with EditText inputs
- ✅ Intent-based image selection (gallery)
- ✅ SharedPreferences for session management
- ✅ RecyclerView with GridLayoutManager for post grid
- ✅ LinearLayout for horizontal/vertical arrangements
- ✅ Circular ImageView implementation
- ✅ Activity navigation with Intent flags

**Design Principles:**
- **Material Design**: Follows Android design guidelines
- **Consistent UI**: Matches app-wide color scheme (tea green, white, dark text)
- **User Feedback**: Toast messages for actions, confirmation dialogs for critical actions
- **Accessibility**: Content descriptions for icons, proper touch targets

---

## Screenshot Caption

**Figure 3.3: Profile Fragment with Modal Dialogs**

This screenshot demonstrates the Profile Fragment implementation with three views: (1) Main profile view showing user information, statistics, and post grid; (2) Edit Profile dialog with username and bio input fields, plus Change Photo functionality; (3) Menu dialog with Sign Out option. The implementation showcases AlertDialog usage, Intent-based image selection, SharedPreferences for session management, and RecyclerView for grid layout.

---

## Alternative Shorter Version

### **3.7 Profile Fragment: User Profile and Dialog Management**

The Profile Fragment (`FragmentProfile`) displays user information using a vertical `LinearLayout` with ScrollView for scrollable content. The layout includes a circular profile picture, user statistics (posts, followers, following) in a horizontal `LinearLayout`, and a `RecyclerView` with `GridLayoutManager` (3 columns) for displaying user posts. The fragment implements two `AlertDialog` instances: (1) Edit Profile dialog with custom layout containing `EditText` fields for username and bio, plus a "Change Photo" button that uses `Intent.ACTION_PICK` to open the gallery; (2) Menu dialog with Sign Out option that clears `SharedPreferences` and navigates to `SignInActivity`. The implementation demonstrates modal dialog management, Intent-based image selection, and session management using SharedPreferences.

