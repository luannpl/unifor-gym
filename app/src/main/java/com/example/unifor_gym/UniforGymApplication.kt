package com.example.unifor_gym

import android.app.Application
import com.google.firebase.FirebaseApp

class UniforGymApplication : Application() {
    override fun onCreate() {
        super.onCreate()

        // Initialize Firebase
        FirebaseApp.initializeApp(this)
    }
}