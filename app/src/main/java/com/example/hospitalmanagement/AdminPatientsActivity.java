package com.example.hospitalmanagement;

import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.hospitalmanagement.adapter.PatientListAdapter;
import com.example.hospitalmanagement.api.ApiService;
import com.example.hospitalmanagement.api.RetrofitClient;
import com.example.hospitalmanagement.model.PatientResponse;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AdminPatientsActivity extends BaseActivity {

    private RecyclerView patientsRecyclerView;
    private PatientListAdapter patientAdapter;
    private List<PatientResponse> patientList = new ArrayList<>();
    private ApiService apiService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_patients);

        apiService = RetrofitClient.getApiService();
        initializeViews();
        loadPatients();
    }

    private void initializeViews() {
        findViewById(R.id.btn_back).setOnClickListener(v -> finish());

        patientsRecyclerView = findViewById(R.id.patients_recyclerview);
        patientsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        
        patientAdapter = new PatientListAdapter(patientList, new PatientListAdapter.OnPatientClickListener() {
            @Override
            public void onPatientClick(PatientResponse patient) {
                onViewAppointments(patient);
            }

            @Override
            public void onViewAppointments(PatientResponse patient) {
                android.content.Intent intent = new android.content.Intent(AdminPatientsActivity.this, AdminAppointmentsActivity.class);
                intent.putExtra("patient_id", patient.getPatientId());
                startActivity(intent);
            }

            @Override
            public void onEditPatient(PatientResponse patient) {
                android.content.Intent intent = new android.content.Intent(AdminPatientsActivity.this, AdminEditPatientActivity.class);
                intent.putExtra("patient_id", patient.getPatientId());
                startActivity(intent);
            }
        });
        patientsRecyclerView.setAdapter(patientAdapter);
    }

    private void loadPatients() {
        apiService.getAllPatients().enqueue(new Callback<List<PatientResponse>>() {
            @Override
            public void onResponse(Call<List<PatientResponse>> call, Response<List<PatientResponse>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    patientList = response.body();
                    patientAdapter.updateList(patientList);
                } else {
                    Toast.makeText(AdminPatientsActivity.this, "Failed to load patients", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<PatientResponse>> call, Throwable t) {
                Toast.makeText(AdminPatientsActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}