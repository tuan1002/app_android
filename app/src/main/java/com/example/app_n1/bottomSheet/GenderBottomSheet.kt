package com.example.app_n1.bottomSheet

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.RadioGroup
import android.widget.Toast
import com.example.app_n1.R
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class GenderBottomSheet : BottomSheetDialogFragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this Bottom Sheet
        val view = inflater.inflate(R.layout.activity_gender_bottom_sheet, container, false)

        val radioGroupGender = view.findViewById<RadioGroup>(R.id.radioGroupGender)
        val buttonSave = view.findViewById<Button>(R.id.closeButtonGender)

        buttonSave.setOnClickListener {
            val selectedGender = when (radioGroupGender.checkedRadioButtonId) {
                R.id.radioMale -> "Nam"
                R.id.radioFemale -> "Nữ"
                else -> null
            }
            if (selectedGender != null) {
                Toast.makeText(context, "Đã chọn giới tính: $selectedGender", Toast.LENGTH_SHORT).show()
                dismiss() // Đóng Bottom Sheet sau khi lưu
            } else {
                Toast.makeText(context, "Vui lòng chọn giới tính", Toast.LENGTH_SHORT).show()
            }
        }
        return view
    }
}
