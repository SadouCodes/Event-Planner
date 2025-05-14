package com.example.eventplanner

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Intent
import android.content.res.ColorStateList
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.ads.AdView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.text.DateFormat
import java.util.*
import com.google.android.gms.ads.AdRequest
import com.google.android.material.bottomnavigation.BottomNavigationView


class EventFormActivity : AppCompatActivity() {

    private lateinit var titleInput: EditText
    private lateinit var descInput: EditText
    private lateinit var locInput: EditText
    private lateinit var hostInput: EditText
    private lateinit var guestsInput: EditText
    private lateinit var tvDate: TextView
    private lateinit var tvTime: TextView
    private lateinit var submitButton: Button

    private var selectedDateTime: Long? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        setTheme(
            if (UserPreferences.getThemeMode(this))
                R.style.Theme_EventPlanner_Dark
            else
                R.style.Theme_EventPlanner_Light
        )

        super.onCreate(savedInstanceState)
        setContentView(R.layout.base_with_nav)
        setupBottomNavigation(this)

        val bottomNav = findViewById<BottomNavigationView>(R.id.bottomNavigationView)
        val accent   = UserPreferences.getAccentColor(this)
        bottomNav.itemIconTintList = ColorStateList.valueOf(accent)
        bottomNav.itemTextColor   = ColorStateList.valueOf(accent)

        val container = findViewById<FrameLayout>(R.id.container)
        val formView = layoutInflater.inflate(R.layout.event_form, container, true)

        val adView = formView.findViewById<AdView>(R.id.adView)

        val adRequest = AdRequest.Builder().build()
        adView.loadAd(adRequest)

        titleInput   = formView.findViewById(R.id.inputTitle)
        descInput    = formView.findViewById(R.id.inputDescription)
        locInput     = formView.findViewById(R.id.inputLocation)
        hostInput    = formView.findViewById(R.id.inputHostEmail)
        guestsInput  = formView.findViewById(R.id.inputGuests)
        tvDate       = formView.findViewById(R.id.tvDate)
        tvTime       = formView.findViewById(R.id.tvTime)
        submitButton = formView.findViewById(R.id.buttonSubmit)

        submitButton.backgroundTintList =
            ColorStateList.valueOf(UserPreferences.getAccentColor(this))

        tvDate.setOnClickListener {
            val cal = Calendar.getInstance()
            DatePickerDialog(
                this,
                { _, year, month, day ->
                    cal.set(year, month, day)
                    selectedDateTime = cal.timeInMillis
                    tvDate.text = "${month + 1}/$day/$year"
                },
                cal.get(Calendar.YEAR),
                cal.get(Calendar.MONTH),
                cal.get(Calendar.DAY_OF_MONTH)
            ).show()
        }

        tvTime.setOnClickListener {
            val cal = Calendar.getInstance()
            selectedDateTime?.let { cal.timeInMillis = it }
            TimePickerDialog(
                this,
                { _, hour, minute ->
                    cal.set(Calendar.HOUR_OF_DAY, hour)
                    cal.set(Calendar.MINUTE, minute)
                    selectedDateTime = cal.timeInMillis
                    val hour12 = if (hour % 12 == 0) 12 else hour % 12
                    val ampm = if (hour < 12) "AM" else "PM"
                    tvTime.text = String.format("%d:%02d %s", hour12, minute, ampm)
                },
                cal.get(Calendar.HOUR_OF_DAY),
                cal.get(Calendar.MINUTE),
                false
            ).show()
        }

        submitButton.setOnClickListener {
            if (!validateForm()) return@setOnClickListener

            val dateTime = selectedDateTime ?: System.currentTimeMillis()
            val event = Event(
                title       = titleInput.text.toString().trim(),
                description = descInput.text.toString().trim(),
                location    = locInput.text.toString().trim(),
                hostEmail   = hostInput.text.toString().trim(),
                dateTime    = dateTime,
                guestEmails = guestsInput.text.toString()
                    .split(",")
                    .mapNotNull { it.trim().takeIf(String::isNotEmpty) }
            )

            CoroutineScope(Dispatchers.IO).launch {
                try {
                    EventRepository.addEvent(event)
                    runOnUiThread {
                        Toast.makeText(
                            this@EventFormActivity,
                            "Event created!",
                            Toast.LENGTH_SHORT
                        ).show()

                        val df      = DateFormat.getDateTimeInstance()
                        val subject = "You're invited: ${event.title}"
                        val body    = """
                            Hi,

                            I'd like to invite you to "${event.title}".

                            When: ${df.format(Date(event.dateTime))}
                            Where: ${event.location}

                            Details: ${event.description}
                        """.trimIndent()

                        val addresses = event.guestEmails.joinToString(",")
                        val emailUri  = Uri.parse("mailto:$addresses")

                        val emailIntentSendTo = Intent(Intent.ACTION_SENDTO, emailUri).apply {
                            putExtra(Intent.EXTRA_SUBJECT, subject)
                            putExtra(Intent.EXTRA_TEXT, body)
                        }

                        Log.d("EventFormActivity", "Email URI: $emailUri")

                        if (emailIntentSendTo.resolveActivity(packageManager) != null) {
                            startActivity(Intent.createChooser(emailIntentSendTo, "Send invites via"))
                        } else {
                            val fallback = Intent(Intent.ACTION_SEND).apply {
                                type = "message/rfc822"
                                putExtra(Intent.EXTRA_EMAIL, event.guestEmails.toTypedArray())
                                putExtra(Intent.EXTRA_SUBJECT, subject)
                                putExtra(Intent.EXTRA_TEXT, body)
                            }
                            if (fallback.resolveActivity(packageManager) != null) {
                                startActivity(Intent.createChooser(fallback, "Send invites via"))
                            } else {
                                Toast.makeText(
                                    this@EventFormActivity,
                                    "No email client installed",
                                    Toast.LENGTH_LONG
                                ).show()
                            }
                        }

                        clearForm()
                    }
                } catch (e: Exception) {
                    runOnUiThread {
                        Toast.makeText(
                            this@EventFormActivity,
                            "Failed to create event: ${e.message}",
                            Toast.LENGTH_LONG
                        ).show()
                        e.printStackTrace()
                    }
                }
            }
        }
    }

    private fun validateForm(): Boolean {
        return when {
            titleInput.text.isNullOrBlank() -> {
                titleInput.error = "Title is required"
                false
            }
            locInput.text.isNullOrBlank() -> {
                locInput.error = "Location is required"
                false
            }
            hostInput.text.isNullOrBlank() -> {
                hostInput.error = "Host email is required"
                false
            }
            else -> true
        }
    }

    private fun clearForm() {
        titleInput.text.clear()
        descInput.text.clear()
        locInput.text.clear()
        hostInput.text.clear()
        guestsInput.text.clear()
        tvDate.text = "Select date"
        tvTime.text = "Select time"
        selectedDateTime = null
    }
}
