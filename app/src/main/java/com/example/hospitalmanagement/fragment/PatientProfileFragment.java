package com.example.hospitalmanagement.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.hospitalmanagement.LoginActivity;
import com.example.hospitalmanagement.R;
import com.example.hospitalmanagement.SettingsActivity;
import com.example.hospitalmanagement.utils.SessionManager;
import com.google.android.material.button.MaterialButton;

public class PatientProfileFragment extends Fragment {

    private SessionManager sessionManager;
    private TextView profileName;
    private TextView profileEmail;
    private MaterialButton btnLogout;
    private MaterialButton btnSettings;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_patient_profile, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        sessionManager = new SessionManager(requireContext());

        profileName = view.findViewById(R.id.profile_name);
        profileEmail = view.findViewById(R.id.profile_email);
        btnLogout = view.findViewById(R.id.btn_logout);
        btnSettings = view.findViewById(R.id.btn_settings);

        loadProfileData();
        setupListeners(view);
    }

    @Override
    public void onResume() {
        super.onResume();
        loadProfileData();
    }

    private void loadProfileData() {
        profileName.setText(sessionManager.getFullName());
        profileEmail.setText(sessionManager.getEmail());
    }

    private void setupListeners(View view) {
        btnLogout.setOnClickListener(v -> logoutUser());
        
        btnSettings.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), SettingsActivity.class);
            startActivity(intent);
        });
        
        view.findViewById(R.id.btn_edit_profile).setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), com.example.hospitalmanagement.EditProfileActivity.class);
            startActivity(intent);
        });
    }

    private void logoutUser() {
        sessionManager.logoutUser();
        Intent intent = new Intent(getActivity(), LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        if (getActivity() != null) {
            getActivity().finish();
        }
    }
}
