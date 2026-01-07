package com.example.hospitalmanagement.model;

import com.google.gson.annotations.SerializedName;

public class SpecializationResponse {
    @SerializedName("spec_id")
    private int specId;

    @SerializedName("name")
    private String name;

    @SerializedName("description")
    private String description;

    public int getSpecId() {
        return specId;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }
}
