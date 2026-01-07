package com.example.hospitalmanagement.model;

public class Appointment {
    private String id;
    private String patientName;
    private String time;
    private String status;
    private String reason;
    private String roomNumber;
    private int patientImage;

    public Appointment() {
        // Default constructor
    }

    public Appointment(String id, String patientName, String time, String status,
                       String reason, String roomNumber, int patientImage) {
        this.id = id;
        this.patientName = patientName;
        this.time = time;
        this.status = status;
        this.reason = reason;
        this.roomNumber = roomNumber;
        this.patientImage = patientImage;
    }

    // Getters and Setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getPatientName() {
        return patientName;
    }

    public void setPatientName(String patientName) {
        this.patientName = patientName;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public String getRoomNumber() {
        return roomNumber;
    }

    public void setRoomNumber(String roomNumber) {
        this.roomNumber = roomNumber;
    }

    public int getPatientImage() {
        return patientImage;
    }

    public void setPatientImage(int patientImage) {
        this.patientImage = patientImage;
    }
}