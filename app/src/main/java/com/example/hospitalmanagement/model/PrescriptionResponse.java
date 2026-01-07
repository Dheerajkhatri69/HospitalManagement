package com.example.hospitalmanagement.model;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class PrescriptionResponse {
    @SerializedName("prescription_id")
    private int prescriptionId;

    @SerializedName("appointment_id")
    private int appointmentId;

    @SerializedName("patient_id")
    private int patientId;

    @SerializedName("patient_name")
    private String patientName;

    @SerializedName("doctor_id")
    private int doctorId;

    @SerializedName("doctor_name")
    private String doctorName;

    @SerializedName("notes")
    private String notes;

    @SerializedName("prescribed_at")
    private String prescribedAt;

    @SerializedName("items")
    private List<PrescriptionItemResponse> items;

    public int getPrescriptionId() {
        return prescriptionId;
    }

    public int getAppointmentId() {
        return appointmentId;
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

    public String getNotes() {
        return notes;
    }

    public String getPrescribedAt() {
        return prescribedAt;
    }

    public List<PrescriptionItemResponse> getItems() {
        return items;
    }
}
