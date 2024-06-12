package com.example.falldetection.ui.request_observer

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.example.falldetection.MyApplication
import com.example.falldetection.R
import com.example.falldetection.data.model.ObserverRequest
import com.example.falldetection.databinding.FragmentRequestObserverBinding
import com.example.falldetection.utils.Utils
import com.google.android.material.snackbar.Snackbar

class RequestObserverFragment : Fragment(), SwipeRefreshLayout.OnRefreshListener {
    private lateinit var binding: FragmentRequestObserverBinding
    private lateinit var mAdapter: RequestObserverAdapter
    private lateinit var progressBar: ProgressBar
    private val viewModel: RequestObserverViewModel by activityViewModels {
        val repository =
            (requireActivity().application as MyApplication).observerRequestRepository
        RequestObserverViewModelFactory(repository)
    }
    private var userEmail: String? = null
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentRequestObserverBinding.inflate(inflater, container, false)
        progressBar = requireActivity().findViewById(R.id.progress_bar_refresh)

        userEmail = Utils.getCurrentEmail(requireContext())
        if (userEmail == null)
            requireActivity().onBackPressedDispatcher.onBackPressed()
        else {
            progressBar.visibility = View.VISIBLE
            viewModel.loadAllRequest(userEmail!!)
        }

        binding.swipeLayout.setOnRefreshListener(this)

        viewModel.loadAllRequest(userEmail!!)

        // set up adapter & observer
        setUpAdapter()
        setObserver()

        return binding.root
    }

    private fun setUpAdapter() {
        val acceptListener = object : RequestObserverAdapter.OnClickListener {
            override fun onClick(request: ObserverRequest) {
                viewModel.acceptRequest(request)
            }
        }

        val denyListener = object : RequestObserverAdapter.OnClickListener {
            override fun onClick(request: ObserverRequest) {
                viewModel.denyRequest(request)
            }
        }

        mAdapter = RequestObserverAdapter(arrayListOf(), acceptListener, denyListener)

        val layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        binding.recyclerRequest.adapter = mAdapter
        binding.recyclerRequest.layoutManager = layoutManager
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun setObserver() {
        viewModel.listRequest.observe(viewLifecycleOwner) {
            if (it.isEmpty()) {
                binding.emptyView.root.visibility = View.VISIBLE
                binding.recyclerRequest.visibility = View.GONE
            } else {
                binding.emptyView.root.visibility = View.GONE
                binding.recyclerRequest.visibility = View.VISIBLE
                mAdapter.updateList(it)
            }
            progressBar.visibility = View.GONE
        }

        viewModel.requestMessage.observe(viewLifecycleOwner) {
            Snackbar.make(requireView(), it, Snackbar.LENGTH_LONG).show()
            viewModel.loadAllRequest(userEmail!!)
        }
    }

    override fun onRefresh() {
        if (Utils.isOnline(requireContext())) {
            viewModel.loadAllRequest(userEmail!!)
        } else {
            Snackbar.make(binding.swipeLayout, R.string.txt_no_internet, Snackbar.LENGTH_LONG)
                .show()
        }
        binding.swipeLayout.isRefreshing = false
    }
}