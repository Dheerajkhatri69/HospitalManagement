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

    private SharedPreferences pref;
    private SharedPreferences.Editor editor;
    private Context context;

    public SessionManager(Context context) {
        this.context = context;
        pref = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        editor = pref.edit();
    }

    public void createLoginSession(int userId, String fullName, String role, String email) {
        editor.putBoolean(KEY_IS_LOGGED_IN, true);
        editor.putInt(KEY_USER_ID, userId);
        editor.putString(KEY_FULL_NAME, fullName);
        editor.putString(KEY_ROLE, role);
        editor.putString(KEY_EMAIL, email);
        editor.commit();
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

    public void logoutUser() {
        editor.clear();
        editor.commit();
    }
}
