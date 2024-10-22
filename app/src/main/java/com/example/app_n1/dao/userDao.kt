package com.example.app_n1.dao

import android.content.Context
import android.content.Intent
import android.widget.Toast
import com.example.app_n1.MainActivity
import com.example.app_n1.models.User
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import java.security.MessageDigest


fun registerDao(context: Context, name: String, email: String, password: String) {

    var pwdHash = hashPassword(password)
    if (name.isEmpty() || email.isEmpty() || password.isEmpty()) {
        Toast.makeText(context, "Please fill all fields", Toast.LENGTH_SHORT).show()
        return
    }

    // Khởi tạo Firebase Database
    val database: DatabaseReference = FirebaseDatabase.getInstance().reference

    // Tạo ID ngẫu nhiên cho người dùng
    val userId = database.child("users").push().key
    val user = User(userId ?: "", name, email, pwdHash)

    // Lưu người dùng vào cơ sở dữ liệu
    database.child("users").child(userId!!).setValue(user)
        .addOnSuccessListener {
            Toast.makeText(context, "Đăng kí thành công", Toast.LENGTH_SHORT).show()

            // Chuyển sang MainActivity sau khi đăng ký thành công
            val intent = Intent(context, MainActivity::class.java)
            context.startActivity(intent)
        }
        .addOnFailureListener {
            Toast.makeText(context, "Đăng kí thất bại", Toast.LENGTH_SHORT).show()
        }
}


fun loginDao(context: Context, email: String, password: String) {
    val database: DatabaseReference = FirebaseDatabase.getInstance().reference

    if (email.isEmpty() || password.isEmpty()) {
        Toast.makeText(context, "Please fill all fields", Toast.LENGTH_SHORT).show()
        return
    }

    // Băm mật khẩu nhập vào
    val pwdHash = hashPassword(password)

    // Kiểm tra thông tin người dùng từ Firebase
    database.child("users").get().addOnSuccessListener { dataSnapshot ->
        var isUserFound = false
        for (userSnapshot in dataSnapshot.children) {
            val user = userSnapshot.getValue(User::class.java)
            // So sánh email và mật khẩu đã băm
            if (user != null && user.email == email && user.password == pwdHash) {
                isUserFound = true

                // Lưu thông tin người dùng vào SharedPreferences
                val sharedPreferences = context.getSharedPreferences("user_session", Context.MODE_PRIVATE)
                val editor = sharedPreferences.edit()
                editor.putString("userId", user.userId)
                editor.putString("userName", user.name)
                editor.putString("userEmail", user.email)
                editor.apply() // Áp dụng thay đổi

                Toast.makeText(context, "Login successful", Toast.LENGTH_SHORT).show()

                // Điều hướng sang MainActivity sau khi đăng nhập thành công
                val intent = Intent(context, MainActivity::class.java)
                context.startActivity(intent)
                // Không cần gọi finish() ở đây vì đây không phải là Activity
                break
            }
        }

        if (!isUserFound) {
            Toast.makeText(context, "Invalid email or password", Toast.LENGTH_SHORT).show()
        }
    }.addOnFailureListener {
        Toast.makeText(context, "Login failed", Toast.LENGTH_SHORT).show()
    }
}


fun hashPassword(password: String): String {
    val bytes = password.toByteArray() // Chuyển chuỗi mật khẩu thành mảng byte
    val md = MessageDigest.getInstance("SHA-256") // Sử dụng thuật toán SHA-256
    val digest = md.digest(bytes) // Tạo băm từ mật khẩu

    // Chuyển đổi giá trị băm sang dạng chuỗi hex
    return digest.fold("", { str, it -> str + "%02x".format(it) })
}