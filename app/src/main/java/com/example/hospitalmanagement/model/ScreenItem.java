package com.example.hospitalmanagement.model;
public class ScreenItem {
    private int imageResId;
    private String heading;
    private String description;
    private String buttonText;
    private boolean isLastScreen;

    public ScreenItem(int imageResId, String heading, String description,
                      String buttonText, boolean isLastScreen) {
        this.imageResId = imageResId;
        this.heading = heading;
        this.description = description;
        this.buttonText = buttonText;
        this.isLastScreen = isLastScreen;
    }

    // Getters
    public int getImageResId() {
        return imageResId;
    }

    public String getHeading() {
        return heading;
    }

    public String getDescription() {
        return description;
    }

    public String getButtonText() {
        return buttonText;
    }

    public boolean isLastScreen() {
        return isLastScreen;
    }
}
