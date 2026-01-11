package com.example.hospitalmanagement;

import android.os.Bundle;
import android.widget.Toast;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.hospitalmanagement.adapter.AppointmentAdapter;
import com.example.hospitalmanagement.api.ApiService;
import com.example.hospitalmanagement.api.RetrofitClient;
import com.example.hospitalmanagement.model.Appointment;
import com.example.hospitalmanagement.model.AppointmentResponse;
import com.google.android.material.chip.ChipGroup;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AdminAppointmentsActivity extends BaseActivity implements AppointmentAdapter.OnAppointmentClickListener {

    private RecyclerView appointmentsRecyclerView;
    private AppointmentAdapter appointmentAdapter;
    private List<Appointment> allAppointments = new ArrayList<>();
    private java.util.Map<String, AppointmentResponse> appointmentResponseMap = new java.util.HashMap<>();
    private ApiService apiService;
    private ChipGroup chipGroupFilter;

    private int filterDoctorId = -1;
    private int filterPatientId = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_appointments);

        // Get Filters from Intent
        filterDoctorId = getIntent().getIntExtra("doctor_id", -1);
        filterPatientId = getIntent().getIntExtra("patient_id", -1);
        String initialFilter = getIntent().getStringExtra("filter");

        apiService = RetrofitClient.getApiService();
        initializeViews();

        if ("today".equals(initialFilter)) {
            chipGroupFilter.check(R.id.chip_today);
        } else {
            chipGroupFilter.check(R.id.chip_all);
        }
        
        loadAppointments();
    }

    private void initializeViews() {
        findViewById(R.id.btn_back).setOnClickListener(v -> finish());

        appointmentsRecyclerView = findViewById(R.id.appointments_recyclerview);
        appointmentsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        
        chipGroupFilter = findViewById(R.id.chip_group_filter);
        chipGroupFilter.setOnCheckedChangeListener((group, checkedId) -> {
            filterList(checkedId);
        });
    }

    private void loadAppointments() {
        Integer docId = filterDoctorId != -1 ? filterDoctorId : null;
        Integer patId = filterPatientId != -1 ? filterPatientId : null;

        apiService.getAllAppointments(docId, patId, null).enqueue(new Callback<List<AppointmentResponse>>() {
            @Override
            public void onResponse(Call<List<AppointmentResponse>> call, Response<List<AppointmentResponse>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    mapResponseToModel(response.body());
                    filterList(chipGroupFilter.getCheckedChipId());
                } else {
                    Toast.makeText(AdminAppointmentsActivity.this, "Failed to load appointments", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<AppointmentResponse>> call, Throwable t) {
                Toast.makeText(AdminAppointmentsActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void mapResponseToModel(List<AppointmentResponse> responses) {
        allAppointments.clear();
        appointmentResponseMap.clear();
        for (AppointmentResponse res : responses) {
            String id = String.valueOf(res.getAppointmentId());
            appointmentResponseMap.put(id, res);
            allAppointments.add(new Appointment(
                    id,
                    res.getDoctorName() != null ? "Dr. " + res.getDoctorName() : "Doctor",
                    res.getScheduledDate() + " " + res.getScheduledTime(),
                    res.getStatus(),
                    "Checkup", // Reason placeholder if not available
                    "", // Room number placeholder
                    R.drawable.ic_patient_avatar
            ));
        }
    }

    private void filterList(int checkedId) {
        List<Appointment> filtered = new ArrayList<>();
        
        java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("yyyy-MM-dd", java.util.Locale.getDefault());
        java.util.Date now = new java.util.Date();
        String todayDate = sdf.format(now);
        
        // Calculate last month
        java.util.Calendar cal = java.util.Calendar.getInstance();
        cal.setTime(now);
        cal.add(java.util.Calendar.MONTH, -1);
        int lastMonth = cal.get(java.util.Calendar.MONTH);
        int lastMonthYear = cal.get(java.util.Calendar.YEAR);

        for (Appointment appt : allAppointments) {
            if (checkedId == R.id.chip_all) {
                filtered.add(appt);
            } else if (checkedId == R.id.chip_today) {
                if (appt.getTime().startsWith(todayDate)) {
                    filtered.add(appt);
                }
            } else if (checkedId == R.id.chip_last_month) {
                 try {
                     String dateStr = appt.getTime().split(" ")[0];
                     java.util.Date date = sdf.parse(dateStr);
                     if (date != null) {
                         cal.setTime(date);
                         if (cal.get(java.util.Calendar.MONTH) == lastMonth && 
                             cal.get(java.util.Calendar.YEAR) == lastMonthYear) {
                             filtered.add(appt);
                         }
                     }
                 } catch (Exception e) {
                     e.printStackTrace();
                 }
            }
        }
        
        appointmentAdapter = new AppointmentAdapter(filtered, this, false, this);
        appointmentsRecyclerView.setAdapter(appointmentAdapter);
    }

    @Override
    public void onAppointmentClick(Appointment appointment) {
        String[] options = {"View Details", "Update Status"};

        new MaterialAlertDialogBuilder(this)
                .setTitle("Select Action")
                .setItems(options, (dialog, which) -> {
                    if (which == 0) {
                        // View Details
                        android.content.Intent intent = new android.content.Intent(this, AppointmentDetailActivity.class);
                        intent.putExtra("appointment_id", Integer.parseInt(appointment.getId()));
                        startActivity(intent);
                    } else {
                        // Update Status
                        showUpdateStatusDialog(appointment);
                    }
                })
                .show();
    }

    private void showUpdateStatusDialog(Appointment appointment) {
        String[] statuses = {"Pending", "Confirmed", "Completed", "Cancelled"};

        new MaterialAlertDialogBuilder(this)
                .setTitle("Update Appointment Status")
                .setSingleChoiceItems(statuses, -1, (dialog, which) -> {
                    String selectedStatus = statuses[which];
                    updateAppointmentStatus(appointment.getId(), selectedStatus);
                    dialog.dismiss();
                })
                .show();
    }
    
    private void updateAppointmentStatus(String appointmentId, String status) {
        try {
            int id = Integer.parseInt(appointmentId);
            
            AppointmentResponse original = appointmentResponseMap.get(appointmentId);
            
            if (original != null) {
                String date = original.getScheduledDate();
                String time = original.getScheduledTime();
                    
                com.example.hospitalmanagement.model.AppointmentUpdate update = 
                    new com.example.hospitalmanagement.model.AppointmentUpdate(date, time, status);
                        
                apiService.updateAppointment(id, update).enqueue(new Callback<com.example.hospitalmanagement.model.MessageResponse>() {
                    @Override
                    public void onResponse(Call<com.example.hospitalmanagement.model.MessageResponse> call, Response<com.example.hospitalmanagement.model.MessageResponse> response) {
                        if (response.isSuccessful()) {
                            Toast.makeText(AdminAppointmentsActivity.this, "Status updated to " + status, Toast.LENGTH_SHORT).show();
                            loadAppointments();
                        } else {
                            Toast.makeText(AdminAppointmentsActivity.this, "Failed to update status", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<com.example.hospitalmanagement.model.MessageResponse> call, Throwable t) {
                         Toast.makeText(AdminAppointmentsActivity.this, "Network error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
            } else {
                Toast.makeText(this, "Error: Could not retrieve appointment details", Toast.LENGTH_SHORT).show();
            }
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }
    }
}