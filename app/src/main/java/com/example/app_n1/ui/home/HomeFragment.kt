package com.example.app_n1.ui.home

import android.app.AlertDialog
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
import com.example.app_n1.databinding.FragmentHomeBinding
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.formatter.ValueFormatter
import com.github.mikephil.charting.components.AxisBase
import com.google.android.material.floatingactionbutton.FloatingActionButton

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    // Danh sách item mà bạn muốn hiển thị
    private val breakfastItems = listOf(
        Item("Bữa sáng 1", "Mô tả cho bữa sáng 1"),
        Item("Bữa sáng 2", "Mô tả cho bữa sáng 2")
    )
    private val lunchItems = listOf(
        Item("Bữa trưa 1", "Mô tả cho bữa trưa 1"),
        Item("Bữa trưa 2", "Mô tả cho bữa trưa 2")
    )
    private val dinnerItems = listOf(
        Item("Bữa tối 1", "Mô tả cho bữa tối 1"),
        Item("Bữa tối 2", "Mô tả cho bữa tối 2")
    )
    private val activityItems = listOf(
        Item("Đi bộ", "130 kcal - 30p"),
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

        // Cài đặt RecyclerView
        setupMealViews()

        return root
    }

    private fun setupViews(root: View) {
        val textViewDinner: TextView = root.findViewById(R.id.textViewDinner)
        val textViewBreakfast: TextView = root.findViewById(R.id.textViewBreakfast)
        val textViewLunch: TextView = root.findViewById(R.id.textViewLunch)
        val textActivity: TextView = root.findViewById(R.id.textActivity)

        textViewBreakfast.visibility = if (breakfastItems.isNullOrEmpty()) View.GONE else View.VISIBLE
        textViewLunch.visibility = if (lunchItems.isNullOrEmpty()) View.GONE else View.VISIBLE
        textViewDinner.visibility = if (dinnerItems.isNullOrEmpty()) View.GONE else View.VISIBLE
        textActivity.visibility = if (activityItems.isNullOrEmpty()) View.GONE else View.VISIBLE
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
            // Thực hiện hành động thêm bữa ăn
            alertDialog.dismiss()
        }

        // Xử lý nút Hủy
        dialogView.findViewById<Button>(R.id.cancel_button).setOnClickListener {
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

    private fun setupRecyclerView(recyclerView: RecyclerView, items: List<Item>) {
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        val adapter = IteambreakfastAdapter(items)
        recyclerView.adapter = adapter
    }

    private fun setupLineChart() {
        val lineChart = binding.lineChart

        // Chuẩn bị dữ liệu cho biểu đồ
        val entries = ArrayList<Entry>().apply {
            add(Entry(0f, 50f))  // Ngày 1: Y = 50
            add(Entry(1f, 60f))  // Ngày 2: Y = 60
            add(Entry(2f, 55f))  // Ngày 3: Y = 55
        }

        // Tạo LineDataSet từ danh sách Entry
        val dataSet = LineDataSet(entries, "Cân nặng").apply {
            color = Color.BLUE  // Màu của đường
            valueTextColor = Color.BLACK  // Màu của giá trị trên biểu đồ
            lineWidth = 2f  // Độ dày của đường
            setCircleColor(Color.RED)  // Màu của điểm tròn
            circleRadius = 4f  // Kích thước của các điểm tròn
        }

        // Tạo LineData từ LineDataSet
        val lineData = LineData(dataSet)

        // Gán dữ liệu cho LineChart
        lineChart.data = lineData

        // Cấu hình XAxis để không lặp lại ngày
        lineChart.xAxis.apply {
            valueFormatter = object : ValueFormatter() {
                override fun getAxisLabel(value: Float, axis: AxisBase?): String {
                    return when (value.toInt()) {
                        0 -> "Ngày 1"
                        1 -> "Ngày 2"
                        2 -> "Ngày 3"
                        else -> ""
                    }
                }
            }
            granularity = 1f  // Cho phép khoảng cách giữa các nhãn
            isGranularityEnabled = true // Bật chế độ granularity
            setDrawLabels(true)  // Vẽ nhãn trên trục X
        }

        lineChart.invalidate()  // Vẽ lại biểu đồ
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
