package com.example.hospitalmanagement.model;

import com.google.gson.annotations.SerializedName;

public class DoctorAvailabilityResponse {
    @SerializedName("avail_id")
    private int availId;

    @SerializedName("doctor_id")
    private int doctorId;

    @SerializedName("doctor_name")
    private String doctorName;

    @SerializedName("day_of_week")
    private String dayOfWeek;

    @SerializedName("start_time")
    private String startTime;

    @SerializedName("end_time")
    private String endTime;

    @SerializedName("is_available")
    private boolean isAvailable;

    public int getAvailId() {
        return availId;
    }

    public int getDoctorId() {
        return doctorId;
    }

    public String getDoctorName() {
        return doctorName;
    }

    public String getDayOfWeek() {
        return dayOfWeek;
    }

    public String getStartTime() {
        return startTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public boolean isAvailable() {
        return isAvailable;
    }
}
