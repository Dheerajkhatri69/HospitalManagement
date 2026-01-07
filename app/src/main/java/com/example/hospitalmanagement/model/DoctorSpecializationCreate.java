package com.example.hospitalmanagement.model;

import com.google.gson.annotations.SerializedName;

public class DoctorSpecializationCreate {
    @SerializedName("doctor_id")
    private int doctorId;

    @SerializedName("spec_id")
    private int specId;

    public DoctorSpecializationCreate(int doctorId, int specId) {
        this.doctorId = doctorId;
        this.specId = specId;
    }

    public int getDoctorId() {
        return doctorId;
    }

    public void setDoctorId(int doctorId) {
        this.doctorId = doctorId;
    }

    public int getSpecId() {
        return specId;
    }

    public void setSpecId(int specId) {
        this.specId = specId;
    }
}
