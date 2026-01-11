package com.example.hospitalmanagement;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;

import com.example.hospitalmanagement.adapter.PatientViewPagerAdapter;
import com.example.hospitalmanagement.utils.NotificationHelper;
import com.example.hospitalmanagement.utils.PatientNotificationWorker;
import com.example.hospitalmanagement.utils.SessionManager;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.concurrent.TimeUnit;

import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;

public class PatientActivity extends BaseActivity {

    private SessionManager sessionManager;
    private ViewPager2 viewPager;
    private BottomNavigationView bottomNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_patient);

        // Initialize Notification Channels
        NotificationHelper.createNotificationChannels(this);

        // Schedule Patient Notification Worker
        PeriodicWorkRequest patientWork = new PeriodicWorkRequest.Builder(PatientNotificationWorker.class, 15,
                TimeUnit.MINUTES).build();
        WorkManager.getInstance(this).enqueueUniquePeriodicWork("PatientNotifications",
                androidx.work.ExistingPeriodicWorkPolicy.KEEP, patientWork);

        // Session Check
        sessionManager = new SessionManager(this);
        if (!sessionManager.isLoggedIn()) {
            startActivity(new android.content.Intent(this, LoginActivity.class));
            finish();
            return;
        }

        setupTopBar();
        setupNavigationDrawer();
        setupViewPagerAndBottomNav();
    }

    private void setupTopBar() {
        // Setup notification icon click
        ImageView notificationIcon = findViewById(R.id.notification_icon);
        notificationIcon.setOnClickListener(v -> {
            startActivity(new android.content.Intent(this, NotificationsActivity.class));
        });

        // Set User Info in Top Bar
        TextView patientName = findViewById(R.id.patient_name);
        patientName.setText(sessionManager.getFullName());
    }

    private void setupNavigationDrawer() {
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

            if (id == R.id.nav_profile) {
                // Navigate to Profile (bottom nav position 3)
                viewPager.setCurrentItem(3);
            } else if (id == R.id.nav_logout) {
                // Logout Logic
                sessionManager.logoutUser();
                android.content.Intent intent = new android.content.Intent(PatientActivity.this, LoginActivity.class);
                intent.setFlags(android.content.Intent.FLAG_ACTIVITY_NEW_TASK
                        | android.content.Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                finish();
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
    }

    private void setupViewPagerAndBottomNav() {
        viewPager = findViewById(R.id.view_pager);
        bottomNavigationView = findViewById(R.id.bottom_navigation);

        PatientViewPagerAdapter adapter = new PatientViewPagerAdapter(this);
        viewPager.setAdapter(adapter);

        // Enable user swipe
        viewPager.setUserInputEnabled(true);

        viewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                switch (position) {
                    case 0:
                        bottomNavigationView.setSelectedItemId(R.id.nav_home);
                        break;
                    case 1:
                        bottomNavigationView.setSelectedItemId(R.id.nav_book_appointment);
                        break;
                    case 2:
                        bottomNavigationView.setSelectedItemId(R.id.nav_my_appointments);
                        break;
                    case 3:
                        bottomNavigationView.setSelectedItemId(R.id.nav_profile);
                        break;
                }
            }
        });

        bottomNavigationView.setOnItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.nav_home) {
                viewPager.setCurrentItem(0);
                return true;
            } else if (id == R.id.nav_book_appointment) {
                viewPager.setCurrentItem(1);
                return true;
            } else if (id == R.id.nav_my_appointments) {
                viewPager.setCurrentItem(2);
                return true;
            } else if (id == R.id.nav_profile) {
                viewPager.setCurrentItem(3);
                return true;
            }
            return false;
        });
    }
}
