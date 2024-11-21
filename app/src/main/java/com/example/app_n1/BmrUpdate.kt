package com.example.app_n1

import android.content.Context
import android.os.Bundle
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.app_n1.bottomSheet.AgeBottomSheet
import com.example.app_n1.bottomSheet.GenderBottomSheet
import com.example.app_n1.bottomSheet.HeightBottomSheet
import com.example.app_n1.bottomSheet.KcalBottomSheet // Nhớ import KcalBottomSheet
import com.example.app_n1.bottomSheet.WightBottomSheet
import com.example.app_n1.dao.getInforBmrUpdate
import com.example.app_n1.databinding.ActivityBmrUpdateBinding

class BmrUpdate : AppCompatActivity() {

    private var _binding: ActivityBmrUpdateBinding? = null
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // Khởi tạo binding và thiết lập nội dung view
        _binding = ActivityBmrUpdateBinding.inflate(layoutInflater)
        setContentView(binding.root) // Sử dụng binding.root thay cho R.layout.activity_bmr_update

        // Áp dụng padding cho cửa sổ
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Gọi getBrmUpdate() sau khi binding đã được khởi tạo
        getBrmUpdate()

        // Khởi tạo các CardView và setOnClickListener
        binding.bmrCard1.setOnClickListener {
            showKcalBottomSheet()
        }

        binding.bmrCard2.setOnClickListener {
            showHeightBottomSheet()
        }

        binding.bmrCard3.setOnClickListener {
            showWeightBottomSheet()
        }

        binding.bmrCard4.setOnClickListener {
            showAgeBottomSheet()
        }

        binding.bmrCard5.setOnClickListener {
            showGenderBottomSheet()
        }
    }

    private fun getBrmUpdate() {
        val sharedPreferences = this.getSharedPreferences("user_session", Context.MODE_PRIVATE)
        val userId = sharedPreferences.getString("userId", null)

        // Kiểm tra userId không null
        if (userId != null) {
            getInforBmrUpdate(userId, binding)
        }
    }

    private fun showKcalBottomSheet() {
        val bottomSheet = KcalBottomSheet()
        bottomSheet.show(supportFragmentManager, bottomSheet.tag)
    }

    private fun showHeightBottomSheet() {
        val bottomSheet = HeightBottomSheet()
        bottomSheet.show(supportFragmentManager, bottomSheet.tag)
    }

    private fun showWeightBottomSheet() {
        val bottomSheet = WightBottomSheet()
        bottomSheet.show(supportFragmentManager, bottomSheet.tag)
    }

    private fun showAgeBottomSheet() {
        val bottomSheet = AgeBottomSheet()
        bottomSheet.show(supportFragmentManager, bottomSheet.tag)
    }

    private fun showGenderBottomSheet() {
        val bottomSheet = GenderBottomSheet()
        bottomSheet.show(supportFragmentManager, bottomSheet.tag)
    }

    fun back_aboutme(view: View) {
        finish()
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}
