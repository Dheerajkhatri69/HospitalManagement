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
import com.example.hospitalmanagement.model.Appointment;
import com.example.hospitalmanagement.utils.SessionManager;
import com.example.hospitalmanagement.api.RetrofitClient;
import com.example.hospitalmanagement.api.ApiService;
import com.example.hospitalmanagement.model.AppointmentResponse;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DoctorAppointmentsFragment extends Fragment implements AppointmentAdapter.OnAppointmentClickListener {

    private RecyclerView appointmentsRecyclerView;
    private SwipeRefreshLayout swipeRefreshLayout;
    private AppointmentAdapter appointmentAdapter;
    private List<Appointment> appointmentList = new ArrayList<>();
    private SessionManager sessionManager;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_doctor_appointments, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        sessionManager = new SessionManager(requireContext());
        
        appointmentsRecyclerView = view.findViewById(R.id.appointments_recyclerview);
        swipeRefreshLayout = view.findViewById(R.id.swipe_refresh_layout);
        
        appointmentsRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        swipeRefreshLayout.setOnRefreshListener(this::loadAppointments);
        
        loadAppointments();
    }

    private void loadAppointments() {
        if (swipeRefreshLayout != null) swipeRefreshLayout.setRefreshing(true);
        Integer doctorId = sessionManager.getDoctorId();
        if (doctorId == null) {
            return;
        }

        ApiService apiService = RetrofitClient.getApiService();

        apiService.getAppointmentsByDoctor(doctorId)
                .enqueue(new Callback<List<AppointmentResponse>>() {
                    @Override
                    public void onResponse(Call<List<AppointmentResponse>> call, Response<List<AppointmentResponse>> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            appointmentList.clear();
                            for (AppointmentResponse apt : response.body()) {
                                String patientName = apt.getPatientName() != null ? apt.getPatientName() : "Patient";
                                String time = apt.getScheduledTime() != null ? apt.getScheduledTime() : "N/A";
                                String status = apt.getStatus() != null ? apt.getStatus() : "Unknown";
                                String reason = "Appointment"; 

                                appointmentList.add(new Appointment(
                                        String.valueOf(apt.getAppointmentId()),
                                        patientName,
                                        time,
                                        status,
                                        reason,
                                        "", 
                                        R.drawable.ic_patient_avatar));
                            }
                            
                            // Pass false for limitToThree
                            appointmentAdapter = new AppointmentAdapter(appointmentList, getContext(), false, DoctorAppointmentsFragment.this);
                            appointmentsRecyclerView.setAdapter(appointmentAdapter);
                        }
                    }

                    @Override
                    public void onFailure(Call<List<AppointmentResponse>> call, Throwable t) {
                        if (swipeRefreshLayout != null) swipeRefreshLayout.setRefreshing(false);
                        // Handle error
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
