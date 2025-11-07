package com.example.tastebuddies;

import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class FragmentProfile extends Fragment {
    private ImageView imageViewProfile;
    private TextView textViewDisplayName, textViewFullName, textViewBio;
    private TextView textViewPosts, textViewFollowers, textViewFollowing;
    private ImageButton imageButtonEdit, imageButtonMenu;
    private ImageButton imageButtonPosts;
    private RecyclerView recyclerViewPosts;
    private int currentUserId;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        MainActivity mainActivity = (MainActivity) getActivity();
        if (mainActivity != null) {
            currentUserId = mainActivity.getCurrentUserId();
        }

        // Apply Window Insets to Handle Status Bar and Camera Cutout
        ViewCompat.setOnApplyWindowInsetsListener(view, (v, insets) -> {
            int topInset = insets.getInsets(WindowInsetsCompat.Type.systemBars()).top;
            Resources resources = getResources();
            int extraPadding = (int) (16 * resources.getDisplayMetrics().density);

            if (v instanceof android.widget.ScrollView) {
                android.widget.ScrollView scrollView = (android.widget.ScrollView) v;
                if (scrollView.getChildCount() > 0) {
                    View child = scrollView.getChildAt(0);
                    if (child instanceof android.view.ViewGroup) {
                        int top = Math.max(topInset + extraPadding, child.getPaddingTop());
                        child.setPadding(
                            child.getPaddingLeft(),
                            top,
                            child.getPaddingRight(),
                            child.getPaddingBottom()
                        );
                    }
                }
            } else {
                int top = Math.max(topInset + extraPadding, v.getPaddingTop());
                v.setPadding(v.getPaddingLeft(), top, v.getPaddingRight(), v.getPaddingBottom());
            }
            return insets;
        });

        imageViewProfile = view.findViewById(R.id.imageViewProfile);
        UiUtils.makeCircular(imageViewProfile);
        
        textViewDisplayName = view.findViewById(R.id.textViewDisplayName);
        textViewFullName = view.findViewById(R.id.textViewFullName);
        textViewBio = view.findViewById(R.id.textViewBio);
        textViewPosts = view.findViewById(R.id.textViewPosts);
        textViewFollowers = view.findViewById(R.id.textViewFollowers);
        textViewFollowing = view.findViewById(R.id.textViewFollowing);
        
        imageButtonEdit = view.findViewById(R.id.imageButtonEdit);
        imageButtonMenu = view.findViewById(R.id.imageButtonMenu);
        imageButtonPosts = view.findViewById(R.id.imageButtonPosts);
        
        // Set up RecyclerView for grid
        recyclerViewPosts = view.findViewById(R.id.gridLayoutPosts);
        
        // Configure RecyclerView with GridLayoutManager (3 columns)
        if (recyclerViewPosts != null) {
            recyclerViewPosts.setLayoutManager(new GridLayoutManager(getContext(), 3));
            List<Post> posts = new ArrayList<>(); // TODO: Load actual posts
            PostGridAdapter postsAdapter = new PostGridAdapter(posts, postId -> {
                Intent intent = new Intent(getActivity(), PostDetailActivity.class);
                intent.putExtra("postId", postId);
                startActivity(intent);
            });
            recyclerViewPosts.setAdapter(postsAdapter);
        }
        
        if (imageButtonEdit != null) {
            imageButtonEdit.setOnClickListener(v -> showEditDialog());
        }
        
        if (imageButtonMenu != null) {
            imageButtonMenu.setOnClickListener(v -> showMenuDialog());
        }

        loadUserProfile();

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        loadUserProfile();
    }

    private void loadUserProfile() {
        if (imageViewProfile != null) {
            imageViewProfile.setImageResource(android.R.drawable.sym_def_app_icon);
        }
        
        if (textViewDisplayName != null) {
            textViewDisplayName.setText("MyUser Name");
        }
        
        if (textViewFullName != null) {
            textViewFullName.setText("MyUser Full Name");
        }
        
        if (textViewBio != null) {
            textViewBio.setText("So you two dig up, dig up dinosaurs? God creates dinosaurs. God destroys dinosaurs. God creates Man. Man destroys God. Man creates Dinosaurs.");
            textViewBio.setVisibility(View.VISIBLE);
        }
        
        if (textViewPosts != null) {
            textViewPosts.setText("300");
        }
        if (textViewFollowers != null) {
            textViewFollowers.setText("100");
        }
        if (textViewFollowing != null) {
            textViewFollowing.setText("103");
        }
    }
    
    
    private void showMenuDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Menu");
        builder.setItems(new String[]{"Sign Out"}, (dialog, which) -> {
            if (which == 0) {
                signOut();
            }
        });
        builder.show();
    }

    private void showEditDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Edit Profile");

        View dialogView = LayoutInflater.from(getActivity()).inflate(R.layout.dialog_edit_profile, null);
        EditText editUsername = dialogView.findViewById(R.id.editTextDialogUsername);
        EditText editBio = dialogView.findViewById(R.id.editTextDialogBio);

        editUsername.setText("user");
        editBio.setText("");

        builder.setView(dialogView);

        builder.setPositiveButton("Save", (dialog, which) -> {
            String username = editUsername.getText().toString().trim();
            String bio = editBio.getText().toString().trim();

            if (username.isEmpty()) {
                Toast.makeText(getActivity(), "Username cannot be empty", Toast.LENGTH_SHORT).show();
                return;
            }

            Toast.makeText(getActivity(), "Profile update disabled", Toast.LENGTH_SHORT).show();
        });

        builder.setNegativeButton("Cancel", null);

        builder.setNeutralButton("Change Photo", (dialog, which) -> {
            selectProfilePicture();
        });

        builder.show();
    }

    private void selectProfilePicture() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, 100);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 100 && data != null) {
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(
                        requireActivity().getContentResolver(), data.getData());
                imageViewProfile.setImageBitmap(bitmap);
            } catch (Exception e) {
                Toast.makeText(getActivity(), "Error loading image", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void signOut() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Sign Out");
        builder.setMessage("Are you sure you want to sign out?");
        builder.setPositiveButton("Sign Out", (dialog, which) -> {
            SharedPreferences sharedPreferences = requireActivity().getSharedPreferences("TasteBuddiesPrefs", android.content.Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.clear();
            editor.apply();
            
            Intent intent = new Intent(getActivity(), SignInActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            requireActivity().finish();
        });
        builder.setNegativeButton("Cancel", null);
        builder.show();
    }
}
