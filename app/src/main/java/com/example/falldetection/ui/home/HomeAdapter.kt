package com.example.falldetection.ui.home

import android.annotation.SuppressLint
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.falldetection.R
import com.example.falldetection.data.model.UserDevice
import com.example.falldetection.databinding.UserItemBinding
import com.example.falldetection.utils.Utils
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference

class HomeAdapter(
    private val listDevices: ArrayList<UserDevice>,
    private val listener: OnClickListener
) : RecyclerView.Adapter<HomeAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = UserItemBinding
            .inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding, listener)
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

    @SuppressLint("NotifyDataSetChanged")
    fun sortList(isAscending: Boolean) {
        if (isAscending) {
            listDevices.sortBy { it.reminderName }
        } else {
            listDevices.sortByDescending { it.reminderName }
        }
        notifyDataSetChanged()
    }

    class ViewHolder(
        private val binding: UserItemBinding,
        private val listener: OnClickListener) : RecyclerView.ViewHolder(binding.root) {
        @SuppressLint("SetTextI18n")
        fun bind(device: UserDevice) {
            binding.textReminderName.text = device.reminderName
            binding.textAge.text = if (device.birthDate != null) {
                "Tuổi: " + Utils.getAge(device.birthDate!!)
            } else {
                ""
            }
//            Glide.with(binding.imgAvatar)
//                .load(device.avatarImg)
//                .placeholder(R.drawable.avatar_1)
//                .into(binding.imgAvatar)

            if (device.avatarImg != null) {
                val fileName = device.avatarImg as String
                val bucketUrl = "gs://falling-detection-3e200.appspot.com/avatars/"

                val storage: FirebaseStorage = FirebaseStorage.getInstance()
                val storageRef: StorageReference = storage.getReferenceFromUrl(bucketUrl)
                val imageRef: StorageReference = storageRef.child(fileName)
                imageRef.downloadUrl.addOnSuccessListener { uri ->
                    Glide.with(binding.imgAvatar).load(uri).error(R.drawable.avatar_1_raster)
                        .into(binding.imgAvatar)
                }.addOnFailureListener { exception ->
                    // Xử lý lỗi nếu có
                    Glide.with(binding.imgAvatar).load(R.drawable.avatar_1_raster)
                        .error(R.drawable.avatar_1_raster)
                        .into(binding.imgAvatar)
                    Log.e("FirebaseStorage", "Failed to get download URL", exception)
                }
            } else {
                Glide.with(binding.imgAvatar)
                    .load(R.drawable.avatar_1_raster)
                    .error(R.drawable.avatar_1_raster).into(binding.imgAvatar)
            }

            binding.btnWatchDetail.setOnClickListener {
                listener.onClick(device)
            }
        }
    }

    interface OnClickListener {
        fun onClick(device: UserDevice)
    }
}