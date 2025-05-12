package com.example.eventplanner

import android.content.Context
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.content.ContextCompat

object UserPreferences {
    private const val PREFS_NAME = "event_prefs"
    private const val KEY_THEME_MODE = "theme_mode"
    private const val KEY_PRIMARY_COLOR = "primary_color"

    fun saveThemeMode(context: Context, isDarkMode: Boolean) {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        prefs.edit().putBoolean(KEY_THEME_MODE, isDarkMode).apply()
    }

    fun getThemeMode(context: Context): Boolean {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        return prefs.getBoolean(KEY_THEME_MODE, false)
    }

    fun savePrimaryColor(context: Context, colorName: String) {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        prefs.edit().putString(KEY_PRIMARY_COLOR, colorName).apply()
    }

    fun getPrimaryColor(context: Context): String {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        return prefs.getString(KEY_PRIMARY_COLOR, "blue") ?: "blue"
    }

    fun applyThemeMode(darkMode: Boolean) {
        AppCompatDelegate.setDefaultNightMode(
            if (darkMode) AppCompatDelegate.MODE_NIGHT_YES
            else AppCompatDelegate.MODE_NIGHT_NO
        )
    }


    fun getAccentColor(context: Context): Int {
        return when (getPrimaryColor(context)) {
            "green" -> ContextCompat.getColor(context, R.color.green)
            "red"   -> ContextCompat.getColor(context, R.color.red)
            else    -> ContextCompat.getColor(context, R.color.blue)
        }
    }


}
