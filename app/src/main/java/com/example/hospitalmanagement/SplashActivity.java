package com.example.hospitalmanagement;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import com.example.hospitalmanagement.utils.SessionManager;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_splash);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                checkSessionAndNavigate();
            }
        }, 1000);
    }

    private void checkSessionAndNavigate() {
        SessionManager sessionManager = new SessionManager(this);
        if (sessionManager.isLoggedIn()) {
            String role = sessionManager.getRole();
            Intent intent;
            if (role.equalsIgnoreCase("doctor")) {
                intent = new Intent(SplashActivity.this, MainActivity.class);
            } else if (role.equalsIgnoreCase("patient")) {
                intent = new Intent(SplashActivity.this, PatientActivity.class);
            } else if (role.equalsIgnoreCase("admin")) {
                intent = new Intent(SplashActivity.this, AdminActivity.class);
            } else {
                // Fallback
                intent = new Intent(SplashActivity.this, LoginActivity.class);
            }
            startActivity(intent);
        } else {
            // Not logged in, proceed to onboarding flow
            Intent intent = new Intent(SplashActivity.this, SplashActivityTwo.class);
            startActivity(intent);
        }
        finish();
    }
}
