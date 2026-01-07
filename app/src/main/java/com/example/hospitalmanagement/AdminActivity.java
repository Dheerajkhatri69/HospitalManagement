package com.example.hospitalmanagement;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.hospitalmanagement.adapter.AppointmentRequestAdapter;
import com.example.hospitalmanagement.api.ApiService;
import com.example.hospitalmanagement.api.RetrofitClient;
import com.example.hospitalmanagement.model.AppointmentCreate;
import com.example.hospitalmanagement.model.AppointmentRequestResponse;
import com.example.hospitalmanagement.model.AppointmentRequestUpdate;
import com.example.hospitalmanagement.model.AppointmentResponse;
import com.example.hospitalmanagement.model.DashboardStats;
import com.example.hospitalmanagement.model.DoctorResponse;
import com.example.hospitalmanagement.model.MessageResponse;
import com.example.hospitalmanagement.utils.SessionManager;
import com.google.android.material.navigation.NavigationView;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AdminActivity extends BaseActivity implements AppointmentRequestAdapter.OnRequestActionListener {

    private SessionManager sessionManager;
    private ApiService apiService;

    // UI Components
    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private ImageView menuIcon, notificationIcon;
    private TextView adminName, notificationBadge;
    private TextView totalPatients, totalDoctors, pendingRequests, todayAppointments;
    private RecyclerView pendingRequestsRecyclerView;
    private LinearLayout emptyState;

    // Data
    private AppointmentRequestAdapter requestAdapter;
    private List<AppointmentRequestResponse> pendingRequestsList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin);

        sessionManager = new SessionManager(this);
        apiService = RetrofitClient.getApiService();

        // Redirect if not logged in
        if (!sessionManager.isLoggedIn()) {
            startActivity(new Intent(this, LoginActivity.class));
            finish();
            return;
        }

        initializeViews();
        setupNavigation();
        setupRecyclerView();
        loadDashboardData();
        loadPendingRequests();
    }

    private void initializeViews() {
        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_view);
        menuIcon = findViewById(R.id.menu_icon);
        notificationIcon = findViewById(R.id.notification_icon);
        adminName = findViewById(R.id.admin_name);
        notificationBadge = findViewById(R.id.notification_badge);

        // Dashboard stats
        totalPatients = findViewById(R.id.total_patients);
        totalDoctors = findViewById(R.id.total_doctors);
        pendingRequests = findViewById(R.id.pending_requests);
        todayAppointments = findViewById(R.id.today_appointments);

        // RecyclerView
        pendingRequestsRecyclerView = findViewById(R.id.pending_requests_recyclerview);
        emptyState = findViewById(R.id.empty_state);

        // Set admin name
        adminName.setText(sessionManager.getFullName());
    }

    private void setupNavigation() {
        // Hamburger menu
        menuIcon.setOnClickListener(v -> drawerLayout.openDrawer(androidx.core.view.GravityCompat.START));

        // Notification icon
        notificationIcon.setOnClickListener(v -> Toast.makeText(this, "Notifications", Toast.LENGTH_SHORT).show());

        // Setup Navigation Drawer
        View headerView = navigationView.getHeaderView(0);
        TextView navName = headerView.findViewById(R.id.nav_header_name);
        TextView navEmail = headerView.findViewById(R.id.nav_header_email);

        navName.setText(sessionManager.getFullName());
        navEmail.setText(sessionManager.getEmail());

        navigationView.setNavigationItemSelectedListener(item -> {
            int id = item.getItemId();

            if (id == R.id.nav_logout) {
                logout();
            } else {
                Toast.makeText(this, "Clicked: " + item.getTitle(), Toast.LENGTH_SHORT).show();
            }

            drawerLayout.closeDrawer(androidx.core.view.GravityCompat.START);
            return true;
        });

        // View all requests button
        TextView viewAllRequests = findViewById(R.id.view_all_requests);
        viewAllRequests.setOnClickListener(v -> Toast.makeText(this, "View All Requests", Toast.LENGTH_SHORT).show());
    }

    private void setupRecyclerView() {
        requestAdapter = new AppointmentRequestAdapter(pendingRequestsList, this);
        pendingRequestsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        pendingRequestsRecyclerView.setAdapter(requestAdapter);
        pendingRequestsRecyclerView.setHasFixedSize(true);
        pendingRequestsRecyclerView.setNestedScrollingEnabled(false);
    }

    private void loadDashboardData() {
        apiService.getDashboardStats().enqueue(new Callback<DashboardStats>() {
            @Override
            public void onResponse(Call<DashboardStats> call, Response<DashboardStats> response) {
                if (response.isSuccessful() && response.body() != null) {
                    DashboardStats stats = response.body();
                    updateDashboardUI(stats);
                }
            }

            @Override
            public void onFailure(Call<DashboardStats> call, Throwable t) {
                Toast.makeText(AdminActivity.this, "Failed to load dashboard: " + t.getMessage(),
                        Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void updateDashboardUI(DashboardStats stats) {
        totalPatients.setText(String.valueOf(stats.getTotalPatients()));
        totalDoctors.setText(String.valueOf(stats.getTotalDoctors()));
        pendingRequests.setText(String.valueOf(stats.getPendingRequests()));
        todayAppointments.setText(String.valueOf(stats.getTodayAppointments()));

        // Update notification badge
        if (stats.getPendingRequests() > 0) {
            notificationBadge.setText(String.valueOf(stats.getPendingRequests()));
            notificationBadge.setVisibility(View.VISIBLE);
        } else {
            notificationBadge.setVisibility(View.GONE);
        }
    }

    private void loadPendingRequests() {
        apiService.getAllAppointmentRequests("pending").enqueue(new Callback<List<AppointmentRequestResponse>>() {
            @Override
            public void onResponse(Call<List<AppointmentRequestResponse>> call,
                    Response<List<AppointmentRequestResponse>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    pendingRequestsList = response.body();
                    updateRequestsList();
                }
            }

            @Override
            public void onFailure(Call<List<AppointmentRequestResponse>> call, Throwable t) {
                Toast.makeText(AdminActivity.this, "Failed to load requests: " + t.getMessage(),
                        Toast.LENGTH_SHORT).show();
                showEmptyState();
            }
        });
    }

    private void updateRequestsList() {
        if (pendingRequestsList.isEmpty()) {
            showEmptyState();
        } else {
            emptyState.setVisibility(View.GONE);
            pendingRequestsRecyclerView.setVisibility(View.VISIBLE);
            requestAdapter.updateList(pendingRequestsList);
        }
    }

    private void showEmptyState() {
        emptyState.setVisibility(View.VISIBLE);
        pendingRequestsRecyclerView.setVisibility(View.GONE);
    }

    @Override
    public void onApprove(AppointmentRequestResponse request) {
        if (request.getDoctorId() == null || request.getDoctorId() == 0) {
            // If no doctor selected in request, let admin select one
            fetchDoctorsAndApprove(request);
        } else {
            showApprovalDialog(request, request.getDoctorId(), request.getDoctorName());
        }
    }

    private void fetchDoctorsAndApprove(AppointmentRequestResponse request) {
        apiService.getAllDoctors().enqueue(new Callback<List<DoctorResponse>>() {
            @Override
            public void onResponse(Call<List<DoctorResponse>> call, Response<List<DoctorResponse>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<DoctorResponse> allDoctors = response.body();
                    DoctorResponse matchingDoctor = null;

                    // Try to find a doctor with matching specialization
                    if (request.getSpecialization() != null && !request.getSpecialization().isEmpty()) {
                        for (DoctorResponse doc : allDoctors) {
                            if (doc.getSpecializations() != null &&
                                    doc.getSpecializations().contains(request.getSpecialization())) {
                                matchingDoctor = doc;
                                break; // Found a match, auto-assign the first one
                            }
                        }
                    }

                    if (matchingDoctor != null) {
                        // Auto-assign found doctor and show confirmation
                        Toast.makeText(AdminActivity.this, "Auto-assigned " + matchingDoctor.getFullName() +
                                " (" + request.getSpecialization() + ")", Toast.LENGTH_SHORT).show();
                        showApprovalDialog(request, matchingDoctor.getDoctorId(), matchingDoctor.getFullName());
                    } else {
                        // No matching doctor found, fallback to manual selection
                        Toast.makeText(AdminActivity.this, "No doctor found for " + request.getSpecialization() +
                                ". Please select manually.", Toast.LENGTH_SHORT).show();
                        showDoctorSelectionDialog(request, allDoctors);
                    }
                } else {
                    Toast.makeText(AdminActivity.this, "Failed to load doctors list", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<DoctorResponse>> call, Throwable t) {
                Toast.makeText(AdminActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void showDoctorSelectionDialog(AppointmentRequestResponse request, List<DoctorResponse> doctors) {
        String[] doctorNames = new String[doctors.size()];
        for (int i = 0; i < doctors.size(); i++) {
            doctorNames[i] = doctors.get(i).getFullName() + " (" +
                    (doctors.get(i).getSpecializations() != null && !doctors.get(i).getSpecializations().isEmpty()
                            ? doctors.get(i).getSpecializations().get(0)
                            : "General")
                    + ")";
        }

        final int[] selectedIndex = { -1 };

        new androidx.appcompat.app.AlertDialog.Builder(this)
                .setTitle("Select Doctor for Appointment")
                .setSingleChoiceItems(doctorNames, -1, (dialog, which) -> selectedIndex[0] = which)
                .setPositiveButton("Select", (dialog, which) -> {
                    if (selectedIndex[0] >= 0) {
                        DoctorResponse selectedDoctor = doctors.get(selectedIndex[0]);
                        showApprovalDialog(request, selectedDoctor.getDoctorId(), selectedDoctor.getFullName());
                    } else {
                        Toast.makeText(AdminActivity.this, "Please select a doctor", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void showApprovalDialog(AppointmentRequestResponse request, int doctorId, String doctorName) {
        new androidx.appcompat.app.AlertDialog.Builder(this)
                .setTitle("Approve Appointment")
                .setMessage("Create appointment for " + request.getPatientName() + "\n" +
                        "with Dr. " + doctorName + "\n" +
                        "on " + request.getPreferredDate() + " at " + request.getPreferredTime() + "?")
                .setPositiveButton("Confirm", (dialog, which) -> createAppointment(request, doctorId))
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void createAppointment(AppointmentRequestResponse request, int doctorId) {
        String formattedDate = formatDateForApi(request.getPreferredDate());
        String formattedTime = formatTimeForApi(request.getPreferredTime());

        // Create appointment object
        AppointmentCreate appointment = new AppointmentCreate(
                request.getRequestId(),
                request.getPatientId(),
                doctorId,
                formattedDate,
                formattedTime,
                "scheduled" // Changed from "Confirmed" to match DB enum
        );

        // Debug Toast
        // Toast.makeText(this, "Creating: " + formattedDate + " " + formattedTime,
        // Toast.LENGTH_SHORT).show();

        // Call API
        apiService.createAppointment(appointment).enqueue(new Callback<AppointmentResponse>() {
            @Override
            public void onResponse(Call<AppointmentResponse> call, Response<AppointmentResponse> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(AdminActivity.this, "Appointment created successfully", Toast.LENGTH_SHORT).show();
                    loadPendingRequests();
                    loadDashboardData();
                } else {
                    try {
                        String errorBody = response.errorBody() != null ? response.errorBody().string()
                                : "Unknown error";
                        Toast.makeText(AdminActivity.this, "Failed: " + response.code() + " - " + errorBody,
                                Toast.LENGTH_LONG).show();
                    } catch (Exception e) {
                        Toast.makeText(AdminActivity.this, "Failed: " + response.code(), Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onFailure(Call<AppointmentResponse> call, Throwable t) {
                Toast.makeText(AdminActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private String formatDateForApi(String dateStr) {
        if (dateStr == null)
            return null;
        try {
            // Check if it's already in yyyy-MM-dd format
            if (dateStr.matches("\\d{4}-\\d{2}-\\d{2}")) {
                return dateStr;
            }
            // Handle ISO format (e.g. 2024-01-01T00:00:00)
            if (dateStr.contains("T")) {
                return dateStr.split("T")[0];
            }
            return dateStr;
        } catch (Exception e) {
            return dateStr;
        }
    }

    private String formatTimeForApi(String timeStr) {
        if (timeStr == null)
            return null;
        try {
            // If format is HH:mm, append :00
            if (timeStr.matches("\\d{2}:\\d{2}")) {
                return timeStr + ":00";
            }
            // If already HH:mm:ss, return as is
            if (timeStr.matches("\\d{2}:\\d{2}:\\d{2}")) {
                return timeStr;
            }
            return timeStr;
        } catch (Exception e) {
            return timeStr;
        }
    }

    @Override
    public void onReject(AppointmentRequestResponse request) {
        new androidx.appcompat.app.AlertDialog.Builder(this)
                .setTitle("Reject Request")
                .setMessage("Are you sure you want to reject this request?")
                .setPositiveButton("Reject", (dialog, which) -> updateRequestStatus(request.getRequestId(), "rejected"))
                .setNegativeButton("Cancel", null)
                .show();
    }

    @Override
    public void onRequestClick(AppointmentRequestResponse request) {
        Toast.makeText(this, "Request details for " + request.getPatientName(),
                Toast.LENGTH_SHORT).show();
    }

    private void updateRequestStatus(int requestId, String status) {
        AppointmentRequestUpdate update = new AppointmentRequestUpdate(status);

        apiService.updateAppointmentRequest(requestId, update).enqueue(new Callback<MessageResponse>() {
            @Override
            public void onResponse(Call<MessageResponse> call, Response<MessageResponse> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(AdminActivity.this, "Request " + status, Toast.LENGTH_SHORT).show();
                    loadPendingRequests(); // Reload list
                    loadDashboardData(); // Refresh stats
                } else {
                    Toast.makeText(AdminActivity.this, "Failed: " + response.message(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<MessageResponse> call, Throwable t) {
                Toast.makeText(AdminActivity.this, "Failed: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void logout() {
        sessionManager.logoutUser();
        Intent intent = new Intent(AdminActivity.this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    private void showAllPatients() {
        apiService.getAllPatients().enqueue(new Callback<List<com.example.hospitalmanagement.model.PatientResponse>>() {
            @Override
            public void onResponse(Call<List<com.example.hospitalmanagement.model.PatientResponse>> call,
                    Response<List<com.example.hospitalmanagement.model.PatientResponse>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    // Create a simple list to display
                    StringBuilder patientList = new StringBuilder("Patients:\n\n");
                    for (com.example.hospitalmanagement.model.PatientResponse patient : response.body()) {
                        patientList.append("• ").append(patient.getFullName()).append("\n");
                    }

                    new androidx.appcompat.app.AlertDialog.Builder(AdminActivity.this)
                            .setTitle("All Patients (" + response.body().size() + ")")
                            .setMessage(patientList.toString())
                            .setPositiveButton("OK", null)
                            .show();
                } else {
                    Toast.makeText(AdminActivity.this, "Failed to load patients", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<com.example.hospitalmanagement.model.PatientResponse>> call, Throwable t) {
                Toast.makeText(AdminActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void showAllDoctors() {
        apiService.getAllDoctors().enqueue(new Callback<List<DoctorResponse>>() {
            @Override
            public void onResponse(Call<List<DoctorResponse>> call,
                    Response<List<DoctorResponse>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    // Create a simple list to display
                    StringBuilder doctorList = new StringBuilder("Doctors:\n\n");
                    for (DoctorResponse doctor : response.body()) {
                        String specs = doctor.getSpecializations() != null && !doctor.getSpecializations().isEmpty()
                                ? " - " + doctor.getSpecializations().get(0)
                                : "";
                        doctorList.append("• ").append(doctor.getFullName()).append(specs).append("\n");
                    }

                    new androidx.appcompat.app.AlertDialog.Builder(AdminActivity.this)
                            .setTitle("All Doctors (" + response.body().size() + ")")
                            .setMessage(doctorList.toString())
                            .setPositiveButton("OK", null)
                            .show();
                } else {
                    Toast.makeText(AdminActivity.this, "Failed to load doctors", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<DoctorResponse>> call, Throwable t) {
                Toast.makeText(AdminActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(androidx.core.view.GravityCompat.START)) {
            drawerLayout.closeDrawer(androidx.core.view.GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }
}
