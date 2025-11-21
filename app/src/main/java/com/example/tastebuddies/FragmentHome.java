package com.example.tastebuddies;

import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Toast;

import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;

import java.util.ArrayList;
import java.util.List;

public class FragmentHome extends Fragment {
    private ListView listViewFeed;
    private PostAdapter postAdapter;
    private int currentUserId;
    private ImageButton buttonRefresh;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        MainActivity mainActivity = (MainActivity) getActivity();
        if (mainActivity != null) {
            currentUserId = mainActivity.getCurrentUserId();
        }

        // Apply Window Insets to Handle Status Bar and Camera Cutout
        ViewCompat.setOnApplyWindowInsetsListener(view, (v, insets) -> {
            int topInset = insets.getInsets(WindowInsetsCompat.Type.systemBars()).top;
            Resources resources = getResources();
            int extraPadding = (int) (16 * resources.getDisplayMetrics().density);
            int top = Math.max(topInset + extraPadding, v.getPaddingTop());
            v.setPadding(v.getPaddingLeft(), top, v.getPaddingRight(), v.getPaddingBottom());
            return insets;
        });

        listViewFeed = view.findViewById(R.id.listViewFeed);
        buttonRefresh = view.findViewById(R.id.buttonRefresh);
        
        // Set up refresh button
        if (buttonRefresh != null) {
            buttonRefresh.setOnClickListener(v -> {
                refreshFeed();
            });
        }

        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        // Load feed after view is fully created
        loadFeed();
    }

    @Override
    public void onResume() {
        super.onResume();
        // Refresh feed when fragment resumes (only if view is already created)
        // onViewCreated will handle initial load, onResume handles refresh when coming back
        if (listViewFeed != null && getView() != null) {
            loadFeed();
        }
    }

    private void loadFeed() {
        if (getActivity() == null || listViewFeed == null) {
            return;
        }
        
        try {
            // Ensure we have the current user ID
            MainActivity mainActivity = (MainActivity) getActivity();
            if (mainActivity != null && currentUserId == -1) {
                currentUserId = mainActivity.getCurrentUserId();
            }
            
            // Load posts from database (ordered by created_at DESC, so newest first)
            TasteBuddiesDatabaseManager dbManager = TasteBuddiesDatabaseManager.getInstance(getActivity());
            List<Post> posts = dbManager.getRecentPosts(50, currentUserId); // Load up to 50 recent posts
            if (posts == null) {
                posts = new ArrayList<>();
            }
            
            // Create listener for post interactions
            PostAdapter.OnPostInteractionListener listener = new PostAdapter.OnPostInteractionListener() {
                @Override
                public void onPostClick(int postId) {
                    Intent intent = new Intent(getActivity(), PostDetailActivity.class);
                    intent.putExtra("postId", postId);
                    startActivity(intent);
                }

                @Override
                public void onLikeClick(int postId) {
                    TasteBuddiesDatabaseManager dbManager = TasteBuddiesDatabaseManager.getInstance(getActivity());
                    boolean isLiked = dbManager.toggleLike(postId, currentUserId);
                    // Refresh the feed to update like status
                    loadFeed();
                }

                @Override
                public void onBookmarkClick(int postId) {
                    TasteBuddiesDatabaseManager dbManager = TasteBuddiesDatabaseManager.getInstance(getActivity());
                    boolean isBookmarked = dbManager.toggleBookmark(postId, currentUserId);
                    // Refresh the feed to update bookmark status
                    loadFeed();
                }

                @Override
                public void onCommentClick(int postId) {
                    Intent intent = new Intent(getActivity(), PostDetailActivity.class);
                    intent.putExtra("postId", postId);
                    startActivity(intent);
                }

                @Override
                public void onProfileClick(int userId) {
                    Intent intent = new Intent(getActivity(), UserProfileActivity.class);
                    intent.putExtra("userId", userId);
                    startActivity(intent);
                }
            };
            
            // Update existing adapter or create new one
            if (postAdapter != null) {
                postAdapter.updatePosts(posts);
            } else {
                postAdapter = new PostAdapter(getActivity(), posts, listener);
                listViewFeed.setAdapter(postAdapter);
            }
            
            // Scroll to top to show newest posts
            if (listViewFeed != null && posts.size() > 0) {
                listViewFeed.post(() -> {
                    listViewFeed.setSelection(0);
                });
            }
            
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(getActivity(), "Error loading feed: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            // Initialize with empty list on error
            if (postAdapter == null) {
                postAdapter = new PostAdapter(getActivity(), new ArrayList<>(), new PostAdapter.OnPostInteractionListener() {
                    @Override public void onPostClick(int postId) {}
                    @Override public void onLikeClick(int postId) {}
                    @Override public void onBookmarkClick(int postId) {}
                    @Override public void onCommentClick(int postId) {}
                    @Override public void onProfileClick(int userId) {}
                });
                listViewFeed.setAdapter(postAdapter);
            } else {
                postAdapter.updatePosts(new ArrayList<>());
            }
        }
    }
    
    private void refreshFeed() {
        if (getActivity() == null) {
            return;
        }
        
        // Show a brief message
        Toast.makeText(getActivity(), "Refreshing feed...", Toast.LENGTH_SHORT).show();
        
        // Force reload by resetting adapter
        postAdapter = null;
        
        // Reload the feed
        loadFeed();
    }

    // Public method to refresh feed from outside the fragment
    public void refreshFeedPublic() {
        // Ensure view is created before loading feed
        if (getView() != null && listViewFeed != null) {
            loadFeed();
        } else {
            // If view isn't ready, it will load in onViewCreated or onResume
        }
    }
}
