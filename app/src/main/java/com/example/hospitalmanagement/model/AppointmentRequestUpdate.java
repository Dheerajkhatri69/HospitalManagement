package com.example.hospitalmanagement.model;

import com.google.gson.annotations.SerializedName;

public class AppointmentRequestUpdate {
    @SerializedName("status")
    private String status;

    public AppointmentRequestUpdate(String status) {
        this.status = status;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
