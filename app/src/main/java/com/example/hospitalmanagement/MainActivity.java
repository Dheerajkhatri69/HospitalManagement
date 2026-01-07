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

public class MainActivity extends AppCompatActivity implements AppointmentAdapter.OnAppointmentClickListener {
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

        // Create sample appointment data
        createSampleAppointments();

        appointmentAdapter = new AppointmentAdapter(appointmentList, this, true, this);
        appointmentsRecyclerView.setAdapter(appointmentAdapter);

        // Set fixed size for better performance
        appointmentsRecyclerView.setHasFixedSize(true);

        // Disable scrolling for RecyclerView since we only show 3 items
        appointmentsRecyclerView.setNestedScrollingEnabled(false);
    }

    private void createSampleAppointments() {
        // Clear existing appointments
        appointmentList.clear();

        // Add sample appointments
        appointmentList.add(new Appointment(
                "1",
                "John Doe",
                "10:30 AM",
                "Confirmed",
                "Regular Checkup",
                "Room 101",
                R.drawable.ic_patient_avatar));

        appointmentList.add(new Appointment(
                "2",
                "Jane Smith",
                "11:45 AM",
                "Pending",
                "Follow-up Visit",
                "Room 102",
                R.drawable.ic_patient_avatar));

        appointmentList.add(new Appointment(
                "3",
                "Robert Johnson",
                "02:15 PM",
                "Confirmed",
                "Consultation",
                "Room 103",
                R.drawable.ic_patient_avatar));

        // Add more appointments (only first 3 will be shown in MainActivity)
        appointmentList.add(new Appointment(
                "4",
                "Sarah Williams",
                "03:30 PM",
                "Cancelled",
                "Routine Checkup",
                "Room 104",
                R.drawable.ic_patient_avatar));
    }

    @Override
    public void onAppointmentClick(Appointment appointment) {
        // Handle appointment click
        Toast.makeText(this, "Appointment with " + appointment.getPatientName(), Toast.LENGTH_SHORT).show();
        // You can navigate to appointment details page here
    }
}