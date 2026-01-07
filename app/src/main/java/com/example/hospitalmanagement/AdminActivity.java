package com.example.hospitalmanagement;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.example.hospitalmanagement.utils.SessionManager;

public class AdminActivity extends AppCompatActivity {

    private SessionManager sessionManager;
    private TextView welcomeText;
    private Button btnManageDoctors, btnManagePatients, logoutButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin);

        sessionManager = new SessionManager(this);

        // Redirect if not logged in
        if (!sessionManager.isLoggedIn()) {
            startActivity(new Intent(this, LoginActivity.class));
            finish();
            return;
        }

        welcomeText = findViewById(R.id.welcomeText);
        btnManageDoctors = findViewById(R.id.btnManageDoctors);
        btnManagePatients = findViewById(R.id.btnManagePatients);
        logoutButton = findViewById(R.id.logoutButton);

        // Set Welcome Text
        String name = sessionManager.getFullName();
        welcomeText.setText("Welcome Admin, " + name);

        // Logout
        logoutButton.setOnClickListener(v -> {
            sessionManager.logoutUser();
            Intent intent = new Intent(AdminActivity.this, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        });

        // Buttons (Placeholders)
        btnManageDoctors.setOnClickListener(
                v -> Toast.makeText(this, "Manage Doctors Feature Coming Soon", Toast.LENGTH_SHORT).show());

        btnManagePatients.setOnClickListener(
                v -> Toast.makeText(this, "Manage Patients Feature Coming Soon", Toast.LENGTH_SHORT).show());
    }
}
