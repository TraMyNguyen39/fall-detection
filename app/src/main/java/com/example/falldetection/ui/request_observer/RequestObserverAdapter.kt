package com.example.falldetection.ui.request_observer

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.falldetection.data.model.ObserverRequest
import com.example.falldetection.databinding.RequestObserverItemBinding
import com.example.falldetection.utils.Utils

class RequestObserverAdapter(
    private val listRequest: ArrayList<ObserverRequest>,
    private val listenerAccept: OnClickListener,
    private val listenerDeny: OnClickListener
) : RecyclerView.Adapter<RequestObserverAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = RequestObserverItemBinding
            .inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding, listenerAccept, listenerDeny)
    }

    override fun getItemCount(): Int {
        return listRequest.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(listRequest[position])
    }

    @SuppressLint("NotifyDataSetChanged")
    fun updateList(list: List<ObserverRequest>) {
        listRequest.clear()
        listRequest.addAll(list)
        notifyDataSetChanged()
    }

    class ViewHolder(
        private val binding: RequestObserverItemBinding,
        private val listenerAccept: OnClickListener,
        private val listenerDeny: OnClickListener
    ) : RecyclerView.ViewHolder(binding.root) {
        @SuppressLint("SetTextI18n")
        fun bind(request: ObserverRequest) {
            binding.textObserverEmail.text = request.supervisorEmail
            binding.textRequestTime.text = "Thời gian: " + Utils.dateToTimeString(request.time)
            binding.btnRequestAccept.setOnClickListener {
                listenerAccept.onClick(request)
            }

            binding.btnRequestDeny.setOnClickListener {
                listenerDeny.onClick(request)
            }
        }
    }

    interface OnClickListener {
        fun onClick(request: ObserverRequest)
    }
}