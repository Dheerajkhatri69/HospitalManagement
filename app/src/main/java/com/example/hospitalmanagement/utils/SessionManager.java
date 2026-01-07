package com.example.hospitalmanagement.utils;

import android.content.Context;
import android.content.SharedPreferences;

public class SessionManager {
    private static final String PREF_NAME = "HospitalAppSession";
    private static final String KEY_IS_LOGGED_IN = "isLoggedIn";
    private static final String KEY_USER_ID = "userId";
    private static final String KEY_FULL_NAME = "fullName";
    private static final String KEY_ROLE = "role";
    private static final String KEY_EMAIL = "email";
    private static final String KEY_PATIENT_ID = "patientId";
    private static final String KEY_DOCTOR_ID = "doctorId";

    private SharedPreferences pref;
    private SharedPreferences.Editor editor;
    private Context context;

    public SessionManager(Context context) {
        this.context = context;
        pref = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        editor = pref.edit();
    }

    public void createLoginSession(int userId, String fullName, String role, String email, Integer patientId,
            Integer doctorId) {
        editor.putBoolean(KEY_IS_LOGGED_IN, true);
        editor.putInt(KEY_USER_ID, userId);
        editor.putString(KEY_FULL_NAME, fullName);
        editor.putString(KEY_ROLE, role);
        editor.putString(KEY_EMAIL, email);

        if (patientId != null) {
            editor.putInt(KEY_PATIENT_ID, patientId);
        }
        if (doctorId != null) {
            editor.putInt(KEY_DOCTOR_ID, doctorId);
        }

        editor.commit();
    }

    // Overload for backward compatibility
    public void createLoginSession(int userId, String fullName, String role, String email) {
        createLoginSession(userId, fullName, role, email, null, null);
    }

    public boolean isLoggedIn() {
        return pref.getBoolean(KEY_IS_LOGGED_IN, false);
    }

    public String getRole() {
        return pref.getString(KEY_ROLE, "");
    }

    public int getUserId() {
        return pref.getInt(KEY_USER_ID, -1);
    }

    public String getFullName() {
        return pref.getString(KEY_FULL_NAME, "");
    }

    public String getEmail() {
        return pref.getString(KEY_EMAIL, "");
    }

    public Integer getPatientId() {
        if (pref.contains(KEY_PATIENT_ID)) {
            return pref.getInt(KEY_PATIENT_ID, -1);
        }
        return null;
    }

    public Integer getDoctorId() {
        if (pref.contains(KEY_DOCTOR_ID)) {
            return pref.getInt(KEY_DOCTOR_ID, -1);
        }
        return null;
    }

    public void logoutUser() {
        editor.clear();
        editor.commit();
    }
}
