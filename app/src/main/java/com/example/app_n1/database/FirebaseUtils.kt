package com.example.app_n1.database

import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

object FirebaseUtils {
    private val database: FirebaseDatabase = FirebaseDatabase.getInstance()

    // Hàm lấy tham chiếu tới Firebase Realtime Database
    fun getDatabaseReference(): DatabaseReference {
        return database.reference
    }
}
