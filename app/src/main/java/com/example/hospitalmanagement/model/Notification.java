package com.example.hospitalmanagement.model;

import com.google.gson.annotations.SerializedName;

public class Notification {
    @SerializedName("notification_id")
    private int notificationId;

    @SerializedName("user_id")
    private int userId;

    @SerializedName("title")
    private String title;

    @SerializedName("message")
    private String message;

    @SerializedName("is_read")
    private boolean isRead;

    @SerializedName("created_at")
    private String createdAt;

    // Default constructor for Gson
    public Notification() {}

    public Notification(String title, String message, String createdAt, boolean isRead) {
        this.title = title;
        this.message = message;
        this.createdAt = createdAt;
        this.isRead = isRead;
    }

    public int getNotificationId() {
        return notificationId;
    }

    public void setNotificationId(int notificationId) {
        this.notificationId = notificationId;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public boolean isRead() {
        return isRead;
    }

    public void setRead(boolean read) {
        isRead = read;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }
    
    // Helper for adapter compatibility
    public String getTime() {
        return createdAt; 
    }
}
