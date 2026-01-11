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
import com.example.hospitalmanagement.adapter.AppointmentAdapter;
import com.example.hospitalmanagement.api.ApiService;
import com.example.hospitalmanagement.api.RetrofitClient;
import com.example.hospitalmanagement.model.Appointment;
import com.example.hospitalmanagement.model.AppointmentResponse;
import com.example.hospitalmanagement.utils.SessionManager;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PatientAppointmentsFragment extends Fragment implements AppointmentAdapter.OnAppointmentClickListener {

    private RecyclerView appointmentsRecyclerView;
    private SwipeRefreshLayout swipeRefreshLayout;
    private AppointmentAdapter appointmentAdapter;
    private List<Appointment> appointmentList = new ArrayList<>();
    private SessionManager sessionManager;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_patient_appointments, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        sessionManager = new SessionManager(requireContext());
        
        appointmentsRecyclerView = view.findViewById(R.id.appointments_recyclerview);
        appointmentsRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        
        swipeRefreshLayout = view.findViewById(R.id.swipe_refresh_layout);
        swipeRefreshLayout.setOnRefreshListener(this::loadAppointments);
        
        loadAppointments();
    }

    private void loadAppointments() {
        if (swipeRefreshLayout != null) {
            swipeRefreshLayout.setRefreshing(true);
        }

        Integer patientId = sessionManager.getPatientId();
        if (patientId == null) {
            if (swipeRefreshLayout != null) swipeRefreshLayout.setRefreshing(false);
            Toast.makeText(getContext(), "Patient ID not found", Toast.LENGTH_SHORT).show();
            return;
        }

        ApiService apiService = RetrofitClient.getApiService();

        apiService.getAppointmentsByPatient(patientId)
                .enqueue(new Callback<List<AppointmentResponse>>() {
                    @Override
                    public void onResponse(Call<List<AppointmentResponse>> call, Response<List<AppointmentResponse>> response) {
                        if (swipeRefreshLayout != null) swipeRefreshLayout.setRefreshing(false);
                        
                        if (response.isSuccessful() && response.body() != null) {
                            appointmentList.clear();
                            for (AppointmentResponse apt : response.body()) {
                                String doctorName = apt.getDoctorName() != null ? apt.getDoctorName() : "Doctor";
                                String time = apt.getScheduledTime() != null ? apt.getScheduledTime() : "N/A";
                                String status = apt.getStatus() != null ? apt.getStatus() : "Unknown";
                                String reason = "Appointment"; 

                                appointmentList.add(new Appointment(
                                        String.valueOf(apt.getAppointmentId()),
                                        doctorName,
                                        time,
                                        status,
                                        reason,
                                        "", 
                                        R.drawable.ic_patient_avatar));
                            }
                            
                            appointmentAdapter = new AppointmentAdapter(appointmentList, getContext(), false, PatientAppointmentsFragment.this);
                            appointmentsRecyclerView.setAdapter(appointmentAdapter);
                        } else {
                            Toast.makeText(getContext(), "Failed to load appointments", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<List<AppointmentResponse>> call, Throwable t) {
                        if (swipeRefreshLayout != null) swipeRefreshLayout.setRefreshing(false);
                        Toast.makeText(getContext(), "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    @Override
    public void onAppointmentClick(Appointment appointment) {
        android.content.Intent intent = new android.content.Intent(getContext(), com.example.hospitalmanagement.AppointmentDetailActivity.class);
        intent.putExtra("appointment_id", Integer.parseInt(appointment.getId()));
        startActivity(intent);
    }
}
