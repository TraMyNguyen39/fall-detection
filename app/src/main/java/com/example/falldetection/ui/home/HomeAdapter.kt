package com.example.falldetection.ui.home

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.falldetection.R
import com.example.falldetection.data.model.UserDevice
import com.example.falldetection.databinding.UserItemBinding
import com.example.falldetection.getAge

class HomeAdapter(
    private val listDevices: ArrayList<UserDevice>
) : RecyclerView.Adapter<HomeAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = UserItemBinding
            .inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return listDevices.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(listDevices[position])
    }

    @SuppressLint("NotifyDataSetChanged")
    fun updateList(list: List<UserDevice>) {
        listDevices.clear()
        listDevices.addAll(list)
        notifyDataSetChanged()
    }

    class ViewHolder(private val binding: UserItemBinding) : RecyclerView.ViewHolder(binding.root) {
        @SuppressLint("SetTextI18n")
        fun bind(device: UserDevice) {
            binding.textRemiderName.text = device.reminderName
            binding.textAge.text = if (device.birthDate != null) {
                "Tuổi: " + getAge(device.birthDate)
            } else {
                "Tuổi: " + "(Không rõ)"
            }
            Glide.with(binding.imgAvatar)
                .load(device.avatarImg)
                .placeholder(R.drawable.avatar_1)
                .into(binding.imgAvatar)
        }
    }
}