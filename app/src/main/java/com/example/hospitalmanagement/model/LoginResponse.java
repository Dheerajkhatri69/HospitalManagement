package com.example.hospitalmanagement.model;

import com.google.gson.annotations.SerializedName;

public class LoginResponse {
    @SerializedName("user_id")
    private int userId;

    @SerializedName("full_name")
    private String fullName;

    @SerializedName("email")
    private String email;

    @SerializedName("role")
    private String role;

    @SerializedName("message")
    private String message;

    public int getUserId() {
        return userId;
    }

    public String getFullName() {
        return fullName;
    }

    public String getEmail() {
        return email;
    }

    public String getRole() {
        return role;
    }

    public String getMessage() {
        return message;
    }
}
