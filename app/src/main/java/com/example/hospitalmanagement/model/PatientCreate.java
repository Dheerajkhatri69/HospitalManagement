package com.example.hospitalmanagement.model;

import com.google.gson.annotations.SerializedName;

public class PatientCreate {
    @SerializedName("user_id")
    private int userId;

    @SerializedName("date_of_birth")
    private String dateOfBirth;

    @SerializedName("gender")
    private String gender;

    @SerializedName("address")
    private String address;

    @SerializedName("phone")
    private String phone;

    @SerializedName("emergency_contact")
    private String emergencyContact;

    @SerializedName("medical_history")
    private String medicalHistory;

    @SerializedName("insurance_info")
    private String insuranceInfo;

    public PatientCreate(int userId, String dateOfBirth, String gender, String address,
            String phone, String emergencyContact, String medicalHistory, String insuranceInfo) {
        this.userId = userId;
        this.dateOfBirth = dateOfBirth;
        this.gender = gender;
        this.address = address;
        this.phone = phone;
        this.emergencyContact = emergencyContact;
        this.medicalHistory = medicalHistory;
        this.insuranceInfo = insuranceInfo;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getDateOfBirth() {
        return dateOfBirth;
    }

    public void setDateOfBirth(String dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getEmergencyContact() {
        return emergencyContact;
    }

    public void setEmergencyContact(String emergencyContact) {
        this.emergencyContact = emergencyContact;
    }

    public String getMedicalHistory() {
        return medicalHistory;
    }

    public void setMedicalHistory(String medicalHistory) {
        this.medicalHistory = medicalHistory;
    }

    public String getInsuranceInfo() {
        return insuranceInfo;
    }

    public void setInsuranceInfo(String insuranceInfo) {
        this.insuranceInfo = insuranceInfo;
    }
}
