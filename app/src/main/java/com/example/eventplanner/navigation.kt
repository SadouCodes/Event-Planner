package com.example.eventplanner

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.bottomnavigation.BottomNavigationView

fun setupBottomNavigation(currentActivity: AppCompatActivity) {
    val nav = currentActivity.findViewById<BottomNavigationView>(R.id.bottomNavigationView)

    // select the current tab without firing the listener
    nav.selectedItemId = when (currentActivity) {
        is MainActivity       -> R.id.nav_events
        is EventFormActivity  -> R.id.nav_create
        is SettingsActivity   -> R.id.nav_settings
        else                  -> R.id.nav_events
    }

    // click‐listener for user taps
    nav.setOnItemSelectedListener { item ->
        when (item.itemId) {
            R.id.nav_events -> {
                if (currentActivity !is MainActivity) {
                    currentActivity.startActivity(
                        Intent(currentActivity, MainActivity::class.java)
                    )
                    currentActivity.finish()
                }
                true
            }
            R.id.nav_create -> {
                if (currentActivity !is EventFormActivity) {
                    currentActivity.startActivity(
                        Intent(currentActivity, EventFormActivity::class.java)
                    )
                    currentActivity.finish()
                }
                true
            }
            R.id.nav_settings -> {
                if (currentActivity !is SettingsActivity) {
                    currentActivity.startActivity(
                        Intent(currentActivity, SettingsActivity::class.java)
                    )
                    currentActivity.finish()
                }
                true
            }
            else -> false
        }
    }
}

