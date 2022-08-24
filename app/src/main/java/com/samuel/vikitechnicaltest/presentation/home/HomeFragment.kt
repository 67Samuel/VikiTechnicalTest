package com.samuel.vikitechnicaltest.presentation.home

import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.WindowManager
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.samuel.vikitechnicaltest.databinding.FragmentHomeBinding
import com.samuel.vikitechnicaltest.presentation.MainViewModel
import com.samuel.vikitechnicaltest.presentation.selectcountry.SelectCountryEvents
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class HomeFragment : Fragment(com.samuel.vikitechnicaltest.R.layout.fragment_home){
    private val TAG: String = "MainFragmentDebug"
    
    private val viewModel: MainViewModel by activityViewModels()

//    private var fragmentHomeBinding: FragmentHomeBinding? = null
    private lateinit var binding: FragmentHomeBinding

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.d(TAG, "onViewCreated: called")
        binding = FragmentHomeBinding.bind(view)
//        fragmentHomeBinding = binding
        binding.fromCountry.root.setOnClickListener {
            Log.d(TAG, "onViewCreated: going to select from country")
            val bundle = bundleOf("direction" to SelectCountryEvents.CountryDirection.FROM.name)
            findNavController().navigate(com.samuel.vikitechnicaltest.R.id.action_mainFragment_to_selectCountryFragment, bundle)
        }
        binding.toCountry.root.setOnClickListener {
            Log.d(TAG, "onViewCreated: going to select to country")
            val bundle = bundleOf("direction" to SelectCountryEvents.CountryDirection.TO.name)
            findNavController().navigate(com.samuel.vikitechnicaltest.R.id.action_mainFragment_to_selectCountryFragment, bundle)
        }
        subscribeObservers()

        // bring up the soft keyboard on start
        binding.inputCard.editText.requestFocus()
        requireActivity().window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE)
    }

    private fun subscribeObservers() {
        viewModel.homeState.observe(viewLifecycleOwner) { state ->
            Log.d(TAG, "subscribeObservers: $state")

            state.fromCountry?.let {
                Log.d(TAG, "subscribeObservers: detected from country")
                binding.fromCountry.name.text = it.name
                binding.fromCountry.image.setImageResource(it.imageId)
            }

            state.toCountry?.let {
                Log.d(TAG, "subscribeObservers: detected to country")
                binding.toCountry.name.text = it.name
                binding.toCountry.image.setImageResource(it.imageId)
            }
        }
    }

    override fun onPause() {
        super.onPause()
        Log.d(TAG, "onPause: called")
    }

    override fun onStop() {
        super.onStop()
//        requireActivity().window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        Log.d(TAG, "onStop: called")
    }



    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        Log.d(TAG, "onSaveInstanceState: called")
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d(TAG, "onDestroy: called")
    }

    override fun onViewStateRestored(savedInstanceState: Bundle?) {
        super.onViewStateRestored(savedInstanceState)
        Log.d(TAG, "onViewStateRestored: called")
    }

    override fun onStart() {
        super.onStart()
        Log.d(TAG, "onStart: called")
    }

    override fun onResume() {
        super.onResume()
        Log.d(TAG, "onResume: called")
    }

    override fun onDestroyView() {
//        fragmentHomeBinding = null
        super.onDestroyView()
    }
}