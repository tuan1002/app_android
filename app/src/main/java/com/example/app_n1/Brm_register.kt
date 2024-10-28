package com.example.app_n1

import android.os.Bundle
import android.view.View
import android.widget.EditText
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.app_n1.dao.userInforDao

class Brm_register : AppCompatActivity() {

    private var selectedGender: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_brm_register)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        eventSelectGender()
    }

    private fun eventSelectGender() {
        val selectedColor = ContextCompat.getColor(this, R.color.greenlight)
        val defaultColor = ContextCompat.getColor(this, R.color.white)

        // Lấy các CardView từ layout
        val cardMale: CardView = findViewById(R.id.card_male)
        val cardFemale: CardView = findViewById(R.id.card_female)

        // Khởi tạo màu nền cho CardView
        cardMale.setCardBackgroundColor(defaultColor)
        cardFemale.setCardBackgroundColor(defaultColor)

        cardMale.setOnClickListener {
            cardMale.setCardBackgroundColor(selectedColor)
            cardFemale.setCardBackgroundColor(defaultColor)
            selectedGender = "Nam"
        }

        cardFemale.setOnClickListener {
            cardFemale.setCardBackgroundColor(selectedColor)
            cardMale.setCardBackgroundColor(defaultColor)
            selectedGender = "Nữ"
        }
    }

    fun bmr_register() {
        val kcal = findViewById<EditText>(R.id.edit_brm_register_kcal).text.toString().toLongOrNull()
        val height = findViewById<EditText>(R.id.edit_brm_register_height).text.toString().toDoubleOrNull()
        val weight = findViewById<EditText>(R.id.edit_brm_register_weight).text.toString().toDoubleOrNull()
        val age = findViewById<EditText>(R.id.edit_brm_register_age).text.toString().toIntOrNull()
        if (kcal == null || height == null || weight == null || age == null || selectedGender == null) {
            Toast.makeText(this, "Vui lòng điền vào tất cả các trường có giá trị hợp lệ", Toast.LENGTH_SHORT).show()
        }
        else{
            userInforDao(this,age , weight , height ,kcal, selectedGender.toString())
        }
    }

    fun btn_brm_register(view: View) {
        bmr_register()
    }
}
