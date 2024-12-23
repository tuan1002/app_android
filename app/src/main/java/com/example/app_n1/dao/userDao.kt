package com.example.app_n1.dao

import android.content.Context
import android.content.Intent
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.app_n1.BmrUpdate
import com.example.app_n1.Brm_register
import com.example.app_n1.LoginActivity
import com.example.app_n1.MainActivity
import com.example.app_n1.R
import com.example.app_n1.database.FirebaseUtils
import com.example.app_n1.databinding.ActivityBmrUpdateBinding
import com.example.app_n1.databinding.FragmentAboutmeBinding
import com.example.app_n1.models.User
import com.example.app_n1.models.UserInfo
import com.google.firebase.database.DatabaseReference
import java.security.MessageDigest
import kotlin.math.pow

private val database: DatabaseReference = FirebaseUtils.getDatabaseReference()
fun registerDao(context: Context, name: String, email: String, password: String) {
    // Kiểm tra xem các trường có rỗng không
    if (name.isEmpty() || email.isEmpty() || password.isEmpty()) {
        Toast.makeText(context, "Vui lòng điền vào tất cả các trường có giá trị hợp lệ", Toast.LENGTH_SHORT).show()
        return
    }
    checkEmailExists(context, email) { exists ->
        if (exists) {
            Toast.makeText(context, "Email đã tồn tại", Toast.LENGTH_SHORT).show()
        } else {
            val pwdHash = hashPassword(password)
            val userId = database.child("users").push().key.toString()
            val user = User(userId, name, email, pwdHash)
            // Lưu thông tin người dùng vào SharedPreferences
            val sharedPreferences = context.getSharedPreferences("user_session", Context.MODE_PRIVATE)
            sharedPreferences.edit().apply {
                putString("userId", user.userId)
                putString("userName", user.name)
                putString("userEmail", user.email)
                apply()
            }
            database.child("users").child(userId).setValue(user)
                .addOnSuccessListener {
                    Toast.makeText(context, "Đăng kí thành công", Toast.LENGTH_SHORT).show()
                    val intent = Intent(context, Brm_register::class.java)
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK) // Thêm cờ để khởi động activity từ context
                    context.startActivity(intent)
                }
                .addOnFailureListener {
                    Toast.makeText(context, "Đăng kí thất bại", Toast.LENGTH_SHORT).show()
                }
        }
    }
}



fun loginDao(context: Context, email: String, password: String) {

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

fun userInforDao(context: Context, age: Int, weight: Double, height: Double, kcal :Long, gender: String ) {
    // Kiểm tra nếu có trường nào trống
    if (age <= 0 || weight <= 0 || height <= 0 || gender.isEmpty() || kcal <= 500) {
        Toast.makeText(context, "Giá trị phải lớn hơn 0", Toast.LENGTH_SHORT).show()
        return
    }

    // Khởi tạo database reference
    val sharedPreferences = context.getSharedPreferences("user_session", Context.MODE_PRIVATE)
    val userId = sharedPreferences.getString("userId", null).toString()



    // Tạo đối tượng UserInfo với thông tin cần lưu
    val userInfo = UserInfo(userId = userId, age = age, weight = weight, height = height,kcal = kcal, gender = gender)

    // Lưu thông tin vào Firebase
    database.child("usersInfor").child(userId).setValue(userInfo)
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

fun logoutDao(context: Context) {
    // Truy cập SharedPreferences
    val sharedPreferences = context.getSharedPreferences("user_session", Context.MODE_PRIVATE)
    val editor = sharedPreferences.edit()

    // Xóa tất cả các giá trị đã lưu trong SharedPreferences
    editor.clear()
    editor.apply() // Áp dụng thay đổi

    // Hiển thị thông báo
    Toast.makeText(context, "Logged out successfully", Toast.LENGTH_SHORT).show()

    // Điều hướng người dùng trở lại màn hình đăng nhập
    val intent = Intent(context, LoginActivity::class.java)
    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK // Xóa ngăn xếp Activity
    context.startActivity(intent)
}

fun getInforBmrUpdate(id: String, activity: ActivityBmrUpdateBinding) {

    if (id.isNotEmpty()) {
        val inforRef = database.child("usersInfor").child(id)
        inforRef.get().addOnSuccessListener { dataSnapshot ->
            val inf = dataSnapshot.getValue(UserInfo::class.java)
            if (inf != null) {
                activity.txtBmrUpdateKcal.text = inf.kcal.toString()
                activity.txtBmrUpdateHeight.text = inf.height.toString()
                activity.txtBmrUpdateWeight.text = inf.weight.toString()
                activity.txtBmrUpdateAge.text = inf.age.toString()
                activity.txtBmrUpdateGender.text = inf.gender.toString()
            }
        }
    }
}


fun getInfor(id: String, binding: FragmentAboutmeBinding) {
    if (id.isNotEmpty()) {
        // Query the "users" table
        val userRef = database.child("users").child(id)
        userRef.get().addOnSuccessListener { dataSnapshot ->
            val user = dataSnapshot.getValue(User::class.java)
            if (user != null) {
                binding.txtAboutmeName.text = user.name ?: "N/A"
            }
        }

        // Query the "usersInfor" table
        val inforRef = database.child("usersInfor").child(id)
        inforRef.get().addOnSuccessListener { dataSnapshot ->
            val inf = dataSnapshot.getValue(UserInfo::class.java)
            if (inf != null) {
                binding.txtAboutmeHeight.text = inf.height.toString() + " m"
                binding.txtAboutmeWeight.text = inf.weight.toString() + " kg"
                binding.txtAboutmeBmi.text = calculateBMI(inf.weight, inf.height).toString()
                binding.txtAboutmeBmr.text = calculateBMR(inf.weight, inf.height, inf.age, inf.gender).toString()
            }
        }
    }
}

fun calculateBMR(weight: Double, height: Double, age: Int, isMale: String): Double {
    val bmr = if (isMale == "Nam") {
        88.362 + (13.397 * weight) + (4.799 * height) - (5.677 * age)
    } else {
        447.593 + (9.247 * weight) + (3.098 * height) - (4.330 * age)
    }
    return String.format("%.2f", bmr).toDouble()
}

fun calculateBMI(weight: Double, height: Double): Double {
    val bmi = weight / (height.pow(2))
    return String.format("%.2f", bmi).toDouble()
}

fun hashPassword(password: String): String {
    val bytes = password.toByteArray() // Chuyển chuỗi mật khẩu thành mảng byte
    val md = MessageDigest.getInstance("SHA-256") // Sử dụng thuật toán SHA-256
    val digest = md.digest(bytes) // Tạo băm từ mật khẩu

    // Chuyển đổi giá trị băm sang dạng chuỗi hex
    return digest.fold("", { str, it -> str + "%02x".format(it) })
}

fun checkEmailExists(context: Context, email: String, callback: (Boolean) -> Unit) {
    // Kiểm tra email từ Firebase
    database.child("users").get().addOnSuccessListener { dataSnapshot ->
        var isUserFound = false
        for (userSnapshot in dataSnapshot.children) {
            val user = userSnapshot.getValue(User::class.java)
            if (user != null && user.email == email) {
                isUserFound = true
                break
            }
        }
        // Gọi callback với kết quả tìm kiếm
        callback(isUserFound)
    }.addOnFailureListener {
        // Nếu có lỗi xảy ra, có thể coi như email không tồn tại
        Toast.makeText(context, "Failed to check email existence", Toast.LENGTH_SHORT).show()
        callback(false)
    }
}
