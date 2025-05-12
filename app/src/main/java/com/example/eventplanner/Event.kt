package com.example.eventplanner

data class Event(
    var id: String? = null,
    var title: String? = null,
    var description: String? = null,
    var location: String? = null,
    var dateTime: Long = 0, // timestamp
    var hostEmail: String? = null,
    var guestEmails: List<String> = listOf(),
    var comments: List<String> = listOf()
)