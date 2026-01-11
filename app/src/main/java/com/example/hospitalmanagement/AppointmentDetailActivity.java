package com.example.hospitalmanagement;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.cardview.widget.CardView;

import com.example.hospitalmanagement.api.ApiService;
import com.example.hospitalmanagement.api.RetrofitClient;
import com.example.hospitalmanagement.model.AppointmentResponse;
import com.example.hospitalmanagement.model.AppointmentUpdate;
import com.example.hospitalmanagement.model.MessageResponse;
import com.example.hospitalmanagement.model.PrescriptionCreate;
import com.example.hospitalmanagement.model.PrescriptionResponse;
import com.example.hospitalmanagement.model.PrescriptionUpdate;
import com.example.hospitalmanagement.utils.SessionManager;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.textfield.TextInputEditText;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AppointmentDetailActivity extends BaseActivity {

    private TextView textDateTime, textStatus, textDoctorName, textPatientName, textPrescriptionNotes;
    private CardView cardPrescription;
    private MaterialButton btnAddPrescription, btnUpdateStatus;
    
    private ApiService apiService;
    private SessionManager sessionManager;
    private int appointmentId;
    private AppointmentResponse appointmentDetails;
    
    private int currentPrescriptionId = -1;
    private String currentPrescriptionNotes = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_appointment_detail);

        appointmentId = getIntent().getIntExtra("appointment_id", -1);
        if (appointmentId == -1) {
            Toast.makeText(this, "Invalid Appointment ID", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        sessionManager = new SessionManager(this);
        apiService = RetrofitClient.getApiService();

        initializeViews();
        loadAppointmentDetails();
    }

    private void initializeViews() {
        textDateTime = findViewById(R.id.text_date_time);
        textStatus = findViewById(R.id.text_status);
        textDoctorName = findViewById(R.id.text_doctor_name);
        textPatientName = findViewById(R.id.text_patient_name);
        textPrescriptionNotes = findViewById(R.id.text_prescription_notes);
        cardPrescription = findViewById(R.id.card_prescription);
        btnAddPrescription = findViewById(R.id.btn_add_prescription);
        btnUpdateStatus = findViewById(R.id.btn_update_status);

        findViewById(R.id.btn_back).setOnClickListener(v -> finish());
        
        btnAddPrescription.setOnClickListener(v -> showAddPrescriptionDialog());
        btnUpdateStatus.setOnClickListener(v -> showUpdateStatusDialog());
    }

    private void loadAppointmentDetails() {
        apiService.getAppointmentById(appointmentId).enqueue(new Callback<AppointmentResponse>() {
            @Override
            public void onResponse(Call<AppointmentResponse> call, Response<AppointmentResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    appointmentDetails = response.body();
                    displayDetails(appointmentDetails);
                    loadPrescription(appointmentDetails.getPatientId());
                } else {
                    Toast.makeText(AppointmentDetailActivity.this, "Failed to load details", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<AppointmentResponse> call, Throwable t) {
                Toast.makeText(AppointmentDetailActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void displayDetails(AppointmentResponse appt) {
        textDateTime.setText("Date: " + appt.getScheduledDate() + "  Time: " + appt.getScheduledTime());
        textStatus.setText("Status: " + appt.getStatus());
        textDoctorName.setText("Doctor: " + appt.getDoctorName());
        textPatientName.setText("Patient: " + appt.getPatientName());

        // Show Add Prescription button only if user is Doctor/Admin and appointment is confirmed or completed
        String role = sessionManager.getRole();
        if ("doctor".equals(role) || "admin".equals(role)) {
            btnAddPrescription.setVisibility(View.VISIBLE);
        }
        
        // Show Update Status button for Admin
        if ("admin".equals(role)) {
            btnUpdateStatus.setVisibility(View.VISIBLE);
        }
    }

    private void loadPrescription(int patientId) {
        apiService.getPatientPrescriptions(patientId).enqueue(new Callback<List<PrescriptionResponse>>() {
            @Override
            public void onResponse(Call<List<PrescriptionResponse>> call, Response<List<PrescriptionResponse>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    boolean found = false;
                    for (PrescriptionResponse p : response.body()) {
                        if (p.getAppointmentId() == appointmentId) {
                            showPrescription(p);
                            found = true;
                            break;
                        }
                    }
                    if (!found) {
                        cardPrescription.setVisibility(View.GONE);
                        currentPrescriptionId = -1;
                        currentPrescriptionNotes = "";
                    }
                }
            }

            @Override
            public void onFailure(Call<List<PrescriptionResponse>> call, Throwable t) {
                // Ignore error, just don't show prescription
            }
        });
    }

    private void showPrescription(PrescriptionResponse prescription) {
        cardPrescription.setVisibility(View.VISIBLE);
        currentPrescriptionId = prescription.getPrescriptionId();
        currentPrescriptionNotes = prescription.getNotes();
        
        StringBuilder sb = new StringBuilder();
        sb.append("Notes: ").append(prescription.getNotes()).append("\n\n");
        
        if (prescription.getItems() != null && !prescription.getItems().isEmpty()) {
            sb.append("Medicines:\n");
            int count = 1;
            for (com.example.hospitalmanagement.model.PrescriptionItemResponse item : prescription.getItems()) {
                sb.append(count).append(". ").append(item.getMedicationName());
                if (item.getDosage() != null && !item.getDosage().isEmpty()) {
                    sb.append(" - ").append(item.getDosage());
                }
                if (item.getFrequency() != null && !item.getFrequency().isEmpty()) {
                    sb.append(" (").append(item.getFrequency()).append(")");
                }
                if (item.getDuration() != null && !item.getDuration().isEmpty()) {
                    sb.append(" for ").append(item.getDuration());
                }
                if (item.getInstructions() != null && !item.getInstructions().isEmpty()) {
                    sb.append("\n   Note: ").append(item.getInstructions());
                }
                sb.append("\n");
                count++;
            }
        }
        
        textPrescriptionNotes.setText(sb.toString());
        
        // If prescription exists, change "Add" button to "Edit"
        String role = sessionManager.getRole();
        if ("doctor".equals(role) || "admin".equals(role)) {
            btnAddPrescription.setText("Edit Prescription");
            btnAddPrescription.setVisibility(View.VISIBLE);
        } else {
            btnAddPrescription.setVisibility(View.GONE);
        }
    }

    private void showAddPrescriptionDialog() {
        if (appointmentDetails == null) return;

        View view = getLayoutInflater().inflate(R.layout.dialog_add_prescription, null);
        TextInputEditText inputNotes = view.findViewById(R.id.input_notes);
        
        // Pre-fill if editing
        if (currentPrescriptionId != -1) {
            inputNotes.setText(currentPrescriptionNotes);
        }

        new MaterialAlertDialogBuilder(this)
                .setTitle(currentPrescriptionId != -1 ? "Edit Prescription" : "Add Prescription")
                .setView(view)
                .setPositiveButton("Save", (dialog, which) -> {
                    String notes = inputNotes.getText().toString().trim();
                    if (!notes.isEmpty()) {
                        if (currentPrescriptionId != -1) {
                            updatePrescription(notes);
                        } else {
                            savePrescription(notes);
                        }
                    } else {
                        Toast.makeText(this, "Notes cannot be empty", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void savePrescription(String notes) {
        PrescriptionCreate create = new PrescriptionCreate(
                appointmentDetails.getAppointmentId(),
                appointmentDetails.getPatientId(),
                appointmentDetails.getDoctorId(),
                notes
        );

        apiService.createPrescription(create).enqueue(new Callback<PrescriptionResponse>() {
            @Override
            public void onResponse(Call<PrescriptionResponse> call, Response<PrescriptionResponse> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(AppointmentDetailActivity.this, "Prescription added", Toast.LENGTH_SHORT).show();
                    loadPrescription(appointmentDetails.getPatientId()); // Reload to show
                } else {
                    Toast.makeText(AppointmentDetailActivity.this, "Failed to add prescription", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<PrescriptionResponse> call, Throwable t) {
                Toast.makeText(AppointmentDetailActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void updatePrescription(String notes) {
        PrescriptionUpdate update = new PrescriptionUpdate(notes);

        apiService.updatePrescription(currentPrescriptionId, update).enqueue(new Callback<MessageResponse>() {
            @Override
            public void onResponse(Call<MessageResponse> call, Response<MessageResponse> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(AppointmentDetailActivity.this, "Prescription updated", Toast.LENGTH_SHORT).show();
                    loadPrescription(appointmentDetails.getPatientId()); // Reload to show
                } else {
                    Toast.makeText(AppointmentDetailActivity.this, "Failed to update prescription", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<MessageResponse> call, Throwable t) {
                Toast.makeText(AppointmentDetailActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void showUpdateStatusDialog() {
        if (appointmentDetails == null) return;

        String[] statuses = {"Pending", "Confirmed", "Completed", "Cancelled"};
        int checkedItem = -1;
        
        // Find current status index
        for (int i = 0; i < statuses.length; i++) {
            if (statuses[i].equalsIgnoreCase(appointmentDetails.getStatus())) {
                checkedItem = i;
                break;
            }
        }

        new MaterialAlertDialogBuilder(this)
                .setTitle("Update Status")
                .setSingleChoiceItems(statuses, checkedItem, (dialog, which) -> {
                     updateAppointmentStatus(statuses[which]);
                     dialog.dismiss();
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void updateAppointmentStatus(String newStatus) {
        AppointmentUpdate update = new AppointmentUpdate(
                appointmentDetails.getScheduledDate(),
                appointmentDetails.getScheduledTime(),
                newStatus
        );

        apiService.updateAppointment(appointmentId, update).enqueue(new Callback<MessageResponse>() {
            @Override
            public void onResponse(Call<MessageResponse> call, Response<MessageResponse> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(AppointmentDetailActivity.this, "Status updated", Toast.LENGTH_SHORT).show();
                    loadAppointmentDetails(); // Reload details
                } else {
                    Toast.makeText(AppointmentDetailActivity.this, "Failed to update status", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<MessageResponse> call, Throwable t) {
                Toast.makeText(AppointmentDetailActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
