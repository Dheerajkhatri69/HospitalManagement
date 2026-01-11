package com.example.hospitalmanagement;

import android.os.Bundle;
import android.widget.Toast;

import com.example.hospitalmanagement.api.ApiService;
import com.example.hospitalmanagement.api.RetrofitClient;
import com.example.hospitalmanagement.model.DoctorCreate;
import com.example.hospitalmanagement.model.DoctorResponse;
import com.example.hospitalmanagement.model.UserCreate;
import com.example.hospitalmanagement.model.UserResponse;
import com.google.android.material.textfield.TextInputEditText;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AdminAddDoctorActivity extends BaseActivity {

    private TextInputEditText addName, addEmail, addPassword, addPhone, addAddress, addExperience, addBio;
    private ApiService apiService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_add_doctor);

        apiService = RetrofitClient.getApiService();
        initializeViews();
    }

    private void initializeViews() {
        addName = findViewById(R.id.add_name);
        addEmail = findViewById(R.id.add_email);
        addPassword = findViewById(R.id.add_password);
        addPhone = findViewById(R.id.add_phone);
        addAddress = findViewById(R.id.add_address);
        addExperience = findViewById(R.id.add_experience);
        addBio = findViewById(R.id.add_bio);

        findViewById(R.id.btn_back).setOnClickListener(v -> finish());
        findViewById(R.id.btn_add).setOnClickListener(v -> addNewDoctor());
    }

    private void addNewDoctor() {
        String name = addName.getText().toString().trim();
        String email = addEmail.getText().toString().trim();
        String password = addPassword.getText().toString().trim();
        String phone = addPhone.getText().toString().trim();
        String address = addAddress.getText().toString().trim();
        String experienceStr = addExperience.getText().toString().trim();
        String bio = addBio.getText().toString().trim();

        if (name.isEmpty() || email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Name, Email and Password are required", Toast.LENGTH_SHORT).show();
            return;
        }

        Integer experience = null;
        if (!experienceStr.isEmpty()) {
            try {
                experience = Integer.parseInt(experienceStr);
            } catch (NumberFormatException e) {
                Toast.makeText(this, "Invalid Experience", Toast.LENGTH_SHORT).show();
                return;
            }
        }

        // 1. Create User first
        UserCreate userCreate = new UserCreate(name, email, password, "doctor");
        Integer finalExperience = experience;

        // Show loading (optional but good)
        // For now using Toast, can be improved later
        Toast.makeText(this, "Creating user...", Toast.LENGTH_SHORT).show();

        apiService.createUser(userCreate).enqueue(new Callback<UserResponse>() {
            @Override
            public void onResponse(Call<UserResponse> call, Response<UserResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    int userId = response.body().getUserId();
                    createDoctorProfile(userId, bio, phone, address, finalExperience);
                } else {
                    Toast.makeText(AdminAddDoctorActivity.this, "Failed to create user", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<UserResponse> call, Throwable t) {
                Toast.makeText(AdminAddDoctorActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void createDoctorProfile(int userId, String bio, String phone, String address, Integer experience) {
        DoctorCreate doctorCreate = new DoctorCreate(userId, bio, phone, address, experience);

        apiService.createDoctor(doctorCreate).enqueue(new Callback<DoctorResponse>() {
            @Override
            public void onResponse(Call<DoctorResponse> call, Response<DoctorResponse> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(AdminAddDoctorActivity.this, "Doctor added successfully", Toast.LENGTH_SHORT).show();
                    finish();
                } else {
                    Toast.makeText(AdminAddDoctorActivity.this, "Failed to create doctor profile", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<DoctorResponse> call, Throwable t) {
                Toast.makeText(AdminAddDoctorActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
