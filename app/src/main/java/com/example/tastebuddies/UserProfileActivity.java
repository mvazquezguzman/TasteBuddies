package com.example.tastebuddies;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.List;

public class UserProfileActivity extends AppCompatActivity {
    private ImageView imageViewProfile;
    private TextView textViewUsername, textViewBio, textViewFollowers, textViewFollowing;
    private Button buttonFollow;
    private ListView listViewPosts;
    private int currentUserId;
    private int profileUserId;
    private PostAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);

        currentUserId = getSharedPreferences("TasteBuddiesPrefs", MODE_PRIVATE)
                .getInt("userId", -1);

        profileUserId = getIntent().getIntExtra("userId", -1);
        if (profileUserId == -1) {
            // Toast.makeText(this, "User not found", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        initializeViews();
        loadUserProfile();
    }

    private void initializeViews() {
        imageViewProfile = findViewById(R.id.imageViewProfile);
        textViewUsername = findViewById(R.id.textViewUsername);
        textViewBio = findViewById(R.id.textViewBio);
        textViewFollowers = findViewById(R.id.textViewFollowers);
        textViewFollowing = findViewById(R.id.textViewFollowing);
        buttonFollow = findViewById(R.id.buttonFollow);
        listViewPosts = findViewById(R.id.listViewPosts);

        buttonFollow.setOnClickListener(v -> toggleFollow());

        listViewPosts.setOnItemClickListener((parent, view, position, id) -> {
            Post post = (Post) adapter.getItem(position);
            Intent intent = new Intent(this, PostDetailActivity.class);
            intent.putExtra("postId", post.getPostId());
            startActivity(intent);
        });
    }

    private void loadUserProfile() {
        imageViewProfile.setImageResource(android.R.drawable.sym_def_app_icon);
        textViewUsername.setText("User");
        textViewBio.setText("No bio");
        textViewFollowers.setText("Followers: 0");
        textViewFollowing.setText("Following: 0");
        buttonFollow.setVisibility(View.GONE);
        
        List<Post> posts = new ArrayList<>();
        adapter = new PostAdapter(this, posts, new PostAdapter.OnPostInteractionListener() {
            @Override
            public void onPostClick(int postId) {
                Intent intent = new Intent(UserProfileActivity.this, PostDetailActivity.class);
                intent.putExtra("postId", postId);
                startActivity(intent);
            }

            @Override
            public void onLikeClick(int postId) {
                // TODO: Implement like functionality
            }

            @Override
            public void onBookmarkClick(int postId) {
                // TODO: Implement bookmark functionality
            }

            @Override
            public void onCommentClick(int postId) {
                Intent intent = new Intent(UserProfileActivity.this, PostDetailActivity.class);
                intent.putExtra("postId", postId);
                startActivity(intent);
            }

            @Override
            public void onProfileClick(int userId) {
                if (userId != profileUserId) {
                    Intent intent = new Intent(UserProfileActivity.this, UserProfileActivity.class);
                    intent.putExtra("userId", userId);
                    startActivity(intent);
                }
            }
        });

        listViewPosts.setAdapter(adapter);
    }

    private void toggleFollow() {
        //
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadUserProfile();
    }
}
