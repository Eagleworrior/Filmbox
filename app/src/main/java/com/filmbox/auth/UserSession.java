package com.filmbox.auth;

import android.content.Context;
import android.content.SharedPreferences;

public class UserSession {
    private static final String PREF_NAME = "FilmBoxUserPref";
    private static final String KEY_IS_LOGGED_IN = "isLoggedIn";
    private static final String KEY_NAME = "fullName";
    private static final String KEY_EMAIL = "email";
    private static final String KEY_PHONE = "phone";
    private static final String KEY_PASSWORD = "password";

    private final SharedPreferences pref;
    private final SharedPreferences.Editor editor;

    public UserSession(Context context) {
        pref = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        editor = pref.edit();
    }

    public void registerUser(String name, String email, String phone, String password) {
        editor.putBoolean(KEY_IS_LOGGED_IN, true);
        editor.putString(KEY_NAME, name);
        editor.putString(KEY_EMAIL, email);
        editor.putString(KEY_PHONE, phone);
        editor.putString(KEY_PASSWORD, password);
        editor.apply();
    }

    public boolean loginUser(String emailOrPhone, String password) {
        String savedEmail = pref.getString(KEY_EMAIL, "");
        String savedPhone = pref.getString(KEY_PHONE, "");
        String savedPassword = pref.getString(KEY_PASSWORD, "");

        if ((emailOrPhone.equals(savedEmail) || emailOrPhone.equals(savedPhone)) && password.equals(savedPassword)) {
            editor.putBoolean(KEY_IS_LOGGED_IN, true);
            editor.apply();
            return true;
        }
        return false;
    }

    public boolean isLoggedIn() {
        return pref.getBoolean(KEY_IS_LOGGED_IN, false);
    }

    public String getUserName() {
        return pref.getString(KEY_NAME, "User");
    }

    public String getUserEmail() {
        return pref.getString(KEY_EMAIL, "");
    }

    public void logout() {
        editor.putBoolean(KEY_IS_LOGGED_IN, false);
        editor.apply();
    }
}
