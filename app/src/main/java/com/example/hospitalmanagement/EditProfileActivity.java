package com.example.hospitalmanagement;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;

import com.example.hospitalmanagement.api.ApiService;
import com.example.hospitalmanagement.api.RetrofitClient;
import com.example.hospitalmanagement.model.DoctorCreate;
import com.example.hospitalmanagement.model.DoctorResponse;
import com.example.hospitalmanagement.model.LoginRequest;
import com.example.hospitalmanagement.model.LoginResponse;
import com.example.hospitalmanagement.model.MessageResponse;
import com.example.hospitalmanagement.model.PatientCreate;
import com.example.hospitalmanagement.model.PatientResponse;
import com.example.hospitalmanagement.model.UserCreate;
import com.example.hospitalmanagement.utils.SessionManager;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class EditProfileActivity extends BaseActivity {

    private TextInputEditText etFullName, etEmail, etNewPassword, etConfirmPassword, etCurrentPassword;
    private TextInputEditText etPhone, etAddress, etBio, etExperience;
    private TextInputEditText etDob, etGender, etEmergencyContact, etInsurance, etMedicalHistory;
    
    private TextInputLayout layoutBio, layoutExperience;
    private TextInputLayout layoutDob, layoutGender, layoutEmergency, layoutInsurance, layoutMedical;
    
    private MaterialCardView cardRoleSpecific;
    private MaterialButton btnSaveChanges;

    private SessionManager sessionManager;
    private ApiService apiService;
    private String userRole;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        // Setup Toolbar
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Edit Profile");
        }

        sessionManager = new SessionManager(this);
        apiService = RetrofitClient.getApiService();
        userRole = sessionManager.getRole();

        initializeViews();
        loadCurrentData();
        setupListeners();
    }

    private void initializeViews() {
        etFullName = findViewById(R.id.et_full_name);
        etEmail = findViewById(R.id.et_email);
        etNewPassword = findViewById(R.id.et_new_password);
        etConfirmPassword = findViewById(R.id.et_confirm_password);
        etCurrentPassword = findViewById(R.id.et_current_password);
        
        // Role specific fields
        cardRoleSpecific = findViewById(R.id.card_role_specific);
        etPhone = findViewById(R.id.et_phone);
        etAddress = findViewById(R.id.et_address);
        
        etBio = findViewById(R.id.et_bio);
        etExperience = findViewById(R.id.et_experience);
        
        etDob = findViewById(R.id.et_dob);
        etGender = findViewById(R.id.et_gender);
        etEmergencyContact = findViewById(R.id.et_emergency_contact);
        etInsurance = findViewById(R.id.et_insurance);
        etMedicalHistory = findViewById(R.id.et_medical_history);
        
        // Layouts for visibility toggling
        layoutBio = findViewById(R.id.layout_bio);
        layoutExperience = findViewById(R.id.layout_experience);
        layoutDob = findViewById(R.id.layout_dob);
        layoutGender = findViewById(R.id.layout_gender);
        layoutEmergency = findViewById(R.id.layout_emergency);
        layoutInsurance = findViewById(R.id.layout_insurance);
        layoutMedical = findViewById(R.id.layout_medical);

        btnSaveChanges = findViewById(R.id.btn_save_changes);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void loadCurrentData() {
        etFullName.setText(sessionManager.getFullName());
        etEmail.setText(sessionManager.getEmail());
        
        if ("doctor".equals(userRole)) {
            setupDoctorView();
        } else if ("patient".equals(userRole)) {
            setupPatientView();
        } else {
            cardRoleSpecific.setVisibility(View.GONE);
        }
    }
    
    private void setupDoctorView() {
        layoutBio.setVisibility(View.VISIBLE);
        layoutExperience.setVisibility(View.VISIBLE);
        
        Integer doctorId = sessionManager.getDoctorId();
        if (doctorId != null) {
            apiService.getDoctorById(doctorId).enqueue(new Callback<DoctorResponse>() {
                @Override
                public void onResponse(Call<DoctorResponse> call, Response<DoctorResponse> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        DoctorResponse doc = response.body();
                        etPhone.setText(doc.getOfficePhone());
                        etAddress.setText(doc.getOfficeAddress());
                        etBio.setText(doc.getBio());
                        if (doc.getYearsExperience() != null) {
                            etExperience.setText(String.valueOf(doc.getYearsExperience()));
                        }
                    }
                }

                @Override
                public void onFailure(Call<DoctorResponse> call, Throwable t) {
                    Toast.makeText(EditProfileActivity.this, "Failed to load doctor details", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }
    
    private void setupPatientView() {
        layoutDob.setVisibility(View.VISIBLE);
        layoutGender.setVisibility(View.VISIBLE);
        layoutEmergency.setVisibility(View.VISIBLE);
        layoutInsurance.setVisibility(View.VISIBLE);
        layoutMedical.setVisibility(View.VISIBLE);
        
        Integer patientId = sessionManager.getPatientId();
        if (patientId != null) {
            apiService.getPatientById(patientId).enqueue(new Callback<PatientResponse>() {
                @Override
                public void onResponse(Call<PatientResponse> call, Response<PatientResponse> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        PatientResponse pat = response.body();
                        etPhone.setText(pat.getPhone());
                        etAddress.setText(pat.getAddress());
                        etDob.setText(pat.getDateOfBirth());
                        etGender.setText(pat.getGender());
                        etEmergencyContact.setText(pat.getEmergencyContact());
                        etInsurance.setText(pat.getInsuranceInfo());
                        etMedicalHistory.setText(pat.getMedicalHistory());
                    }
                }

                @Override
                public void onFailure(Call<PatientResponse> call, Throwable t) {
                    Toast.makeText(EditProfileActivity.this, "Failed to load patient details", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    private void setupListeners() {
        btnSaveChanges.setOnClickListener(v -> attemptSaveChanges());
    }

    private void attemptSaveChanges() {
        String fullName = etFullName.getText().toString().trim();
        String email = etEmail.getText().toString().trim();
        String newPassword = etNewPassword.getText().toString().trim();
        String confirmPassword = etConfirmPassword.getText().toString().trim();
        String currentPassword = etCurrentPassword.getText().toString().trim();

        if (TextUtils.isEmpty(fullName)) {
            etFullName.setError("Full name is required");
            return;
        }

        if (TextUtils.isEmpty(email)) {
            etEmail.setError("Email is required");
            return;
        }

        if (TextUtils.isEmpty(currentPassword)) {
            etCurrentPassword.setError("Current password is required to save changes");
            return;
        }

        if (!TextUtils.isEmpty(newPassword)) {
            if (!newPassword.equals(confirmPassword)) {
                etConfirmPassword.setError("Passwords do not match");
                return;
            }
            if (newPassword.length() < 6) {
                etNewPassword.setError("Password must be at least 6 characters");
                return;
            }
        }

        verifyPasswordAndSave(fullName, email, currentPassword, newPassword);
    }

    private void verifyPasswordAndSave(String fullName, String email, String currentPassword, String newPassword) {
        btnSaveChanges.setEnabled(false);
        btnSaveChanges.setText("Verifying...");

        // Use the current session email for verification, in case the user changed the email field
        String sessionEmail = sessionManager.getEmail();

        LoginRequest loginRequest = new LoginRequest(sessionEmail, currentPassword);
        apiService.login(loginRequest).enqueue(new Callback<LoginResponse>() {
            @Override
            public void onResponse(Call<LoginResponse> call, Response<LoginResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    // Password verified
                    saveProfileChanges(fullName, email, TextUtils.isEmpty(newPassword) ? currentPassword : newPassword);
                } else {
                    btnSaveChanges.setEnabled(true);
                    btnSaveChanges.setText("Save Changes");
                    Toast.makeText(EditProfileActivity.this, "Incorrect current password", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<LoginResponse> call, Throwable t) {
                btnSaveChanges.setEnabled(true);
                btnSaveChanges.setText("Save Changes");
                Toast.makeText(EditProfileActivity.this, "Network error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void saveProfileChanges(String fullName, String email, String passwordToSave) {
        btnSaveChanges.setText("Saving...");

        UserCreate userUpdate = new UserCreate(fullName, email, passwordToSave, sessionManager.getRole());
        
        apiService.updateUser(sessionManager.getUserId(), userUpdate).enqueue(new Callback<MessageResponse>() {
            @Override
            public void onResponse(Call<MessageResponse> call, Response<MessageResponse> response) {
                if (response.isSuccessful()) {
                    if ("doctor".equals(userRole)) {
                        updateDoctorInfo(fullName, email);
                    } else if ("patient".equals(userRole)) {
                        updatePatientInfo(fullName, email);
                    } else {
                        finishUpdate(fullName, email);
                    }
                } else {
                    btnSaveChanges.setEnabled(true);
                    btnSaveChanges.setText("Save Changes");
                    Toast.makeText(EditProfileActivity.this, "Failed to update profile", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<MessageResponse> call, Throwable t) {
                btnSaveChanges.setEnabled(true);
                btnSaveChanges.setText("Save Changes");
                Toast.makeText(EditProfileActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
    
    private void updateDoctorInfo(String fullName, String email) {
        String phone = etPhone.getText().toString().trim();
        String address = etAddress.getText().toString().trim();
        String bio = etBio.getText().toString().trim();
        String experienceStr = etExperience.getText().toString().trim();
        Integer experience = null;
        
        if (!experienceStr.isEmpty()) {
            try {
                experience = Integer.parseInt(experienceStr);
            } catch (NumberFormatException e) {
                // Ignore
            }
        }
        
        DoctorCreate doctorUpdate = new DoctorCreate(sessionManager.getUserId(), bio, phone, address, experience);
        apiService.updateDoctor(sessionManager.getDoctorId(), doctorUpdate).enqueue(new Callback<MessageResponse>() {
            @Override
            public void onResponse(Call<MessageResponse> call, Response<MessageResponse> response) {
                if (response.isSuccessful()) {
                    finishUpdate(fullName, email);
                } else {
                    Toast.makeText(EditProfileActivity.this, "User updated but Doctor info failed", Toast.LENGTH_SHORT).show();
                    finishUpdate(fullName, email); // Still finish as user info updated
                }
            }

            @Override
            public void onFailure(Call<MessageResponse> call, Throwable t) {
                Toast.makeText(EditProfileActivity.this, "User updated but Doctor info error", Toast.LENGTH_SHORT).show();
                finishUpdate(fullName, email);
            }
        });
    }
    
    private void updatePatientInfo(String fullName, String email) {
        String phone = etPhone.getText().toString().trim();
        String address = etAddress.getText().toString().trim();
        String dob = etDob.getText().toString().trim();
        String gender = etGender.getText().toString().trim();
        String emergency = etEmergencyContact.getText().toString().trim();
        String insurance = etInsurance.getText().toString().trim();
        String medical = etMedicalHistory.getText().toString().trim();
        
        PatientCreate patientUpdate = new PatientCreate(sessionManager.getUserId(), dob, gender, address, phone, emergency, medical, insurance);
        apiService.updatePatient(sessionManager.getPatientId(), patientUpdate).enqueue(new Callback<MessageResponse>() {
            @Override
            public void onResponse(Call<MessageResponse> call, Response<MessageResponse> response) {
                if (response.isSuccessful()) {
                    finishUpdate(fullName, email);
                } else {
                    Toast.makeText(EditProfileActivity.this, "User updated but Patient info failed", Toast.LENGTH_SHORT).show();
                    finishUpdate(fullName, email);
                }
            }

            @Override
            public void onFailure(Call<MessageResponse> call, Throwable t) {
                Toast.makeText(EditProfileActivity.this, "User updated but Patient info error", Toast.LENGTH_SHORT).show();
                finishUpdate(fullName, email);
            }
        });
    }
    
    private void finishUpdate(String fullName, String email) {
        Toast.makeText(EditProfileActivity.this, "Profile updated successfully", Toast.LENGTH_SHORT).show();
        
        // Update Session
        sessionManager.createLoginSession(
                sessionManager.getUserId(),
                fullName,
                sessionManager.getRole(),
                email,
                sessionManager.getPatientId(),
                sessionManager.getDoctorId()
        );

        finish();
    }
}
