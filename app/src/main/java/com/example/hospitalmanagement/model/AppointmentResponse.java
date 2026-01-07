package com.example.hospitalmanagement.model;

import com.google.gson.annotations.SerializedName;

public class AppointmentResponse {
    @SerializedName("appointment_id")
    private int appointmentId;

    @SerializedName("request_id")
    private int requestId;

    @SerializedName("patient_id")
    private int patientId;

    @SerializedName("patient_name")
    private String patientName;

    @SerializedName("doctor_id")
    private int doctorId;

    @SerializedName("doctor_name")
    private String doctorName;

    @SerializedName("specialization")
    private String specialization;

    @SerializedName("scheduled_date")
    private String scheduledDate;

    @SerializedName("scheduled_time")
    private String scheduledTime;

    @SerializedName("status")
    private String status;

    @SerializedName("created_at")
    private String createdAt;

    public int getAppointmentId() {
        return appointmentId;
    }

    public int getRequestId() {
        return requestId;
    }

    public int getPatientId() {
        return patientId;
    }

    public String getPatientName() {
        return patientName;
    }

    public int getDoctorId() {
        return doctorId;
    }

    public String getDoctorName() {
        return doctorName;
    }

    public String getSpecialization() {
        return specialization;
    }

    public String getScheduledDate() {
        return scheduledDate;
    }

    public String getScheduledTime() {
        return scheduledTime;
    }

    public String getStatus() {
        return status;
    }

    public String getCreatedAt() {
        return createdAt;
    }
}
