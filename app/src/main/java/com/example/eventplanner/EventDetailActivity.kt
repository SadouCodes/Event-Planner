package com.example.eventplanner

import android.content.Intent
import android.content.res.ColorStateList
import android.net.Uri
import android.os.Bundle
import android.provider.CalendarContract
import android.widget.Button
import android.widget.FrameLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.lifecycle.lifecycleScope
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.text.DateFormat
import java.util.*

class EventDetailActivity : AppCompatActivity() {

    private lateinit var tvTitle: TextView
    private lateinit var tvDescription: TextView
    private lateinit var tvHost: TextView
    private lateinit var tvDateTime: TextView
    private lateinit var tvLocation: TextView
    private lateinit var btnAddToCalendar: Button
    private lateinit var currentEvent: Event

    override fun onCreate(savedInstanceState: Bundle?) {
        // Apply saved dark/light mode
        setTheme(
            if (UserPreferences.getThemeMode(this))
                R.style.Theme_EventPlanner_Dark
            else
                R.style.Theme_EventPlanner_Light
        )
        AppCompatDelegate.setDefaultNightMode(
            if (UserPreferences.getThemeMode(this))
                AppCompatDelegate.MODE_NIGHT_YES
            else
                AppCompatDelegate.MODE_NIGHT_NO
        )


        super.onCreate(savedInstanceState)

        Toast.makeText(this,
            "DetailActivity for ID=${intent.getStringExtra("EVENT_ID")}",
            Toast.LENGTH_LONG
        ).show()

        setContentView(R.layout.base_with_nav)
        setupBottomNavigation(this)

        // Inflate detail layout
        val container = findViewById<FrameLayout>(R.id.container)
        layoutInflater.inflate(R.layout.activity_event_detail, container, true)

        // Bind views
        tvTitle          = container.findViewById(R.id.tvEventTitle)
        tvDescription    = container.findViewById(R.id.tvEventDescription)
        tvHost           = container.findViewById(R.id.tvHostEmail)
        tvDateTime       = container.findViewById(R.id.tvDateTime)
        tvLocation       = container.findViewById(R.id.tvLocation)
        btnAddToCalendar = container.findViewById(R.id.buttonAddToCalendar)

        btnAddToCalendar.backgroundTintList =
            ColorStateList.valueOf(UserPreferences.getAccentColor(this))


        // Fetch the Event by ID
        val eventId = intent.getStringExtra("EVENT_ID") ?: return
        lifecycleScope.launch {
            try {
                val snapshot = FirebaseFirestore.getInstance()
                    .collection("events")
                    .document(eventId)
                    .get()
                    .await()
                snapshot.toObject(Event::class.java)?.let { event ->
                    currentEvent = event
                    populateFields(event)
                }
            } catch (e: Exception) {
                Toast.makeText(this@EventDetailActivity,
                    "Error loading event", Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun populateFields(event: Event) {
        // Display event data
        tvTitle.text       = event.title
        tvDescription.text = event.description
        tvHost.text        = "Host: ${event.hostEmail}"
        val df = DateFormat.getDateTimeInstance()
        tvDateTime.text    = "Date & Time: ${df.format(Date(event.dateTime))}"
        tvLocation.text    = "Location: ${event.location}"

        // Calendar Intent
        btnAddToCalendar.setOnClickListener {
            val calIntent = Intent(Intent.ACTION_INSERT).apply {
                data = CalendarContract.Events.CONTENT_URI
                putExtra(CalendarContract.Events.TITLE, event.title)
                putExtra(CalendarContract.Events.EVENT_LOCATION, event.location)
                putExtra(CalendarContract.Events.DESCRIPTION, event.description)
                putExtra(CalendarContract.EXTRA_EVENT_BEGIN_TIME, event.dateTime)
            }
            startActivity(calIntent)
        }


    }
}
