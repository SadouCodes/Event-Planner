package com.example.eventplanner

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.Toast
import com.google.android.material.textfield.TextInputEditText

class EventView(context: Context) : LinearLayout(context) {
    private val eventNameInput: TextInputEditText
    private val eventLocationInput: TextInputEditText
    private val createEventButton: Button

    interface OnEventCreatedListener {
        fun onEventCreated(eventName: String, eventLocation: String)
    }

    private var listener: OnEventCreatedListener? = null

    init {
        val view = LayoutInflater.from(context).inflate(R.layout.event_form, this, true)

        eventNameInput = view.findViewById(R.id.event_name)
        eventLocationInput = view.findViewById(R.id.event_location)
        createEventButton = view.findViewById(R.id.create_event_button)

        createEventButton.setOnClickListener {
            val eventName = eventNameInput.text.toString()
            val eventLocation = eventLocationInput.text.toString()

            if (eventName.isEmpty() || eventLocation.isEmpty()) {
                Toast.makeText(context, "Please fill in all fields", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            listener?.onEventCreated(eventName, eventLocation)
        }
    }

    fun setOnEventCreatedListener(listener: OnEventCreatedListener) {
        this.listener = listener
    }

    fun clearInputs() {
        eventNameInput.text?.clear()
        eventLocationInput.text?.clear()
    }
}