package com.example.tastebuddies;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class SignInActivity extends AppCompatActivity {
    private EditText editTextEmail, editTextPassword;
    private Button buttonLogin;
    private TextView textViewRegister;
    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signin);

        sharedPreferences = getSharedPreferences("TasteBuddiesPrefs", MODE_PRIVATE);

        // Check if user is already logged in
        if (sharedPreferences.getInt("userId", -1) != -1) {
            startActivity(new Intent(SignInActivity.this, MainActivity.class));
            finish();
            return;
        }

        editTextEmail = findViewById(R.id.editTextEmail);
        editTextPassword = findViewById(R.id.editTextPassword);
        buttonLogin = findViewById(R.id.buttonLogin);
        Button buttonDemoLogin = findViewById(R.id.buttonDemoLogin);
        textViewRegister = findViewById(R.id.textViewRegister);

        buttonLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loginUser();
            }
        });

        buttonDemoLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loginAsDemo();
            }
        });

        textViewRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(SignInActivity.this, SignUpActivity.class));
            }
        });
    }

    private void loginUser() {
        String email = editTextEmail.getText().toString().trim();
        String password = editTextPassword.getText().toString().trim();

        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
            return;
        }


        // Demo Account Credentials
        String demoEmail = "demo@tastebuddies.com";
        String demoPassword = "demo123";

        if (email.equalsIgnoreCase(demoEmail) && password.equals(demoPassword)) {
            // Save demo user ID
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putInt("userId", 1);
            editor.putString("username", "Demo User");
            editor.putString("email", demoEmail);
            editor.apply();

            Toast.makeText(this, "Logged in as Demo User", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(SignInActivity.this, MainActivity.class));
            finish();
        } else {
            Toast.makeText(this, "Invalid credentials. Use demo@tastebuddies.com / demo123", Toast.LENGTH_LONG).show();
        }

    }

    private void loginAsDemo() {
        // Auto-fill Demo Credentials to login
        editTextEmail.setText("demo@tastebuddies.com");
        editTextPassword.setText("demo123");
        loginUser();
    }
}
