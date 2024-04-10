package com.example.falldetection.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.appcompat.widget.SearchView
import androidx.core.content.ContextCompat
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import com.example.falldetection.R
import com.example.falldetection.databinding.FragmentHomeBinding

class HomeFragment : Fragment(), MenuProvider {
    private lateinit var binding: FragmentHomeBinding
//    private val viewModel: ProfileViewModel by activityViewModels {
//        val repository =
//            (requireActivity().application as MyApplication).repository
//        ProfileViewModelFactory(repository)
//    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentHomeBinding.inflate(inflater, container, false)
        val menuHost: MenuHost = requireActivity()
        menuHost.addMenuProvider(this, viewLifecycleOwner, Lifecycle.State.RESUMED)
        binding.recyclerUsers.adapter = HomeAdapter()
        return binding.root
    }

    override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
        menuInflater.inflate(R.menu.main_menu, menu)

        val searchView = menu.findItem(R.id.main_menu_item_search).actionView as SearchView
        val searchIcon = searchView.findViewById<ImageView>(androidx.appcompat.R.id.search_mag_icon)
        searchIcon.setColorFilter(ContextCompat.getColor(requireContext(), R.color.white));
//        searchIcon.setImageDrawable(requireContext().getDrawable(R.drawable.ic_search))

//        searchIcon.setColorFilter()
//        searchIcon.setColorFilter(
//            resources.getColor(R.color.white, null),
//            android.graphics.PorterDuff.Mode.SRC_IN);
    }

    override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
        return when (menuItem.itemId) {
            R.id.main_menu_item_profile -> {

                true
            }

            else -> false
        }
    }

}