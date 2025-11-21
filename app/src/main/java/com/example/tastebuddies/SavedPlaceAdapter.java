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

public class SavedPlaceAdapter extends BaseAdapter {
    private Context context;
    private List<SavedPlace> savedPlaces;
    private LayoutInflater inflater;
    private TasteBuddiesDatabaseManager dbManager;
    private int currentUserId;
    private OnPlaceRemovedListener onPlaceRemovedListener;

    public interface OnPlaceRemovedListener {
        void onPlaceRemoved();
    }

    public SavedPlaceAdapter(Context context, List<SavedPlace> savedPlaces) {
        this.context = context;
        this.savedPlaces = savedPlaces;
        this.inflater = LayoutInflater.from(context);
        this.dbManager = TasteBuddiesDatabaseManager.getInstance(context);
        
        // Get current user ID from SharedPreferences
        SharedPreferences prefs = context.getSharedPreferences("TasteBuddiesPrefs", Context.MODE_PRIVATE);
        this.currentUserId = prefs.getInt("userId", -1);
    }

    public void setOnPlaceRemovedListener(OnPlaceRemovedListener listener) {
        this.onPlaceRemovedListener = listener;
    }

    @Override
    public int getCount() {
        return savedPlaces.size();
    }

    @Override
    public Object getItem(int position) {
        return savedPlaces.get(position);
    }

    @Override
    public long getItemId(int position) {
        return savedPlaces.get(position).getSavedPlaceId();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.item_saved_place, parent, false);
            holder = new ViewHolder();
            holder.textViewPlaceName = convertView.findViewById(R.id.textViewPlaceName);
            holder.ratingBar = convertView.findViewById(R.id.ratingBar);
            holder.textViewRating = convertView.findViewById(R.id.textViewRating);
            holder.textViewReviewCount = convertView.findViewById(R.id.textViewReviewCount);
            holder.textViewPlaceType = convertView.findViewById(R.id.textViewPlaceType);
            holder.textViewDistance = convertView.findViewById(R.id.textViewDistance);
            holder.textViewStatus = convertView.findViewById(R.id.textViewStatus);
            holder.imageView = convertView.findViewById(R.id.imageView);
            holder.buttonUnsave = convertView.findViewById(R.id.buttonUnsave);
            holder.buttonDirections = convertView.findViewById(R.id.buttonDirections);
            holder.buttonCall = convertView.findViewById(R.id.buttonCall);
            holder.buttonWebsite = convertView.findViewById(R.id.buttonWebsite);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        SavedPlace place = savedPlaces.get(position);
        final int currentPosition = position;

        // Set place name
        holder.textViewPlaceName.setText(place.getPlaceName());

        // Set rating
        holder.ratingBar.setRating(place.getRating());
        holder.textViewRating.setText(String.format("%.1f", place.getRating()));
        holder.textViewReviewCount.setText("(" + place.getReviewCount() + " reviews)");

        // Set place type
        holder.textViewPlaceType.setText(place.getPlaceType());

        // Set distance
        holder.textViewDistance.setText(place.getDistance());

        // Set status
        holder.textViewStatus.setText(place.getStatus());

        // Load image
        if (place.getImageUrl() != null && !place.getImageUrl().isEmpty()) {
            Glide.with(context)
                .load(place.getImageUrl())
                .placeholder(R.drawable.food1)
                .error(R.drawable.food1)
                .centerCrop()
                .into(holder.imageView);
        } else {
            holder.imageView.setImageResource(R.drawable.food1);
        }

        // Set up unsave button
        holder.buttonUnsave.setOnClickListener(v -> {
            SavedPlace placeToRemove = savedPlaces.get(currentPosition);
            dbManager.deleteSavedPlace(currentUserId, placeToRemove.getPlaceName(),
                    placeToRemove.getLatitude(), placeToRemove.getLongitude());
            Toast.makeText(context, "Place removed from Want to Try", Toast.LENGTH_SHORT).show();
            
            // Remove from list and notify adapter
            savedPlaces.remove(currentPosition);
            notifyDataSetChanged();
            
            // Notify listener to refresh the count
            if (onPlaceRemovedListener != null) {
                onPlaceRemovedListener.onPlaceRemoved();
            }
        });

        // Set up action buttons
        holder.buttonDirections.setOnClickListener(v -> {
            String uri = "http://maps.google.com/maps?daddr=" + 
                        place.getLatitude() + "," + place.getLongitude();
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
            context.startActivity(intent);
        });

        holder.buttonCall.setOnClickListener(v -> {
            if (place.getPhone() != null && !place.getPhone().isEmpty()) {
                Intent intent = new Intent(Intent.ACTION_DIAL);
                intent.setData(Uri.parse("tel:" + place.getPhone()));
                context.startActivity(intent);
            }
        });

        holder.buttonWebsite.setOnClickListener(v -> {
            if (place.getWebsite() != null && !place.getWebsite().isEmpty()) {
                String websiteUrl = place.getWebsite();
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
        TextView textViewPlaceName;
        RatingBar ratingBar;
        TextView textViewRating;
        TextView textViewReviewCount;
        TextView textViewPlaceType;
        TextView textViewDistance;
        TextView textViewStatus;
        ImageView imageView;
        ImageButton buttonUnsave;
        Button buttonDirections;
        Button buttonCall;
        Button buttonWebsite;
    }
}

