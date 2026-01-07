package com.example.hospitalmanagement.api;

import com.example.hospitalmanagement.model.LoginRequest;
import com.example.hospitalmanagement.model.LoginResponse;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface ApiService {
    @POST("login")
    Call<LoginResponse> login(@Body LoginRequest loginRequest);
}
