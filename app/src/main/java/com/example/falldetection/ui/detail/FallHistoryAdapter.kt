package com.example.falldetection.ui.detail

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.falldetection.data.model.FallHistoryItem
import com.example.falldetection.databinding.DeviceItemBinding
import com.example.falldetection.utils.Utils

class FallHistoryAdapter(
    private val listFallHistory: ArrayList<FallHistoryItem>
) : RecyclerView.Adapter<FallHistoryAdapter.ViewHolder>() {

    private var reminderNameDevice: String = ""
    fun updateReminderName(reminderNameDevice: String) {
        this.reminderNameDevice = reminderNameDevice
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = DeviceItemBinding
            .inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding, reminderNameDevice)
    }

    override fun getItemCount(): Int {
        return listFallHistory.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(listFallHistory[position])
    }

    @SuppressLint("NotifyDataSetChanged")
    fun updateList(list: List<FallHistoryItem>) {
        listFallHistory.clear()
        listFallHistory.addAll(list)
        notifyDataSetChanged()
    }

    class ViewHolder(
        private val binding: DeviceItemBinding,
        private val reminderNameDevice: String) : RecyclerView.ViewHolder(binding.root) {
        @SuppressLint("SetTextI18n")
        fun bind(fallHistory: FallHistoryItem) {
            binding.textDangeroutsRemiderTime.text = "Thời gian: " + Utils.dateToTimeString(fallHistory.time)
//            binding.textDangeroutsRemiderAddress.text = fallHistory.address
        }
    }
}