package com.example.eventplanner

import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class EventFormActivity : AppCompatActivity() {

    private lateinit var titleInput: EditText
    private lateinit var descInput: EditText
    private lateinit var locInput: EditText
    private lateinit var hostInput: EditText
    private lateinit var guestsInput: EditText
    private lateinit var submitButton: Button

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

        val container = findViewById<FrameLayout>(R.id.container)
        val formView = layoutInflater.inflate(R.layout.event_form, container, true)

        titleInput = formView.findViewById(R.id.inputTitle)
        descInput = formView.findViewById(R.id.inputDescription)
        locInput = formView.findViewById(R.id.inputLocation)
        hostInput = formView.findViewById(R.id.inputHostEmail)
        guestsInput = formView.findViewById(R.id.inputGuests)
        submitButton = formView.findViewById(R.id.buttonSubmit)

        submitButton.setOnClickListener {
            if (validateForm()) {
                val event = Event(
                    title = titleInput.text.toString().trim(),
                    description = descInput.text.toString().trim(),
                    location = locInput.text.toString().trim(),
                    hostEmail = hostInput.text.toString().trim(),
                    dateTime = System.currentTimeMillis(),
                    guestEmails = guestsInput.text.toString()
                        .split(",").mapNotNull { it.trim().takeIf(String::isNotEmpty) },
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
    }
}
