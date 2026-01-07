package com.example.hospitalmanagement.model;

import com.google.gson.annotations.SerializedName;

public class PrescriptionCreate {
    @SerializedName("appointment_id")
    private int appointmentId;

    @SerializedName("patient_id")
    private int patientId;

    @SerializedName("doctor_id")
    private int doctorId;

    @SerializedName("notes")
    private String notes;

    public PrescriptionCreate(int appointmentId, int patientId, int doctorId, String notes) {
        this.appointmentId = appointmentId;
        this.patientId = patientId;
        this.doctorId = doctorId;
        this.notes = notes;
    }

    public int getAppointmentId() {
        return appointmentId;
    }

    public void setAppointmentId(int appointmentId) {
        this.appointmentId = appointmentId;
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

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }
}
