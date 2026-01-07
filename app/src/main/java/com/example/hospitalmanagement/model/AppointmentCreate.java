package com.example.hospitalmanagement.model;

import com.google.gson.annotations.SerializedName;

public class AppointmentCreate {
    @SerializedName("request_id")
    private int requestId;

    @SerializedName("patient_id")
    private int patientId;

    @SerializedName("doctor_id")
    private int doctorId;

    @SerializedName("scheduled_date")
    private String scheduledDate;

    @SerializedName("scheduled_time")
    private String scheduledTime;

    @SerializedName("status")
    private String status;

    public AppointmentCreate(int requestId, int patientId, int doctorId,
            String scheduledDate, String scheduledTime, String status) {
        this.requestId = requestId;
        this.patientId = patientId;
        this.doctorId = doctorId;
        this.scheduledDate = scheduledDate;
        this.scheduledTime = scheduledTime;
        this.status = status;
    }

    public int getRequestId() {
        return requestId;
    }

    public void setRequestId(int requestId) {
        this.requestId = requestId;
    }

    public int getPatientId() {
        return patientId;
    }

    public void setPatientId(int patientId) {
        this.patientId = patientId;
    }

    public int getDoctorId() {
        return doctorId;
    }

    public void setDoctorId(int doctorId) {
        this.doctorId = doctorId;
    }

    public String getScheduledDate() {
        return scheduledDate;
    }

    public void setScheduledDate(String scheduledDate) {
        this.scheduledDate = scheduledDate;
    }

    public String getScheduledTime() {
        return scheduledTime;
    }

    public void setScheduledTime(String scheduledTime) {
        this.scheduledTime = scheduledTime;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
