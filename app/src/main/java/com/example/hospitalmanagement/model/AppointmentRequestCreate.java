package com.example.hospitalmanagement.model;

import com.google.gson.annotations.SerializedName;

public class AppointmentRequestCreate {
    @SerializedName("patient_id")
    private int patientId;

    @SerializedName("spec_id")
    private int specId;

    @SerializedName("doctor_id")
    private int doctorId;

    @SerializedName("preferred_date")
    private String preferredDate;

    @SerializedName("preferred_time")
    private String preferredTime;

    @SerializedName("notes")
    private String notes;

    public AppointmentRequestCreate(int patientId, int specId, int doctorId, String preferredDate,
            String preferredTime, String notes) {
        this.patientId = patientId;
        this.specId = specId;
        this.doctorId = doctorId;
        this.preferredDate = preferredDate;
        this.preferredTime = preferredTime;
        this.notes = notes;
    }

    public int getPatientId() {
        return patientId;
    }

    public void setPatientId(int patientId) {
        this.patientId = patientId;
    }

    public int getSpecId() {
        return specId;
    }

    public void setSpecId(int specId) {
        this.specId = specId;
    }

    public int getDoctorId() {
        return doctorId;
    }

    public void setDoctorId(int doctorId) {
        this.doctorId = doctorId;
    }

    public String getPreferredDate() {
        return preferredDate;
    }

    public void setPreferredDate(String preferredDate) {
        this.preferredDate = preferredDate;
    }

    public String getPreferredTime() {
        return preferredTime;
    }

    public void setPreferredTime(String preferredTime) {
        this.preferredTime = preferredTime;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }
}
