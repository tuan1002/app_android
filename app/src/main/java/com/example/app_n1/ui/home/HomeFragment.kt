package com.example.app_n1.ui.home

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.app_n1.Adapter.IteambreakfastAdapter
import com.example.app_n1.Adapter.Item
import com.example.app_n1.R
import com.example.app_n1.databinding.FragmentHomeBinding
import com.example.app_n1.exercise
import com.example.app_n1.mealActivity
import com.example.app_n1.models.DailyLog
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    // Danh sách item mà bạn muốn hiển thị
    private val breakfastItems = mutableListOf<Item>()
    private val lunchItems = mutableListOf<Item>()
    private val dinnerItems = mutableListOf<Item>()
    private val activityItems = mutableListOf<Item>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val root: View = binding.root

        setupViews(root)

        // Gọi hàm để vẽ biểu đồ sau khi layout đã được inflate
        setupLineChart()

        val fabAddMeal: FloatingActionButton = root.findViewById(R.id.fab_add_meal)
        fabAddMeal.setOnClickListener {
            showCustomDialog()
        }
        val sharedPreferences = requireContext().getSharedPreferences("user_session", AppCompatActivity.MODE_PRIVATE)
        val userId = sharedPreferences.getString("userId", null).orEmpty() // Lấy userId từ SharedPreferences
        fetchTodayNutrientsRealtime(userId) // Thay bằng hàm realtime
        loadMealDataRealtime()             // Thay bằng hàm realtime
        return root
    }

    fun fetchTodayNutrientsRealtime(userId: String) {
        val database = FirebaseDatabase.getInstance()
        val dailyLogsRef = database.getReference("dailyLogs/$userId")

        val todayDate = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())

        dailyLogsRef.child(todayDate).addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    var totalCalories = 0
                    var totalCarbs = 0.0
                    var totalProtein = 0.0
                    var totalFat = 0.0
                    var totalCaloriesBurned = 0

                    snapshot.child("meals").children.forEach { mealSnapshot ->
                        val foodsSnapshot = mealSnapshot.child("foods")
                        foodsSnapshot.children.forEach { foodSnapshot ->
                            val calories = foodSnapshot.child("calories").getValue(Int::class.java) ?: 0
                            val carbs = foodSnapshot.child("carbs").getValue(Double::class.java) ?: 0.0
                            val protein = foodSnapshot.child("protein").getValue(Double::class.java) ?: 0.0
                            val fat = foodSnapshot.child("fat").getValue(Double::class.java) ?: 0.0

                            totalCalories += calories
                            totalCarbs += carbs
                            totalProtein += protein
                            totalFat += fat
                        }
                    }

                    snapshot.child("exercises").children.forEach { exSnap ->
                        val caloriesBurned = exSnap.child("caloriesBurned").getValue(Int::class.java) ?: 0
                        totalCaloriesBurned += caloriesBurned
                    }

                    // Tính tổng calories còn lại
                    val remainingCalories = totalCalories - totalCaloriesBurned

                    // Hiển thị dữ liệu lên TextView
                    binding.progressTex3.text = totalCalories.toString()
                    binding.textView8.text = "%.0f".format(totalCarbs)
                    binding.textView9.text = "%.0f".format(totalProtein)
                    binding.textView10.text = "%.0f".format(totalFat)
                    binding.progressTex4.text = totalCaloriesBurned.toString()
                    binding.progressTex2.text = remainingCalories.toString()
                } else {
                    // Reset dữ liệu nếu không có log cho ngày hôm nay
                    binding.progressTex3.text = "0"
                    binding.textView8.text = "0"
                    binding.textView9.text = "0"
                    binding.textView10.text = "0"
                    binding.progressTex4.text = "0"
                    binding.progressTex2.text = "0"
                }
            }

            override fun onCancelled(error: DatabaseError) {
                println("Firebase error: ${error.message}")
            }
        })
    }



    private fun setupViews(root: View) {
        val textViewDinner: TextView = root.findViewById(R.id.textViewDinner)
        val textViewBreakfast: TextView = root.findViewById(R.id.textViewBreakfast)
        val textViewLunch: TextView = root.findViewById(R.id.textViewLunch)
        val textActivity: TextView = root.findViewById(R.id.textActivity)
        // Kiểm tra nếu breakfastItems không rỗng thì hiển thị textViewBreakfast
        textViewBreakfast.visibility = if (breakfastItems.isEmpty()) View.GONE else View.VISIBLE
        textViewLunch.visibility = if (lunchItems.isEmpty()) View.GONE else View.VISIBLE
        textViewDinner.visibility = if (dinnerItems.isEmpty()) View.GONE else View.VISIBLE
        textActivity.visibility = if (activityItems.isEmpty()) View.GONE else View.VISIBLE
        // Hoạt động (bài tập)
        if (activityItems.isNotEmpty()) {
            binding.recycleractivity.visibility = View.VISIBLE
            setupRecyclerView(binding.recycleractivity, activityItems)
        } else {
            binding.recycleractivity.visibility = View.GONE
        }
    }

    private fun loadMealDataRealtime() {
        val currentDate = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
        val sharedPreferences = requireContext().getSharedPreferences("user_session", AppCompatActivity.MODE_PRIVATE)
        val userId = sharedPreferences.getString("userId", null).orEmpty()

        val logRef = FirebaseDatabase.getInstance().getReference("dailyLogs").child(userId).child(currentDate)

        logRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                breakfastItems.clear()
                lunchItems.clear()
                dinnerItems.clear()
                activityItems.clear()

                val dailyLog = snapshot.getValue(DailyLog::class.java)
                dailyLog?.meals?.forEach { meal ->
                    when (meal.mealName) {
                        "Breakfast" -> {
                            meal.foods.forEach { food ->
                                breakfastItems.add(Item(food.name, "${food.calories} Kcal"))
                            }
                        }
                        "Lunch" -> {
                            meal.foods.forEach { food ->
                                lunchItems.add(Item(food.name, "${food.calories} Kcal"))
                            }
                        }
                        "Dinner" -> {
                            meal.foods.forEach { food ->
                                dinnerItems.add(Item(food.name, "${food.calories} Kcal"))
                            }
                        }
                    }
                }

                dailyLog?.exercises?.forEach { exercise ->
                    activityItems.add(Item(exercise.name, "${exercise.caloriesBurned}"))
                }

                // Cập nhật lại UI
                setupViews(requireView())
                setupMealViews()
            }

            override fun onCancelled(error: DatabaseError) {
                println("Firebase error: ${error.message}")
            }
        })
    }


    private fun showCustomDialog() {
        // Tạo AlertDialog Builder
        val dialogBuilder = AlertDialog.Builder(requireContext())

        // Inflate layout tùy chỉnh
        val dialogView = LayoutInflater.from(requireContext()).inflate(R.layout.bottom_sheet_layout, null)

        // Thiết lập nội dung cho AlertDialog
        dialogBuilder.setView(dialogView)

        // Tạo AlertDialog
        val alertDialog = dialogBuilder.create()

        // Xử lý nút Thêm
        dialogView.findViewById<Button>(R.id.add_meal_button).setOnClickListener {
            val intent = Intent(requireContext(), mealActivity()::class.java)
            startActivity(intent)
            alertDialog.dismiss()
        }

        dialogView.findViewById<Button>(R.id.add_exercises_button).setOnClickListener{
            val intent = Intent(requireContext(), exercise()::class.java)
            startActivity(intent)
            alertDialog.dismiss()
        }

        // Hiển thị AlertDialog
        alertDialog.show()
    }

    private fun setupMealViews() {
        // Bữa sáng
        if (breakfastItems.isNotEmpty()) {
            binding.recyclerViewBreakfast.visibility = View.VISIBLE
            setupRecyclerView(binding.recyclerViewBreakfast, breakfastItems)
        } else {
            binding.recyclerViewBreakfast.visibility = View.GONE
        }

        // Bữa trưa
        if (lunchItems.isNotEmpty()) {
            binding.recyclerViewLunch.visibility = View.VISIBLE
            setupRecyclerView(binding.recyclerViewLunch, lunchItems)
        } else {
            binding.recyclerViewLunch.visibility = View.GONE
        }

        // Bữa tối
        if (dinnerItems.isNotEmpty()) {
            binding.recyclerViewDinner.visibility = View.VISIBLE
            setupRecyclerView(binding.recyclerViewDinner, dinnerItems)
        } else {
            binding.recyclerViewDinner.visibility = View.GONE
        }

        // Hoạt động
        if (activityItems.isNotEmpty()) {
            binding.recycleractivity.visibility = View.VISIBLE
            setupRecyclerView(binding.recycleractivity, activityItems)
        } else {
            binding.recycleractivity.visibility = View.GONE
        }
    }

    private fun setupRecyclerView(recyclerView: RecyclerView, items: MutableList<Item>) {
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        val adapter = IteambreakfastAdapter(items) { position ->
            val itemToRemove = items[position]
            val foodName = itemToRemove.name // Lấy tên món ăn cần xóa
            val mealName = when (recyclerView.id) {
                R.id.recyclerViewBreakfast -> "Breakfast"
                R.id.recyclerViewLunch -> "Lunch"
                R.id.recyclerViewDinner -> "Dinner"
                else -> ""
            }

            // Tạo hộp thoại xác nhận xóa món ăn
            val dialogBuilder = AlertDialog.Builder(requireContext())
            dialogBuilder.setTitle("Xóa món ăn")
            dialogBuilder.setMessage("Bạn có chắc chắn muốn xóa món ăn \"$foodName\" không?")

            // Nếu người dùng chọn "Yes", xóa món ăn
            dialogBuilder.setPositiveButton("Yes") { _, _ ->
                // Xóa món ăn khỏi Firebase
                removeMealFromFirebaseById(foodName, mealName)

                // Xóa món ăn khỏi danh sách và cập nhật RecyclerView
                items.removeAt(position)
                recyclerView.adapter?.notifyItemRemoved(position)  // Cập nhật RecyclerView
            }

            // Nếu người dùng chọn "No", đóng hộp thoại
            dialogBuilder.setNegativeButton("No") { dialog, _ ->
                dialog.dismiss() // Đóng hộp thoại nếu không muốn xóa
            }

            // Hiển thị hộp thoại xác nhận
            dialogBuilder.create().show()
        }

        recyclerView.adapter = adapter
    }


    private fun removeMealFromFirebaseById(foodname: String, mealName: String) {
        // Lấy ngày hiện tại
        val currentDate = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())

        // Lấy userId từ SharedPreferences
        val sharedPreferences = requireContext().getSharedPreferences("user_session", AppCompatActivity.MODE_PRIVATE)
        val userId = sharedPreferences.getString("userId", null).orEmpty()

        // Lấy dữ liệu từ Firebase DailyLogs của người dùng trong ngày hôm nay
        val logRef = FirebaseDatabase.getInstance().getReference("dailyLogs").child(userId).child(currentDate)

        logRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                // Lấy dữ liệu DailyLog từ Firebase
                val dailyLog = snapshot.getValue(DailyLog::class.java)
                dailyLog?.meals?.forEach { meal ->
                    if (meal.mealName == mealName) {
                        // Loại bỏ món ăn theo foodId
                        val updatedFoods = meal.foods.filterNot { it.name == foodname }.toMutableList()
                        meal.foods = updatedFoods // Cập nhật lại foods
                    }
                }

                // Cập nhật lại dữ liệu vào Firebase
                logRef.setValue(dailyLog)
            }

            override fun onCancelled(error: DatabaseError) {
                // Handle error if needed
            }
        })
    }


    private fun setupLineChart() {

    }
}
