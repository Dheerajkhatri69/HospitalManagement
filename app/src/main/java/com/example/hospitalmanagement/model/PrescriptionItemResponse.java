package com.example.hospitalmanagement.model;

import com.google.gson.annotations.SerializedName;

public class PrescriptionItemResponse {
    @SerializedName("item_id")
    private int itemId;

    @SerializedName("prescription_id")
    private int prescriptionId;

    @SerializedName("medication_name")
    private String medicationName;

    @SerializedName("dosage")
    private String dosage;

    @SerializedName("frequency")
    private String frequency;

    @SerializedName("duration")
    private String duration;

    @SerializedName("instructions")
    private String instructions;

    public int getItemId() {
        return itemId;
    }

    public int getPrescriptionId() {
        return prescriptionId;
    }

    public String getMedicationName() {
        return medicationName;
    }

    public String getDosage() {
        return dosage;
    }

    public String getFrequency() {
        return frequency;
    }

    public String getDuration() {
        return duration;
    }

    public String getInstructions() {
        return instructions;
    }
}
