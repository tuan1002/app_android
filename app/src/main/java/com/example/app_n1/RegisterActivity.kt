package com.example.app_n1

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.EditText
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.app_n1.dao.registerDao
import com.example.app_n1.database.FirebaseUtils
import com.example.app_n1.models.User
import com.google.firebase.database.DatabaseReference

class RegisterActivity : AppCompatActivity() {
    private lateinit var database: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_register)
        database = FirebaseUtils.getDatabaseReference()

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    fun login(view: View) {
        val intent = Intent(this, LoginActivity::class.java)
        startActivity(intent)
    }

    fun registerUser(view: View) {
        // Lấy thông tin từ các EditText
        val name = findViewById<EditText>(R.id.txtNameRe).text.toString()
        val email = findViewById<EditText>(R.id.txtEmailRe).text.toString()
        val password = findViewById<EditText>(R.id.txtPasswprdRe).text.toString() // Nếu không dùng, có thể bỏ qua
        registerDao(this , name , email , password)
    }
}