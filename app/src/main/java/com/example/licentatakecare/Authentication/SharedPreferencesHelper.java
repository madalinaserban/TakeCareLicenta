package com.example.licentatakecare.Authentication;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Pair;

public class SharedPreferencesHelper {
    private static final String PREF_NAME = "com.example.licentatakecare.Authentication";
    private static final String KEY_CARD_ID = "cardId";
    private static final String KEY_PASSWORD = "password";

    private SharedPreferences sharedPreferences;

    public SharedPreferencesHelper(Context context) {
        sharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
    }

    public void saveUserCredentials(String cardId, String password) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(KEY_CARD_ID, cardId);
        editor.putString(KEY_PASSWORD, password);
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
}
