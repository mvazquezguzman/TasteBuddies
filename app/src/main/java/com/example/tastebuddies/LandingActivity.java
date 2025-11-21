package com.example.tastebuddies;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class LandingActivity extends AppCompatActivity {
    private ImageButton arrowButton;
    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_landing);

        // Handle window insets to position images correctly below status bar
        View rootView = findViewById(android.R.id.content);
        ViewCompat.setOnApplyWindowInsetsListener(rootView, (v, insets) -> {
            int topInset = insets.getInsets(WindowInsetsCompat.Type.systemBars()).top;
            return insets;
        });

        sharedPreferences = getSharedPreferences("TasteBuddiesPrefs", MODE_PRIVATE);

        // Check if user is already logged in
        if (sharedPreferences.getInt("userId", -1) != -1) {
            startActivity(new Intent(LandingActivity.this, MainActivity.class));
            finish();
            return;
        }

        arrowButton = findViewById(R.id.arrowButton);

        arrowButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LandingActivity.this, SignInActivity.class));
            }
        });
    }
}
