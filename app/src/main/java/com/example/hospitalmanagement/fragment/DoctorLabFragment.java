package com.example.hospitalmanagement.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.hospitalmanagement.R;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;

public class DoctorLabFragment extends Fragment {

    private TextInputEditText labTimingInput;
    private View uploadCard;
    private MaterialButton generateReportBtn;
    private View reportSection;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_doctor_lab, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        labTimingInput = view.findViewById(R.id.lab_timing_input);
        uploadCard = view.findViewById(R.id.upload_card);
        generateReportBtn = view.findViewById(R.id.generate_report_btn);
        reportSection = view.findViewById(R.id.report_section);

        setupListeners();
    }

    private void setupListeners() {
        labTimingInput.setOnClickListener(v -> {
            // Show Date/Time Picker
            Toast.makeText(getContext(), "Select Date & Time", Toast.LENGTH_SHORT).show();
        });

        uploadCard.setOnClickListener(v -> {
            // Open Gallery/Camera
            Toast.makeText(getContext(), "Upload MRI Image", Toast.LENGTH_SHORT).show();
        });

        generateReportBtn.setOnClickListener(v -> {
            // Call AI Model API
            Toast.makeText(getContext(), "Generating Report...", Toast.LENGTH_SHORT).show();
            // Simulate success
            reportSection.setVisibility(View.VISIBLE);

            // Send Notification
            if (getContext() != null) {
                com.example.hospitalmanagement.utils.NotificationHelper.showLabNotification(
                        getContext(),
                        "Lab Report Ready",
                        "The MRI analysis report is ready for review.",
                        1001
                );
            }
        });
    }
}
