package com.example.app_n1.database

import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

object FirebaseUtils {
    private var database: FirebaseDatabase? = null

    // Hàm lấy tham chiếu tới Firebase Realtime Database
    fun getDatabaseReference(): DatabaseReference {
        if (database == null) {
            database = FirebaseDatabase.getInstance()
            database!!.setPersistenceEnabled(true) // Kích hoạt chế độ lưu trữ ngoại tuyến
        }
        return database!!.reference
    }
}
