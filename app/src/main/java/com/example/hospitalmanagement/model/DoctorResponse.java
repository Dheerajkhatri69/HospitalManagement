package com.example.hospitalmanagement.model;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class DoctorResponse {
    @SerializedName("doctor_id")
    private int doctorId;

    @SerializedName("user_id")
    private int userId;

    @SerializedName("full_name")
    private String fullName;

    @SerializedName("email")
    private String email;

    @SerializedName("bio")
    private String bio;

    @SerializedName("office_phone")
    private String officePhone;

    @SerializedName("office_address")
    private String officeAddress;

    @SerializedName("years_experience")
    private Integer yearsExperience;

    @SerializedName("specializations")
    private List<String> specializations;

    public int getDoctorId() {
        return doctorId;
    }

    public int getUserId() {
        return userId;
    }

    public String getFullName() {
        return fullName;
    }

    public String getEmail() {
        return email;
    }

    public String getBio() {
        return bio;
    }

    public String getOfficePhone() {
        return officePhone;
    }

    public String getOfficeAddress() {
        return officeAddress;
    }

    public Integer getYearsExperience() {
        return yearsExperience;
    }

    public List<String> getSpecializations() {
        return specializations;
    }
}
