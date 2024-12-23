package com.example.app_n1.Adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.app_n1.databinding.ItemExerciseBinding
import com.example.app_n1.models.Exercise

class itemExerciseAdapter(
    private val foodList: MutableList<Exercise>,
    var onItemClickListener: ((Exercise) -> Unit)? = null
) : RecyclerView.Adapter<itemExerciseAdapter.ExerciseViewHolder>() { // Sửa tên lớp ViewHolder

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ExerciseViewHolder {
        val binding = ItemExerciseBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ExerciseViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ExerciseViewHolder, position: Int) {
        val foodItem = foodList[position]
        holder.bind(foodItem)
    }

    override fun getItemCount(): Int = foodList.size

    inner class ExerciseViewHolder(private val binding: ItemExerciseBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(foodItem: Exercise) {
            binding.ExerciseName.text = "Name: ${foodItem.name}"
            binding.ExerciseCalories.text = "Calories: ${foodItem.caloriesBurned} kcal"
            binding.root.setOnClickListener {
                onItemClickListener?.invoke(foodItem)
            }
        }
    }
}
