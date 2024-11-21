package com.example.app_n1.dao

import com.example.app_n1.database.FirebaseUtils
import com.example.app_n1.models.Food
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class MealDao {
    private val firestore = FirebaseUtils.getDatabaseReference()


}
