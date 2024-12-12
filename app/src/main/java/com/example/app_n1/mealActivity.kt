package com.example.app_n1

import android.graphics.Color
import android.os.Bundle
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.app_n1.Adapter.itemFoodAdapter
import com.example.app_n1.models.DailyLog
import com.example.app_n1.models.Food
import com.example.app_n1.models.Meal
import com.google.firebase.database.*
import java.util.UUID
import java.util.Date
import java.util.Locale

class mealActivity : AppCompatActivity() {
    private lateinit var backButton: ImageView

    private lateinit var foodList: MutableList<Food>
    private lateinit var foodAdapter: itemFoodAdapter
    private lateinit var databaseReference: DatabaseReference

    private lateinit var cardBreakfast: CardView
    private lateinit var cardLunch: CardView
    private lateinit var cardDinner: CardView
    private var selectedMealName: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_meal)

        // Khởi tạo danh sách và adapter
        foodList = mutableListOf()
        foodAdapter = itemFoodAdapter(foodList) { food ->
            addFoodToSelectedMeal(food)
        }

        val recyclerView: RecyclerView = findViewById(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = foodAdapter

        // Thiết lập nút quay lại
        backButton = findViewById(R.id.back_button_meal)
        backButton.setOnClickListener {
            onBackPressed()
        }

        // Tìm các CardView từ layout
        cardBreakfast = findViewById(R.id.card_breakfast)
        cardLunch = findViewById(R.id.card_lunch)
        cardDinner = findViewById(R.id.card_dinner)

        // Xử lý sự kiện chọn bữa ăn
        cardBreakfast.setOnClickListener { selectMeal(cardBreakfast, "Breakfast") }
        cardLunch.setOnClickListener { selectMeal(cardLunch, "Lunch") }
        cardDinner.setOnClickListener { selectMeal(cardDinner, "Dinner") }

        // Khởi tạo reference tới Firebase Realtime Database
        databaseReference = FirebaseDatabase.getInstance().reference

        // Load dữ liệu từ Realtime Database
        loadFoodData()
    }


    // Load danh sách món ăn từ Firebase
    private fun loadFoodData() {
        val foodRef = databaseReference.child("foods")
        foodRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                foodList.clear()
                for (foodSnapshot in snapshot.children) {
                    val food = foodSnapshot.getValue(Food::class.java)
                    food?.let { foodList.add(it) }
                }
                foodAdapter.notifyDataSetChanged()
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@mealActivity, "Error loading food data", Toast.LENGTH_SHORT).show()
            }
        })
    }


    // Xử lý chọn bữa ăn
    private fun selectMeal(selectedCard: CardView, mealName: String) {
        resetCardColors()
        selectedCard.setCardBackgroundColor(Color.parseColor("#4CAF50"))
        selectedMealName = mealName
    }

    // Reset màu của các CardView
    private fun resetCardColors() {
        cardBreakfast.setCardBackgroundColor(Color.WHITE)
        cardLunch.setCardBackgroundColor(Color.WHITE)
        cardDinner.setCardBackgroundColor(Color.WHITE)
    }

    // Thêm món ăn vào bữa được chọn
    private fun addFoodToSelectedMeal(food: Food) {
        if (selectedMealName == null) {
            Toast.makeText(this, "Vui lòng chọn bữa ăn trước", Toast.LENGTH_SHORT).show()
            return
        }

        // Hiển thị hộp thoại xác nhận
        val builder = androidx.appcompat.app.AlertDialog.Builder(this)
        builder.setTitle("Xác nhận")
        builder.setMessage("Bạn có muốn thêm món ${food.name} vào $selectedMealName không?")

        // Nếu người dùng chọn "Có"
        builder.setPositiveButton("Có") { _, _ ->
            val sharedPreferences = getSharedPreferences("user_session", MODE_PRIVATE)
            val userId = sharedPreferences.getString("userId", null).orEmpty()

            val currentDate = java.text.SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
            val logRef = databaseReference.child("dailyLogs").child(userId).child(currentDate)

            logRef.get().addOnSuccessListener { snapshot ->
                val dailyLog = snapshot.getValue(DailyLog::class.java) ?: DailyLog(
                    logId = currentDate,
                    userId = userId,
                    date = currentDate,
                    meals = listOf()
                )

                val updatedMeals = if (dailyLog.meals.any { it.mealName == selectedMealName }) {
                    dailyLog.meals.map { meal ->
                        if (meal.mealName == selectedMealName) {
                            meal.copy(foods = meal.foods + food)
                        } else {
                            meal
                        }
                    }
                } else {
                    dailyLog.meals + Meal(
                        mealId = UUID.randomUUID().toString(),
                        mealName = selectedMealName!!,
                        foods = listOf(food)
                    )
                }

                val updatedDailyLog = dailyLog.copy(meals = updatedMeals)

                logRef.setValue(updatedDailyLog).addOnSuccessListener {
                    Toast.makeText(this, "Thêm ${food.name} thành công", Toast.LENGTH_SHORT).show()

                    // Chuyển icon từ X sang dấu check
                }.addOnFailureListener {
                    Toast.makeText(this, "Không thể thêm", Toast.LENGTH_SHORT).show()
                }
            }.addOnFailureListener {
                Toast.makeText(this, "Lỗi khi tìm nạp nhật ký hàng ngày", Toast.LENGTH_SHORT).show()
            }
        }

        // Nếu người dùng chọn "Không"
        builder.setNegativeButton("Không") { dialog, _ ->
            dialog.dismiss()
        }

        // Hiển thị hộp thoại
        builder.create().show()
    }


    // Xử lý nút quay lại
    override fun onBackPressed() {
        super.onBackPressed()
        Toast.makeText(this, "Returning to previous screen", Toast.LENGTH_SHORT).show()
    }
}
