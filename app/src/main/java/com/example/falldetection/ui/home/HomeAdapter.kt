package com.example.falldetection.ui.home

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.falldetection.databinding.UserItemBinding

class HomeAdapter : RecyclerView.Adapter<HomeAdapter.ViewHolder>() {

    class ViewHolder(private val binding : UserItemBinding) : RecyclerView.ViewHolder(binding.root) {

    }

    private lateinit var binding : UserItemBinding

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        binding = UserItemBinding.inflate(LayoutInflater.from(parent.context),
            parent,
            false)
        return ViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return 5
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

    }
}