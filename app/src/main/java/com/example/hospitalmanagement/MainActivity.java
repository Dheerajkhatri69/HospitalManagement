package com.example.hospitalmanagement;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;

import com.example.hospitalmanagement.adapter.DoctorViewPagerAdapter;
import com.example.hospitalmanagement.utils.DoctorNotificationWorker;
import com.example.hospitalmanagement.utils.NotificationHelper;
import com.example.hospitalmanagement.utils.SessionManager;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.concurrent.TimeUnit;

import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;

public class MainActivity extends BaseActivity {

    private SessionManager sessionManager;
    private ViewPager2 viewPager;
    private BottomNavigationView bottomNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialize Notification Channels
        NotificationHelper.createNotificationChannels(this);
        
        // Schedule Doctor Notification Worker
        PeriodicWorkRequest doctorWork = new PeriodicWorkRequest.Builder(DoctorNotificationWorker.class, 15, TimeUnit.MINUTES).build();
        WorkManager.getInstance(this).enqueueUniquePeriodicWork("DoctorNotifications", androidx.work.ExistingPeriodicWorkPolicy.KEEP, doctorWork);

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
        TextView doctorName = findViewById(R.id.doctor_name);
        doctorName.setText("Dr. " + sessionManager.getFullName());
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

            if (id == R.id.nav_logout) {
                // Logout Logic
                sessionManager.logoutUser();
                android.content.Intent intent = new android.content.Intent(MainActivity.this, LoginActivity.class);
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
    }

    private void setupViewPagerAndBottomNav() {
        viewPager = findViewById(R.id.view_pager);
        bottomNavigationView = findViewById(R.id.bottom_navigation);

        DoctorViewPagerAdapter adapter = new DoctorViewPagerAdapter(this);
        viewPager.setAdapter(adapter);

        // Disable swipe if needed, but user requested swipe navigation
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
                        bottomNavigationView.setSelectedItemId(R.id.nav_appointments);
                        break;
                    case 2:
                        bottomNavigationView.setSelectedItemId(R.id.nav_lab);
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
            } else if (id == R.id.nav_appointments) {
                viewPager.setCurrentItem(1);
                return true;
            } else if (id == R.id.nav_lab) {
                viewPager.setCurrentItem(2);
                return true;
            } else if (id == R.id.nav_profile) {
                viewPager.setCurrentItem(3);
                return true;
            }
            return false;
        });
    }

    public SessionManager getSessionManager() {
        return sessionManager;
    }
}
