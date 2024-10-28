package com.example.app_n1

import android.os.Bundle
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.app_n1.bottomSheet.AgeBottomSheet
import com.example.app_n1.bottomSheet.GenderBottomSheet
import com.example.app_n1.bottomSheet.HeightBottomSheet
import com.example.app_n1.bottomSheet.KcalBottomSheet // Nhớ import KcalBottomSheet
import com.example.app_n1.bottomSheet.WightBottomSheet
import com.google.android.material.floatingactionbutton.FloatingActionButton

class BmrUpdate : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_bmr_update)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val kcalButton: CardView = findViewById(R.id.bmr_card_1)
        kcalButton.setOnClickListener {
            showKcalBottomSheet()
        }

        val heightButton: CardView = findViewById(R.id.bmr_card_2)
        heightButton.setOnClickListener {
            showHeightBottomSheet()
        }

        val weightButton: CardView = findViewById(R.id.bmr_card_3)
        weightButton.setOnClickListener {
            showWeightBottomSheet()
        }

        val ageButton: CardView = findViewById(R.id.bmr_card_4)
        ageButton.setOnClickListener {
            showAgeBottomSheet()
        }
        val genderButton: CardView = findViewById(R.id.bmr_card_5)
        genderButton.setOnClickListener {
            showGenderBottomSheet()
        }
    }

    private fun showKcalBottomSheet() {
        val bottomSheet = KcalBottomSheet() // Khởi tạo BottomSheetDialogFragment
        bottomSheet.show(supportFragmentManager, bottomSheet.tag) // Hiển thị Bottom Sheet
    }

    private fun showHeightBottomSheet() {
        val bottomSheet = HeightBottomSheet() // Khởi tạo BottomSheetDialogFragment
        bottomSheet.show(supportFragmentManager, bottomSheet.tag) // Hiển thị Bottom Sheet
    }

    private fun showWeightBottomSheet() {
        val bottomSheet = WightBottomSheet() // Khởi tạo BottomSheetDialogFragment
        bottomSheet.show(supportFragmentManager, bottomSheet.tag) // Hiển thị Bottom Sheet
    }

    private fun showAgeBottomSheet() {
        val bottomSheet = AgeBottomSheet() // Khởi tạo BottomSheetDialogFragment
        bottomSheet.show(supportFragmentManager, bottomSheet.tag) // Hiển thị Bottom Sheet
    }
    private fun showGenderBottomSheet() {
        val bottomSheet = GenderBottomSheet() // Khởi tạo BottomSheetDialogFragment
        bottomSheet.show(supportFragmentManager, bottomSheet.tag) // Hiển thị Bottom Sheet
    }
    fun back_aboutme(view: View) {
        finish()
    }
}
