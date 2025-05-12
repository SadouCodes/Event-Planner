package com.example.eventplanner

import android.app.Application
import com.google.android.gms.ads.MobileAds
import com.google.firebase.FirebaseApp

class MyApp : Application() {
    override fun onCreate() {
        super.onCreate()
        FirebaseApp.initializeApp(this)
        MobileAds.initialize(this) { /* no-op callback */ }
    }
}
