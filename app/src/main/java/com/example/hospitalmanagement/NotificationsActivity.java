package com.example.hospitalmanagement;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.hospitalmanagement.adapter.NotificationAdapter;
import com.example.hospitalmanagement.api.ApiService;
import com.example.hospitalmanagement.api.RetrofitClient;
import com.example.hospitalmanagement.model.MessageResponse;
import com.example.hospitalmanagement.model.Notification;
import com.example.hospitalmanagement.utils.SessionManager;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class NotificationsActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private TextView tvNoNotifications;
    private ApiService apiService;
    private SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notifications);

        apiService = RetrofitClient.getApiService();
        sessionManager = new SessionManager(this);

        recyclerView = findViewById(R.id.recycler_notifications);
        tvNoNotifications = findViewById(R.id.tv_no_notifications);
        
        findViewById(R.id.btn_back).setOnClickListener(v -> finish());

        fetchNotifications();
    }

    private void fetchNotifications() {
        int userId = sessionManager.getUserId();
        if (userId == -1) {
            Toast.makeText(this, "User not logged in", Toast.LENGTH_SHORT).show();
            return;
        }

        apiService.getNotifications(userId).enqueue(new Callback<List<Notification>>() {
            @Override
            public void onResponse(Call<List<Notification>> call, Response<List<Notification>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<Notification> notifications = response.body();
                    setupRecyclerView(notifications);
                } else {
                    showMockData(); // Fallback to mock data if API fails or returns empty initially
                }
            }

            @Override
            public void onFailure(Call<List<Notification>> call, Throwable t) {
                Toast.makeText(NotificationsActivity.this, "Failed to load notifications: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                showMockData();
            }
        });
    }

    private void setupRecyclerView(List<Notification> notifications) {
        if (notifications.isEmpty()) {
            recyclerView.setVisibility(View.GONE);
            tvNoNotifications.setVisibility(View.VISIBLE);
        } else {
            recyclerView.setVisibility(View.VISIBLE);
            tvNoNotifications.setVisibility(View.GONE);
            
            NotificationAdapter adapter = new NotificationAdapter(notifications, notification -> {
                if (!notification.isRead()) {
                    markAsRead(notification);
                }
            });
            recyclerView.setLayoutManager(new LinearLayoutManager(this));
            recyclerView.setAdapter(adapter);
        }
    }

    private void markAsRead(Notification notification) {
        apiService.markNotificationRead(notification.getNotificationId()).enqueue(new Callback<MessageResponse>() {
            @Override
            public void onResponse(Call<MessageResponse> call, Response<MessageResponse> response) {
                if (response.isSuccessful()) {
                    notification.setRead(true);
                    if (recyclerView.getAdapter() != null) {
                        recyclerView.getAdapter().notifyDataSetChanged();
                    }
                }
            }

            @Override
            public void onFailure(Call<MessageResponse> call, Throwable t) {
                // Silent failure is okay here
            }
        });
    }

    private void showMockData() {
        // Only show mock data if we really want to fallback, otherwise show empty state
        // For now, let's keep it empty if API fails to avoid confusion
        setupRecyclerView(new ArrayList<>());
    }
}
