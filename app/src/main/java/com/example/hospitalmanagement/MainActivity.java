package com.example.hospitalmanagement;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.viewpager2.widget.ViewPager2;

import com.example.hospitalmanagement.adapter.AppointmentAdapter;
import com.example.hospitalmanagement.model.Appointment;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends BaseActivity implements AppointmentAdapter.OnAppointmentClickListener {
    private RecyclerView appointmentsRecyclerView;
    private AppointmentAdapter appointmentAdapter;
    private List<Appointment> appointmentList = new ArrayList<>();

    private com.example.hospitalmanagement.utils.SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Session Check
        sessionManager = new com.example.hospitalmanagement.utils.SessionManager(this);
        if (!sessionManager.isLoggedIn()) {
            startActivity(new android.content.Intent(this, LoginActivity.class));
            finish();
            return;
        }
        // EdgeToEdge.enable(this);
        //
        // // Handle edge-to-edge insets
        // ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v,
        // insets) -> {
        // Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
        // v.setPadding(systemBars.left, systemBars.top, systemBars.right,
        // systemBars.bottom);
        // return insets;
        // });

        // Setup notification icon click
        ImageView notificationIcon = findViewById(R.id.notification_icon);
        notificationIcon.setOnClickListener(v -> {
            Toast.makeText(this, "Notifications", Toast.LENGTH_SHORT).show();
            // Add navigation to notification page here
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
        // Setup Navigation Item Click Listener
        navigationView.setNavigationItemSelectedListener(item -> {
            int id = item.getItemId();

            if (id == R.id.nav_logout) {
                // Logout Logic
                sessionManager.logoutUser();
                android.content.Intent intent = new android.content.Intent(MainActivity.this, LoginActivity.class);
                intent.setFlags(android.content.Intent.FLAG_ACTIVITY_NEW_TASK
                        | android.content.Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                finish();
            } else {
                // Handle navigation view item clicks here.
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
        TextView doctorName = findViewById(R.id.doctor_name);
        doctorName.setText("Dr. " + sessionManager.getFullName());

        // Setup search functionality
        com.google.android.material.textfield.TextInputEditText searchBox = findViewById(R.id.search_box);
        searchBox.setOnClickListener(v -> {
            Toast.makeText(this, "Search", Toast.LENGTH_SHORT).show();
            // Add search functionality here
        });

        // Setup Quick Action icons
        setupQuickActions();

        // Setup banner carousel - REMOVED (Replaced with HorizontalScrollView in XML)

        // Setup appointment RecyclerView
        setupAppointmentsRecyclerView();

        // Setup "Show All" button
        TextView showAllButton = findViewById(R.id.show_all_button);
        showAllButton.setOnClickListener(v -> {
            Toast.makeText(this, "Show All Appointments", Toast.LENGTH_SHORT).show();
            // Add navigation to appointments page here
        });
    }

    private void setupQuickActions() {
        // You can add click listeners for each quick action icon
        View patientRecords = findViewById(R.id.quick_action_patients);
        View schedule = findViewById(R.id.quick_action_schedule);
        View prescriptions = findViewById(R.id.quick_action_prescriptions);
        View labResults = findViewById(R.id.quick_action_lab);

        patientRecords.setOnClickListener(v -> Toast.makeText(this, "Patient Records", Toast.LENGTH_SHORT).show());

        schedule.setOnClickListener(v -> Toast.makeText(this, "Schedule", Toast.LENGTH_SHORT).show());

        prescriptions.setOnClickListener(v -> Toast.makeText(this, "Prescriptions", Toast.LENGTH_SHORT).show());

        labResults.setOnClickListener(v -> Toast.makeText(this, "Lab Results", Toast.LENGTH_SHORT).show());
    }

    private void setupAppointmentsRecyclerView() {
        appointmentsRecyclerView = findViewById(R.id.appointments_recyclerview);
        appointmentsRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Load real appointment data from API
        loadRealAppointments();

        appointmentAdapter = new AppointmentAdapter(appointmentList, this, true, this);
        appointmentsRecyclerView.setAdapter(appointmentAdapter);

        // Set fixed size for better performance
        appointmentsRecyclerView.setHasFixedSize(true);

        // Disable scrolling for RecyclerView since we only show 3 items
        appointmentsRecyclerView.setNestedScrollingEnabled(false);
    }

    private void loadRealAppointments() {
        Integer doctorId = sessionManager.getDoctorId();
        if (doctorId == null) {
            Toast.makeText(this, "Doctor ID not found", Toast.LENGTH_SHORT).show();
            return;
        }

        com.example.hospitalmanagement.api.ApiService apiService = com.example.hospitalmanagement.api.RetrofitClient
                .getApiService();

        apiService.getAppointmentsByDoctor(doctorId)
                .enqueue(new retrofit2.Callback<List<com.example.hospitalmanagement.model.AppointmentResponse>>() {
                    @Override
                    public void onResponse(
                            retrofit2.Call<List<com.example.hospitalmanagement.model.AppointmentResponse>> call,
                            retrofit2.Response<List<com.example.hospitalmanagement.model.AppointmentResponse>> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            appointmentList.clear();
                            for (com.example.hospitalmanagement.model.AppointmentResponse apt : response.body()) {
                                // Convert API response to Appointment model
                                String patientName = apt.getPatientName() != null ? apt.getPatientName() : "Patient";
                                String time = apt.getScheduledTime() != null ? apt.getScheduledTime() : "N/A";
                                String status = apt.getStatus() != null ? apt.getStatus() : "Unknown";
                                String reason = "Appointment"; // No reason field in API

                                appointmentList.add(new Appointment(
                                        String.valueOf(apt.getAppointmentId()),
                                        patientName,
                                        time,
                                        status,
                                        reason,
                                        "", // No room number
                                        R.drawable.ic_patient_avatar));
                            }
                            appointmentAdapter.notifyDataSetChanged();
                        } else {
                            Toast.makeText(MainActivity.this, "Failed to load appointments", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(
                            retrofit2.Call<List<com.example.hospitalmanagement.model.AppointmentResponse>> call,
                            Throwable t) {
                        Toast.makeText(MainActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    @Override
    public void onAppointmentClick(Appointment appointment) {
        // Handle appointment click
        Toast.makeText(this, "Appointment with " + appointment.getPatientName(), Toast.LENGTH_SHORT).show();
        // You can navigate to appointment details page here
    }
}