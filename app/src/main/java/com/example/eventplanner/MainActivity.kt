package com.example.eventplanner

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.google.firebase.firestore.FirebaseFirestore


class MainActivity : AppCompatActivity() {
    fun getLayoutResourceId() = R.layout.activity_main

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.base_with_nav)

        val db = FirebaseFirestore.getInstance()
        Log.d("Firebase", "Firestore instance: $db")

        // Themes, satisfies 1 of the local data requirement
        setTheme(
            if (UserPreferences.getThemeMode(this))
                R.style.Theme_EventPlanner_Dark
            else
                R.style.Theme_EventPlanner_Light
        )

        // Colors, satisfies the 2nd local data requirement
        val primaryColor = UserPreferences.getPrimaryColor(this)

        layoutInflater.inflate(R.layout.activity_main, findViewById(R.id.container), true)
        setupBottomNavigation(this)

    }
}
