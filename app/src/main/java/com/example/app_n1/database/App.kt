package com.example.app_n1

import android.app.Application
import com.google.firebase.database.FirebaseDatabase

class App : Application() {
    override fun onCreate() {
        super.onCreate()
        // Kích hoạt chế độ lưu trữ ngoại tuyến
        FirebaseDatabase.getInstance().setPersistenceEnabled(true)
    }
}
