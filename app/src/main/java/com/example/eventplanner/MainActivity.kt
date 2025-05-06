package com.example.eventplanner

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdView
import com.google.android.gms.ads.MobileAds
import com.google.android.material.bottomnavigation.BottomNavigationView

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val eventView = EventView(this)
        setContentView(eventView)

        eventView.setOnEventCreatedListener(object : EventView.OnEventCreatedListener {
            override fun onEventCreated(eventName: String, eventLocation: String) {
                Toast.makeText(this@MainActivity,
                    "Created event: $eventName at $eventLocation",
                    Toast.LENGTH_SHORT).show()

                eventView.clearInputs()
            }
        })
    }
}
