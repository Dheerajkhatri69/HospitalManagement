package com.example.hospitalmanagement.model;

import com.google.gson.annotations.SerializedName;

public class ChatResponse {
    @SerializedName("chat_id")
    private int chatId;

    @SerializedName("sender_id")
    private int senderId;

    @SerializedName("sender_name")
    private String senderName;

    @SerializedName("receiver_id")
    private int receiverId;

    @SerializedName("receiver_name")
    private String receiverName;

    @SerializedName("appointment_id")
    private Integer appointmentId;

    @SerializedName("message")
    private String message;

    @SerializedName("chat_type")
    private String chatType;

    @SerializedName("sent_at")
    private String sentAt;

    public int getChatId() {
        return chatId;
    }

    public int getSenderId() {
        return senderId;
    }

    public String getSenderName() {
        return senderName;
    }

    public int getReceiverId() {
        return receiverId;
    }

    public String getReceiverName() {
        return receiverName;
    }

    public Integer getAppointmentId() {
        return appointmentId;
    }

    public String getMessage() {
        return message;
    }

    public String getChatType() {
        return chatType;
    }

    public String getSentAt() {
        return sentAt;
    }
}
