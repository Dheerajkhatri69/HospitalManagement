package com.example.hospitalmanagement.model;

import com.google.gson.annotations.SerializedName;

public class PrescriptionUpdate {
    @SerializedName("notes")
    private String notes;

    public PrescriptionUpdate(String notes) {
        this.notes = notes;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }
}
