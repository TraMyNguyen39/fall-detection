package com.example.falldetection.ui.profile

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import com.example.falldetection.databinding.FragmentProfileBinding

class ProfileFragment : Fragment() {
    private lateinit var binding: FragmentProfileBinding
//    private val viewModel: ProfileViewModel by activityViewModels {
//        val repository =
//            (requireActivity().application as MyApplication).repository
//        ProfileViewModelFactory(repository)
//    }
    private lateinit var navController: NavController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        navController = findNavController()
//        val currentBackStackEntry = navController.currentBackStackEntry!!
//        val savedStateHandler = currentBackStackEntry.savedStateHandle
//        savedStateHandler.getLiveData<Boolean>(LoginFragment.LOGIN_SUCCESSFUL)
//            .observe(currentBackStackEntry) { success ->
//                if (!success) {
////                    val action = HomeFragmentDirections.actionGlobalHomeFragment()
////                    navController.navigate(action)
//                }
//                // hủy:
//                savedStateHandler.remove<Boolean>(LoginFragment.LOGIN_SUCCESSFUL)
//            }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
//        viewModel.user.observe(viewLifecycleOwner) { user ->
//            if (user == null) {
////                val action = ProfileFragmentDirections
////                    .actionProfileFragmentToLoginFragment()
////                navController.navigate(action)
//            } else {
//                showProfile(user)
//            }
//        }
    }

    override fun onDestroy() {
        super.onDestroy()
//        navController.currentBackStackEntry!!
//            .savedStateHandle
//            .remove<Boolean>(LoginFragment.LOGIN_SUCCESSFUL)
    }

//    private fun showProfile(user: User?) {
//        user?.let {
//            binding.textProfileFullName.text =
//                getString(R.string.txt_profile_full_name, user.fullName)
//            binding.textProfileEmail.text =
//                getString(R.string.txt_profile_email, user.email)
//            binding.textProfileBirthDate.text =
//                getString(R.string.txt_profile_birth_date, dateToString(user.birthDate))
//            binding.textProfilePhoneNumber.text =
//                getString(R.string.txt_profile_phone, user.phoneNumber)
//            binding.textProfileOccupation.text =
//                getString(R.string.txt_profile_gender, user.gender)
//        }
//    }
}