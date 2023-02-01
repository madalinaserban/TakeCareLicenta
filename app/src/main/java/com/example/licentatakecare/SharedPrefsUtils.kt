package com.example.licentatakecare

import android.content.Context

object SharedPrefsUtils {
    private const val MY_SHARED_PREFS = "MY_SHARED_PREFS"
    private const val ARG_ACCESS_TOKEN = "accessToken"

    fun saveAccessToken(context: Context, token: String) {
        val sharedPrefs = context.getSharedPreferences(MY_SHARED_PREFS, Context.MODE_PRIVATE)
        with(sharedPrefs.edit()) {
            putString(ARG_ACCESS_TOKEN, token)
            apply()
        }
    }

    fun getAccessToken(context: Context): String? {
        return context.getSharedPreferences(MY_SHARED_PREFS, Context.MODE_PRIVATE)
            ?.getString(ARG_ACCESS_TOKEN, null)
    }

    fun removeAccessToken(context: Context) {
        val sharedPrefs = context.getSharedPreferences(MY_SHARED_PREFS, Context.MODE_PRIVATE)
        with(sharedPrefs.edit()) {
            remove(ARG_ACCESS_TOKEN)
            apply()
        }
    }
}