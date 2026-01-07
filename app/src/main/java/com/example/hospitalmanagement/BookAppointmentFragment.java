package com.example.hospitalmanagement;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.hospitalmanagement.adapter.DoctorSelectionAdapter;
import com.example.hospitalmanagement.api.ApiService;
import com.example.hospitalmanagement.api.RetrofitClient;
import com.example.hospitalmanagement.model.AppointmentRequestCreate;
import com.example.hospitalmanagement.model.AppointmentRequestResponse;
import com.example.hospitalmanagement.model.DoctorResponse;
import com.example.hospitalmanagement.model.SpecializationResponse;
import com.example.hospitalmanagement.utils.SessionManager;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class BookAppointmentFragment extends Fragment implements DoctorSelectionAdapter.OnDoctorSelectedListener {

    // UI Components
    private ImageView btnClose;
    private TextView stepIndicator;
    private AutoCompleteTextView specializationDropdown;
    private LinearLayout doctorSelectionSection, dateSelectionSection, timeSelectionSection, notesSection;
    private RecyclerView doctorsRecyclerView;
    private ProgressBar doctorsLoading;
    private TextInputEditText dateInput, timeInput, notesInput;
    private MaterialButton btnBack, btnNext;

    // Data
    private ApiService apiService;
    private SessionManager sessionManager;
    private List<SpecializationResponse> specializationList = new ArrayList<>();
    private List<DoctorResponse> doctorList = new ArrayList<>();
    private DoctorSelectionAdapter doctorAdapter;

    // Form Data
    private int currentStep = 1;
    private int selectedSpecId = -1;
    private DoctorResponse selectedDoctor = null;
    private String selectedDate = "";
    private String selectedTime = "";
    private Calendar calendar = Calendar.getInstance();

    public BookAppointmentFragment() {
        // Required empty public constructor
    }

    public static BookAppointmentFragment newInstance() {
        return new BookAppointmentFragment();
    }

    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        apiService = RetrofitClient.getApiService();
        sessionManager = new SessionManager(requireContext());
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_book_appointment, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        initializeViews(view);
        setupListeners();
        loadSpecializations();
    }

    private void initializeViews(View view) {
        btnClose = view.findViewById(R.id.btn_close);
        stepIndicator = view.findViewById(R.id.step_indicator);
        specializationDropdown = view.findViewById(R.id.specialization_dropdown);

        doctorSelectionSection = view.findViewById(R.id.doctor_selection_section);
        dateSelectionSection = view.findViewById(R.id.date_selection_section);
        timeSelectionSection = view.findViewById(R.id.time_selection_section);
        notesSection = view.findViewById(R.id.notes_section);

        doctorsRecyclerView = view.findViewById(R.id.doctors_recyclerview);
        doctorsLoading = view.findViewById(R.id.doctors_loading);

        dateInput = view.findViewById(R.id.date_input);
        timeInput = view.findViewById(R.id.time_input);
        notesInput = view.findViewById(R.id.notes_input);

        btnBack = view.findViewById(R.id.btn_back);
        btnNext = view.findViewById(R.id.btn_next);

        // Setup RecyclerView
        doctorAdapter = new DoctorSelectionAdapter(doctorList, this);
        doctorsRecyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        doctorsRecyclerView.setAdapter(doctorAdapter);
    }

    private void setupListeners() {
        btnClose.setOnClickListener(v -> dismissFragment());
        btnBack.setOnClickListener(v -> handleBackButton());
        btnNext.setOnClickListener(v -> handleNextButton());

        // Specialization selection
        specializationDropdown.setOnItemClickListener((parent, view, position, id) -> {
            SpecializationResponse selected = specializationList.get(position);
            selectedSpecId = selected.getSpecId();
            loadDoctorsBySpecialization(selectedSpecId);
        });

        // Date picker
        dateInput.setOnClickListener(v -> showDatePicker());

        // Time picker
        timeInput.setOnClickListener(v -> showTimePicker());
    }

    private void loadSpecializations() {
        apiService.getAllSpecializations().enqueue(new Callback<List<SpecializationResponse>>() {
            @Override
            public void onResponse(Call<List<SpecializationResponse>> call,
                    Response<List<SpecializationResponse>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    specializationList = response.body();
                    setupSpecializationDropdown();
                }
            }

            @Override
            public void onFailure(Call<List<SpecializationResponse>> call, Throwable t) {
                showToast("Failed to load specializations: " + t.getMessage());
            }
        });
    }

    private void setupSpecializationDropdown() {
        List<String> specializationNames = new ArrayList<>();
        for (SpecializationResponse spec : specializationList) {
            specializationNames.add(spec.getName());
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                requireContext(),
                android.R.layout.simple_dropdown_item_1line,
                specializationNames);
        specializationDropdown.setAdapter(adapter);
    }

    private void loadDoctorsBySpecialization(int specId) {
        doctorsLoading.setVisibility(View.VISIBLE);
        doctorsRecyclerView.setVisibility(View.GONE);

        apiService.getAllDoctors().enqueue(new Callback<List<DoctorResponse>>() {
            @Override
            public void onResponse(Call<List<DoctorResponse>> call,
                    Response<List<DoctorResponse>> response) {
                doctorsLoading.setVisibility(View.GONE);

                if (response.isSuccessful() && response.body() != null) {
                    // Filter doctors by specialization
                    List<DoctorResponse> filteredDoctors = new ArrayList<>();
                    String selectedSpecName = getSpecializationName(specId);

                    for (DoctorResponse doctor : response.body()) {
                        if (doctor.getSpecializations() != null &&
                                doctor.getSpecializations().contains(selectedSpecName)) {
                            filteredDoctors.add(doctor);
                        }
                    }

                    doctorList = filteredDoctors;
                    doctorAdapter.updateList(doctorList);
                    doctorsRecyclerView.setVisibility(View.VISIBLE);

                    if (doctorList.isEmpty()) {
                        showToast("No doctors available for this specialization");
                    }
                }
            }

            @Override
            public void onFailure(Call<List<DoctorResponse>> call, Throwable t) {
                doctorsLoading.setVisibility(View.GONE);
                showToast("Failed to load doctors: " + t.getMessage());
            }
        });
    }

    private String getSpecializationName(int specId) {
        for (SpecializationResponse spec : specializationList) {
            if (spec.getSpecId() == specId) {
                return spec.getName();
            }
        }
        return "";
    }

    private void showDatePicker() {
        DatePickerDialog datePickerDialog = new DatePickerDialog(
                requireContext(),
                (view, year, month, dayOfMonth) -> {
                    calendar.set(Calendar.YEAR, year);
                    calendar.set(Calendar.MONTH, month);
                    calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);

                    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
                    selectedDate = dateFormat.format(calendar.getTime());

                    SimpleDateFormat displayFormat = new SimpleDateFormat("MMM dd, yyyy", Locale.getDefault());
                    dateInput.setText(displayFormat.format(calendar.getTime()));
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH));

        // Set minimum date to today
        datePickerDialog.getDatePicker().setMinDate(System.currentTimeMillis());
        datePickerDialog.show();
    }

    private void showTimePicker() {
        TimePickerDialog timePickerDialog = new TimePickerDialog(
                requireContext(),
                (view, hourOfDay, minute) -> {
                    selectedTime = String.format(Locale.getDefault(), "%02d:%02d:00", hourOfDay, minute);
                    String displayTime = String.format(Locale.getDefault(), "%02d:%02d", hourOfDay, minute);
                    timeInput.setText(displayTime);
                },
                calendar.get(Calendar.HOUR_OF_DAY),
                calendar.get(Calendar.MINUTE),
                false);
        timePickerDialog.show();
    }

    @Override
    public void onDoctorSelected(DoctorResponse doctor) {
        selectedDoctor = doctor;
    }

    private void handleBackButton() {
        if (currentStep > 1) {
            currentStep--;
            updateStepUI();
        }
    }

    private void handleNextButton() {
        if (validateCurrentStep()) {
            if (currentStep < 4) {
                currentStep++;
                updateStepUI();
            } else {
                submitAppointmentRequest();
            }
        }
    }

    private boolean validateCurrentStep() {
        switch (currentStep) {
            case 1:
                if (selectedSpecId == -1) {
                    showToast("Please select a specialization");
                    return false;
                }
                return true;
            case 2:
                if (selectedDoctor == null) {
                    showToast("Please select a doctor");
                    return false;
                }
                return true;
            case 3:
                if (selectedDate.isEmpty()) {
                    showToast("Please select a date");
                    return false;
                }
                return true;
            case 4:
                if (selectedTime.isEmpty()) {
                    showToast("Please select a time");
                    return false;
                }
                return true;
            default:
                return true;
        }
    }

    private void updateStepUI() {
        stepIndicator.setText("Step " + currentStep + " of 4");

        // Hide all sections
        doctorSelectionSection.setVisibility(View.GONE);
        dateSelectionSection.setVisibility(View.GONE);
        timeSelectionSection.setVisibility(View.GONE);
        notesSection.setVisibility(View.GONE);

        // Show current step section
        switch (currentStep) {
            case 1:
                // Specialization is always visible
                btnBack.setVisibility(View.GONE);
                btnNext.setText("Next");
                break;
            case 2:
                doctorSelectionSection.setVisibility(View.VISIBLE);
                btnBack.setVisibility(View.VISIBLE);
                btnNext.setText("Next");
                break;
            case 3:
                dateSelectionSection.setVisibility(View.VISIBLE);
                btnBack.setVisibility(View.VISIBLE);
                btnNext.setText("Next");
                break;
            case 4:
                timeSelectionSection.setVisibility(View.VISIBLE);
                notesSection.setVisibility(View.VISIBLE);
                btnBack.setVisibility(View.VISIBLE);
                btnNext.setText("Submit Request");
                break;
        }
    }

    private void submitAppointmentRequest() {
        Integer patientId = sessionManager.getPatientId();
        if (patientId == null) {
            showToast("Patient ID not found");
            return;
        }

        if (selectedDoctor == null) {
            showToast("Doctor not selected");
            return;
        }

        String notes = notesInput.getText() != null ? notesInput.getText().toString() : "";

        AppointmentRequestCreate request = new AppointmentRequestCreate(
                patientId,
                selectedSpecId,
                selectedDoctor.getDoctorId(),
                selectedDate,
                selectedTime,
                notes);

        apiService.createAppointmentRequest(request).enqueue(new Callback<AppointmentRequestResponse>() {
            @Override
            public void onResponse(Call<AppointmentRequestResponse> call,
                    Response<AppointmentRequestResponse> response) {
                if (response.isSuccessful()) {
                    showToast("Appointment request submitted successfully!");
                    dismissFragment();
                    // Notify parent activity to refresh appointments
                } else {
                    showToast("Failed to submit request");
                }
            }

            @Override
            public void onFailure(Call<AppointmentRequestResponse> call, Throwable t) {
                showToast("Error: " + t.getMessage());
            }
        });
    }

    private void dismissFragment() {
        requireActivity().getSupportFragmentManager().beginTransaction()
                .remove(this)
                .commit();
    }

    private void showToast(String message) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show();
    }
}
