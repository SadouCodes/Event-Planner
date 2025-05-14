package com.example.eventplanner

// Sadou Sow (50%) & Karan Agarwal (50%)

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
            val intent = Intent(this, EventDetailActivity::class.java)
            intent.putExtra("EVENT_ID", event.id)
            startActivity(intent)
        }
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapter

        FirebaseFirestore.getInstance()
            .collection("events")
            .addSnapshotListener { snap, err ->
                if (err != null || snap == null) return@addSnapshotListener

                val list = snap.documents.mapNotNull { doc ->
                    doc.toObject(Event::class.java)
                        ?.apply { id = doc.id }
                }

                adapter.update(list)
            }

        setupBottomNavigation(this)
    }
}
