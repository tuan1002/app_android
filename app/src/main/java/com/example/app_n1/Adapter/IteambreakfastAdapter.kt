package com.example.app_n1.Adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.app_n1.databinding.BreakfastItemBinding

class IteambreakfastAdapter(
    private val items: MutableList<Item>, // Sử dụng MutableList để có thể thay đổi nội dung
    private val onDelete: (Int) -> Unit   // Callback để xóa item
) : RecyclerView.Adapter<IteambreakfastAdapter.ItemViewHolder>() {

    class ItemViewHolder(private val binding: BreakfastItemBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: Item, position: Int, onDelete: (Int) -> Unit) {
            binding.textViewItem.text = item.name
            binding.textViewDescription.text = item.description

            // Xử lý sự kiện khi bấm vào dấu "X" để xóa item
            binding.iconDeleteHome.setOnClickListener {
                onDelete(position)  // Gọi callback khi nhấn vào nút "X"
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = BreakfastItemBinding.inflate(inflater, parent, false)
        return ItemViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        holder.bind(items[position], position, onDelete)
    }

    override fun getItemCount(): Int = items.size
}


data class Item(val name: String, val description: String)
