package com.example.pushmessage

import android.app.Application
import com.google.firebase.FirebaseApp

class MyApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        // Firebase 초기화
        if (!FirebaseApp.getApps(this).isEmpty()) {
            FirebaseApp.initializeApp(this)
        }

    }
}
