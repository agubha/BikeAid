package com.example.bikeaid;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;

public class StartActivity extends AppCompatActivity {
    TextView login, emergencybreakdown;
    private FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getSupportActionBar() != null)
            getSupportActionBar().hide();

        //Get Firebase auth instance
        auth = FirebaseAuth.getInstance();

        if (auth.getCurrentUser() != null) {
            startActivity(new Intent(StartActivity.this, MainActivity.class));
            finish();
        }

        setContentView(R.layout.activity_start);
        login = findViewById(R.id.login);
        emergencybreakdown = findViewById(R.id.emergencyBreakdown);
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loginActivity();
            }
        });
        emergencybreakdown.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mapActivity();
            }
        });
    }

    private void mapActivity() {
        Intent intent = new Intent(StartActivity.this, MapsActivity.class);
        startActivity(intent);

    }

    private void loginActivity() {
        Intent intent = new Intent(StartActivity.this, LoginActivity.class);
        startActivity(intent);
    }
}
