package com.example.hospitalmanagement.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.hospitalmanagement.R;
import com.example.hospitalmanagement.adapter.AppointmentRequestAdapter;
import com.example.hospitalmanagement.api.ApiService;
import com.example.hospitalmanagement.api.RetrofitClient;
import com.example.hospitalmanagement.model.AppointmentRequestResponse;
import com.example.hospitalmanagement.model.AppointmentRequestUpdate;
import com.example.hospitalmanagement.model.MessageResponse;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AdminRequestsFragment extends Fragment implements AppointmentRequestAdapter.OnRequestActionListener {

    private RecyclerView requestsRecyclerView;
    private SwipeRefreshLayout swipeRefreshLayout;
    private LinearLayout emptyState;
    private AppointmentRequestAdapter requestAdapter;
    private List<AppointmentRequestResponse> pendingRequestsList = new ArrayList<>();
    private ApiService apiService;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_admin_requests, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        apiService = RetrofitClient.getApiService();
        initializeViews(view);
        loadPendingRequests();
    }

    private void initializeViews(View view) {
        requestsRecyclerView = view.findViewById(R.id.requests_recyclerview);
        swipeRefreshLayout = view.findViewById(R.id.swipe_refresh_layout);
        emptyState = view.findViewById(R.id.empty_state);
        
        swipeRefreshLayout.setOnRefreshListener(this::loadPendingRequests);

        requestsRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        requestAdapter = new AppointmentRequestAdapter(pendingRequestsList, this);
        requestsRecyclerView.setAdapter(requestAdapter);
    }

    private void loadPendingRequests() {
        if (swipeRefreshLayout != null) swipeRefreshLayout.setRefreshing(true);
        apiService.getAllAppointmentRequests("pending").enqueue(new Callback<List<AppointmentRequestResponse>>() {
            @Override
            public void onResponse(Call<List<AppointmentRequestResponse>> call, Response<List<AppointmentRequestResponse>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    pendingRequestsList = response.body();
                    updateRequestsList();
                } else {
                    showEmptyState();
                }
            }

            @Override
            public void onFailure(Call<List<AppointmentRequestResponse>> call, Throwable t) {
                if (swipeRefreshLayout != null) swipeRefreshLayout.setRefreshing(false);
                if (getContext() != null) {
                    Toast.makeText(getContext(), "Failed to load requests: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                    showEmptyState();
                }
            }
        });
    }

    private void updateRequestsList() {
        if (pendingRequestsList.isEmpty()) {
            showEmptyState();
        } else {
            emptyState.setVisibility(View.GONE);
            requestsRecyclerView.setVisibility(View.VISIBLE);
            requestAdapter.updateList(pendingRequestsList);
        }
    }

    private void showEmptyState() {
        emptyState.setVisibility(View.VISIBLE);
        requestsRecyclerView.setVisibility(View.GONE);
    }

    @Override
    public void onApprove(AppointmentRequestResponse request) {
        processRequestAction(request, "approved");
    }

    @Override
    public void onReject(AppointmentRequestResponse request) {
        processRequestAction(request, "rejected");
    }

    @Override
    public void onRequestClick(AppointmentRequestResponse request) {
        // Show details if needed
    }

    private void processRequestAction(AppointmentRequestResponse request, String status) {
        AppointmentRequestUpdate update = new AppointmentRequestUpdate(status);
        
        apiService.updateAppointmentRequest(request.getRequestId(), update).enqueue(new Callback<MessageResponse>() {
            @Override
            public void onResponse(Call<MessageResponse> call, Response<MessageResponse> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(getContext(), "Request " + status, Toast.LENGTH_SHORT).show();
                    loadPendingRequests(); // Reload list
                } else {
                    Toast.makeText(getContext(), "Failed to update request", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<MessageResponse> call, Throwable t) {
                Toast.makeText(getContext(), "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
