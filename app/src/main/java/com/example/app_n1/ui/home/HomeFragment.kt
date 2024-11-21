package com.example.app_n1.ui.home

import android.app.AlertDialog
import android.content.Intent
import android.graphics.Color
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
import com.example.app_n1.RegisterActivity
import com.example.app_n1.databinding.FragmentHomeBinding
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
    private val activityItems = listOf(
        Item("Đi bộ", "130 kcal - 30p")
    )

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

        loadMealData()
        return root
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
    }

    private fun loadMealData() {
        val currentDate = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())  // Lấy ngày hôm nay
        val sharedPreferences = requireContext().getSharedPreferences("user_session", AppCompatActivity.MODE_PRIVATE)
        val userId = sharedPreferences.getString("userId", null).orEmpty()  // Lấy userId từ SharedPreferences

        // Lấy dữ liệu từ Firebase DailyLogs của người dùng trong ngày hôm nay
        val logRef = FirebaseDatabase.getInstance().getReference("dailyLogs").child(userId).child(currentDate)

        logRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                // Xóa danh sách cũ trước khi thêm mới
                breakfastItems.clear()
                lunchItems.clear()
                dinnerItems.clear()

                // Lọc món ăn từ dailyLogs
                val dailyLog = snapshot.getValue(DailyLog::class.java)
                dailyLog?.meals?.forEach { meal ->
                    when (meal.mealName) {
                        "Breakfast" -> {  // Món ăn của bữa sáng
                            meal.foods.forEach { food ->
                                breakfastItems.add(Item(food.name, food.carbs.toString())) // Lưu foodId
                            }
                        }
                        "Lunch" -> {  // Món ăn của bữa trưa
                            meal.foods.forEach { food ->
                                lunchItems.add(Item(food.name, food.carbs.toString())) // Lưu foodId
                            }
                        }
                        "Dinner" -> {  // Món ăn của bữa tối
                            meal.foods.forEach { food ->
                                dinnerItems.add(Item(food.name, food.carbs.toString())) // Lưu foodId
                            }
                        }
                    }
                }

                // Cập nhật lại UI sau khi tải dữ liệu
                setupViews(requireView())
                setupMealViews()
            }

            override fun onCancelled(error: DatabaseError) {
                // Handle error if needed
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
