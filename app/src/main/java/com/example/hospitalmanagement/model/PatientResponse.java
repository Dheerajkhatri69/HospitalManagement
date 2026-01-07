package com.example.hospitalmanagement.model;

import com.google.gson.annotations.SerializedName;

public class PatientResponse {
    @SerializedName("patient_id")
    private int patientId;

    @SerializedName("user_id")
    private int userId;

    @SerializedName("full_name")
    private String fullName;

    @SerializedName("email")
    private String email;

    @SerializedName("date_of_birth")
    private String dateOfBirth;

    @SerializedName("gender")
    private String gender;

    @SerializedName("phone")
    private String phone;

    @SerializedName("emergency_contact")
    private String emergencyContact;

    @SerializedName("medical_history")
    private String medicalHistory;

    @SerializedName("insurance_info")
    private String insuranceInfo;

    public int getPatientId() {
        return patientId;
    }

    public int getUserId() {
        return userId;
    }

    public String getFullName() {
        return fullName;
    }

    public String getEmail() {
        return email;
    }

    public String getDateOfBirth() {
        return dateOfBirth;
    }

    public String getGender() {
        return gender;
    }

    public String getPhone() {
        return phone;
    }

    public String getEmergencyContact() {
        return emergencyContact;
    }

    public String getMedicalHistory() {
        return medicalHistory;
    }

    public String getInsuranceInfo() {
        return insuranceInfo;
    }
}
