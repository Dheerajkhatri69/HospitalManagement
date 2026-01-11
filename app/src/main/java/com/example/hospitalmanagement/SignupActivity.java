package com.example.hospitalmanagement;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.hospitalmanagement.api.ApiService;
import com.example.hospitalmanagement.api.RetrofitClient;
import com.example.hospitalmanagement.model.PatientCreate;
import com.example.hospitalmanagement.model.PatientResponse;
import com.example.hospitalmanagement.model.UserCreate;
import com.example.hospitalmanagement.model.UserResponse;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SignupActivity extends BaseActivity {

    private TextInputEditText nameEditText, emailEditText, passwordEditText, confirmPasswordEditText;
    private MaterialButton signupButton;
    private ProgressBar signupProgressBar;
    private View backButton, loginText;
    private ApiService apiService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        apiService = RetrofitClient.getApiService();
        initializeViews();
        setupClickListeners();
    }

    private void initializeViews() {
        nameEditText = findViewById(R.id.nameEditText);
        emailEditText = findViewById(R.id.emailEditText);
        passwordEditText = findViewById(R.id.passwordEditText);
        confirmPasswordEditText = findViewById(R.id.confirmPasswordEditText);
        signupButton = findViewById(R.id.signupButton);
        signupProgressBar = findViewById(R.id.signupProgressBar);
        backButton = findViewById(R.id.backButton);
        loginText = findViewById(R.id.loginText);
    }

    private void setupClickListeners() {
        backButton.setOnClickListener(v -> {
            finish();
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        });

        signupButton.setOnClickListener(v -> {
            String name = nameEditText.getText().toString().trim();
            String email = emailEditText.getText().toString().trim();
            String password = passwordEditText.getText().toString().trim();
            String confirmPassword = confirmPasswordEditText.getText().toString().trim();

            if (name.isEmpty() || email.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
                Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
            } else if (!password.equals(confirmPassword)) {
                Toast.makeText(this, "Passwords do not match", Toast.LENGTH_SHORT).show();
            } else {
                performSignup(name, email, password);
            }
        });

        loginText.setOnClickListener(v -> {
            navigateToLogin();
        });
    }

    private void performSignup(String name, String email, String password) {
        signupButton.setEnabled(false);
        signupButton.setText(""); // Hide text
        signupProgressBar.setVisibility(View.VISIBLE);

        UserCreate userCreate = new UserCreate(name, email, password, "patient");

        apiService.createUser(userCreate).enqueue(new Callback<UserResponse>() {
            @Override
            public void onResponse(Call<UserResponse> call, Response<UserResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    createPatientProfile(response.body().getUserId());
                } else {
                    resetButtonState();
                    Toast.makeText(SignupActivity.this, "Signup failed: " + response.message(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<UserResponse> call, Throwable t) {
                resetButtonState();
                Toast.makeText(SignupActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void createPatientProfile(int userId) {
        // Create patient with default values
        PatientCreate patientCreate = new PatientCreate(
                userId,
                "", // DOB
                "", // Gender
                "", // Address
                "", // Phone
                "", // Emergency Contact
                "", // Medical History
                ""  // Insurance Info
        );

        apiService.createPatient(patientCreate).enqueue(new Callback<PatientResponse>() {
            @Override
            public void onResponse(Call<PatientResponse> call, Response<PatientResponse> response) {
                resetButtonState();
                if (response.isSuccessful()) {
                    Toast.makeText(SignupActivity.this, "Signup successful! Please login.", Toast.LENGTH_SHORT).show();
                    navigateToLogin();
                } else {
                    // Even if patient profile creation fails, the user is created. 
                    // We might still want to let them login, or handle this error.
                    // For now, let's treat it as success but warn.
                    Toast.makeText(SignupActivity.this, "User created but profile setup failed. Please contact support.", Toast.LENGTH_LONG).show();
                    navigateToLogin();
                }
            }

            @Override
            public void onFailure(Call<PatientResponse> call, Throwable t) {
                resetButtonState();
                Toast.makeText(SignupActivity.this, "Error creating profile: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void resetButtonState() {
        signupButton.setEnabled(true);
        signupButton.setText("Sign Up");
        signupProgressBar.setVisibility(View.GONE);
    }

    private void navigateToLogin() {
        Intent intent = new Intent(SignupActivity.this, LoginActivity.class);
        startActivity(intent);
        finish();
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
    }
}
