package com.example.eventplanner

class Event {
    var id: String? = null
    var title: String? = null
    var description: String? = null
    var location: String? = null
    var dateTime: Long = 0 // timestamp
    var hostEmail: String? = null

    constructor()

    constructor(
        id: String?, title: String?, description: String?, location: String?,
        dateTime: Long, hostEmail: String?
    ) {
        this.id = id
        this.title = title
        this.description = description
        this.location = location
        this.dateTime = dateTime
        this.hostEmail = hostEmail
    }
}