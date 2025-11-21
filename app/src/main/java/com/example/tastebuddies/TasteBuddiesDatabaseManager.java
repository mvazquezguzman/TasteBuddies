package com.example.tastebuddies;

import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;

public class TasteBuddiesDatabaseManager {
    private static final String DATABASE_NAME = "tastebuddies.db";
    private static final int DB_VERSION = 2;

    public static final String TABLE_USERS = "users";
    public static final String TABLE_POSTS = "posts";
    public static final String TABLE_COMMENTS = "comments";
    public static final String TABLE_LIKES = "likes";
    public static final String TABLE_BOOKMARKS = "bookmarks";
    public static final String TABLE_SAVED_PLACES = "saved_places";

    public static final String DEMO_EMAIL = "demo@tastebuddies.com";
    public static final String DEMO_PASSWORD = "demo123";

    private static TasteBuddiesDatabaseManager instance;
    private final Context appContext;
    private SQLiteDatabase database;

    private TasteBuddiesDatabaseManager(Context context) {
        this.appContext = context.getApplicationContext();
        openDatabase();
        createTablesIfNeeded();
        seedDemoData();
    }

    public static synchronized TasteBuddiesDatabaseManager getInstance(Context context) {
        if (instance == null) {
            instance = new TasteBuddiesDatabaseManager(context);
        }
        return instance;
    }

    public SQLiteDatabase getDatabase() {
        if (database == null || !database.isOpen()) {
            openDatabase();
        }
        return database;
    }

    public void close() {
        if (database != null && database.isOpen()) {
            database.close();
        }
    }

    private void openDatabase() {
        database = appContext.openOrCreateDatabase(
                DATABASE_NAME,
                Context.MODE_PRIVATE,
                null
        );
        setDatabaseVersion();
    }

    private void setDatabaseVersion() {
        int currentVersion = database.getVersion();
        if (currentVersion != DB_VERSION) {
            database.setVersion(DB_VERSION);
        }
        database.execSQL("PRAGMA foreign_keys=ON;");
    }

    private void createTablesIfNeeded() {
        database.execSQL("CREATE TABLE IF NOT EXISTS " + TABLE_USERS + " (" +
                "user_id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "username TEXT NOT NULL," +
                "email TEXT NOT NULL UNIQUE," +
                "password TEXT NOT NULL," +
                "display_name TEXT," +
                "bio TEXT," +
                "profile_picture BLOB," +
                "created_at INTEGER DEFAULT (strftime('%s','now') * 1000)" +
                ")");

        database.execSQL("CREATE TABLE IF NOT EXISTS " + TABLE_POSTS + " (" +
                "post_id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "user_id INTEGER NOT NULL," +
                "food_name TEXT NOT NULL," +
                "review TEXT," +
                "rating INTEGER," +
                "post_image BLOB," +
                "restaurant_name TEXT," +
                "location TEXT," +
                "latitude REAL," +
                "longitude REAL," +
                "created_at INTEGER DEFAULT (strftime('%s','now') * 1000)," +
                "like_count INTEGER DEFAULT 0," +
                "FOREIGN KEY(user_id) REFERENCES " + TABLE_USERS + "(user_id) ON DELETE CASCADE" +
                ")");

        database.execSQL("CREATE TABLE IF NOT EXISTS " + TABLE_COMMENTS + " (" +
                "comment_id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "post_id INTEGER NOT NULL," +
                "user_id INTEGER NOT NULL," +
                "comment_text TEXT NOT NULL," +
                "created_at INTEGER DEFAULT (strftime('%s','now') * 1000)," +
                "FOREIGN KEY(post_id) REFERENCES " + TABLE_POSTS + "(post_id) ON DELETE CASCADE," +
                "FOREIGN KEY(user_id) REFERENCES " + TABLE_USERS + "(user_id) ON DELETE CASCADE" +
                ")");

        database.execSQL("CREATE TABLE IF NOT EXISTS " + TABLE_LIKES + " (" +
                "post_id INTEGER NOT NULL," +
                "user_id INTEGER NOT NULL," +
                "created_at INTEGER DEFAULT (strftime('%s','now') * 1000)," +
                "PRIMARY KEY(post_id, user_id)," +
                "FOREIGN KEY(post_id) REFERENCES " + TABLE_POSTS + "(post_id) ON DELETE CASCADE," +
                "FOREIGN KEY(user_id) REFERENCES " + TABLE_USERS + "(user_id) ON DELETE CASCADE" +
                ")");

        database.execSQL("CREATE TABLE IF NOT EXISTS " + TABLE_BOOKMARKS + " (" +
                "post_id INTEGER NOT NULL," +
                "user_id INTEGER NOT NULL," +
                "created_at INTEGER DEFAULT (strftime('%s','now') * 1000)," +
                "PRIMARY KEY(post_id, user_id)," +
                "FOREIGN KEY(post_id) REFERENCES " + TABLE_POSTS + "(post_id) ON DELETE CASCADE," +
                "FOREIGN KEY(user_id) REFERENCES " + TABLE_USERS + "(user_id) ON DELETE CASCADE" +
                ")");

        database.execSQL("CREATE TABLE IF NOT EXISTS " + TABLE_SAVED_PLACES + " (" +
                "saved_place_id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "user_id INTEGER NOT NULL," +
                "place_name TEXT NOT NULL," +
                "place_type TEXT," +
                "rating REAL," +
                "review_count INTEGER," +
                "distance TEXT," +
                "status TEXT," +
                "service_options TEXT," +
                "phone TEXT," +
                "website TEXT," +
                "hours TEXT," +
                "latitude REAL," +
                "longitude REAL," +
                "image_url TEXT," +
                "created_at INTEGER DEFAULT (strftime('%s','now') * 1000)," +
                "FOREIGN KEY(user_id) REFERENCES " + TABLE_USERS + "(user_id) ON DELETE CASCADE" +
                ")");

        database.execSQL("CREATE INDEX IF NOT EXISTS idx_posts_user ON " +
                TABLE_POSTS + "(user_id, created_at DESC)");
        database.execSQL("CREATE INDEX IF NOT EXISTS idx_comments_post ON " +
                TABLE_COMMENTS + "(post_id, created_at DESC)");
        database.execSQL("CREATE INDEX IF NOT EXISTS idx_saved_places_user ON " +
                TABLE_SAVED_PLACES + "(user_id, created_at DESC)");
    }

    private void seedDemoData() {
        if (isDemoUserPresent()) {
            return;
        }

        long demoUserId = insertDemoUser();
        insertDemoPosts(demoUserId);
    }

    private boolean isDemoUserPresent() {
        boolean exists = false;
        Cursor cursor = null;
        try {
            cursor = database.rawQuery(
                    "SELECT user_id FROM " + TABLE_USERS + " WHERE email = ? LIMIT 1",
                    new String[]{DEMO_EMAIL}
            );
            exists = cursor.moveToFirst();
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return exists;
    }

    private long insertDemoUser() {
        byte[] profileBytes = getBytesFromResource(R.drawable.icon_user);
        SQLiteStatement statement = database.compileStatement(
                "INSERT INTO " + TABLE_USERS + " " +
                        "(username, email, password, display_name, bio, profile_picture, created_at) " +
                        "VALUES (?, ?, ?, ?, ?, ?, ?)"
        );
        statement.bindString(1, "demo");
        statement.bindString(2, DEMO_EMAIL);
        statement.bindString(3, DEMO_PASSWORD);
        statement.bindString(4, "Demo User");
        statement.bindString(5, "Exploring tasty bites around town!");
        if (profileBytes != null) {
            statement.bindBlob(6, profileBytes);
        } else {
            statement.bindNull(6);
        }
        statement.bindLong(7, System.currentTimeMillis());

        long userId = statement.executeInsert();
        statement.close();
        return userId;
    }

    private void insertDemoPosts(long userId) {
        insertPost(userId,
                "Sunrise Pancakes",
                "Fluffy pancakes with maple syrup.",
                5,
                R.drawable.food1,
                "Morning Glory Cafe",
                "123 Sunshine Ave",
                37.7749,
                -122.4194);

        insertPost(userId,
                "Mediterranean Bowl",
                "Fresh and healthy lunch option.",
                4,
                R.drawable.food2,
                "Oasis Kitchen",
                "456 Olive St",
                34.0522,
                -118.2437);

        insertPost(userId,
                "Sushi Night",
                "Chef's special sushi platter.",
                5,
                R.drawable.food3,
                "Sakura House",
                "789 Blossom Rd",
                40.7128,
                -74.0060);
    }

    private void insertPost(long userId,
                            String foodName,
                            String review,
                            int rating,
                            int imageResId,
                            String restaurantName,
                            String location,
                            double latitude,
                            double longitude) {
        byte[] postBytes = getBytesFromResource(imageResId);
        SQLiteStatement statement = database.compileStatement(
                "INSERT INTO " + TABLE_POSTS + " (" +
                        "user_id, food_name, review, rating, post_image, restaurant_name, " +
                        "location, latitude, longitude, created_at, like_count" +
                        ") VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)"
        );
        statement.bindLong(1, userId);
        statement.bindString(2, foodName);
        statement.bindString(3, review);
        statement.bindLong(4, rating);
        if (postBytes != null) {
            statement.bindBlob(5, postBytes);
        } else {
            statement.bindNull(5);
        }
        statement.bindString(6, restaurantName);
        statement.bindString(7, location);
        statement.bindDouble(8, latitude);
        statement.bindDouble(9, longitude);
        statement.bindLong(10, System.currentTimeMillis());
        statement.bindLong(11, 0);
        statement.executeInsert();
        statement.close();
    }

    private byte[] getBytesFromResource(int resourceId) {
        Bitmap bitmap = BitmapFactory.decodeResource(appContext.getResources(), resourceId);
        if (bitmap == null) {
            return null;
        }
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 85, stream);
        return stream.toByteArray();
    }

    public void executeSql(String sql) throws SQLException {
        getDatabase().execSQL(sql);
    }

    public void executeSql(String sql, Object[] bindArgs) throws SQLException {
        getDatabase().execSQL(sql, bindArgs);
    }

    public User authenticateUser(String email, String password) {
        User user = null;
        Cursor cursor = null;
        try {
            cursor = database.rawQuery(
                    "SELECT user_id, username, email, display_name, bio, profile_picture " +
                            "FROM " + TABLE_USERS +
                            " WHERE email = ? AND password = ? LIMIT 1",
                    new String[]{email, password});
            if (cursor.moveToFirst()) {
                user = mapUser(cursor);
            }
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return user;
    }

    public boolean isEmailTaken(String email) {
        Cursor cursor = null;
        try {
            cursor = database.rawQuery(
                    "SELECT user_id FROM " + TABLE_USERS + " WHERE email = ? LIMIT 1",
                    new String[]{email});
            return cursor.moveToFirst();
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }

    public long createUser(String username, String email, String password) {
        SQLiteStatement statement = database.compileStatement(
                "INSERT INTO " + TABLE_USERS +
                        "(username, email, password, display_name, bio, profile_picture, created_at) " +
                        "VALUES (?, ?, ?, ?, ?, ?, ?)");
        statement.bindString(1, username);
        statement.bindString(2, email);
        statement.bindString(3, password);
        statement.bindString(4, username);
        statement.bindString(5, "");
        statement.bindNull(6);
        statement.bindLong(7, System.currentTimeMillis());
        long id = statement.executeInsert();
        statement.close();
        return id;
    }

    public User getUserById(int userId) {
        User user = null;
        Cursor cursor = null;
        try {
            cursor = database.rawQuery(
                    "SELECT user_id, username, email, display_name, bio, profile_picture " +
                            "FROM " + TABLE_USERS + " WHERE user_id = ? LIMIT 1",
                    new String[]{String.valueOf(userId)});
            if (cursor.moveToFirst()) {
                user = mapUser(cursor);
            }
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return user;
    }

    public void updateUserProfile(int userId, String displayName, String bio) {
        SQLiteStatement statement = database.compileStatement(
                "UPDATE " + TABLE_USERS + " SET display_name = ?, bio = ? WHERE user_id = ?");
        statement.bindString(1, displayName);
        statement.bindString(2, bio);
        statement.bindLong(3, userId);
        statement.executeUpdateDelete();
        statement.close();
    }

    public List<Post> getRecentPosts(int limit, int viewerUserId) {
        List<Post> posts = new ArrayList<>();
        String limitClause = limit > 0 ? " LIMIT " + limit : "";
        Cursor cursor = null;
        try {
            // First, get post metadata without BLOBs to avoid CursorWindow size issues
            cursor = database.rawQuery(
                    "SELECT p.post_id, p.user_id, u.username, " +
                            "p.food_name, p.review, p.rating, p.restaurant_name, " +
                            "p.location, p.latitude, p.longitude, p.created_at, " +
                            "(SELECT COUNT(*) FROM " + TABLE_COMMENTS + " c WHERE c.post_id = p.post_id) AS comment_count, " +
                            "(SELECT COUNT(*) FROM " + TABLE_LIKES + " l WHERE l.post_id = p.post_id) AS like_count, " +
                            "CASE WHEN EXISTS (SELECT 1 FROM " + TABLE_LIKES + " l WHERE l.post_id = p.post_id AND l.user_id = ?) THEN 1 ELSE 0 END AS is_liked, " +
                            "CASE WHEN EXISTS (SELECT 1 FROM " + TABLE_BOOKMARKS + " b WHERE b.post_id = p.post_id AND b.user_id = ?) THEN 1 ELSE 0 END AS is_bookmarked " +
                            "FROM " + TABLE_POSTS + " p " +
                            "JOIN " + TABLE_USERS + " u ON u.user_id = p.user_id " +
                            "ORDER BY p.created_at DESC" + limitClause,
                    new String[]{String.valueOf(viewerUserId), String.valueOf(viewerUserId)});
            
            // Fetch BLOBs separately for each post to avoid CursorWindow overflow
            while (cursor.moveToNext()) {
                try {
                    Post post = mapPostWithoutBlobs(cursor);
                    // Fetch BLOBs separately
                    post.setPostImage(getPostImageBlob(post.getPostId()));
                    post.setProfilePicture(getUserProfilePictureBlob(post.getUserId()));
                    posts.add(post);
                } catch (Exception e) {
                    // Skip posts that cause errors (e.g., corrupted data)
                    e.printStackTrace();
                }
            }
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return posts;
    }

    public List<Post> getPostsByUser(int userId, int viewerUserId) {
        List<Post> posts = new ArrayList<>();
        Cursor cursor = null;
        try {
            // Get post metadata without BLOBs to avoid CursorWindow size issues
            cursor = database.rawQuery(
                    "SELECT p.post_id, p.user_id, u.username, " +
                            "p.food_name, p.review, p.rating, p.restaurant_name, " +
                            "p.location, p.latitude, p.longitude, p.created_at, " +
                            "(SELECT COUNT(*) FROM " + TABLE_COMMENTS + " c WHERE c.post_id = p.post_id) AS comment_count, " +
                            "(SELECT COUNT(*) FROM " + TABLE_LIKES + " l WHERE l.post_id = p.post_id) AS like_count, " +
                            "CASE WHEN EXISTS (SELECT 1 FROM " + TABLE_LIKES + " l WHERE l.post_id = p.post_id AND l.user_id = ?) THEN 1 ELSE 0 END AS is_liked, " +
                            "CASE WHEN EXISTS (SELECT 1 FROM " + TABLE_BOOKMARKS + " b WHERE b.post_id = p.post_id AND b.user_id = ?) THEN 1 ELSE 0 END AS is_bookmarked " +
                            "FROM " + TABLE_POSTS + " p " +
                            "JOIN " + TABLE_USERS + " u ON u.user_id = p.user_id " +
                            "WHERE p.user_id = ? " +
                            "ORDER BY p.created_at DESC",
                    new String[]{String.valueOf(viewerUserId), String.valueOf(viewerUserId), String.valueOf(userId)});
            
            // Fetch BLOBs separately for each post to avoid CursorWindow overflow
            while (cursor.moveToNext()) {
                try {
                    Post post = mapPostWithoutBlobs(cursor);
                    // Fetch BLOBs separately
                    post.setPostImage(getPostImageBlob(post.getPostId()));
                    post.setProfilePicture(getUserProfilePictureBlob(post.getUserId()));
                    posts.add(post);
                } catch (Exception e) {
                    // Skip posts that cause errors (e.g., corrupted data)
                    e.printStackTrace();
                }
            }
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return posts;
    }

    public List<Post> getBookmarkedPosts(int userId) {
        List<Post> posts = new ArrayList<>();
        Cursor cursor = null;
        try {
            // Get post metadata without BLOBs to avoid CursorWindow size issues
            cursor = database.rawQuery(
                    "SELECT p.post_id, p.user_id, u.username, " +
                            "p.food_name, p.review, p.rating, p.restaurant_name, " +
                            "p.location, p.latitude, p.longitude, p.created_at, " +
                            "(SELECT COUNT(*) FROM " + TABLE_COMMENTS + " c WHERE c.post_id = p.post_id) AS comment_count, " +
                            "(SELECT COUNT(*) FROM " + TABLE_LIKES + " l WHERE l.post_id = p.post_id) AS like_count, " +
                            "CASE WHEN EXISTS (SELECT 1 FROM " + TABLE_LIKES + " l WHERE l.post_id = p.post_id AND l.user_id = ?) THEN 1 ELSE 0 END AS is_liked, " +
                            "1 AS is_bookmarked " +
                            "FROM " + TABLE_BOOKMARKS + " b " +
                            "JOIN " + TABLE_POSTS + " p ON p.post_id = b.post_id " +
                            "JOIN " + TABLE_USERS + " u ON u.user_id = p.user_id " +
                            "WHERE b.user_id = ? " +
                            "ORDER BY p.created_at DESC",
                    new String[]{String.valueOf(userId), String.valueOf(userId)});
            
            // Fetch BLOBs separately for each post to avoid CursorWindow overflow
            while (cursor.moveToNext()) {
                try {
                    Post post = mapPostWithoutBlobs(cursor);
                    // Fetch BLOBs separately
                    post.setPostImage(getPostImageBlob(post.getPostId()));
                    post.setProfilePicture(getUserProfilePictureBlob(post.getUserId()));
                    posts.add(post);
                } catch (Exception e) {
                    // Skip posts that cause errors (e.g., corrupted data)
                    e.printStackTrace();
                }
            }
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return posts;
    }

    public int getBookmarkCount(int userId) {
        SQLiteStatement statement = database.compileStatement(
                "SELECT COUNT(*) FROM " + TABLE_BOOKMARKS + " WHERE user_id = ?");
        statement.bindLong(1, userId);
        int count = (int) statement.simpleQueryForLong();
        statement.close();
        return count;
    }

    public int getPostCountByUser(int userId) {
        SQLiteStatement statement = database.compileStatement(
                "SELECT COUNT(*) FROM " + TABLE_POSTS + " WHERE user_id = ?");
        statement.bindLong(1, userId);
        int count = (int) statement.simpleQueryForLong();
        statement.close();
        return count;
    }

    public Post getPostById(int postId, int viewerUserId) {
        Post post = null;
        Cursor cursor = null;
        try {
            // Get post metadata without BLOBs to avoid CursorWindow size issues
            cursor = database.rawQuery(
                    "SELECT p.post_id, p.user_id, u.username, " +
                            "p.food_name, p.review, p.rating, p.restaurant_name, " +
                            "p.location, p.latitude, p.longitude, p.created_at, " +
                            "(SELECT COUNT(*) FROM " + TABLE_COMMENTS + " c WHERE c.post_id = p.post_id) AS comment_count, " +
                            "(SELECT COUNT(*) FROM " + TABLE_LIKES + " l WHERE l.post_id = p.post_id) AS like_count, " +
                            "CASE WHEN EXISTS (SELECT 1 FROM " + TABLE_LIKES + " l WHERE l.post_id = p.post_id AND l.user_id = ?) THEN 1 ELSE 0 END AS is_liked, " +
                            "CASE WHEN EXISTS (SELECT 1 FROM " + TABLE_BOOKMARKS + " b WHERE b.post_id = p.post_id AND b.user_id = ?) THEN 1 ELSE 0 END AS is_bookmarked " +
                            "FROM " + TABLE_POSTS + " p " +
                            "JOIN " + TABLE_USERS + " u ON u.user_id = p.user_id " +
                            "WHERE p.post_id = ? LIMIT 1",
                    new String[]{String.valueOf(viewerUserId), String.valueOf(viewerUserId), String.valueOf(postId)});
            if (cursor.moveToFirst()) {
                post = mapPostWithoutBlobs(cursor);
                // Fetch BLOBs separately
                post.setPostImage(getPostImageBlob(post.getPostId()));
                post.setProfilePicture(getUserProfilePictureBlob(post.getUserId()));
            }
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return post;
    }

    public boolean toggleLike(int postId, int userId) {
        boolean isLiked = isPostLiked(postId, userId);
        if (isLiked) {
            SQLiteStatement deleteStmt = database.compileStatement(
                    "DELETE FROM " + TABLE_LIKES + " WHERE post_id = ? AND user_id = ?");
            deleteStmt.bindLong(1, postId);
            deleteStmt.bindLong(2, userId);
            deleteStmt.executeUpdateDelete();
            deleteStmt.close();
        } else {
            SQLiteStatement insertStmt = database.compileStatement(
                    "INSERT OR IGNORE INTO " + TABLE_LIKES + " (post_id, user_id, created_at) VALUES (?, ?, ?)");
            insertStmt.bindLong(1, postId);
            insertStmt.bindLong(2, userId);
            insertStmt.bindLong(3, System.currentTimeMillis());
            insertStmt.executeInsert();
            insertStmt.close();
        }
        updateLikeCount(postId);
        return !isLiked;
    }

    public boolean toggleBookmark(int postId, int userId) {
        boolean isBookmarked = isPostBookmarked(postId, userId);
        if (isBookmarked) {
            SQLiteStatement deleteStmt = database.compileStatement(
                    "DELETE FROM " + TABLE_BOOKMARKS + " WHERE post_id = ? AND user_id = ?");
            deleteStmt.bindLong(1, postId);
            deleteStmt.bindLong(2, userId);
            deleteStmt.executeUpdateDelete();
            deleteStmt.close();
        } else {
            SQLiteStatement insertStmt = database.compileStatement(
                    "INSERT OR IGNORE INTO " + TABLE_BOOKMARKS + " (post_id, user_id, created_at) VALUES (?, ?, ?)");
            insertStmt.bindLong(1, postId);
            insertStmt.bindLong(2, userId);
            insertStmt.bindLong(3, System.currentTimeMillis());
            insertStmt.executeInsert();
            insertStmt.close();
        }
        return !isBookmarked;
    }

    private boolean isPostLiked(int postId, int userId) {
        Cursor cursor = null;
        try {
            cursor = database.rawQuery(
                    "SELECT 1 FROM " + TABLE_LIKES + " WHERE post_id = ? AND user_id = ? LIMIT 1",
                    new String[]{String.valueOf(postId), String.valueOf(userId)});
            return cursor.moveToFirst();
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }

    private boolean isPostBookmarked(int postId, int userId) {
        Cursor cursor = null;
        try {
            cursor = database.rawQuery(
                    "SELECT 1 FROM " + TABLE_BOOKMARKS + " WHERE post_id = ? AND user_id = ? LIMIT 1",
                    new String[]{String.valueOf(postId), String.valueOf(userId)});
            return cursor.moveToFirst();
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }

    private void updateLikeCount(int postId) {
        SQLiteStatement countStmt = database.compileStatement(
                "SELECT COUNT(*) FROM " + TABLE_LIKES + " WHERE post_id = ?");
        countStmt.bindLong(1, postId);
        long likeCount = countStmt.simpleQueryForLong();
        countStmt.close();

        SQLiteStatement updateStmt = database.compileStatement(
                "UPDATE " + TABLE_POSTS + " SET like_count = ? WHERE post_id = ?");
        updateStmt.bindLong(1, likeCount);
        updateStmt.bindLong(2, postId);
        updateStmt.executeUpdateDelete();
        updateStmt.close();
    }

    public long addComment(int postId, int userId, String commentText) {
        SQLiteStatement statement = database.compileStatement(
                "INSERT INTO " + TABLE_COMMENTS + " (post_id, user_id, comment_text, created_at) VALUES (?, ?, ?, ?)");
        statement.bindLong(1, postId);
        statement.bindLong(2, userId);
        statement.bindString(3, commentText);
        statement.bindLong(4, System.currentTimeMillis());
        long id = statement.executeInsert();
        statement.close();
        return id;
    }

    public List<Comment> getCommentsForPost(int postId) {
        List<Comment> comments = new ArrayList<>();
        Cursor cursor = null;
        try {
            cursor = database.rawQuery(
                    "SELECT c.comment_id, c.post_id, c.user_id, u.username, u.profile_picture, c.comment_text, c.created_at " +
                            "FROM " + TABLE_COMMENTS + " c " +
                            "JOIN " + TABLE_USERS + " u ON u.user_id = c.user_id " +
                            "WHERE c.post_id = ? " +
                            "ORDER BY c.created_at ASC",
                    new String[]{String.valueOf(postId)});
            while (cursor.moveToNext()) {
                comments.add(mapComment(cursor));
            }
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return comments;
    }

    public long insertPost(int userId,
                           String foodName,
                           String review,
                           int rating,
                           Bitmap bitmap,
                           String restaurantName,
                           String location,
                           Double latitude,
                           Double longitude) {
        byte[] postBytes = bitmap != null ? bitmapToBytes(bitmap) : null;
        SQLiteStatement statement = database.compileStatement(
                "INSERT INTO " + TABLE_POSTS + " (" +
                        "user_id, food_name, review, rating, post_image, restaurant_name, " +
                        "location, latitude, longitude, created_at, like_count" +
                        ") VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)");
        statement.bindLong(1, userId);
        statement.bindString(2, foodName);
        if (review != null) {
            statement.bindString(3, review);
        } else {
            statement.bindNull(3);
        }
        statement.bindLong(4, rating);
        if (postBytes != null) {
            statement.bindBlob(5, postBytes);
        } else {
            statement.bindNull(5);
        }
        if (restaurantName != null) {
            statement.bindString(6, restaurantName);
        } else {
            statement.bindNull(6);
        }
        if (location != null) {
            statement.bindString(7, location);
        } else {
            statement.bindNull(7);
        }
        if (latitude != null) {
            statement.bindDouble(8, latitude);
        } else {
            statement.bindNull(8);
        }
        if (longitude != null) {
            statement.bindDouble(9, longitude);
        } else {
            statement.bindNull(9);
        }
        statement.bindLong(10, System.currentTimeMillis());
        statement.bindLong(11, 0);

        long id = statement.executeInsert();
        statement.close();
        return id;
    }

    public static byte[] bitmapToBytes(Bitmap bitmap) {
        if (bitmap == null) {
            return null;
        }
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 85, stream);
        return stream.toByteArray();
    }

    /**
     * Delete a post from the database.
     * Due to foreign key constraints with ON DELETE CASCADE, this will also automatically
     * delete all related comments, likes, and bookmarks.
     * 
     * @param postId The ID of the post to delete
     * @param userId The ID of the user attempting to delete (for verification)
     * @return true if the post was deleted, false if the post doesn't exist or user doesn't own it
     */
    public boolean deletePost(int postId, int userId) {
        // First verify the post exists and belongs to the user
        Cursor cursor = null;
        try {
            cursor = database.rawQuery(
                    "SELECT user_id FROM " + TABLE_POSTS + " WHERE post_id = ? LIMIT 1",
                    new String[]{String.valueOf(postId)});
            if (!cursor.moveToFirst()) {
                // Post doesn't exist
                return false;
            }
            int postUserId = cursor.getInt(cursor.getColumnIndexOrThrow("user_id"));
            if (postUserId != userId) {
                // User doesn't own this post
                return false;
            }
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }

        // Delete the post (CASCADE will handle related data)
        SQLiteStatement deleteStmt = database.compileStatement(
                "DELETE FROM " + TABLE_POSTS + " WHERE post_id = ?");
        deleteStmt.bindLong(1, postId);
        int rowsDeleted = deleteStmt.executeUpdateDelete();
        deleteStmt.close();

        return rowsDeleted > 0;
    }

    /**
     * Delete a post without user verification (use with caution, typically for admin operations)
     * 
     * @param postId The ID of the post to delete
     * @return true if the post was deleted, false if the post doesn't exist
     */
    public boolean deletePost(int postId) {
        SQLiteStatement deleteStmt = database.compileStatement(
                "DELETE FROM " + TABLE_POSTS + " WHERE post_id = ?");
        deleteStmt.bindLong(1, postId);
        int rowsDeleted = deleteStmt.executeUpdateDelete();
        deleteStmt.close();
        return rowsDeleted > 0;
    }

    private User mapUser(Cursor cursor) {
        int userId = cursor.getInt(cursor.getColumnIndexOrThrow("user_id"));
        String username = cursor.getString(cursor.getColumnIndexOrThrow("username"));
        String email = cursor.getString(cursor.getColumnIndexOrThrow("email"));
        String displayName = cursor.getString(cursor.getColumnIndexOrThrow("display_name"));
        String bio = cursor.getString(cursor.getColumnIndexOrThrow("bio"));
        byte[] profilePicture = cursor.getBlob(cursor.getColumnIndexOrThrow("profile_picture"));
        User user = new User(userId, username, email, displayName, bio, profilePicture);
        return user;
    }

    private Post mapPost(Cursor cursor) {
        int postId = cursor.getInt(cursor.getColumnIndexOrThrow("post_id"));
        int userId = cursor.getInt(cursor.getColumnIndexOrThrow("user_id"));
        String username = cursor.getString(cursor.getColumnIndexOrThrow("username"));
        byte[] profilePicture = cursor.getBlob(cursor.getColumnIndexOrThrow("profile_picture"));
        String foodName = cursor.getString(cursor.getColumnIndexOrThrow("food_name"));
        String review = cursor.getString(cursor.getColumnIndexOrThrow("review"));
        int rating = cursor.getInt(cursor.getColumnIndexOrThrow("rating"));
        byte[] postImage = cursor.getBlob(cursor.getColumnIndexOrThrow("post_image"));
        String restaurantName = cursor.getString(cursor.getColumnIndexOrThrow("restaurant_name"));
        String location = cursor.getString(cursor.getColumnIndexOrThrow("location"));
        int latitudeIndex = cursor.getColumnIndex("latitude");
        int longitudeIndex = cursor.getColumnIndex("longitude");
        double latitude = (latitudeIndex != -1 && !cursor.isNull(latitudeIndex)) ? cursor.getDouble(latitudeIndex) : 0;
        double longitude = (longitudeIndex != -1 && !cursor.isNull(longitudeIndex)) ? cursor.getDouble(longitudeIndex) : 0;
        long createdAt = cursor.getLong(cursor.getColumnIndexOrThrow("created_at"));
        Post post = new Post(postId, userId, username, profilePicture, foodName, review,
                rating, postImage, restaurantName, location, latitude, longitude, createdAt);
        int commentCount = cursor.getInt(cursor.getColumnIndexOrThrow("comment_count"));
        int likeCount = cursor.getInt(cursor.getColumnIndexOrThrow("like_count"));
        int isLiked = cursor.getInt(cursor.getColumnIndexOrThrow("is_liked"));
        post.setCommentCount(commentCount);
        post.setLikeCount(likeCount);
        post.setLiked(isLiked == 1);
        int isBookmarkedIndex = cursor.getColumnIndex("is_bookmarked");
        if (isBookmarkedIndex != -1) {
            post.setBookmarked(cursor.getInt(isBookmarkedIndex) == 1);
        }
        return post;
    }

    private Post mapPostWithoutBlobs(Cursor cursor) {
        int postId = cursor.getInt(cursor.getColumnIndexOrThrow("post_id"));
        int userId = cursor.getInt(cursor.getColumnIndexOrThrow("user_id"));
        String username = cursor.getString(cursor.getColumnIndexOrThrow("username"));
        String foodName = cursor.getString(cursor.getColumnIndexOrThrow("food_name"));
        String review = cursor.getString(cursor.getColumnIndexOrThrow("review"));
        int rating = cursor.getInt(cursor.getColumnIndexOrThrow("rating"));
        String restaurantName = cursor.getString(cursor.getColumnIndexOrThrow("restaurant_name"));
        String location = cursor.getString(cursor.getColumnIndexOrThrow("location"));
        int latitudeIndex = cursor.getColumnIndex("latitude");
        int longitudeIndex = cursor.getColumnIndex("longitude");
        double latitude = (latitudeIndex != -1 && !cursor.isNull(latitudeIndex)) ? cursor.getDouble(latitudeIndex) : 0;
        double longitude = (longitudeIndex != -1 && !cursor.isNull(longitudeIndex)) ? cursor.getDouble(longitudeIndex) : 0;
        long createdAt = cursor.getLong(cursor.getColumnIndexOrThrow("created_at"));
        // Create post with null BLOBs - will be set separately
        Post post = new Post(postId, userId, username, null, foodName, review,
                rating, null, restaurantName, location, latitude, longitude, createdAt);
        int commentCount = cursor.getInt(cursor.getColumnIndexOrThrow("comment_count"));
        int likeCount = cursor.getInt(cursor.getColumnIndexOrThrow("like_count"));
        int isLiked = cursor.getInt(cursor.getColumnIndexOrThrow("is_liked"));
        post.setCommentCount(commentCount);
        post.setLikeCount(likeCount);
        post.setLiked(isLiked == 1);
        int isBookmarkedIndex = cursor.getColumnIndex("is_bookmarked");
        if (isBookmarkedIndex != -1) {
            post.setBookmarked(cursor.getInt(isBookmarkedIndex) == 1);
        }
        return post;
    }

    private byte[] getPostImageBlob(int postId) {
        Cursor cursor = null;
        try {
            cursor = database.rawQuery(
                    "SELECT post_image FROM " + TABLE_POSTS + " WHERE post_id = ? LIMIT 1",
                    new String[]{String.valueOf(postId)});
            if (cursor.moveToFirst()) {
                int columnIndex = cursor.getColumnIndex("post_image");
                if (columnIndex != -1 && !cursor.isNull(columnIndex)) {
                    return cursor.getBlob(columnIndex);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return null;
    }

    private byte[] getUserProfilePictureBlob(int userId) {
        Cursor cursor = null;
        try {
            cursor = database.rawQuery(
                    "SELECT profile_picture FROM " + TABLE_USERS + " WHERE user_id = ? LIMIT 1",
                    new String[]{String.valueOf(userId)});
            if (cursor.moveToFirst()) {
                int columnIndex = cursor.getColumnIndex("profile_picture");
                if (columnIndex != -1 && !cursor.isNull(columnIndex)) {
                    return cursor.getBlob(columnIndex);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return null;
    }

    private Comment mapComment(Cursor cursor) {
        int commentId = cursor.getInt(cursor.getColumnIndexOrThrow("comment_id"));
        int postId = cursor.getInt(cursor.getColumnIndexOrThrow("post_id"));
        int userId = cursor.getInt(cursor.getColumnIndexOrThrow("user_id"));
        String username = cursor.getString(cursor.getColumnIndexOrThrow("username"));
        byte[] profilePicture = cursor.getBlob(cursor.getColumnIndexOrThrow("profile_picture"));
        String commentText = cursor.getString(cursor.getColumnIndexOrThrow("comment_text"));
        long createdAt = cursor.getLong(cursor.getColumnIndexOrThrow("created_at"));
        return new Comment(commentId, postId, userId, username, profilePicture, commentText, createdAt);
    }

    // Saved Places methods
    public long savePlace(int userId, RestaurantInfo restaurant) {
        SQLiteStatement statement = database.compileStatement(
                "INSERT INTO " + TABLE_SAVED_PLACES + " (" +
                        "user_id, place_name, place_type, rating, review_count, distance, " +
                        "status, service_options, phone, website, hours, latitude, longitude, image_url, created_at" +
                        ") VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)"
        );
        statement.bindLong(1, userId);
        statement.bindString(2, restaurant.getName());
        statement.bindString(3, restaurant.getType());
        statement.bindDouble(4, restaurant.getRating());
        statement.bindLong(5, restaurant.getReviewCount());
        statement.bindString(6, restaurant.getDistance());
        statement.bindString(7, restaurant.getStatus());
        statement.bindString(8, restaurant.getServiceOptions());
        if (restaurant.getPhone() != null) {
            statement.bindString(9, restaurant.getPhone());
        } else {
            statement.bindNull(9);
        }
        if (restaurant.getWebsite() != null) {
            statement.bindString(10, restaurant.getWebsite());
        } else {
            statement.bindNull(10);
        }
        if (restaurant.getHours() != null) {
            statement.bindString(11, restaurant.getHours());
        } else {
            statement.bindNull(11);
        }
        statement.bindDouble(12, restaurant.getLatitude());
        statement.bindDouble(13, restaurant.getLongitude());
        if (restaurant.getImageUrl1() != null) {
            statement.bindString(14, restaurant.getImageUrl1());
        } else {
            statement.bindNull(14);
        }
        statement.bindLong(15, System.currentTimeMillis());

        long id = statement.executeInsert();
        statement.close();
        return id;
    }

    public boolean isPlaceSaved(int userId, String placeName, double latitude, double longitude) {
        Cursor cursor = database.rawQuery(
                "SELECT COUNT(*) FROM " + TABLE_SAVED_PLACES +
                        " WHERE user_id = ? AND place_name = ? AND latitude = ? AND longitude = ?",
                new String[]{String.valueOf(userId), placeName, String.valueOf(latitude), String.valueOf(longitude)}
        );
        boolean exists = false;
        if (cursor.moveToFirst()) {
            exists = cursor.getInt(0) > 0;
        }
        cursor.close();
        return exists;
    }

    public List<SavedPlace> getSavedPlaces(int userId) {
        List<SavedPlace> savedPlaces = new ArrayList<>();
        Cursor cursor = database.rawQuery(
                "SELECT * FROM " + TABLE_SAVED_PLACES +
                        " WHERE user_id = ? ORDER BY created_at DESC",
                new String[]{String.valueOf(userId)}
        );

        while (cursor.moveToNext()) {
            savedPlaces.add(mapSavedPlace(cursor));
        }
        cursor.close();
        return savedPlaces;
    }

    public int getSavedPlacesCount(int userId) {
        Cursor cursor = database.rawQuery(
                "SELECT COUNT(*) FROM " + TABLE_SAVED_PLACES + " WHERE user_id = ?",
                new String[]{String.valueOf(userId)}
        );
        int count = 0;
        if (cursor.moveToFirst()) {
            count = cursor.getInt(0);
        }
        cursor.close();
        return count;
    }

    public void deleteSavedPlace(int userId, String placeName, double latitude, double longitude) {
        database.delete(TABLE_SAVED_PLACES,
                "user_id = ? AND place_name = ? AND latitude = ? AND longitude = ?",
                new String[]{String.valueOf(userId), placeName, String.valueOf(latitude), String.valueOf(longitude)});
    }

    private SavedPlace mapSavedPlace(Cursor cursor) {
        int savedPlaceId = cursor.getInt(cursor.getColumnIndexOrThrow("saved_place_id"));
        int userId = cursor.getInt(cursor.getColumnIndexOrThrow("user_id"));
        String placeName = cursor.getString(cursor.getColumnIndexOrThrow("place_name"));
        String placeType = cursor.getString(cursor.getColumnIndexOrThrow("place_type"));
        float rating = cursor.getFloat(cursor.getColumnIndexOrThrow("rating"));
        int reviewCount = cursor.getInt(cursor.getColumnIndexOrThrow("review_count"));
        String distance = cursor.getString(cursor.getColumnIndexOrThrow("distance"));
        String status = cursor.getString(cursor.getColumnIndexOrThrow("status"));
        String serviceOptions = cursor.getString(cursor.getColumnIndexOrThrow("service_options"));
        String phone = cursor.getString(cursor.getColumnIndex("phone"));
        String website = cursor.getString(cursor.getColumnIndex("website"));
        String hours = cursor.getString(cursor.getColumnIndex("hours"));
        double latitude = cursor.getDouble(cursor.getColumnIndexOrThrow("latitude"));
        double longitude = cursor.getDouble(cursor.getColumnIndexOrThrow("longitude"));
        String imageUrl = cursor.getString(cursor.getColumnIndex("image_url"));
        long createdAt = cursor.getLong(cursor.getColumnIndexOrThrow("created_at"));

        return new SavedPlace(savedPlaceId, userId, placeName, placeType, rating, reviewCount,
                distance, status, serviceOptions, phone, website, hours, latitude, longitude, imageUrl, createdAt);
    }
}

