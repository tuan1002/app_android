package com.example.app_n1

import android.os.Bundle
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.app_n1.Adapter.itemExerciseAdapter
import com.example.app_n1.models.DailyLog
import com.example.app_n1.models.Exercise
import com.google.firebase.database.*
import java.text.SimpleDateFormat
import java.util.*

class exercise : AppCompatActivity() {

    private lateinit var backButton: ImageView
    private lateinit var exerciseList: MutableList<Exercise>
    private lateinit var exerciseAdapter: itemExerciseAdapter
    private lateinit var databaseReference: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_exercise)

        // Khởi tạo danh sách và adapter
        exerciseList = mutableListOf()
        exerciseAdapter = itemExerciseAdapter(exerciseList) { exercise ->
            addExerciseToDailyLog(exercise)
        }

        databaseReference = FirebaseDatabase.getInstance().reference
        backButton = findViewById(R.id.back_button_meal)
        backButton.setOnClickListener { onBackPressed() }

        // Thiết lập giao diện
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        val recyclerView = findViewById<RecyclerView>(R.id.recyclerViewActivity)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = exerciseAdapter
        // Tải dữ liệu bài tập từ Firebase
        loadExercisesData()
    }

    private fun loadExercisesData() {
        val exercisesRef = databaseReference.child("exercises")
        exercisesRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                exerciseList.clear()
                for (exerciseSnapshot in snapshot.children) {
                    val exercise = exerciseSnapshot.getValue(Exercise::class.java)
                    exercise?.let { exerciseList.add(it) }
                }
                exerciseAdapter.notifyDataSetChanged()
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@exercise, "Error loading exercise data", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun addExerciseToDailyLog(exercise: Exercise) {
        val builder = androidx.appcompat.app.AlertDialog.Builder(this)
        builder.setTitle("Xác nhận")
            .setMessage("Bạn có muốn thêm bài tập ${exercise.name} không?")
            .setPositiveButton("Có") { _, _ ->
                val sharedPreferences = getSharedPreferences("user_session", MODE_PRIVATE)
                val userId = sharedPreferences.getString("userId", null).orEmpty()

                val currentDate = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
                val logRef = databaseReference.child("dailyLogs").child(userId).child(currentDate)

                logRef.get().addOnSuccessListener { snapshot ->
                    val dailyLog = snapshot.getValue(DailyLog::class.java) ?: DailyLog(
                        logId = currentDate,
                        userId = userId,
                        date = currentDate,
                        exercises = emptyList()
                    )

                    if (dailyLog.exercises.any { it.name == exercise.name }) {
                        Toast.makeText(this, "Bài tập đã tồn tại trong nhật ký", Toast.LENGTH_SHORT).show()
                    } else {
                        val updatedExercises = dailyLog.exercises + exercise
                        val updatedDailyLog = dailyLog.copy(exercises = updatedExercises)

                        logRef.setValue(updatedDailyLog).addOnSuccessListener {
                            Toast.makeText(this, "Thêm bài tập ${exercise.name} thành công", Toast.LENGTH_SHORT).show()
                        }.addOnFailureListener {
                            Toast.makeText(this, "Không thể thêm bài tập", Toast.LENGTH_SHORT).show()
                        }
                    }
                }.addOnFailureListener {
                    Toast.makeText(this, "Lỗi khi tìm nạp nhật ký hàng ngày", Toast.LENGTH_SHORT).show()
                }
            }
            .setNegativeButton("Không") { dialog, _ ->
                dialog.dismiss()
            }
            .create()
            .show()
    }
}
