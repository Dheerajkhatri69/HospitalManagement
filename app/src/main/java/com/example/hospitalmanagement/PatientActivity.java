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

import java.util.ArrayList;
import java.util.List;

public class PatientActivity extends AppCompatActivity implements AppointmentAdapter.OnAppointmentClickListener {
    private RecyclerView appointmentsRecyclerView;
    private AppointmentAdapter appointmentAdapter;
    private List<Appointment> appointmentList = new ArrayList<>();

    private SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_patient);

        // Session Check
        sessionManager = new SessionManager(this);
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

        // Create sample appointment data
        createSampleAppointments();

        appointmentAdapter = new AppointmentAdapter(appointmentList, this, true, this);
        appointmentsRecyclerView.setAdapter(appointmentAdapter);
        appointmentsRecyclerView.setHasFixedSize(true);
        appointmentsRecyclerView.setNestedScrollingEnabled(false);
    }

    private void createSampleAppointments() {
        appointmentList.clear();
        // Sample data for patient view (e.g. Doctor names instead of patient names)
        appointmentList.add(new Appointment("1", "Dr. SMITH (Cardio)", "10:30 AM", "Confirmed", "Checkup", "Room 101",
                R.drawable.ic_patient_avatar));
        appointmentList.add(new Appointment("2", "Dr. JANE (Derma)", "11:45 AM", "Pending", "Consultation", "Room 102",
                R.drawable.ic_patient_avatar));
    }

    @Override
    public void onAppointmentClick(Appointment appointment) {
        Toast.makeText(this, "Details for " + appointment.getPatientName(), Toast.LENGTH_SHORT).show();
    }
}
