package com.example.licentatakecare.authentication;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Pair;

public class SharedPreferencesHelper {
    private static final String PREF_NAME = "user.Authentication";
    private static final String KEY_CARD_ID = "cardId";
    private static final String KEY_PASSWORD = "password";
    private static final String KEY_IS_LOGGED_IN = "isLoggedIn";

    private SharedPreferences sharedPreferences;

    public SharedPreferencesHelper(Context context) {
        sharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
    }

    public void saveUserCredentials(String cardId, String password) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(KEY_CARD_ID, cardId);
        editor.putString(KEY_PASSWORD, password);
        editor.putBoolean(KEY_IS_LOGGED_IN, true);
        editor.apply();
    }

    public Pair<String, String> retrieveUserCredentials() {
        String cardId = sharedPreferences.getString(KEY_CARD_ID, null);
        String password = sharedPreferences.getString(KEY_PASSWORD, null);
        if (cardId != null && password != null) {
            return new Pair<>(cardId, password);
        }
        return null;
    }

    public String getCardId() {
        return sharedPreferences.getString(KEY_CARD_ID, null);
    }

    public boolean isLoggedIn() {
        return sharedPreferences.getBoolean(KEY_IS_LOGGED_IN, false);
    }
}

