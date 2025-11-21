package com.example.tastebuddies;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.List;

public class PostDetailActivity extends AppCompatActivity {
    private ImageView imageViewProfile, imageViewPost;
    private TextView textViewUsername, textViewFoodName, textViewRestaurant, 
                     textViewLocation, textViewReview, textViewLikes, textViewComments;
    private RatingBar ratingBar;
    private ImageView imageViewLike;
    private ListView listViewComments;
    private EditText editTextComment;
    private Button buttonPostComment;
    private int currentUserId;
    private int postId;
    private Post post;
    private CommentAdapter commentAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_detail);

        currentUserId = getSharedPreferences("TasteBuddiesPrefs", MODE_PRIVATE)
                .getInt("userId", -1);

        postId = getIntent().getIntExtra("postId", -1);
        if (postId == -1) {
            // Toast.makeText(this, "Post not found", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        initializeViews();
        loadPost();
        loadComments();
    }

    private void initializeViews() {
        imageViewProfile = findViewById(R.id.imageViewProfile);
        textViewUsername = findViewById(R.id.textViewUsername);
        imageViewPost = findViewById(R.id.imageViewPost);
        textViewFoodName = findViewById(R.id.textViewFoodName);
        textViewRestaurant = findViewById(R.id.textViewRestaurant);
        textViewLocation = findViewById(R.id.textViewLocation);
        ratingBar = findViewById(R.id.ratingBar);
        textViewReview = findViewById(R.id.textViewReview);
        textViewLikes = findViewById(R.id.textViewLikes);
        textViewComments = findViewById(R.id.textViewComments);
        imageViewLike = findViewById(R.id.imageViewLike);
        listViewComments = findViewById(R.id.listViewComments);
        editTextComment = findViewById(R.id.editTextComment);
        buttonPostComment = findViewById(R.id.buttonPostComment);

        imageViewLike.setOnClickListener(v -> toggleLike());
        buttonPostComment.setOnClickListener(v -> postComment());
        textViewUsername.setOnClickListener(v -> {
            if (post != null) {
                Intent intent = new Intent(this, UserProfileActivity.class);
                intent.putExtra("userId", post.getUserId());
                startActivity(intent);
            }
        });
    }

    private void loadPost() {
        finish();
    }

    private void toggleLike() {
    }

    private void postComment() {
    }

    private void loadComments() {
        List<Comment> comments = new ArrayList<>();
        commentAdapter = new CommentAdapter(this, comments);
        listViewComments.setAdapter(commentAdapter);
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadPost();
        loadComments();
    }
}
