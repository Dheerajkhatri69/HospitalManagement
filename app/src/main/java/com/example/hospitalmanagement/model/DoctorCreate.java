package com.example.hospitalmanagement.model;

import com.google.gson.annotations.SerializedName;

public class DoctorCreate {
    @SerializedName("user_id")
    private int userId;

    @SerializedName("bio")
    private String bio;

    @SerializedName("office_phone")
    private String officePhone;

    @SerializedName("office_address")
    private String officeAddress;

    @SerializedName("years_experience")
    private Integer yearsExperience;

    public DoctorCreate(int userId, String bio, String officePhone, String officeAddress, Integer yearsExperience) {
        this.userId = userId;
        this.bio = bio;
        this.officePhone = officePhone;
        this.officeAddress = officeAddress;
        this.yearsExperience = yearsExperience;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getBio() {
        return bio;
    }

    public void setBio(String bio) {
        this.bio = bio;
    }

    public String getOfficePhone() {
        return officePhone;
    }

    public void setOfficePhone(String officePhone) {
        this.officePhone = officePhone;
    }

    public String getOfficeAddress() {
        return officeAddress;
    }

    public void setOfficeAddress(String officeAddress) {
        this.officeAddress = officeAddress;
    }

    public Integer getYearsExperience() {
        return yearsExperience;
    }

    public void setYearsExperience(Integer yearsExperience) {
        this.yearsExperience = yearsExperience;
    }
}
