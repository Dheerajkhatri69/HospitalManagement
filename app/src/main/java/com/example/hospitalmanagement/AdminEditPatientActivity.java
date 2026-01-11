package com.example.hospitalmanagement;

import android.os.Bundle;
import android.widget.Toast;

import com.example.hospitalmanagement.api.ApiService;
import com.example.hospitalmanagement.api.RetrofitClient;
import com.example.hospitalmanagement.model.MessageResponse;
import com.example.hospitalmanagement.model.PatientCreate;
import com.example.hospitalmanagement.model.PatientResponse;
import com.example.hospitalmanagement.model.UserCreate;
import com.google.android.material.textfield.TextInputEditText;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AdminEditPatientActivity extends BaseActivity {

    private TextInputEditText editName, editEmail, editDob, editGender, editPhone, editAddress, editEmergencyContact, editInsurance, editMedicalHistory;
    private ApiService apiService;
    private int patientId;
    private int userId;
    private String currentRole = "patient";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_edit_patient);

        patientId = getIntent().getIntExtra("patient_id", -1);
        if (patientId == -1) {
            Toast.makeText(this, "Error: Invalid Patient ID", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        apiService = RetrofitClient.getApiService();
        initializeViews();
        loadPatientDetails();
    }

    private void initializeViews() {
        editName = findViewById(R.id.edit_name);
        editEmail = findViewById(R.id.edit_email);
        editDob = findViewById(R.id.edit_dob);
        editGender = findViewById(R.id.edit_gender);
        editPhone = findViewById(R.id.edit_phone);
        editAddress = findViewById(R.id.edit_address);
        editEmergencyContact = findViewById(R.id.edit_emergency_contact);
        editInsurance = findViewById(R.id.edit_insurance);
        editMedicalHistory = findViewById(R.id.edit_medical_history);

        findViewById(R.id.btn_back).setOnClickListener(v -> finish());
        findViewById(R.id.btn_save).setOnClickListener(v -> saveChanges());
    }

    private void loadPatientDetails() {
        apiService.getPatientById(patientId).enqueue(new Callback<PatientResponse>() {
            @Override
            public void onResponse(Call<PatientResponse> call, Response<PatientResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    populateFields(response.body());
                } else {
                    Toast.makeText(AdminEditPatientActivity.this, "Failed to load patient details", Toast.LENGTH_SHORT).show();
                    finish();
                }
            }

            @Override
            public void onFailure(Call<PatientResponse> call, Throwable t) {
                Toast.makeText(AdminEditPatientActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                finish();
            }
        });
    }

    private void populateFields(PatientResponse patient) {
        userId = patient.getUserId();
        editName.setText(patient.getFullName());
        editEmail.setText(patient.getEmail());
        editDob.setText(patient.getDateOfBirth());
        editGender.setText(patient.getGender());
        editPhone.setText(patient.getPhone());
        editAddress.setText(patient.getAddress());
        editEmergencyContact.setText(patient.getEmergencyContact());
        editInsurance.setText(patient.getInsuranceInfo());
        editMedicalHistory.setText(patient.getMedicalHistory());
    }

    private void saveChanges() {
        String name = editName.getText().toString().trim();
        String email = editEmail.getText().toString().trim();
        String dob = editDob.getText().toString().trim();
        String gender = editGender.getText().toString().trim();
        String phone = editPhone.getText().toString().trim();
        String address = editAddress.getText().toString().trim();
        String emergency = editEmergencyContact.getText().toString().trim();
        String insurance = editInsurance.getText().toString().trim();
        String medical = editMedicalHistory.getText().toString().trim();

        if (name.isEmpty() || email.isEmpty()) {
            Toast.makeText(this, "Name and Email are required", Toast.LENGTH_SHORT).show();
            return;
        }

        // Update User Info first
        UserCreate userUpdate = new UserCreate(name, email, null, currentRole);
        
        apiService.updateUser(userId, userUpdate).enqueue(new Callback<MessageResponse>() {
            @Override
            public void onResponse(Call<MessageResponse> call, Response<MessageResponse> response) {
                if (response.isSuccessful()) {
                    // If user update successful, update patient info
                    updatePatientInfo(dob, gender, address, phone, emergency, medical, insurance);
                } else {
                    Toast.makeText(AdminEditPatientActivity.this, "Failed to update user info", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<MessageResponse> call, Throwable t) {
                Toast.makeText(AdminEditPatientActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void updatePatientInfo(String dob, String gender, String address, String phone, String emergency, String medical, String insurance) {
        PatientCreate patientUpdate = new PatientCreate(userId, dob, gender, address, phone, emergency, medical, insurance);
        
        apiService.updatePatient(patientId, patientUpdate).enqueue(new Callback<MessageResponse>() {
            @Override
            public void onResponse(Call<MessageResponse> call, Response<MessageResponse> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(AdminEditPatientActivity.this, "Patient updated successfully", Toast.LENGTH_SHORT).show();
                    finish();
                } else {
                    Toast.makeText(AdminEditPatientActivity.this, "Failed to update patient info", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<MessageResponse> call, Throwable t) {
                Toast.makeText(AdminEditPatientActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}