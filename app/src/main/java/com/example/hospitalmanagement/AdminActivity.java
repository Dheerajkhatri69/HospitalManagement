package com.example.hospitalmanagement;

import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import androidx.viewpager2.widget.ViewPager2;

import com.example.hospitalmanagement.adapter.AdminViewPagerAdapter;
import com.example.hospitalmanagement.utils.NewRequestWorker;
import com.example.hospitalmanagement.utils.NotificationHelper;
import com.example.hospitalmanagement.utils.SessionManager;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.concurrent.TimeUnit;

import androidx.work.ExistingPeriodicWorkPolicy;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;

public class AdminActivity extends BaseActivity {

    private SessionManager sessionManager;
    private ViewPager2 viewPager;
    private BottomNavigationView bottomNavigationView;
    private TextView pageTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin);

        // Initialize Notification Channels
        NotificationHelper.createNotificationChannels(this);

        sessionManager = new SessionManager(this);
        if (!sessionManager.isLoggedIn()) {
            // Redirect to Login if needed, though SessionManager usually handles check
            // For now, assume BaseActivity or SessionManager handles it
            startActivity(new android.content.Intent(this, LoginActivity.class));
            finish();
            return;
        }

        initializeViews();
        setupViewPagerAndBottomNav();
        scheduleRequestWorker();
    }

    private void scheduleRequestWorker() {
        PeriodicWorkRequest requestWork = new PeriodicWorkRequest.Builder(NewRequestWorker.class, 15, TimeUnit.MINUTES)
                .build();

        WorkManager.getInstance(this).enqueueUniquePeriodicWork(
                "NewRequestCheck",
                ExistingPeriodicWorkPolicy.KEEP,
                requestWork
        );
    }

    private void initializeViews() {
        pageTitle = findViewById(R.id.page_title);
        
        findViewById(R.id.notification_icon).setOnClickListener(v -> 
            startActivity(new android.content.Intent(this, NotificationsActivity.class))
        );
    }

    private void setupViewPagerAndBottomNav() {
        viewPager = findViewById(R.id.view_pager);
        bottomNavigationView = findViewById(R.id.bottom_navigation);

        AdminViewPagerAdapter adapter = new AdminViewPagerAdapter(this);
        viewPager.setAdapter(adapter);
        
        // Enable user swipe
        viewPager.setUserInputEnabled(true);

        viewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                switch (position) {
                    case 0:
                        bottomNavigationView.setSelectedItemId(R.id.nav_admin_home);
                        pageTitle.setText("Admin Dashboard");
                        break;
                    case 1:
                        bottomNavigationView.setSelectedItemId(R.id.nav_admin_requests);
                        pageTitle.setText("Appointment Requests");
                        break;
                    case 2:
                        bottomNavigationView.setSelectedItemId(R.id.nav_admin_doctors);
                        pageTitle.setText("Manage Doctors");
                        break;
                    case 3:
                        bottomNavigationView.setSelectedItemId(R.id.nav_admin_profile);
                        pageTitle.setText("Profile");
                        break;
                }
            }
        });

        bottomNavigationView.setOnItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.nav_admin_home) {
                viewPager.setCurrentItem(0);
                return true;
            } else if (id == R.id.nav_admin_requests) {
                viewPager.setCurrentItem(1);
                return true;
            } else if (id == R.id.nav_admin_doctors) {
                viewPager.setCurrentItem(2);
                return true;
            } else if (id == R.id.nav_admin_profile) {
                viewPager.setCurrentItem(3);
                return true;
            }
            return false;
        });
    }
}
