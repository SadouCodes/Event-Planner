package com.example.eventplanner

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.FrameLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.FirebaseApp
import com.google.firebase.firestore.FirebaseFirestore
import com.google.android.material.bottomnavigation.BottomNavigationView
import android.content.res.ColorStateList


class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {

        setTheme(
            if (UserPreferences.getThemeMode(this))
                R.style.Theme_EventPlanner_Dark
            else
                R.style.Theme_EventPlanner_Light
        )

        super.onCreate(savedInstanceState)
        setContentView(R.layout.base_with_nav)

        val bottomNav = findViewById<BottomNavigationView>(R.id.bottomNavigationView)
        val accent   = UserPreferences.getAccentColor(this)
        bottomNav.itemIconTintList = ColorStateList.valueOf(accent)
        bottomNav.itemTextColor   = ColorStateList.valueOf(accent)

        FirebaseApp.initializeApp(this)



        val container = findViewById<FrameLayout>(R.id.container)
        layoutInflater.inflate(R.layout.activity_main, container, true)

        val recyclerView = container.findViewById<RecyclerView>(R.id.recyclerViewEvents)
        val adapter = EventAdapter(emptyList()) { event ->
            // On click, open detail screen
            val intent = Intent(this, EventDetailActivity::class.java)
            intent.putExtra("EVENT_ID", event.id)
            startActivity(intent)
        }
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapter

        // 3) Listen for real-time updates from Firestore
        FirebaseFirestore.getInstance()
            .collection("events")
            .addSnapshotListener { snap, err ->
                if (err != null || snap == null) return@addSnapshotListener

                val list = snap.documents.mapNotNull { doc ->
                    doc.toObject(Event::class.java)
                        // stash the Firestore ID onto the Event
                        ?.apply { id = doc.id }
                }

                // DEBUG: verify IDs are coming through
              //  list.forEach { Log.d("MainActivity", "Loaded event ${it.title} with id=${it.id}") }

                adapter.update(list)
            }

        // 4) Finally, wire up bottom navigation
        setupBottomNavigation(this)
    }
}
