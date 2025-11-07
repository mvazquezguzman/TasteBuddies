package com.example.tastebuddies;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.location.Location;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;


public class FragmentUpload extends Fragment {
    private static final int REQUEST_IMAGE_CAPTURE = 1;
    private static final int REQUEST_IMAGE_PICK = 2;
    private static final int REQUEST_CAMERA_PERMISSION = 100;
    private static final int REQUEST_LOCATION_PERMISSION = 101;

    private ImageView imageViewFood;
    private EditText editTextFoodName, editTextRestaurant, editTextLocation, editTextReview;
    private RatingBar ratingBar;
    private Button buttonSelectImage, buttonTakePhoto, buttonPost;
    private Bitmap selectedBitmap;
    private FusedLocationProviderClient fusedLocationClient;
    private double latitude, longitude;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_upload, container, false);

        // Apply Window Insets to Handle Status Bar and Camera Cutout
        ViewCompat.setOnApplyWindowInsetsListener(view, (v, insets) -> {
            int topInset = insets.getInsets(WindowInsetsCompat.Type.systemBars()).top;
            Resources resources = getResources();
            int extraPadding = (int) (16 * resources.getDisplayMetrics().density);
            int top = Math.max(topInset + extraPadding, v.getPaddingTop());
            v.setPadding(v.getPaddingLeft(), top, v.getPaddingRight(), v.getPaddingBottom());
            return insets;
        });

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity());

        imageViewFood = view.findViewById(R.id.imageViewFood);
        editTextFoodName = view.findViewById(R.id.editTextFoodName);
        editTextRestaurant = view.findViewById(R.id.editTextRestaurant);
        editTextLocation = view.findViewById(R.id.editTextLocation);
        editTextReview = view.findViewById(R.id.editTextReview);
        ratingBar = view.findViewById(R.id.ratingBar);
        buttonSelectImage = view.findViewById(R.id.buttonSelectImage);
        buttonTakePhoto = view.findViewById(R.id.buttonTakePhoto);
        buttonPost = view.findViewById(R.id.buttonPost);

        buttonSelectImage.setOnClickListener(v -> selectImageFromGallery());
        buttonTakePhoto.setOnClickListener(v -> takePhoto());
        buttonPost.setOnClickListener(v -> createPost());

        getCurrentLocation();

        return view;
    }

    private void selectImageFromGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, REQUEST_IMAGE_PICK);
    }

    private void takePhoto() {
        if (ContextCompat.checkSelfPermission(requireActivity(), Manifest.permission.CAMERA) 
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(requireActivity(), 
                    new String[]{Manifest.permission.CAMERA}, REQUEST_CAMERA_PERMISSION);
            return;
        }

        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(requireActivity().getPackageManager()) != null) {
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
        }
    }

    private void getCurrentLocation() {
        if (ContextCompat.checkSelfPermission(requireActivity(), Manifest.permission.ACCESS_FINE_LOCATION) 
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(requireActivity(), 
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_LOCATION_PERMISSION);
            return;
        }

        fusedLocationClient.getLastLocation().addOnSuccessListener(requireActivity(), location -> {
            if (location != null) {
                latitude = location.getLatitude();
                longitude = location.getLongitude();
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == REQUEST_IMAGE_CAPTURE && data != null) {
                Bundle extras = data.getExtras();
                if (extras != null) {
                    selectedBitmap = (Bitmap) extras.get("data");
                    imageViewFood.setImageBitmap(selectedBitmap);
                }
            } else if (requestCode == REQUEST_IMAGE_PICK && data != null) {
                try {
                    selectedBitmap = MediaStore.Images.Media.getBitmap(
                            requireActivity().getContentResolver(), data.getData());
                    imageViewFood.setImageBitmap(selectedBitmap);
                } catch (Exception e) {
                    Toast.makeText(getActivity(), "Error loading image", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        
        if (requestCode == REQUEST_CAMERA_PERMISSION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                takePhoto();
            } else {
                Toast.makeText(getActivity(), "Camera permission denied", Toast.LENGTH_SHORT).show();
            }
        } else if (requestCode == REQUEST_LOCATION_PERMISSION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getCurrentLocation();
            }
        }
    }

    private void createPost() {
        String foodName = editTextFoodName.getText().toString().trim();
        String restaurant = editTextRestaurant.getText().toString().trim();
        String location = editTextLocation.getText().toString().trim();
        String review = editTextReview.getText().toString().trim();
        int rating = (int) ratingBar.getRating();

        if (foodName.isEmpty()) {
            Toast.makeText(getActivity(), "Please enter food name", Toast.LENGTH_SHORT).show();
            return;
        }

        if (rating == 0) {
            Toast.makeText(getActivity(), "Please provide a rating", Toast.LENGTH_SHORT).show();
            return;
        }

        if (selectedBitmap == null) {
            Toast.makeText(getActivity(), "Please select an image", Toast.LENGTH_SHORT).show();
            return;
        }

        Toast.makeText(getActivity(), "Post creation disabled", Toast.LENGTH_SHORT).show();
    }
}
