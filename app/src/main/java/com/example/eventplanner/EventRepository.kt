package com.example.eventplanner

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.toObject
import kotlinx.coroutines.tasks.await

object EventRepository {
    private val db = FirebaseFirestore.getInstance()
    private val eventsRef = db.collection("events")

    suspend fun addEvent(event: Event) {
        val newRef = eventsRef.document()
        event.id = newRef.id
        newRef.set(event).await()
    }

    suspend fun getEvents(): List<Event> {
        val snapshot = eventsRef.get().await()
        return snapshot.documents.mapNotNull { it.toObject<Event>() }
    }

    suspend fun updateEvent(event: Event) {
        event.id?.let { id ->
            eventsRef.document(id).set(event).await()
        }
    }

    suspend fun deleteEvent(eventId: String) {
        eventsRef.document(eventId).delete().await()
    }
}
