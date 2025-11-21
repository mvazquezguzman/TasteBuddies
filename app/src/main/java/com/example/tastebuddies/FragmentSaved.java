package com.example.tastebuddies;

import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import androidx.core.content.ContextCompat;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;
import java.util.List;

public class FragmentSaved extends Fragment implements OnMapReadyCallback {
    private ListView listViewSaved;
    private SavedPlaceAdapter savedPlaceAdapter;
    private PostAdapter adapter;
    private int currentUserId;
    private TextView textViewEmpty;
    private TextView textViewWantToTryCount;
    private LinearLayout layoutWantToTry;
    private LinearLayout buttonList;
    private LinearLayout buttonMap;
    private TextView textList;
    private TextView textMap;
    private FrameLayout mapViewContainer;
    private MapView mapView;
    private GoogleMap googleMap;
    private List<Post> savedPosts;
    private List<SavedPlace> savedPlaces;
    private TasteBuddiesDatabaseManager dbManager;
    private boolean isListView = true;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_saved, container, false);

        MainActivity mainActivity = (MainActivity) getActivity();
        if (mainActivity != null) {
            currentUserId = mainActivity.getCurrentUserId();
        }
        
        dbManager = TasteBuddiesDatabaseManager.getInstance(requireContext());

        // Apply Window Insets to Handle Status Bar and Camera Cutout
        ViewCompat.setOnApplyWindowInsetsListener(view, (v, insets) -> {
            int topInset = insets.getInsets(WindowInsetsCompat.Type.systemBars()).top;
            Resources resources = getResources();
            int extraPadding = (int) (16 * resources.getDisplayMetrics().density);
            int top = Math.max(topInset + extraPadding, v.getPaddingTop());
            v.setPadding(v.getPaddingLeft(), top, v.getPaddingRight(), v.getPaddingBottom());
            return insets;
        });

        listViewSaved = view.findViewById(R.id.listViewSaved);
        textViewEmpty = view.findViewById(R.id.textViewEmpty);
        textViewWantToTryCount = view.findViewById(R.id.textViewWantToTryCount);
        layoutWantToTry = view.findViewById(R.id.layoutWantToTry);
        mapViewContainer = view.findViewById(R.id.mapViewContainer);
        
        // Toggle button components
        buttonList = view.findViewById(R.id.buttonList);
        buttonMap = view.findViewById(R.id.buttonMap);
        textList = view.findViewById(R.id.textList);
        textMap = view.findViewById(R.id.textMap);
        
        // Initialize MapView
        mapView = new MapView(requireContext());
        if (savedInstanceState != null) {
            mapView.onCreate(savedInstanceState);
        } else {
            mapView.onCreate(null);
        }
        mapView.getMapAsync(this);
        mapViewContainer.addView(mapView);

        // Set up toggle button listeners
        buttonList.setOnClickListener(v -> {
            if (!isListView) {
                setListView();
            }
        });

        buttonMap.setOnClickListener(v -> {
            if (isListView) {
                setMapView();
            }
        });

        // Set initial state (List view active)
        setListView();

        loadCounts();
        loadSavedPosts();

        layoutWantToTry.setOnClickListener(v -> {
            // Already in Want to Try section, just refresh
            loadSavedPosts();
        });

        listViewSaved.setOnItemClickListener((parent, view1, position, id) -> {
            // Saved places don't navigate to post detail, they're just displayed
            // The action buttons (Directions, Call, Website) handle interactions
        });

        return view;
    }

    private void loadCounts() {
        int count = dbManager.getSavedPlacesCount(currentUserId);
        textViewWantToTryCount.setText(String.valueOf(count));
    }

    private void loadSavedPosts() {
        savedPosts = new ArrayList<>();
        savedPlaces = dbManager.getSavedPlaces(currentUserId);
        
        // Update visibility based on current view mode
        if (isListView) {
            if (savedPlaces.isEmpty()) {
                textViewEmpty.setVisibility(View.VISIBLE);
                listViewSaved.setVisibility(View.GONE);
            } else {
                textViewEmpty.setVisibility(View.GONE);
                listViewSaved.setVisibility(View.VISIBLE);
                savedPlaceAdapter = new SavedPlaceAdapter(requireContext(), savedPlaces);
                savedPlaceAdapter.setOnPlaceRemovedListener(() -> {
                    loadCounts();
                    loadSavedPosts();
                    // Update map if in map view
                    if (!isListView && googleMap != null) {
                        updateMapMarkers();
                    }
                });
                listViewSaved.setAdapter(savedPlaceAdapter);
            }
        } else {
            updateMapMarkers();
        }
    }

    private void setListView() {
        isListView = true;
        buttonList.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.tea_green));
        buttonMap.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.white));
        textList.setTextColor(ContextCompat.getColor(requireContext(), R.color.black));
        textMap.setTextColor(ContextCompat.getColor(requireContext(), R.color.black));
        
        // Show ListView, hide MapView
        listViewSaved.setVisibility(View.VISIBLE);
        mapViewContainer.setVisibility(View.GONE);
        
        // Show "Want to Try" section
        layoutWantToTry.setVisibility(View.VISIBLE);
        
        // Update empty state visibility
        if (savedPlaces == null || savedPlaces.isEmpty()) {
            textViewEmpty.setVisibility(View.VISIBLE);
            listViewSaved.setVisibility(View.GONE);
        } else {
            textViewEmpty.setVisibility(View.GONE);
        }
    }

    private void setMapView() {
        isListView = false;
        buttonList.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.white));
        buttonMap.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.tea_green));
        textList.setTextColor(ContextCompat.getColor(requireContext(), R.color.black));
        textMap.setTextColor(ContextCompat.getColor(requireContext(), R.color.black));
        
        // Hide ListView, show MapView
        listViewSaved.setVisibility(View.GONE);
        textViewEmpty.setVisibility(View.GONE);
        mapViewContainer.setVisibility(View.VISIBLE);
        
        // Hide "Want to Try" section
        layoutWantToTry.setVisibility(View.GONE);
        
        // Update map markers
        updateMapMarkers();
    }
    
    @Override
    public void onMapReady(GoogleMap map) {
        googleMap = map;
        updateMapMarkers();
    }
    
    private void updateMapMarkers() {
        if (googleMap == null) {
            return;
        }
        
        googleMap.clear();
        
        if (savedPlaces == null || savedPlaces.isEmpty()) {
            return;
        }
        
        // Add markers for all saved places
        LatLng firstLocation = null;
        for (SavedPlace place : savedPlaces) {
            if (place.getLatitude() != 0 && place.getLongitude() != 0) {
                LatLng location = new LatLng(place.getLatitude(), place.getLongitude());
                if (firstLocation == null) {
                    firstLocation = location;
                }
                
                googleMap.addMarker(new MarkerOptions()
                    .position(location)
                    .title(place.getPlaceName())
                    .snippet(place.getPlaceType()));
            }
        }
        
        // Move camera to first location or default location
        if (firstLocation != null) {
            googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(firstLocation, 12f));
        } else {
            // Default to Georgia Gwinnett College location if no places have coordinates
            googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(33.9425, -84.0686), 12f));
        }
    }
    
    @Override
    public void onResume() {
        super.onResume();
        if (mapView != null) {
            mapView.onResume();
        }
        loadCounts();
        loadSavedPosts();
    }
    
    @Override
    public void onPause() {
        super.onPause();
        if (mapView != null) {
            mapView.onPause();
        }
    }
    
    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mapView != null) {
            mapView.onDestroy();
        }
    }
    
    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (mapView != null) {
            mapView.onSaveInstanceState(outState);
        }
    }
    
    @Override
    public void onLowMemory() {
        super.onLowMemory();
        if (mapView != null) {
            mapView.onLowMemory();
        }
    }
}

