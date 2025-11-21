package com.example.tastebuddies;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;

public class MainActivity extends AppCompatActivity {
    private BottomNavigationView bottomNavigationView;
    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        sharedPreferences = getSharedPreferences("TasteBuddiesPrefs", MODE_PRIVATE);

        // Check if user is logged in
        if (sharedPreferences.getInt("userId", -1) == -1) {
            startActivity(new Intent(this, SignInActivity.class));
            finish();
            return;
        }

        bottomNavigationView = findViewById(R.id.bottomNavigationView);

        // Set default fragment
        if (savedInstanceState == null) {
            loadFragment(new FragmentHome());
        }

        bottomNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                Fragment fragment = null;
                int itemId = item.getItemId();
                
                if (itemId == R.id.nav_home) {
                    fragment = new FragmentHome();
                } else if (itemId == R.id.nav_search) {
                    fragment = new FragmentSearch();
                } else if (itemId == R.id.nav_upload) {
                    fragment = new FragmentUpload();
                } else if (itemId == R.id.nav_saved) {
                    fragment = new FragmentSaved();
                } else if (itemId == R.id.nav_profile) {
                    fragment = new FragmentProfile();
                }

                if (fragment != null) {
                    loadFragment(fragment);
                    return true;
                }
                return false;
            }
        });
    }

    public void loadFragment(Fragment fragment) {
        String tag = null;
        if (fragment instanceof FragmentHome) {
            tag = "home";
        }
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragmentContainer, fragment, tag)
                .commit();
    }
    
    public void loadFragmentNow(Fragment fragment) {
        String tag = null;
        if (fragment instanceof FragmentHome) {
            tag = "home";
        }
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragmentContainer, fragment, tag)
                .commitNow();
    }

    public int getCurrentUserId() {
        return sharedPreferences.getInt("userId", -1);
    }

    public BottomNavigationView getBottomNavigationView() {
        return bottomNavigationView;
    }

    public void loadSearchFragment() {
        loadFragment(new FragmentSearch());
    }

    public void switchToSavedFragment() {
        loadFragment(new FragmentSaved());
        bottomNavigationView.setSelectedItemId(R.id.nav_saved);
    }

    public void refreshHomeFragment() {
        // Always create a new home fragment to ensure fresh data is loaded
        // This is more reliable than trying to refresh an existing fragment
        FragmentHome homeFragment = new FragmentHome();
        loadFragmentNow(homeFragment);
        if (bottomNavigationView != null) {
            bottomNavigationView.setSelectedItemId(R.id.nav_home);
        }
    }
}
