package com.example.tastebuddies;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

public class PostAdapter extends BaseAdapter {
    private Context context;
    private List<Post> posts;
    private LayoutInflater inflater;
    private OnPostInteractionListener listener;

    public interface OnPostInteractionListener {
        void onPostClick(int postId);
        void onLikeClick(int postId);
        void onBookmarkClick(int postId);
        void onCommentClick(int postId);
        void onProfileClick(int userId);
    }

    public PostAdapter(Context context, List<Post> posts, OnPostInteractionListener listener) {
        this.context = context;
        this.posts = posts;
        this.inflater = LayoutInflater.from(context);
        this.listener = listener;
    }

    @Override
    public int getCount() {
        return posts.size();
    }

    @Override
    public Object getItem(int position) {
        return posts.get(position);
    }

    @Override
    public long getItemId(int position) {
        return posts.get(position).getPostId();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.item_post, parent, false);
            holder = new ViewHolder();
            holder.imageViewProfile = convertView.findViewById(R.id.imageViewProfile);
            holder.textViewFoodName = convertView.findViewById(R.id.textViewFoodName);
            holder.textViewRestaurant = convertView.findViewById(R.id.textViewRestaurant);
            holder.ratingBar = convertView.findViewById(R.id.ratingBar);
            holder.imageViewPost = convertView.findViewById(R.id.imageViewPost);
            holder.imageViewLike = convertView.findViewById(R.id.imageViewLike);
            holder.imageViewBookmark = convertView.findViewById(R.id.imageViewBookmark);
            holder.textViewCaption = convertView.findViewById(R.id.textViewCaption);
            holder.textViewViewComments = convertView.findViewById(R.id.textViewViewComments);
            holder.textViewTimestamp = convertView.findViewById(R.id.textViewTimestamp);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        Post post = posts.get(position);

        // Set profile picture
        if (post.getProfilePicture() != null && post.getProfilePicture().length > 0) {
            Bitmap profileBitmap = BitmapFactory.decodeByteArray(post.getProfilePicture(), 0, post.getProfilePicture().length);
            holder.imageViewProfile.setImageBitmap(profileBitmap);
        } else {
            holder.imageViewProfile.setImageResource(android.R.drawable.sym_def_app_icon);
        }

        // Set food name and restaurant
        holder.textViewFoodName.setText(post.getFoodName());
        holder.textViewRestaurant.setText(post.getRestaurantName());
        holder.ratingBar.setRating(post.getRating());

        // Set post image
        if (post.getPostImage() != null && post.getPostImage().length > 0) {
            Bitmap postBitmap = BitmapFactory.decodeByteArray(post.getPostImage(), 0, post.getPostImage().length);
            holder.imageViewPost.setImageBitmap(postBitmap);
            holder.imageViewPost.setVisibility(View.VISIBLE);
        } else {
            holder.imageViewPost.setVisibility(View.VISIBLE);
            holder.imageViewPost.setImageResource(android.R.drawable.ic_menu_gallery);
        }

        // Format caption with username
        String caption = post.getUsername() + " " + post.getReview();
        holder.textViewCaption.setText(caption);

        // Set "View all X comments" text
        int commentCount = post.getCommentCount();
        if (commentCount > 0) {
            holder.textViewViewComments.setText("View all " + commentCount + " comment" + (commentCount == 1 ? "" : "s"));
            holder.textViewViewComments.setVisibility(View.VISIBLE);
        } else {
            holder.textViewViewComments.setVisibility(View.GONE);
        }

        // Format and set timestamp
        holder.textViewTimestamp.setText(formatTimestamp(post.getCreatedAt()));

        // Update like button state
        if (post.isLiked()) {
            holder.imageViewLike.setImageResource(android.R.drawable.star_big_on);
        } else {
            holder.imageViewLike.setImageResource(android.R.drawable.star_big_off);
        }

        // Set click listeners
        holder.imageViewProfile.setOnClickListener(v -> {
            if (listener != null) listener.onProfileClick(post.getUserId());
        });

        holder.textViewFoodName.setOnClickListener(v -> {
            if (listener != null) listener.onPostClick(post.getPostId());
        });

        holder.imageViewLike.setOnClickListener(v -> {
            if (listener != null) listener.onLikeClick(post.getPostId());
        });

        holder.imageViewBookmark.setOnClickListener(v -> {
            if (listener != null) listener.onBookmarkClick(post.getPostId());
        });

        holder.textViewViewComments.setOnClickListener(v -> {
            if (listener != null) listener.onCommentClick(post.getPostId());
        });

        holder.imageViewPost.setOnClickListener(v -> {
            if (listener != null) listener.onPostClick(post.getPostId());
        });

        convertView.setOnClickListener(v -> {
            if (listener != null) listener.onPostClick(post.getPostId());
        });

        return convertView;
    }

    static class ViewHolder {
        ImageView imageViewProfile;
        TextView textViewFoodName;
        TextView textViewRestaurant;
        RatingBar ratingBar;
        ImageView imageViewPost;
        ImageView imageViewLike;
        ImageView imageViewBookmark;
        TextView textViewCaption;
        TextView textViewViewComments;
        TextView textViewTimestamp;
    }

    private String formatTimestamp(long timestamp) {
        if (timestamp == 0) {
            return "Just now";
        }

        long now = System.currentTimeMillis();
        long diff = now - timestamp;

        if (diff < TimeUnit.MINUTES.toMillis(1)) {
            return "Just now";
        } else if (diff < TimeUnit.HOURS.toMillis(1)) {
            long minutes = TimeUnit.MILLISECONDS.toMinutes(diff);
            return minutes + " minute" + (minutes == 1 ? "" : "s") + " ago";
        } else if (diff < TimeUnit.DAYS.toMillis(1)) {
            long hours = TimeUnit.MILLISECONDS.toHours(diff);
            return hours + " hour" + (hours == 1 ? "" : "s") + " ago";
        } else if (diff < TimeUnit.DAYS.toMillis(7)) {
            long days = TimeUnit.MILLISECONDS.toDays(diff);
            return days + " day" + (days == 1 ? "" : "s") + " ago";
        } else {
            SimpleDateFormat sdf = new SimpleDateFormat("MMM d", Locale.getDefault());
            return sdf.format(new Date(timestamp));
        }
    }

    public void updatePosts(List<Post> newPosts) {
        this.posts = newPosts;
        notifyDataSetChanged();
    }
}
