package com.example.hospitalmanagement.model;

import com.google.gson.annotations.SerializedName;

public class ChatCreate {
    @SerializedName("sender_id")
    private int senderId;

    @SerializedName("receiver_id")
    private int receiverId;

    @SerializedName("appointment_id")
    private Integer appointmentId;

    @SerializedName("message")
    private String message;

    @SerializedName("chat_type")
    private String chatType;

    public ChatCreate(int senderId, int receiverId, Integer appointmentId,
            String message, String chatType) {
        this.senderId = senderId;
        this.receiverId = receiverId;
        this.appointmentId = appointmentId;
        this.message = message;
        this.chatType = chatType;
    }

    public int getSenderId() {
        return senderId;
    }

    public void setSenderId(int senderId) {
        this.senderId = senderId;
    }

    public int getReceiverId() {
        return receiverId;
    }

    public void setReceiverId(int receiverId) {
        this.receiverId = receiverId;
    }

    public Integer getAppointmentId() {
        return appointmentId;
    }

    public void setAppointmentId(Integer appointmentId) {
        this.appointmentId = appointmentId;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getChatType() {
        return chatType;
    }

    public void setChatType(String chatType) {
        this.chatType = chatType;
    }
}
