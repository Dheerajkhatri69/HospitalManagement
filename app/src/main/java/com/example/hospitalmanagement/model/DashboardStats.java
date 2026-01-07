package com.example.hospitalmanagement.model;

import com.google.gson.annotations.SerializedName;

public class DashboardStats {
    @SerializedName("total_patients")
    private int totalPatients;

    @SerializedName("total_doctors")
    private int totalDoctors;

    @SerializedName("upcoming_appointments")
    private int upcomingAppointments;

    @SerializedName("pending_requests")
    private int pendingRequests;

    @SerializedName("today_appointments")
    private int todayAppointments;

    public int getTotalPatients() {
        return totalPatients;
    }

    public int getTotalDoctors() {
        return totalDoctors;
    }

    public int getUpcomingAppointments() {
        return upcomingAppointments;
    }

    public int getPendingRequests() {
        return pendingRequests;
    }

    public int getTodayAppointments() {
        return todayAppointments;
    }
}
