package com.example.hospitalmanagement.model;

import com.google.gson.annotations.SerializedName;

public class AppointmentUpdate {
    @SerializedName("scheduled_date")
    private String scheduledDate;

    @SerializedName("scheduled_time")
    private String scheduledTime;

    @SerializedName("status")
    private String status;

    public AppointmentUpdate(String scheduledDate, String scheduledTime, String status) {
        this.scheduledDate = scheduledDate;
        this.scheduledTime = scheduledTime;
        this.status = status;
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
