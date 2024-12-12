package com.example.app_n1.models

import com.example.app_n1.R
import java.sql.Date

data class User(
    val userId: String = "",  // unique ID for the user
    val name: String = "",
    val email: String = "",
    val password : String = ""
)



data class UserInfo(
    val userId: String = "",
    val age: Int = 0,
    val weight: Double = 0.0,
    val height: Double = 0.0,
    val kcal : Long = 0L,
    val gender : String = ""
)

data class FoodUser(
    val foodId: String = "",   // unique ID for the food entry
    val userId: String = "",   // link to User
    val name: String = "",     // food name
    val calories: Double = 0.0,
    val protein: Double = 0.0,
    val fat: Double = 0.0,
    val carbohydrates: Double = 0.0
)
data class Food(
    val foodId: String = "",
    val name: String = "",
    val calories: Int = 0,
    val protein: Double = 0.0,
    val fat: Double = 0.0,
    val carbs: Double = 0.0,
)
data class DailyLog(
    val logId: String = "",
    val userId: String = "",
    val date: String = "",
    val meals: List<Meal> = listOf(),
    val exercises: List<Exercise> = listOf()
)

data class Exercise(
    val exerciseId: String = "",
    val name: String = "",
    val caloriesBurned: Int = 0
)

data class Meal(
    val mealId: String = "",
    val mealName: String = "",
    var foods: List<Food> = listOf()
)
data class WeightLog(
    val logId: String = "",      // unique ID for each weight log entry
    val userId: String = "",     // link to User
    val weight: Double = 0.0,    // weight of the user at the time of log
    val date: Long = 0L          // timestamp for the log entry (in milliseconds)
)