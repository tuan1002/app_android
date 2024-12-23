package com.example.app_n1

import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import com.google.android.gms.tasks.Tasks
import com.google.firebase.database.FirebaseDatabase
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class CaloChart : AppCompatActivity() {

    private lateinit var barChart: BarChart

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_calo_chart)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        barChart = findViewById(R.id.barChart)
        val sharedPreferences = getSharedPreferences("user_session", AppCompatActivity.MODE_PRIVATE)
        val userId = sharedPreferences.getString("userId", null).orEmpty() // Lấy userId từ SharedPreferences

        fetchCaloriesLast7Days(userId) { data ->
            if (data.isNotEmpty()) {
                setupBarChart(barChart, data)
            } else {
                Toast.makeText(this, "Không có dữ liệu trong 7 ngày qua", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun fetchCaloriesLast7Days(userId: String, onResult: (List<Pair<String, Int>>) -> Unit) {
        val database = FirebaseDatabase.getInstance()
        val dailyLogsRef = database.getReference("dailyLogs/$userId")

        val calendar = Calendar.getInstance()
        val dateFormatter = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())

        // Lưu danh sách ngày trong 7 ngày gần nhất
        val last7Days = mutableListOf<String>()
        for (i in 0..6) {
            last7Days.add(dateFormatter.format(calendar.time))
            calendar.add(Calendar.DAY_OF_YEAR, -1)
        }

        val calorieData = mutableListOf<Pair<String, Int>>()

        // Lấy dữ liệu calo từng ngày
        val tasks = last7Days.map { date ->
            dailyLogsRef.child(date).get().continueWith { task ->
                val snapshot = task.result
                val totalCalories = snapshot?.child("meals")?.children?.sumOf { mealSnapshot ->
                    mealSnapshot.child("foods").children.sumOf { foodSnapshot ->
                        foodSnapshot.child("calories").getValue(Int::class.java) ?: 0
                    }
                } ?: 0
                date to totalCalories
            }
        }

        // Chờ tất cả các ngày hoàn thành
        Tasks.whenAll(tasks).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                calorieData.addAll(tasks.mapNotNull { it.result })
                onResult(calorieData)
            } else {
                Toast.makeText(this, "Lỗi khi tải dữ liệu Firebase", Toast.LENGTH_SHORT).show()
                onResult(emptyList())
            }
        }
    }

    private fun setupBarChart(barChart: BarChart, data: List<Pair<String, Int>>) {
        val entries = data.mapIndexed { index, (date, calories) ->
            BarEntry(index.toFloat(), calories.toFloat())
        }

        val barDataSet = BarDataSet(entries, "Calories in last 7 days")
        barDataSet.color = resources.getColor(R.color.purple_700, null) // Đổi màu cột
        barDataSet.valueTextSize = 12f

        val barData = BarData(barDataSet)
        barChart.data = barData

        // Tùy chỉnh trục x để hiển thị ngày
        barChart.xAxis.apply {
            valueFormatter = IndexAxisValueFormatter(data.map { it.first })
            granularity = 1f
            position = XAxis.XAxisPosition.BOTTOM
            textSize = 12f
        }

        // Tùy chỉnh trục y
        barChart.axisLeft.apply {
            textSize = 12f
            axisMinimum = 0f // Bắt đầu từ 0
        }
        barChart.axisRight.isEnabled = false

        // Tùy chỉnh khác
        barChart.description.isEnabled = false // Tắt mô tả
        barChart.setFitBars(true)
        barChart.invalidate()
    }
}
