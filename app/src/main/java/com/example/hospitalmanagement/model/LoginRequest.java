package com.example.hospitalmanagement.model;

public class LoginRequest {
    @com.google.gson.annotations.SerializedName("email")
    private String email;

    @com.google.gson.annotations.SerializedName("password")
    private String password;

    public LoginRequest(String email, String password) {
        this.email = email;
        this.password = password;
    }
}
