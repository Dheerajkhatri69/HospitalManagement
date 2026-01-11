package com.example.hospitalmanagement.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.hospitalmanagement.R;
import com.example.hospitalmanagement.api.ApiService;
import com.example.hospitalmanagement.api.RetrofitClient;
import com.example.hospitalmanagement.model.DashboardStats;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AdminHomeFragment extends Fragment {

    private TextView totalPatients, totalDoctors, pendingRequests, todayAppointments;
    private SwipeRefreshLayout swipeRefreshLayout;
    private ApiService apiService;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_admin_home, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        apiService = RetrofitClient.getApiService();
        initializeViews(view);
        loadDashboardData();
    }

    private void initializeViews(View view) {
        swipeRefreshLayout = view.findViewById(R.id.swipe_refresh_layout);
        swipeRefreshLayout.setOnRefreshListener(this::loadDashboardData);

        totalPatients = view.findViewById(R.id.total_patients);
        totalDoctors = view.findViewById(R.id.total_doctors);
        pendingRequests = view.findViewById(R.id.pending_requests);
        todayAppointments = view.findViewById(R.id.today_appointments);

        // Card Listeners
        view.findViewById(R.id.card_doctors).setOnClickListener(v -> {
            // Navigate to Doctors Tab (Tab 2)
            if (getActivity() instanceof com.example.hospitalmanagement.AdminActivity) {
                ((androidx.viewpager2.widget.ViewPager2) getActivity().findViewById(R.id.view_pager)).setCurrentItem(2);
            }
        });

        view.findViewById(R.id.card_patients).setOnClickListener(v -> {
            // Start Patients Activity
            android.content.Intent intent = new android.content.Intent(getActivity(), com.example.hospitalmanagement.AdminPatientsActivity.class);
            startActivity(intent);
        });

        view.findViewById(R.id.card_appointments).setOnClickListener(v -> {
            // Start Appointments Activity (All)
            android.content.Intent intent = new android.content.Intent(getActivity(), com.example.hospitalmanagement.AdminAppointmentsActivity.class);
            intent.putExtra("filter", "all");
            startActivity(intent);
        });

        view.findViewById(R.id.card_today_appointments).setOnClickListener(v -> {
            // Start Appointments Activity (Today)
            android.content.Intent intent = new android.content.Intent(getActivity(), com.example.hospitalmanagement.AdminAppointmentsActivity.class);
            intent.putExtra("filter", "today");
            startActivity(intent);
        });
    }

    private void loadDashboardData() {
        if (swipeRefreshLayout != null) swipeRefreshLayout.setRefreshing(true);
        apiService.getDashboardStats().enqueue(new Callback<DashboardStats>() {
            @Override
            public void onResponse(Call<DashboardStats> call, Response<DashboardStats> response) {
                if (swipeRefreshLayout != null) swipeRefreshLayout.setRefreshing(false);
                if (response.isSuccessful() && response.body() != null) {
                    DashboardStats stats = response.body();
                    updateDashboardUI(stats);
                }
            }

            @Override
            public void onFailure(Call<DashboardStats> call, Throwable t) {
                if (swipeRefreshLayout != null) swipeRefreshLayout.setRefreshing(false);
                if (getContext() != null) {
                    Toast.makeText(getContext(), "Failed to load dashboard: " + t.getMessage(),
                            Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void updateDashboardUI(DashboardStats stats) {
        totalPatients.setText(String.valueOf(stats.getTotalPatients()));
        totalDoctors.setText(String.valueOf(stats.getTotalDoctors()));
        pendingRequests.setText(String.valueOf(stats.getPendingRequests()));
        todayAppointments.setText(String.valueOf(stats.getTodayAppointments()));
    }
}
