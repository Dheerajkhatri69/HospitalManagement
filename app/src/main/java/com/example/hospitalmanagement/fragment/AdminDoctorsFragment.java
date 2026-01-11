package com.example.hospitalmanagement.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.hospitalmanagement.R;
import com.example.hospitalmanagement.adapter.DoctorListAdapter;
import com.example.hospitalmanagement.api.ApiService;
import com.example.hospitalmanagement.api.RetrofitClient;
import com.example.hospitalmanagement.model.DoctorResponse;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AdminDoctorsFragment extends Fragment {

    private RecyclerView doctorsRecyclerView;
    private DoctorListAdapter doctorAdapter;
    private List<DoctorResponse> doctorList = new ArrayList<>();
    private ApiService apiService;
    private SwipeRefreshLayout swipeRefreshLayout;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_admin_doctors, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        apiService = RetrofitClient.getApiService();
        initializeViews(view);
        loadDoctors();
    }
    
    @Override
    public void onResume() {
        super.onResume();
        loadDoctors();
    }

    private void initializeViews(View view) {
        doctorsRecyclerView = view.findViewById(R.id.doctors_recyclerview);
        swipeRefreshLayout = view.findViewById(R.id.swipe_refresh_layout);
        
        doctorsRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        
        doctorAdapter = new DoctorListAdapter(doctorList, new DoctorListAdapter.OnDoctorClickListener() {
            @Override
            public void onDoctorClick(DoctorResponse doctor) {
                onViewAppointments(doctor);
            }

            @Override
            public void onViewAppointments(DoctorResponse doctor) {
                if (getContext() != null) {
                    android.content.Intent intent = new android.content.Intent(getContext(), com.example.hospitalmanagement.AdminAppointmentsActivity.class);
                    intent.putExtra("doctor_id", doctor.getDoctorId());
                    startActivity(intent);
                }
            }

            @Override
            public void onEditDoctor(DoctorResponse doctor) {
                if (getContext() != null) {
                    android.content.Intent intent = new android.content.Intent(getContext(), com.example.hospitalmanagement.AdminEditDoctorActivity.class);
                    intent.putExtra("doctor_id", doctor.getDoctorId());
                    startActivity(intent);
                }
            }
        });
        
        doctorsRecyclerView.setAdapter(doctorAdapter);

        view.findViewById(R.id.fab_add_doctor).setOnClickListener(v -> {
             if (getContext() != null) {
                android.content.Intent intent = new android.content.Intent(getContext(), com.example.hospitalmanagement.AdminAddDoctorActivity.class);
                startActivity(intent);
            }
        });
        
        swipeRefreshLayout.setOnRefreshListener(this::loadDoctors);
    }

    private void loadDoctors() {
        swipeRefreshLayout.setRefreshing(true);
        apiService.getAllDoctors().enqueue(new Callback<List<DoctorResponse>>() {
            @Override
            public void onResponse(Call<List<DoctorResponse>> call, Response<List<DoctorResponse>> response) {
                swipeRefreshLayout.setRefreshing(false);
                if (response.isSuccessful() && response.body() != null) {
                    doctorList = response.body();
                    doctorAdapter.updateList(doctorList);
                } else {
                    if (getContext() != null) {
                         Toast.makeText(getContext(), "Failed to load doctors", Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onFailure(Call<List<DoctorResponse>> call, Throwable t) {
                swipeRefreshLayout.setRefreshing(false);
                if (getContext() != null) {
                    Toast.makeText(getContext(), "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
