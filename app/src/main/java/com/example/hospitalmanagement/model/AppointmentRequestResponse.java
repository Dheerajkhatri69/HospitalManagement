package com.example.hospitalmanagement.model;

import com.google.gson.annotations.SerializedName;

public class AppointmentRequestResponse {
    @SerializedName("request_id")
    private int requestId;

    @SerializedName("patient_id")
    private int patientId;

    @SerializedName("patient_name")
    private String patientName;

    @SerializedName("specialization")
    private String specialization;

    @SerializedName("doctor_id")
    private Integer doctorId;

    @SerializedName("doctor_name")
    private String doctorName;

    @SerializedName("preferred_date")
    private String preferredDate;

    @SerializedName("preferred_time")
    private String preferredTime;

    @SerializedName("status")
    private String status;

    @SerializedName("notes")
    private String notes;

    @SerializedName("created_at")
    private String createdAt;

    public int getRequestId() {
        return requestId;
    }

    public int getPatientId() {
        return patientId;
    }

    public String getPatientName() {
        return patientName;
    }

    public String getSpecialization() {
        return specialization;
    }

    public Integer getDoctorId() {
        return doctorId;
    }

    public String getDoctorName() {
        return doctorName;
    }

    public String getPreferredDate() {
        return preferredDate;
    }

    public String getPreferredTime() {
        return preferredTime;
    }

    public String getStatus() {
        return status;
    }

    public String getNotes() {
        return notes;
    }

    public String getCreatedAt() {
        return createdAt;
    }
}
