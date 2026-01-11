package com.example.hospitalmanagement;

import android.os.Bundle;
import android.widget.Toast;

import com.example.hospitalmanagement.api.ApiService;
import com.example.hospitalmanagement.api.RetrofitClient;
import com.example.hospitalmanagement.model.DoctorCreate;
import com.example.hospitalmanagement.model.DoctorResponse;
import com.example.hospitalmanagement.model.MessageResponse;
import com.example.hospitalmanagement.model.UserCreate;
import com.google.android.material.textfield.TextInputEditText;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AdminEditDoctorActivity extends BaseActivity {

    private TextInputEditText editName, editEmail, editPhone, editAddress, editExperience, editBio;
    private ApiService apiService;
    private int doctorId;
    private int userId;
    private String currentRole = "doctor"; // We know we are editing a doctor

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_edit_doctor);

        doctorId = getIntent().getIntExtra("doctor_id", -1);
        if (doctorId == -1) {
            Toast.makeText(this, "Error: Invalid Doctor ID", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        apiService = RetrofitClient.getApiService();
        initializeViews();
        loadDoctorDetails();
    }

    private void initializeViews() {
        editName = findViewById(R.id.edit_name);
        editEmail = findViewById(R.id.edit_email);
        editPhone = findViewById(R.id.edit_phone);
        editAddress = findViewById(R.id.edit_address);
        editExperience = findViewById(R.id.edit_experience);
        editBio = findViewById(R.id.edit_bio);

        findViewById(R.id.btn_back).setOnClickListener(v -> finish());
        findViewById(R.id.btn_save).setOnClickListener(v -> saveChanges());
    }

    private void loadDoctorDetails() {
        apiService.getDoctorById(doctorId).enqueue(new Callback<DoctorResponse>() {
            @Override
            public void onResponse(Call<DoctorResponse> call, Response<DoctorResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    populateFields(response.body());
                } else {
                    Toast.makeText(AdminEditDoctorActivity.this, "Failed to load doctor details", Toast.LENGTH_SHORT).show();
                    finish();
                }
            }

            @Override
            public void onFailure(Call<DoctorResponse> call, Throwable t) {
                Toast.makeText(AdminEditDoctorActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                finish();
            }
        });
    }

    private void populateFields(DoctorResponse doctor) {
        userId = doctor.getUserId();
        editName.setText(doctor.getFullName());
        editEmail.setText(doctor.getEmail());
        editPhone.setText(doctor.getOfficePhone());
        editAddress.setText(doctor.getOfficeAddress());
        if (doctor.getYearsExperience() != null) {
            editExperience.setText(String.valueOf(doctor.getYearsExperience()));
        }
        editBio.setText(doctor.getBio());
    }

    private void saveChanges() {
        String name = editName.getText().toString().trim();
        String email = editEmail.getText().toString().trim();
        String phone = editPhone.getText().toString().trim();
        String address = editAddress.getText().toString().trim();
        String experienceStr = editExperience.getText().toString().trim();
        String bio = editBio.getText().toString().trim();

        if (name.isEmpty() || email.isEmpty()) {
            Toast.makeText(this, "Name and Email are required", Toast.LENGTH_SHORT).show();
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

        // Update User Info first
        UserCreate userUpdate = new UserCreate(name, email, null, currentRole);
        
        Integer finalExperience = experience;
        apiService.updateUser(userId, userUpdate).enqueue(new Callback<MessageResponse>() {
            @Override
            public void onResponse(Call<MessageResponse> call, Response<MessageResponse> response) {
                if (response.isSuccessful()) {
                    // If user update successful, update doctor info
                    updateDoctorInfo(bio, phone, address, finalExperience);
                } else {
                    Toast.makeText(AdminEditDoctorActivity.this, "Failed to update user info", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<MessageResponse> call, Throwable t) {
                Toast.makeText(AdminEditDoctorActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void updateDoctorInfo(String bio, String phone, String address, Integer experience) {
        DoctorCreate doctorUpdate = new DoctorCreate(userId, bio, phone, address, experience);
        
        apiService.updateDoctor(doctorId, doctorUpdate).enqueue(new Callback<MessageResponse>() {
            @Override
            public void onResponse(Call<MessageResponse> call, Response<MessageResponse> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(AdminEditDoctorActivity.this, "Doctor updated successfully", Toast.LENGTH_SHORT).show();
                    finish();
                } else {
                    Toast.makeText(AdminEditDoctorActivity.this, "Failed to update doctor info", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<MessageResponse> call, Throwable t) {
                Toast.makeText(AdminEditDoctorActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}