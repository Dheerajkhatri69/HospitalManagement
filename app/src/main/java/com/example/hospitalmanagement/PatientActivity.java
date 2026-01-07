package com.example.hospitalmanagement;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.hospitalmanagement.adapter.AppointmentAdapter;
import com.example.hospitalmanagement.model.Appointment;
import com.example.hospitalmanagement.utils.SessionManager;
import com.example.hospitalmanagement.api.ApiService;
import com.example.hospitalmanagement.api.RetrofitClient;

import java.util.ArrayList;
import java.util.List;

public class PatientActivity extends BaseActivity implements AppointmentAdapter.OnAppointmentClickListener {
    private RecyclerView appointmentsRecyclerView;
    private AppointmentAdapter appointmentAdapter;
    private List<Appointment> appointmentList = new ArrayList<>();

    private SessionManager sessionManager;
    private ApiService apiService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_patient);

        // Session Check
        sessionManager = new SessionManager(this);
        apiService = RetrofitClient.getApiService();
        if (!sessionManager.isLoggedIn()) {
            startActivity(new android.content.Intent(this, LoginActivity.class));
            finish();
            return;
        }

        // Setup notification icon click
        ImageView notificationIcon = findViewById(R.id.notification_icon);
        notificationIcon.setOnClickListener(v -> {
            Toast.makeText(this, "Notifications", Toast.LENGTH_SHORT).show();
        });

        // Setup DrawerLayout
        androidx.drawerlayout.widget.DrawerLayout drawerLayout = findViewById(R.id.drawer_layout);
        com.google.android.material.navigation.NavigationView navigationView = findViewById(R.id.nav_view);

        // Setup hamburger menu icon
        ImageView menuIcon = findViewById(R.id.menu_icon);
        menuIcon.setOnClickListener(v -> {
            drawerLayout.openDrawer(androidx.core.view.GravityCompat.START);
        });

        // Setup Navigation Item Click Listener
        navigationView.setNavigationItemSelectedListener(item -> {
            int id = item.getItemId();

            if (id == R.id.nav_logout) {
                // Logout Logic
                sessionManager.logoutUser();
                android.content.Intent intent = new android.content.Intent(PatientActivity.this, LoginActivity.class);
                intent.setFlags(android.content.Intent.FLAG_ACTIVITY_NEW_TASK
                        | android.content.Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                finish();
            } else {
                Toast.makeText(this, "Clicked: " + item.getTitle(), Toast.LENGTH_SHORT).show();
            }

            drawerLayout.closeDrawer(androidx.core.view.GravityCompat.START);
            return true;
        });

        // Set User Info in Sidebar Header
        View headerView = navigationView.getHeaderView(0);
        TextView navName = headerView.findViewById(R.id.nav_header_name);
        TextView navEmail = headerView.findViewById(R.id.nav_header_email);

        navName.setText(sessionManager.getFullName());
        navEmail.setText(sessionManager.getEmail());

        // Set User Info in Top Bar
        TextView patientName = findViewById(R.id.patient_name);
        patientName.setText(sessionManager.getFullName());

        // Setup search functionality
        com.google.android.material.textfield.TextInputEditText searchBox = findViewById(R.id.search_box);
        searchBox.setOnClickListener(v -> {
            Toast.makeText(this, "Search", Toast.LENGTH_SHORT).show();
        });

        // Setup Quick Action icons
        setupQuickActions();

        // Setup appointment RecyclerView
        setupAppointmentsRecyclerView();

        // Setup "Show All" button
        TextView showAllButton = findViewById(R.id.show_all_button);
        showAllButton.setOnClickListener(v -> {
            Toast.makeText(this, "Show All Appointments", Toast.LENGTH_SHORT).show();
        });

        // Setup Floating Action Button for booking
        com.google.android.material.floatingactionbutton.FloatingActionButton fabBookAppointment = findViewById(
                R.id.fab_book_appointment);
        fabBookAppointment.setOnClickListener(v -> openBookingFragment());
    }

    private void setupQuickActions() {
        View appointments = findViewById(R.id.quick_action_appointments);
        View book = findViewById(R.id.quick_action_book);
        View lab = findViewById(R.id.quick_action_lab);
        View profile = findViewById(R.id.quick_action_profile);

        appointments.setOnClickListener(v -> Toast.makeText(this, "My Appointments", Toast.LENGTH_SHORT).show());
        book.setOnClickListener(v -> Toast.makeText(this, "Book Appointment", Toast.LENGTH_SHORT).show());
        lab.setOnClickListener(v -> Toast.makeText(this, "Lab Results", Toast.LENGTH_SHORT).show());
        profile.setOnClickListener(v -> Toast.makeText(this, "Profile", Toast.LENGTH_SHORT).show());
    }

    private void setupAppointmentsRecyclerView() {
        appointmentsRecyclerView = findViewById(R.id.appointments_recyclerview);
        appointmentsRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Load real appointment data from API
        loadRealAppointments();

        appointmentAdapter = new AppointmentAdapter(appointmentList, this, true, this);
        appointmentsRecyclerView.setAdapter(appointmentAdapter);
        appointmentsRecyclerView.setHasFixedSize(true);
        appointmentsRecyclerView.setNestedScrollingEnabled(false);
    }

    private void loadRealAppointments() {
        Integer patientId = sessionManager.getPatientId();
        if (patientId == null) {
            Toast.makeText(this, "Patient ID not found", Toast.LENGTH_SHORT).show();
            return;
        }

        apiService.getAppointmentsByPatient(patientId)
                .enqueue(new retrofit2.Callback<List<com.example.hospitalmanagement.model.AppointmentResponse>>() {
                    @Override
                    public void onResponse(
                            retrofit2.Call<List<com.example.hospitalmanagement.model.AppointmentResponse>> call,
                            retrofit2.Response<List<com.example.hospitalmanagement.model.AppointmentResponse>> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            appointmentList.clear();
                            for (com.example.hospitalmanagement.model.AppointmentResponse apt : response.body()) {
                                // Convert API response to Appointment model
                                String doctorName = apt.getDoctorName() != null ? apt.getDoctorName() : "Doctor";
                                String time = apt.getScheduledTime() != null ? apt.getScheduledTime() : "N/A";
                                String status = apt.getStatus() != null ? apt.getStatus() : "Unknown";
                                String reason = "Appointment"; // No reason field in API

                                appointmentList.add(new Appointment(
                                        String.valueOf(apt.getAppointmentId()),
                                        doctorName,
                                        time,
                                        status,
                                        reason,
                                        "", // No room number
                                        R.drawable.ic_patient_avatar));
                            }
                            appointmentAdapter.notifyDataSetChanged();
                        } else {
                            Toast.makeText(PatientActivity.this, "Failed to load appointments", Toast.LENGTH_SHORT)
                                    .show();
                        }
                    }

                    @Override
                    public void onFailure(
                            retrofit2.Call<List<com.example.hospitalmanagement.model.AppointmentResponse>> call,
                            Throwable t) {
                        Toast.makeText(PatientActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void openBookingFragment() {
        BookAppointmentFragment fragment = BookAppointmentFragment.newInstance();
        getSupportFragmentManager().beginTransaction()
                .add(android.R.id.content, fragment)
                .addToBackStack("booking")
                .commit();
    }

    @Override
    public void onAppointmentClick(Appointment appointment) {
        Toast.makeText(this, "Details for " + appointment.getPatientName(), Toast.LENGTH_SHORT).show();
    }
}
