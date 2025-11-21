package com.example.tastebuddies;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import java.util.List;

public class RestaurantCardAdapter extends BaseAdapter {
    private Context context;
    private List<RestaurantInfo> restaurants;
    private LayoutInflater inflater;
    private TasteBuddiesDatabaseManager dbManager;
    private int currentUserId;

    public RestaurantCardAdapter(Context context, List<RestaurantInfo> restaurants) {
        this.context = context;
        this.restaurants = restaurants;
        this.inflater = LayoutInflater.from(context);
        this.dbManager = TasteBuddiesDatabaseManager.getInstance(context);
        
        // Get current user ID from SharedPreferences
        SharedPreferences prefs = context.getSharedPreferences("TasteBuddiesPrefs", Context.MODE_PRIVATE);
        this.currentUserId = prefs.getInt("userId", -1);
    }

    @Override
    public int getCount() {
        return restaurants.size();
    }

    @Override
    public Object getItem(int position) {
        return restaurants.get(position);
    }

    @Override
    public long getItemId(int position) {
        return restaurants.get(position).getId();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.item_restaurant_card, parent, false);
            holder = new ViewHolder();
            holder.textViewRestaurantName = convertView.findViewById(R.id.textViewRestaurantName);
            holder.ratingBar = convertView.findViewById(R.id.ratingBar);
            holder.textViewRating = convertView.findViewById(R.id.textViewRating);
            holder.textViewReviewCount = convertView.findViewById(R.id.textViewReviewCount);
            holder.textViewRestaurantType = convertView.findViewById(R.id.textViewRestaurantType);
            holder.textViewDistance = convertView.findViewById(R.id.textViewDistance);
            holder.textViewStatus = convertView.findViewById(R.id.textViewStatus);
            holder.textViewServiceOptions = convertView.findViewById(R.id.textViewServiceOptions);
            holder.textViewHours = convertView.findViewById(R.id.textViewHours);
            holder.textViewPhone = convertView.findViewById(R.id.textViewPhone);
            holder.imageView1 = convertView.findViewById(R.id.imageView1);
            holder.buttonSave = convertView.findViewById(R.id.buttonSave);
            holder.buttonDirections = convertView.findViewById(R.id.buttonDirections);
            holder.buttonCall = convertView.findViewById(R.id.buttonCall);
            holder.buttonWebsite = convertView.findViewById(R.id.buttonWebsite);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        RestaurantInfo restaurant = restaurants.get(position);

        // Set restaurant name
        holder.textViewRestaurantName.setText(restaurant.getName());

        // Set rating
        holder.ratingBar.setRating(restaurant.getRating());
        holder.textViewRating.setText(String.format("%.1f", restaurant.getRating()));
        holder.textViewReviewCount.setText("(" + restaurant.getReviewCount() + " reviews)");

        // Set restaurant type
        holder.textViewRestaurantType.setText(restaurant.getType());

        // Set distance
        holder.textViewDistance.setText(restaurant.getDistance());

        // Set status
        holder.textViewStatus.setText(restaurant.getStatus());

        // Set service options
        holder.textViewServiceOptions.setText(restaurant.getServiceOptions());

        // Set hours
        if (restaurant.getHours() != null && !restaurant.getHours().isEmpty() && 
            !restaurant.getHours().equals("Hours not available")) {
            holder.textViewHours.setText("Hours: " + restaurant.getHours());
            holder.textViewHours.setVisibility(View.VISIBLE);
        } else {
            holder.textViewHours.setVisibility(View.GONE);
        }

        // Set phone number
        if (restaurant.getPhone() != null && !restaurant.getPhone().isEmpty()) {
            holder.textViewPhone.setText("Phone: " + restaurant.getPhone());
            holder.textViewPhone.setVisibility(View.VISIBLE);
        } else {
            holder.textViewPhone.setVisibility(View.GONE);
        }

        // Load restaurant image from URL using Glide (only first image)
        if (restaurant.getImageUrl1() != null && !restaurant.getImageUrl1().isEmpty()) {
            Glide.with(context)
                .load(restaurant.getImageUrl1())
                .placeholder(R.drawable.food1)
                .error(R.drawable.food1)
                .centerCrop()
                .into(holder.imageView1);
        } else {
            holder.imageView1.setImageResource(R.drawable.food1);
        }

        // Set up save button
        boolean isSaved = dbManager.isPlaceSaved(currentUserId, restaurant.getName(), 
                restaurant.getLatitude(), restaurant.getLongitude());
        if (isSaved) {
            holder.buttonSave.setImageResource(R.drawable.ic_bookmark);
            holder.buttonSave.setAlpha(1.0f);
        } else {
            holder.buttonSave.setImageResource(R.drawable.ic_bookmark);
            holder.buttonSave.setAlpha(0.5f);
        }
        
        holder.buttonSave.setOnClickListener(v -> {
            boolean currentlySaved = dbManager.isPlaceSaved(currentUserId, restaurant.getName(),
                    restaurant.getLatitude(), restaurant.getLongitude());
            if (currentlySaved) {
                // Unsave the place
                dbManager.deleteSavedPlace(currentUserId, restaurant.getName(),
                        restaurant.getLatitude(), restaurant.getLongitude());
                holder.buttonSave.setAlpha(0.5f);
                Toast.makeText(context, "Place removed from Want to Try", Toast.LENGTH_SHORT).show();
            } else {
                // Save the place
                dbManager.savePlace(currentUserId, restaurant);
                holder.buttonSave.setAlpha(1.0f);
                Toast.makeText(context, "Place saved to Want to Try", Toast.LENGTH_SHORT).show();
            }
        });

        // Set up action buttons
        holder.buttonDirections.setOnClickListener(v -> {
            // Open directions in Google Maps
            String uri = "http://maps.google.com/maps?daddr=" + 
                        restaurant.getLatitude() + "," + restaurant.getLongitude();
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
            context.startActivity(intent);
        });

        holder.buttonCall.setOnClickListener(v -> {
            if (restaurant.getPhone() != null && !restaurant.getPhone().isEmpty()) {
                Intent intent = new Intent(Intent.ACTION_DIAL);
                intent.setData(Uri.parse("tel:" + restaurant.getPhone()));
                context.startActivity(intent);
            }
        });

        holder.buttonWebsite.setOnClickListener(v -> {
            if (restaurant.getWebsite() != null && !restaurant.getWebsite().isEmpty()) {
                String websiteUrl = restaurant.getWebsite();
                // Ensure URL has http:// or https://
                if (!websiteUrl.startsWith("http://") && !websiteUrl.startsWith("https://")) {
                    websiteUrl = "https://" + websiteUrl;
                }
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(websiteUrl));
                context.startActivity(intent);
            }
        });

        return convertView;
    }

    static class ViewHolder {
        TextView textViewRestaurantName;
        RatingBar ratingBar;
        TextView textViewRating;
        TextView textViewReviewCount;
        TextView textViewRestaurantType;
        TextView textViewDistance;
        TextView textViewStatus;
        TextView textViewServiceOptions;
        TextView textViewHours;
        TextView textViewPhone;
        ImageView imageView1;
        ImageButton buttonSave;
        Button buttonDirections;
        Button buttonCall;
        Button buttonWebsite;
    }
}

