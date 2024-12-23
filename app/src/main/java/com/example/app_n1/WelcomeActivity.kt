package com.example.app_n1

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.firebase.database.FirebaseDatabase

class WelcomeActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_welcome)

        // Kiểm tra trạng thái đăng nhập
        checkLoginState()

        // Thiết lập padding cho hệ thống
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    private fun checkLoginState() {
        val sharedPreferences = getSharedPreferences("user_session", AppCompatActivity.MODE_PRIVATE)
        val userId = sharedPreferences.getString("userId", null).orEmpty()  // Lấy userId từ SharedPreferences

        //
        if (userId.isNotEmpty()) {
            // Chuyển về MainActivity nếu đã đăng nhập
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish() // Kết thúc WelcomeActivity để không quay lại
        }
    }

    fun register(view: View) {
        val intent = Intent(this, RegisterActivity::class.java)
        startActivity(intent)
    }

    fun login(view: View) {
        val intent = Intent(this, LoginActivity::class.java)
        startActivity(intent)
    }
}
