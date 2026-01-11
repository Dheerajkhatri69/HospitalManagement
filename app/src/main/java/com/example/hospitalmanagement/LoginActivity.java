package com.example.hospitalmanagement;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;

public class LoginActivity extends BaseActivity {

    // UI Components
    private TextInputEditText emailEditText, passwordEditText;
    private MaterialButton loginButton;
    private ProgressBar loginProgressBar;
    private View backButton, forgotPasswordText, signupText;

    // Members
    private com.example.hospitalmanagement.api.ApiService apiService;
    private com.example.hospitalmanagement.utils.SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // API & Session Init
        apiService = com.example.hospitalmanagement.api.RetrofitClient.getApiService();
        sessionManager = new com.example.hospitalmanagement.utils.SessionManager(this);

        // Check if already logged in (Optional, usually done in Splash)
        if (sessionManager.isLoggedIn()) {
            navigateBasedOnRole(sessionManager.getRole());
        }

        initializeViews();
        setupClickListeners();
    }

    private void initializeViews() {
        // EditTexts
        emailEditText = findViewById(R.id.emailEditText);
        passwordEditText = findViewById(R.id.passwordEditText);

        // Buttons
        loginButton = findViewById(R.id.loginButton);
        loginProgressBar = findViewById(R.id.loginProgressBar);

        // Clickable Views
        backButton = findViewById(R.id.backButton);
        forgotPasswordText = findViewById(R.id.forgotPasswordText);
        signupText = findViewById(R.id.signupText);
    }

    private void setupClickListeners() {
        // Back Button - Goes back to previous screen
        backButton.setOnClickListener(v -> {
            onBackPressed();
        });

        // Login Button - Calls API
        loginButton.setOnClickListener(v -> {
            String email = emailEditText.getText().toString().trim();
            String password = passwordEditText.getText().toString().trim();

            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
            } else {
                performLogin(email, password);
            }
        });

        // Forgot Password - Shows message
        forgotPasswordText.setOnClickListener(v -> {
            Toast.makeText(this, "Forgot password feature coming soon", Toast.LENGTH_SHORT).show();
        });

        // Sign Up Text - Navigates to SignUp activity
        signupText.setOnClickListener(v -> {
            navigateToSignUp();
        });
    }

    private void performLogin(String email, String password) {
        loginButton.setEnabled(false);
        loginButton.setText(""); // Hide text
        loginProgressBar.setVisibility(View.VISIBLE);
        
        com.example.hospitalmanagement.model.LoginRequest request = new com.example.hospitalmanagement.model.LoginRequest(
                email, password);

        apiService.login(request).enqueue(new retrofit2.Callback<com.example.hospitalmanagement.model.LoginResponse>() {
            @Override
            public void onResponse(retrofit2.Call<com.example.hospitalmanagement.model.LoginResponse> call,
                    retrofit2.Response<com.example.hospitalmanagement.model.LoginResponse> response) {
                loginButton.setEnabled(true);
                loginButton.setText("Login");
                loginProgressBar.setVisibility(View.GONE);

                if (response.isSuccessful() && response.body() != null) {
                    com.example.hospitalmanagement.model.LoginResponse loginResponse = response.body();

                    // Save Session with patient_id and doctor_id
                    sessionManager.createLoginSession(
                            loginResponse.getUserId(),
                            loginResponse.getFullName(),
                            loginResponse.getRole(),
                            loginResponse.getEmail(),
                            loginResponse.getPatientId(),
                            loginResponse.getDoctorId());

                    Toast.makeText(LoginActivity.this, "Login Successful", Toast.LENGTH_SHORT).show();
                    navigateBasedOnRole(loginResponse.getRole());
                } else {
                    try {
                        String errorBody = response.errorBody() != null ? response.errorBody().string() : "Unknown error";
                        android.util.Log.e("LoginActivity", "Login Failed: " + response.code() + " - " + errorBody);
                        
                        new com.google.android.material.dialog.MaterialAlertDialogBuilder(LoginActivity.this)
                            .setTitle("Login Failed")
                            .setMessage("Code: " + response.code() + "\nError: " + errorBody)
                            .setPositiveButton("OK", null)
                            .show();
                    } catch (Exception e) {
                        Toast.makeText(LoginActivity.this, "Login Failed: " + response.code(), Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onFailure(retrofit2.Call<com.example.hospitalmanagement.model.LoginResponse> call,
                    Throwable t) {
                loginButton.setEnabled(true);
                loginButton.setText("Login");
                loginProgressBar.setVisibility(View.GONE);

                String errorMessage = "Network Error: ";
                if (t instanceof java.net.SocketTimeoutException) {
                    errorMessage += "Connection Timed Out. Server might be sleeping.";
                } else if (t instanceof java.net.UnknownHostException) {
                    errorMessage += "Unknown Host. Check Internet.";
                } else if (t instanceof java.io.IOException) {
                    errorMessage += "IO Error: " + t.getMessage();
                } else {
                    errorMessage += t.getClass().getSimpleName() + ": " + t.getMessage();
                }
                
                android.util.Log.e("LoginActivity", "Login Failure", t);
                
                new com.google.android.material.dialog.MaterialAlertDialogBuilder(LoginActivity.this)
                    .setTitle("Login Error")
                    .setMessage(errorMessage)
                    .setPositiveButton("OK", null)
                    .show();
            }
        });
    }

    private void navigateBasedOnRole(String role) {
        Intent intent;
        if (role.equalsIgnoreCase("doctor")) {
            intent = new Intent(LoginActivity.this, MainActivity.class);
        } else if (role.equalsIgnoreCase("patient")) {
            intent = new Intent(LoginActivity.this, PatientActivity.class);
        } else if (role.equalsIgnoreCase("admin")) {
            intent = new Intent(LoginActivity.this, AdminActivity.class);
        } else {
            // Default or Error
            Toast.makeText(this, "Unknown Role: " + role, Toast.LENGTH_SHORT).show();
            return;
        }
        startActivity(intent);
        finish();
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
    }

    private void navigateToSignUp() {
        Intent intent = new Intent(LoginActivity.this, SignupActivity.class);
        startActivity(intent);
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
    }
}
