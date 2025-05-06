package com.example.eventplanner

import android.content.Context

object UserPreferences {
    private const val PREFS_NAME = "event_prefs"

    fun saveUserPreferences(context: Context, colorPreference: String?) {
        val sharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putString("color", colorPreference)
        editor.apply()
    }

    fun getUserPreferences(context: Context): String {
        val sharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        return sharedPreferences.getString("color", "default")!!
    }
}