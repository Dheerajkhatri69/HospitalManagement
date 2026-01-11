package com.example.hospitalmanagement.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.hospitalmanagement.R;
import com.example.hospitalmanagement.api.ApiService;
import com.example.hospitalmanagement.api.RetrofitClient;
import com.example.hospitalmanagement.model.AppointmentResponse;
import com.example.hospitalmanagement.utils.SessionManager;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PatientHomeFragment extends Fragment {

    private TextView welcomeText;
    private TextView nextApptDoctor;
    private TextView nextApptTime;
    private CardView nextAppointmentCard;
    private TextView noUpcomingText;
    private SwipeRefreshLayout swipeRefreshLayout;

    private SessionManager sessionManager;
    private ApiService apiService;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_patient_home, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        sessionManager = new SessionManager(requireContext());
        apiService = RetrofitClient.getApiService();

        initializeViews(view);
        setupWelcomeMessage();
        loadNextAppointment();
    }

    private void initializeViews(View view) {
        welcomeText = view.findViewById(R.id.welcome_text);
        nextApptDoctor = view.findViewById(R.id.next_appt_doctor);
        nextApptTime = view.findViewById(R.id.next_appt_time);
        nextAppointmentCard = view.findViewById(R.id.next_appointment_card);
        noUpcomingText = view.findViewById(R.id.no_upcoming_text);

        // Setup Search
        view.findViewById(R.id.search_box).setOnClickListener(v -> {
            Toast.makeText(requireContext(), "Search functionality coming soon", Toast.LENGTH_SHORT).show();
        });
    }

    private void setupWelcomeMessage() {
        String name = sessionManager.getFullName();
        welcomeText.setText("Welcome Back, " + name + "!");
    }

    private void loadNextAppointment() {
        Integer patientId = sessionManager.getPatientId();
        if (patientId == null) return;

        apiService.getAppointmentsByPatient(patientId).enqueue(new Callback<List<AppointmentResponse>>() {
            @Override
            public void onResponse(Call<List<AppointmentResponse>> call, Response<List<AppointmentResponse>> response) {
                if (response.isSuccessful() && response.body() != null && !response.body().isEmpty()) {
                    // Assuming the API returns sorted appointments or we take the first one
                    // For now, let's take the first one
                    AppointmentResponse nextAppt = response.body().get(0);
                    
                    nextApptDoctor.setText(nextAppt.getDoctorName() != null ? nextAppt.getDoctorName() : "Doctor");
                    nextApptTime.setText(nextAppt.getScheduledTime() != null ? nextAppt.getScheduledTime() : "N/A");
                    
                    nextAppointmentCard.setVisibility(View.VISIBLE);
                    noUpcomingText.setVisibility(View.GONE);
                } else {
                    nextAppointmentCard.setVisibility(View.GONE);
                    noUpcomingText.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onFailure(Call<List<AppointmentResponse>> call, Throwable t) {
                if (swipeRefreshLayout != null) swipeRefreshLayout.setRefreshing(false);
                // Fail silently for home screen or show empty state
                nextAppointmentCard.setVisibility(View.GONE);
                noUpcomingText.setVisibility(View.VISIBLE);
            }
        });
    }
}
